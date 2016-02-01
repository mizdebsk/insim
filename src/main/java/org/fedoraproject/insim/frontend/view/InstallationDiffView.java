/*-
 * Copyright (c) 2016 Red Hat, Inc.
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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.fedoraproject.insim.data.InstallationDAO;
import org.fedoraproject.insim.model.Installation;

/**
 * @author Mikolaj Izdebski
 */
@ManagedBean
@ViewScoped
public class InstallationDiffView {

    @Inject
    private InstallationDAO dao;

    private Installation oldInstallation;
    private Installation newInstallation;

    private Integer oldId;
    private Integer newId;

    public void load() {
        oldInstallation = dao.getById(oldId);
        newInstallation = dao.getById(newId);
    }

    public Integer getOldId() {
        return oldId;
    }

    public void setOldId(Integer oldId) {
        this.oldId = oldId;
    }

    public Integer getNewId() {
        return newId;
    }

    public void setNewId(Integer newId) {
        this.newId = newId;
    }

    public Installation getOldInstallation() {
        return oldInstallation;
    }

    public void setOldInstallation(Installation oldInstallation) {
        this.oldInstallation = oldInstallation;
    }

    public Installation getNewInstallation() {
        return newInstallation;
    }

    public void setNewInstallation(Installation newInstallation) {
        this.newInstallation = newInstallation;
    }
}
