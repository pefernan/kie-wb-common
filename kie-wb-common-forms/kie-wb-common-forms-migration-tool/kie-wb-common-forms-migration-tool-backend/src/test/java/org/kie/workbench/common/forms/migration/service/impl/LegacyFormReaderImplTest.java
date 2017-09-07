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

package org.kie.workbench.common.forms.migration.service.impl;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyField;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyForm;
import org.kie.workbench.common.forms.migration.util.FormContentReader;

public class LegacyFormReaderImplTest {

    public static final String CALL_CUSTOMER_FORM = "CallCustomerForComments-taskform.form";

    private LegacyFormReaderImpl formReader = new LegacyFormReaderImpl();

    @Test
    public void testReadCallCustomerForm() throws Exception {
        String formContent = FormContentReader.readFormContent(CALL_CUSTOMER_FORM);

        LegacyForm legacyForm = formReader.loadFormFromXML(formContent);

        Assertions.assertThat(legacyForm).isNotNull();
        Assertions.assertThat(legacyForm.getFormFields()).hasSize(4);

        LegacyField customer_lastname = legacyForm.getField("customer_lastname");
        Assertions.assertThat(customer_lastname).isNotNull();

        LegacyField customer_name = legacyForm.getField("customer_name");
        Assertions.assertThat(customer_name).isNotNull();

        LegacyField customer_phone = legacyForm.getField("customer_phone");
        Assertions.assertThat(customer_phone).isNotNull();

        LegacyField customer_comment = legacyForm.getField("customer_comment");
        Assertions.assertThat(customer_comment).isNotNull();
    }
}
