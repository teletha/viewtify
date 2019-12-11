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
import javafx.scene.control.TextField;

import kiss.I;
import viewtify.Viewtify;
import viewtify.ui.helper.ValueHelper;

public class UITextValue<E> extends UserInterface<UITextValue<E>, TextField> implements ValueHelper<UITextValue<E>, E> {

    /** The property holder. */
    private final SimpleObjectProperty<E> property = new SimpleObjectProperty();

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    private UITextValue(View view) {
        super(new TextField(), view);

        Viewtify.observe(property).to(v -> {
            ui.setText(I.transform(v, String.class));
        });

        Viewtify.observe(ui.textProperty()).to(v -> {
            property.set((E) I.transform(v, property.get().getClass()));
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<E> valueProperty() {
        return property;
    }

}
