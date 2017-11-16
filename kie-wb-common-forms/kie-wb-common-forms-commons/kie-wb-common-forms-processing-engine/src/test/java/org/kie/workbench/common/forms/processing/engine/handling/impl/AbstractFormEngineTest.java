/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import junit.framework.TestCase;
import org.kie.workbench.common.forms.processing.engine.handling.FieldChangeHandler;
import org.kie.workbench.common.forms.processing.engine.handling.FormField;
import org.kie.workbench.common.forms.processing.engine.handling.impl.model.Model;
import org.kie.workbench.common.forms.processing.engine.handling.impl.model.User;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.verification.VerificationMode;

import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public abstract class AbstractFormEngineTest extends TestCase {

    public static final String VALUE_FIELD = "value";
    public static final String USER_NAME_FIELD = "user_name";
    public static final String USER_LAST_NAME_FIELD = "user_lastName";
    public static final String USER_BIRTHDAY_FIELD = "user_birthday";
    public static final String USER_MARRIED_FIELD = "user_married";
    public static final String USER_ADDRESS_FIELD = "user_address";

    public static final String[] ALL_FIELDS = {
            VALUE_FIELD,
            USER_NAME_FIELD,
            USER_LAST_NAME_FIELD,
            USER_BIRTHDAY_FIELD,
            USER_MARRIED_FIELD,
            USER_ADDRESS_FIELD
    };

    protected int executionCounts;

    @Mock
    protected FieldChangeHandler anonymous;

    @Mock
    protected FieldChangeHandler value;

    @Mock
    protected FieldChangeHandler userName;

    @Mock
    protected FieldChangeHandler userLastName;

    @Mock
    protected FieldChangeHandler userBirthday;

    @Mock
    protected FieldChangeHandler userMarried;

    @Mock
    protected FieldChangeHandler userAddress;

    protected Model model;

    protected List<FormField> formFields = new ArrayList<>();

    protected void init() {

        User user = new User();

        user.setName("John");
        user.setLastName("Snow");
        user.setBirtDay(new Date());
        user.setMarried(false);
        user.setAddress("Winterfell");

        model = new Model();
        model.setUser(user);
        model.setValue(25);

        formFields.add(generateFormField(VALUE_FIELD,
                                                         "value",
                                                         true));
        formFields.add(generateFormField(USER_NAME_FIELD,
                                                         "user.name",
                                                         true));
        formFields.add(generateFormField(USER_LAST_NAME_FIELD,
                                                         "user.lastName",
                                                         true));
        formFields.add(generateFormField(USER_BIRTHDAY_FIELD,
                                                         "user.birthday",
                                                         true));
        formFields.add(generateFormField(USER_MARRIED_FIELD,
                                                         "user.married",
                                                         true));
        formFields.add(generateFormField(USER_ADDRESS_FIELD,
                                                         "user.address",
                                                         true));

        executionCounts = 0;

        Answer answer = new Answer() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                executionCounts++;
                return null;
            }
        };
        doAnswer(answer).when(anonymous).onFieldChange(anyString(),
                                                       anyObject());
        doAnswer(answer).when(value).onFieldChange(anyString(),
                                                   anyObject());
        doAnswer(answer).when(userName).onFieldChange(anyString(),
                                                      anyObject());
        doAnswer(answer).when(userLastName).onFieldChange(anyString(),
                                                          anyObject());
        doAnswer(answer).when(userBirthday).onFieldChange(anyString(),
                                                          anyObject());
        doAnswer(answer).when(userMarried).onFieldChange(anyString(),
                                                         anyObject());
        doAnswer(answer).when(userAddress).onFieldChange(anyString(),
                                                         anyObject());
    }

    public FormField generateFormField(String fieldName,
                                       String binding,
                                       boolean validateOnChange) {

        Widget widget = mock(Widget.class);

        IsWidget isWidget = mock(IsWidget.class,
                                 withSettings().extraInterfaces(HasValue.class));

        when(isWidget.asWidget()).thenReturn(widget);

        FormField field = mock(FormField.class);

        when(field.getFieldName()).thenReturn(fieldName);
        when(field.getFieldBinding()).thenReturn(binding);
        when(field.isValidateOnChange()).thenReturn(validateOnChange);
        when(field.isBindable()).thenReturn(true);
        when(field.getWidget()).thenReturn(isWidget);
        when(field.isContentValid()).thenReturn(true);

        return field;
    }

    protected void checkClearedFields(String... cleared) {
        Arrays.stream(cleared).forEach(fieldName -> {
            FormField field = findFormField(fieldName);
            assertNotNull(field);
            verify(field,
                   atLeastOnce()).clearError();
        });
    }

    protected void checkWrongFields(String... wrongFields) {
        /*
        Checking that the validation given fields has been successfull. The conditions to check:
        - Group Verification: VALIDATION_ERROR_CLASSNAME should be added to at least one time
            (it may be more if there are more validation errors)
        - HelpBlock Verification: helpBlock's innerHTML should be modified at least two times (one to clean it up
            and at least one more to add the validation error message )
        */
        doValidationFailure(atLeast(1),
                            wrongFields);
    }

    protected void checkValidFields(String... validFields) {

        /*
        Checking that the validation given fields has been successfull. The conditions to check:
        - Group Verification: group shouldn't contain the VALIDATION_ERROR_CLASSNAME
        - HelpBlock Verification: helpBlock's innerHTML should be modified only one time (to clean it up)
        */
        doValidationFailure(never(),
                            validFields);
    }

    protected void doValidationFailure(VerificationMode setErrorTimes,
                                       String... fields) {

        Arrays.stream(fields).forEach(fieldName -> {
            FormField field = findFormField(fieldName);
            assertNotNull(field);
            verify(field,
                   atLeastOnce()).clearError();
            verify(field,
                   setErrorTimes).setError(anyString());
        });
    }

    protected FormField findFormField(String fieldName) {
        FormField field = formFields.stream().filter(formField -> fieldName.equals(formField.getFieldName())).findFirst().get();

        if (field == null) {
            field = formFields.stream().filter(formField -> fieldName.equals(formField.getFieldBinding())).findFirst().get();
        }

        return field;
    }
}
