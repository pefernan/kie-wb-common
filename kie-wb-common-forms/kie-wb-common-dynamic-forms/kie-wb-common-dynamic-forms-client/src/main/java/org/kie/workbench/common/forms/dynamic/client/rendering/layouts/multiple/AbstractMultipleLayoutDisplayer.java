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

import javax.annotation.PreDestroy;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple.basic.displayer.BasicLayoutDisplayer;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;

public abstract class AbstractMultipleLayoutDisplayer<VIEW extends MultipleLayoutDisplayerView> implements MultipleLayoutDisplayer {

    protected VIEW view;

    protected ManagedInstance<BasicLayoutDisplayer> displayersInstance;

    public AbstractMultipleLayoutDisplayer(VIEW view,
                                           ManagedInstance<BasicLayoutDisplayer> displayersInstance) {
        this.view = view;
        this.displayersInstance = displayersInstance;

        this.view.init(this);
    }

    public void addLayout(String title,
                          String layout,
                          FormRenderingContext context) {
        BasicLayoutDisplayer displayer = displayersInstance.get();

        displayer.init(title,
                       layout,
                       context);

        view.addDisplayer(displayer);
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }

    @PreDestroy
    public void destroy() {
        view.clear();
        displayersInstance.destroyAll();
    }
}
