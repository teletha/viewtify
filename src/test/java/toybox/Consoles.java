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
public class Consoles extends Selectable<Consoles, Console> {

    public void createConsole() {
        Path directory = selectionIndex.v == -1 ? Filer.locate("").toAbsolutePath() : getSelection().directory;

        Console console = new Console();
        console.directory = directory;

        add(console);
        System.out.println(this);
    }
}
