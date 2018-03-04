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

import javafx.scene.control.TabPane.TabClosingPolicy;

import kiss.I;
import viewtify.ActivationPolicy;
import viewtify.UI;
import viewtify.View;
import viewtify.Viewtify;
import viewtify.ui.UITabPane;

/**
 * @version 2018/03/04 16:04:31
 */
public class Toybox2 extends View {

    private @UI UITabPane main;

    private final Consoles consoles = I.make(Consoles.class);

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        main.model(consoles, ConsoleView::new).policy(TabClosingPolicy.ALL_TABS).keybind("ctrl+t", consoles::createConsole);
    }

    /**
     * Entry point.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Viewtify.activate(Toybox2.class, ActivationPolicy.Latest);
    }
}
