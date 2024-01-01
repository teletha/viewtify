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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.control.Label;
import kiss.Signal;
import viewtify.Viewtify;
import viewtify.ui.helper.TooltipHelper;

public class UIPieChart extends AbstractChart<UIPieChart, PieChart, PieChart.Data> implements TooltipHelper<UIPieChart, PieChart> {

    /** The tooltip label formatter. */
    private NumberFormat formatter = DecimalFormat.getInstance();

    /**
     * @param view
     */
    public UIPieChart(View view) {
        super(new PieChart(), view);

        startAngle(90);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UIPieChart data(String name, Signal<Data> data) {
        data.on(Viewtify.UIThread).to(x -> {
            ui.getData().add(x);

            Label label = createTooltipLabel(root.getChildren().size());
            label.setText(x.getName() + " - " + formatter.format(x.getPieValue()));

            root.getChildren().add(label);
        }, view);
        return this;
    }

    /**
     * Set the tick label of y-axis.
     * 
     * @param decimalFormat A decimal pattern.
     * @return
     * @see "https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/text/DecimalFormat.html"
     */
    public UIPieChart label(String decimalFormat) {
        if (decimalFormat != null) {
            this.formatter = new DecimalFormat(decimalFormat);
        }
        return this;
    }

    /**
     * Configure the starting angle.
     * 
     * @param angle
     * @return
     */
    public UIPieChart startAngle(double angle) {
        ui.setStartAngle(angle);
        return this;
    }

    /**
     * The length of the line from the outside of the pie to the slice labels.
     * 
     * @param length
     * @return
     */
    public UIPieChart lineLength(double length) {
        ui.setLabelLineLength(length);
        return this;
    }

    public UIPieChart label(boolean enable) {
        ui.setLabelsVisible(enable);
        return this;
    }
}