/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package toybox;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import filer.Filer;
import kiss.Storable;
import viewtify.model.Selectable;

/**
 * @version 2018/03/04 19:33:38
 */
public class Consoles extends Selectable<Console> implements Storable<Consoles> {

    public Consoles() {
        restore();
        selectionIndex.observe().as(Object.class).merge(add, remove).debounce(1000, TimeUnit.MILLISECONDS).to(this::store);
    }

    public void createConsole() {
        Path directory = hasSelection() ? selection().directory : Filer.locate("").toAbsolutePath();

        Console console = new Console();
        console.directory = directory;

        add(console);
        System.out.println(this);
    }
}
