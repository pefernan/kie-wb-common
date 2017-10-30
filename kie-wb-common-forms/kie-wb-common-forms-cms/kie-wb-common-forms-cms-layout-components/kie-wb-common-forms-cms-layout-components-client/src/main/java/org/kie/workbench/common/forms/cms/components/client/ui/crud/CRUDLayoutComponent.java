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

package org.kie.workbench.common.forms.cms.components.client.ui.crud;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.databinding.client.HasProperties;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.cms.components.client.resources.i18n.CMSComponentsConstants;
import org.kie.workbench.common.forms.cms.components.client.ui.AbstractFormsCMSLayoutComponent;
import org.kie.workbench.common.forms.cms.components.client.ui.settings.SettingsDisplayer;
import org.kie.workbench.common.forms.cms.components.service.shared.RenderingContextGenerator;
import org.kie.workbench.common.forms.cms.components.shared.model.crud.CRUDSettings;
import org.kie.workbench.common.forms.crud.client.component.CrudActionsHelper;
import org.kie.workbench.common.forms.crud.client.component.CrudComponent;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.ColumnGeneratorManager;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EmbedsForm;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

@Dependent
public class CRUDLayoutComponent extends AbstractFormsCMSLayoutComponent<CRUDSettings, CRUDSettingsReader> {

    private Caller<RenderingContextGenerator> contextGenerator;
    private ColumnGeneratorManager columnGeneratorManager;
    private AsyncDataProvider<HasProperties> dataProvider;
    private CrudComponent crudComponent;
    private FormRenderingContext context;

    @Inject
    public CRUDLayoutComponent(TranslationService translationService,
                               SettingsDisplayer settingsDisplayer,
                               CRUDSettingsReader reader,
                               Caller<RenderingContextGenerator> contextGenerator,
                               ColumnGeneratorManager columnGeneratorManager,
                               CrudComponent crudComponent) {
        super(translationService,
              settingsDisplayer,
              reader);
        this.contextGenerator = contextGenerator;
        this.columnGeneratorManager = columnGeneratorManager;
        this.crudComponent = crudComponent;
    }

    @Override
    protected IsWidget getWidget() {
        if(checkSettings()) {
            contextGenerator.call((RemoteCallback<FormRenderingContext>) formRenderingContext -> {
                if (formRenderingContext != null) {
                    this.context = formRenderingContext;

                    dataProvider = new AsyncDataProvider<HasProperties>() {
                        @Override
                        protected void onRangeChanged(HasData<HasProperties> hasData) {

                        }
                    };

                    crudComponent.init(new CrudActionsHelper() {
                        @Override
                        public int getPageSize() {
                            return 5;
                        }

                        @Override
                        public boolean showEmbeddedForms() {
                            return true;
                        }

                        @Override
                        public boolean isAllowCreate() {
                            return true;
                        }

                        @Override
                        public boolean isAllowEdit() {
                            return true;
                        }

                        @Override
                        public boolean isAllowDelete() {
                            return true;
                        }

                        @Override
                        public List<ColumnMeta> getGridColumns() {
                            return getColumnMetas();
                        }

                        @Override
                        public AsyncDataProvider getDataProvider() {
                            return dataProvider;
                        }

                        @Override
                        public void createInstance() {

                        }

                        @Override
                        public void editInstance(int index) {

                        }

                        @Override
                        public void deleteInstance(int index) {

                        }
                    });
                }
            }).generateContext(settings);
        }
        return crudComponent;
    }

    private List<ColumnMeta> getColumnMetas() {
        FormDefinition tableForm = (FormDefinition) context.getAvailableForms().get(settings.getTableForm());

        return tableForm
                .getFields()
                .stream()
                .filter(fieldDefinition -> !(fieldDefinition instanceof EmbedsForm))
                .map(columnField -> new ColumnMeta(columnGeneratorManager.getGeneratorByType(columnField.getStandaloneClassName()).getColumn(columnField.getBinding()), columnField.getLabel()))
                .collect(Collectors.toList());
    }

    @Override
    protected boolean checkSettings() {
        return super.checkSettings() && settings.getCreationForm() != null && settings.getEditionForm() != null && settings.getPreviewForm() != null && settings.getTableForm() != null;
    }

    @Override
    public String getDragComponentTitle() {
        return translationService.getTranslation(CMSComponentsConstants.CRUDLayoutComponentTitle);
    }
}
