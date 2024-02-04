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
import viewtify.ui.View;

@Icy
public abstract class DockModel {

    @Property
    public abstract Class<? extends View> view();

    @Property
    public abstract WiseConsumer<Dock> registration();

    public String id() {
        return I.make(view()).id();
    }

    public Variable<String> title() {
        return I.make(view()).title();
    }

    @Property
    public UnaryOperator<DockRecommendedLocation> location() {
        return UnaryOperator.identity();
    }

    public void register() {
        registration().accept((Dock) this);
    }

    public static Dock of(Class<? extends View> type) {
        return Dock.with.view(type).registration(dock -> {
            DockSystem.register(dock.id()).text(dock.title()).contentsLazy(tab -> I.make(dock.view()));
        });
    }
}
