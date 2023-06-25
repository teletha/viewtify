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
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.StringConverter;

import kiss.I;
import kiss.Signal;
import viewtify.Viewtify;
import viewtify.ui.helper.TooltipHelper;

public class UINumberLineChart<X extends Number, Y extends Number> extends AbstractChart<UINumberLineChart<X, Y>, LineChart<X, Y>>
        implements TooltipHelper<UINumberLineChart<X, Y>, LineChart<X, Y>> {

    private Label title;

    /** The cache of hovered line points. */
    private List<Node> hovers;

    private Function<Double, X> detector;

    /**
     * @param view
     */
    public UINumberLineChart(View view) {
        super(new LineChart(new NumberAxis(), new NumberAxis()), view);
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
    public UINumberLineChart<X, Y> data(String name, Signal<XYChart.Data<X, Y>> data) {

        XYChart.Series<X, Y> series = new XYChart.Series();
        series.setName(name);
        Viewtify.inUI(() -> ui.getData().add(series));

        Circle mark = new Circle(4);
        mark.setFill(Color.rgb(255, 255, 255));
        mark.setStrokeWidth(2);
        mark.getStyleClass().addAll("default-color" + ((tooltip().size() - 1)), "chart-series-line");

        Label label = new Label();
        label.setGraphic(mark);
        label.setGraphicTextGap(8);
        label.setPadding(new Insets(5, 0, 2, 0));

        tooltip().add(label);

        data.on(Viewtify.UIThread).to(x -> {
            series.getData().add(x);

            x.getNode().setVisible(false);
        });
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
        popup();

        this.detector = detector;
        this.hovers = new ArrayList();
        this.title = new Label();
        tooltip().add(title);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean showTooltip(MouseEvent e) {
        Node back = ui.lookup(".chart-plot-background");
        Bounds outer = ui.localToScreen(ui.getBoundsInLocal());
        Bounds inner = back.localToScreen(back.getBoundsInLocal());

        if (!inner.contains(e.getScreenX(), e.getScreenY())) {
            return false;
        }
        hideLinePoint();

        NumberAxis axisX = (NumberAxis) ui.getXAxis();
        double valueX = axisX.getValueForDisplay(e.getX() - (inner.getMinX() - outer.getMinX())).doubleValue();
        X detected = detector.apply(valueX);

        StringConverter<Number> formatter = axisX.getTickLabelFormatter();
        title.setText(formatter == null ? detected.toString() : formatter.toString(detected));

        root: for (int i = 0; i < ui.getData().size(); i++) {
            Series<X, Y> series = ui.getData().get(i);
            for (Data<X, Y> data : series.getData()) {
                if (data.getXValue().equals(detected)) {
                    Label label = (Label) tooltip().get(i + 1);
                    label.setText(series.getName() + "   " + data.getYValue());

                    Node node = data.getNode();
                    node.setVisible(true);
                    hovers.add(node);
                    continue root;
                }
            }
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

    private void hideLinePoint() {
        for (Node hover : hovers) {
            hover.setVisible(false);
        }
        hovers.clear();
    }
}