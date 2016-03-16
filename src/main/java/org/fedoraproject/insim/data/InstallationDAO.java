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
import java.util.function.Function;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.fedoraproject.insim.model.Collection;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Installation_;
import org.fedoraproject.insim.model.Module;
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

    private TypedQuery<Installation> createModuleCollectionQuery(Module module, Collection col, boolean orderDescTime) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Installation> criteria = cb.createQuery(Installation.class);
        Root<Installation> inst = criteria.from(Installation.class);
        Predicate modMatch = cb.equal(inst.get(Installation_.module), module);
        Predicate colMatch = cb.equal(inst.join(Installation_.repository).get(Repository_.collection), col);
        Function<Expression<?>, Order> orderFunc = orderDescTime ? cb::desc : cb::asc;
        Order order = orderFunc.apply(inst.join(Installation_.repository).get(Repository_.creationTime));
        criteria.select(inst).where(cb.and(modMatch, colMatch)).orderBy(order);
        return em.createQuery(criteria);
    }

    public List<Installation> getByModuleCollection(Module module, Collection col) {
        return createModuleCollectionQuery(module, col, false).getResultList();
    }

    public Installation getLatestByModuleCollection(Module module, Collection col) {
        return createModuleCollectionQuery(module, col, true) //
                .setMaxResults(1).getResultList().stream().findFirst().orElse(null);
    }

    @Transactional
    public void persist(Installation inst) {
        em.persist(inst);
    }

}
