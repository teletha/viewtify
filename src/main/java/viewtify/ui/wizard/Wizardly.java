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

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.ButtonType;
import javafx.scene.layout.ColumnConstraints;

import kiss.I;
import kiss.Variable;
import stylist.Style;
import stylist.StyleDSL;
import stylist.value.Numeric;
import viewtify.Theme;
import viewtify.Viewtify;
import viewtify.ViewtyDialog.DialogView;
import viewtify.ui.UIButton;
import viewtify.ui.UIGridView;
import viewtify.ui.UIHBox;
import viewtify.ui.UILabel;
import viewtify.ui.UIScrollPane;
import viewtify.ui.UIVBox;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.anime.Anime;
import viewtify.ui.anime.SwapAnime;
import viewtify.ui.helper.User;

public class Wizardly extends DialogView<Object> {

    private UIScrollPane main;

    private UIGridView<?> navi;

    private final List<? extends DialogView> views;

    private final List<Navi> navis = new ArrayList();

    private final int max;

    private int current;

    private UIButton prev;

    private UIButton next;

    private UIButton cancel;

    private UIButton complete;

    /**
     * @param views
     */
    public Wizardly(Class<? extends DialogView>[] views) {
        this.views = I.signal(views).map(x -> I.make(x)).toList();
        this.max = this.views.size() - 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, styles.root, () -> {
                    $(navi);
                    $(main, styles.main);
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
            Navi n = new Navi(i, view.title());
            navis.add(n);

            navi.constrain(constraints);
            navi.ui.add(n.ui(), i, 0);
        }

        prev = find(ButtonType.PREVIOUS).when(User.Action.interruptively(), () -> assign(current - 1));
        next = find(ButtonType.NEXT).when(User.Action.interruptively(), () -> assign(current + 1));
        cancel = find(ButtonType.CANCEL);
        complete = find(ButtonType.FINISH);

        assign(0);
    }

    private void assign(int index) {
        int old = current;
        this.current = index;

        this.prev.show(index != 0);
        this.next.show(index != max);
        this.complete.show(index == max);

        for (Navi navi : navis) {
            navi.update(old, index);
        }

        this.main.content(views.get(index), SwapAnime.FadeOutIn);
    }

    interface styles extends StyleDSL {

        String passive = "-fx-mid-text-color";

        String active = "-fx-edit-color";

        Numeric circle = Numeric.of(28, px);

        Style root = () -> {
            padding.size(0, px);
        };

        Style main = () -> {
            padding.size(10, px);
        };

        Style navi = () -> {
            display.width.fill();
            text.align.center();
            padding.vertical(8, px);
        };

        Style step = () -> {
            display.maxWidth(circle).minWidth(circle).width(circle).height(circle);
            border.radius(circle.divide(2));
            background.color(passive);
            text.align.center();
            font.size(16, px).color("white").family("League Gothic");
        };

        Style title = () -> {
            text.align.center();
            padding.top(8, px);
            font.color(passive);
        };

        Style current = () -> {
            $.with(navi, () -> {
                display.opacity(1);
            });
        };

        Style complete = () -> {
            $.with(navi, () -> {
                display.opacity(1);
            });
        };

        Style lineRight = () -> {
            display.width.fill().maxHeight(2, px);
            background.color(passive);
            margin.top(circle.divide(2)).left(2, px);
        };

        Style lineLeft = () -> {
            display.width.fill().maxHeight(2, px);
            margin.top(circle.divide(2)).right(2, px);
            background.color(passive);
        };

        Style none = () -> {
            display.width.fill().maxHeight(0, px);
        };
    }

    /**
     * 
     */
    private class Navi extends View {

        private final int step;

        private final Variable<String> title;

        private UIVBox box;

        private UILabel num;

        private UILabel text;

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
                    $(box, styles.navi, () -> {
                        $(hbox, () -> {
                            $(left, step == 0 ? styles.none : styles.lineLeft);
                            $(num, styles.step);
                            $(right, step == max ? styles.none : styles.lineRight);
                        });
                        $(text, styles.title);
                    });
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            num.text(step + 1);
            text.text(title);
        }

        private void update(int prev, int next) {
            // current selected
            Theme theme = Viewtify.CurrentTheme.exact();
            Anime anime = Anime.define();

            if (step == next) {
                anime.backgroundColor(num, theme.edit()).opacity(num, 1);
                // if (step != 0) anime.backgroundColor(left, theme.textMid()).opacity(left, 1);
                // if (step != max) anime.backgroundColor(right, theme.textMid()).opacity(right,
                // 0.6);
                // } else if (step < next) {
                // anime.backgroundColor(num, theme.textMid()).opacity(num, 1);
                // if (step != 0) anime.backgroundColor(left, theme.textMid()).opacity(left, 1);
                // if (step != max) anime.backgroundColor(right, theme.textMid()).opacity(right, 1);
            } else {
                anime.backgroundColor(num, theme.textMid()).opacity(num, 0.75);
                // if (step != 0) anime.backgroundColor(left, theme.textMid()).opacity(left, 0.6);
                // if (step != max) anime.backgroundColor(right, theme.textMid()).opacity(right,
                // 0.6);
            }
            anime.run();
        }
    }
}
