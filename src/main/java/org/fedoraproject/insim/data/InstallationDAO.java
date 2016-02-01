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

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.fedoraproject.insim.model.Collection;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Installation_;
import org.fedoraproject.insim.model.Package;
import org.fedoraproject.insim.model.Repository_;

/**
 * @author Mikolaj Izdebski
 */
@ApplicationScoped
public class InstallationDAO {

    @Inject
    private EntityManager em;

    public Installation getById(Integer id) {
        return em.find(Installation.class, id);
    }

    public List<Installation> getByPackageCollection(Package pkg, Collection col) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Installation> criteria = cb.createQuery(Installation.class);
        Root<Installation> inst = criteria.from(Installation.class);
        Predicate pkgMatch = cb.equal(inst.get(Installation_.pkg), pkg);
        Predicate colMatch = cb.equal(inst.join(Installation_.repository).get(Repository_.collection), col);
        Order order = cb.asc(inst.join(Installation_.repository).get(Repository_.creationTime));
        criteria.select(inst).where(cb.and(pkgMatch, colMatch)).orderBy(order);
        return em.createQuery(criteria).getResultList();
    }

    public Installation getLatestByPackageCollection(Package pkg, Collection col) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Installation> criteria = cb.createQuery(Installation.class);
        Root<Installation> inst = criteria.from(Installation.class);
        Predicate pkgMatch = cb.equal(inst.get(Installation_.pkg), pkg);
        Predicate colMatch = cb.equal(inst.join(Installation_.repository).get(Repository_.collection), col);
        Order order = cb.desc(inst.join(Installation_.repository).get(Repository_.creationTime));
        criteria.select(inst).where(cb.and(pkgMatch, colMatch)).orderBy(order);
        List<Installation> resultList = em.createQuery(criteria).setMaxResults(1).getResultList();
        return resultList.isEmpty() ? null : resultList.iterator().next();
    }

    @Transactional
    public void persist(Installation inst) {
        em.persist(inst);
    }

}
