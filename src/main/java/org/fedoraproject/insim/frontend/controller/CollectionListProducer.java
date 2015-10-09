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

import org.fedoraproject.insim.data.CollectionDAO;
import org.fedoraproject.insim.model.Collection;

/**
 * @author Mikolaj Izdebski
 */
@RequestScoped
public class CollectionListProducer {

    @Inject
    private CollectionDAO dao;

    private List<Collection> collections;

    @Produces
    @Named
    public List<Collection> getCollections() {
        return collections;
    }

    @PostConstruct
    public void initialize() {
        collections = dao.getAll();
    }

}
