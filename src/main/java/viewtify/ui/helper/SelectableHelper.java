/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import javafx.scene.control.SelectionModel;

import kiss.Variable;

public interface SelectableHelper<Self extends SelectableHelper<Self, E>, E> extends PropertyAccessHelper {

    /**
     * Retrieve the {@link SelectionModel}.
     * 
     * @return
     */
    private SelectionModel<E> model() {
        return property(Type.SelectionModel).getValue();
    }

    /**
     * Get the latest selected item.
     * 
     * @return
     */
    default Variable<E> selectedItem() {
        return Variable.of(model().getSelectedItem());
    }
}
