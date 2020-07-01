/*
 * Copyright (C) 2020 viewtify Development Team
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
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.TextFieldSkin;

import kiss.I;
import viewtify.Viewtify;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.ValueHelper;

abstract class UITextBase<Self extends UITextBase<Self, V>, V> extends UserInterface<Self, TextField>
        implements ValueHelper<Self, V>, ContextMenuHelper<Self>, EditableHelper<Self> {

    /** The internal model value. */
    private final SimpleObjectProperty<V> model = new SimpleObjectProperty();

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    UITextBase(View view) {
        super(new VerifiableTextField(), view);

        // propagate value from model to ui
        Viewtify.observe(model).to(value -> {
            try {
                ui.setText(I.transform(value, String.class));
            } catch (Throwable e) {
                // ignore
            }
        });

        // propagate value from ui to model
        Viewtify.observe(ui.textProperty()).to(uiText -> {
            try {
                model.set((V) I.transform(uiText, model.get().getClass()));
            } catch (Throwable e) {
                // ignore
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Property<V> valueProperty() {
        return model;
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
        String text = ui.getText();
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
    public final Self acceptAlphabeticInput() {
        return acceptInput("[a-zA-Z]+");
    }

    /**
     * You will be able to enter only alphabet and numeric character.
     * 
     * @return
     */
    public final Self acceptAlphaNumericInput() {
        return acceptInput("[a-zA-Z0-9]+");
    }

    /**
     * You will be able to enter only numbers.
     * 
     * @return
     */
    public final Self acceptPositiveNumberInput() {
        return acceptInput("[0-9.]+");
    }

    /**
     * You will be able to enter only integral numbers.
     * 
     * @return
     */
    public final Self acceptIntegralInput() {
        return acceptInput("[\\-0-9]+");
    }

    /**
     * You will be able to enter only numbers.
     * 
     * @return
     */
    public final Self acceptDecimalInput() {
        return acceptInput("[+\\-0-9.]+");
    }

    /**
     * Specify the character types that can be entered as regular expressions.
     * 
     * @param regex
     * @return
     */
    public final Self acceptInput(String regex) {
        ((VerifiableTextField) ui).acceptInput(regex);
        return (Self) this;
    }

    /**
     * Specifies how to normalize the input characters.
     * 
     * @param form
     * @return
     */
    public final Self normalizeInput(Normalizer.Form form) {
        ((VerifiableTextField) ui).form = form;
        return (Self) this;
    }

    /**
     * Limit the number of characters that can be entered; a number less than or equal to 0 disables
     * this limit.
     * 
     * @param size
     * @return
     */
    public final Self maximumInput(int size) {
        ((VerifiableTextField) ui).max = size - 1;
        return (Self) this;
    }

    /**
     * Set whether or not to mask the input values so that they are not visible.
     * 
     * @param enable If it is true, mask it, and if it is false, unmask it.
     * @return Chainable API.
     */
    public final Self masking(boolean enable) {
        ((VerifiableTextField) ui).masking = enable;
        ui.setText(ui.getText()); // apply immediately
        return (Self) this;
    }

    /**
     * 
     */
    static class VerifiableTextField extends TextField {

        private boolean masking;

        private int max;

        private Pattern acceptable;

        private Form form;

        /**
         * 
         */
        VerifiableTextField() {
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