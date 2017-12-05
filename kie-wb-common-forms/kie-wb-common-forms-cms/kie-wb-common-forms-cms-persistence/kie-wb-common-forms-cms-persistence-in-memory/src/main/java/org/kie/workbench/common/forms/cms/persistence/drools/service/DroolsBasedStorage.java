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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import demo.invoices.Client;
import demo.invoices.rules.ClientCreateRuleUnit;
import demo.invoices.rules.ClientDeleteRuleUnit;
import demo.invoices.rules.ClientFromBarcelonaRuleUnit;
import demo.invoices.rules.ClientNotFromBarcelonaRuleUnit;
import demo.invoices.rules.ClientSaveRuleUnit;
import org.drools.core.datasources.CursoredDataSource;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.utils.KieHelper;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.persistence.drools.service.impl.DroolsTypeStorageImpl;
import org.kie.workbench.common.forms.cms.persistence.drools.units.PersistentRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.service.Storage;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceCreationResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceDeleteResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.InstanceEditionResponse;
import org.kie.workbench.common.forms.cms.persistence.shared.OperationResult;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistentInstance;

@Service
@ApplicationScoped
public class DroolsBasedStorage implements Storage {

    private Map<String, List<PersistentRuleUnit>> deployedRuleUnits = new HashMap<>();

    private Map<String, DroolsTypeStorage> storages = new HashMap<>();

    private BackendApplicationRuntime applicationRuntime;

    private boolean init = false;

    private KieBase kieBase;
    private RuleUnitExecutor executor;

    @Inject
    public DroolsBasedStorage(BackendApplicationRuntime applicationRuntime,
                              Instance<PersistentRuleUnit<?>> persistentRuleUnits) {
        this.applicationRuntime = applicationRuntime;

        for (PersistentRuleUnit persistentRuleUnit : persistentRuleUnits) {
            List<PersistentRuleUnit> ruleUnits = deployedRuleUnits.get(persistentRuleUnit.getModelType().getName());

            if (ruleUnits == null) {
                ruleUnits = new ArrayList<>();
                deployedRuleUnits.put(persistentRuleUnit.getModelType().getName(),
                                      ruleUnits);
            }

            ruleUnits.add(persistentRuleUnit);
        }
    }

    @Override
    public InstanceCreationResponse createInstance(PersistentInstance instance) {
        instance = getTypeStorage(instance.getType()).createInstance(instance);

        if (instance != null) {
            return new InstanceCreationResponse(OperationResult.SUCCESS, instance);
        }

        return new InstanceCreationResponse(OperationResult.ERROR);
    }

    @Override
    public InstanceEditionResponse saveInstance(PersistentInstance instance) {

        instance = getTypeStorage(instance.getType()).saveInstance(instance);

        if (instance != null) {
            return new InstanceEditionResponse(OperationResult.SUCCESS, instance);
        }

        return new InstanceEditionResponse(OperationResult.ERROR);
    }

    @Override
    public PersistentInstance getInstance(String type,
                                          String id) {

        return getTypeStorage(type).getInstance(id);
    }

    @Override
    public Collection<PersistentInstance> query(String type) {
        return getTypeStorage(type).query();
    }

    @Override
    public InstanceDeleteResponse deleteInstance(String type,
                                                 String id) {

        if (getTypeStorage(type).deleteInstance(id)) {
            return new InstanceDeleteResponse(OperationResult.SUCCESS);
        }

        return new InstanceDeleteResponse(OperationResult.ERROR);
    }

    private DroolsTypeStorage getTypeStorage(String type) {
        DroolsTypeStorage storage = storages.get(type);

        if (storage == null) {
            if (!init) {
                init();
            }

            storage = new DroolsTypeStorageImpl(type,
                                                applicationRuntime.getAppKieContainer(),
                                                (CursoredDataSource) executor.newDataSource(type),
                                                executor,
                                                deployedRuleUnits.get(type));

            storages.put(type,
                         storage);
        }

        return storage;
    }

    protected void init() {
        KieHelper helper = new KieHelper();

        String queryDRL = "import " + Client.class.getName() + "\n" +
                "import " + ClientFromBarcelonaRuleUnit.class.getName() + "\n" +
                "import " + ClientNotFromBarcelonaRuleUnit.class.getName() + "\n" +
                "rule FromBarcelona @Unit( ClientFromBarcelonaRuleUnit.class ) when\n" +
                "    $c : Client(address == 'Barcelona', $name : name) from dataSource\n" +
                "then\n" +
                "    instances.add($c);\n" +
                "    System.out.println($name + \" lives in Barcelona\");\n" +
                "end\n" +
                "rule NotFromBarcelona @Unit( ClientNotFromBarcelonaRuleUnit.class ) when\n" +
                "    $c : Client(address != 'Barcelona', $name : name) from dataSource\n" +
                "then\n" +
                "    instances.add($c);\n" +
                "    System.out.println($name + \" doesn't lives in Barcelona\");\n" +
                "end";

        String createDRL = "import " + Client.class.getName() + "\n" +
                "import " + ClientCreateRuleUnit.class.getName() + "\n" +
                "rule CreateClient @Unit(ClientCreateRuleUnit.class) \n" +
                "then\n" +
                "    Client client = (Client) instance;\n" +
                "    System.out.println(\"Welcome \" + client.getName() + \" from \" + client.getAddress());\n" +
                "end\n";

        String saveDRL = "import " + Client.class.getName() + "\n" +
                "import " + ClientSaveRuleUnit.class.getName() + "\n" +
                "rule SaveClient @Unit(ClientSaveRuleUnit.class) \n" +
                "then\n" +
                "    Client client = (Client) instance;\n" +
                "    System.out.println(\"Updating \" + client.getName() + \" from \" + client.getAddress());\n" +
                "end\n";

        String deleteDRL = "import " + Client.class.getName() + "\n" +
                "import " + ClientDeleteRuleUnit.class.getName() + "\n" +
                "rule DeleteClient @Unit(ClientDeleteRuleUnit.class) \n" +
                "then\n" +
                "    Client client = (Client) instance;\n" +
                "    System.out.println(\"Goodbye \" + client.getName() + \" from \" + client.getAddress());\n" +
                "end\n";

        helper.addContent(queryDRL,
                          ResourceType.DRL);
        helper.addContent(createDRL,
                          ResourceType.DRL);
        helper.addContent(saveDRL,
                          ResourceType.DRL);
        helper.addContent(deleteDRL,
                          ResourceType.DRL);

        this.kieBase = helper.build();

        this.executor = RuleUnitExecutor.create().bind(kieBase);

        init = true;
    }
}
