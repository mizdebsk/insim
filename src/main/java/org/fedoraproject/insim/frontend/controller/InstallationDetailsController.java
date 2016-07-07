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

import javax.inject.Inject;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.fedoraproject.insim.data.InstallationDAO;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.util.Hrn;

/**
 * @author Mikolaj Izdebski
 */
@WebServlet("/installation/*")
public class InstallationDetailsController extends HttpServlet {

    private static final long serialVersionUID = 1;

    private static final String JSP = "/installation-details.jsp";

    @Inject
    private InstallationDAO dao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id;
        try {
            id = Integer.parseInt(request.getPathInfo().substring(1));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        Installation installation = dao.getById(id);
        if (installation == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("hrn", new Hrn());
        request.setAttribute("installation", installation);
        RequestDispatcher view = request.getRequestDispatcher(JSP);
        view.forward(request, response);
    }
}
