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
import java.util.function.Function;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.fedoraproject.insim.data.InstallationDAO;
import org.fedoraproject.insim.data.PackageDAO;
import org.fedoraproject.insim.model.Collection;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Package;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.BarChartModel;
import org.primefaces.model.chart.ChartSeries;

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

    private final List<PackageCollectionView> collectionViews = new ArrayList<>();

    private final List<Chart> charts = new ArrayList<>();

    public class Chart {
        private final String id;
        private final BarChartModel model;

        public Chart(String id, String title, Function<Installation, Number> f, Number upstreamValue) {
            this.id = id;

            model = new BarChartModel();
            model.setTitle(title);
            ChartSeries values = new ChartSeries();
            for (PackageCollectionView pcv : collectionViews) {
                values.set(pcv.getCollection().getName(), f.apply(pcv.getLatestInstallation()));
            }
            if (upstreamValue != null) {
                values.set("Upsteam", upstreamValue);
            }
            model.addSeries(values);

            model.getAxis(AxisType.X).setLabel("Collection");
            model.getAxis(AxisType.Y).setLabel(title);
        }

        public String getId() {
            return id;
        }

        public BarChartModel getModel() {
            return model;
        }

        public int getWidth() {
            return 100 + 50 * model.getSeries().get(0).getData().size();
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
            List<Installation> installations = instDAO.getByPackageCollection(pkg, coll);
            if (!installations.isEmpty()) {
                collectionViews.add(new PackageCollectionView(pkg, coll, installations));
            }
        }

        charts.add(new Chart("isz", "Install size", Installation::getInstallSize, pkg.getUpstreamInstallSize()));
        charts.add(new Chart("dsz", "Download size", Installation::getDownloadSize, pkg.getUpstreamDownloadSize()));
        charts.add(new Chart("dcn", "Dependency count", Installation::getDependencyCount, null));
        charts.add(new Chart("fcn", "File count", Installation::getFileCount, null));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PackageCollectionView> getCollectionViews() {
        return collectionViews;
    }

    public List<Chart> getCharts() {
        return charts;
    }
}
