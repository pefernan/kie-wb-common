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

package org.kie.workbench.common.forms.dynamic.client.rendering.layouts.multiple;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormRendererComponent;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

public abstract class AbstractMultipleLayoutComponent<DISPLAYER extends MultipleLayoutDisplayer> extends FormRendererComponent implements LayoutDragComponent {

    public static final String LAYOUTS_PARAMS = "layouts";

    public static final String TITLE_PARAM = "title";
    public static final String LAYOUT_PARAM = "layout";

    private DISPLAYER displayer;

    private boolean initialized = false;

    public AbstractMultipleLayoutComponent(DISPLAYER displayer) {
        this.displayer = displayer;
    }

    @Override
    public IsWidget getPreviewWidget(RenderingContext renderingContext) {
        return getWidget(renderingContext);
    }

    @Override
    public IsWidget getShowWidget(RenderingContext renderingContext) {
        return getWidget(renderingContext);
    }

    private IsWidget getWidget(RenderingContext renderingContext) {

        if (!initialized) {
            loadLayouts(renderingContext);
        }

        return ElementWrapperWidget.getWidget(displayer.getElement());
    }

    private void loadLayouts(RenderingContext renderingContext) {
        String layoutsSettings = renderingContext.getComponent().getProperties().get(LAYOUTS_PARAMS);
        if (layoutsSettings != null && !layoutsSettings.isEmpty()) {

            JSONArray jsonTabs = JSONParser.parseStrict(layoutsSettings).isArray();

            if (jsonTabs != null) {
                for (int i = 0; i < jsonTabs.size(); i++) {
                    JSONObject jsonTab = jsonTabs.get(i).isObject();

                    String title = jsonTab.get(TITLE_PARAM).isString().stringValue();
                    String layout = jsonTab.get(LAYOUT_PARAM).isString().stringValue();
                    displayer.addLayout(title,
                                        layout,
                                        this.renderingContext);
                }
            }
        }
    }
}
