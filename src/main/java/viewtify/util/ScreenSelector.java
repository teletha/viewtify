/*
 * Copyright (C) 2020 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import javafx.scene.robot.Robot;
import javafx.stage.Screen;
import javafx.stage.Window;

public enum ScreenSelector {

    Application, Mouse, Primary;

    /**
     * Select {@link Screen}.
     * 
     * @return
     */
    public Screen select() {
        switch (this) {
        case Application:
            Window w = Window.getWindows().get(0);
            // correct 10 pixel for maximized window
            return Screen.getScreensForRectangle(w.getX() + 10, w.getY(), w.getWidth(), w.getHeight()).get(0);

        case Mouse:
            Robot robot = new Robot();
            return Screen.getScreensForRectangle(robot.getMouseX(), robot.getMouseY(), 1, 1).get(0);

        default:
            return Screen.getPrimary();
        }
    }
}