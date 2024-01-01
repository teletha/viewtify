/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.concurrent.TimeUnit;

import javafx.beans.property.Property;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

import kiss.I;
import viewtify.property.SmartProperty;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.DnDAssistant;
import viewtify.ui.helper.ValueHelper;

public class UIFontPicker extends UserInterface<UIFontPicker, HBox>
        implements ValueHelper<UIFontPicker, Font>, ContextMenuHelper<UIFontPicker> {

    /** The drag and drop copy. */
    private static final DnDAssistant<String> FontDnD = new DnDAssistant();

    /** The shared font-name list. */
    private static final ObservableList<String> names = FXCollections.observableArrayList();

    /** The shared font-size list. */
    private static final ObservableList<Double> sizes = FXCollections.observableArrayList();

    static {
        names.addAll(Font.getFamilies());
        sizes.addAll(8.0, 9.0, 10.0, 10.5, 11.0, 12.0, 13.0, 14.0, 15.0, 16.0, 17.0, 18.0, 19.0, 20.0, 22.0, 24.0);
    }

    /** The color data holder. */
    private final Property<Font> font = new SmartProperty<>(Font.getDefault());

    /** The name selection. */
    private final UIComboBox<String> nameSelector;

    /** The size selection. */
    private final UIComboBox<Double> sizeSelector;

    /**
     * Builde {@link ComboBox}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIFontPicker(View view) {
        super(new HBox(), view);

        nameSelector = new UIComboBox(view);
        nameSelector.ui.setMinWidth(160);
        nameSelector.items(names);
        nameSelector.select(font.getValue().getFamily());
        nameSelector.tooltip(I.translate("Specify the font name."));

        sizeSelector = new UIComboBox(view);
        sizeSelector.ui.setMinWidth(80);
        sizeSelector.items(sizes);
        sizeSelector.select(font.getValue().getSize());
        sizeSelector.tooltip(I.translate("Specify the font size."));

        ui.getChildren().addAll(nameSelector.ui, sizeSelector.ui);

        nameSelector.observing()
                .combineLatest(sizeSelector.observing())
                .map(v -> v.map(Font::font))
                .debounce(400, TimeUnit.MILLISECONDS)
                .to(this.font::setValue);

        FontDnD.source(nameSelector).target(nameSelector);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Font> valueProperty() {
        return font;
    }
}