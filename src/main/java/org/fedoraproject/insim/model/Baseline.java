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
package org.fedoraproject.insim.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

/**
 * @author Mikolaj Izdebski
 */
@Entity
public class Baseline implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    private String name;

    @ElementCollection
    private List<String> pkgs;

    @ManyToMany
    private List<Baseline> parents;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPackages() {
        return this.pkgs;
    }

    public void setPackages(List<String> pkgs) {
        this.pkgs = pkgs;
    }

    public List<Baseline> getParents() {
        return parents;
    }

    public void setParents(List<Baseline> parents) {
        this.parents = parents;
    }

    public List<String> getAllPackages() {
        return getAllPackages(new TreeSet<>(), new LinkedHashSet<>());
    }

    private List<String> getAllPackages(Set<String> pset, Set<Baseline> visited) {
        if (!visited.contains(this)) {
            pset.addAll(getPackages());
            visited.add(this);
            for (Baseline b : parents) {
                b.getAllPackages(pset, visited);
            }
            visited.remove(this);
        }
        return new ArrayList<>(pset);
    }

}
