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

package org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple.well.displayer;

import javax.inject.Inject;

import org.jboss.errai.common.client.dom.DOMUtil;
import org.jboss.errai.common.client.dom.Div;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple.AbstractInnerLayoutDisplayerView;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;

@Templated
public class WellLayoutDisplayerViewImpl extends AbstractInnerLayoutDisplayerView<WellLayoutDisplayer> implements WellLayoutDisplayerView {

    @Inject
    @DataField
    private Div layout;

    @Inject
    public WellLayoutDisplayerViewImpl(DynamicFormRenderer dynamicFormRenderer) {
        super(dynamicFormRenderer);
    }

    @Override
    public void render(FormRenderingContext context) {
        super.render(context);

        DOMUtil.appendWidgetToElement(layout,
                                      dynamicFormRenderer);
    }
}
