/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.function.BiFunction;
import java.util.function.Predicate;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import kiss.I;
import kiss.Signal;
import viewtify.Key;

/**
 * @version 2018/08/02 10:24:27
 */
public final class User<E extends Event> {

    /** User Action */
    public static final User<ActionEvent> Action = new User(ActionEvent.ACTION);

    /** User Action */
    public static final User<GestureEvent> Gesture = GestureBy(MouseButton.NONE);

    /**
     * Helper method to create {@link User} action for gesture.
     * 
     * @param button A target mouse button.
     * @return A user action.
     */
    public static final User<GestureEvent> GestureBy(MouseButton button) {
        return new User<>(MouseEvent.MOUSE_DRAGGED, (helper, signal) -> signal.take(valid(button))
                .takeUntil(helper.when(User.MouseRelease).take(valid(button)))
                .scan(GestureEvent::new, GestureEvent::update)
                .repeat());
    }

    /** User Action */
    public static final User<GestureEvent> GestureFinish = GestureFinishBy(MouseButton.NONE);

    /**
     * Helper method to create {@link User} action for gesture.
     * 
     * @param button A target mouse button.
     * @return A user action.
     */
    public static final User<GestureEvent> GestureFinishBy(MouseButton button) {
        return new User<>(MouseEvent.MOUSE_DRAGGED, (helper, signal) -> signal.take(valid(button))
                .takeUntil(helper.when(User.MouseRelease).take(valid(button)))
                .scan(GestureEvent::new, GestureEvent::update)
                .last()
                .repeat());
    }

    /**
     * Helper method to detect {@link MouseButton}.
     * 
     * @param button A target button to detect.
     * @return
     */
    private static Predicate<MouseEvent> valid(MouseButton button) {
        return button == null || button == MouseButton.NONE ? I.accept() : e -> e.getButton() == button;
    }

    /**
     * Helper method to create {@link User} action for key input.
     * 
     * @param button A target mouse button.
     * @return A user action.
     */
    public static final User<KeyEvent> input(Key key) {
        return new User<>(KeyEvent.KEY_RELEASED, (helper, signal) -> signal.take(valid(key)));
    }

    /**
     * Helper method to detect {@link Key}.
     * 
     * @param button A target key to detect.
     * @return
     */
    private static Predicate<KeyEvent> valid(Key key) {
        return key == null ? I.accept() : e -> e.getCode().getCode() == key.code;
    }

    /** User Action */
    public static final User<KeyEvent> KeyPress = new User(KeyEvent.KEY_PRESSED);

    /** User Action */
    public static final User<KeyEvent> KeyRelease = new User(KeyEvent.KEY_RELEASED);

    /** User Action */
    public static final User<KeyEvent> KeyType = new User(KeyEvent.KEY_TYPED);

    /** User Action */
    public static final User<MouseEvent> MouseClick = new User(MouseEvent.MOUSE_CLICKED);

    /** User Action */
    public static final User<MouseEvent> MouseDrag = new User(MouseEvent.MOUSE_DRAGGED);

    /** User Action */
    public static final User<MouseEvent> MouseEnter = new User(MouseEvent.MOUSE_ENTERED);

    /** User Action */
    public static final User<MouseEvent> MouseExit = new User(MouseEvent.MOUSE_EXITED);

    /** User Action */
    public static final User<MouseEvent> MouseMove = new User(MouseEvent.MOUSE_MOVED);

    /** User Action */
    public static final User<MouseEvent> MousePress = new User(MouseEvent.MOUSE_PRESSED);

    /** User Action */
    public static final User<MouseEvent> MouseRelease = new User(MouseEvent.MOUSE_RELEASED);

    /** User Action */
    public static final User<ScrollEvent> Scroll = new User(ScrollEvent.SCROLL);

    /** User Action */
    public static final User<ScrollEvent> ScrollStart = new User(ScrollEvent.SCROLL_STARTED);

    /** User Action */
    public static final User<ScrollEvent> ScrollFinish = new User(ScrollEvent.SCROLL_FINISHED);

    /** The actual event type. */
    final EventType type;

    /** The action post processor. */
    final BiFunction<UserActionHelper, Signal<?>, Signal<E>> hook;

    /**
     * Hide constructor.
     * 
     * @param type
     */
    private User(EventType<E> type) {
        this(type, (h, s) -> s);
    }

    /**
     * Hide constructor.
     * 
     * @param type
     */
    private <T extends Event> User(EventType<T> type, BiFunction<UserActionHelper<?>, Signal<T>, Signal<E>> hook) {
        this.type = type;
        this.hook = (BiFunction) hook;
    }

    /**
     * Drag event at detail.
     * 
     * @version 2018/08/01 17:02:20
     */
    @SuppressWarnings("serial")
    public static final class GestureEvent extends Event {

        /** The minimal movement where the gesture is recognized. */
        private static final double tolerance = 15;

        /** The initial event. */
        private final MouseEvent init;

        /** The previous event. */
        private MouseEvent prev;

        /** The current event. */
        private MouseEvent now;

        /** The last horizontal position where the gesture is recognized. */
        private double lastX;

        /** The last vertical position where the gesture is recognized. */
        private double lastY;

        /** The gesture expression. */
        private final StringBuilder directions = new StringBuilder();

        /**
         * @param start
         */
        private GestureEvent(MouseEvent start) {
            super(start.getSource(), start.getTarget(), start.getEventType());
            this.init = this.prev = this.now = start;

            lastX = start.getX();
            lastY = start.getY();
        }

        /**
         * Update event.
         * 
         * @param event
         * @return
         */
        private GestureEvent update(MouseEvent event) {
            this.prev = this.now;
            this.now = event;

            double x = event.getX();
            double y = event.getY();
            double distanceX = Math.abs(x - lastX);
            double distanceY = Math.abs(y - lastY);

            if (tolerance <= distanceX || tolerance <= distanceY) {
                // determine current direction
                char direction = distanceY < distanceX ? x < lastX ? 'L' : 'R' : y < lastY ? 'U' : 'D';

                // compare to last direction
                int length = directions.length();
                if (length == 0 || direction != directions.charAt(length - 1)) {
                    directions.append(direction);
                }

                // save current position
                lastX = x;
                lastY = y;
            }
            return this;
        }

        /**
         * Horizontal position of the event relative to the origin of the MouseEvent's source.
         *
         * @return horizontal position of the event relative to the origin of the MouseEvent's source.
         */
        public final double getX() {
            return now.getX() - prev.getX();
        }

        /**
         * Vertical position of the event relative to the origin of the MouseEvent's source.
         *
         * @return vertical position of the event relative to the origin of the MouseEvent's source.
         */
        public final double getY() {
            return now.getY() - prev.getY();
        }

        /**
         * Horizontal position of the event relative to the origin of the MouseEvent's source.
         *
         * @return horizontal position of the event relative to the origin of the MouseEvent's source.
         */
        public final double getFullX() {
            return now.getX() - init.getX();
        }

        /**
         * Vertical position of the event relative to the origin of the MouseEvent's source.
         *
         * @return vertical position of the event relative to the origin of the MouseEvent's source.
         */
        public final double getFullY() {
            return now.getY() - init.getY();
        }

        /**
         * The accumulated directional expression.
         * 
         * @return
         */
        public final String directions() {
            return directions.toString();
        }
    }
}
