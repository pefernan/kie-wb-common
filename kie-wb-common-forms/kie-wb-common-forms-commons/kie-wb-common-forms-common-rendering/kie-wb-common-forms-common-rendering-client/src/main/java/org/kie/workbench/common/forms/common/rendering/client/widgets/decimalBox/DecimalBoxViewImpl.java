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

package org.kie.workbench.common.forms.common.rendering.client.widgets.decimalBox;

import javax.inject.Inject;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import org.jboss.errai.common.client.dom.TextInput;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.SinkNative;
import org.jboss.errai.ui.shared.api.annotations.Templated;

@Templated
public class DecimalBoxViewImpl extends Composite implements DecimalBoxView {

    private DecimalBox presenter;

    @Inject
    @DataField
    private TextInput input;

    @Override
    public void setPresenter(DecimalBox presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setValue(Double value) {
        input.setValue(value == null ? "" : value.toString());
    }

    @Override
    public void setEnabled(boolean enabled) {
        input.setReadOnly(!enabled);
    }

    @Override
    public Double getValue() {
        // TODO check value before notifying the presenter
        return Double.valueOf(input.getValue());
    }

    public void updateValue(Event event) {
        Window.alert("Value updated " + input.getValue());
        presenter.notifyValueChange(getValue());
    }

    public void onKeyDown(Event event) {
        int key = event.getKeyCode();
        boolean isShiftPressed = event.getShiftKey();
        boolean isNumPadDigit = (key >= KeyCodes.KEY_NUM_ZERO && key <= KeyCodes.KEY_NUM_NINE);
        boolean isKeyboardDigit = (key >= KeyCodes.KEY_ZERO && key <= KeyCodes.KEY_NINE);
        boolean isBackspace = (key == KeyCodes.KEY_BACKSPACE);
        boolean isDecimalSeparator = (key == KeyCodes.KEY_NUM_PERIOD || key == 190);
/*
        if (!isShiftPressed && (  ))
        if ((key >= KeyCodes.KEY_ZERO && key <= KeyCodes.KEY_NINE) ||
                (key >= KeyCodes.KEY_NUM_ZERO && key <= KeyCodes.KEY_NUM_NINE) ||
                key == KeyCodes.KEY_BACKSPACE
                ) {
            return;
        }
        if (key == KeyCodes.KEY_NUM_PERIOD || key == 190) {
            if (!input.getValue().contains(".")) {
                return;
            }
        }
        event.stopPropagation();
        event.preventDefault();
        */
        //Window.alert("keyDown " + event.getKeyCode());
    }

    @SinkNative(Event.ONKEYDOWN | Event.ONCHANGE)
    @EventHandler("input")
    public void onEvent(Event event) {
        switch (event.getTypeInt()) {
            case Event.ONCHANGE:
                updateValue(event);
                break;
            case Event.ONKEYDOWN:
                onKeyDown(event);
                break;
            default:
                break;
        }
    }
}
