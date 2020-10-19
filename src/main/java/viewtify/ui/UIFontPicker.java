/*
 * Copyright (C) 2020 viewtify Development Team
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
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import viewtify.Viewtify;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.EditableHelper;
import viewtify.ui.helper.ValueHelper;

public class UIFontPicker extends UserInterface<UIFontPicker, HBox>
        implements ValueHelper<UIFontPicker, Font>, EditableHelper<UIFontPicker>, ContextMenuHelper<UIFontPicker> {

    /** The shared font-name list. */
    private static final ObservableList<String> fonts = FXCollections.observableArrayList();

    private static final ObservableList<Double> sizes = FXCollections.observableArrayList();

    static {
        fonts.addAll(Font.getFamilies());
        sizes.addAll(7.0, 8.0, 9.0, 10.0, 10.5, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 22.0, 24.0, 26.0, 28.0, 30.0);
    }

    /** The color data holder. */
    private final Property<Font> font = new SimpleObjectProperty<>(Font.getDefault());

    /** The editing mode. */
    private final BooleanProperty edit = new SimpleBooleanProperty(false);

    /** The name selection. */
    private final ComboBox<String> names = new ComboBox();

    /** The size selection. */
    private final ComboBox<Double> size = new ComboBox();

    /**
     * Builde {@link ComboBox}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIFontPicker(View view) {
        super(new HBox(), view);

        names.setMinWidth(160);
        names.setItems(fonts);
        names.getSelectionModel().select(font.getValue().getFamily());

        size.setMinWidth(70);
        size.setItems(sizes);

        ui.getChildren().addAll(names, size);

        Viewtify.observing(font).to(now -> {

        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BooleanProperty edit() {
        return edit;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Font> valueProperty() {
        return font;
    }
}