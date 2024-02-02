/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.dock;

import icy.manipulator.Icy;
import icy.manipulator.Icy.Property;
import kiss.Variable;
import kiss.WiseConsumer;

@Icy
public abstract class DockModel {

    @Property
    public abstract String id();

    @Property
    public Variable<String> title() {
        return Variable.of(id());
    }

    @Property
    public abstract WiseConsumer<Dock> register();
}
