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
import java.util.List;
import javax.enterprise.inject.Instance;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyForm;
import org.kie.workbench.common.forms.migration.service.LegacyFormReader;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionResult;
import org.kie.workbench.common.forms.migration.service.conversion.FieldConverter;
import org.kie.workbench.common.forms.migration.service.conversion.SuccessfulFormConversionResult;
import org.kie.workbench.common.forms.migration.service.conversion.impl.converters.decorators.HTMLLabelFieldConverter;
import org.kie.workbench.common.forms.migration.service.conversion.impl.converters.formFields.CheckBoxConverter;
import org.kie.workbench.common.forms.migration.service.conversion.impl.converters.formFields.HTMLEditorConverter;
import org.kie.workbench.common.forms.migration.service.conversion.impl.converters.formFields.InputDateConverter;
import org.kie.workbench.common.forms.migration.service.conversion.impl.converters.formFields.InputTextAreaConverter;
import org.kie.workbench.common.forms.migration.service.conversion.impl.converters.formFields.InputTextCharacterConverter;
import org.kie.workbench.common.forms.migration.service.conversion.impl.converters.formFields.InputTextConverter;
import org.kie.workbench.common.forms.migration.service.conversion.impl.converters.formFields.InputTextDecimalConverter;
import org.kie.workbench.common.forms.migration.service.conversion.impl.converters.formFields.InputTextIntegerConverter;
import org.kie.workbench.common.forms.migration.service.impl.LegacyFormReaderImpl;
import org.kie.workbench.common.forms.migration.service.impl.LegacyFormReaderImplTest;
import org.kie.workbench.common.forms.migration.util.FormContentReader;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FormConverterToolImplTest {

    private FormConverterToolImpl converterTool;

    private LegacyFormReader reader = new LegacyFormReaderImpl();

    @Before
    public void init() {

        List<FieldConverter> converters = new ArrayList<>();

        converters.add(new CheckBoxConverter());
        converters.add(new HTMLEditorConverter());
        converters.add(new InputDateConverter());
        converters.add(new InputTextAreaConverter());
        converters.add(new InputTextCharacterConverter());
        converters.add(new InputTextConverter());
        converters.add(new InputTextDecimalConverter());
        converters.add(new InputTextIntegerConverter());
        converters.add(new HTMLLabelFieldConverter());

        Instance<FieldConverter> fieldConverters = mock(Instance.class);
        when(fieldConverters.iterator()).thenReturn(converters.iterator());

        converterTool = new FormConverterToolImpl(fieldConverters);
    }

    @Test
    public void testConvert() throws Exception {
        String legacyContent = FormContentReader.readFormContent(LegacyFormReaderImplTest.CALL_CUSTOMER_FORM);

        LegacyForm legacyForm = reader.loadFormFromXML(legacyContent);

        ConversionResult conversionResult = converterTool.convertForm(legacyForm);

        Assertions.assertThat(conversionResult).isNotNull().isInstanceOf(SuccessfulFormConversionResult.class);

        SuccessfulFormConversionResult successfulConversionResult = (SuccessfulFormConversionResult) conversionResult;

        Assertions.assertThat(successfulConversionResult.getResult()).isNotNull();
        Assertions.assertThat(successfulConversionResult.getResult().getFields()).isNotNull();
        Assertions.assertThat(successfulConversionResult.getResult().getLayoutTemplate()).isNotNull();
        Assertions.assertThat(successfulConversionResult.getResult().getFields().size()).isEqualTo(legacyForm.getFormFields().size());
    }
}
