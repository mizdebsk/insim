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

import org.fedoraproject.insim.data.CollectionDAO;
import org.fedoraproject.insim.model.Collection;

/**
 * @author Mikolaj Izdebski
 */
@WebServlet("/collection/*")
public class CollectionDetailsController extends HttpServlet {

    private static final long serialVersionUID = 1;

    private static final String JSP = "/collection-details.jsp";

    @Inject
    private CollectionDAO dao;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getPathInfo().substring(1);
        Collection collection = dao.getByName(name);
        if (collection == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        RequestDispatcher view = request.getRequestDispatcher(JSP);
        request.setAttribute("collection", collection);
        view.forward(request, response);
    }
}
