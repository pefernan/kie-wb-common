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

package org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.kie.workbench.common.forms.dynamic.client.rendering.FormRendererComponent;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.uberfire.ext.layout.editor.client.api.LayoutDragComponent;
import org.uberfire.ext.layout.editor.client.api.RenderingContext;

public abstract class AbstractInnerLayoutComponent<DISPLAYER extends InnerLayoutDisplayer> extends FormRendererComponent implements LayoutDragComponent {

    public static final String LAYOUT_PARAM = "innerLayout";
    public static final String TITLE_PARAM = "title";

    protected DISPLAYER displayer;

    public AbstractInnerLayoutComponent(DISPLAYER displayer) {
        this.displayer = displayer;
    }

    @Override
    public void init(FormRenderingContext renderingContext) {
        super.init(renderingContext);
    }

    public IsWidget getPreviewWidget(RenderingContext ctx) {
        return getWidget(ctx);
    }

    public IsWidget getShowWidget(RenderingContext ctx) {
        return getWidget(ctx);
    }

    protected IsWidget getWidget(RenderingContext ctx) {
        if (!displayer.isInitialized()) {
            String jsonLayoutTemplate = ctx.getComponent().getProperties().get(LAYOUT_PARAM);
            String title = ctx.getComponent().getProperties().get(TITLE_PARAM);

            displayer.init(title,
                           jsonLayoutTemplate,
                           renderingContext);
        }
        return ElementWrapperWidget.getWidget(displayer.getElement());
    }

    public boolean isValid() {
        return displayer.isValid();
    }
}
