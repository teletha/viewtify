/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import static java.util.concurrent.TimeUnit.*;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.util.Duration;

import org.controlsfx.control.decoration.Decoration;
import org.controlsfx.control.decoration.GraphicDecoration;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationMessage;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.decoration.AbstractValidationDecoration;
import org.controlsfx.validation.decoration.GraphicValidationDecoration;

import kiss.I;
import kiss.Manageable;
import kiss.Singleton;
import kiss.Storable;
import viewtify.View;
import viewtify.Viewtify;
import viewtify.ui.helper.DisableHelper;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;

/**
 * @version 2018/07/31 16:01:23
 */
public class UserInterface<Self extends UserInterface, W extends Node>
        implements UserActionHelper<Self>, StyleHelper<Self, W>, DisableHelper<Self> {

    static {
        I.load(Message.class, false);
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    /** User configuration for UI. */
    private static final Preference preference = I.make(Preference.class).restore();

    /** The actual view. */
    public final W ui;

    /** The associated view. */
    private final View view;

    /** The validatiors. */
    private ValidationSupport validations;

    /** Do restore oonly once. */
    private boolean restored = false;

    /**
     * @param ui
     */
    public UserInterface(W ui, View view) {
        this.ui = ui;
        this.view = view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public W ui() {
        return ui;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Boolean> disable() {
        return ui.disableProperty();
    }

    /**
     * Register keyborad shortcut.
     * 
     * @param key
     * @param action
     * @return
     */
    public Self keybind(String key, Runnable action) {
        KeyCombination stroke = KeyCodeCombination.keyCombination(key);
        when(User.KeyPress).take(stroke::match).to(action::run);
        return (Self) this;
    }

    /**
     * Select parent {@link Node}.
     * 
     * @return
     */
    public UserInterface parent() {
        return new UserInterface(ui.getParent(), view);
    }

    /**
     * Specifies whether this {@code Node} and any subnodes should be rendered as part of the scene
     * graph. A node may be visible and yet not be shown in the rendered scene if, for instance, it is
     * off the screen or obscured by another Node. Invisible nodes never receive mouse events or
     * keyboard focus and never maintain keyboard focus when they become invisible.
     *
     * @defaultValue true
     */
    public Self visible(boolean visible) {
        ui.setVisible(visible);
        return (Self) this;
    }

    /**
     * Set the validator for this {@link UserInterface}.
     * 
     * @param validator A validator.
     * @return Chainable API.
     */
    public final Self require(Predicate<Self> validator) {
        if (validator != null) {
            require(self -> {
                assert validator.test(self);
            });
        }
        return (Self) this;
    }

    /**
     * Set the validator for this {@link UserInterface}.
     * 
     * @param validator A validator.
     * @return Chainable API.
     */
    public final Self require(Consumer<Self> validator) {
        if (ui instanceof Control) {
            ValidationSupport validation = validationSupport();
            validation.registerValidator((Control) ui, false, new Validator(validator, this));
            validation.initInitialDecoration();
        }
        return (Self) this;
    }

    /**
     * Return the validation result of this {@link UserInterface}.
     */
    public final ReadOnlyBooleanProperty isInvalid() {
        return validationSupport().invalidProperty();
    }

    /**
     * Retrieve the singleton {@link ValidationSupport} for this {@link UserInterface}.
     * 
     * @return
     */
    private synchronized ValidationSupport validationSupport() {
        if (validations == null) {
            validations = new ValidationSupport();
            validations.setValidationDecorator(new ValidationDecoration());
        }
        return validations;
    }

    /**
     * Restore UI related settings.
     * 
     * @param property
     * @param value
     */
    protected final <T> void restore(Property<T> property, T value) {
        restore(property, property::setValue, value);
    }

    /**
     * Restore UI related settings.
     * 
     * @param property
     * @param writer
     * @param value
     */
    protected final <T> void restore(ReadOnlyProperty<T> property, Consumer<T> writer, T value) {
        if (value == null || restored) {
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }
        restored = true;

        String id = view.id() + " 🔄 " + ui.getId();

        // restore
        if (ui.getId() != null) {
            String stored = preference.get(id);

            if (stored != null) {
                try {
                    value = (T) I.transform(stored, value.getClass());
                } catch (Throwable e) {
                    // ignore
                }
            }
        }
        writer.accept(value);

        // prepare for store
        Viewtify.signal(property).debounce(1000, MILLISECONDS).to(change -> {
            preference.put(id, I.transform(change, String.class));
            preference.store();
        });
    }

    /**
     * Create new menu item.
     * 
     * @return
     */
    public static final UIMenuItem menuItem() {
        return new UIMenuItem(new MenuItem());
    }

    /**
     * Create new menu item.
     * 
     * @return
     */
    public static final UIContextMenu contextMenu(Consumer<UIContextMenu> builder) {
        UIContextMenu menu = new UIContextMenu(new ContextMenu());
        builder.accept(menu);

        return menu;
    }

    /**
     * @version 2017/11/30 14:03:31
     */
    @SuppressWarnings("serial")
    @Manageable(lifestyle = Singleton.class)
    private static class Preference extends TreeMap<String, String> implements Storable<Preference> {
    }

    /**
     * @version 2018/08/03 15:16:51
     */
    private static class Validator<T> implements org.controlsfx.validation.Validator<T> {

        /** The actual validator. */
        private final Consumer<T> validator;

        /**
         * Hide constructor.
         */
        private Validator(Consumer<T> validator, T value) {
            this.validator = v -> validator.accept(value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ValidationResult apply(Control ui, T value) {
            try {
                validator.accept(value);
                return null;
            } catch (Throwable e) {
                String message = e.getLocalizedMessage();

                if (message == null || message.isEmpty()) {
                    message = I.i18n(Message::invalidValue);
                }
                return ValidationResult.fromError(ui, message);
            }
        }
    }

    /**
     * @version 2018/08/03 16:45:41
     */
    private static class ValidationDecoration extends AbstractValidationDecoration {
        private static final Image ERROR_IMAGE = new Image(GraphicValidationDecoration.class
                .getResource("/impl/org/controlsfx/control/validation/decoration-error.png") //$NON-NLS-1$
                .toExternalForm());

        private static final Image WARNING_IMAGE = new Image(GraphicValidationDecoration.class
                .getResource("/impl/org/controlsfx/control/validation/decoration-warning.png") //$NON-NLS-1$
                .toExternalForm());

        private static final Image REQUIRED_IMAGE = new Image(GraphicValidationDecoration.class
                .getResource("/impl/org/controlsfx/control/validation/required-indicator.png") //$NON-NLS-1$
                .toExternalForm());

        private static final String SHADOW_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 10, 0, 0, 0);"; //$NON-NLS-1$

        private static final String POPUP_SHADOW_EFFECT = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 5, 0, 0, 5);"; //$NON-NLS-1$

        private static final String TOOLTIP_COMMON_EFFECTS = "-fx-font-weight: bold; -fx-padding: 5; -fx-border-width:1;"; //$NON-NLS-1$

        private static final String ERROR_TOOLTIP_EFFECT = POPUP_SHADOW_EFFECT + TOOLTIP_COMMON_EFFECTS + "-fx-background-color: FBEFEF; -fx-text-fill: cc0033; -fx-border-color:cc0033;"; //$NON-NLS-1$

        private static final String WARNING_TOOLTIP_EFFECT = POPUP_SHADOW_EFFECT + TOOLTIP_COMMON_EFFECTS + "-fx-background-color: FFFFCC; -fx-text-fill: CC9900; -fx-border-color: CC9900;"; //$NON-NLS-1$

        /**
         * {@inheritDoc}
         */
        @Override
        protected Collection<Decoration> createValidationDecorations(ValidationMessage message) {
            Node image = new ImageView(Severity.ERROR == message.getSeverity() ? ERROR_IMAGE : WARNING_IMAGE);
            image.setStyle(SHADOW_EFFECT);

            Tooltip tooltip = new Tooltip(message.getText());
            tooltip.setAutoFix(true);
            tooltip.setShowDelay(Duration.ZERO);
            tooltip.setShowDuration(Duration.INDEFINITE);
            tooltip.setStyle(Severity.ERROR == message.getSeverity() ? ERROR_TOOLTIP_EFFECT : WARNING_TOOLTIP_EFFECT);

            Label label = new Label();
            label.setGraphic(image);
            label.setTooltip(tooltip);
            label.setAlignment(Pos.CENTER);

            return List.of(new GraphicDecoration(label, Pos.CENTER_LEFT));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Collection<Decoration> createRequiredDecorations(Control target) {
            return List.of(new GraphicDecoration(new ImageView(REQUIRED_IMAGE), Pos.TOP_LEFT, REQUIRED_IMAGE.getWidth() / 2, REQUIRED_IMAGE
                    .getHeight() / 2));
        }
    }
}
