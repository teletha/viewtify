/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.view;

import javafx.beans.property.Property;

import kiss.model.Model;
import viewtify.property.SmartProperty;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.ValueHelper;

public abstract class ModelPropertySheet<M> extends View implements ValueHelper<ModelPropertySheet<M>, M> {

    /** The property for model. */
    private final SmartProperty<M> property = new SmartProperty();

    /** The associated model. */
    protected final Model<M> model = (Model<M>) Model.of(Model.collectParameters(getClass(), ModelPropertySheet.class)[0]);

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<M> valueProperty() {
        return property;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                foŕ(selectVisualizer(model), (index, p) -> {

                });
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
    }

    /**
     * Select visible properties.
     * 
     * @return
     */
    protected abstract Iterable<kiss.model.Property> selectVisualizer(Model<M> model);
}