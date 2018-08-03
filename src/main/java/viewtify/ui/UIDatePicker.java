/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.scene.control.DatePicker;

import viewtify.View;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.PreferenceHelper;
import viewtify.ui.helper.User;

/**
 * @version 2018/08/03 19:21:26
 */
public class UIDatePicker extends UserInterface<UIDatePicker, DatePicker>
        implements PreferenceHelper<UIDatePicker, LocalDate>, EditableHelper<UIDatePicker> {

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    private UIDatePicker(DatePicker ui, View view) {
        super(ui, view);

        // FUNCTIONALITY : wheel scroll will change selection.
        when(User.Scroll, e -> {
            if (e.getDeltaY() < 0) {
                value(value().plusDays(1));
            } else {
                value(value().minusDays(1));
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty editable() {
        return ui.editableProperty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<LocalDate> model() {
        return ui.valueProperty();
    }
}
