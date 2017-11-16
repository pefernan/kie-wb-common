/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.forms.processing.engine.handling.impl;

import javax.inject.Inject;

import org.kie.workbench.common.forms.processing.engine.handling.FieldStateValidator;
import org.kie.workbench.common.forms.processing.engine.handling.Form;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.FormValidator;
import org.kie.workbench.common.forms.processing.engine.handling.ModelValidator;

public class FormValidatorImpl implements FormValidator {

    private Form form;

    private ModelValidator modelValidator;

    private FieldStateValidator fieldStateValidator;

    @Inject
    public FormValidatorImpl(ModelValidator modelValidator,
                             FieldStateValidator fieldStateValidator) {
        this.modelValidator = modelValidator;
        this.fieldStateValidator = fieldStateValidator;
    }

    @Override
    public void setForm(Form form) {
        this.form = form;
    }

    @Override
    public boolean validate(Object model) {

        clearAllFieldErrors();

        boolean isModelValid = modelValidator.validate(form.getFormFields(),
                                                       model);
        boolean isFieldStateValid = fieldStateValidator.validate(form.getFormFields());

        return isFieldStateValid && isModelValid;
    }

    @Override
    public boolean validate(String fieldName,
                            Object model) {

        clearFieldError(fieldName);

        FormField field = findFormField(fieldName);

        if (!fieldStateValidator.validate(field)) {
            return false;
        }

        return modelValidator.validate(field,
                                       model);
    }


    protected void clearAllFieldErrors() {
        for (FormField formField : form.getFormFields()) {
            formField.clearError();
        }
    }

    protected void clearFieldError(String fieldName) {
        FormField field = findFormField(fieldName);
        if (field != null) {
            field.clearError();
        }
    }

    protected FormField findFormField(String fieldName) {
        FormField field = form.getFieldByName(fieldName);

        if (field == null) {
            field = form.getFieldByBinding(fieldName);
        }

        return field;
    }

    public ModelValidator getModelValidator() {
        return this.modelValidator;
    }

    public void setModelValidator(ModelValidator modelValidator) {
        this.modelValidator = modelValidator;
    }
}
