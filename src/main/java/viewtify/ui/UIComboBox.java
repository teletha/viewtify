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

import java.util.function.Function;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

import kiss.Disposable;
import kiss.Signal;
import kiss.Signaling;
import viewtify.Viewtify;
import viewtify.ui.helper.Actions;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.User;
import viewtify.ui.helper.ValueHelper;
import viewtify.ui.helper.ValuedLabelHelper;

public class UIComboBox<T> extends UserInterface<UIComboBox<T>, ComboBox<T>>
        implements CollectableHelper<UIComboBox<T>, T>, ValueHelper<UIComboBox<T>, T>, ValuedLabelHelper<UIComboBox<T>, T>,
        ContextMenuHelper<UIComboBox<T>> {

    /**
     * Builde {@link ComboBox}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIComboBox(View view) {
        super(new ComboBox(), view);

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

    @Override
    public UIComboBox<T> textWhen(Function<Signal<T>, Signal<String>> text) {
        ListCell<T> cell = ui.getButtonCell();

        // clear previous cell
        if (cell instanceof DynamicLabel) {
            ((DynamicLabel) cell).disposer.dispose();
        }

        // assign new cell
        ui.setButtonCell(text == null ? null : new DynamicLabel(text));

        // API definition
        return this;
    }

    /**
     * Dynamic label.
     */
    private static class DynamicLabel<T> extends ListCell<T> {

        private final Label label = new Label();

        private final Signaling<T> signal = new Signaling();

        private final Disposable disposer;

        private DynamicLabel(Function<Signal<T>, Signal<String>> text) {
            setGraphic(label);
            disposer = text.apply(signal.expose).on(Viewtify.UIThread).to(label::setText);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void updateItem(T item, boolean empty) {
            if (empty) {
                label.setText("");
            } else {
                signal.accept(item);
            }
        }
    }
}
