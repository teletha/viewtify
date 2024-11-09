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

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ComboBoxListViewSkin;

import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.PlaceholderHelper;
import viewtify.ui.helper.SelectableHelper;

public abstract class AbstractComboBox<T, Self extends AbstractComboBox<T, Self, W>, W extends Node> extends UserInterface<Self, W>
        implements CollectableHelper<Self, T>, SelectableHelper<Self, T>, ContextMenuHelper<Self>, PlaceholderHelper<Self> {

    /**
     * Delegation.
     * 
     * @param ui
     * @param view
     */
    protected AbstractComboBox(W ui, View view) {
        super(ui, view);
    }

    /**
     * Get the base {@link ComboBox}.
     * 
     * @return
     */
    protected abstract ComboBox<T> comboBox();

    /**
     * Get the popup control.
     * 
     * @return
     */
    public final ListView listView() {
        return (ListView) ((ComboBoxListViewSkin) comboBox().getSkin()).getPopupContent();
    }

    /**
     * Show popup.
     * 
     * @return
     */
    public final Self show() {
        comboBox().show();
        return (Self) this;
    }

    /**
     * Hide popup.
     * 
     * @return
     */
    public final Self hide() {
        comboBox().hide();
        return (Self) this;
    }

    /**
     * Toggle popup.
     * 
     * @return
     */
    public final Self toggle() {
        return comboBox().isShowing() ? hide() : show();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringProperty placeholderProperty() {
        return comboBox().promptTextProperty();
    }
}