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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

import javafx.beans.value.WritableDoubleValue;
import javafx.geometry.Rectangle2D;
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
import viewtify.util.Corner;
import viewtify.util.FXUtils;

public class Toast {

    /** The margin for each notifications. */
    private static final int MARGIN = 12;

    /** The base transparent window. */
    private static final LinkedList<Notification> notifications = new LinkedList();

    /** The maximum size of notifications. */
    private static int max = 7;

    /** The animation time. */
    private static Duration duration = Duration.millis(333);

    /** The notification area. */
    private static Corner corner = Corner.TopRight;

    /** The opacity of notification area. */
    private static final double opacity = 0.85;

    /** The width of notification area. */
    private static final int width = 250;

    /**
     * Configure the notification area. (default : TopRight)
     * 
     * @param area A notification area.
     */
    public static void setArea(Corner area) {
        corner = Objects.requireNonNullElse(area, Corner.TopRight);
    }

    /**
     * Configure the maximum viewable size of notifications. (default : 7)
     * 
     * @param size A positive number.
     */
    public static void setMax(int size) {
        max = Math.max(1, size);
    }

    /**
     * Configure the width of notification area. (default : 0.85)
     * 
     * @param duration A positive number.
     */
    public static void setOpacity(double opacity) {
        opacity = Math.min(1, Math.max(0.1, opacity));
    }

    /**
     * Configure the animation time. (default : 333)
     * 
     * @param duration A positive number (mills).
     */
    public static void setTime(int durationMills) {
        duration = Duration.millis(Math.max(1, durationMills));
    }

    /**
     * Configure the width of notification area. (default : 250)
     * 
     * @param duration A positive number.
     */
    public static void setWidth(int width) {
        width = Math.max(10, width);
    }

    /**
     * Show the specified node.
     * 
     * @param node
     */
    public static void show(String message) {
        Label label = new Label(message);
        label.setWrapText(true);

        show(label);
    }

    /**
     * Show the specified node.
     * 
     * @param node
     */
    public static void show(Node node) {
        add(new Notification(node));
    }

    /**
     * Add notification.
     * 
     * @param notification
     */
    private static void add(Notification notification) {
        notifications.add(notification);
        if (max < notifications.size()) {
            remove(notifications.peekFirst());
        } else {
            layoutNotifications();
        }
    }

    /**
     * Remove notification.
     * 
     * @param notification
     */
    private static void remove(Notification notification) {
        // UI effect
        FXUtils.animate(duration, notification.popup.opacityProperty(), 0, () -> {
            notification.popup.hide();
            notification.popup.getContent().clear();
        });

        // model management
        notifications.remove(notification);

        // draw UI
        layoutNotifications();
    }

    /**
     * Layout all notifications.
     */
    private static void layoutNotifications() {
        Window w = Window.getWindows().get(0);
        // correct 10 pixel for maximized window
        Screen screen = Screen.getScreensForRectangle(w.getX() + 10, w.getY(), w.getWidth(), w.getHeight()).get(0);
        Rectangle2D rect = screen.getBounds();
        boolean isTopSide = corner.isTopSide();
        double x = corner.isLeftSide() ? rect.getMinX() + MARGIN : rect.getMaxX() - width - MARGIN;
        double y = isTopSide ? 30 : rect.getMaxY() - 30;

        Iterator<Notification> iterator = isTopSide ? notifications.descendingIterator() : notifications.iterator();
        while (iterator.hasNext()) {
            Toast.Notification notify = iterator.next();

            if (notify.popup.isShowing()) {
                if (!isTopSide) y -= notify.popup.getHeight() + MARGIN;
                notify.popup.setX(x);
                FXUtils.animate(duration, notify.y, y);
            } else {
                notify.popup.setOpacity(0);
                notify.popup.show(w);
                if (!isTopSide) y -= notify.popup.getHeight() + MARGIN;
                notify.popup.setX(x);
                notify.popup.setY(y);

                FXUtils.animate(duration, notify.popup.opacityProperty(), 1);
            }

            if (isTopSide) y += notify.popup.getHeight() + MARGIN;
        }
    }

    /**
     * 
     */
    private static class Notification {

        /** The base transparent window. */
        private final Popup popup = new Popup();

        /** Expose location y property. */
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

        /**
         * @param node
         */
        private Notification(Node node) {
            VBox box = new VBox(node);
            StyleHelper.of(box).style(Styles.popup);
            box.setMaxWidth(width);
            box.setMinWidth(width);
            box.setOpacity(opacity);

            popup.setX(0);
            popup.getContent().add(box);
            UserActionHelper.of(popup).when(User.MouseClick).to(() -> remove(this));
        }

    }

    /**
     * Notification style.
     */
    private static interface Styles extends StyleDSL {

        Style popup = () -> {
            padding.vertical(MARGIN, px).horizontal(MARGIN, px);
            background.color("-fx-background");
            border.radius(7, px);
        };
    }
}
