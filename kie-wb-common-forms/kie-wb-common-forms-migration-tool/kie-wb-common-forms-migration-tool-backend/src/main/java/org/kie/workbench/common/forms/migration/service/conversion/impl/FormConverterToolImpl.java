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

package org.kie.workbench.common.forms.migration.service.conversion.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyField;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyForm;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionConstants;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionResult;
import org.kie.workbench.common.forms.migration.service.conversion.FormConverterTool;
import org.kie.workbench.common.forms.migration.service.conversion.SuccessfulFormConversionResult;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionContext;
import org.kie.workbench.common.forms.migration.service.conversion.FieldConverter;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

@ApplicationScoped
public class FormConverterToolImpl implements FormConverterTool {

    protected Map<String, FieldConverter> converters = new HashMap<>();

    @Inject
    public FormConverterToolImpl(Instance<FieldConverter> converterInstance) {
        converterInstance.iterator().forEachRemaining(converter -> converter.getSupportedLegacyFieldTypes().forEach(type -> converters.put(type, converter)));
    }

    @Override
    public ConversionResult convertForm(LegacyForm legacyForm) {

        FormDefinition formDefinition = new FormDefinition();
        formDefinition.setId(legacyForm.getId().toString());
        formDefinition.setName(legacyForm.getName());

        ConversionContext context = new ConversionContext(legacyForm, formDefinition);

        List<LegacyField> outputProcessing = new ArrayList<>();

        for(LegacyField field : legacyForm.getFormFields()) {
            FieldConverter converter = converters.get(field.getFieldType());
            if (converter!=null) {

                String binding;

                boolean sameBinding = StringUtils.equals(field.getInputBinding(), field.getOutputBinding());

                if (sameBinding) {
                    binding = field.getInputBinding();
                } else {
                    if (StringUtils.isEmpty(field.getOutputBinding())) {
                        binding = field.getInputBinding();
                    } else if (StringUtils.isEmpty(field.getInputBinding())) {
                        binding = field.getOutputBinding();
                    } else {
                        binding = field.getInputBinding();
                        outputProcessing.add(field);
                    }
                }
                ConversionResult result = converter.convert(field, binding, context);
            }
        }

        for(LegacyField field : outputProcessing) {
            FieldConverter converter = converters.get(field.getFieldType());
            if (converter!=null) {
                field.setGroupWithPrevious(false);
                ConversionResult result = converter.convert(field, field.getOutputBinding(), context);
            }
        }

        generateLayout(context);

        return new SuccessfulFormConversionResult(formDefinition, context.getTranslations());
    }

    protected void generateLayout(ConversionContext context) {
        LayoutTemplate layoutTemplate = new LayoutTemplate();

        context.getFormDefinition().setLayoutTemplate(layoutTemplate);

        context.getLayoutStructure().forEach(row -> {
            LayoutRow layoutRow = new LayoutRow();

            layoutTemplate.addRow(layoutRow);

            int avgSpan = Math.floorDiv(ConversionConstants.COL_MAX_SPAN,
                                        row.size());

            int offset = ConversionConstants.COL_MAX_SPAN % row.size();

            for (LayoutComponent layoutComponent : row) {
                int span = avgSpan;
                if(offset > 0) {
                    avgSpan ++;
                    offset --;
                }
                LayoutColumn layoutCol = new LayoutColumn(String.valueOf(span));
                layoutCol.add(layoutComponent);
                layoutRow.add(layoutCol);
            }
        });
    }
}
