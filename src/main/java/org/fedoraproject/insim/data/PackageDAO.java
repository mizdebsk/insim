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
package org.fedoraproject.insim.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.fedoraproject.insim.model.Package;
import org.fedoraproject.insim.model.Package_;

/**
 * @author Mikolaj Izdebski
 */
@ApplicationScoped
public class PackageDAO {

    @Inject
    private EntityManager em;

    public Package getByName(String name) {
        return em.find(Package.class, name);
    }

    public List<Package> getAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Package> criteria = cb.createQuery(Package.class);
        Root<Package> pkg = criteria.from(Package.class);
        criteria.select(pkg).orderBy(cb.asc(pkg.get(Package_.name)));
        return em.createQuery(criteria).getResultList();
    }

    @Transactional
    public void persist(Package pkg) {
        em.persist(pkg);
    }

}
