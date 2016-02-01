/*-
 * Copyright (c) 2015-2016 Red Hat, Inc.
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
package org.fedoraproject.insim.data;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.fedoraproject.insim.model.DependencyGraph;

/**
 * @author Mikolaj Izdebski
 */
@ApplicationScoped
public class DependencyGraphDAO {

    @Inject
    private EntityManager em;

    @Transactional
    public DependencyGraph getById(Integer id) {
        return em.find(DependencyGraph.class, id);
    }

    @Transactional
    public void persist(DependencyGraph graph) {
        em.persist(graph);
    }

}
