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

package org.kie.workbench.common.forms.cms.persistence.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersistenceServiceImpl implements PersistenceService {

    private Map<String, List<Map<String, Object>>> data = new HashMap<>();

    @Override
    public PersistenceResponse createInstance(String type,
                                              Map<String, Object> instance) {
        getTableForType(type).add(instance);
        return PersistenceResponse.SUCCESS;
    }

    @Override
    public PersistenceResponse saveInstance(String type,
                                            int index,
                                            Map<String, Object> instance) {
        getTableForType(type).set(index, instance);
        return PersistenceResponse.SUCCESS;
    }

    @Override
    public List<Map<String, Object>> query(String type) {
        return getTableForType(type);
    }

    @Override
    public Map<String, Object> getInstance(String type,
                                      int index) {
        return getTableForType(type).get(index);
    }

    @Override
    public PersistenceResponse deleteInstance(String type,
                                              int index) {
        getTableForType(type).remove(index);
        return PersistenceResponse.SUCCESS;
    }

    public List<Map<String, Object>> getTableForType(String type) {
        List<Map<String, Object>> table = data.get(type);

        if( table == null) {
            table = new ArrayList<>();
            data.put(type, table);
        }

        return table;
    }
}
