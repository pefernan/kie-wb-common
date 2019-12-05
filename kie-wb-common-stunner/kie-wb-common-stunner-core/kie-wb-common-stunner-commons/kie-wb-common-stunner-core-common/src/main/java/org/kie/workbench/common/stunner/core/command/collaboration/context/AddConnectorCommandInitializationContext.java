/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.command.collaboration.context;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.uberfire.collaboration.store.InitializationContext;

@Portable
public class AddConnectorCommandInitializationContext implements InitializationContext {
    private Node sourceNode;
    private Edge candidate;
    private Connection connection;
    private String shapeSetId;

    public AddConnectorCommandInitializationContext(@MapsTo("sourceNode") Node sourceNode, @MapsTo("candidate") Edge candidate, @MapsTo("connection") Connection connection, @MapsTo("shapeSetId") String shapeSetId) {
        this.sourceNode = sourceNode;
        this.candidate = candidate;
        this.connection = connection;
        this.shapeSetId = shapeSetId;
    }

    public Node getSourceNode() {
        return sourceNode;
    }

    public Edge getCandidate() {
        return candidate;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getShapeSetId() {
        return shapeSetId;
    }
}
