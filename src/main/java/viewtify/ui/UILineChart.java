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

import java.util.List;

import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class UILineChart<X, Y> extends UserInterface<UILineChart<X, Y>, LineChart<X, Y>> {

    /**
     * @param view
     */
    public UILineChart(View view) {
        super(new LineChart(new NumberAxis(), new NumberAxis()), view);
    }

    /**
     * Set title.
     * 
     * @param title
     * @return
     */
    public UILineChart<X, Y> title(String title) {
        ui.setTitle(title);
        return this;
    }

    /**
     * Set title location.
     * 
     * @param side
     * @return
     */
    public UILineChart<X, Y> title(Side side) {
        ui.setTitleSide(side);
        return this;
    }

    /**
     * Set title of axis.
     * 
     * @param title
     * @return
     */
    public UILineChart<X, Y> titleX(String title) {
        ui.getXAxis().setLabel(title);
        return this;
    }

    /**
     * Set title of axis.
     * 
     * @param title
     * @return
     */
    public UILineChart<X, Y> titleY(String title) {
        ui.getYAxis().setLabel(title);
        return this;
    }

    /**
     * Set data.
     * 
     * @param data
     * @return
     */
    public UILineChart<X, Y> data(List<XYChart.Data<X, Y>> data) {
        XYChart.Series<X, Y> series = new XYChart.Series();
        series.getData().addAll(data);
        ui.getData().add(series);
        return this;
    }
}