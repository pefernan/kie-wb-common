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

package org.kie.workbench.common.forms.cms.persistence.service.impl.types;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.runtime.KieContainer;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ClientTypePersistenceServiceTest {

    @Mock
    private KieContainer kieContainer;

    private ClientTypePersistenceService service;

    @Before
    public void init() {
        service = new ClientTypePersistenceService();

        service.init(kieContainer);
    }

    @Test
    public void testCreate() {
        Map<String, Object> instance = new HashMap<>();

        instance.put("id", new Long(12346));
        instance.put("name", "Pere");
        instance.put("address", "Sitges");

        PersistentInstance pere = service.createInstance(new PersistentInstance(null, service.getType(), instance));

        instance = new HashMap<>();

        instance.put("id", new Long(123463));
        instance.put("name", "Monica");
        instance.put("address", "Barcelona");

        PersistentInstance monica = service.createInstance(new PersistentInstance(null, service.getType(), instance));

        boolean deleted = service.deleteInstance(pere.getId());
    }
}
