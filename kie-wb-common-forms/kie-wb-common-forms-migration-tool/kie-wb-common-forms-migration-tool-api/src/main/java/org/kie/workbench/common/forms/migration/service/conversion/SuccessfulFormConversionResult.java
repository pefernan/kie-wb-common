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

package org.kie.workbench.common.forms.migration.service.conversion;

import java.util.Map;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.model.FormDefinition;

@Portable
public class SuccessfulFormConversionResult implements ConversionResult {

    private FormDefinition result;

    private Map<String, Map<String, String>> constants;

    public SuccessfulFormConversionResult(FormDefinition result,
                                          Map<String, Map<String, String>> constants) {
        this.result = result;
        this.constants = constants;
    }

    public FormDefinition getResult() {
        return result;
    }

    public Map<String, Map<String, String>> getConstants() {
        return constants;
    }

    @Override
    public ConversionType getType() {
        return ConversionType.SUCCESS;
    }
}
