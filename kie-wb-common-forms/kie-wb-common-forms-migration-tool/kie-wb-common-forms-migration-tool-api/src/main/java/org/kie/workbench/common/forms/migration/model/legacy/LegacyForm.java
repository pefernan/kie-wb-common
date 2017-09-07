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
package org.kie.workbench.common.forms.migration.model.legacy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.kie.workbench.common.forms.migration.model.legacy.data.DataHolder;

public class LegacyForm implements Serializable,
                                   Comparable {

    public static final String LABEL_MODE_DEFAULT = "default";

    private Long id;

    private String name;

    private String labelMode = LABEL_MODE_DEFAULT;

    private Set<LegacyField> formFields = new TreeSet<>();

    private List<DataHolder> holders = new ArrayList<>();

    public LegacyForm() {

    }

    public Long getId() {
        return this.id;
    }

    public String getItemClassName() {
        return LegacyForm.class.getName();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabelMode() {
        return labelMode;
    }

    public void setLabelMode(String labelMode) {
        this.labelMode = labelMode;
    }

    public Set<LegacyField> getFormFields() {
        return this.formFields;
    }

    public void setFormFields(Set<LegacyField> formFields) {
        this.formFields = formFields;
    }

    public String toString() {
        return "Form [" + getName() + "]";
    }

    public boolean equals(Object other) {
        if (!(other instanceof LegacyForm)) {
            return false;
        }
        LegacyForm castOther = (LegacyForm) other;
        return this.getId().equals(castOther.getId());
    }

    public int hashCode() {
        return getId().hashCode();
    }

    /**
     * Get field by name
     * @param name Desired field name, must be not null
     * @return field by given name or null if it doesn't exist.
     */
    public LegacyField getField(String name) {
        if (name == null || name.trim().length() == 0) {
            return null;
        }
        if (getFormFields() != null) {

            for (LegacyField field : formFields) {
                if (name.equals(field.getFieldName())) {
                    return field;
                }
            }
        }
        return null;
    }

    public List<DataHolder> getHolders() {
        return holders;
    }

    public void setHolders(List<DataHolder> holders) {
        this.holders = holders;
    }

    public boolean containsFormField(String fieldName) {
        if (formFields != null && fieldName != null && !"".equals(fieldName)) {
            for (LegacyField formField : formFields) {
                if (formField.getFieldName().equals(fieldName)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSynchronizationObject() {
        return ("JBPM Form " + this.getId()).intern();
    }

    /**
     * Get a Set with all field names (Strings) present in this form
     * @return a Set with all field names present in this form
     */
    public Set getFieldNames() {
        Set s = new TreeSet();
        for (LegacyField formField : formFields) {
            s.add(formField.getFieldName());
        }
        return s;
    }

    @Override
    public int compareTo(Object o) {
        return id.compareTo(((LegacyForm) o).getId());
    }

    public void addDataHolder(DataHolder holder) {
        holders.add(holder);
    }
}
