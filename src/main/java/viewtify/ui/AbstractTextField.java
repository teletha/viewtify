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
import javafx.scene.control.TextField;

import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.ValueHelper;

public abstract class AbstractTextField<Self extends AbstractTextField<Self, F>, F extends TextField> extends UserInterface<Self, F>
        implements ValueHelper<Self, String>, ContextMenuHelper<Self>, EditableHelper<Self> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    protected AbstractTextField(F ui, View view) {
        super(ui, view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Property<String> valueProperty() {
        return ui.textProperty();
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
    public final Self acceptNumberInput() {
        return acceptInput("[+\\-0-9.]+");
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
     * 
     */
    protected static class VerifiableTextField extends TextField {

        private int max;

        private Pattern acceptable;

        private Form form;

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
    }
}
