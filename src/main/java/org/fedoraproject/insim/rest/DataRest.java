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
package org.fedoraproject.insim.rest;

import java.util.Arrays;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.fedoraproject.insim.data.DependencyGraphDAO;
import org.fedoraproject.insim.data.InstallationDAO;
import org.fedoraproject.insim.data.ModuleDAO;
import org.fedoraproject.insim.model.Collection;
import org.fedoraproject.insim.model.DependencyGraph;
import org.fedoraproject.insim.model.Module;

/**
 * @author Mikolaj Izdebski
 */
@Path("/data")
@RequestScoped
public class DataRest {

    @Inject
    private ModuleDAO moduleDao;

    @Inject
    private InstallationDAO instDao;

    @Inject
    private DependencyGraphDAO graphDao;

    @GET
    @Path("installations/{module}")
    @Produces(MediaType.APPLICATION_JSON)
    public Object getModuleInstallations( //
            @PathParam("module") String moduleName //
    ) {
        Module module = moduleDao.getByName(moduleName);
        if (module == null)
            return Response.status(Status.NOT_FOUND).build();

        return module.getCollections().stream().collect(Collectors.toMap(Collection::getName, coll -> instDao
                .getByModuleCollection(module, coll).stream()
                .map(inst -> Arrays.asList(inst.getId(), inst.getRepository().getCreationTime(), inst.getComplete(),
                        inst.getInstallSize(), inst.getDownloadSize(), inst.getDependencyCount(), inst.getFileCount()))
                .collect(Collectors.toList())));
    }

    @GET
    @Path("graph/{installation}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDependencyGraph( //
            @PathParam("installation") Integer instId //
    ) {
        DependencyGraph graph = graphDao.getById(instId);
        if (graph == null)
            return Response.status(Status.NOT_FOUND).build();

        return Response.ok(graph.getJson()).build();
    }

}
