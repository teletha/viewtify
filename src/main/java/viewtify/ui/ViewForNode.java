/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import javafx.scene.Node;

/**
 * A simple wrapper to describe a {@link View} using Javafx's {@link Node} directly.
 */
public abstract class ViewForNode extends View {

    /**
     * {@inheritDoc}
     */
    @Override
    protected final ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(() -> declareNode());
            }
        };
    }

    /**
     * Declare UI as JavaFX node.
     * 
     * @return
     */
    protected abstract Node declareNode();
}
