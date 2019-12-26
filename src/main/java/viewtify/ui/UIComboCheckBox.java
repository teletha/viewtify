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

import org.controlsfx.control.CheckComboBox;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.SelectableHelper;

public class UIComboCheckBox<T> extends UserInterface<UIComboCheckBox<T>, CheckComboBox<T>>
        implements CollectableHelper<UIComboCheckBox<T>, T>, SelectableHelper<UIComboCheckBox<T>, T>,
        ContextMenuHelper<UIComboCheckBox<T>> {

    /** The item property. */
    private final Property<ObservableList<T>> itemProperty = new SimpleObjectProperty();

    /**
     * Builde {@link ComboBox}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIComboCheckBox(View view) {
        super(new CheckComboBox(), view);

        itemProperty.setValue(ui.getItems());
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
            ui.setTitle(title.toString());
            ui.setShowCheckedCount(true);
        }
        return this;
    }
}
