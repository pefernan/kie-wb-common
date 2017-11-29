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

package org.kie.workbench.common.forms.cms.common.backend.services.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.utils.KieHelper;
import org.kie.workbench.common.forms.cms.common.backend.services.BackendApplicationRuntime;
import org.kie.workbench.common.forms.cms.common.shared.events.FormsDeployedEvent;
import org.kie.workbench.common.forms.cms.common.shared.services.FormService;
import org.kie.workbench.common.forms.cms.common.shared.services.impl.AbstractApplicationRuntime;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
import org.kie.workbench.common.services.backend.builder.core.BuildHelper;

@ApplicationScoped
public class BackendApplicationRuntimeImpl extends AbstractApplicationRuntime implements BackendApplicationRuntime {

    private BuildHelper buildHelper;

    private KieContainer appContainer;

    private VFSFormFinderService formFinderService;

    private Event<FormsDeployedEvent> formsDeployed;

    @Inject
    public BackendApplicationRuntimeImpl(FormService formService,
                                         BuildHelper buildHelper,
                                         VFSFormFinderService formFinderService,
                                         Event<FormsDeployedEvent> formsDeployed) {
        super(formService);
        this.buildHelper = buildHelper;
        this.formFinderService = formFinderService;
        this.formsDeployed = formsDeployed;
    }

    @Override
    public void initRuntime(Project project) {
        ((Runnable) () -> {
            // Only to simulate the app deployment TODO: remove me!
            BuildHelper.BuildResult result = buildHelper.build(project);

            appContainer = result.getBuilder().getKieContainer();

            FormsDeployedEvent event = new FormsDeployedEvent(formFinderService.findAllForms(project.getRootPath()));

            formsDeployed.fire(event);
        }).run();
    }

    @Override
    public KieContainer getAppKieContainer() {
        return appContainer;
    }

    @Override
    public boolean isInitialized() {
        return appContainer != null;
    }
}
