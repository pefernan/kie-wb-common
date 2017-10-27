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

package org.kie.workbench.common.forms.cms.components.service.backend;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.kie.workbench.common.forms.editor.service.shared.VFSFormFinderService;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.screens.datamodeller.service.DataModelerService;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.services.datamodeller.core.DataObject;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.kie.workbench.common.services.shared.project.KieProjectService;

@Dependent
public class ProvidersHelperService {

    public static final String MASTER = "master";

    private LibraryService libraryService;

    private KieProjectService projectService;

    private DataModelerService dataModelerService;

    private VFSFormFinderService formFinderService;

    @Inject
    public ProvidersHelperService(LibraryService libraryService,
                                  KieProjectService projectService,
                                  DataModelerService dataModelerService,
                                  VFSFormFinderService formFinderService) {
        this.libraryService = libraryService;
        this.projectService = projectService;
        this.dataModelerService = dataModelerService;
        this.formFinderService = formFinderService;
    }

    public Collection<OrganizationalUnit> getOrganizationalUnits() {
        return libraryService.getOrganizationalUnits();
    }

    public Collection<KieProject> getOrganizationalUnitProjects(String ouId) {
        Collection<OrganizationalUnit> ous = getOrganizationalUnits();

        Optional<OrganizationalUnit> ouOptional = ous.stream().filter(ou -> ou.getIdentifier().equals(ouId)).findFirst();

        List<KieProject> projects = new ArrayList<>();

        if(ouOptional.isPresent()) {
            OrganizationalUnit ou = ouOptional.get();
            ou.getRepositories().forEach(repository -> {
                projects.addAll(projectService.getProjects(repository, MASTER).stream().map(project -> (KieProject)project).collect(Collectors.toList()));
            });
        }

        return projects;
    }

    public Collection<DataObject> getProjectDataObjects(String ouId, String projectName) {

        Optional<KieProject> projectOptional = getProject(ouId, projectName);

        if(projectOptional.isPresent()) {
            return dataModelerService.loadModel(projectOptional.get()).getDataObjects();
        }

        return Collections.EMPTY_LIST;
    }

    public Collection<FormDefinition> getFormsForDataObject(String ouId, String projectName, String dataObject) {
        Optional<KieProject> projectOptional = getProject(ouId, projectName);

        if(projectOptional.isPresent()) {
            return formFinderService.findFormsForType(dataObject, projectOptional.get().getRootPath());
        }

        return Collections.EMPTY_LIST;
    }

    public FormDefinition getFormById(String ouId, String projectName, String formId) {
        Optional<KieProject> projectOptional = getProject(ouId, projectName);

        if(projectOptional.isPresent()) {
            return formFinderService.findFormById(formId, projectOptional.get().getRootPath());
        }

        return null;
    }

    private Optional<KieProject> getProject(String ouId, String projectName) {
        Collection<KieProject> projects = getOrganizationalUnitProjects(ouId);

        return projects.stream().filter(kieProject -> kieProject.getProjectName().equals(projectName)).findFirst();
    }
}
