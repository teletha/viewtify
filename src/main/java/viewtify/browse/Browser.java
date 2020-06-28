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

import static viewtify.ui.UIWeb.Operation.*;

import viewtify.Viewtify;

public class Browser {

    public static void main(String[] args) {
        Viewtify.browser(browser -> {
            browser.load("https://lightning.bitflyer.jp")
                    .$(inputByHuman("#LoginId", "ログインIDは？"))
                    .$(inputByHuman("#Password", "パスパードは？"))
                    .$(click("#login_btn"))
                    .$(awaitContentLoading())
                    .to(() -> {
                        browser.stage().get().close();
                    });
        });
    }
}
