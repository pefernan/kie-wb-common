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

package org.kie.workbench.common.forms.processing.engine.handling;

/**
 * Component that validates the form state
 */
public interface FormValidator<MODEL> {

    public static final String NESTED_PROPERTY_SEPARATOR = "_";

    /**
     * Sets the rendered {@link Form}
     */
    void setForm(Form form);

    /**
     * Validates the given form model
     * @return True or false depending on the validations
     */
    boolean validate(MODEL model);

    /**
     * Validates a specific field value.
     * @return True or false depending on the validation
     */
    boolean validate(String fieldName,
                     MODEL model);
}
