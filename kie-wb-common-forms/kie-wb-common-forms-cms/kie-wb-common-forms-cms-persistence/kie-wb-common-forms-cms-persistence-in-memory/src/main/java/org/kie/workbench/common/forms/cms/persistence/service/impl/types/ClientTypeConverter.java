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

import org.apache.commons.beanutils.PropertyUtils;
import org.kie.workbench.common.forms.cms.persistence.service.fakeModel.Client;
import org.kie.workbench.common.forms.cms.persistence.service.impl.TypeConverter;

public class ClientTypeConverter extends TypeConverter<Client> {

    @Override
    public Map<String, Object> toFlatValue(Client client,
                                           ClassLoader classLoader) throws Exception {

        Map<String, Object> flatValue = new HashMap<>();

        flatValue.put("id",
                      client.getId());
        flatValue.put("name",
                      client.getName());
        flatValue.put("address",
                      client.getAddress());
        return flatValue;
    }

    @Override
    public Client toRawValue(Map<String, Object> flatValue,
                             Client client,
                             ClassLoader classLoader) throws Exception {

        final Client clientInstance;

        if(client == null) {
            clientInstance = new Client();
        } else {
            clientInstance = client;
        }

        flatValue.entrySet().forEach(entry -> {
            try {
                PropertyUtils.setProperty(clientInstance,
                                          entry.getKey(),
                                          entry.getValue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return clientInstance;
    }
}
