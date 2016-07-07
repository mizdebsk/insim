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
import java.util.List;

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fedoraproject.insim.data.InstallationDAO;
import org.fedoraproject.insim.data.ModuleDAO;
import org.fedoraproject.insim.model.Collection;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Module;
import org.fedoraproject.insim.util.Hrn;

/**
 * @author Mikolaj Izdebski
 */
@WebServlet("/module/*")
public class ModuleDetailsController extends HttpServlet {

    private static final long serialVersionUID = 1;

    private static final String JSP = "/module-details.jsp";

    public static class CollectionInfo {
        private final Collection collection;
        private final Installation latestInstallation;

        public CollectionInfo(Collection collection, Installation latestInstallation) {
            this.collection = collection;
            this.latestInstallation = latestInstallation;
        }

        public Collection getCollection() {
            return collection;
        }

        public Installation getLatestInstallation() {
            return latestInstallation;
        }

    }

    @Inject
    private ModuleDAO dao;

    @Inject
    private InstallationDAO instDAO;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getPathInfo().substring(1);
        Module module = dao.getByName(name);
        if (module == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("module", module);
        request.setAttribute("hrn", new Hrn());
        List<CollectionInfo> collectionInfos = new ArrayList<>();
        for (Collection coll : module.getCollections()) {
            Installation latestInstallation = instDAO.getLatestByModuleCollection(module, coll);
            if (latestInstallation != null) {
                collectionInfos.add(new CollectionInfo(coll, latestInstallation));
            }
        }
        request.setAttribute("collectionInfos", collectionInfos);
        RequestDispatcher view = request.getRequestDispatcher(JSP);
        view.forward(request, response);
    }
}
