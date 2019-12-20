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
import javafx.scene.control.TextField;

import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.ValueHelper;

public abstract class AbstractTextField<Self extends AbstractTextField<Self, F>, F extends TextField> extends UserInterface<Self, F>
        implements ValueHelper<Self, String>, ContextMenuHelper<Self> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    protected AbstractTextField(F ui, View view) {
        super(ui, view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Property<String> valueProperty() {
        return ui.textProperty();
    }

    /**
     * Check whether this field is empty or not.
     * 
     * @return
     */
    public final boolean isEmpty() {
        String text = value();
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
}
