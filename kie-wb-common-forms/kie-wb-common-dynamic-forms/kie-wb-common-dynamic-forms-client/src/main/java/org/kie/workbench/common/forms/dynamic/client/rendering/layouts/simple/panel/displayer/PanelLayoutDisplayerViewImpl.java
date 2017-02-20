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

package org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple.panel.displayer;

import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple.AbstractInnerLayoutDisplayerView;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;

@Templated
public class PanelLayoutDisplayerViewImpl extends AbstractInnerLayoutDisplayerView<PanelLayoutDisplayer> implements PanelLayoutDisplayerView {

    @Inject
    @DataField
    private Div heading;

    @Inject
    @Named("h3")
    @DataField
    private HTMLElement title;

    @Inject
    @DataField
    private Div layout;

    @Inject
    public PanelLayoutDisplayerViewImpl(DynamicFormRenderer dynamicFormRenderer) {
        super(dynamicFormRenderer);
    }

    @Override
    public void render(FormRenderingContext context) {
        super.render(context);

        boolean showTitle = presenter.getTitle() != null && !presenter.getTitle().isEmpty();

        if (showTitle) {
            title.setTextContent(presenter.getTitle());
        }

        heading.getStyle().setProperty("display",
                                       showTitle ? "block" : "none");

        DOMUtil.appendWidgetToElement(layout,
                                      dynamicFormRenderer);
    }
}
