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

import javafx.scene.SnapshotParameters;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ImageInput;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import kiss.Disposable;
import kiss.I;
import viewtify.Viewtify;

class LoaderEffect extends Blend {

    /** The width of colored stripe. */
    private static final int stripeWidthColor = 3;

    /** The width of transparen striped. */
    private static final int stripeWidthTransparent = 6;

    /** The waiting time before showing stripe. */
    private static final int initialDelay = 200;

    /** The duretion of fade out. (millis) */
    private static final int fadeTime = 600;

    /** The step of fade out. */
    private static final int fadeStep = 20;

    /** The reusable text. */
    private static WritableImage textImage;

    /** The image effect. */
    private final ImageInput image = new ImageInput();

    /** The text effect. */
    private final ImageInput text = new ImageInput();

    /** The image and text. */
    private final Blend overlay = new Blend(BlendMode.SRC_OVER);

    /** The latest start time. */
    private long startTime;

    /** The state of effect. */
    // 0 : none
    // 1 : starting
    // 2 : stopping
    private int state = 0;

    /** The stripe showing task, */
    private Disposable startingTask;

    /** The stripe hiding task. */
    private Disposable stoppingTask;

    /**
     * Hide construction.
     */
    LoaderEffect() {
        overlay.setTopInput(text);
        overlay.setBottomInput(image);

        setTopInput(overlay);
        setBottomInput(new BoxBlur(2, 2, 1));
        setMode(BlendMode.SRC_OVER);
    }

    /**
     * Show the progress indicator.
     * 
     * @param region
     */
    public synchronized void show(Region region) {
        switch (state) {
        case 0: // none
            state = 1;
            startTime = System.currentTimeMillis();
            region.setEffect(this);
            startingTask = I.schedule(initialDelay, 50, TimeUnit.MILLISECONDS, true)
                    .on(Viewtify.UIThread)
                    .effectOnDispose(() -> region.setEffect(null))
                    .to(x -> {
                        int width = (int) region.getWidth();
                        int height = (int) region.getHeight();

                        image.setSource(drawStripe(width, height, x.intValue()));

                        WritableImage textImage = drawText();
                        text.setSource(textImage);
                        text.setX((width - textImage.getWidth()) / 2);
                        text.setY((height - textImage.getHeight()) / 2);
                    });
            break;

        case 1: // starting
            break;

        case 2: // stopping
            state = 1;
            if (stoppingTask != null) {
                stoppingTask.dispose();
                stoppingTask = null;
                setOpacity(1);
            }
            break;
        }
    }

    /**
     * Hide the progress indicator.
     */
    public synchronized void hide() {
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
                        .effectOnComplete(this::reset)
                        .to(x -> setOpacity(Math.max(0, getOpacity() - (1d / fadeStep))));
            }
            break;

        case 2: // stopping
            break;
        }
    }

    /**
     * Reset state.
     */
    private void reset() {
        if (startingTask != null) {
            startingTask.dispose();
            startingTask = null;
        }
        if (stoppingTask != null) {
            stoppingTask.dispose();
            stoppingTask = null;
        }
        setOpacity(1);
        startTime = 0;
        state = 0;
    }

    /**
     * Draw the stripe image
     * 
     * @param width
     * @param height
     * @param frame
     * @return
     */
    private WritableImage drawStripe(int width, int height, int frame) {
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int stripeWidth = stripeWidthColor + stripeWidthTransparent;
        int offset = frame % stripeWidth;
        Color color = Viewtify.CurrentTheme.v.accent();

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

    /**
     * Draw the stripe image
     * 
     * @return
     */
    private static synchronized WritableImage drawText() {
        if (textImage == null) {
            Text text = new Text("Loading...");
            text.setFont(Font.font("System", FontWeight.LIGHT, 14));
            text.setFill(Viewtify.CurrentTheme.v.text());
            text.setFontSmoothingType(FontSmoothingType.GRAY);

            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            textImage = text.snapshot(params, null);
        }
        return textImage;
    }
}
