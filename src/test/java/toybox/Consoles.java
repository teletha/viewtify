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

import java.util.concurrent.TimeUnit;

import kiss.Storable;
import psychopath.Directory;
import psychopath.Locator;
import viewtify.model.Selectable;

/**
 * @version 2018/09/17 22:24:47
 */
public class Consoles extends Selectable<Console> implements Storable<Consoles> {

    public Consoles() {
        restore();
        selectionIndex.observe().as(Object.class).merge(add, remove).debounce(1000, TimeUnit.MILLISECONDS).to(this::store);
    }

    public void createConsole() {
        Directory directory = hasSelection() ? selection().directory : Locator.directory("").absolutize();

        Console console = new Console();
        console.directory = directory;

        add(console);
        System.out.println(this);
    }
}
