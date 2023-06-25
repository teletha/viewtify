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

import java.text.DecimalFormat;
import java.text.NumberFormat;

import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import kiss.Signal;
import viewtify.Viewtify;
import viewtify.ui.helper.TooltipHelper;

public class UIPieChart extends AbstractChart<UIPieChart, PieChart> implements TooltipHelper<UIPieChart, PieChart> {

    private NumberFormat formatter = DecimalFormat.getInstance();

    /**
     * @param view
     */
    public UIPieChart(View view) {
        super(new PieChart(), view);
    }

    /**
     * Set data.
     * 
     * @param data
     * @return
     */
    public UIPieChart data(PieChart.Data data) {
        Viewtify.inUI(() -> {
            ui.getData().add(data);

            Circle mark = new Circle(4);
            mark.setFill(Color.rgb(255, 255, 255));
            mark.setStrokeWidth(2);
            mark.getStyleClass().addAll("default-color" + ((tooltip().size() - 1)), "chart-series-line");

            Label label = new Label();
            label.setGraphic(mark);
            label.setGraphicTextGap(8);
            label.setPadding(new Insets(5, 0, 2, 0));
            label.setText(data.getName() + " - " + formatter.format(data.getPieValue()));

            tooltip().add(label);
        });
        return this;
    }

    /**
     * Set the tick label of y-axis.
     * 
     * @param decimalFormat A decimal pattern.
     * @return
     * @see "https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/text/DecimalFormat.html"
     */
    public UIPieChart dataFormat(String decimalFormat) {
        if (decimalFormat != null) {
            this.formatter = new DecimalFormat(decimalFormat);
        }
        return this;
    }

    /**
     * Set data.
     * 
     * @param data
     * @return
     */
    public UIPieChart data(Signal<PieChart.Data> data) {
        data.to(ui.getData()::add);

        return this;
    }

    public UIPieChart startAngle(double angle) {
        ui.setStartAngle(angle);
        return this;
    }
}