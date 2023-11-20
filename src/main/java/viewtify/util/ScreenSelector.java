/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Window;
import kiss.I;
import kiss.Variable;

public enum ScreenSelector implements Translatable {

    Application(I.translate("Application")), Mouse(I.translate("Mouse")), Primary(I.translate("Primary")), InWindow(
            I.translate("In Window"));

    private final Variable<String> text;

    /**
     * @param text
     */
    private ScreenSelector(Variable<String> text) {
        this.text = text;
    }

    /**
     * Select {@link Screen}.
     * 
     * @return
     */
    public Rectangle2D select() {
        switch (this) {
        case Application:
            Window w = Window.getWindows().get(0);
            // correct 10 pixel for maximized window
            return Screen.getScreensForRectangle(w.getX() + 10, w.getY(), w.getWidth(), w.getHeight()).get(0).getBounds();

        case Mouse:
            Robot robot = new Robot();
            return Screen.getScreensForRectangle(robot.getMouseX(), robot.getMouseY(), 1, 1).get(0).getBounds();

        case Primary:
            return Screen.getPrimary().getBounds();

        default:
            Parent root = Window.getWindows().get(0).getScene().getRoot();
            Bounds bound = root.localToScreen(root.getBoundsInLocal());
            return new Rectangle2D(bound.getMinX(), bound.getMinY() + 10, bound.getWidth(), bound.getHeight());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Variable<String> toTraslated() {
        return text;
    }
}