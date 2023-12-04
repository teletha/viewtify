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
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;

import kiss.Disposable;
import kiss.I;
import kiss.WiseConsumer;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.preference.Preferences;
import viewtify.ui.anime.Anime;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;
import viewtify.util.TextNotation;

public class Toast extends Preferences {

    /** The base transparent window. */
    private static final LinkedList<Notification> notifications = new LinkedList();

    public static final ToastSetting setting = Preferences.of(ToastSetting.class);

    static {
        setting.gap.observe().to(Toast::layoutNotifications);
        setting.area.observe().to(Toast::layoutNotifications);
        setting.screen.observe().to(Toast::layoutNotifications);
        setting.opacity.observe().to(v -> {
            for (Notification notification : notifications) {
                notification.ui().getContent().get(0).setOpacity(v / 100d);
            }
        });
    }

    /**
     * Show the specified node.
     */
    public static void show(String message, WiseConsumer<Runnable>... actions) {
        if (setting.enable.is(true)) {
            Notification notification = new Notification();
            Runnable hide = () -> remove(notification);
            notification.builder = () -> TextNotation.parse(message, I.signal(actions).map(x -> x.bindLast(hide)).toList());

            add(notification);
        }
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
        if (setting.enable.is(true)) {
            Notification notification = new Notification();
            notification.builder = node;

            add(notification);
        }
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
            Anime.define().effect(notification.ui().opacityProperty(), 0, setting.animation.v).run(() -> {
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
            int gap = setting.gap.exact();
            Rectangle2D rect = setting.screen.v.select();

            // use viewtify notification
            boolean isTopSide = setting.area.v.isTopSide();
            double x = setting.area.v.isLeftSide() ? rect.getMinX() + gap : rect.getMaxX() - setting.width.v - gap;
            double y = isTopSide ? rect.getMinY() + gap : rect.getMaxY();

            Iterator<Notification> iterator = isTopSide ? notifications.descendingIterator() : notifications.iterator();
            while (iterator.hasNext()) {
                Toast.Notification notify = iterator.next();
                Popup popup = notify.ui();

                if (popup.isShowing()) {
                    if (!isTopSide) y -= popup.getHeight() + gap;
                    popup.setX(x);
                    Anime.define().effect(notify.y, y, setting.animation.v).run();
                } else {
                    popup.setOpacity(0);
                    popup.show(Window.getWindows().get(0));
                    if (!isTopSide) y -= popup.getHeight() + gap;
                    popup.setX(x);
                    popup.setY(y);

                    Anime.define().effect(popup.opacityProperty(), 1, setting.animation.v).run();
                }

                if (isTopSide) y += popup.getHeight() + gap;
            }
        });
    }

    /**
     * 
     */
    private static class Notification {

        private Supplier<Node> builder;

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
                box.setOpacity(setting.opacity.v / 100d);

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
            padding.vertical(15, px).horizontal(15, px);
            background.color("derive(-fx-control-inner-background, 10%)");
            border.radius(5, px).color("-fx-light-text-color");
        };
    }
}