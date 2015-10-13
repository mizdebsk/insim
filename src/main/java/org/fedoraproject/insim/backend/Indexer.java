/*-
 * Copyright (c) 2015 Red Hat, Inc.
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.fedoraproject.insim.data.CollectionDAO;
import org.fedoraproject.insim.data.InstallationDAO;
import org.fedoraproject.insim.data.PackageDAO;
import org.fedoraproject.insim.data.RepositoryDAO;
import org.fedoraproject.insim.model.Collection;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Package;
import org.fedoraproject.insim.model.Repository;
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

    private Installation newInstallation(Sack sack, Package pkg) throws HawkeyException {
        Installation inst = new Installation();
        inst.setPackage(pkg);

        Simulation sim = new Simulation(sack, pkg.getName());
        sim.setBaseDeps(pkg.getBaseline().getPackages());
        if (!sim.run()) {
            inst.setComplete(false);
            return inst;
        }

        int fileCount = 0;
        long downloadSize = 0;
        long installSize = 0;
        Set<String> deps = new LinkedHashSet<>();
        for (PackageInfo dep : sim.getInstPkgs()) {
            fileCount += dep.getFileCount();
            downloadSize += dep.getDownloadSize();
            installSize += dep.getInstallSize();
            if (dep.getName().equals(pkg.getName())) {
                inst.setVersion(dep.getVersion());
                inst.setRelease(dep.getRelease());
            } else {
                deps.add(dep.getName());
            }
        }

        inst.setComplete(true);
        inst.setDependencyCount(deps.size());
        inst.setFileCount(fileCount);
        inst.setDownloadSize(downloadSize);
        inst.setInstallSize(installSize);
        inst.setDependencies(deps);
        return inst;
    }

    public Installation index(String collectionName, String url, Long timestamp) {

        Collection collection = collectionDao.getByName(collectionName);
        if (collection == null) {
            return null;
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
                Installation inst = newInstallation(sack, pkg);
                inst.setRepository(repo);
                instDao.persist(inst);
            }
        } catch (HawkeyException | IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
