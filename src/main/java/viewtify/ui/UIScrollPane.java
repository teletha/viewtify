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

import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import viewtify.ui.anime.SwapAnime;

public class UIScrollPane extends UserInterface<UIScrollPane, ScrollPane> {

    final UIVBox box;

    /**
     * @param view
     */
    public UIScrollPane(View view) {
        super(new SmoothScrollPane(), view);

        box = new UIVBox(view);

        ui.setContent(box.ui());
        HBox.setHgrow(ui, Priority.ALWAYS);
    }

    /**
     * Set scroll bar policy.
     * 
     * @param horizontal
     * @param vertical
     * @return
     */
    public UIScrollPane policy(ScrollBarPolicy horizontal, ScrollBarPolicy vertical) {
        if (horizontal != null) {
            ui.setHbarPolicy(horizontal);
        }
        if (vertical != null) {
            ui.setVbarPolicy(vertical);
        }
        return this;
    }

    /**
     * Set box fitting policy.
     * 
     * @param width
     * @param height
     */
    public UIScrollPane fit(boolean width, boolean height) {
        ui.setFitToWidth(width);
        ui.setFitToHeight(height);

        return this;
    }

    /**
     * Set the first child content.
     * 
     * @param provider
     * @return
     */
    public UIScrollPane content(UserInterfaceProvider<Node> provider, SwapAnime... anime) {
        box.content(provider, anime);
        return this;
    }

    /**
     * Set the scroll bar width to slim (5px).
     * 
     * @return
     */
    public UIScrollPane slim() {
        style("slim");
        return this;
    }

    /**
     * Set the scroll bar width to thin (3px).
     * 
     * @return
     */
    public UIScrollPane thin() {
        style("thin");
        return this;
    }

    /**
     * {@link ScrollPane} with kinda smooth transition scrolling.
     */
    private static class SmoothScrollPane extends ScrollPane {

        private SmoothTransition transition;

        private SmoothScrollPane() {
            VBox inner = new VBox();
            inner.setOnScroll(e -> {
                transition = new SmoothTransition(transition, e.getDeltaY(), getContent().getBoundsInLocal().getWidth(), getVvalue());
                transition.play();
            });

            contentProperty().addListener((bean, oldValue, newValue) -> {
                if (newValue != inner) {
                    inner.getChildren().clear();
                    inner.getChildren().add(newValue);

                    setContent(inner);
                }
            });
        }

        /**
         * Transition with varying speed based on previously existing transitions.
         */
        private class SmoothTransition extends Transition {

            private final double modifier;

            private final double deltaY;

            private final double width;

            private final double vvalue;

            private SmoothTransition(SmoothTransition old, double deltaY, double width, double vvalue) {
                setCycleDuration(Duration.millis(200));
                setCycleCount(0);
                // if the last transition was moving in the same direction, and is still playing
                // then increment the modifer. This will boost the distance, thus looking faster
                // and seemingly consecutive.
                if (old != null && old.getStatus() == Status.RUNNING && 0 < deltaY * old.deltaY) {
                    modifier = old.modifier + 1;
                } else {
                    modifier = 1;
                }
                this.deltaY = deltaY;
                this.width = width;
                this.vvalue = vvalue;
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void play() {
                super.play();
                // Even with a linear interpolation, startup is visibly slower than the middle.
                // So skip a small bit of the animation to keep up with the speed of prior
                // animation. The value of 10 works and isn't noticeable unless you really pay
                // close attention. This works best on linear but also is decent for others.
                if (modifier > 1) {
                    jumpTo(getCycleDuration().divide(10));
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void interpolate(double frac) {
                setVvalue(Interpolator.LINEAR.interpolate(vvalue, vvalue + -deltaY * modifier / width, frac));
            }
        }
    }
}