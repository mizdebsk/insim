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

import java.sql.Timestamp;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.fedoraproject.insim.model.Collection;
import org.fedoraproject.insim.model.Installation;
import org.fedoraproject.insim.model.Installation_;
import org.fedoraproject.insim.model.Module;
import org.fedoraproject.insim.model.Repository;
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

    /**
     * <pre>
     * SELECT i.*
     * FROM Installation i JOIN Repository r ON r.id = i.repository_id
     * WHERE i.module_name = ${module} AND r.collection_name = ${collection}
     * ORDER BY r.creationtime DESC
     * LIMIT 1
     * </pre>
     * 
     * @param module
     * @param collection
     * @return
     */
    public Installation getLatestByModuleCollection(Module module, Collection collection) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Installation> cq = cb.createQuery(Installation.class);

        Root<Installation> t_Inst = cq.from(Installation.class);
        Join<Installation, Repository> table_Repo = t_Inst.join(Installation_.repository);

        Path<Module> c_Inst_module = t_Inst.get(Installation_.module);
        Path<Collection> c_Repo_collection = table_Repo.get(Repository_.collection);
        Path<Timestamp> c_Repo_creationTime = table_Repo.get(Repository_.creationTime);

        return em
                .createQuery( //
                        cq.select(t_Inst) //
                                .where(cb.and( //
                                        cb.equal(c_Inst_module, module), //
                                        cb.equal(c_Repo_collection, collection)))
                                .orderBy(cb.desc(c_Repo_creationTime)))
                .setMaxResults(1) //
                .getResultList() //
                .stream().findFirst().orElse(null);
    }

    /**
     * <pre>
     * SELECT i.*
     * FROM Installation i JOIN Repository r ON r.id = i.repository_id
     * WHERE i.module_name = ${module}
     * ORDER BY r.creationtime ASC
     * </pre>
     * 
     * @param module
     * @return
     */
    public List<Installation> getByModule(Module module) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Installation> cq = cb.createQuery(Installation.class);

        Root<Installation> t_Inst = cq.from(Installation.class);
        Join<Installation, Repository> t_Repo = t_Inst.join(Installation_.repository);

        Path<Module> c_Inst_module = t_Inst.get(Installation_.module);
        Path<Timestamp> c_Repo_creationTime = t_Repo.get(Repository_.creationTime);

        return em.createQuery( //
                cq.select(t_Inst) //
                        .where(cb.equal(c_Inst_module, module)) //
                        .orderBy(cb.asc(c_Repo_creationTime)))
                .getResultList();
    }

    @Transactional
    public void persist(Installation inst) {
        em.persist(inst);
    }

}
