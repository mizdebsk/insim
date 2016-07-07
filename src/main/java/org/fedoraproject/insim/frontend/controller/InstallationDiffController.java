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
package org.fedoraproject.insim.frontend.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fedoraproject.insim.data.InstallationDAO;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Rpm;
import org.fedoraproject.insim.util.Hrn;

/**
 * @author Mikolaj Izdebski
 */
@WebServlet("/installation/diff/*")
public class InstallationDiffController extends HttpServlet {

    private static final long serialVersionUID = 1;

    private static final String JSP = "/installation-diff.jsp";

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] parts = request.getPathInfo().split("/+");
        if (parts.length != 3) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        int oldId, newId;
        try {
            oldId = Integer.parseInt(parts[1]);
            newId = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Installation oldInstallation = dao.getById(oldId);
        Installation newInstallation = dao.getById(newId);
        if (oldInstallation == null || newInstallation == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        List<Rpm> oldRpms = oldInstallation.getRpms();
        List<Rpm> newRpms = newInstallation.getRpms();
        DiffPiece added = new DiffPiece("Added packages");
        DiffPiece removed = new DiffPiece("Removed packages");
        DiffPiece updated = new DiffPiece("Updated packages");
        List<DiffPiece> pieces = Arrays.asList(added, removed, updated);

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

        request.setAttribute("hrn", new Hrn());
        request.setAttribute("oldInstallation", oldInstallation);
        request.setAttribute("newInstallation", newInstallation);
        request.setAttribute("diffPieces", pieces);
        RequestDispatcher view = request.getRequestDispatcher(JSP);
        view.forward(request, response);
    }

}
