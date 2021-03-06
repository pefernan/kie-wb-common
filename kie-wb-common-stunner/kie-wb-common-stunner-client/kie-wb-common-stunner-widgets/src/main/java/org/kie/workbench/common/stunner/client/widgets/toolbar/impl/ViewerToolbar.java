/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.client.widgets.toolbar.impl;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.client.widgets.toolbar.ToolbarView;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ClearSelectionToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.SwitchGridToolbarCommand;
import org.kie.workbench.common.stunner.client.widgets.toolbar.command.ToolbarCommandFactory;
import org.kie.workbench.common.stunner.client.widgets.toolbar.item.AbstractToolbarItem;
import org.kie.workbench.common.stunner.core.client.session.impl.AbstractClientReadOnlySession;

public class ViewerToolbar extends AbstractToolbar<AbstractClientReadOnlySession> {

    private final ManagedInstance<AbstractToolbarItem<AbstractClientReadOnlySession>> items;
    private final ToolbarCommandFactory commandFactory;

    ViewerToolbar(final ToolbarCommandFactory commandFactory,
                  final ManagedInstance<AbstractToolbarItem<AbstractClientReadOnlySession>> items,
                  final ToolbarView<AbstractToolbar> view) {
        super(view);
        this.commandFactory = commandFactory;
        this.items = items;
        addDefaultCommands();
    }

    @SuppressWarnings("unchecked")
    private void addDefaultCommands() {
        addCommand(commandFactory.newClearSelectionCommand());
        addCommand(commandFactory.newSwitchGridCommand());
    }

    @Override
    protected AbstractToolbarItem<AbstractClientReadOnlySession> newToolbarItem() {
        return items.get();
    }

    public ClearSelectionToolbarCommand getClearSelectionToolbarCommand() {
        return (ClearSelectionToolbarCommand) getCommand(0);
    }

    public SwitchGridToolbarCommand getSwitchGridToolbarCommand() {
        return (SwitchGridToolbarCommand) getCommand(1);
    }
}
