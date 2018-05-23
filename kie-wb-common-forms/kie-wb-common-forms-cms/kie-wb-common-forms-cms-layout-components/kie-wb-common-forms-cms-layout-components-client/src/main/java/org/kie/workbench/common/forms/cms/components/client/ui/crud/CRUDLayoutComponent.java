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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.ui.ElementWrapperWidget;
import org.jboss.errai.databinding.client.BindableProxy;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.kie.workbench.common.forms.cms.components.client.resources.i18n.CMSComponentsConstants;
import org.kie.workbench.common.forms.cms.components.client.ui.AbstractFormsCMSLayoutComponent;
import org.kie.workbench.common.forms.cms.components.client.ui.settings.SettingsDisplayer;
import org.kie.workbench.common.forms.cms.components.service.shared.RenderingContextGenerator;
import org.kie.workbench.common.forms.cms.components.shared.model.crud.CRUDSettings;
import org.kie.workbench.common.forms.cms.persistence.shared.PersistenceService;
import org.kie.workbench.common.forms.crud.client.component.CrudActionsHelper;
import org.kie.workbench.common.forms.dynamic.client.helper.MapModelBindingHelper;
import org.kie.workbench.common.forms.dynamic.client.rendering.renderers.relations.multipleSubform.ColumnGeneratorManager;
import org.kie.workbench.common.forms.dynamic.service.shared.FormRenderingContext;
import org.kie.workbench.common.forms.dynamic.service.shared.impl.MapModelRenderingContext;
import org.kie.workbench.common.forms.fields.shared.fieldTypes.relations.EmbedsForm;
import org.kie.workbench.common.forms.model.FormDefinition;
import org.uberfire.ext.widgets.table.client.ColumnMeta;

@Dependent
public class CRUDLayoutComponent extends AbstractFormsCMSLayoutComponent<CRUDSettings, CRUDSettingsReader> implements CRUDLayoutComponentView.Presenter {

    private ColumnGeneratorManager columnGeneratorManager;
    private AsyncDataProvider<BindableProxy<Map<String, Object>>> dataProvider;
    private FormRenderingContext context;
    private CRUDLayoutComponentView view;
    private MapModelBindingHelper mapModelBindingHelper;
    private List<Map<String, Object>> values;
    private List<BindableProxy<Map<String, Object>>> tableValues;

    @Inject
    public CRUDLayoutComponent(TranslationService translationService,
                               SettingsDisplayer settingsDisplayer,
                               CRUDSettingsReader reader,
                               Caller<RenderingContextGenerator> contextGenerator,
                               ColumnGeneratorManager columnGeneratorManager,
                               CRUDLayoutComponentView view,
                               MapModelBindingHelper mapModelBindingHelper,
                               PersistenceService persistenceService) {
        super(translationService,
              settingsDisplayer,
              reader,
              persistenceService,
              contextGenerator);
        this.columnGeneratorManager = columnGeneratorManager;
        this.view = view;
        this.mapModelBindingHelper = mapModelBindingHelper;

        view.init(this);
    }

    protected void refreshCrud() {
        int currentStart = view.getCRUD().getCurrentPage();
        if (currentStart < 0) {
            currentStart = 0;
        } else if (currentStart <= tableValues.size()) {
            currentStart -= 5;
        }
        dataProvider.updateRowCount(tableValues.size(),
                                    true);
        dataProvider.updateRowData(currentStart,
                                   tableValues);
        view.getCRUD().refresh();
    }

    @Override
    protected IsWidget getWidget() {
        if (checkSettings()) {
            contextGenerator.call((RemoteCallback<FormRenderingContext>) formRenderingContext -> {
                if (formRenderingContext != null) {
                    this.context = formRenderingContext;

                    values = persistenceService.query(settings.getDataObject());
                    tableValues = values.stream().map(CRUDLayoutComponent.this::convert).collect(Collectors.toList());

                    dataProvider = new AsyncDataProvider<BindableProxy<Map<String, Object>>>() {
                        @Override
                        protected void onRangeChanged(HasData<BindableProxy<Map<String, Object>>> hasData) {
                            if (values != null) {
                                updateRowCount(tableValues.size(),
                                               true);
                                updateRowData(0,
                                              tableValues);
                            } else {
                                updateRowCount(0,
                                               true);
                                updateRowData(0,
                                              new ArrayList<>());
                            }
                        }
                    };

                    view.showCRUD();

                    refreshCrud();

                }
            }).generateContext(settings);
        }
        return ElementWrapperWidget.getWidget(view.getElement());
    }

    private BindableProxy<Map<String, Object>> convert(Map<String, Object> instance) {
        MapModelRenderingContext ctx = new MapModelRenderingContext();

        ctx.setRootForm((FormDefinition) context.getAvailableForms().get(settings.getTableForm()));
        ctx.getAvailableForms().putAll(context.getAvailableForms());

        ctx.setModel(instance);

        mapModelBindingHelper.initContext(ctx);
        return (BindableProxy<Map<String, Object>>) ctx.getModel();
    }

    private List<ColumnMeta> getColumnMetas() {
        FormDefinition tableForm = (FormDefinition) context.getAvailableForms().get(settings.getTableForm());

        return tableForm
                .getFields()
                .stream()
                .filter(fieldDefinition -> !(fieldDefinition instanceof EmbedsForm))
                .map(columnField -> new ColumnMeta(columnGeneratorManager.getGeneratorByType(columnField.getStandaloneClassName()).getColumn(columnField.getBinding()),
                                                   columnField.getLabel()))
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

    @Override
    public CrudActionsHelper getActionsHelper() {
        return new CrudActionsHelper() {
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
                MapModelRenderingContext createContext = new MapModelRenderingContext();
                createContext.getAvailableForms().putAll(context.getAvailableForms());
                createContext.setRootForm((FormDefinition) context.getAvailableForms().get(settings.getCreationForm()));
                createContext.setModel(new HashMap<>());

                view.showForm(createContext,
                              () -> {
                                  persistenceService.createInstance(settings.getDataObject(),
                                                                   createContext.getModel());
                                  tableValues.add(convert(createContext.getModel()));
                                  view.showCRUD();
                                  refreshCrud();
                              },
                              () -> {
                                  view.showCRUD();
                                  refreshCrud();
                              });
            }

            @Override
            public void editInstance(int index) {
                MapModelRenderingContext createContext = new MapModelRenderingContext();
                createContext.getAvailableForms().putAll(context.getAvailableForms());
                createContext.setRootForm((FormDefinition) context.getAvailableForms().get(settings.getCreationForm()));
                createContext.setModel(persistenceService.getInstance(settings.getDataObject(), index));

                view.showForm(createContext,
                              () -> {
                                  persistenceService.saveInstance(settings.getDataObject(),
                                                                   index,
                                                                   createContext.getModel());
                                  values.set(index, createContext.getModel());
                                  tableValues.set(index, convert(createContext.getModel()));
                                  view.showCRUD();
                                  refreshCrud();
                              },
                              () -> {
                                  view.showCRUD();
                                  refreshCrud();
                              });
            }

            @Override
            public void deleteInstance(int index) {
                persistenceService.deleteInstance(settings.getDataObject(), index);
                tableValues.remove(index);
                refreshCrud();
            }
        };
    }
}
