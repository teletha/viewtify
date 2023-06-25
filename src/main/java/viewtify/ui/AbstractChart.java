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

import javafx.geometry.Side;
import javafx.scene.chart.Chart;

public abstract class AbstractChart<Self extends AbstractChart<Self, C>, C extends Chart> extends UserInterface<Self, C> {

    /**
     * @param view
     */
    protected AbstractChart(C chart, View view) {
        super(chart, view);
    }

    /**
     * Set title.
     * 
     * @param title
     * @return
     */
    public Self title(String title) {
        ui.setTitle(title);
        return (Self) this;
    }

    /**
     * Set title location.
     * 
     * @param side
     * @return
     */
    public Self title(Side side) {
        ui.setTitleSide(side);
        return (Self) this;
    }

    /**
     * Set legends.
     * 
     * @param enable
     * @return
     */
    public Self legend(boolean enable) {
        ui.setLegendVisible(enable);
        return (Self) this;
    }

    /**
     * Set legends location.
     * 
     * @param side
     * @return
     */
    public Self legend(Side side) {
        ui.setLegendSide(side);
        return (Self) this;
    }
}
