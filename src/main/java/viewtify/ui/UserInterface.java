/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import static java.util.concurrent.TimeUnit.*;

import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.util.Duration;

import org.controlsfx.control.decoration.Decorator;
import org.controlsfx.control.decoration.GraphicDecoration;
import org.controlsfx.validation.ValidationSupport;

import kiss.I;
import kiss.Manageable;
import kiss.Signal;
import kiss.Singleton;
import kiss.Storable;
import kiss.WiseRunnable;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.model.Validation;
import viewtify.ui.helper.DisableHelper;
import viewtify.ui.helper.PreferenceHelper;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;
import viewtify.util.Icon;

/**
 * @version 2018/08/28 23:42:08
 */
public class UserInterface<Self extends UserInterface, W extends Node>
        implements UserActionHelper<Self>, StyleHelper<Self, W>, DisableHelper<Self>, UserInterfaceProvider<W> {

    /** User configuration for UI. */
    private static final Preference preference = I.make(Preference.class).restore();

    /** The actual view. */
    public final W ui;

    /** The associated view. */
    protected final View view;

    /** The validation system. */
    private Validation validation;

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
     * graph. A node may be visible and yet not be shown in the rendered scene if, for instance, it
     * is off the screen or obscured by another Node. Invisible nodes never receive mouse events or
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
        return require(() -> {
            assert validator.test((Self) this);
        });
    }

    /**
     * Set the validator for this {@link UserInterface}.
     * 
     * @param validator A validator.
     * @return Chainable API.
     */
    public final Self require(WiseRunnable validator) {
        validation().require(validator);

        return (Self) this;
    }

    /**
     * Register the validation timing.
     * 
     * @param timing
     * @return
     */
    public final Self requireWhen(Signal<?>... timings) {
        I.signal(timings).skipNull().to(validation()::when);

        return (Self) this;
    }

    /**
     * Register the validation timing.
     * 
     * @param timing
     * @return
     */
    public final Self requireWhen(UserInterface... timings) {
        I.signal(timings).skipNull().map(UserInterface::validateWhen).to(validation()::when);

        return (Self) this;
    }

    /**
     * Mark as valid interface.
     * 
     * @return
     */
    public final Self decorateBy(Icon icon) {
        decorateBy(icon, null);

        return (Self) this;
    }

    /**
     * Decorate this ui by the specified icon.
     * 
     * @param icon
     * @param message
     */
    private void decorateBy(Icon icon, String message) {
        I.signal(Decorator.getDecorations(ui))
                .take(GraphicDecoration.class::isInstance)
                .take(1)
                .on(Viewtify.UIThread)
                .to(deco -> Decorator.removeDecoration(ui, deco), e -> {

                }, () -> {
                    Label label = new Label();
                    label.setGraphic(icon.image());
                    label.setAlignment(Pos.CENTER);

                    if (message != null && !message.isEmpty()) {
                        Tooltip tooltip = new Tooltip(message);
                        tooltip.setAutoFix(true);
                        tooltip.setShowDelay(Duration.ZERO);
                        tooltip.setShowDuration(Duration.INDEFINITE);
                        StyleHelper.of(tooltip).style(Styles.ValidationToolTip);
                        label.setTooltip(tooltip);
                    }

                    Decorator.addDecoration(ui, new GraphicDecoration(label, Pos.CENTER_LEFT));
                });
    }

    /**
     * Undecorate all icons.
     */
    private void undecorate() {
        I.signal(Decorator.getDecorations(ui))
                .take(GraphicDecoration.class::isInstance)
                .take(1)
                .on(Viewtify.UIThread)
                .to(deco -> Decorator.removeDecoration(ui, deco));
    }

    /**
     * Mark as invalid interface.
     * 
     * @return
     */
    public final Self invalid(String message) {
        validation().message.set(message);
        return (Self) this;
    }

    /**
     * Return the validation result of this {@link UserInterface}.
     */
    public final BooleanBinding isValid() {
        return validation().valid;
    }

    /**
     * Return the validation result of this {@link UserInterface}.
     */
    public final BooleanBinding isInvalid() {
        return validation().invalid;
    }

    /**
     * Retrieve the singleton {@link ValidationSupport} for this {@link UserInterface}.
     * 
     * @return
     */
    private synchronized Validation validation() {
        if (validation == null) {
            validation = new Validation();
            validation.when(validateWhen());
            validation.message.observe().to(message -> {
                if (message == null) {
                    undecorate();
                } else {
                    decorateBy(Icon.Error, message);
                }
            });
        }
        return validation;
    }

    /**
     * Built-in validation timing for this {@link UserInterface}.
     * 
     * @return
     */
    private Signal<?> validateWhen() {
        if (this instanceof PreferenceHelper) {
            return Viewtify.signal(((PreferenceHelper) this).model());
        } else {
            return null;
        }
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
     * @version 2018/09/03 14:36:43
     */
    private static class Styles implements StyleDSL {

        static Style ValidationToolTip = () -> {
            font.size(12, px).color("-fx-light-text-color");
            background.color($.rgba(60, 60, 60, 0.8));
            padding.vertical(8, px).horizontal(12, px);
        };
    }
}
