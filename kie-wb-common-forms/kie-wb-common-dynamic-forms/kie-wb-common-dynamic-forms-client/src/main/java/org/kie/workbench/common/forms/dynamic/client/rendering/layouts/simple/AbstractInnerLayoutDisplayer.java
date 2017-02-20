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

import javax.annotation.PostConstruct;

import org.jboss.errai.common.client.dom.HTMLElement;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.util.parser.LayoutTemplateParser;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.uberfire.commons.validation.PortablePreconditions;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

public abstract class AbstractInnerLayoutDisplayer<VIEW extends InnerLayoutDisplayerView> implements InnerLayoutDisplayer {

    protected String title;
    protected LayoutTemplate layoutTemplate;
    protected FormRenderingContext formRenderingContext;

    protected VIEW view;

    public AbstractInnerLayoutDisplayer(VIEW view) {
        this.view = view;
    }

    @PostConstruct
    public void initialize() {
        view.init(this);
    }

    @Override
    public void init(String title,
                     String jsonLayoutTemplate,
                     FormRenderingContext renderingContext) {
        PortablePreconditions.checkNotNull("title",
                                           title);
        PortablePreconditions.checkNotNull("layoutTemplate",
                                           jsonLayoutTemplate);
        PortablePreconditions.checkNotNull("renderingContext",
                                           renderingContext);

        this.title = title;
        this.layoutTemplate = LayoutTemplateParser.parseLayoutTemplate(jsonLayoutTemplate);
        this.formRenderingContext = renderingContext.getCopyFor(layoutTemplate);
        view.render(formRenderingContext);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public FormRenderingContext getFormRenderingContext() {
        return formRenderingContext;
    }

    @Override
    public boolean isInitialized() {
        return title != null && layoutTemplate != null && formRenderingContext != null;
    }

    @Override
    public boolean isValid() {
        return view.isValid();
    }

    @Override
    public HTMLElement getElement() {
        return view.getElement();
    }
}
