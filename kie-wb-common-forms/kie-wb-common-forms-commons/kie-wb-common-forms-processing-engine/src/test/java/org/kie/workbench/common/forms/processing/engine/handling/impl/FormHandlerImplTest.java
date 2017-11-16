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

package org.kie.workbench.common.forms.processing.engine.handling.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.validation.Validation;
import javax.validation.Validator;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.jboss.errai.databinding.client.PropertyChangeUnsubscribeHandle;
import org.jboss.errai.databinding.client.api.Converter;
import org.jboss.errai.databinding.client.api.DataBinder;
import org.jboss.errai.ui.client.local.spi.TranslationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeListener;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.IsNestedModel;
import org.kie.workbench.common.forms.processing.engine.handling.impl.model.ModelProxy;
import org.kie.workbench.common.forms.processing.engine.handling.impl.test.TestFormHandler;
import org.mockito.Mock;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(GwtMockitoTestRunner.class)
public class FormHandlerImplTest extends AbstractFormEngineTest {

    @Mock
    protected DataBinder binder;

    @Mock
    protected PropertyChangeUnsubscribeHandle unsubscribeHandle;

    @Mock
    protected TranslationService translationService;

    protected FormHandlerImpl formHandler;

    protected ModelProxy proxy;

    protected boolean checkBindings = false;

    @Before
    public void init() {
        super.init();

        proxy = spy(new ModelProxy(model));

        when(binder.getModel()).thenReturn(proxy);

        when(binder.addPropertyChangeHandler(any())).thenReturn(unsubscribeHandle);
        when(binder.addPropertyChangeHandler(anyString(), any())).thenReturn(unsubscribeHandle);

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

        FormValidatorImpl formValidator = new FormValidatorImpl(new DefaultModelValidator(validator), new FieldStateValidatorImpl(translationService));

        FieldChangeHandlerManagerImpl fieldChangeHandlerManager = new FieldChangeHandlerManagerImpl();
        fieldChangeHandlerManager.setValidator(formValidator);

        formHandler = new TestFormHandler(formValidator, fieldChangeHandlerManager, binder);
    }

    @Test
    public void testHandlerDataBinderSetupWithBindings() {
        formHandler.setUp(binder, true);

        checkBindings = true;
        runSetupTest();
    }

    @Test
    public void testHandlerDataBinderSetupWithoutBindings() {
        formHandler.setUp(binder);

        checkBindings = false;
        runSetupTest();
    }

    @Test
    public void testHandlerModelSetup() {
        formHandler.setUp(model);

        checkBindings = true;
        runSetupTest();
    }

    protected void runSetupTest() {
        Converter integerConverter = mock(Converter.class);
        when(integerConverter.toWidgetValue(25)).thenReturn(25l);
        when(integerConverter.toModelValue(25l)).thenReturn(25);

        for (FormField formField : formFields) {
            if (formField.getFieldName().equals(VALUE_FIELD)) {
                formHandler.registerInput(formField, integerConverter);
            } else {
                formHandler.registerInput(formField);
            }
        }
        formHandler.addFieldChangeHandler(anonymous);
        formHandler.addFieldChangeHandler(VALUE_FIELD, value);
        formHandler.addFieldChangeHandler(USER_NAME_FIELD, userName);
        formHandler.addFieldChangeHandler(USER_LAST_NAME_FIELD, userLastName);
        formHandler.addFieldChangeHandler(USER_BIRTHDAY_FIELD, userBirthday);
        formHandler.addFieldChangeHandler(USER_MARRIED_FIELD, userMarried);
        formHandler.addFieldChangeHandler(USER_ADDRESS_FIELD, userAddress);

        if (checkBindings) {
            verify(binder, times(formFields.size())).bind(anyObject(), anyString(), any(), any());
        } else {
            verify(binder, never()).bind(anyObject(), anyString());
        }
        verify(binder, times(formFields.size())).addPropertyChangeHandler(anyString(), any());
    }

    @Test
    public void testHandlerDataBinderCorrectValidationBindings() {
        testHandlerDataBinderSetupWithBindings();
        runCorrectValidationTest(false);
    }

    @Test
    public void testHandlerDataBinderCorrectValidationWithoutBindings() {
        testHandlerDataBinderSetupWithoutBindings();
        runCorrectValidationTest(false);
    }

    @Test
    public void testHandlerModelCorrectValidation() {
        testHandlerModelSetup();
        runCorrectValidationTest(true);
    }

    protected void runCorrectValidationTest(boolean skipGetModel) {
        assertTrue(formHandler.validate());

        if (!skipGetModel) {
            int expectedTimes = 1;

            if (formHandler.handlerHelper.supportsInputBinding()) {
                expectedTimes += formFields.size();
            }
            verify(binder, times(expectedTimes)).getModel();
            // checking if property is null
            verify(proxy, times(expectedTimes - 1)).get(anyString());
        }

        assertTrue(formHandler.validate(VALUE_FIELD));
        assertTrue(formHandler.validate(USER_NAME_FIELD));
        assertTrue(formHandler.validate(USER_LAST_NAME_FIELD));
        assertTrue(formHandler.validate(USER_BIRTHDAY_FIELD));
        assertTrue(formHandler.validate(USER_MARRIED_FIELD));
        assertTrue(formHandler.validate(USER_ADDRESS_FIELD));
    }

    @Test
    public void testHandlerDataBinderWrongValidationWithBindings() {
        testHandlerDataBinderSetupWithBindings();
        runWrongValidationTest(false);
    }

    @Test
    public void testHandlerDataBinderWrongValidationWithoutBindings() {
        testHandlerDataBinderSetupWithBindings();
        runWrongValidationTest(false);
    }

    @Test
    public void testHandlerModelWrongValidationWithoutBindings() {
        testHandlerModelSetup();
        runWrongValidationTest(true);
    }

    @Test
    public void testForceModelSynchronization() {
        checkBindings = true;
        IsWidgetAndNestedModel myModel = new IsWidgetAndNestedModel() {

            Integer current = 0;
            Integer newValue = 0;
            final Collection<ValueChangeHandler<Integer>> handlers = new ArrayList<>();

            @Override
            public Integer getValue() {
                return current;
            }

            @Override
            public void setValue(Integer value) {
                newValue = value;
            }

            @Override
            public void setValue(Integer value, boolean fireEvents) {
                newValue = value;
            }

            @Override
            public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Integer> handler) {
                handlers.add(handler);
                return new HandlerRegistration() {

                    @Override
                    public void removeHandler() {
                        handlers.remove(handler);
                    }
                };
            }

            @Override
            public void fireEvent(GwtEvent<?> event) {

            }

            @Override
            public Widget asWidget() {
                return mock(Widget.class);
            }

            @Override
            public void addFieldChangeHandler(FieldChangeHandler notifyParentField) {

            }

            @Override
            public void forceModelSynchronization() {
                current = newValue;
            }
        };

        IsWidgetAndNestedModel myModelMock = spy(myModel);
        formHandler.setUp(myModelMock);
        formHandler.registerInput(generateNestedModel("myValue", "myValue", myModelMock, false));
        myModelMock.setValue(1234);
        assertEquals(new Integer(0), myModelMock.getValue());

        formHandler.forceModelSynchronization();
        verify(myModelMock).forceModelSynchronization();
        assertEquals(new Integer(1234), myModelMock.getValue());
    }

    private FormField generateNestedModel(String fieldName, String binding, IsWidgetAndNestedModel myModel, boolean validateOnChange) {
        return new FormField() {

            @Override
            public String getFieldName() {
                return fieldName;
            }

            @Override
            public String getFieldBinding() {
                return binding;
            }

            @Override
            public boolean isValidateOnChange() {
                return validateOnChange;
            }

            @Override
            public boolean isBindable() {
                return false;
            }

            @Override
            public void setVisible(boolean visible) {

            }

            @Override
            public void setReadOnly(boolean readOnly) {

            }

            @Override
            public boolean isRequired() {
                return false;
            }

            @Override
            public void clearError() {

            }

            @Override
            public void setError(String error) {

            }

            @Override
            public IsWidget getWidget() {
                return myModel;
            }

            @Override
            public Collection<FieldChangeListener> getChangeListeners() {
                return Collections.emptyList();
            }
        };
    }

    protected void runWrongValidationTest(boolean skipGetModel) {
        model.setValue(-123);
        model.getUser().setLastName("");
        model.getUser().setAddress("");

        assertFalse(formHandler.validate());

        if (!skipGetModel) {
            verify(binder, times(formFields.size() + 1)).getModel();
            // checking if property is null
            verify(proxy, times(formFields.size())).get(anyString());
        }

        assertFalse(formHandler.validate(VALUE_FIELD));
        assertTrue(formHandler.validate(USER_NAME_FIELD));
        assertFalse(formHandler.validate(USER_LAST_NAME_FIELD));
        assertTrue(formHandler.validate(USER_BIRTHDAY_FIELD));
        assertTrue(formHandler.validate(USER_MARRIED_FIELD));
        assertFalse(formHandler.validate(USER_ADDRESS_FIELD));
    }

    @After
    public void end() {
        formHandler.clear();

        if (checkBindings) {
            verify(binder).unbind();
        } else {
            verify(binder, never()).unbind();
        }
        verify(unsubscribeHandle, times(formFields.size())).unsubscribe();
    }

    interface IsWidgetAndNestedModel extends IsWidget, IsNestedModel, HasValue<Integer> {

    }
}
