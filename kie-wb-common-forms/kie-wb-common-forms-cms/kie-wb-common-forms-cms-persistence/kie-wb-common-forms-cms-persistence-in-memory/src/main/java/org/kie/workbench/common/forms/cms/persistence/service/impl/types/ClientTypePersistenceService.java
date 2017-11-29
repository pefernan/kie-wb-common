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
import java.util.List;

import org.kie.api.io.ResourceType;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.utils.KieHelper;
import org.kie.workbench.common.forms.cms.persistence.service.fakeModel.Client;
import org.kie.workbench.common.forms.cms.persistence.service.fakeModel.ClientRuleUnit;
import org.kie.workbench.common.forms.cms.persistence.service.impl.AbstractTypePersistenceService;
import org.kie.workbench.common.forms.cms.persistence.service.impl.TypeConverter;

public class ClientTypePersistenceService extends AbstractTypePersistenceService<Client> {

    public static String TYPE = "demo.invoices.Client";

    public ClientTypePersistenceService() {
        super(TYPE);
    }

    @Override
    protected TypeConverter<Client> getConverter() {
        return new ClientTypeConverter();
    }

    @Override
    protected void addContent(KieHelper helper) {
        String drl =
                "import " + Client.class.getName() + "\n" +
                        "import " + ClientRuleUnit.class.getName() + "\n" +
                        "rule FromBarcelona @Unit( ClientRuleUnit.class ) when\n" +
                        "    $c : Client(address == 'Barcelona', $name : name) from clients\n" +
                        "then\n" +
                        "    results.add($c);\n" +
                        "    System.out.println($name + \" lives in Barcelona\");\n" +
                        "end\n";/* +
                "rule NotFromBarcelona @Unit( ClientRuleUnit.class ) when\n" +
                "    Client(address != 'Barcelona', $name : name) from clients\n" +
                "then\n" +
                "    System.out.println($name + \" doesn't lives in Barcelona\");\n" +
                "end";*/

        helper.addContent(drl, ResourceType.DRL);

    }

    @Override
    protected void afterCreate(RuleUnitExecutor executor) {


        List<Client> results = new ArrayList<>();
        executor.bindVariable("results",
                              results);

        executor.run(ClientRuleUnit.class);
    }
}
