/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.project.factory.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.bpmn.BPMNDefinitionSet;
import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.Package;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.Bounds;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewImpl;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BPMNProjectDiagramFactoryTest {

    private static final String NAME = "name1";
    private static final String PKG = "org.kie.wb.common.stunner.bpmn.project.test";

    @Mock
    ProjectMetadata metadata;
    @Mock
    Graph graph;
    @Mock
    Node diagramNode;
    @Mock
    Bounds bounds;
    BPMNDiagram diagram;
    private View<BPMNDiagram> diagramNodeContent;
    private final List<Node> graphNodes = new ArrayList<>(1);

    private BPMNProjectDiagramFactory tested;

    @Before
    public void setup() throws Exception {
        diagram = new BPMNDiagram.BPMNDiagramBuilder().build();
        diagramNodeContent = new ViewImpl<BPMNDiagram>(diagram,
                                                       bounds);
        graphNodes.add(diagramNode);
        when(diagramNode.getContent()).thenReturn(diagramNodeContent);
        when(graph.nodes()).thenReturn(graphNodes);
        tested = new BPMNProjectDiagramFactory();
    }

    @Test
    public void testMetadataType() {
        Class<? extends Metadata> type = tested.getMetadataType();
        assertEquals(ProjectMetadata.class,
                     type);
    }

    @Test
    public void testDefinitionSetType() {
        Class<?> type = tested.getDefinitionSetType();
        assertEquals(BPMNDefinitionSet.class,
                     type);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildNoPackageSpecified() {
        when(metadata.getProjectPackage()).thenReturn(null);
        ProjectDiagram pdiagram = tested.build(NAME,
                                               metadata,
                                               graph);
        assertNotNull(pdiagram);
        assertEquals(graph,
                     pdiagram.getGraph());
        assertEquals(NAME,
                     diagram.getDiagramSet().getId().getValue());
        assertEquals(Package.DEFAULT_PACKAGE,
                     diagram.getDiagramSet().getPackageProperty().getValue());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild() {
        final String pName = "p1";
        when(metadata.getProjectPackage()).thenReturn(PKG);
        when(metadata.getProjectName()).thenReturn(pName);
        ProjectDiagram pdiagram = tested.build(NAME,
                                               metadata,
                                               graph);
        assertNotNull(pdiagram);
        assertEquals(graph,
                     pdiagram.getGraph());
        assertEquals(pName + "." + NAME,
                     diagram.getDiagramSet().getId().getValue());
        assertEquals(PKG,
                     diagram.getDiagramSet().getPackageProperty().getValue());
    }
}
