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

package org.kie.workbench.common.forms.cms.components.client.ui.objectCreation;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.cms.components.client.resources.i18n.CMSComponentsConstants;
import org.kie.workbench.common.forms.cms.components.client.ui.AbstractFormsCMSLayoutComponent;
import org.kie.workbench.common.forms.cms.components.client.ui.displayer.FormDisplayer;
import org.kie.workbench.common.forms.cms.components.client.ui.settings.SettingsDisplayer;
import org.kie.workbench.common.forms.cms.components.service.shared.RenderingContextGenerator;
import org.kie.workbench.common.forms.cms.components.shared.model.objectCreation.ObjectCreationSettings;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;

@Dependent
public class ObjectCreationComponent extends AbstractFormsCMSLayoutComponent<ObjectCreationSettings, ObjectCreationSettingsReader> {

    private FormDisplayer displayer;

    private Caller<RenderingContextGenerator> contextGenerator;

    private FormRenderingContext context;

    @Inject
    public ObjectCreationComponent(TranslationService translationService,
                                   SettingsDisplayer settingsDisplayer,
                                   ObjectCreationSettingsReader reader,
                                   Caller<RenderingContextGenerator> contextGenerator,
                                   FormDisplayer displayer) {
        super(translationService,
              settingsDisplayer,
              reader);
        this.contextGenerator = contextGenerator;
        this.displayer = displayer;
    }

    @Override
    public String getDragComponentTitle() {
        return translationService.getTranslation(CMSComponentsConstants.ObjectCreationComponentTitle);
    }

    @Override
    protected IsWidget getWidget() {

        if (checkSettings()) {
            contextGenerator.call((RemoteCallback<FormRenderingContext>) contextResponse -> {
                if (contextResponse != null) {
                    displayer.setEnabled(true);
                    context = contextResponse;
                    displayer.init(context,
                                   () -> {
                                   },
                                   () -> {
                                   });
                } else {
                    displayer.setEnabled(false);
                }
            }).generateContext(settings.getOu(),
                                   settings.getProject(),
                                   settings.getForm());
        } else {
            displayer.setEnabled(false);
        }

        return ElementWrapperWidget.getWidget(displayer.getElement());
    }

    @Override
    protected boolean checkSettings() {
        return super.checkSettings() && settings.getForm() != null;
    }
}
