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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import stylist.value.Color;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.ValueHelper;
import viewtify.util.DelegatingProperty;
import viewtify.util.FXUtils;

public class UIColorPicker extends UserInterface<UIColorPicker, ColorPicker>
        implements ValueHelper<UIColorPicker, Color>, EditableHelper<UIColorPicker>, ContextMenuHelper<UIColorPicker> {

    private final DelegatingProperty<javafx.scene.paint.Color, Color> color = new DelegatingProperty<>(ui
            .valueProperty(), FXUtils::color, FXUtils::color);

    /**
     * Builde {@link ComboBox}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIColorPicker(View view) {
        super(new ColorPicker(), view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty edit() {
        return ui.editableProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Color> valueProperty() {
        return color;
    }
}
