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
import kiss.Variable;
import kiss.WiseConsumer;
import viewtify.ui.UITab;
import viewtify.ui.View;

@Icy
public abstract class DockModel {

    @Property
    public abstract String id();

    @Property
    public abstract Variable<String> title();

    @Property
    public abstract WiseConsumer<UITab> content();

    @Property
    public UnaryOperator<DockRecommendedLocation> location() {
        return UnaryOperator.identity();
    }

    public static Dock of(Class<? extends View> type) {
        View view = I.make(type);
        return Dock.with.id(view.id()).content(tab -> tab.contentsLazy(type)).title(view.title());
    }
}
