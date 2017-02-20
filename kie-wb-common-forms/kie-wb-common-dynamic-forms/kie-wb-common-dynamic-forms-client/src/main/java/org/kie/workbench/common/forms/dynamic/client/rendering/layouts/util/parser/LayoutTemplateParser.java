/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.client.rendering.layouts.util.parser;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.util.parser.model.JSONLayoutColumn;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.util.parser.model.JSONLayoutComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.util.parser.model.JSONLayoutRow;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.util.parser.model.JSONLayoutTemplate;
import org.uberfire.ext.layout.editor.api.editor.LayoutColumn;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

public class LayoutTemplateParser {

    public static LayoutTemplate parseLayoutTemplate(String templateJson) {
        JSONLayoutTemplate jsonTemplate = JsonUtils.safeEval(templateJson);

        if (jsonTemplate == null) {
            throw new IllegalArgumentException("Wrong layout template");
        }

        LayoutTemplate template = new LayoutTemplate(jsonTemplate.getName(),
                                                     jsonTemplate.getLayoutPropertiesMap());

        List<LayoutRow> rows = readRows(jsonTemplate.getRows());

        template.getRows().addAll(rows);

        return template;
    }

    private static List<LayoutRow> readRows(JsArray<JSONLayoutRow> jsonRows) {
        List<LayoutRow> rows = new ArrayList<>();

        if (jsonRows != null) {
            for (int i = 0; i < jsonRows.length(); i++) {
                JSONLayoutRow jsonRow = jsonRows.get(i);

                LayoutRow row = new LayoutRow();
                row.add(readColumns(jsonRow.getLayoutColumns()));
                rows.add(row);
            }
        }
        return rows;
    }

    private static List<LayoutColumn> readColumns(JsArray<JSONLayoutColumn> jsonColumns) {
        List<LayoutColumn> columns = new ArrayList<>();

        if (jsonColumns != null) {
            for (int i = 0; i < jsonColumns.length(); i++) {
                JSONLayoutColumn jsonColumn = jsonColumns.get(i);

                LayoutColumn column = new LayoutColumn(jsonColumn.getSpan());

                if (jsonColumn.getRows() != null) {
                    column.getRows().addAll(readRows(jsonColumn.getRows()));
                }

                if (jsonColumn.getLayoutComponents() != null) {
                    column.getLayoutComponents().addAll(readLayoutComponents(jsonColumn.getLayoutComponents()));
                }

                columns.add(column);
            }
        }

        return columns;
    }

    private static List<LayoutComponent> readLayoutComponents(JsArray<JSONLayoutComponent> jsonComponents) {
        List<LayoutComponent> components = new ArrayList<>();

        if (jsonComponents != null) {
            for (int i = 0; i < jsonComponents.length(); i++) {
                JSONLayoutComponent jsonComponent = jsonComponents.get(i);

                LayoutComponent component = new LayoutComponent(jsonComponent.getDragTypeName());
                component.addProperties(jsonComponent.getPropertiesMap());
                components.add(component);
            }
        }

        return components;
    }
}
