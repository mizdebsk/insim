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
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;

/**
 * @author Mikolaj Izdebski
 */
@Entity
public class Module implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    private String name;

    @ManyToMany(mappedBy = "modules")
    @OrderBy("name ASC")
    private List<Collection> collections;

    @ElementCollection
    private List<String> rpms;

    @ManyToMany
    private List<Module> parents;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Collection> getCollections() {
        return collections;
    }

    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    public List<String> getRpms() {
        return rpms;
    }

    public void setRpms(List<String> rpms) {
        this.rpms = rpms;
    }

    public boolean isMetamodule() {
        return !getRpms().isEmpty();
    }

    public List<Module> getParents() {
        return parents;
    }

    public void setParents(List<Module> parents) {
        this.parents = parents;
    }

    public List<String> getInstallRpms() {
        return isMetamodule() ? getRpms() : Collections.singletonList(getName());
    }

    public List<String> getBaselineRpms() {
        return getParentRpms(new TreeSet<>(), new LinkedHashSet<>());
    }

    private List<String> getParentRpms(Set<String> rpmSet, Set<Module> visited) {
        if (visited.add(this)) {
            for (Module mod : parents) {
                rpmSet.addAll(mod.getInstallRpms());
                mod.getParentRpms(rpmSet, visited);
            }
        }
        return new ArrayList<>(rpmSet);
    }

}
