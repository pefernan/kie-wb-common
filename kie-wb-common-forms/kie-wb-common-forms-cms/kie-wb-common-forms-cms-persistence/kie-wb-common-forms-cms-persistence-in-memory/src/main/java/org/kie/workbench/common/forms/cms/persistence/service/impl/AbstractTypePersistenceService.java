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
import java.util.List;
import java.util.Map;

import org.kie.api.KieBase;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.utils.KieHelper;
import org.kie.workbench.common.forms.cms.persistence.service.TypePersistenceService;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistenceResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentModel;

public abstract class AbstractTypePersistenceService<TYPE> implements TypePersistenceService<TYPE> {

    private String type;
    private KieContainer kieContainer;
    private KieBase kieBase;
    private RuleUnitExecutor executor;
    private DataSource<TYPE> dataSource;
    private TypeConverter<TYPE> converter;

    private Map<String, FactHandle> modelHandles = new HashMap<>();

    public AbstractTypePersistenceService(String type) {
        this.type = type;
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
        this.dataSource = executor.newDataSource(type);
        this.converter = getConverter();
    }

    public String getType() {
        return type;
    }

    protected abstract TypeConverter<TYPE> getConverter();

    protected abstract void addContent(KieHelper helper);

    protected abstract void afterCreate(RuleUnitExecutor executor);

    @Override
    public PersistenceResponse createInstance(PersistentModel instance) {
        try {
            TYPE model = converter.toRawValue(instance.getModel(),
                                              null,
                                              kieContainer.getClassLoader());

            FactHandle handle = dataSource.insert(model);

            modelHandles.put(instance.getId(),
                             handle);

            afterCreate(executor);

            return PersistenceResponse.SUCCESS;
        } catch (Exception ex) {
            return PersistenceResponse.ERROR;
        }
    }

    @Override
    public PersistenceResponse saveInstance(PersistentModel instance) {

        return null;
    }

    @Override
    public List<PersistentModel> query(String type) {

        return null;
    }

    @Override
    public PersistentModel getInstance(String type,
                                       String id) {

        return null;
    }

    @Override
    public PersistenceResponse deleteInstance(String type,
                                              String id) {
        return null;
    }
}
