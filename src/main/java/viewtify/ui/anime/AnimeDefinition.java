/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.anime;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.util.Duration;
import kiss.WiseRunnable;

public abstract class AnimeDefinition {

    /** The current processing timeline. */
    private Timeline current;

    /**
     * Declare animation process.
     * 
     * @param action
     */
    public AnimeDefinition(WiseRunnable action) {
        Timeline before = current = new Timeline();
        before();
        before.setOnFinished(e -> {
            if (action != null) action.run();

            Timeline after = current = new Timeline();
            after();
            after.setOnFinished(x -> {
                cleanup();
            });
            after.play();
        });

        initialize();
        before.play();
    }

    /**
     * Initialize effects. You can override to declare your original effect.
     */
    public void initialize() {

    }

    /**
     * Declare animation before the action. You can override to declare your original effect.
     */
    public void before() {
    }

    /**
     * Declare animetion after the action. You can override to declare your original effect.
     */
    public void after() {
    }

    /**
     * Cleanup effects. You can override to declare your original effect.
     */
    public void cleanup() {
    }

    /** The standard effect time. */
    protected static final Duration BASE_DURATION = Duration.seconds(0.2);

    /**
     * Shorthand to declare animation effect.
     */
    protected final void effect(WritableValue<Number> value, Number num) {
        effect(value, num, BASE_DURATION);
    }

    /**
     * Shorthand to declare animation effect.
     */
    protected final void effect(WritableValue<Number> value, Number num, double sec) {
        effect(value, num, Duration.seconds(sec));
    }

    /**
     * Shorthand to declare animation effect.
     */
    protected final void effect(WritableValue<Number> value, Number num, Duration duration) {
        current.getKeyFrames().add(new KeyFrame(duration, new KeyValue(value, num)));
    }
}
