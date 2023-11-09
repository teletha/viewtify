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
import viewtify.ViewtyDialog.DialogView;
import viewtify.ui.UIButton;
import viewtify.ui.UIGridView;
import viewtify.ui.UIHBox;
import viewtify.ui.UILabel;
import viewtify.ui.UIScrollPane;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
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
        this.max = this.views.size();
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
            Navi n = new Navi(i + 1, view.title());
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
        this.next.show(index != max - 1);
        this.complete.show(index == max - 1);

        for (int i = 0; i < navis.size(); i++) {
            Navi n = navis.get(i);
            if (i < index) {
                if (i != 0) n.left.style(styles.complete).unstyle(styles.current);;
                n.right.style(styles.complete).unstyle(styles.current);
                n.num.style(styles.complete).unstyle(styles.current);;
            } else if (i == index) {
                if (i != 0) n.left.style(styles.complete, styles.current);
                n.right.unstyle(styles.complete).style(styles.current);
                n.num.unstyle(styles.complete).style(styles.current);
            } else {
                n.left.unstyle(styles.complete, styles.current);
                n.right.unstyle(styles.complete, styles.current);
                n.num.unstyle(styles.complete, styles.current);
            }
        }

        this.main.content(views.get(index), SwapAnime.FadeOutIn);
    }

    interface styles extends StyleDSL {

        Numeric circle = Numeric.of(28, px);

        String strokeColor = "-fx-mid-text-color";

        String completeColor = "-fx-success";

        String unselectedColor = "-fx-focus-color";

        String selectedColor = "-fx-default-button";

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

        Style current = () -> {
        };

        Style complete = () -> {
        };

        Style step = () -> {
            display.maxWidth(circle).minWidth(circle).width(circle).height(circle);
            border.radius(circle.divide(2)).color(strokeColor).width(1, px);
            text.align.center();
            font.size(16, px).color(strokeColor).family("League Gothic");
            background.color(selectedColor);

            $.with(complete, () -> {
                border.color(strokeColor).width(1, px);
                background.color(completeColor);
            });

            $.with(current, () -> {
                border.color(strokeColor).width(2, px);
            });
        };

        Style title = () -> {
            text.align.center();
            padding.top(8, px);
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

        private UILabel num;

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
                                $(right, step == max ? styles.none : styles.line);
                            });
                            $(num, styles.step);
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
            num.text(step);
        }
    }
}
