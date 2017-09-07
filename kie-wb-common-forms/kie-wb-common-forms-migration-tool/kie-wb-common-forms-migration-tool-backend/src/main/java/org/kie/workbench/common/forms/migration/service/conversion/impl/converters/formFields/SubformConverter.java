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

package org.kie.workbench.common.forms.migration.service.conversion.impl.converters.formFields;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;

import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.subForm.definition.SubFormFieldDefinition;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyField;
import org.kie.workbench.common.forms.model.FieldDefinition;

@Dependent
public class SubformConverter extends AbstractField2FieldDefinitionConverter {

    @Override
    protected Map<String, String> getTypeBindings() {
        Map<String, String> bindings = new HashMap<>();

        bindings.put("Subform", "");

        return new HashMap<>();
    }

    @Override
    protected FieldDefinition doConvert(LegacyField legacyField) {
        SubFormFieldDefinition subform = new SubFormFieldDefinition();

        // TODO: Lookup forms
        subform.setNestedForm(legacyField.getDefaultSubform());

        return subform;
    }
}
