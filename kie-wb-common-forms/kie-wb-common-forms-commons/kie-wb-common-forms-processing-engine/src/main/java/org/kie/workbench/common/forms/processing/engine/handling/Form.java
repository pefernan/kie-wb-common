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

package org.kie.workbench.common.forms.processing.engine.handling;

import java.util.Collection;
import java.util.List;

import jsinterop.annotations.JsType;

@JsType(isNative = true)
public class Form {
    private List<FormField> formFields;

    public Form(List<FormField> formFields) {
        this.formFields = formFields;
    }

    public Collection<FormField> getFormFields() {
        return formFields;
    }

    public FormField getFieldByBinding(String binding) {
        return formFields.stream().filter(formField -> binding.equals(formField.getFieldBinding())).findFirst().get();
    }

    public FormField getFieldByName(String name) {
        return formFields.stream().filter(formField -> formField.getFieldName().equals(name)).findFirst().get();
    }
}
