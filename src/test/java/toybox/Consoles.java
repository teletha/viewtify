/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox;

import java.nio.file.Path;

import filer.Filer;

/**
 * @version 2018/03/04 19:33:38
 */
public class Consoles extends SelectableModel<Console> {

    public void createConsole() {
        Path directory = getSelectedIndex() == -1 ? Filer.locate("").toAbsolutePath() : getSelectedItem().directory;

        Console console = new Console();
        console.directory = directory;

        items.add(console);
        System.out.println(this);
    }
}
