/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;

import org.controlsfx.control.CheckComboBox;

import kiss.I;
import viewtify.property.SmartProperty;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.SelectableHelper;

public class UIComboCheckBox<T> extends UserInterface<UIComboCheckBox<T>, Pane>
        implements CollectableHelper<UIComboCheckBox<T>, T>, SelectableHelper<UIComboCheckBox<T>, T>,
        ContextMenuHelper<UIComboCheckBox<T>> {

    /** The item property. */
    private final Property<ObservableList<T>> itemProperty = new SmartProperty();

    /** The actual combo box. */
    private final CheckComboBox combo = new CheckComboBox();

    /**
     * Builde {@link ComboBox}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIComboCheckBox(View view) {
        super(new Pane(), view);
        ui.getChildren().add(combo);

        itemProperty.setValue(combo.getItems());
    }

    /**
     * Retrieve the property.
     * 
     * @param type A property type.
     * @return A property.
     */
    @Override
    public <X> Property<X> property(Type<X> type) {
        try {
            return (Property<X>) combo.getClass().getMethod(type.name).invoke(combo);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<T>> itemsProperty() {
        return itemProperty;
    }

    /**
     * Set title.
     * 
     * @param title
     * @return
     */
    public UIComboCheckBox<T> title(CharSequence title) {
        if (title != null) {
            combo.setTitle(title.toString());
            combo.setShowCheckedCount(true);
        }
        return this;
    }
}