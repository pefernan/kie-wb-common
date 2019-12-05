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
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.uberfire.collaboration.store.InitializationContext;

@Portable
public class UpdateElementPositionCommandInitializationContext implements InitializationContext {

    private final Node<View<?>, Edge> element;
    private final Point2D location;

    public UpdateElementPositionCommandInitializationContext(@MapsTo("element") Node<View<?>, Edge> element, @MapsTo("location") Point2D location) {
        this.element = element;
        this.location = location;
    }

    public Node<View<?>, Edge> getElement() {
        return element;
    }

    public Point2D getLocation() {
        return location;
    }
}
