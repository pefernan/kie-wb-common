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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Instance;

import demo.invoices.rules.ClientCreateRuleUnit;
import demo.invoices.rules.ClientDeleteRuleUnit;
import demo.invoices.rules.ClientFromBarcelonaRuleUnit;
import demo.invoices.rules.ClientSaveRuleUnit;
import org.guvnor.common.services.project.model.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.persistence.drools.units.PersistentRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.shared.OperationResult;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DroolsBasedStorageTest {

    private final static String CLIENT = "demo.invoices.Client";

    @Mock
    private Project project;

    @Mock
    private KieContainer kieContainer;

    @Mock
    private BackendApplicationRuntime applicationRuntime;

    @Mock
    private Instance<PersistentRuleUnit<?>> persistentRuleUnits;

    private DroolsBasedStorage storage;

    @Before
    public void init() {

        List<PersistentRuleUnit<?>> units = new ArrayList<>();

        units.add(new ClientCreateRuleUnit());
        units.add(new ClientSaveRuleUnit());
        units.add(new ClientDeleteRuleUnit());
        units.add(new ClientFromBarcelonaRuleUnit());

        when(applicationRuntime.getAppKieContainer()).thenReturn(kieContainer);
        when(persistentRuleUnits.iterator()).thenReturn(units.iterator());

        storage = new DroolsBasedStorage(applicationRuntime,
                                         persistentRuleUnits);
    }

    @Test
    public void test() {

        Map<String, Object> instance = new HashMap<>();

        instance.put("id",
                     new Long(12346));
        instance.put("name",
                     "Pere");
        instance.put("address",
                     "Sitges");

        PersistentInstance persistentInstance = new PersistentInstance(null,
                                                                       CLIENT,
                                                                       instance);

        OperationResult response = storage.createInstance(persistentInstance).getResult();

        Map<String, Object> monica = new HashMap<>();
        monica.put("name", "Monica");
        monica.put("address", "Barcelona");

        storage.createInstance(new PersistentInstance(null, CLIENT, monica));

        assertEquals(OperationResult.SUCCESS,
                     response);
        assertNotNull(persistentInstance.getId());

        instance.put("name",
                     "Pere Fernandez");
        response = storage.saveInstance(persistentInstance).getResult();

        assertEquals(OperationResult.SUCCESS,
                     response);

        response = storage.deleteInstance(persistentInstance.getType(),
                                          persistentInstance.getId()).getResult();
        assertEquals(OperationResult.SUCCESS,
                     response);
        assertNotNull(persistentInstance.getId());
    }
}
