/*-
 * Copyright (c) 2015-2016 Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fedoraproject.insim.backend;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fedoraproject.insim.data.CollectionDAO;
import org.fedoraproject.insim.data.DependencyGraphDAO;
import org.fedoraproject.insim.data.InstallationDAO;
import org.fedoraproject.insim.data.PackageDAO;
import org.fedoraproject.insim.data.RepositoryDAO;
import org.fedoraproject.insim.model.Collection;
import org.fedoraproject.insim.model.DependencyGraph;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Package;
import org.fedoraproject.insim.model.Repository;
import org.fedoraproject.insim.model.Rpm;
import org.fedoraproject.javadeptools.hawkey.HawkeyException;
import org.fedoraproject.javadeptools.hawkey.PackageInfo;
import org.fedoraproject.javadeptools.hawkey.Sack;

/**
 * @author Mikolaj Izdebski
 */
@ApplicationScoped
public class Indexer {

    private static final List<String> ARCHES = Arrays.asList("x86_64");

    @Inject
    private CollectionDAO collectionDao;

    @Inject
    private RepositoryDAO repoDao;

    @Inject
    private PackageDAO pkgDao;

    @Inject
    private InstallationDAO instDao;

    @Inject
    private DependencyGraphDAO graphDao;

    private static String jsonify(Map<PackageInfo, Set<PackageInfo>> map) {
        return "{"
                + map.entrySet().stream()
                        .map(e -> "\"" + e.getKey().getName() + "\":[" + e.getValue().stream()
                                .map(dep -> "\"" + dep.getName() + "\"").collect(Collectors.joining(",")) + "]")
                        .collect(Collectors.joining(","))
                + "}";
    }

    private void newInstallation(Sack sack, Repository repo, Package pkg) throws HawkeyException {
        Installation inst = new Installation();
        inst.setRepository(repo);
        inst.setPackage(pkg);

        Simulation sim = new Simulation(sack);
        sim.addBaseDeps(pkg.getBaseline().getAllPackages());
        sim.addInstDeps(pkg.getInstallRpms());
        if (!sim.run()) {
            inst.setComplete(false);
            instDao.persist(inst);
            return;
        }

        int fileCount = 0;
        long downloadSize = 0;
        long installSize = 0;
        for (PackageInfo dep : sim.getInstPkgs()) {
            fileCount += dep.getFileCount();
            downloadSize += dep.getDownloadSize();
            installSize += dep.getInstallSize();
            if (dep.getName().equals(pkg.getName())) {
                inst.setVersion(dep.getVersion());
                inst.setRelease(dep.getRelease());
            }
            Rpm rpm = new Rpm();
            rpm.setInstallation(inst);
            rpm.setName(dep.getName());
            rpm.setEpoch(dep.getEpoch());
            rpm.setVersion(dep.getVersion());
            rpm.setRelease(dep.getRelease());
            rpm.setArch(dep.getArch());
            rpm.setInstallSize(dep.getInstallSize());
            rpm.setDownloadSize(dep.getDownloadSize());
            rpm.setFileCount(dep.getFileCount());
            inst.addRpm(rpm);
        }

        inst.setComplete(true);
        inst.setDependencyCount(sim.getInstPkgs().size() - 1);
        inst.setFileCount(fileCount);
        inst.setDownloadSize(downloadSize);
        inst.setInstallSize(installSize);
        instDao.persist(inst);

        DependencyGraph graph = new DependencyGraph();
        graph.setInstallation(inst);
        graph.setJson(jsonify(sim.getDependencyTree()));
        graphDao.persist(graph);
    }

    public Response index(String collectionName, String url, Long timestamp) {

        Collection collection = collectionDao.getByName(collectionName);
        if (collection == null) {
            return Response.status(Status.NOT_FOUND).build();
        }

        if (url == null) {
            url = collection.getLocation();
        }
        if (timestamp == null) {
            timestamp = new Date().getTime();
        } else {
            timestamp *= 1000;
        }

        try (Sack sack = new Sack("x86_64")) {

            for (String arch : ARCHES) {
                try (RepoCache repoCache = new RepoCache(url, arch)) {
                    repoCache.loadIntoSack(sack);
                }
            }

            Repository repo = new Repository();
            repo.setCollection(collection);
            repo.setActive(true);
            repo.setCreationTime(new Timestamp(timestamp));
            repoDao.persist(repo);

            for (Package pkg : pkgDao.getAll()) {
                newInstallation(sack, repo, pkg);
            }
        } catch (HawkeyException | IOException e) {
            throw new RuntimeException(e);
        }

        return Response.noContent().build();
    }

}
