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

    /** The duretion of fade out. (millis) */
    private static final int fadeTime = 800;

    /** The step of fade out. */
    private static final int fadeStep = 60;

    private final ImageInput input = new ImageInput();

    private final Region region;

    private boolean disposing;

    private Disposable disposable;

    private long start;

    private double opacity = 1;

    /**
     * 
     */
    ProgressIndicatorEffect(Region region) {
        this.region = region;

        setTopInput(input);
        setMode(BlendMode.SRC_OVER);
    }

    public synchronized void start() {
        if (disposable == null) {
            start = System.currentTimeMillis();
            region.setEffect(this);
            opacity = 1;

            disposable = I.schedule(150, 50, TimeUnit.MILLISECONDS, true).on(Viewtify.UIThread).to(x -> {
                input.setSource(createDiagonalStripesImage((int) region.getWidth(), (int) region.getHeight(), x.intValue()));
            });
        }
    }

    public synchronized void stop() {
        if (!disposing) {
            disposing = true;
            if (System.currentTimeMillis() - start < 200) {
                reset();
            } else {
                I.schedule(0, fadeTime / fadeStep, TimeUnit.MILLISECONDS, true).take(fadeStep).on(Viewtify.UIThread).to(x -> {
                    opacity = Math.max(0, opacity - (1d / fadeStep));
                }, e -> {

                }, this::reset);
            }
        }
    }

    private void reset() {
        region.setEffect(null);
        disposable.dispose();
        disposable = null;
        disposing = false;
        opacity = 1;
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
