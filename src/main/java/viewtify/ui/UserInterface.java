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

import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import kiss.I;
import kiss.Manageable;
import kiss.Singleton;
import kiss.Storable;
import viewtify.View;
import viewtify.Viewtify;
import viewtify.ui.helper.DisableHelper;
import viewtify.ui.helper.EventHelper;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.User;

/**
 * @version 2018/07/31 16:01:23
 */
public class UserInterface<Self extends UserInterface, W extends Node>
        implements EventHelper<Self>, StyleHelper<Self, W>, DisableHelper<Self> {

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
     * Validation helper.
     * 
     * @param condition
     * @return
     */
    public Self require(Predicate<Self> condition) {
        if (ui instanceof Control) {
            if (validations == null) {
                validations = new ValidationSupport();
            }

            validations.registerValidator((Control) ui, false, Validator.createPredicateValidator(v -> {
                return condition.test((Self) this);
            }, ""));
        }
        return (Self) this;
    }

    /**
     * Validation helper.
     */
    public ReadOnlyBooleanProperty isInvalid() {
        if (validations == null) {
            validations = new ValidationSupport();
        }
        return validations.invalidProperty();
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
}
