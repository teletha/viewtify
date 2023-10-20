/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.effect.Effect;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import kiss.I;
import kiss.Signal;
import kiss.Variable;
import viewtify.Viewtify;

public interface DisableHelper<Self extends DisableHelper> extends PropertyAccessHelper {

    /**
     * Get the disable property.
     * 
     * @return
     */
    default Property<Boolean> disableProperty() {
        return property(Type.Disable);
    }

    /**
     * Gets whether it is disable.
     * 
     * @return A result.
     */
    default boolean isDisable() {
        return disableProperty().getValue() == true;
    }

    /**
     * Disables itself.
     * 
     * @return Chainable API.
     */
    default Self disableNow() {
        return disable(true);
    }

    /**
     * Disables itself.
     * 
     * @param state A disable state.
     * @return Chainable API.
     */
    default Self disable(boolean state) {
        disableProperty().setValue(state);
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default <V> Self disableWhen(ValueHelper<?, V> context, Predicate<V> condition) {
        if (context != null && condition != null) {
            disableWhen(context.observing().map(condition::test));
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default Self disableWhen(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one || other).to(disableProperty()::setValue);
        }
        return (Self) this;
    }

    /**
     * Disables itself when any specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default Self disableWhenAny(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one && other).to(disableProperty()::setValue);
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self disableWhen(Variable<Boolean> condition) {
        if (condition != null) {
            disableWhen(Viewtify.property(condition));
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param target A target value to test.
     * @param condition A condition.
     * @return Chainable API.
     */
    default <V> Self disableWhen(Variable<V> target, Predicate<V> condition) {
        if (target != null && condition != null) {
            disableWhen(target.observing().is(condition));
        }
        return (Self) this;
    }

    /**
     * Disables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self disableWhen(ObservableValue<Boolean> condition) {
        if (condition != null) {
            disableProperty().bind(condition);
        }
        return (Self) this;
    }

    /**
     * Disable itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self disableDuring(long time, TimeUnit unit) {
        if (0 < time && unit != null) {
            disable(true);
            I.schedule(time, unit).to(() -> disable(false));
        }
        return (Self) this;
    }

    /**
     * Disable itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self disableDuring(long time, ChronoUnit unit) {
        if (0 < time && unit != null) {
            disableDuring(time, TimeUnit.of(unit));
        }
        return (Self) this;
    }

    /**
     * Disable itself for a bit.
     * 
     * @return Chainable API.
     */
    default Self disableBriefly() {
        return disableDuring(400, TimeUnit.MILLISECONDS);
    }

    /**
     * Gets whether it is enable.
     * 
     * @return A result.
     */
    default boolean isEnable() {
        return disableProperty().getValue() == false;
    }

    /**
     * Enables itself.
     * 
     * @return Chainable API.
     */
    default Self enableNow() {
        return enable(true);
    }

    /**
     * Enables itself.
     * 
     * @param state A disable state.
     * @return Chainable API.
     */
    default Self enable(boolean state) {
        return disable(!state);
    }

    /**
     * Enables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default <V> Self enableWhen(ValueHelper<?, V> context, Predicate<V> condition) {
        if (context != null && condition != null) {
            enableWhen(context.observing().map(condition::test));
        }
        return (Self) this;
    }

    /**
     * Enables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default Self enableWhen(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one || other).to(this::enable);
        }
        return (Self) this;
    }

    /**
     * Enables itself when any specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @param conditions Additional timing conditions.
     * @return Chainable API.
     */
    default Self enableWhenAny(Signal<Boolean> condition, Signal<Boolean>... conditions) {
        if (condition != null) {
            condition.combineLatest(conditions, (one, other) -> one && other).to(this::enable);
        }
        return (Self) this;
    }

    /**
     * Enables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self enableWhen(Variable<Boolean> condition) {
        if (condition != null) {
            enableWhen(Viewtify.property(condition));
        }
        return (Self) this;
    }

    /**
     * Enables itself when the specified condition is True, and enables it when False.
     * 
     * @param condition A timing condition.
     * @return Chainable API.
     */
    default Self enableWhen(ObservableValue<Boolean> condition) {
        if (condition != null) {
            disableWhen(BooleanBinding.booleanExpression(condition).not());
        }
        return (Self) this;
    }

    /**
     * Enables itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self enableDuring(long time, TimeUnit unit) {
        if (0 < time && unit != null) {
            enable(true);
            I.schedule(time, unit).to(() -> enable(false));
        }
        return (Self) this;
    }

    /**
     * Enables itself for a specified time.
     * 
     * @param time A time value.
     * @param unit A time unit.
     * @return Chainable API.
     */
    default Self enableDuring(long time, ChronoUnit unit) {
        if (0 < time && unit != null) {
            enableDuring(time, TimeUnit.of(unit));
        }
        return (Self) this;
    }

    /**
     * Enables itself for a bit.
     * 
     * @return Chainable API.
     */
    default Self enableBriefly() {
        return enableDuring(400, TimeUnit.MILLISECONDS);
    }

    /**
     * Show progress indicator.
     * 
     * @return
     */
    default Self showIndicator() {
        Region ui = (Region) ui();

        Property<Effect> property = property(Type.Effect);
        property.setValue(createCircularProgressImage(ui.getWidth(), ui.getHeight()));

        // Anime.define()
        // .duration(2)
        // .effect(box.widthProperty(), 5)
        // .effect(box.heightProperty(), 5)
        // .effect(box.iterationsProperty(), 5)
        // .runInfinitely();

        return disableNow();
    }

    private Effect createCircularProgressImage(double width, double height) {
        WritableImage writableImage = new WritableImage((int) width, (int) height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int circleRadius = 25;
        int circleDiameter = 5;
        int numCircles = 16;
        double centerX = width / 2;
        double centerY = height / 2;
        double radius = Math.min(width, height) / 2 - 10; // 10は余白

        for (int i = 0; i < numCircles; i++) {
            double angle = 360.0 / numCircles * i;
            double x = centerX + circleRadius * Math.cos(Math.toRadians(angle));
            double y = centerY + circleRadius * Math.sin(Math.toRadians(angle));

            // 丸を描画
            for (int j = 0; j < 360; j++) {
                double radian = Math.toRadians(j);
                int pixelX = (int) (x + circleDiameter / 2 * Math.cos(radian)); // 半径の半分が直径の半分
                int pixelY = (int) (y + circleDiameter / 2 * Math.sin(radian));

                if (pixelX >= 0 && pixelX < width && pixelY >= 0 && pixelY < height) {
                    // 丸を塗りつぶしで描画
                    pixelWriter.setColor(pixelX, pixelY, Color.BLUE);
                }
            }
        }

        // // ブラーを追加して効果を強調
        // GaussianBlur blur = new GaussianBlur(5);
        // blur.setInput(writableImage);
        //
        // WritableImage finalImage = new WritableImage(width, height);
        // PixelWriter finalPixelWriter = finalImage.getPixelWriter();
        // for (int x = 0; x < width; x++) {
        // for (int y = 0; y < height; y++) {
        // finalPixelWriter.setColor(x, y, pixelWriter.getColor(x, y));
        // }
        // }

        return new ImageInput(writableImage);
    }

    private Effect createStripedPatternEffect(double width, double height) {
        WritableImage writableImage = new WritableImage((int) width, (int) height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        Color stripeColor1 = Color.RED;
        Color stripeColor2 = Color.TRANSPARENT;

        // 斜め45度のストライプを描画
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int stripePosition = x + y; // 斜めの位置
                int stripeIndex = stripePosition / 3;
                Color stripeColor = stripeIndex % 2 == 0 ? stripeColor1 : stripeColor2;
                pixelWriter.setColor(x, y, stripeColor);
            }
        }

        return new ImageInput(writableImage);
    }

    /**
     * Hide progress indicator.
     * 
     * @return
     */
    default Self hideIndicator() {
        Property<Effect> property = property(Type.Effect);
        property.setValue(null);

        return enableNow();
    }
}