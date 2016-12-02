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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.jboss.errai.databinding.client.api.Bindable;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.FieldPolicy;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.FormDefinition;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.LabelPosition;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.properties.FormField;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.properties.FieldParam;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.properties.SkipFormField;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.properties.crud.CrudColumn;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.properties.crud.CrudSettings;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.properties.selector.Option;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.definitions.properties.selector.Options;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.layout.AfterElement;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.layout.FieldGroup;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.layout.FieldGroupType;
import org.kie.workbench.common.forms.dynamic.poc.base.annotation.layout.StartElement;
import org.kie.workbench.common.forms.model.impl.basic.selectors.listBox.StringListBoxFieldDefinition;
import org.kie.workbench.common.forms.model.impl.basic.textBox.TextBoxFieldDefinition;

@Portable
@Bindable
@FormDefinition ( labelPosition = LabelPosition.DEFAULT,
        allowInheritance = true,
        policy = FieldPolicy.ALL,
        i18nBundle = "org.kie.workbench.common.forms.dynamic.poc.model.I18nBundle",
        i18nKeysPreffix = "Person" )
public class Person {

    @SkipFormField
    private Long id;

    @StartElement
    @FieldGroup ( key = "personalData", labelKey = "personalData.legend", type = FieldGroupType.TAB )
    @FormField (
            type = TextBoxFieldDefinition.class,
            labelKey = "name.label",
            required = true,
            settings = { @FieldParam ( name = "maxLength", value = "20" ), @FieldParam ( name = "placeHolder", value = "name.placeHolder" ) }
    )
    private String name;

    @AfterElement ( value = "name", mewRow = false )
    @FormField (
            type = TextBoxFieldDefinition.class,
            labelKey = "lastName.label",
            required = true,
            settings = { @FieldParam ( name = "maxLength", value = "60" ), @FieldParam ( name = "placeHolder", value = "lastName.placeHolder" ) }
    )
    private String lastName;

    @AfterElement ( "personalData" )
    @FieldGroup ( key = "address", labelKey = "address.legend", type = FieldGroupType.TAB )
    private Address address;

    @FormField (
            type = StringListBoxFieldDefinition.class,
            labelKey = "status.label"
    )
    @Options (
            defaultValue = "single",
            options = {
                    @Option ( text = "single", value = "single" ),
                    @Option ( text = "married", value = "married" ),
                    @Option ( text = "trying", value = "trying" )
            }
    )
    private String status;

    @AfterElement ( "status" )
    @CrudSettings (
            columns = {
                    @CrudColumn ( text = "priority", property = "priority" ),
                    @CrudColumn ( text = "name", property = "name" ),
                    @CrudColumn ( text = "description", property = "description" ),
                    @CrudColumn ( text = "done", property = "done" )
            }
    )
    private List<Task> tasks;

    public Long getId() {
        return id;
    }

    public void setId( Long id ) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName( String lastName ) {
        this.lastName = lastName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress( Address address ) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus( String status ) {
        this.status = status;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks( List<Task> tasks ) {
        this.tasks = tasks;
    }
}
