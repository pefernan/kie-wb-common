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

import java.util.ArrayList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.collaboration.store.InitializationContext;

@Portable
public class CompositeCommandInitializationContext implements InitializationContext {

    private boolean reverse;
    private List<InitializationContext> contexts = new ArrayList<>();

    public CompositeCommandInitializationContext(@MapsTo("reverse") boolean reverse) {
        this.reverse = reverse;
    }

    public boolean isReverse() {
        return reverse;
    }

    public List<InitializationContext> getContexts() {
        return contexts;
    }

    public void setContexts(List<InitializationContext> contexts) {
        this.contexts = contexts;
    }

    public CompositeCommandInitializationContext addCommand(InitializationContext context) {
        contexts.add(context);
        return this;
    }
}
