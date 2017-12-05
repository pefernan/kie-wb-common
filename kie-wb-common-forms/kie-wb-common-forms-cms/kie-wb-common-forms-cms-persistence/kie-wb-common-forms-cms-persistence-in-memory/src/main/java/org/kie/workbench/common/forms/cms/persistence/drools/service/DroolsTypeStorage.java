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

package org.kie.workbench.common.forms.cms.persistence.drools.service;

import java.util.Collection;

import org.drools.core.datasources.CursoredDataSource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.workbench.common.forms.cms.persistence.drools.units.PersistentRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;

public interface DroolsTypeStorage {

    void init(String type,
              KieContainer kieContainer,
              CursoredDataSource dataSource,
              RuleUnitExecutor ruleUnitExecutor,
              Collection<PersistentRuleUnit> persistentRuleUnits);

    PersistentInstance createInstance(PersistentInstance instance);

    PersistentInstance saveInstance(PersistentInstance instance);

    PersistentInstance getInstance(String id);

    Collection<PersistentInstance> query();

    boolean deleteInstance(String id);

    String getType();
}
