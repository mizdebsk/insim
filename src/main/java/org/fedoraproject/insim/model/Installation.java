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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Mikolaj Izdebski
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "repository_id", "module_name" }) })
public class Installation implements Serializable {

    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne(optional = false)
    private Module module;

    @ManyToOne(optional = false)
    private Repository repository;

    @Column(nullable = true)
    private String version;

    @Column(nullable = true)
    private String release;

    @Column(nullable = false)
    private Boolean complete;

    @Column(nullable = false)
    private Long installSize;

    @Column(nullable = false)
    private Long downloadSize;

    @Column(nullable = false)
    private Integer dependencyCount;

    @Column(nullable = false)
    private Integer fileCount;

    @OneToMany(mappedBy = "installation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("name ASC")
    private List<Rpm> rpms;

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Module getModule() {
        return this.module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Repository getRepository() {
        return this.repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRelease() {
        return this.release;
    }

    public void setRelease(String release) {
        this.release = release;
    }

    public Boolean getComplete() {
        return this.complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    public Long getInstallSize() {
        return this.installSize;
    }

    public void setInstallSize(Long installSize) {
        this.installSize = installSize;
    }

    public Long getDownloadSize() {
        return this.downloadSize;
    }

    public void setDownloadSize(Long downloadSize) {
        this.downloadSize = downloadSize;
    }

    public Integer getDependencyCount() {
        return this.dependencyCount;
    }

    public void setDependencyCount(Integer dependencyCount) {
        this.dependencyCount = dependencyCount;
    }

    public Integer getFileCount() {
        return this.fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public List<Rpm> getRpms() {
        return rpms;
    }

    public void setRpms(List<Rpm> rpms) {
        this.rpms = rpms;
    }

    public void addRpm(Rpm rpm) {
        if (rpms == null)
            rpms = new ArrayList<>();
        rpms.add(rpm);
    }

}
