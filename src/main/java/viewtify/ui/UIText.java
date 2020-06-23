/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;

import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.ValueHelper;

public class UIText extends UserInterface<UIText, TextField>
        implements ValueHelper<UIText, String>, ContextMenuHelper<UIText>, EditableHelper<UIText> {

    private final StringProperty value = new SimpleStringProperty();

    private boolean valueChanging = false;

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    public UIText(View view) {
        super(new VerifiableTextField(), view);

        // from model value to UI text value
        value.addListener((o, prev, next) -> {
            if (valueChanging == false) {
                try {
                    valueChanging = true;
                    ui.textProperty().set(next);
                } finally {
                    valueChanging = false;
                }
            }
        });

        // from UI text value to model value
        ui.textProperty().addListener((o, prev, next) -> {
            if (valueChanging == false) {
                try {
                    valueChanging = true;
                    value.set(next);
                } finally {
                    valueChanging = false;
                }
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Property<String> valueProperty() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty edit() {
        return ui.editableProperty();
    }

    /**
     * Check whether this field is empty or not.
     * 
     * @return
     */
    public final boolean isEmpty() {
        String text = value();
        return text == null || text.isEmpty();
    }

    /**
     * Check whether this field is empty or not.
     * 
     * @return
     */
    public final boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * You will be able to enter only alphabet.
     * 
     * @return
     */
    public final UIText acceptAlphabeticInput() {
        return acceptInput("[a-zA-Z]+");
    }

    /**
     * You will be able to enter only alphabet and numeric character.
     * 
     * @return
     */
    public final UIText acceptAlphaNumericInput() {
        return acceptInput("[a-zA-Z0-9]+");
    }

    /**
     * You will be able to enter only numbers.
     * 
     * @return
     */
    public final UIText acceptNumberInput() {
        return acceptInput("[+\\-0-9.]+");
    }

    /**
     * You will be able to enter only numbers.
     * 
     * @return
     */
    public final UIText acceptPositiveNumberInput() {
        return acceptInput("[0-9.]+");
    }

    /**
     * Specify the character types that can be entered as regular expressions.
     * 
     * @param regex
     * @return
     */
    public final UIText acceptInput(String regex) {
        ((VerifiableTextField) ui).acceptInput(regex);
        return this;
    }

    /**
     * Specifies how to normalize the input characters.
     * 
     * @param form
     * @return
     */
    public final UIText normalizeInput(Normalizer.Form form) {
        ((VerifiableTextField) ui).form = form;
        return this;
    }

    /**
     * Limit the number of characters that can be entered; a number less than or equal to 0 disables
     * this limit.
     * 
     * @param size
     * @return
     */
    public final UIText maximumInput(int size) {
        ((VerifiableTextField) ui).max = size - 1;
        return this;
    }

    /**
     * Set whether or not to mask the input values so that they are not visible.
     * 
     * @param enable If it is true, mask it, and if it is false, unmask it.
     * @return Chainable API.
     */
    public UIText masking(boolean enable) {
        ((VerifiableTextField) ui).masking = enable;
        ui.setText(ui.getText()); // apply immediately
        return this;
    }

    /**
     * 
     */
    protected static class VerifiableTextField extends TextField {

        private boolean masking;

        private int max;

        private Pattern acceptable;

        private Form form;

        /**
         * 
         */
        public VerifiableTextField() {
            setSkin(new VerifiableTextFieldSkin(this));
        }

        /**
         * Specify the character types that can be entered as regular expressions.
         * 
         * @param regex
         * @return
         */
        private void acceptInput(String regex) {
            acceptable = regex == null || regex.isBlank() ? null : Pattern.compile(regex);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void replaceText(int start, int end, String text) {
            if (text.length() == 0) {
                super.replaceText(start, end, text);
                return;
            }

            String next = new StringBuilder(getText()).replace(start, end, text).toString();

            if (0 < max && max < getLength()) {
                return;
            }

            if (form != null) {
                text = Normalizer.normalize(text, form);
            }

            if (acceptable == null || acceptable.matcher(text).matches()) {
                super.replaceText(start, end, text);
            }
        }

        /**
         * 
         */
        private class VerifiableTextFieldSkin extends TextFieldSkin {

            private VerifiableTextFieldSkin(TextField control) {
                super(control);
            }

            @Override
            protected String maskText(String text) {
                return masking ? "\u25cf".repeat(text.length()) : text;
            }
        }
    }
}
