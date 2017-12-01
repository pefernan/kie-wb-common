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

package org.kie.workbench.common.forms.cms.persistence.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.drools.core.datasources.CursoredDataSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.utils.KieHelper;
import org.kie.workbench.common.forms.cms.persistence.service.TypePersistenceService;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistenceResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;

public abstract class AbstractTypePersistenceService<TYPE> implements TypePersistenceService {

    private String type;
    private String dataSourceName;
    private KieContainer kieContainer;
    private KieBase kieBase;
    private RuleUnitExecutor executor;

    protected CursoredDataSource<TYPE> dataSource;
    protected TypeConverter<TYPE> converter;

    private Map<String, CursoredDataSource.DataSourceFactHandle> modelHandles = new HashMap<>();

    public AbstractTypePersistenceService(String type, String dataSourceName) {
        this.type = type;
        this.dataSourceName = dataSourceName;
    }

    public void init(KieContainer kieContainer) {
        this.kieContainer = kieContainer;

        /* In a perfect world we would get the kieBase from the container but we are only in a POC ;)
        TODO: fix this when possible
        this.kieBase = kieContainer.getKieBase();
        */
        KieHelper helper = new KieHelper();

        addContent(helper);

        this.kieBase = helper.build();

        this.executor = RuleUnitExecutor.create().bind(kieBase);
        this.dataSource = (CursoredDataSource)executor.newDataSource(dataSourceName);
        this.converter = getConverter();
    }

    public String getType() {
        return type;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    protected abstract TypeConverter<TYPE> getConverter();

    protected abstract void addContent(KieHelper helper);

    protected abstract void afterCreate(RuleUnitExecutor executor, TYPE instance);

    protected abstract void afterSave(RuleUnitExecutor executor);

    protected abstract void afterDelete(RuleUnitExecutor executor);

    @Override
    public PersistentInstance createInstance(PersistentInstance persistentInstance) {
        try {
            persistentInstance.setId(UUID.randomUUID().toString());

            TYPE instance = converter.toRawValue(persistentInstance.getModel(),
                                              null,
                                              kieContainer.getClassLoader());

            CursoredDataSource.DataSourceFactHandle handle = (CursoredDataSource.DataSourceFactHandle) dataSource.insert(instance);

            modelHandles.put(persistentInstance.getId(),
                             handle);

            afterCreate(executor, instance);

            return persistentInstance;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public PersistentInstance saveInstance(PersistentInstance instance) {

        CursoredDataSource.DataSourceFactHandle handle = modelHandles.get(instance.getId());

        if(handle != null) {
            try {
                TYPE model = converter.toRawValue(instance.getModel(),
                                                  (TYPE) handle.getObject(),
                                                  kieContainer.getClassLoader());

                dataSource.update(handle, model);

                afterSave(executor);

                return instance;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public PersistentInstance getInstance(String id) {

        CursoredDataSource.DataSourceFactHandle handle = modelHandles.remove(id);

        if(handle != null) {
            try {
                Map<String, Object> flatModel = converter.toFlatValue((TYPE) handle.getObject(),
                                                                      kieContainer.getClassLoader());

                return new PersistentInstance(getType(),
                                              id,
                                              flatModel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public boolean deleteInstance(String id) {
        CursoredDataSource.DataSourceFactHandle handle = modelHandles.remove(id);

        if(handle != null) {
            dataSource.delete(handle);

            afterDelete(executor);

            return true;
        }

        return false;
    }
}
