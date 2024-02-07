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
import icy.manipulator.Icy.Overload;
import icy.manipulator.Icy.Property;
import kiss.I;
import kiss.Variable;
import kiss.WiseConsumer;
import viewtify.ui.View;

@Icy
public abstract class DockModel {

    /**
     * Set the View class to be displayed.
     * 
     * @return
     */
    @Property
    public abstract Class<? extends View> view();

    /**
     * Sets the behaviour when actually displayed on the tab.
     * 
     * @return
     */
    @Property
    public WiseConsumer<Dock> registration() {
        return dock -> DockSystem.register(dock.id()).text(dock.title()).contentsLazy(tab -> I.make(dock.view()));
    }

    /**
     * Determines the area to be displayed when showing.
     * 
     * @return
     */
    @Property
    public UnaryOperator<DockRecommendedLocation> location() {
        return UnaryOperator.identity();
    }

    /**
     * Sets whether or not to display this information during the initial layout.
     * 
     * @return
     */
    @Property
    public boolean initialView() {
        return false;
    }

    /**
     * Set as the View to be displayed during the initial layout.
     * 
     * @return
     */
    @Overload("initialView")
    private boolean showOnInitial() {
        return true;
    }

    public String id() {
        return I.make(view()).id();
    }

    public Variable<String> title() {
        return I.make(view()).title();
    }

    /**
     * Show view.
     */
    public void show() {
        registration().accept((Dock) this);
    }
}
