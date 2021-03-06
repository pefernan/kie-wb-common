/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.screens.library.client.screens;

import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.kie.workbench.common.screens.examples.model.ExampleProject;
import org.kie.workbench.common.screens.library.api.LibraryInfo;
import org.kie.workbench.common.screens.library.api.LibraryService;
import org.kie.workbench.common.screens.library.api.ProjectInfo;
import org.kie.workbench.common.screens.library.client.events.ProjectDetailEvent;
import org.kie.workbench.common.screens.library.client.perspective.LibraryPerspective;
import org.kie.workbench.common.screens.library.client.util.ExamplesUtils;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.mvp.UberElement;
import org.uberfire.mvp.Command;

@WorkbenchScreen(identifier = LibraryPlaces.LIBRARY_SCREEN,
        owningPerspective = LibraryPerspective.class)
public class LibraryScreen {

    public interface View extends UberElement<LibraryScreen> {

        void clearProjects();

        void addProject( String project,
                         Command details,
                         Command select );

        void clearFilterText();

        void addProjectToImport( ExampleProject exampleProject );

        void clearImportProjectsContainer();
    }

    private View view;

    private PlaceManager placeManager;

    private LibraryPlaces libraryPlaces;

    private Event<ProjectDetailEvent> projectDetailEvent;

    private Caller<LibraryService> libraryService;

    private ExamplesUtils examplesUtils;

    LibraryInfo libraryInfo;

    @Inject
    public LibraryScreen( final View view,
                          final PlaceManager placeManager,
                          final LibraryPlaces libraryPlaces,
                          final Event<ProjectDetailEvent> projectDetailEvent,
                          final Caller<LibraryService> libraryService,
                          final ExamplesUtils examplesUtils ) {
        this.view = view;
        this.placeManager = placeManager;
        this.libraryPlaces = libraryPlaces;
        this.projectDetailEvent = projectDetailEvent;
        this.libraryService = libraryService;
        this.examplesUtils = examplesUtils;
    }

    @PostConstruct
    public void setup() {
        libraryService.call( new RemoteCallback<LibraryInfo>() {
            @Override
            public void callback( LibraryInfo libraryInfo ) {
                updateLibrary( libraryInfo );
            }
        } ).getLibraryInfo( libraryPlaces.getSelectedRepository() );
        placeManager.closePlace( LibraryPlaces.EMPTY_LIBRARY_SCREEN );
    }

    private void updateLibrary( final LibraryInfo libraryInfo ) {
        LibraryScreen.this.libraryInfo = libraryInfo;
        view.clearFilterText();
        setupProjects( libraryInfo.getProjects() );
    }

    private void setupProjects( final Set<Project> projects ) {
        view.clearProjects();

        projects.stream().forEach( p -> view.addProject( p.getProjectName(),
                                                         detailsCommand( p ),
                                                         selectCommand( p ) ) );
    }

    public void newProject() {
        libraryPlaces.goToNewProject();
    }

    public void updateProjectsBy( final String filter ) {
        if ( libraryInfo != null ) {
            Set<Project> filteredProjects = filterProjects( filter );
            setupProjects( filteredProjects );
        }
    }

    public void importProject( final ExampleProject exampleProject ) {
        examplesUtils.importProject( exampleProject );
    }

    public void updateImportProjects() {
        examplesUtils.getExampleProjects( exampleProjects -> {
            view.clearImportProjectsContainer();
            for ( ExampleProject exampleProject : exampleProjects ) {
                view.addProjectToImport( exampleProject );
            }
        } );
    }

    Command selectCommand( final Project project ) {
        return () -> {
            final ProjectInfo projectInfo = getProjectInfo( project );
            libraryPlaces.goToProject( projectInfo );
        };
    }

    Command detailsCommand( final Project project ) {
        return () -> {
            final ProjectInfo projectInfo = getProjectInfo( project );
            projectDetailEvent.fire( new ProjectDetailEvent( projectInfo ) );
        };
    }

    Set<Project> filterProjects( final String filter ) {
        return libraryInfo.getProjects().stream()
                .filter( p -> p.getProjectName() != null )
                .filter( p -> p.getProjectName().toUpperCase()
                        .startsWith( filter.toUpperCase() ) )
                .collect( Collectors.toSet() );
    }

    private ProjectInfo getProjectInfo( final Project project ) {
        return new ProjectInfo( libraryPlaces.getSelectedOrganizationalUnit(),
                                libraryPlaces.getSelectedRepository(),
                                libraryPlaces.getSelectedBranch(),
                                project );
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return "Library Screen";
    }

    @WorkbenchPartView
    public UberElement<LibraryScreen> getView() {
        return view;
    }
}
