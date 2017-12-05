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

package demo.invoices.rules;

import java.util.List;

import demo.invoices.Client;
import org.kie.api.runtime.rule.DataSource;
import org.kie.workbench.common.forms.cms.persistence.drools.units.InstanceQueryRuleUnit;

public class ClientNotFromBarcelonaRuleUnit implements InstanceQueryRuleUnit<Client> {

    private DataSource<Client> dataSource;
    private List<Client> instances;

    @Override
    public DataSource<Client> getDataSource() {
        return dataSource;
    }

    @Override
    public void setDataSource(DataSource<Client> dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Client> getInstances() {
        return instances;
    }

    @Override
    public void setInstances(List<Client> instances) {
        this.instances = instances;
    }

    @Override
    public Class<Client> getModelType() {
        return Client.class;
    }
}
