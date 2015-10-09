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
package org.fedoraproject.insim.frontend.view;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import org.fedoraproject.insim.data.CollectionDAO;
import org.fedoraproject.insim.model.Collection;

/**
 * @author Mikolaj Izdebski
 */
@ManagedBean
@ViewScoped
public class CollectionView {

    @Inject
    private CollectionDAO dao;

    private Collection pkg;

    private String name;

    public Collection getCollection() {
        return pkg;
    }

    public void load() {
        pkg = dao.getByName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
