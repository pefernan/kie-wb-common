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

package org.kie.workbench.common.forms.migration.model;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.forms.migration.model.legacy.LegacyForm;

@Portable
public class FormSummary {

    private LegacyForm legacyForm;

private FormStatus status;

    public FormSummary(LegacyForm legacyForm,
                       FormStatus status) {
        this.legacyForm = legacyForm;
        this.status = status;
    }

    public LegacyForm getLegacyForm() {
        return legacyForm;
    }

    public void setLegacyForm(LegacyForm legacyForm) {
        this.legacyForm = legacyForm;
    }

    public FormStatus getStatus() {
        return status;
    }

    public void setStatus(FormStatus status) {
        this.status = status;
    }
}
