/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.keys;

import javafx.stage.Stage;
import javafx.stage.Window;

import kiss.I;
import viewtify.Viewtify;

public enum WindowCommand implements Command<WindowCommand> {
    Close;

    static {
        Close.shortcut(Key.Escape).contribute(() -> {
            I.signal(Window.getWindows()).take(Window::isFocused).as(Stage.class).skip(Viewtify::isMainWindow).first().to(Stage::close);
        });
    }
}
