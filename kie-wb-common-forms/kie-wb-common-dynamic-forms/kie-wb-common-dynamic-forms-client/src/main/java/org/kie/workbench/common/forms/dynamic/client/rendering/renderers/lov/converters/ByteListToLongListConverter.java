/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.dynamic.client.rendering.renderers.lov.converters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.errai.databinding.client.api.Converter;

public class ByteListToLongListConverter implements Converter<List, List> {

    @Override
    public Class<List> getModelType() {
        return List.class;
    }

    @Override
    public Class<List> getComponentType() {
        return List.class;
    }

    @Override
    public List<Byte> toModelValue(List componentValue) {
        if(componentValue == null) {
            return null;
        }

        List<Long> longList = componentValue;

        return longList.stream().map(longValue -> {
            if(longValue == null) {
                return null;
            }
            return longValue.byteValue();
        }).collect(Collectors.toList());
    }

    @Override
    public List<Long> toWidgetValue(List modelValue) {
        if(modelValue == null) {
            return new ArrayList<>();
        }

        List<Byte> byteList = modelValue;

        return byteList.stream().map(byteValue -> {
            if(byteValue == null) {
                return null;
            }
            return byteValue.longValue();
        }).collect(Collectors.toList());
    }
}
