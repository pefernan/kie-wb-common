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

package org.kie.workbench.common.stunner.core.client.canvas.command.collaboration;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.kie.workbench.common.stunner.core.client.canvas.command.AddChildNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddConnectorCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.AddNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.DeleteCanvasNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.SetConnectionTargetNodeCommand;
import org.kie.workbench.common.stunner.core.client.canvas.command.UpdateElementPositionCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.collaboration.context.AddCanvasChildNodeCommandInitializationContext;
import org.kie.workbench.common.stunner.core.command.collaboration.context.AddConnectorCommandInitializationContext;
import org.kie.workbench.common.stunner.core.command.collaboration.context.AddNodeCommandInitializationContext;
import org.kie.workbench.common.stunner.core.command.collaboration.context.CompositeCommandInitializationContext;
import org.kie.workbench.common.stunner.core.command.collaboration.context.DeferredCompositeCommandInitializationContext;
import org.kie.workbench.common.stunner.core.command.collaboration.context.DeleteCanvasNodeCommandInitializationContext;
import org.kie.workbench.common.stunner.core.command.collaboration.context.SetConnectionTargetNodeCommandInitializationContext;
import org.kie.workbench.common.stunner.core.command.collaboration.context.UpdateElementPositionCommandInitializationContext;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCompositeCommand;
import org.uberfire.collaboration.store.CollaborationCommand;
import org.uberfire.collaboration.store.CollaborationCommandFactory;
import org.uberfire.collaboration.store.InitializationContext;

public class StunnerCollaborationCommandFactory implements CollaborationCommandFactory {

    private final Map<Class<? extends InitializationContext>, Function<InitializationContext, CollaborationCommand>> builders = new HashMap<>();

    {
        builders.put(UpdateElementPositionCommandInitializationContext.class, context -> updateElementPosition((UpdateElementPositionCommandInitializationContext) context));
        builders.put(AddCanvasChildNodeCommandInitializationContext.class, context -> addCanvasChildNode((AddCanvasChildNodeCommandInitializationContext) context));
        builders.put(DeleteCanvasNodeCommandInitializationContext.class, context -> deleteCanvasNode((DeleteCanvasNodeCommandInitializationContext) context));
        builders.put(CompositeCommandInitializationContext.class, context -> newCompositeCommand((CompositeCommandInitializationContext) context));
        builders.put(AddConnectorCommandInitializationContext.class, context -> newAddConnectorCommand((AddConnectorCommandInitializationContext) context));
        builders.put(SetConnectionTargetNodeCommandInitializationContext.class, context -> newSetConnectionTargetNodeCommand((SetConnectionTargetNodeCommandInitializationContext) context));
        builders.put(DeferredCompositeCommandInitializationContext.class, context -> newDeferredCompositeCommand((DeferredCompositeCommandInitializationContext) context));
        builders.put(AddNodeCommandInitializationContext.class, context -> newAddNodeCommand((AddNodeCommandInitializationContext) context));
    }

    @Override
    public CollaborationCommand newCommand(InitializationContext context) {
        return builders.get(context.getClass()).apply(context);
    }

    private AddChildNodeCommand addCanvasChildNode(AddCanvasChildNodeCommandInitializationContext context) {
        return new AddChildNodeCommand(context.getParent(), context.getCandidate(), context.getShapeSetId());
    }

    private UpdateElementPositionCommand updateElementPosition(UpdateElementPositionCommandInitializationContext context) {
        return new UpdateElementPositionCommand(context.getElement(), context.getLocation());
    }

    private DeleteCanvasNodeCommand deleteCanvasNode(DeleteCanvasNodeCommandInitializationContext context) {
        return new DeleteCanvasNodeCommand(context.getCandidate(), context.getParent());
    }

    private DeferredCompositeCommand newDeferredCompositeCommand(DeferredCompositeCommandInitializationContext compositeContext) {
        DeferredCompositeCommand.Builder builder = new DeferredCompositeCommand.Builder();

        if (compositeContext.isReverse()) {
            builder.reverse();
        }
        compositeContext.getContexts().forEach(context -> builder.deferCommand(() -> newCommand(context)));
        return builder.build();
    }

    private CompositeCommand newCompositeCommand(CompositeCommandInitializationContext compositeContext) {
        CompositeCommand compositeCommand = new CompositeCommand(compositeContext.isReverse());
        compositeContext.getContexts().forEach(context -> compositeCommand.addCommand((Command) newCommand(context)));
        return compositeCommand;
    }

    private AddConnectorCommand newAddConnectorCommand(AddConnectorCommandInitializationContext context) {
        return new AddConnectorCommand(context.getSourceNode(), context.getCandidate(), context.getConnection(), context.getShapeSetId());
    }

    private SetConnectionTargetNodeCommand newSetConnectionTargetNodeCommand(SetConnectionTargetNodeCommandInitializationContext context) {
        return new SetConnectionTargetNodeCommand(context.getNode(), context.getEdge(), context.getConnection());
    }

    private AddNodeCommand newAddNodeCommand(AddNodeCommandInitializationContext context) {
        return new AddNodeCommand(context.getCandidate(), context.getShapeSetId());
    }
}
