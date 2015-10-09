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
package org.fedoraproject.insim.frontend.controller;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.fedoraproject.insim.data.PackageDAO;
import org.fedoraproject.insim.model.Package;

/**
 * @author Mikolaj Izdebski
 */
@RequestScoped
public class PackageListProducer {

    @Inject
    private PackageDAO dao;

    private List<Package> packages;

    @Produces
    @Named
    public List<Package> getPackages() {
        return packages;
    }

    @PostConstruct
    public void initialize() {
        packages = dao.getAll();
    }

}
