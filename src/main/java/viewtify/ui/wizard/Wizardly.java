/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.wizard;

import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.ColumnConstraints;
import kiss.I;
import kiss.Variable;
import stylist.Style;
import stylist.StyleDSL;
import stylist.value.Numeric;
import viewtify.ViewtyDialog.DialogView;
import viewtify.ui.UIGridView;
import viewtify.ui.UIHBox;
import viewtify.ui.UIScrollPane;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class Wizardly extends DialogView<Object> {

    private UIScrollPane main;

    private UIGridView<?> navi;

    private final List<? extends DialogView> views;

    private IntegerProperty step = new SimpleIntegerProperty();

    /**
     * @param views
     */
    public Wizardly(Class<? extends DialogView>[] views) {
        this.views = I.signal(views).map(x -> I.make(x)).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    $(navi);
                    $(main);
                });
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        ColumnConstraints constraints = new ColumnConstraints();
        constraints.setPercentWidth(100d / views.size());
        constraints.setFillWidth(true);

        for (int i = 0; i < views.size(); i++) {
            DialogView view = views.get(i);
            Navi n = new Navi(i + 1, view.title());

            navi.constrain(constraints);
            navi.ui.add(n.ui(), i, 0);
        }
    }

    private void assign(int index) {
        this.step.set(index);
    }

    interface styles extends StyleDSL {

        Numeric circle = Numeric.of(40, px);

        String strokeColor = "-fx-light-text-color";

        Style root = () -> {
            display.width.fill();
            padding.vertical(10, px);
        };

        Style navi = () -> {
            display.width.fill();
            text.align.center();
        };

        Style step = () -> {
            display.maxWidth(circle).minWidth(circle).width(circle).height(circle);
            border.radius(circle.divide(2)).color(strokeColor);
            text.align.center();
            padding.top(4, px);
            font.size(20, px).color(strokeColor).weight.bold();
            background.color("-fx-background");
        };

        Style title = () -> {
            text.align.center();
            padding.vertical(5, px);
        };

        Style line = () -> {
            display.width.fill();
            border.top.color(strokeColor);
        };

        Style none = () -> {
            display.width.fill();
        };

        Style backline = () -> {
            position.absolute().top(circle.divide(2));
        };
    }

    /**
     * 
     */
    private class Navi extends View {

        private final int step;

        private final Variable<String> title;

        private UIHBox left;

        private UIHBox right;

        /**
         * @param step
         * @param title
         * @param desc
         */
        private Navi(int step, Variable<String> title) {
            this.step = step;
            this.title = title;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ViewDSL declareUI() {
            return new ViewDSL() {
                {
                    $(vbox, styles.navi, () -> {
                        $(sbox, () -> {
                            $(hbox, styles.backline, () -> {
                                $(left, step == 1 ? styles.none : styles.line);
                                $(right, step == views.size() ? styles.none : styles.line);
                            });
                            label(step, styles.step);
                        });
                        label(title, styles.title);
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
    }
}
