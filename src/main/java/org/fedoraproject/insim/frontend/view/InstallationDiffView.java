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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.fedoraproject.insim.data.InstallationDAO;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Rpm;

/**
 * @author Mikolaj Izdebski
 */
@ManagedBean
@ViewScoped
public class InstallationDiffView {

    public static class FieldDiff {
        private final Object oldValue;
        private final Object newValue;

        public FieldDiff(Rpm oldRpm, Rpm newRpm, Function<Rpm, Object> getter) {
            this.oldValue = oldRpm != null ? getter.apply(oldRpm) : null;
            this.newValue = newRpm != null ? getter.apply(newRpm) : null;
        }

        public boolean hasOld() {
            return oldValue != null;
        }

        public Object getOld() {
            return oldValue;
        }

        public boolean hasNew() {
            return newValue != null;
        }

        public Object getNew() {
            return newValue;
        }

        public boolean differs() {
            return oldValue == null || newValue == null || !oldValue.equals(newValue);
        }
    }

    public static class DiffPiece {
        private final String title;
        private final List<List<FieldDiff>> elements = new ArrayList<>();

        public DiffPiece(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }

        public void addDiff(Rpm oldRpm, Rpm newRpm) {
            List<FieldDiff> fields = Rpm.FIELD_GETTERS.stream().map(getter -> new FieldDiff(oldRpm, newRpm, getter))
                    .collect(Collectors.toList());
            if (fields.stream().anyMatch(f -> f.differs()))
                elements.add(fields);
        }

        public List<List<FieldDiff>> getElements() {
            return elements;
        }

    }

    @Inject
    private InstallationDAO dao;

    private Installation oldInstallation;
    private Installation newInstallation;

    private Integer oldId;
    private Integer newId;

    private List<DiffPiece> pieces = new ArrayList<>();

    public void load() {
        oldInstallation = dao.getById(oldId);
        newInstallation = dao.getById(newId);
        computeDiff();
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

    private void computeDiff() {
        List<Rpm> oldRpms = oldInstallation.getRpms();
        List<Rpm> newRpms = newInstallation.getRpms();
        DiffPiece added = new DiffPiece("Added packages");
        DiffPiece removed = new DiffPiece("Removed packages");
        DiffPiece updated = new DiffPiece("Updated packages");
        pieces.add(added);
        pieces.add(removed);
        pieces.add(updated);

        int i = 0;
        int j = 0;
        while (i < oldRpms.size() && j < newRpms.size()) {
            Rpm a = i < oldRpms.size() ? oldRpms.get(i) : null;
            Rpm b = j < newRpms.size() ? newRpms.get(j) : null;
            if (a == null) {
                added.addDiff(null, b);
                j++;
            } else if (b == null) {
                removed.addDiff(a, null);
                i++;
            } else if (a.getName().compareTo(b.getName()) < 0) {
                removed.addDiff(a, null);
                i++;
            } else if (a.getName().compareTo(b.getName()) > 0) {
                added.addDiff(null, b);
                j++;
            } else {
                updated.addDiff(a, b);
                i++;
                j++;
            }
        }
    }

    public List<DiffPiece> getDiffPieces() {
        return pieces;
    }

}
