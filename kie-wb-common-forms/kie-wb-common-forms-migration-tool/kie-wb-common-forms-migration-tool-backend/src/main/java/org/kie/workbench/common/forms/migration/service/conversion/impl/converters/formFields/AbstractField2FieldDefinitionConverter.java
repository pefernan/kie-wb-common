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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyField;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionConstants;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionResult;
import org.kie.workbench.common.forms.migration.service.conversion.FailedConversionResult;
import org.kie.workbench.common.forms.migration.service.conversion.field.SuccessfulFieldConversionResult;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionContext;
import org.kie.workbench.common.forms.migration.service.conversion.impl.converters.AbstractFieldConverter;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormLayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public abstract class AbstractField2FieldDefinitionConverter extends AbstractFieldConverter {

    protected Map<String, String> legacyTypeAndJavaTypeBinding = new HashMap<>();

    public AbstractField2FieldDefinitionConverter() {
        legacyTypeAndJavaTypeBinding = getTypeBindings();
    }

    @Override
    public ConversionResult convert(LegacyField legacyField,
                                    String binding,
                                    ConversionContext context) {
        super.convert(legacyField,
                      binding,
                      context);

        ConversionResult result;

        try {
            FieldDefinition fieldDefinition = doConvert(legacyField);
            fieldDefinition.setId(legacyField.getId().toString());
            fieldDefinition.setName(legacyField.getFieldName());
            fieldDefinition.setReadOnly(legacyField.getReadonly());
            fieldDefinition.setRequired(legacyField.getFieldRequired());
            fieldDefinition.setBinding(binding);
            fieldDefinition.setStandaloneClassName(legacyTypeAndJavaTypeBinding.get(legacyField.getFieldType()));

            Map<String, Properties> translations = new HashMap<>();

            legacyField.getLabel().forEach((lang, value) -> {
                context.registerTranslation(lang,
                                            legacyField.getFieldName() + ConversionConstants.CONSTANT_SEPARATOR + ConversionConstants.LABEL,
                                            value);
                if (ConversionConstants.DEFAULT_LANG.equals(lang)) {
                    fieldDefinition.setLabel(StringUtils.defaultString(value));
                }
            });

            legacyField.getTitle().forEach((lang, value) -> {
                context.registerTranslation(lang,
                                            legacyField.getFieldName() + ConversionConstants.CONSTANT_SEPARATOR + ConversionConstants.HELP_TEXT,
                                            value);
            });

            context.getFormDefinition().getFields().add(fieldDefinition);

            result = new SuccessfulFieldConversionResult(fieldDefinition,
                                                         translations);
        } catch (Exception ex) {
            result = new FailedConversionResult(ex.getMessage());
        }

        return result;
    }

    @Override
    protected LayoutComponent getComponent(LegacyField legacyField,
                                           ConversionContext context) {

        LayoutComponent component = new LayoutComponent(ConversionConstants.LAYOUT_COMPONENT_CLASS_NAME);
        component.getProperties().put(FormLayoutComponent.FORM_ID,
                                      context.getFormDefinition().getId());
        component.getProperties().put(FormLayoutComponent.FIELD_ID,
                                      legacyField.getId().toString());

        return component;
    }

    @Override
    public Collection<String> getSupportedLegacyFieldTypes() {
        return legacyTypeAndJavaTypeBinding.keySet();
    }

    protected abstract Map<String, String> getTypeBindings();

    protected abstract FieldDefinition doConvert(LegacyField legacyField);
}
