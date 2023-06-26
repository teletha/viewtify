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

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;
import kiss.I;
import kiss.Signal;
import viewtify.Viewtify;
import viewtify.ui.helper.TooltipHelper;

public class UILineChart<X extends Number, Y extends Number> extends AbstractChart<UILineChart<X, Y>, LineChart<X, Y>, LineChart.Data<X, Y>>
        implements TooltipHelper<UILineChart<X, Y>, LineChart<X, Y>> {

    /** The tooltip title. */
    private final Label title = new Label();

    /** The cache of hovered line points. */
    private final List<Node> hovers = new ArrayList();

    /** The inner chart area. */
    private final Node back;

    /** The label formmater for axis-x. */
    private Formatter formatX = new Formatter();

    /** The label formmater for axis-y. */
    private Formatter formatY = formatX;

    /**
     * @param view
     */
    public UILineChart(View view) {
        super(new LineChart(new NumberAxis(), new NumberAxis()), view);

        root.getChildren().add(title);
        back = ui.lookup(".chart-plot-background");
    }

    /**
     * Set title of axis.
     * 
     * @param title
     * @return
     */
    public UILineChart<X, Y> titleX(String title) {
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
    public UILineChart<X, Y> titleY(String title) {
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
    public UILineChart<X, Y> labelX(String decimalFormat) {
        if (decimalFormat != null && ui.getXAxis() instanceof NumberAxis axis) {
            axis.setTickLabelFormatter(formatX = new Formatter(decimalFormat));
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
    public UILineChart<X, Y> labelY(String decimalFormat) {
        if (decimalFormat != null && ui.getYAxis() instanceof NumberAxis axis) {
            axis.setTickLabelFormatter(formatY = new Formatter(decimalFormat));
        }
        return this;
    }

    /**
     * Set data.
     * 
     * @param data
     * @return
     */
    @Override
    public UILineChart<X, Y> data(String name, Signal<Data<X, Y>> data) {
        XYChart.Series<X, Y> series = new XYChart.Series();
        series.setName(name);

        Viewtify.inUI(() -> {
            // build data source
            ui.getData().add(series);

            // build tooltip UI
            Label label = createTooltipLabel(root.getChildren().size() - 1);
            root.getChildren().add(label);
        });

        data.on(Viewtify.UIThread).to(x -> {
            // update data source
            series.getData().add(x);

            // initialize tooltip label
            Node node = x.getNode();
            node.setVisible(false);
            node.setManaged(false);
        }, view);
        return this;
    }

    /**
     * Set data range.
     * 
     * @return
     */
    public UILineChart<X, Y> range(X start, X end) {
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
    public UILineChart<X, Y> rangeAuto(boolean auto) {
        Axis<X> x = ui.getXAxis();
        if (x instanceof NumberAxis axis) {
            axis.setAutoRanging(auto);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean showTooltip(MouseEvent e) {
        Bounds outer = ui.localToScreen(ui.getBoundsInLocal());
        Bounds inner = back.localToScreen(back.getBoundsInLocal());

        if (!inner.contains(e.getScreenX(), e.getScreenY())) {
            return false;
        }
        hideLinePoint();

        NumberAxis axisX = (NumberAxis) ui.getXAxis();
        double valueX = axisX.getValueForDisplay(e.getX() - (inner.getMinX() - outer.getMinX())).doubleValue();

        // update tooltip title
        title.setText(formatX.toString(valueX));

        root: for (int i = 0; i < ui.getData().size(); i++) {
            Series<X, Y> series = ui.getData().get(i);
            ObservableList<Data<X, Y>> items = series.getData();
            if (items.isEmpty()) {
                continue;
            }

            Label label = (Label) root.getChildren().get(i + 1);
            Predicate<X> matcher;
            Class type = items.get(0).getXValue().getClass();
            if (type == Integer.class || type == Long.class || type == BigInteger.class) {
                matcher = v -> v.longValue() == Math.round(valueX);
            } else {
                matcher = v -> v.doubleValue() == valueX;
            }

            for (Data<X, Y> data : series.getData()) {
                if (matcher.test(data.getXValue())) {
                    // update tooltip label
                    label.setText(series.getName() + "   " + formatY.toString(data.getYValue()));

                    // show line point
                    Node node = data.getNode();
                    node.setVisible(true);
                    hovers.add(node);
                    continue root;
                }
            }

            // clear tooltip label
            label.setText(series.getName() + "   " + formatY.toString(0));
        }

        return super.showTooltip(e);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void hideTooltip(MouseEvent e) {
        super.hideTooltip(e);
        hideLinePoint();
    }

    /**
     * Hide all line points.
     */
    private void hideLinePoint() {
        for (Node hover : hovers) {
            hover.setVisible(false);
        }
        hovers.clear();
    }

    /**
     * {@link DecimalFormat} based converter.
     */
    private static class Formatter extends StringConverter<Number> {

        private final DecimalFormat formatter;

        private Formatter() {
            this.formatter = new DecimalFormat();
        }

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
}