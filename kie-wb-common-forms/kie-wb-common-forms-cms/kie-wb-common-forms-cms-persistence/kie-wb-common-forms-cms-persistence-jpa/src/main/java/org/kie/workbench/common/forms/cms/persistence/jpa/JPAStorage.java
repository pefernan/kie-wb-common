/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.cms.persistence.jpa;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.persistence.service.Storage;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceCreationResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceDeleteResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceEditionResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.OperationResult;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;

@Dependent
public class JPAStorage implements Storage {

    private BackendApplicationRuntime runtime;

    private Map<String, Map<Object, Object>> memory = new HashMap<>();

    private JPAPersistenceManager persistenceManager;

    @Inject
    public JPAStorage(JPAPersistenceManager persistenceManager) {
        this.persistenceManager = persistenceManager;
    }

    @Override
    public void init(BackendApplicationRuntime runtime) {
        this.runtime = runtime;

        persistenceManager.init(runtime);
    }

    @Override
    public InstanceCreationResponse createInstance(PersistentInstance instance) {

        Object bean = runtime.getModuleMarshaller().unMarshall(instance.getType(), instance.getModel());

        persistenceManager.saveInstance(bean);

        Map<Object, Object> db = getDB(instance.getType());

        int id = db.size();

        db.put(id, bean);

        instance.setId(id);

        return new InstanceCreationResponse(OperationResult.SUCCESS, instance);
    }

    @Override
    public InstanceEditionResponse saveInstance(PersistentInstance instance) {
        Object bean = get(instance.getType(), instance.getId());

        runtime.getModuleMarshaller().unMarshall(bean, instance.getModel());

        return new InstanceEditionResponse(OperationResult.SUCCESS, instance);
    }

    private Object get(String type, Object id) {
        return getDB(type).get(id);
    }

    @Override
    public Collection<PersistentInstance> query(String type) {

        return getDB(type).entrySet().stream()
                .map(entry -> new PersistentInstance(entry.getKey(), type, runtime.getModuleMarshaller().marshall(entry.getValue())))
                .collect(Collectors.toList());
    }

    @Override
    public PersistentInstance getInstance(String type, Object id) {
        Object bean = get(type, id);

        return new PersistentInstance(id, type, runtime.getModuleMarshaller().marshall(bean));
    }

    @Override
    public InstanceDeleteResponse deleteInstance(String type, Object id) {
        getDB(type).remove(id);

        return new InstanceDeleteResponse(OperationResult.SUCCESS);
    }

    private Map<Object, Object> getDB(String type) {
        Map<Object, Object> db = memory.get(type);

        if (db == null) {
            db = new HashMap<>();

            memory.put(type, db);
        }

        return db;
    }
}
