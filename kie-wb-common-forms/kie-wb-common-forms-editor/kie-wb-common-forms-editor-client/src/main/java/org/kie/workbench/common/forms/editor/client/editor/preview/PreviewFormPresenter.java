/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.forms.editor.client.editor.preview;

import java.util.Date;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.kie.workbench.common.forms.adf.definitions.settings.ColSpan;
import org.kie.workbench.common.forms.adf.engine.shared.formGeneration.layout.LayoutGenerator;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutColumnDefinition;
import org.kie.workbench.common.forms.adf.service.definitions.layout.LayoutSettings;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.multiple.accordion.AccordionLayoutComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.multiple.tabs.TabbedLayoutComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple.fieldSet.FieldSetLayoutComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple.panel.PanelLayoutComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.layouts.simple.well.WellLayoutComponent;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.StaticModelFormRenderingContext;
import org.kie.workbench.common.forms.model.FieldDataType;
import org.kie.workbench.common.forms.model.FieldDefinition;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.kie.workbench.common.forms.model.PortableJavaModel;
import org.kie.workbench.common.forms.service.FieldManager;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;

@Dependent
public class PreviewFormPresenter implements IsWidget {

    public interface PreviewFormPresenterView extends IsWidget {
        public void preview( FormRenderingContext context );
    }

    private PreviewFormPresenterView view;

    private FieldManager fieldManager;

    @Inject
    public PreviewFormPresenter( PreviewFormPresenterView view, FieldManager fieldManager ) {
        this.view = view;
        this.fieldManager = fieldManager;
    }

    public void preview( FormRenderingContext context ) {

        FormDefinition form = new FormDefinition(new PortableJavaModel(Test.class.getName()));
        form.setId("form");

        LayoutGenerator layoutGenerator = new LayoutGenerator();

        layoutGenerator.init(new LayoutColumnDefinition[]{new LayoutColumnDefinition(ColSpan.AUTO)});

        createFieldDefinition("panel1", String.class, form);
        createFieldDefinition("panel2", String.class, form);
        createFieldDefinition("panel3", String.class, form);
        createFieldDefinition("panel4", String.class, form);
        createFieldDefinition("well1", String.class, form);
        createFieldDefinition("well2", String.class, form);
        createFieldDefinition("fieldset1", String.class, form);
        createFieldDefinition("fieldset2", String.class, form);
        createFieldDefinition("name", String.class, form);
        createFieldDefinition("lastName", String.class, form);
        createFieldDefinition("birthday", Date.class, form);
        createFieldDefinition("married", Boolean.class, form);
        createFieldDefinition("accordion1", String.class, form);
        createFieldDefinition("accordion2", Date.class, form);
        createFieldDefinition("accordion3", Boolean.class, form);
        createFieldDefinition("accordion4", String.class, form);

        LayoutComponent panel = new LayoutComponent(PanelLayoutComponent.class.getName());
        panel.getProperties().put(PanelLayoutComponent.TITLE_PARAM, "Panel!");
        panel.getProperties().put(PanelLayoutComponent.LAYOUT_PARAM, "{\"version\":1,\"name\":\"test\",\"layoutProperties\":{},\"rows\":[{\"layoutColumns\":[{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"panel1\",\"form_id\":\"form\"}}]},{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"panel2\",\"form_id\":\"form\"}}]}]}]}");

        layoutGenerator.addComponent(panel, new LayoutSettings());

        panel = new LayoutComponent(PanelLayoutComponent.class.getName());
        panel.getProperties().put(PanelLayoutComponent.TITLE_PARAM, "");
        panel.getProperties().put(PanelLayoutComponent.LAYOUT_PARAM, "{\"version\":1,\"name\":\"test\",\"layoutProperties\":{},\"rows\":[{\"layoutColumns\":[{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"panel3\",\"form_id\":\"form\"}}]},{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"panel4\",\"form_id\":\"form\"}}]}]}]}");

        layoutGenerator.addComponent(panel, new LayoutSettings());

        LayoutComponent well = new LayoutComponent(WellLayoutComponent.class.getName());
        well.getProperties().put(PanelLayoutComponent.TITLE_PARAM, "Well!");
        well.getProperties().put(PanelLayoutComponent.LAYOUT_PARAM, "{\"version\":1,\"name\":\"test\",\"layoutProperties\":{},\"rows\":[{\"layoutColumns\":[{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"well1\",\"form_id\":\"form\"}}]},{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"well2\",\"form_id\":\"form\"}}]}]}]}");

        layoutGenerator.addComponent(well, new LayoutSettings());


        LayoutComponent fieldSet = new LayoutComponent(FieldSetLayoutComponent.class.getName());
        fieldSet.getProperties().put(FieldSetLayoutComponent.TITLE_PARAM, "FieldSet!");
        fieldSet.getProperties().put(FieldSetLayoutComponent.LAYOUT_PARAM, "{\"version\":1,\"name\":\"test\",\"layoutProperties\":{},\"rows\":[{\"layoutColumns\":[{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"fieldset1\",\"form_id\":\"form\"}}]},{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"fieldset2\",\"form_id\":\"form\"}}]}]}]}");

        layoutGenerator.addComponent(fieldSet, new LayoutSettings());

        LayoutComponent tabs = new LayoutComponent(TabbedLayoutComponent.class.getName());

        JSONArray jsonArray = new JSONArray();

        JSONObject tab = new JSONObject();

        tab.put(TabbedLayoutComponent.TITLE_PARAM, new JSONString("Personal Data"));
        tab.put(TabbedLayoutComponent.LAYOUT_PARAM, new JSONString("{\"version\":1,\"name\":\"test\",\"layoutProperties\":{},\"rows\":[{\"layoutColumns\":[{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"name\",\"form_id\":\"form\"}}]},{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"lastName\",\"form_id\":\"form\"}}]}]}]}"));

        jsonArray.set(0, tab);

        tab = new JSONObject();
        tab.put(TabbedLayoutComponent.TITLE_PARAM, new JSONString("Extra Data"));
        tab.put(TabbedLayoutComponent.LAYOUT_PARAM, new JSONString("{\"version\":1,\"name\":\"test\",\"layoutProperties\":{},\"rows\":[{\"layoutColumns\":[{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"married\",\"form_id\":\"form\"}}]},{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"birthday\",\"form_id\":\"form\"}}]}]}]}"));

        jsonArray.set(1, tab);

        tabs.getProperties().put(TabbedLayoutComponent.LAYOUTS_PARAMS, jsonArray.toString());

        layoutGenerator.addComponent( tabs, new LayoutSettings());

        LayoutComponent accordion = new LayoutComponent(AccordionLayoutComponent.class.getName());

        JSONArray accordionJson = new JSONArray();

        JSONObject accordionPanel = new JSONObject();

        accordionPanel.put(AccordionLayoutComponent.TITLE_PARAM, new JSONString("Personal Data in Accordion"));
        accordionPanel.put(AccordionLayoutComponent.LAYOUT_PARAM, new JSONString("{\"version\":1,\"name\":\"test\",\"layoutProperties\":{},\"rows\":[{\"layoutColumns\":[{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"accordion2\",\"form_id\":\"form\"}}]},{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"accordion1\",\"form_id\":\"form\"}}]}]}]}"));

        accordionJson.set(0, accordionPanel);

        accordionPanel = new JSONObject();
        accordionPanel.put(AccordionLayoutComponent.TITLE_PARAM, new JSONString("Extra Data in Accordion"));
        accordionPanel.put(AccordionLayoutComponent.LAYOUT_PARAM, new JSONString("{\"version\":1,\"name\":\"test\",\"layoutProperties\":{},\"rows\":[{\"layoutColumns\":[{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"accordion3\",\"form_id\":\"form\"}}]},{\"span\":\"6\",\"rows\":[],\"layoutComponents\":[{\"dragTypeName\":\"org.kie.workbench.common.forms.editor.client.editor.rendering.EditorFieldLayoutComponent\",\"properties\":{\"field_id\":\"accordion4\",\"form_id\":\"form\"}}]}]}]}"));

        accordionJson.set(1, accordionPanel);

        accordion.getProperties().put(TabbedLayoutComponent.LAYOUTS_PARAMS, accordionJson.toString());

        layoutGenerator.addComponent( accordion, new LayoutSettings());

        form.setLayoutTemplate(layoutGenerator.build());

        StaticModelFormRenderingContext ctx = new StaticModelFormRenderingContext();
        ctx.setModel(new Test());
        ctx.setRootForm(form);
        view.preview( ctx );
    }

    protected void createFieldDefinition( String name, Class type, FormDefinition form ) {
        FieldDefinition field = fieldManager.getDefinitionByDataType(new FieldDataType(type.getName()));
        field.setName(name);
        field.setId(name);
        field.setBinding(name);
        field.setLabel(name);
        form.getFields().add(field);
    }
    @Override
    public Widget asWidget() {
        return view.asWidget();
    }
}
