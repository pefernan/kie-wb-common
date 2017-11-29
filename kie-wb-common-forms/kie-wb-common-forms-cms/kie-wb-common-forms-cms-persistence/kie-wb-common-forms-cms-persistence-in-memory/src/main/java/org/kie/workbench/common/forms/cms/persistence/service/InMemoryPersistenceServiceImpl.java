/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.cms.persistence.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.persistence.service.impl.TypePersistenceServiceRegistry;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistenceResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistenceService;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentModel;
import org.uberfire.commons.data.Pair;

@Service
@ApplicationScoped
public class InMemoryPersistenceServiceImpl implements PersistenceService {

    private Map<String, List<Pair<String, Map<String, Object>>>> data = new HashMap<>();

    private Map<String, TypePersistenceService> persistenceServices = new HashMap<>();

    private BackendApplicationRuntime applicationRuntime;

    @Inject
    public InMemoryPersistenceServiceImpl(BackendApplicationRuntime applicationRuntime) {
        this.applicationRuntime = applicationRuntime;
    }

    @Override
    public PersistenceResponse createInstance(PersistentModel instance) {
        if(instance.getId() != null) {
            return saveInstance(instance);
        }
        instance.setId(UUID.randomUUID().toString());
        return getPersistenceService(instance.getType()).createInstance(instance);
    }

    @Override
    public PersistenceResponse saveInstance(PersistentModel instance) {

        if(instance.getId() == null) {
            return createInstance(instance);
        }

        List<Pair<String, Map<String, Object>>> table = getTableForType(instance.getType());

        Pair<String, Map<String, Object>> modelPair = table.stream().filter(pair->pair.getK1().equals(instance.getId())).findFirst().orElse(null);

        if(modelPair == null) {
            return PersistenceResponse.ERROR;
        }

        Pair newModelPair = new Pair(instance.getId(), instance.getModel());

        table.set(table.indexOf(modelPair), newModelPair);

        return PersistenceResponse.SUCCESS;
    }

    @Override
    public PersistentModel getInstance(String type,
                                       String id) {

        Pair<String, Map<String, Object>> modelPair = getTableForType(type).stream().filter(pair -> pair.getK1().equals(id)).findFirst().orElse(null);

        if(modelPair != null) {
            return new PersistentModel(modelPair.getK1(), type, modelPair.getK2());
        }

        return null;
    }

    @Override
    public List<PersistentModel> query(String type) {
        return getTableForType(type).stream().map(pair -> new PersistentModel(pair.getK1(), type, pair.getK2())).collect(Collectors.toList());
    }

    @Override
    public PersistenceResponse deleteInstance(String type,
                                              String id) {
        List<Pair<String, Map<String, Object>>> table = getTableForType(type);

        table.removeIf(pair -> pair.getK1().equals(id));

        return PersistenceResponse.SUCCESS;
    }

    private TypePersistenceService getPersistenceService(String type) {
        TypePersistenceService persistenceService = persistenceServices.get(type);

        if(persistenceService == null) {
            persistenceService = TypePersistenceServiceRegistry.getConverter(type);

            persistenceService.init(applicationRuntime.getAppKieContainer());

            persistenceServices.put(type, persistenceService);
        }

        return persistenceService;
    }

    private List<Pair<String, Map<String, Object>>> getTableForType(String type) {
        List<Pair<String, Map<String, Object>>> table = data.get(type);

        if( table == null) {
            table = new ArrayList<>();
            data.put(type, table);
        }

        return table;
    }
}
