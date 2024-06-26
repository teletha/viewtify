/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.toast;

import java.util.Deque;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
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
import kiss.Variable;
import kiss.WiseRunnable;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.preference.Preferences;
import viewtify.ui.anime.Anime;
import viewtify.ui.helper.StyleHelper;
import viewtify.ui.helper.User;
import viewtify.ui.helper.UserActionHelper;
import viewtify.util.TextNotation;

/**
 * Represents a Toast notification utility class that provides methods to show various types of
 * notifications. Notifications are displayed as transient messages to the user.
 */
public class Toast {

    /** The base transparent window. */
    private static final Deque<Notification> notifications = new ConcurrentLinkedDeque();

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
     * Shows a Toast notification with the specified message and optional actions.
     *
     * @param message The message to be displayed in the notification.
     * @param actions Optional actions (WiseRunnable) to be performed when the notification is
     *            interacted with.
     */
    public static void show(Object message, WiseRunnable... actions) {
        if (setting.enable.is(true)) {
            Toastable toastable = I.find(Toastable.class, message.getClass());
            if (toastable == null) {
                show(Objects.toString(message), actions);
            } else {
                toastable.show(message);
            }
        }
    }

    /**
     * Shows a Toast notification with the specified message and optional actions.
     *
     * @param message The message to be displayed in the notification.
     * @param actions Optional actions (WiseRunnable) to be performed when the notification is
     *            interacted with.
     */
    public static void show(String message, WiseRunnable... actions) {
        if (setting.enable.is(true)) {
            Notification notification = new Notification();
            WiseRunnable hide = () -> remove(notification);
            notification.builder = () -> TextNotation
                    .parse(message, setting.width.v - styles.pad * 2, I.signal(actions).map(x -> I.bundle(hide, x)).toList());

            add(notification);
        }
    }

    /**
     * Shows a Toast notification with the specified dynamic message variable and optional actions.
     *
     * @param message The dynamic message variable to be displayed in the notification.
     * @param actions Optional actions (WiseRunnable) to be performed when the notification is
     *            interacted with.
     */
    public static void show(Variable<String> message, WiseRunnable... actions) {
        if (setting.enable.is(true)) {
            Notification notification = new Notification();
            WiseRunnable hide = () -> remove(notification);
            notification.builder = () -> TextNotation
                    .parse(message, setting.width.v - styles.pad * 2, I.signal(actions).map(x -> I.bundle(hide, x)).toList());

            add(notification);
        }
    }

    /**
     * Shows a Toast notification with the specified Node.
     *
     * @param node The Node to be displayed in the notification.
     */
    public static void show(Node node) {
        show(() -> node);
    }

    /**
     * Shows a Toast notification with the specified Node supplier.
     *
     * @param node The Node supplier to provide the content for the notification.
     */
    public static void show(Supplier<Node> node) {
        if (setting.enable.is(true)) {
            Notification notification = new Notification();
            notification.builder = node;

            add(notification);
        }
    }

    /**
     * Adds a new notification to the list of notifications. Handles maximum notification count.
     *
     * @param notification The Notification to be added.
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
     * Removes a notification from the list of notifications.
     *
     * @param notification The Notification to be removed.
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
     * Layouts all notifications on the screen according to the specified settings.
     */
    private static void layoutNotifications() {
        Viewtify.inUI(() -> {
            double gap = setting.gap.exact();
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
                    Anime.define().effect(notify, y, setting.animation.v).run();
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
     * Represents a single notification with its associated builder, UI, and disposer.
     */
    private static class Notification implements WritableDoubleValue {

        private Supplier<Node> builder;

        /** The base transparent window. */
        private Popup ui;

        private Disposable disposer;

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

        /**
         * Generates UI lazily.
         *
         * @return The Popup UI for the notification.
         */
        private synchronized Popup ui() {
            if (ui == null) {
                ui = new Popup();
                VBox box = new VBox(builder.get());
                StyleHelper.of(box).style(styles.popup);
                box.setMaxWidth(setting.width.v);
                box.setMinWidth(setting.width.v);
                box.setOpacity(setting.opacity.v / 100d);

                ui.setX(0);
                ui.getContent().add(box);
                UserActionHelper.of(ui).when(User.MouseClick).to(() -> remove(this));
                if (0 < setting.autoHide.v * 1000) {
                    disposer = I.schedule(setting.autoHide.v.longValue(), TimeUnit.SECONDS)
                            .first()
                            .on(Viewtify.UIThread)
                            .to(() -> remove(this));
                }
            }
            return ui;
        }
    }

    /**
     * Represents the styles for Toast notifications, including padding, background, and border
     * styles.
     */
    private static interface styles extends StyleDSL {

        int pad = 15;

        Style popup = () -> {
            padding.size(pad, px);
            background.color("derive(-fx-control-inner-background, 10%)");
            border.radius(5, px).color("-fx-light-text-color");
        };
    }
}