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
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import kiss.I;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.ui.helper.User;
import viewtify.ui.helper.ValueHelper;
import viewtify.util.Range;

public class UIRange<V extends Comparable<V>> extends UserInterface<UIRange, HBox> implements ValueHelper<UIRange<V>, Range<V>> {

    /** The start value editor. */
    private final UIText startEditor;

    /** The end value editor. */
    private final UIText endEditor;

    /** The associated value. */
    private final SimpleObjectProperty<Range<V>> property = new SimpleObjectProperty();

    /**
     * Build {@link Label}.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    UIRange(View view) {
        super(new HBox(), view);

        startEditor = view.make(UIText.class).style($.form).when(User.Scroll, e -> {
            if (e.getDeltaY() > 0) {
                property.set(property.get().nextStart());
            } else {
                property.set(property.get().previousStart());
            }
        });
        endEditor = view.make(UIText.class).style($.form).when(User.Scroll, e -> {
            if (e.getDeltaY() > 0) {
                property.set(property.get().nextEnd());
            } else {
                property.set(property.get().previousEnd());
            }
        });

        ui.getChildren().addAll(startEditor.ui, endEditor.ui);

        Viewtify.observe(property).to(v -> {
            startEditor.value(I.transform(v.start, String.class));
            endEditor.value(I.transform(v.end, String.class));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<Range<V>> valueProperty() {
        return property;
    }

    /**
     * 
     */
    private interface $ extends StyleDSL {
        Style form = () -> {
            display.width(75, px);
        };
    }
}
