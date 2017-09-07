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

package org.kie.workbench.common.forms.migration.service.conversion.impl.converters;

import org.kie.workbench.common.forms.migration.model.legacy.LegacyField;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionContext;
import org.kie.workbench.common.forms.migration.service.conversion.ConversionResult;
import org.kie.workbench.common.forms.migration.service.conversion.DefaultSuccessConversionResult;
import org.kie.workbench.common.forms.migration.service.conversion.FieldConverter;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

public abstract class AbstractFieldConverter implements FieldConverter {

    @Override
    public ConversionResult convert(LegacyField legacyField,
                                    String binding,
                                    ConversionContext context) {

        context.addLayoutComponent(getComponent(legacyField,
                                                context),
                                   !legacyField.getGroupWithPrevious());

        return new DefaultSuccessConversionResult();
    }

    protected abstract LayoutComponent getComponent(LegacyField legacyField,
                                                    ConversionContext context);
}
