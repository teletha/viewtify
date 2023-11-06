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
import java.util.stream.IntStream;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.layout.Priority;
import kiss.I;
import kiss.Variable;
import stylist.Style;
import stylist.StyleDSL;
import stylist.value.Numeric;
import viewtify.ViewtyDialog.DialogView;
import viewtify.ui.UIGridView;
import viewtify.ui.UIScrollPane;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class Wizardly extends DialogView<Object> {

    private UIScrollPane main;

    private UIGridView<?> navi;

    private final List<? extends DialogView> views;

    private final List<Navi> navis;

    private IntegerProperty step = new SimpleIntegerProperty();

    /**
     * @param views
     */
    public Wizardly(Class<? extends DialogView>[] views) {
        this.views = I.signal(views).map(x -> I.make(x)).toList();
        this.navis = IntStream.range(0, views.length).mapToObj(i -> new Navi(i, this.views.get(i).title())).toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, () -> {
                    $(navi, styles.navigations);
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
        main.content(views.get(0));

        for (int i = 0; i < views.size(); i++) {
            navi.constrainColumn(Priority.ALWAYS);
            navi.ui.add(navis.get(i).ui(), i, 0);
        }
    }

    private void assign(int index) {
        this.step.set(index);
    }

    interface styles extends StyleDSL {

        Numeric circle = Numeric.of(26, px);

        String strokeColor = "-fx-light-text-color";

        Style navigations = () -> {
            display.width.fill();
            background.color("-fx-accent");
        };

        Style navi = () -> {
            padding.horizontal(5, px).vertical(5, px);
            text.align.center();
        };

        Style step = () -> {
            display.width(circle).height(circle);
            border.radius(circle.divide(2)).color(strokeColor);
            text.align.center();
            font.size(12, px);
            padding.top(4, px);
            margin.right(5, px);
        };

        Style title = () -> {
            text.align.center();
            padding.vertical(5, px);
        };

        Style line = () -> {
            display.width.fill();
            border.bottom.color(strokeColor);
        };
    }

    /**
     * 
     */
    private static class Navi extends View {

        private final int step;

        private final Variable<String> title;

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
                    $(hbox, styles.navi, () -> {
                        label(step, styles.step);
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
