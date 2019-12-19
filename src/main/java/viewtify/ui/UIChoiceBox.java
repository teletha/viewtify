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

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

import viewtify.ui.helper.Actions;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.User;
import viewtify.ui.helper.ValueHelper;

public class UIChoiceBox<T> extends UserInterface<UIChoiceBox<T>, ChoiceBox<T>>
        implements CollectableHelper<UIChoiceBox<T>, T>, ValueHelper<UIChoiceBox<T>, T>, ContextMenuHelper<UIChoiceBox<T>> {

    /**
     * Builde {@link ChoiceBox}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UIChoiceBox(View view) {
        super(new ChoiceBox(), view);

        // FUNCTIONALITY : wheel scroll will change selection.
        when(User.Scroll, Actions.traverse(ui.getSelectionModel()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<T> valueProperty() {
        return ui.valueProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<T>> itemsProperty() {
        return ui.itemsProperty();
    }
}
