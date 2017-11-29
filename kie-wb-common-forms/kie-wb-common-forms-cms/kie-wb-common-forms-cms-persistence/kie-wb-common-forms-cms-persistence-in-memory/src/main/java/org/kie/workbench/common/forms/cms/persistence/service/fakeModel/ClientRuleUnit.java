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

package org.kie.workbench.common.forms.cms.persistence.service.fakeModel;

import java.util.List;

import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;

public class ClientRuleUnit implements RuleUnit {

    private String address;
    private DataSource<Client> clients;
    private List<Client> results;

    public ClientRuleUnit() {
    }

    public ClientRuleUnit(String address,
                          DataSource<Client> clients) {
        this.address = address;
        this.clients = clients;
    }

    public DataSource<Client> getClients() {
        return clients;
    }

    public List<Client> getResults() {
        return results;
    }

    public String getAddress() {
        return address;
    }

// life cycle methods

    @Override
    public void onStart() {
        System.out.println("ClientRuleUnit started.");
    }

    @Override
    public void onEnd() {
        System.out.println("ClientRuleUnit ended.");
    }
}
