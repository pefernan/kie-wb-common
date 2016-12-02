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

package org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.metaModel;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.DefaultFieldDefinition;

@java.lang.annotation.Retention( RetentionPolicy.RUNTIME )
@java.lang.annotation.Target({ ElementType.TYPE })
public @interface FieldDefinition {
    Class<? extends org.kie.workbench.common.forms.model.FieldDefinition> type() default DefaultFieldDefinition.class;
}
