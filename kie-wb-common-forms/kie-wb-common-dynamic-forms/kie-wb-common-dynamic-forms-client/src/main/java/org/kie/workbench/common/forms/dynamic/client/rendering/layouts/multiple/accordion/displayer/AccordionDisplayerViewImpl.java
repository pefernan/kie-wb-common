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

package org.kie.workbench.common.forms.dynamic.client.rendering.layouts.multiple.accordion.displayer;

import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.Panel;
import org.gwtbootstrap3.client.ui.PanelCollapse;
import org.gwtbootstrap3.client.ui.PanelGroup;
import org.gwtbootstrap3.client.ui.PanelHeader;
import org.gwtbootstrap3.client.ui.constants.Toggle;
import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple.InnerLayoutDisplayer;

@Templated
public class AccordionDisplayerViewImpl implements AccordionDisplayerView,
                                                   IsElement {

    public static final String PANEL_CONTENT_MARGIN_CSS = "panel-content-margin";

    private AccordionDisplayer presenter;

    @Inject
    @DataField
    private PanelGroup panel = new PanelGroup();

    @Override
    public void init(AccordionDisplayer presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        panel.clear();
    }

    @Override
    public void addDisplayer(InnerLayoutDisplayer displayer) {

        Panel childPanel = new Panel();

        PanelCollapse body = new PanelCollapse();

        HTMLElement element = displayer.getElement();
        element.setClassName(PANEL_CONTENT_MARGIN_CSS);

        body.add(ElementWrapperWidget.getWidget(element));

        PanelHeader header = new PanelHeader();
        header.setText(displayer.getTitle());
        header.setDataTargetWidget(body);
        header.setDataParent(panel.getId());
        header.setDataToggle(Toggle.COLLAPSE);

        childPanel.add(header);
        childPanel.add(body);

        panel.add(childPanel);
    }
}
