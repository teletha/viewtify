/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.lang.reflect.Method;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Objects;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.TextFormatter.Change;

import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;

import impl.org.controlsfx.skin.CustomTextFieldSkin;
import kiss.I;
import viewtify.Viewtify;
import viewtify.property.SmartProperty;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.PlaceholderHelper;
import viewtify.ui.helper.ValueHelper;
import viewtify.util.GuardedOperation;

public class UIText<V> extends UserInterface<UIText<V>, CustomTextField>
        implements ValueHelper<UIText<V>, V>, ContextMenuHelper<UIText<V>>, EditableHelper<UIText<V>>, PlaceholderHelper<UIText<V>> {

    /** The input type. */
    public final Class type;

    /** The internal model value. */
    private final SmartProperty<V> model = new SmartProperty();

    /** The value sync state. */
    private final GuardedOperation updating = new GuardedOperation().ignoreError();

    /** The inline formatter. */
    private VerifiableFormatter formatter;

    /**
     * Enchanced view.
     */
    public UIText(View view, Class type) {
        super(new VerifiableTextField(), view);
        this.type = type;

        // propagate value from model to ui
        Viewtify.observe(model).to(value -> {
            updating.guard(() -> {
                ui.setText(I.transform(value, String.class));
            });
        });

        // propagate value from ui to model
        Viewtify.observe(ui.textProperty()).to(uiText -> {
            updating.guard(() -> {
                if (uiText.isBlank()) {
                    model.set(null);
                } else if (type == String.class) {
                    model.set((V) uiText);
                } else {
                    model.set((V) I.transform(uiText, type));
                }
            });
        });
    }

    /**
     * Return the current length of inputed text.
     */
    public final int length() {
        return ui.getLength();
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
     * Check whether this field's text is selected or not.
     * 
     * @return
     */
    public final boolean isTextSelected() {
        return ui.getSelection().getLength() != 0;
    }

    /**
     * Check whether this field's text is selected or not.
     * 
     * @return
     */
    public final boolean isNotTextSelected() {
        return !isTextSelected();
    }

    /**
     * You will be able to enter only alphabet.
     * 
     * @return
     */
    public final UIText<V> acceptAlphabeticInput() {
        return acceptInput("[a-zA-Z]+");
    }

    /**
     * You will be able to enter only alphabet and numeric character.
     * 
     * @return
     */
    public final UIText<V> acceptAlphaNumericInput() {
        return acceptInput("[a-zA-Z0-9]+");
    }

    /**
     * You will be able to enter only integral numbers.
     * 
     * @return
     */
    public final UIText<V> acceptIntegralInput() {
        return acceptInput("[\\-0-9]+");
    }

    /**
     * You will be able to enter only positive integral numbers.
     * 
     * @return
     */
    public final UIText<V> acceptPositiveIntegralInput() {
        return acceptInput("[0-9]+");
    }

    /**
     * You will be able to enter only numbers.
     * 
     * @return
     */
    public final UIText<V> acceptDecimalInput() {
        return acceptInput("[+\\-0-9.]+");
    }

    /**
     * You will be able to enter only numbers.
     * 
     * @return
     */
    public final UIText<V> acceptPositiveDecimalInput() {
        return acceptInput("[0-9.]+");
    }

    /**
     * Specify the character types that can be entered as regular expressions.
     * 
     * @param regex
     * @return
     */
    public final UIText<V> acceptInput(String regex) {
        ((VerifiableTextField) ui).acceptInput(regex);
        return this;
    }

    /**
     * Specifies how to normalize the input characters.
     * 
     * @param form
     * @return
     */
    public final UIText<V> normalizeInput(Normalizer.Form form) {
        ((VerifiableTextField) ui).form = form;
        return this;
    }

    /**
     * Get the current limited size of characters. A number less than or equal to 0 disables this
     * limit.
     * 
     * @return
     */
    public final int maximumInput() {
        return ((VerifiableTextField) ui).max;
    }

    /**
     * Limit the number of characters that can be entered; a number less than or equal to 0 disables
     * this limit.
     * 
     * @param size
     * @return
     */
    public final UIText<V> maximumInput(int size) {
        ((VerifiableTextField) ui).max = size;
        return this;
    }

    /**
     * Set whether or not to mask the input values so that they are not visible.
     * 
     * @param enable If it is true, mask it, and if it is false, unmask it.
     * @return Chainable API.
     */
    public final UIText<V> masking(boolean enable) {
        ((VerifiableTextField) ui).masking = enable;
        ui.setText(ui.getText()); // apply immediately
        return this;
    }

    /**
     * Set the uneditable text prefix.
     * 
     * @param prefix
     * @return Chainable API.
     */
    public final UIText<V> prefix(String prefix) {
        ui.setLeft(new Label(prefix));
        return this;
    }

    /**
     * Set the uneditable text suffix.
     * 
     * @param suffix
     * @return Chainable API.
     */
    public final UIText<V> suffix(String suffix) {
        // formatter().suffix = Objects.requireNonNull(suffix, "");
        ui.setRight(new Label(suffix));
        return this;
    }

    /**
     * Set the text formatter.
     * 
     * @param formatter Your formatter.
     * @return Chainable API.
     */
    public final UIText<V> formatter(TextFormatter<V> formatter) {
        if (formatter != null) {
            ui.setTextFormatter(formatter);
        }
        return this;
    }

    /**
     * Show a clear button inside the {@link TextField} (on the right hand side of it) when text is
     * entered by the user.
     */
    public final UIText<V> clearable() {
        try {
            Method method = TextFields.class.getDeclaredMethod("setupClearButtonField", TextField.class, ObjectProperty.class);
            method.setAccessible(true);
            method.invoke(null, ui, ui.rightProperty());
        } catch (Exception e) {
            throw I.quiet(e);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIText<V> placeholder(Object text) {
        ui.setPromptText(Objects.toString(text));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIText<V> placeholder(Property text) {
        ui.promptTextProperty().bind(text);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIText<V> placeholder(UserInterfaceProvider text) {
        throw new UnsupportedOperationException("Text field doesn't support the placeholder by node.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIText<V> placeholder(Node node) {
        throw new UnsupportedOperationException("Text field doesn't support the placeholder by node.");
    }

    /**
     * 
     */
    static class VerifiableTextField extends CustomTextField {

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

            int additional = text.length() - (end - start);
            if (0 < max && max < getLength() + additional) {
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
        private class VerifiableTextFieldSkin extends CustomTextFieldSkin {

            private VerifiableTextFieldSkin(TextField control) {
                super(control);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected String maskText(String text) {
                return masking ? "\u25cf".repeat(text.length()) : text;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public ObjectProperty<Node> leftProperty() {
                return VerifiableTextField.this.leftProperty();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public ObjectProperty<Node> rightProperty() {
                return VerifiableTextField.this.rightProperty();
            }
        }
    }

    /**
     * 
     */
    private class VerifiableFormatter implements UnaryOperator<Change> {

        private String prefix = "";

        private String suffix = "";

        private TextFormatter<V> formatter = new TextFormatter<>(this);

        /**
         * {@inheritDoc}
         */
        @Override
        public Change apply(Change c) {
            try {
                String oldText = c.getControlText();
                String newText = c.getControlNewText();
                int newLength = newText.length();

                if (c.isContentChange()) {
                    if (!newText.startsWith(prefix)) {
                        c.setText(prefix + c.getText());
                    }

                    if (!newText.endsWith(suffix)) {
                        c.setText(c.getText() + suffix);
                    }

                    if (c.isDeleted()) {
                        if (oldText.length() - suffix.length() < c.getRangeEnd()) {
                            return null;
                        }
                    }
                } else {

                }

                int caretPrev = c.getControlCaretPosition();
                int caretNext = c.getCaretPosition();
                if (caretNext < caretPrev) {
                    if (prefix != null) {
                        if (caretNext < prefix.length()) {
                            c.setCaretPosition(caretPrev);
                        }
                    }
                }

                if (caretPrev < caretNext) {
                    if (suffix != null) {
                        if (newLength < caretNext + suffix.length()) {
                            c.setCaretPosition(caretPrev);
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return c;
        }
    }
}