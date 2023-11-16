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
import javafx.scene.control.DialogPane;
import javafx.scene.layout.ColumnConstraints;
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
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.anime.Anime;
import viewtify.ui.anime.SwapAnime;
import viewtify.ui.helper.User;

public class Wizardly<V> extends DialogView<V> {

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
    public Wizardly(V value, List<DialogView<V>> views) {
        this.value = value;
        this.views = views;
        this.max = views.size() - 1;
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
    public void setPane(DialogPane pane) {
        super.setPane(pane);

        for (DialogView view : views) {
            view.setPane(pane);
        }
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
            view.value = value;

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
        this.current = index;

        this.prev.show(index != 0);
        this.next.show(index != max);
        this.complete.show(index == max);

        for (Navi navi : navis) {
            navi.update(index);
        }

        this.main.content(views.get(index), SwapAnime.FadeOutIn);
    }

    interface styles extends StyleDSL {

        double initialOpacity = 0.5;

        String passive = "-fx-mid-text-color";

        String active = "-fx-edit-color";

        Numeric circle = Numeric.of(28, px);

        Style navi = () -> {
            text.align.center();
            padding.vertical(10, px);
        };

        Style stepNumber = () -> {
            display.maxWidth(circle).minWidth(circle).width(circle).height(circle).opacity(initialOpacity);
            border.radius(circle.divide(2));
            background.color(passive);
            text.align.center();
            font.size(16, px).color("white").family("League Gothic");
        };

        Style stepTitle = () -> {
            display.opacity(initialOpacity);
            text.align.center();
            padding.top(8, px);
            font.color(passive);
        };

        Style lineRight = () -> {
            display.width.fill().maxHeight(2, px).opacity(initialOpacity + 0.2);
            background.color(passive);
            margin.top(circle.divide(2)).left(2, px);
        };

        Style lineLeft = () -> {
            display.width.fill().maxHeight(2, px).opacity(initialOpacity + 0.2);
            margin.top(circle.divide(2)).right(2, px);
            background.color(passive);
        };

        Style lineNone = () -> {
            display.width.fill().maxHeight(0, px);
        };
    }

    /**
     * 
     */
    private class Navi extends View {

        private final int step;

        private final Variable<String> title;

        private UILabel stepBox;

        private UILabel titleBox;

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
                        $(hbox, () -> {
                            $(left, step == 0 ? styles.lineNone : styles.lineLeft);
                            $(stepBox, styles.stepNumber);
                            $(right, step == max ? styles.lineNone : styles.lineRight);
                        });
                        $(titleBox, styles.stepTitle);
                    });
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            stepBox.text(step + 1);
            titleBox.text(title);
        }

        /**
         * Update step navigator.
         * 
         * @param prev
         * @param next
         */
        private void update(int next) {
            // current selected
            Theme theme = Viewtify.CurrentTheme.exact();

            if (step == next) {
                Anime.define()
                        .init(() -> stepBox.text(step + 1))
                        .backgroundColor(stepBox, theme.edit())
                        .opacity(stepBox, 1)
                        .opacity(titleBox, 1)
                        .run();
            } else if (step < next) {
                Anime.define()
                        .init(() -> stepBox.text("✔"))
                        .backgroundColor(stepBox, theme.success())
                        .opacity(stepBox, 1)
                        .opacity(titleBox, styles.initialOpacity)
                        .run();
            } else {
                System.out.println("Change");
                Anime.define()
                        .init(() -> stepBox.text(step + 1))
                        .backgroundColor(stepBox, theme.textMid())
                        .opacity(stepBox, styles.initialOpacity)
                        .opacity(titleBox, styles.initialOpacity)
                        .run();
            }
        }
    }
}
