/*
 * Copyright (C) 2020 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.toast;

import java.util.LinkedList;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableDoubleValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Screen;
import javafx.stage.Window;
import javafx.util.Duration;

import stylist.Style;
import stylist.StyleDSL;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;

public class Toast {

    /** The base transparent window. */
    private static final LinkedList<Notify> notifications = new LinkedList();

    /** The maximum size of notifications. */
    private static final int max = 5;

    private static Duration duration = Duration.millis(400);

    /**
     * Show the specified node.
     * 
     * @param node
     */
    public static synchronized void show(String message) {
        Label label = new Label(message);
        label.setWrapText(true);

        show(label);
    }

    /**
     * Show the specified node.
     * 
     * @param node
     */
    public static synchronized void show(Node node) {
        notifications.add(new Notify(node));

        if (max < notifications.size()) {
            notifications.peekFirst().disappear();
        }

        layout();
    }

    private static void layout() {
        Window window = Window.getWindows().get(0);
        Screen screen = Screen.getScreensForRectangle(window.getX(), window.getY(), window.getWidth(), window.getHeight()).get(0);
        double x = screen.getBounds().getMinX();;
        double y = 30;
        double margin = 9;

        for (Notify notify : notifications) {
            if (notify.popup.isShowing()) {
                notify.moveTo(x, y);
            } else {
                notify.popup.show(window);
                notify.appear(x, y);
            }

            y += notify.popup.getHeight() + margin;
        }
    }

    /**
     * 
     */
    private static class Notify {

        /** The base transparent window. */
        private final Popup popup = new Popup();

        private final WritableDoubleValue y = new WritableDoubleValue() {

            @Override
            public Number getValue() {
                return get();
            }

            @Override
            public double get() {
                return popup.getY();
            }

            @Override
            public void setValue(Number value) {
                set(value.doubleValue());
            }

            @Override
            public void set(double value) {
                popup.setY(value);
            }
        };

        private Notify(Node node) {
            VBox box = new VBox(node);
            StyleHelper.of(box).style(Styles.pop);

            popup.setX(0);
            popup.getContent().add(box);
            UserActionHelper.of(popup).when(User.MouseClick).to(this::disappear);
        }

        private void appear(double x, double y) {
            popup.setOpacity(0);
            popup.setX(x);
            popup.setY(y);

            Timeline appear = new Timeline(new KeyFrame(duration, new KeyValue(popup.opacityProperty(), 1)));
            appear.play();
        }

        private void moveTo(double x, double y) {
            popup.setX(x);
            Timeline move = new Timeline(new KeyFrame(duration, new KeyValue(this.y, y)));
            move.play();
        }

        private void disappear() {
            Timeline disappear = new Timeline();
            disappear.getKeyFrames().add(new KeyFrame(duration, new KeyValue(popup.opacityProperty(), 0)));
            disappear.setOnFinished(e -> {
                popup.hide();
                popup.getContent().clear();
                layout();
            });
            disappear.play();

            notifications.remove(this);
        }
    }

    private static interface Styles extends StyleDSL {
        Style pop = () -> {
            display.width(250, px).opacity(0.8);
            padding.vertical(9, px).horizontal(9, px);
            background.color("-fx-background");
            border.radius(7, px);
        };
    }
}
