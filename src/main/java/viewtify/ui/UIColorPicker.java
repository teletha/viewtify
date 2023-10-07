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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.DnDAssistant;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.ValueHelper;

public class UIColorPicker extends UserInterface<UIColorPicker, ColorPicker>
        implements ValueHelper<UIColorPicker, Color>, EditableHelper<UIColorPicker>, ContextMenuHelper<UIColorPicker> {

    /** The color data transfer. */
    private static final DnDAssistant<Color> ColorDnD = new DnDAssistant();

    /**
     * Builde {@link ComboBox}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIColorPicker(View view) {
        super(new ColorPicker(), view);

        ColorDnD.source(this).target(this);
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
        return ui.valueProperty();
    }
}