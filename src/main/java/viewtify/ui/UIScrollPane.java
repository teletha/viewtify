/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class UIScrollPane extends UserInterface<UIScrollPane, ScrollPane> {

    /**
     * @param ui
     * @param view
     */
    public UIScrollPane(View view) {
        super(new SmoothishScrollPane(), view);
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
     * {@link ScrollPane} with kinda smooth transition scrolling.
     */
    private static class SmoothishScrollPane extends ScrollPane {

        private SmoothishScrollPane() {
            // set content in a wrapper
            VBox wrapper = new VBox();
            wrapper.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));

            // add scroll handling to wrapper
            wrapper.setOnScroll(new EventHandler<ScrollEvent>() {
                private SmoothishTransition transition;

                @Override
                public void handle(ScrollEvent event) {
                    double deltaY = 2 * event.getDeltaY();
                    double width = getContent().getBoundsInLocal().getWidth();
                    double vvalue = getVvalue();
                    Interpolator interp = Interpolator.LINEAR;
                    transition = new SmoothishTransition(transition, deltaY) {
                        @Override
                        protected void interpolate(double frac) {
                            double x = interp.interpolate(vvalue, vvalue + -deltaY * getMod() / width, frac);
                            setVvalue(x);
                        }
                    };
                    transition.play();
                }
            });

            contentProperty().addListener((b, o, n) -> {
                if (n != wrapper) {
                    wrapper.getChildren().clear();
                    wrapper.getChildren().add(n);

                    setContent(wrapper);
                }
            });
        }

        /**
         * @param t Transition to check.
         * @return {@code true} if transition is playing.
         */
        private static boolean playing(Transition t) {
            return t.getStatus() == Status.RUNNING;
        }

        /**
         * @param d1 Value 1
         * @param d2 Value 2.
         * @return {@code true} if values signes are matching.
         */
        private static boolean sameSign(double d1, double d2) {
            return (d1 > 0 && d2 > 0) || (d1 < 0 && d2 < 0);
        }

        /**
         * Transition with varying speed based on previously existing transitions.
         * 
         * @author Matt
         */
        abstract class SmoothishTransition extends Transition {
            private final double mod;

            private final double delta;

            public SmoothishTransition(SmoothishTransition old, double delta) {
                setCycleDuration(Duration.millis(200));
                setCycleCount(0);
                // if the last transition was moving inthe same direction, and is still playing
                // then increment the modifer. This will boost the distance, thus looking faster
                // and seemingly consecutive.
                if (old != null && sameSign(delta, old.delta) && playing(old)) {
                    mod = old.getMod() + 1;
                } else {
                    mod = 1;
                }
                this.delta = delta;
            }

            public double getMod() {
                return mod;
            }

            @Override
            public void play() {
                super.play();
                // Even with a linear interpolation, startup is visibly slower than the middle.
                // So skip a small bit of the animation to keep up with the speed of prior
                // animation. The value of 10 works and isn't noticeable unless you really pay
                // close attention. This works best on linear but also is decent for others.
                if (getMod() > 1) {
                    jumpTo(getCycleDuration().divide(10));
                }
            }
        }
    }
}