/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.List;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.paint.Color;

import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.PreferenceHelper;

/**
 * @version 2018/09/11 1:03:38
 */
public class UIColorPicker extends UserInterface<UIColorPicker, ColorPicker>
        implements PreferenceHelper<UIColorPicker, Color>, EditableHelper<UIColorPicker> {

    /**
     * Builde {@link ComboBox}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    protected UIColorPicker(View view) {
        super(new Internal(), view);
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
    public Property<Color> model() {
        return ui.valueProperty();
    }

    /**
     * @version 2018/09/09 23:26:36
     */
    private static class Internal extends ColorPicker {

        /**
         * {@inheritDoc}
         */
        @Override
        public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
            return ExtraCSS.metadata(super.getControlCssMetaData());
        }
    }
}
