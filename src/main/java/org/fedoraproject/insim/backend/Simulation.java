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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.fedoraproject.javadeptools.hawkey.Goal;
import org.fedoraproject.javadeptools.hawkey.HawkeyException;
import org.fedoraproject.javadeptools.hawkey.PackageInfo;
import org.fedoraproject.javadeptools.hawkey.Sack;

/**
 * @author Mikolaj Izdebski
 */
class Simulation {
    private final Sack sack;
    private final String pkg;
    private final Set<String> baseDeps = new LinkedHashSet<>();
    private final Set<PackageInfo> basePkgs = new LinkedHashSet<>();
    private final Set<PackageInfo> instPkgs = new LinkedHashSet<>();

    public Simulation(Sack sack, String pkg) {
        this.sack = sack;
        this.pkg = pkg;
    }

    public void setBaseDeps(Collection<String> deps) {
        baseDeps.clear();
        baseDeps.addAll(deps);
    }

    public Set<PackageInfo> getBasePkgs() {
        return Collections.unmodifiableSet(basePkgs);
    }

    public Set<PackageInfo> getInstPkgs() {
        return Collections.unmodifiableSet(instPkgs);
    }

    public boolean run() throws HawkeyException {
        basePkgs.clear();
        instPkgs.clear();

        try (Goal goal = new Goal(sack)) {
            for (String dep : baseDeps) {
                goal.install(dep);
            }
            boolean ok = goal.run();
            if (!ok) {
                return false;
            }
            basePkgs.addAll(goal.listInstalls());

            goal.install(pkg);
            ok = goal.run();
            if (!ok) {
                return false;
            }

            instPkgs.addAll(goal.listInstalls());
            instPkgs.removeAll(basePkgs);

            return true;
        }
    }

    public Map<PackageInfo, Set<PackageInfo>> getDependencyTree() throws HawkeyException {
        Map<PackageInfo, Set<PackageInfo>> map = new LinkedHashMap<>();
        for (PackageInfo pkg : instPkgs) {
            Set<PackageInfo> deps = new LinkedHashSet<>(sack.resolveRequires(pkg.getName()));
            deps.retainAll(instPkgs);
            map.put(pkg, deps);
        }
        return map;
    }

}
