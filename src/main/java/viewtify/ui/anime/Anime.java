/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.anime;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;
import java.util.function.LongUnaryOperator;
import java.util.function.UnaryOperator;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.WritableDoubleValue;
import javafx.beans.value.WritableIntegerValue;
import javafx.beans.value.WritableLongValue;
import javafx.beans.value.WritableValue;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import kiss.Disposable;
import kiss.WiseRunnable;
import viewtify.Viewtify;
import viewtify.ui.UserInterfaceProvider;

public class Anime {

    /** The standard effect time. */
    public static final Duration BASE_DURATION = Duration.seconds(0.2);

    /** The registered init task. */
    private final List<WiseRunnable> initializers = new ArrayList();

    /** The initial timeline. */
    private final Timeline initial = new Timeline();

    /** The current timeline. */
    private Timeline current = initial;

    /** The default duraion. */
    private Duration defaultDuration = BASE_DURATION;

    /** The default interpolation. */
    private Interpolator defaultInterpolator = Interpolator.LINEAR;

    /** The side effect manager. */
    private final List<WiseRunnable> effects = new ArrayList();

    /**
     * Create new {@link Anime}.
     * 
     * @return
     */
    public static Anime define() {
        return new Anime();
    }

    /**
     * Hide constructor.
     */
    private Anime() {
    }

    /**
     * Set default interpolation.
     * 
     * @param interpolator
     * @return Chainable API.
     */
    public final Anime interpolator(Interpolator interpolator) {
        this.defaultInterpolator = interpolator;
        return this;
    }

    /**
     * Set default duration.
     * 
     * @param duration
     * @return Chainable API.
     */
    public final Anime duration(double duration) {
        this.defaultDuration = Duration.seconds(duration);
        return this;
    }

    /**
     * Set default duration.
     * 
     * @param duration
     * @return Chainable API.
     */
    public final Anime duration(Duration duration) {
        this.defaultDuration = duration;
        return this;
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime init(WritableValue<Number> value, Number num) {
        if (value != null && num != null) {
            initializers.add(() -> value.setValue(num));
        }
        return this;
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime init(WiseRunnable init) {
        if (init != null) {
            initializers.add(init);
        }
        return this;
    }

    /**
     * Animate background color
     * 
     * @param region
     * @param color
     * @return
     */
    public final Anime backgroundColor(UserInterfaceProvider<? extends Region> region, Color color) {
        BackgroundBinding bind = new BackgroundBinding(region.ui());
        return effect(bind.color, color);
    }

    /**
     * Animate background color
     * 
     * @param node
     * @param num
     * @return
     */
    public final Anime opacity(UserInterfaceProvider<? extends Node> node, double num) {
        return effect(node.ui().opacityProperty(), num);
    }

    /**
     * Animate location.
     * 
     * @param node
     * @param x
     * @param y
     * @return
     */
    public final Anime move(UserInterfaceProvider<? extends Node> node, double x, double y) {
        return moveX(node, x).moveY(node, y);
    }

    /**
     * Animate location.
     * 
     * @param node
     * @param x
     * @return
     */
    public final Anime moveX(UserInterfaceProvider<? extends Node> node, double x) {
        return effect(node.ui().translateXProperty(), x);
    }

    /**
     * Animate location.
     * 
     * @param node
     * @param y
     * @return
     */
    public final Anime moveY(UserInterfaceProvider<? extends Node> node, double y) {
        return effect(node.ui().translateYProperty(), y);
    }

    /**
     * Extract the user defined style.
     * 
     * @param <N>
     * @param <T>
     * @param node
     * @param extractor
     * @return
     */
    private static <N extends Node, T> T extractStyle(N node, Function<N, T> extractor) {
        T style = extractor.apply(node);
        if (style == null) {
            node.applyCss();
            style = extractor.apply(node);
        }
        return style;
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final <V> Anime effect(WritableValue<V> value, V num) {
        return effect(value, num, (Duration) null);
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final <V> Anime effect(WritableValue<V> value, UnaryOperator<V> num) {
        return effect(value, num.apply(value.getValue()));
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime effect(WritableIntegerValue value, IntUnaryOperator num) {
        return effect(value, num.applyAsInt(value.get()));
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime effect(WritableLongValue value, LongUnaryOperator num) {
        return effect(value, num.applyAsLong(value.get()));
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final Anime effect(WritableDoubleValue value, DoubleUnaryOperator num) {
        return effect(value, num.applyAsDouble(value.get()));
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final <V> Anime effect(WritableValue<V> value, V num, Interpolator interpolator) {
        return effect(value, num, null, interpolator);
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final <V> Anime effect(WritableValue<V> value, V num, double sec) {
        return effect(value, num, Duration.seconds(sec));
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final <V> Anime effect(WritableValue<V> value, V num, double sec, Interpolator interpolator) {
        return effect(value, num, Duration.seconds(sec), interpolator);
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final <V> Anime effect(WritableValue<V> value, V num, Duration duration) {
        return effect(value, num, duration, null);
    }

    /**
     * Shorthand to declare animation effect.
     */
    public final <V> Anime effect(WritableValue<V> value, V num, Duration duration, Interpolator interpolator) {
        duration = Objects.requireNonNullElse(duration, defaultDuration);
        interpolator = Objects.requireNonNullElse(interpolator, defaultInterpolator);

        current.getKeyFrames().add(new KeyFrame(duration, new KeyValue(value, num, interpolator)));
        return this;
    }

    /**
     * Register post action.
     * 
     * @param effect
     * @return
     */
    public final Anime effect(WiseRunnable effect) {
        if (effect != null) {
            effects.add(effect);
        }
        return this;
    }

    /**
     * Define the next action.
     * 
     * @return
     */
    public final Anime then(WiseRunnable... finisher) {
        effects.addAll(List.of(finisher));

        Timeline before = current;
        Timeline after = current = new Timeline();
        before.setOnFinished(e -> {
            effects.forEach(WiseRunnable::run);
            effects.clear();

            after.play();
        });
        return this;
    }

    /**
     * Play animation.
     */
    public final Disposable run(WiseRunnable... finisher) {
        effects.addAll(List.of(finisher));

        current.setOnFinished(e -> {
            effects.forEach(WiseRunnable::run);
            effects.clear();
        });

        for (Runnable initializer : initializers) {
            initializer.run();
        }

        Viewtify.inUI(initial::play);

        return current::stop;
    }

    /**
     * Play animation with loop.
     */
    public Disposable runInfinitely() {
        current.setAutoReverse(true);
        current.setCycleCount(Integer.MAX_VALUE);
        return run();
    }

    private static final class BackgroundBinding {

        /** The color holder. */
        private final ObjectProperty<Color> color = new SimpleObjectProperty();

        /** The corner holder. */
        private CornerRadii corner = CornerRadii.EMPTY;

        /** The inset holder. */
        private Insets inset = Insets.EMPTY;

        /** Background porperyt holder */
        private final List<BackgroundImage> images;

        /**
         * @param colorProperty
         */
        private BackgroundBinding(Region region) {
            Background background = extractStyle(region, Region::getBackground);
            List<BackgroundFill> fills = background.getFills();
            if (!fills.isEmpty()) {
                BackgroundFill fill = fills.get(0);

                if (fill.getFill() instanceof Color c) color.set(c);
                corner = fill.getRadii();
                inset = fill.getInsets();
            }
            images = background.getImages();

            Viewtify.observe(color).to(x -> {
                region.setBackground(new Background(List.of(new BackgroundFill(color.get(), corner, inset)), images));
            });
        }
    }
}