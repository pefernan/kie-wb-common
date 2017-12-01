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

import java.util.ArrayList;

import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.utils.KieHelper;
import org.kie.workbench.common.forms.cms.persistence.service.drools.units.impl.ClientCreateRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.service.fakeModel.Client;
import org.kie.workbench.common.forms.cms.persistence.service.drools.units.impl.ClientFromBarcelonaRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.service.drools.units.impl.ClientNotFromBarcelonaRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.service.impl.AbstractTypePersistenceService;
import org.kie.workbench.common.forms.cms.persistence.service.impl.TypeConverter;

public class ClientTypePersistenceService extends AbstractTypePersistenceService<Client> {

    public static final String TYPE = "demo.invoices.Client";
    public static final String DATA_SOURCE = "clients";

    public ClientTypePersistenceService() {
        super(TYPE,
              DATA_SOURCE);
    }

    @Override
    protected TypeConverter<Client> getConverter() {
        return new ClientTypeConverter();
    }

    @Override
    protected void addContent(KieHelper helper) {
        String drl = "import " + Client.class.getName() + "\n" +
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

        String drl2 = "import " + Client.class.getName() + "\n" +
                "import " + ClientCreateRuleUnit.class.getName() + "\n" +
                "rule CreateClient @Unit(ClientCreateRuleUnit.class) when\n" +
                "    $c : Client(address == 'Barcelona', this == instance) from dataSource\n" +
                "then\n" +
                "    System.out.println($c.getName() + \" lives in Barcelona\");\n" +
                "end\n";

        helper.addContent(drl,
                          ResourceType.DRL);

        helper.addContent(drl2,
                          ResourceType.DRL);
    }

    @Override
    protected void afterCreate(RuleUnitExecutor executor, Client instance) {

        ClientCreateRuleUnit createRuleUnit = new ClientCreateRuleUnit();
        createRuleUnit.setDataSource(dataSource);
        createRuleUnit.setInstance(instance);

        executor.run(createRuleUnit);

        /*ClientFromBarcelonaRuleUnit fromBarcelonaRuleUnit = new ClientFromBarcelonaRuleUnit();
        fromBarcelonaRuleUnit.setDataSource(dataSource);
        fromBarcelonaRuleUnit.setInstances(new ArrayList<>());

        executor.run(fromBarcelonaRuleUnit);

        ClientNotFromBarcelonaRuleUnit notFromBarcelona = new ClientNotFromBarcelonaRuleUnit();
        notFromBarcelona.setDataSource(dataSource);
        notFromBarcelona.setInstances(new ArrayList<>());

        executor.run(notFromBarcelona);
*/
/*
        List<Client> resultsFromBarcelona = new ArrayList<>();
        executor.bindVariable("results",
                              resultsFromBarcelona);

        executor.run(ClientFromBarcelonaRuleUnit.class);

        List<Client> resultsNotFromBarcelona = new ArrayList<>();
        executor.bindVariable("results",
                              resultsNotFromBarcelona);
        executor.run(ClientNotFromBarcelonaRuleUnit.class);*/
    }

    @Override
    protected void afterSave(RuleUnitExecutor executor) {

    }

    @Override
    protected void afterDelete(RuleUnitExecutor executor) {
        //afterCreate(executor);
    }
}
