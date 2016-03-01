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
package org.fedoraproject.insim.frontend.view;

import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.fedoraproject.insim.data.InstallationDAO;
import org.fedoraproject.insim.data.PackageDAO;
import org.fedoraproject.insim.model.Collection;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Package;

/**
 * @author Mikolaj Izdebski
 */
@ManagedBean
@ViewScoped
public class PackageView {

    @Inject
    private PackageDAO dao;
    @Inject
    private InstallationDAO instDAO;

    private Package pkg;

    private String name;

    private final List<CollectionInfo> collectionInfos = new ArrayList<>();

    public static class CollectionInfo {
        private final Collection collection;
        private final Installation latestInstallation;

        public CollectionInfo(Collection collection, Installation latestInstallation) {
            this.collection = collection;
            this.latestInstallation = latestInstallation;
        }

        public Collection getCollection() {
            return collection;
        }

        public Installation getLatestInstallation() {
            return latestInstallation;
        }

    }

    public Package getPackage() {
        return pkg;
    }

    public void load() {
        pkg = dao.getByName(name);
        if (pkg == null)
            return;

        for (Collection coll : pkg.getCollections()) {
            Installation latestInstallation = instDAO.getLatestByPackageCollection(pkg, coll);
            if (latestInstallation != null) {
                collectionInfos.add(new CollectionInfo(coll, latestInstallation));
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CollectionInfo> getCollectionInfos() {
        return collectionInfos;
    }
}
