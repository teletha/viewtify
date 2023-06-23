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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;

import kiss.I;
import viewtify.ui.helper.TooltipHelper;
import viewtify.ui.helper.UserActionHelper;

public class UINumberLineChart<X extends Number, Y extends Number> extends UserInterface<UINumberLineChart<X, Y>, LineChart<X, Y>>
        implements UserActionHelper<UINumberLineChart<X, Y>>, TooltipHelper<UINumberLineChart<X, Y>, LineChart<X, Y>> {

    private final VBox root = new VBox();

    private final Label title = new Label();

    private boolean popuped;

    /**
     * @param view
     */
    public UINumberLineChart(View view) {
        super(new LineChart(new NumberAxis(), new NumberAxis()), view);

        root.getChildren().add(title);
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
    public UINumberLineChart<X, Y> titleX(String title) {
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
    public UINumberLineChart<X, Y> titleY(String title) {
        ui.getYAxis().setLabel(title);
        return this;
    }

    /**
     * Set the tick label of x-axis.
     * 
     * @param decimalFormat A decimal pattern.
     * @return
     * @see "https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/text/DecimalFormat.html"
     */
    public UINumberLineChart<X, Y> labelX(String decimalFormat) {
        if (decimalFormat != null && ui.getXAxis() instanceof NumberAxis axis) {
            axis.setTickLabelFormatter(new Formatter(decimalFormat));
        }
        return this;
    }

    /**
     * Set the tick label of y-axis.
     * 
     * @param decimalFormat A decimal pattern.
     * @return
     * @see "https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/text/DecimalFormat.html"
     */
    public UINumberLineChart<X, Y> labelY(String decimalFormat) {
        if (decimalFormat != null && ui.getYAxis() instanceof NumberAxis axis) {
            axis.setTickLabelFormatter(new Formatter(decimalFormat));
        }
        return this;
    }

    /**
     * {@link DecimalFormat} based converter.
     */
    private static class Formatter extends StringConverter<Number> {

        private final DecimalFormat formatter;

        private Formatter(String pattern) {
            this.formatter = new DecimalFormat(pattern);
        }

        @Override
        public String toString(Number object) {
            return formatter.format(object);
        }

        @Override
        public Number fromString(String string) {
            try {
                return formatter.parse(string);
            } catch (ParseException e) {
                throw I.quiet(e);
            }
        }
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

        ui.getData().add(series);

        for (Data<X, Y> d : data) {
            d.getNode().setVisible(false);
        }

        Circle mark = new Circle(4);
        mark.setFill(Color.rgb(255, 255, 255));
        mark.setStrokeWidth(2);
        mark.getStyleClass().addAll("default-color" + ((root.getChildren().size() - 1)), "chart-series-line");

        Label label = new Label();
        label.setGraphic(mark);
        label.setGraphicTextGap(8);
        label.setPadding(new Insets(5, 0, 2, 0));

        root.getChildren().add(label);
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
     * When the mouse is operated on a chart, the corresponding data is displayed in a tooltip.
     * 
     * @param detector Function to convert from X-axis position information to appropriate data
     *            information.
     * @return Chainable API
     */
    public UINumberLineChart<X, Y> popup(Function<Double, X> detector) {
        Tooltip tooltip = new Tooltip();
        tooltip.setGraphic(root);

        NumberAxis axisX = (NumberAxis) ui.getXAxis();
        List<Node> hovers = new ArrayList();
        Node back = ui.lookup(".chart-plot-background");

        ui.setOnMouseExited(e -> {
            popuped = false;
            tooltip.hide();
            ui.getData().stream().flatMap(x -> x.getData().stream()).forEach(x -> x.getNode().setVisible(false));
        });
        ui.setOnMouseMoved(e -> {
            Bounds outer = ui.localToScreen(ui.getBoundsInLocal());
            Bounds inner = back.localToScreen(back.getBoundsInLocal());

            // remove old hover effect
            for (Node hover : hovers) {
                hover.setVisible(false);
            }
            hovers.clear();

            if (inner.contains(e.getScreenX(), e.getScreenY())) {
                double valueX = axisX.getValueForDisplay(e.getX() - (inner.getMinX() - outer.getMinX())).doubleValue();
                X detected = detector.apply(valueX);

                title.setText(axisX.getTickLabelFormatter().toString(detected));

                root: for (int i = 0; i < ui.getData().size(); i++) {
                    Series<X, Y> series = ui.getData().get(i);
                    for (Data<X, Y> data : series.getData()) {
                        if (data.getXValue().equals(detected)) {
                            Label label = (Label) root.getChildren().get(i + 1);
                            label.setText(series.getName() + "   " + data.getYValue());

                            Node node = data.getNode();
                            node.setVisible(true);
                            hovers.add(node);
                            continue root;
                        }
                    }
                }

                double x = outer.getMinX() + e.getX() + 20;
                double y = outer.getMinY() + e.getY() - 15;

                if (popuped) {
                    tooltip.setX(x);
                    tooltip.setY(y);
                } else {
                    popuped = true;
                    tooltip.show(ui, x, y);
                }
            } else {
                if (popuped) {
                    popuped = false;
                    tooltip.hide();
                }
            }
        });
        return this;
    }
}