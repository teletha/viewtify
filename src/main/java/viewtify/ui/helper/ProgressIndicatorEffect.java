/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.util.concurrent.TimeUnit;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import kiss.Disposable;
import kiss.I;
import viewtify.Viewtify;

class ProgressIndicatorEffect extends Blend {

    private static final int stripeWidthColor = 5;

    private static final int stripeWidthTransparent = 10;

    private static final int initialDelay = 200;

    /** The duretion of fade out. (millis) */
    private static final int fadeTime = 800;

    /** The step of fade out. */
    private static final int fadeStep = 60;

    private final ImageInput input = new ImageInput();

    private long startTime;

    private double opacity = 1;

    // 0 : none
    // 1 : starting
    // 2 : stopping
    private int state = 0;

    private Disposable startingTask;

    private Disposable stoppingTask;

    ProgressIndicatorEffect() {
        setTopInput(input);
        setMode(BlendMode.SRC_OVER);
    }

    public synchronized void start(Region region) {
        switch (state) {
        case 0: // none
            state = 1;
            startTime = System.currentTimeMillis();
            region.setEffect(this);
            startingTask = I.schedule(initialDelay, 50, TimeUnit.MILLISECONDS, true)
                    .on(Viewtify.UIThread)
                    .effectOnDispose(() -> region.setEffect(null))
                    .to(x -> {
                        input.setSource(createDiagonalStripesImage((int) region.getWidth(), (int) region.getHeight(), x.intValue()));
                    });
            break;

        case 1: // starting
            break;

        case 2: // stopping
            state = 1;
            if (stoppingTask != null) {
                stoppingTask.dispose();
                stoppingTask = null;
                opacity = 1;
            }
            break;
        }
    }

    public synchronized void stop() {
        switch (state) {
        case 0: // none
            break;

        case 1: // starting
            state = 2;
            if (System.currentTimeMillis() - startTime <= initialDelay) {
                reset();
            } else {
                stoppingTask = I.schedule(0, fadeTime / fadeStep, TimeUnit.MILLISECONDS, true)
                        .take(fadeStep)
                        .on(Viewtify.UIThread)
                        .effectOnComplete(this::reset)
                        .to(x -> opacity = Math.max(0, opacity - (1d / fadeStep)));
            }
            break;

        case 2: // stopping
            break;
        }
    }

    private void reset() {
        if (startingTask != null) {
            startingTask.dispose();
            startingTask = null;
        }
        if (stoppingTask != null) {
            stoppingTask.dispose();
            stoppingTask = null;
        }
        opacity = 1;
        startTime = 0;
        state = 0;
    }

    private WritableImage createDiagonalStripesImage(int width, int height, int frame) {
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int stripeWidth = stripeWidthColor + stripeWidthTransparent;
        int offset = frame % stripeWidth;
        Color stripeColor = Viewtify.CurrentTheme.v.accent();
        Color color = new Color(stripeColor.getRed(), stripeColor.getGreen(), stripeColor.getBlue(), opacity);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int diagonalPosition = x - offset + y;
                if (diagonalPosition < 0 || diagonalPosition % stripeWidth >= stripeWidthColor) {
                    pixelWriter.setColor(x, y, Color.TRANSPARENT);
                } else {
                    pixelWriter.setColor(x, y, color);
                }
            }
        }

        return writableImage;
    }
}
