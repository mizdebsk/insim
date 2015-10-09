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

import org.fedoraproject.insim.model.Collection;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Package;
import org.primefaces.model.chart.AxisType;
import org.primefaces.model.chart.ChartSeries;
import org.primefaces.model.chart.DateAxis;
import org.primefaces.model.chart.LineChartModel;

/**
 * @author Mikolaj Izdebski
 */
public class PackageCollectionView {
    private final Package pkg;
    private final Collection collection;
    private final List<Installation> installations;
    private final List<Chart> charts;

    public class Chart {
        private final String id;
        private final LineChartModel model;

        public Chart(String id, String title, Function<Installation, Number> f) {
            this.id = id;

            model = new LineChartModel();
            model.setTitle(title);

            DateAxis x = new DateAxis();
            x.setTickAngle(-50);
            x.setTickFormat("%#d %b %Y, %H:%M");
            model.getAxes().put(AxisType.X, x);

            ChartSeries installSize = new ChartSeries();
            for (Installation i : getInstallations()) {
                long time = i.getRepository().getCreationTime().getTime();
                installSize.set(time, f.apply(i));
            }

            model.addSeries(installSize);
        }

        public String getId() {
            return id;
        }

        public LineChartModel getModel() {
            return model;
        }

        public int getWidth() {
            return 200 + 30 * installations.size();
        }
    }

    public PackageCollectionView(Package pkg, Collection collection, List<Installation> installations) {
        this.pkg = pkg;
        this.collection = collection;
        this.installations = installations;

        this.charts = new ArrayList<>();
        charts.add(new Chart("isz", "Install size", Installation::getInstallSize));
        charts.add(new Chart("dsz", "Download size", Installation::getDownloadSize));
        charts.add(new Chart("dcn", "Dependency count", Installation::getDependencyCount));
        charts.add(new Chart("fcn", "File count", Installation::getFileCount));
    }

    public Package getPackage() {
        return pkg;
    }

    public Collection getCollection() {
        return collection;
    }

    public List<Installation> getInstallations() {
        return installations;
    }

    public List<Chart> getCharts() {
        return charts;
    }

    public List<LineChartModel> getChartModels() {
        List<LineChartModel> chartModels = new ArrayList<>(4);
        return chartModels;
    }

    public Installation getLatestInstallation() {
        return installations.get(installations.size() - 1);
    }

}
