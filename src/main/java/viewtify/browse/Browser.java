/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.browse;

import viewtify.Viewtify;
import viewtify.ui.UIWeb;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class Browser extends View {

    UIWeb web;

    class view extends ViewDSL {
        {
            $(web);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        web.load("https://www.google.co.jp/")
                .flatMap(w -> web.input(".gLFyf", "test"))
                .flatMap(w -> web.click(".gNO89b"))
                .flatMap(w -> web.awaitContentLoading())
                .flatMap(w -> web.input(".gLFyf", "reset"))
                .flatMap(w -> web.text(".LC20lb"))
                .to(e -> {
                    System.out.println("OK :" + e);
                }, e -> {
                    e.printStackTrace();
                }, () -> {
                    System.out.println("Complete");
                });
    }

    public static void main(String[] args) {
        Viewtify.application().activate(Browser.class);
    }
}
