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

package org.kie.workbench.common.forms.cms.persistence.drools.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import demo.invoices.Client;
import org.apache.commons.beanutils.ConstructorUtils;
import org.drools.core.datasources.CursoredDataSource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.workbench.common.forms.cms.persistence.drools.service.DroolsTypeStorage;
import org.kie.workbench.common.forms.cms.persistence.drools.units.InstanceCreateRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.drools.units.InstanceDeleteRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.drools.units.InstanceQueryRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.drools.units.InstanceSaveRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.drools.units.PersistentRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.service.impl.types.ClientTypeConverter;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;

public class DroolsTypeStorageImpl implements DroolsTypeStorage {

    private String type;
    private KieContainer kieContainer;

    private CursoredDataSource dataSource;
    private Map<String, CursoredDataSource.DataSourceFactHandle> modelHandles = new HashMap<>();

    private RuleUnitExecutor executor;

    private InstanceCreateRuleUnit createRuleUnit;
    private InstanceSaveRuleUnit saveRuleUnit;
    private InstanceDeleteRuleUnit deleteRuleUnit;
    private Map<String, InstanceQueryRuleUnit> queryRuleUnits = new HashMap<>();

    ClientTypeConverter converter = new ClientTypeConverter();

    public DroolsTypeStorageImpl(String type,
                                 KieContainer kieContainer,
                                 CursoredDataSource dataSource,
                                 RuleUnitExecutor executor,
                                 List<PersistentRuleUnit> persistentRuleUnits) {
        init(type,
             kieContainer,
             dataSource,
             executor,
             persistentRuleUnits);
    }

    @Override
    public void init(String type,
                     KieContainer kieContainer,
                     CursoredDataSource dataSource,
                     RuleUnitExecutor ruleUnitExecutor,
                     Collection<PersistentRuleUnit> persistentRuleUnits) {

        this.type = type;
        this.kieContainer = kieContainer;
        this.dataSource = dataSource;
        this.executor = ruleUnitExecutor;

        if (persistentRuleUnits != null) {
            persistentRuleUnits.forEach((PersistentRuleUnit persistentRuleUnit) -> {
                if (persistentRuleUnit instanceof InstanceCreateRuleUnit) {
                    this.createRuleUnit = (InstanceCreateRuleUnit) persistentRuleUnit;
                }

                if (persistentRuleUnit instanceof InstanceSaveRuleUnit) {
                    this.saveRuleUnit = (InstanceSaveRuleUnit) persistentRuleUnit;
                }

                if (persistentRuleUnit instanceof InstanceDeleteRuleUnit) {
                    this.deleteRuleUnit = (InstanceDeleteRuleUnit) persistentRuleUnit;
                }

                if (persistentRuleUnit instanceof InstanceQueryRuleUnit) {
                    queryRuleUnits.put(persistentRuleUnit.getClass().getName(),
                                       (InstanceQueryRuleUnit) persistentRuleUnit);
                }
            });
        }
    }

    @Override
    public PersistentInstance createInstance(PersistentInstance persistentInstance) {

        persistentInstance.setId(UUID.randomUUID().toString());

        try {
            Object instance = converter.toRawValue(persistentInstance.getModel(),
                                                   null,
                                                   kieContainer.getClassLoader());

            CursoredDataSource.DataSourceFactHandle handle = (CursoredDataSource.DataSourceFactHandle) dataSource.insert(instance);

            modelHandles.put(persistentInstance.getId(),
                             handle);

            if (createRuleUnit != null) {

                InstanceCreateRuleUnit rule = ConstructorUtils.invokeConstructor(createRuleUnit.getClass(),
                                                                                 null);

                rule.setInstance(instance);
                rule.setDataSource(dataSource);

                executor.run(rule);
            }

            return persistentInstance;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public PersistentInstance saveInstance(PersistentInstance persistentInstance) {
        CursoredDataSource.DataSourceFactHandle handle = modelHandles.get(persistentInstance.getId());

        if (handle != null) {
            try {
                Object instance = converter.toRawValue(persistentInstance.getModel(),
                                                       (Client) handle.getObject(),
                                                       kieContainer.getClassLoader());

                dataSource.update(handle,
                                  instance);

                if (saveRuleUnit != null) {

                    InstanceSaveRuleUnit rule = ConstructorUtils.invokeConstructor(saveRuleUnit.getClass(),
                                                                                   null);

                    rule.setInstance(instance);
                    rule.setDataSource(dataSource);

                    executor.run(rule);
                }

                return persistentInstance;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public PersistentInstance getInstance(String id) {

        CursoredDataSource.DataSourceFactHandle handle = modelHandles.get(id);

        if (handle != null) {
            try {
                Map<String, Object> flatInstance = converter.toFlatValue((Client) handle.getObject(),
                                                                         kieContainer.getClassLoader());

                return new PersistentInstance(getType(),
                                              id,
                                              flatInstance);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public Collection<PersistentInstance> query() {
        if (!queryRuleUnits.isEmpty()) {

            try {
                InstanceQueryRuleUnit rule = ConstructorUtils.invokeConstructor(queryRuleUnits.values().stream().findFirst().get().getClass(),
                                                                                null);

                rule.setInstances(new ArrayList());
                rule.setDataSource(dataSource);

                executor.run(rule);

                return getInstances(modelHandles.entrySet().stream().filter(entry -> rule.getInstances().contains(entry.getValue())));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return getInstances(modelHandles.entrySet().stream());
    }

    protected Collection<PersistentInstance> getInstances(Stream<Map.Entry<String, CursoredDataSource.DataSourceFactHandle>> entries) {
        return entries.map(entry -> {
            try {
                Map<String, Object> flatValue = converter.toFlatValue((Client) entry.getValue().getObject(),
                                                                      kieContainer.getClassLoader());

                return new PersistentInstance(entry.getKey(),
                                              getType(),
                                              flatValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).filter(persistenceInstance -> persistenceInstance != null).collect(Collectors.toList());
    }

    @Override
    public boolean deleteInstance(String id) {
        CursoredDataSource.DataSourceFactHandle handle = modelHandles.remove(id);

        if (handle != null) {
            try {
                if (deleteRuleUnit != null) {
                    InstanceDeleteRuleUnit rule = ConstructorUtils.invokeConstructor(deleteRuleUnit.getClass(),
                                                                                     null);

                    rule.setInstance(handle.getObject());
                    rule.setDataSource(dataSource);

                    executor.run(rule);
                }

                dataSource.delete(handle);

                return true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return false;
    }

    @Override
    public String getType() {
        return null;
    }
}
