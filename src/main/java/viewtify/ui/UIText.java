/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
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
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.StringConverter;

import org.controlsfx.control.textfield.AutoCompletionBinding.ISuggestionRequest;
import org.controlsfx.control.textfield.CustomTextField;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.StackedFontIcon;

import impl.org.controlsfx.skin.CustomTextFieldSkin;
import kiss.I;
import viewtify.Viewtify;
import viewtify.property.SmartProperty;
import viewtify.style.FormStyles;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.PlaceholderHelper;
import viewtify.ui.helper.ValueHelper;
import viewtify.util.GuardedOperation;
import viewtify.util.MonkeyPatch;

public class UIText<V> extends UserInterface<UIText<V>, CustomTextField>
        implements ValueHelper<UIText<V>, V>, ContextMenuHelper<UIText<V>>, EditableHelper<UIText<V>>, PlaceholderHelper<UIText<V>> {

    /** The input type. */
    public final Class type;

    /** The internal model value. */
    private final SmartProperty<V> model = new SmartProperty();

    /** The value sync state. */
    private final GuardedOperation updating = new GuardedOperation().ignoreError();

    /** The value renderer. */
    private Function<V, String> renderer = v -> I.transform(v, String.class);

    /**
     * Enchanced view.
     */
    public UIText(View view, Class type) {
        super(new VerifiableTextField(), view);
        this.type = type;

        // propagate value from model to ui
        Viewtify.observe(model).to(value -> {
            updating.guard(() -> {
                ui.setText(renderer.apply(value));
            });
        });

        // propagate value from ui to model
        Viewtify.observe(ui.textProperty()).to(uiText -> {
            updating.guard(() -> {
                if (uiText.isBlank()) {
                    if (type == String.class) {
                        model.set((V) "");
                    } else {
                        model.set(null);
                    }
                } else if (type == String.class) {
                    model.set((V) uiText);
                } else {
                    model.set((V) I.transform(uiText, type));
                }
            });
        });

        MonkeyPatch.fix(ui);
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
     * Specify the character types that can be entered as regular expressions.
     * 
     * @return
     */
    public final UIText<V> rejectAnyInput() {
        ((VerifiableTextField) ui).acceptInput("$^");
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
     * Set the icon prefix.
     * 
     * @param prefix
     * @return Chainable API.
     */
    public final UIText<V> prefix(Ikon prefix) {
        ui.setLeft(icon(prefix));
        return this;
    }

    /**
     * Set the uneditable text suffix.
     * 
     * @param suffix
     * @return Chainable API.
     */
    public final UIText<V> suffix(String suffix) {
        ui.setRight(new Label(suffix));
        return this;
    }

    /**
     * Set the icon suffix.
     * 
     * @param suffix
     * @return Chainable API.
     */
    public final UIText<V> suffix(Ikon suffix) {
        ui.setRight(icon(suffix));
        return this;
    }

    /**
     * Build icon pane.
     * 
     * @param ikon
     * @return
     */
    private Node icon(Ikon ikon) {
        StackedFontIcon icon = new StackedFontIcon();
        icon.setIconCodes(ikon);
        icon.setColors(Color.GRAY);
        icon.iconSizeProperty().bind(ui.fontProperty().map(Font::getSize));
        icon.setPadding(new Insets(0, 4, 0, 4));
        return icon;
    }

    /**
     * Set the value renderer.
     * 
     * @param renderer
     * @return
     */
    public final UIText<V> renderer(Function<V, String> renderer) {
        if (renderer != null) {
            this.renderer = renderer;
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
    public StringProperty placeholderProperty() {
        return ui.promptTextProperty();
    }

    /**
     * Support auto-completion.
     * 
     * @param suggests
     * @return
     */
    public UIText<V> suggest(List<V> suggests) {
        TextFields.bindAutoCompletion(ui, suggests);
        return this;
    }

    /**
     * Support auto-completion.
     * 
     * @param suggestionProvider
     * @return
     */
    public UIText<V> suggest(Callback<ISuggestionRequest, Collection<V>> suggestionProvider) {
        TextFields.bindAutoCompletion(ui, suggestionProvider);
        return this;
    }

    /**
     * Support auto-completion.
     * 
     * @param suggestionProvider
     * @return
     */
    public UIText<V> suggest(Callback<ISuggestionRequest, Collection<V>> suggestionProvider, StringConverter<V> converter) {
        TextFields.bindAutoCompletion(ui, suggestionProvider, converter);
        return this;
    }

    /**
     * Create combined text input user interface.
     * 
     * @return
     */
    public UIHBox combine(UIText... combiners) {
        UIHBox container = new UIHBox(view).style("text-input").style(FormStyles.Combined);
        ObservableList<Node> children = container.ui().getChildren();
        UIText[] inputs = I.array(new UIText[] {this}, combiners);

        for (UIText<?> input : inputs) {
            input.style("noborder").style(FormStyles.CombinedItem).whenFocus(focused -> {
                if (focused) {
                    container.style("focused");
                } else {
                    container.unstyle("focused");
                }
            });
            children.add(input.ui());
        }
        return container;
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
}