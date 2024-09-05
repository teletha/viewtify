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
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;

import org.controlsfx.glyphfont.FontAwesome;

import kiss.Disposable;
import kiss.I;
import kiss.Variable;
import kiss.WiseRunnable;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.preference.Preferences;
import viewtify.ui.UILabel;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
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
     * @param monitor The monitorable notification.
     */
    public static void show(ToastMonitor monitor) {
        if (setting.enable.is(true)) {
            Notification notification = new Notification();
            MonitorView view = new MonitorView(monitor, notification);
            notification.builder = view::ui;
            notification.monitor = monitor;

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
                Notification notify = iterator.next();
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

        private ToastMonitor monitor;

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
                ui.getContent().add(monitor == null ? box : showCloseButton(box));
                if (monitor == null) {
                    UserActionHelper.of(ui).when(User.MouseClick).to(() -> remove(this));
                    if (0 < setting.autoHide.v * 1000) {
                        disposer = I.schedule(setting.autoHide.v.longValue(), TimeUnit.SECONDS)
                                .first()
                                .on(Viewtify.UIThread)
                                .to(() -> remove(this));
                    }
                }
            }
            return ui;
        }

        /**
         * Show close button at top right corner.
         * 
         * @param node
         * @return
         */
        private Node showCloseButton(Node node) {
            UILabel label = new UILabel(null).text(FontAwesome.Glyph.CLOSE, styles.icon)
                    .tooltip(I.translate("Stop this task."))
                    .when(User.LeftClick, () -> {
                        remove(this);
                        monitor.cancels.forEach(WiseRunnable::run);
                    });

            StackPane pane = new StackPane();
            pane.getChildren().addAll(node, label.ui);

            StackPane.setAlignment(label.ui, Pos.TOP_RIGHT);
            StackPane.setMargin(label.ui, new Insets(5, 7, 0, 0));
            StackPane.setAlignment(node, Pos.TOP_LEFT);

            return pane;
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

        Style indicator = () -> {
            margin.right(12, px);
        };

        Style title = () -> {
            margin.top(3, px);
        };

        Style icon = () -> {
            cursor.pointer();

            $.hover(() -> {
                font.color("-fx-focus-color");
            });
        };
    }

    /**
     * Monitorable task view.
     */
    private static class MonitorView extends View implements Disposable {

        private UILabel title;

        private UILabel message;

        private ProgressIndicator indicator;

        private final ToastMonitor monitor;

        private final Notification notification;

        /**
         * @param
         */
        private MonitorView(ToastMonitor monitor, Notification notification) {
            this.monitor = monitor;
            this.notification = notification;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected ViewDSL declareUI() {
            return new ViewDSL() {
                {
                    $(hbox, () -> {
                        $(() -> indicator, styles.indicator);
                        $(vbox, styles.title, () -> {
                            $(title);
                            $(message);
                        });
                    });
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            int width = setting.width.v - styles.pad * 2;
            title.ui.setMaxWidth(width);
            title.ui.setWrapText(true);
            message.ui.setMaxWidth(width);
            message.ui.setWrapText(true);

            monitor.title.observing().switchVariable(I::translate).to(x -> {
                title.text(x);
            });
            monitor.message.observing().switchVariable(I::translate).to(x -> {
                message.text(x);
            });
            monitor.progress.observing().to(x -> {
                indicator.setProgress(x);

                if (1d <= x) {
                    monitor.completes.forEach(WiseRunnable::run);
                    I.schedule(400, TimeUnit.MILLISECONDS).to(() -> Toast.remove(notification));
                }
            });
        }

    }
}