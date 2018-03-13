/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.forms.client.formFilters;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.forms.adf.engine.shared.FormElementFilter;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;

@ApplicationScoped
public class StunnerFilterProviderManager {

    private Map<Class<?>, StunnerFormElementFilterProvider> filters = new HashMap<>();

    private ManagedInstance<StunnerFormElementFilterProvider> instance;

    protected StunnerFilterProviderManager() {
        this(null);
    }

    @Inject
    public StunnerFilterProviderManager(ManagedInstance<StunnerFormElementFilterProvider> instance) {
        this.instance = instance;
    }

    @PostConstruct
    public void init() {
        instance.forEach(provider -> filters.put(provider.getDefinitionType(), provider));
    }

    public Collection<FormElementFilter> getFilterForDefinition(String elementUUID, Element<? extends Definition<?>> element, Object definition) {
        StunnerFormElementFilterProvider provider = filters.get(definition.getClass());

        if (provider != null) {
            return provider.provideFilters(elementUUID, element, definition);
        }

        return Collections.emptyList();
    }

    @PreDestroy
    public void destroy() {
        instance.destroyAll();
    }
}
