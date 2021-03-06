/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.fields.shared.fieldTypes.basic.selectors;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormDefinition;
import org.kie.workbench.common.forms.adf.definitions.annotations.FormField;
import org.kie.workbench.common.forms.adf.definitions.annotations.i18n.I18nSettings;
import org.kie.workbench.common.forms.adf.definitions.annotations.layout.Column;
import org.kie.workbench.common.forms.adf.definitions.annotations.layout.Layout;

@Portable
@Bindable
@FormDefinition(
        i18n = @I18nSettings(keyPreffix = "FieldProperties.selector.options"),
        layout = @Layout({@Column, @Column, @Column}),
        startElement = "value"
)
public class DefaultSelectorOption<T> implements SelectorOption<Object> {

    @FormField(labelKey = "value")
    private Object value;

    @FormField(
            labelKey = "text",
            afterElement = "value"
    )
    private String text;

    @FormField(
            labelKey = "defaultValue",
            afterElement = "text"
    )
    private boolean defaultValue = false;

    public DefaultSelectorOption() {
    }

    public DefaultSelectorOption(@MapsTo("value") T value,
                                 @MapsTo("text") String text,
                                 @MapsTo("defaultValue") boolean defaultValue) {
        this.value = value;
        this.text = text;
        this.defaultValue = defaultValue;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }
}
