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

import java.util.function.UnaryOperator;

import icy.manipulator.Icy;
import icy.manipulator.Icy.Property;
import kiss.I;
import kiss.WiseBiConsumer;
import viewtify.ui.UITab;

@Icy
public abstract class TypedDockModel<T> {

    @Property
    public abstract String id();

    @Property
    public abstract WiseBiConsumer<UITab, T> registration();

    @Property
    public UnaryOperator<DockRecommendedLocation> location() {
        return UnaryOperator.identity();
    }

    public void register(T param) {
        UITab tab = DockSystem.register(id() + " " + I.transform(param, String.class));
        registration().accept(tab, param);
    }
}
