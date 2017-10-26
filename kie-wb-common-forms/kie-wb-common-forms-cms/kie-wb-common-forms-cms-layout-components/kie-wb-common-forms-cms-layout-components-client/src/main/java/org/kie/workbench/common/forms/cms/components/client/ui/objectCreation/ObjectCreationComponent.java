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
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.cms.components.client.resources.i18n.CMSComponentsConstants;
import org.kie.workbench.common.forms.cms.components.client.ui.AbstractFormsCMSLayoutComponent;
import org.kie.workbench.common.forms.cms.components.client.ui.settings.SettingsDisplayer;
import org.kie.workbench.common.forms.cms.components.shared.model.objectCreation.ObjectCreationSettings;
import org.kie.workbench.common.forms.dynamic.client.DynamicFormRenderer;

@Dependent
public class ObjectCreationComponent extends AbstractFormsCMSLayoutComponent<ObjectCreationSettings, ObjectCreationSettingsReader> {

    private DynamicFormRenderer formRenderer;

    @Inject
    public ObjectCreationComponent(TranslationService translationService,
                                   SettingsDisplayer settingsDisplayer,
                                   ObjectCreationSettingsReader reader,
                                   DynamicFormRenderer formRenderer) {
        super(translationService,
              settingsDisplayer,
              reader);
        this.formRenderer = formRenderer;
    }

    @Override
    public String getDragComponentTitle() {
        return translationService.getTranslation(CMSComponentsConstants.ObjectCreationComponentTitle);
    }

    @Override
    protected IsWidget getWidget() {
        return formRenderer;
    }
}
