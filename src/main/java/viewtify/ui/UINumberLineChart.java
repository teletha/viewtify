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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.scene.shape.Circle;
import viewtify.ui.helper.TooltipHelper;
import viewtify.ui.helper.UserActionHelper;

public class UINumberLineChart<X extends Number, Y extends Number> extends UserInterface<UINumberLineChart<X, Y>, LineChart<X, Y>>
        implements UserActionHelper<UINumberLineChart<X, Y>>, TooltipHelper<UINumberLineChart<X, Y>, LineChart<X, Y>> {

    /**
     * @param view
     */
    public UINumberLineChart(View view) {
        super(new LineChart(new NumberAxis(), new NumberAxis()), view);
    }

    /**
     * Set title.
     * 
     * @param title
     * @return
     */
    public UINumberLineChart<X, Y> title(String title) {
        ui.setTitle(title);
        return this;
    }

    /**
     * Set title location.
     * 
     * @param side
     * @return
     */
    public UINumberLineChart<X, Y> title(Side side) {
        ui.setTitleSide(side);
        return this;
    }

    /**
     * Set title of axis.
     * 
     * @param title
     * @return
     */
    public UINumberLineChart<X, Y> labelX(String title) {
        Axis<X> x = ui.getXAxis();
        x.setLabel(title);
        return this;
    }

    /**
     * Set title of axis.
     * 
     * @param title
     * @return
     */
    public UINumberLineChart<X, Y> labelY(String title) {
        ui.getYAxis().setLabel(title);
        return this;
    }

    /**
     * Set data.
     * 
     * @param data
     * @return
     */
    public UINumberLineChart<X, Y> data(String name, List<XYChart.Data<X, Y>> data) {
        XYChart.Series<X, Y> series = new XYChart.Series();
        series.setName(name);
        series.getData().addAll(data);

        for (Data<X, Y> d : data) {
            d.setNode(new Circle(0));
        }

        ui.getData().add(series);
        return this;
    }

    /**
     * Set data range.
     * 
     * @return
     */
    public UINumberLineChart<X, Y> rangeAuto(boolean auto) {
        Axis<X> x = ui.getXAxis();
        if (x instanceof NumberAxis axis) {
            axis.setAutoRanging(auto);
        }
        return this;
    }

    /**
     * Set data range.
     * 
     * @return
     */
    public UINumberLineChart<X, Y> range(X start, X end) {
        Axis<X> x = ui.getXAxis();
        if (x instanceof NumberAxis axis) {
            axis.setAutoRanging(false);
            axis.setLowerBound(start.doubleValue());
            axis.setUpperBound(end.doubleValue());
        }
        return this;
    }

    /**
     * Set data.
     * 
     * @return
     */
    public UINumberLineChart<X, Y> showPopup(Function<Double, X> detector) {
        Tooltip tooltip = new Tooltip();
        AtomicReference<X> latest = new AtomicReference();

        Axis<X> axis = ui.getXAxis();
        Node back = ui.lookup(".chart-plot-background");
        back.setOnMouseMoved(e -> {
            double valueX = axis.getValueForDisplay(e.getX()).doubleValue();
            X detected = detector.apply(valueX);

            List<Y> values = new ArrayList();
            root: for (Series<X, Y> series : ui.getData()) {
                for (Data<X, Y> data : series.getData()) {
                    if (data.getXValue().equals(detected)) {
                        values.add(data.getYValue());
                        continue root;
                    }
                }
                values.add(null);
            }

            tooltip.setText("OK");
            tooltip.show(ui, e.getScreenX(), e.getScreenY());
            System.out.println(detected + "   " + values);
        });
        back.setOnMouseExited(e -> {
            tooltip.hide();
        });
        return this;
    }
}