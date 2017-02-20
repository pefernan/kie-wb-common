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

package org.kie.workbench.common.forms.dynamic.client.rendering.layouts.multiple.tabs.displayer;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.gwtbootstrap3.client.ui.NavTabs;
import org.gwtbootstrap3.client.ui.TabContent;
import org.gwtbootstrap3.client.ui.TabListItem;
import org.gwtbootstrap3.client.ui.TabPane;
import org.gwtbootstrap3.client.ui.TabPanel;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.api.IsElement;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple.InnerLayoutDisplayer;

@Templated
public class TabDisplayerViewImpl implements TabDisplayerView,
                                             IsElement {

    public static final String TAB_CONTENT_MARGIN_CSS = "tab-content-margin";

    private TabDisplayer presenter;

    @Inject
    @DataField
    private TabPanel panel = new TabPanel();

    private NavTabs header = new NavTabs();
    private TabContent content = new TabContent();

    @PostConstruct
    public void init() {
        panel.add(header);
        panel.add(content);
        content.getElement().addClassName(TAB_CONTENT_MARGIN_CSS);
    }

    @Override
    public void init(TabDisplayer presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clear() {
        header.clear();
        content.clear();
    }

    @Override
    public void addDisplayer(InnerLayoutDisplayer displayer) {
        TabPane pane = new TabPane();
        pane.add(ElementWrapperWidget.getWidget(displayer.getElement()));
        content.add(pane);

        TabListItem title = new TabListItem(displayer.getTitle());
        title.setDataTargetWidget(pane);
        header.add(title);
        if (header.getElement().getChildCount() == 1) {
            title.setActive(true);
            pane.setActive(true);
        }
    }
}
