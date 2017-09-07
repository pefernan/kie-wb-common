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

package org.kie.workbench.common.forms.migration.service.conversion.field;

import java.util.Map;
import java.util.Properties;

import org.kie.workbench.common.forms.migration.service.conversion.ConversionResult;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public class SuccessfulFieldConversionResult implements ConversionResult {

    private FieldDefinition fieldDefinition;

    private Map<String, Properties> translations;

    public SuccessfulFieldConversionResult(FieldDefinition fieldDefinition,
                                           Map<String, Properties> translations) {
        this.fieldDefinition = fieldDefinition;
        this.translations = translations;
    }

    public FieldDefinition getFieldDefinition() {
        return fieldDefinition;
    }

    public Map<String, Properties> getTranslations() {
        return translations;
    }

    @Override
    public ConversionType getType() {
        return ConversionType.SUCCESS;
    }
}
