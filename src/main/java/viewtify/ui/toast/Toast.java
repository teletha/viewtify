/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.toast;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javafx.beans.value.WritableDoubleValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import javafx.util.Duration;

import kiss.Disposable;
import kiss.I;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.model.PreferenceModel;
import viewtify.ui.anime.Animatable;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;
import viewtify.util.Corner;
import viewtify.util.ScreenSelector;

public class Toast {

    /** The margin for each notifications. */
    private static final int MARGIN = 12;

    /** The base transparent window. */
    private static final LinkedList<Notification> notifications = new LinkedList();

    /** The setting. */
    public static final Setting setting = I.make(Setting.class);

    /**
     * Show the specified node.
     */
    public static void show(String message) {
        show(() -> {
            Label label = new Label(message);
            label.setWrapText(true);
            return label;
        });
    }

    /**
     * Show the specified node.
     * 
     * @param node
     */
    public static void show(Node node) {
        show(() -> node);
    }

    /**
     * Show the specified node.
     * 
     * @param node
     */
    public static void show(Supplier<Node> node) {
        add(new Notification(node));
    }

    /**
     * Add notification.
     * 
     * @param notification
     */
    private static void add(Notification notification) {
        notifications.add(notification);
        if (setting.max.v < notifications.size()) {
            while (setting.max.v < notifications.size()) {
                remove(notifications.peekFirst());
            }
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
        // model management
        if (notifications.remove(notification)) {
            if (notification.disposer != null) {
                notification.disposer.dispose();
            }

            // UI effect
            Animatable.play(setting.animation.v, notification.ui().opacityProperty(), 0, () -> {
                notification.ui().hide();
                notification.ui().getContent().clear();
            });

            // draw UI
            layoutNotifications();
        }
    }

    /**
     * Layout all notifications.
     */
    private static void layoutNotifications() {
        Viewtify.inUI(() -> {
            Rectangle2D rect = setting.screen.v.select().getBounds();
            boolean isTopSide = setting.area.v.isTopSide();
            double x = setting.area.v.isLeftSide() ? rect.getMinX() + MARGIN : rect.getMaxX() - setting.width.v - MARGIN;
            double y = isTopSide ? MARGIN : rect.getMaxY() - MARGIN;

            Iterator<Notification> iterator = isTopSide ? notifications.descendingIterator() : notifications.iterator();
            while (iterator.hasNext()) {
                Toast.Notification notify = iterator.next();
                Popup popup = notify.ui();

                if (popup.isShowing()) {
                    if (!isTopSide) y -= popup.getHeight() + MARGIN;
                    popup.setX(x);
                    Animatable.play(setting.animation.v, notify.y, y);
                } else {
                    popup.setOpacity(0);
                    popup.show(Window.getWindows().get(0));
                    if (!isTopSide) y -= popup.getHeight() + MARGIN;
                    popup.setX(x);
                    popup.setY(y);

                    Animatable.play(setting.animation.v, popup.opacityProperty(), 1);
                }

                if (isTopSide) y += popup.getHeight() + MARGIN;
            }
        });
    }

    /**
     * 
     */
    public static class Setting extends PreferenceModel<Setting> {

        /** The maximum size of notifications. */
        public final Preference<Integer> max = initialize(7).requireMin(1);

        /** The animation time. */
        public final Preference<Duration> animation = initialize(Duration.millis(333)).requireMin(Duration.ONE);

        /** The automatic hiding time. */
        public final Preference<Duration> autoHide = initialize(Duration.seconds(60)).requireMin(Duration.ZERO);

        /** The notification area. */
        public final Preference<Corner> area = initialize(Corner.TopRight);

        /** The notification screen. */
        public final Preference<ScreenSelector> screen = initialize(ScreenSelector.Application);

        /** The opacity of notification area. */
        public final Preference<Double> opacity = initialize(0.85).requireBetween(0, 1);

        /** The width of notification area. */
        public final Preference<Integer> width = initialize(250).requireMin(50);

        /**
         * Hide constructor.
         */
        private Setting() {
            sync();
        }
    }

    /**
     * 
     */
    private static class Notification {

        private final Supplier<Node> builder;

        /** The base transparent window. */
        private Popup ui;

        /** Expose location y property. */
        private final WritableDoubleValue y = new WritableDoubleValue() {

            @Override
            public Number getValue() {
                return get();
            }

            @Override
            public double get() {
                return ui().getY();
            }

            @Override
            public void setValue(Number value) {
                set(value.doubleValue());
            }

            @Override
            public void set(double value) {
                ui().setY(value);
            }
        };

        private Disposable disposer;

        /**
         * @param node
         */
        private Notification(Supplier<Node> builder) {
            this.builder = builder;
        }

        /**
         * Generate UI lazy.
         * 
         * @return
         */
        private synchronized Popup ui() {
            if (ui == null) {
                ui = new Popup();
                VBox box = new VBox(builder.get());
                StyleHelper.of(box).style(Styles.popup);
                box.setMaxWidth(setting.width.v);
                box.setMinWidth(setting.width.v);
                box.setOpacity(setting.opacity.v);

                ui.setX(0);
                ui.getContent().add(box);
                UserActionHelper.of(ui).when(User.MouseClick).to(() -> remove(this));
                if (0 < setting.autoHide.v.toMillis()) {
                    disposer = I.schedule((long) setting.autoHide.v.toMillis(), TimeUnit.MILLISECONDS)
                            .first()
                            .on(Viewtify.UIThread)
                            .to(() -> remove(this));
                }
            }
            return ui;
        }
    }

    /**
     * Notification style.
     */
    private static interface Styles extends StyleDSL {

        Style popup = () -> {
            padding.vertical(MARGIN, px).horizontal(MARGIN, px);
            background.color("-fx-background");
            border.radius(7, px).color("-fx-mid-text-color");
        };
    }
}