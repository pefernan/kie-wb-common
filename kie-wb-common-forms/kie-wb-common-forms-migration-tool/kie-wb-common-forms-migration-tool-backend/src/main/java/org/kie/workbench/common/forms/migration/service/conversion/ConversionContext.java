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

package org.kie.workbench.common.forms.migration.service.conversion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyForm;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionConstants;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public class ConversionContext {

    private LegacyForm legacyForm;

    private FormDefinition formDefinition;

    private List<List<LayoutComponent>> layoutStructure;

    private Map<String, Map<String, String>> translations;

    public ConversionContext(LegacyForm legacyForm,
                             FormDefinition formDefinition) {
        this.legacyForm = legacyForm;
        this.formDefinition = formDefinition;
        this.translations = new HashMap<>();
        this.layoutStructure = new ArrayList<>();
    }

    public LegacyForm getLegacyForm() {
        return legacyForm;
    }

    public FormDefinition getFormDefinition() {
        return formDefinition;
    }

    public List<List<LayoutComponent>> getLayoutStructure() {
        return layoutStructure;
    }

    public Map<String, Map<String, String>> getTranslations() {
        return translations;
    }

    public void registerTranslation(String lang, String key, String value) {
        if (!StringUtils.isEmpty(lang) && !StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)) {
            Map<String, String> langTranslations = translations.get(lang);

            if (langTranslations == null) {
                langTranslations = new HashMap<>();
                translations.put(lang, langTranslations);
            }

            langTranslations.put(key, value);
        }
    }

    public void addLayoutComponent(LayoutComponent layoutComponent, boolean newLine) {
        List<LayoutComponent> currentColumn;
        if (newLine || layoutStructure.isEmpty()) {
            currentColumn = newLine();
        } else {
            currentColumn = layoutStructure.get(layoutStructure.size() -1);
        }

        if(currentColumn.size() == ConversionConstants.MAX_ROW_ELEMENTS) {
            currentColumn = newLine();
        }

        currentColumn.add(layoutComponent);
    }

    private List<LayoutComponent> newLine() {
        List<LayoutComponent> newColumn = new ArrayList<>();
        layoutStructure.add(newColumn);
        return newColumn;
    }
}
