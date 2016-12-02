/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.dynamic.poc.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.FormDefinition;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.layout.AfterElement;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.layout.StartElement;

@Portable
@FormDefinition (
        i18nBundle = "org.kie.workbench.common.forms.dynamic.poc.model.I18nBundle",
        i18nKeysPreffix = "Task"
)
public class Task {

    @StartElement
    private Priority priority;

    @AfterElement ( value = "priority", mewRow = false )
    private String name;

    @AfterElement ( value = "name", mewRow = false )
    private String description;

    @AfterElement ( value = "description", mewRow = false )
    private boolean done;

    public Priority getPriority() {
        return priority;
    }

    public void setPriority( Priority priority ) {
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription( String description ) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone( boolean done ) {
        this.done = done;
    }
}
