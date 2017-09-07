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
package org.kie.workbench.common.forms.migration.type;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.workbench.type.ResourceTypeDefinition;

@ApplicationScoped
public class LegacyFormResourceTypeDefinition implements ResourceTypeDefinition {

    public static final String EXTENSION = "form";

    @Override
    public String getShortName() {
        return "Legacy Form Definition";
    }

    @Override
    public String getDescription() {
        return "Legacy Form Definitions";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return EXTENSION;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean accept(final Path path) {
        return path.getFileName().endsWith("." + getSuffix());
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "*." + getSuffix();
    }
}
