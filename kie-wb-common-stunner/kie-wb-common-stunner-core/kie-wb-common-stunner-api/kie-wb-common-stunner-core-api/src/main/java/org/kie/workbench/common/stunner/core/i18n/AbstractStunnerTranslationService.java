/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.i18n;

import org.uberfire.commons.validation.PortablePreconditions;

public abstract class AbstractStunnerTranslationService implements StunnerTranslationService {

    public static final String I18N_SEPARATOR = ".";

    public static final String TITLE_SUFFIX = "title";
    public static final String CATEGORY_SUFFIX = "category";
    public static final String DESCRIPTION_SUFFIX = "description";
    public static final String CAPTION_SUFFIX = "caption";
    public static final String PROPERTY_SET_NAME_SUFFIX = "propertySetName";

    @Override
    public String getDefinitionSetDescription(String defSetId) {
        PortablePreconditions.checkNotNull("defSetId",
                                           defSetId);
        return getKeyValue(defSetId + I18N_SEPARATOR + DESCRIPTION_SUFFIX);
    }

    @Override
    public String getPropertySetName(String proepSetId) {
        return getKeyValue(proepSetId + I18N_SEPARATOR + PROPERTY_SET_NAME_SUFFIX);
    }

    @Override
    public String getDefinitionTitle(String defId) {
        PortablePreconditions.checkNotNull("defId",
                                           defId);
        return getKeyValue(defId + I18N_SEPARATOR + TITLE_SUFFIX);
    }

    @Override
    public String getDefinitionCategory(String defId) {
        PortablePreconditions.checkNotNull("defId",
                                           defId);
        return getKeyValue(defId + I18N_SEPARATOR + CATEGORY_SUFFIX);
    }

    @Override
    public String getDefinitionDescription(String defId) {
        PortablePreconditions.checkNotNull("defId",
                                           defId);
        return getKeyValue(defId + I18N_SEPARATOR + DESCRIPTION_SUFFIX);
    }

    @Override
    public String getPropertyCaption(String propId) {
        PortablePreconditions.checkNotNull("propId",
                                           propId);
        return getKeyValue(propId + I18N_SEPARATOR + CAPTION_SUFFIX);
    }

    @Override
    public String getPropertyDescription(String propId) {
        PortablePreconditions.checkNotNull("propId",
                                           propId);
        return getKeyValue(propId + I18N_SEPARATOR + DESCRIPTION_SUFFIX);
    }

    protected abstract String getKeyValue(String key);
}
