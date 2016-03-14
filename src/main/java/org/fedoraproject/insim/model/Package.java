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
import java.util.Collections;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

/**
 * @author Mikolaj Izdebski
 */
@Entity
public class Package implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    private String name;
    @ManyToOne
    private Baseline baseline;
    private String upstream;
    private String upstreamVersion;
    private Long upstreamInstallSize;
    private Long upstreamDownloadSize;
    @ManyToMany(mappedBy = "packages")
    private List<Collection> collections;
    @ElementCollection
    private List<String> rpms;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Baseline getBaseline() {
        return this.baseline;
    }

    public void setBaseline(Baseline baseline) {
        this.baseline = baseline;
    }

    public String getUpstream() {
        return this.upstream;
    }

    public void setUpstream(String upstream) {
        this.upstream = upstream;
    }

    public String getUpstreamVersion() {
        return upstreamVersion;
    }

    public void setUpstreamVersion(String upstreamVersion) {
        this.upstreamVersion = upstreamVersion;
    }

    public Long getUpstreamInstallSize() {
        return this.upstreamInstallSize;
    }

    public void setUpstreamInstallSize(Long upstreamInstallSize) {
        this.upstreamInstallSize = upstreamInstallSize;
    }

    public Long getUpstreamDownloadSize() {
        return this.upstreamDownloadSize;
    }

    public void setUpstreamDownloadSize(Long upstreamDownloadSize) {
        this.upstreamDownloadSize = upstreamDownloadSize;
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

    public boolean isMetapackage() {
        return !getRpms().isEmpty();
    }

    public List<String> getInstallRpms() {
        return isMetapackage() ? getRpms() : Collections.singletonList(getName());
    }

}
