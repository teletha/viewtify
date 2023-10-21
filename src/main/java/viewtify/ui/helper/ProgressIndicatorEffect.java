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

    private final ImageInput input = new ImageInput();

    private final Region region;

    private Disposable disposable;

    private long start;

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

            disposable = I.schedule(150, 50, TimeUnit.MILLISECONDS, true).on(Viewtify.UIThread).to(x -> {
                input.setSource(createDiagonalStripesImage((int) region.getWidth(), (int) region.getHeight(), x.intValue(), 10, 5, 1));
            });
        }
    }

    public synchronized void stop() {
        if (disposable != null) {
            if (System.currentTimeMillis() - start < 100) {
                region.setEffect(null);
                disposable.dispose();

                disposable = null;
            } else {
                // I.schedule(0, 50, TimeUnit.MILLISECONDS, true).on(Viewtify.UIThread).to(x -> {
                // input.setSource(createDiagonalStripesImage((int) region.getWidth(), (int)
                // region.getHeight(), x.intValue(), 10, 5, 1));
                // });
            }
        }
    }

    private void fadeOut() {

    }

    private WritableImage createDiagonalStripesImage(int width, int height, int frame, int firstWidth, int secondWidth, double opacity) {
        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        int w = firstWidth + secondWidth;
        int offset = frame % w;
        Color stripeColor = Viewtify.CurrentTheme.v.accent();
        stripeColor = new Color(stripeColor.getRed(), stripeColor.getGreen(), stripeColor.getBlue(), opacity);
        Color transparentColor = Color.TRANSPARENT;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                // ストライプの開始位置をオフセットによって調整
                int diagonalPosition = x - offset + y;
                if (diagonalPosition < 0) {
                    // オフセットでずらした分は透明色で始まる
                    pixelWriter.setColor(x, y, transparentColor);
                } else if (diagonalPosition % w < secondWidth) { // 2pxの水色ストライプ
                    pixelWriter.setColor(x, y, stripeColor);
                } else { // 4pxの透明ストライプ
                    pixelWriter.setColor(x, y, transparentColor);
                }
            }
        }

        return writableImage;
    }
}
