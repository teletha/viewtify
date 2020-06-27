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

import javafx.scene.control.TextInputDialog;

import kiss.Signal;
import viewtify.Viewtify;
import viewtify.ui.UIWeb;

public class Browser {

    public static void main(String[] args) {
        Viewtify.browser(web -> {
            web.load("https://lightning.bitflyer.jp")
                    .$(inputByHuman("#LoginId"))
                    .$(inputByHuman("#Password"))
                    .$(click("#login_btn"))
                    .$(awaitContentLoading())
                    .$(detour("https://lightning.bitflyer.jp/Home/TwoFactorAuth", Browser::retrieveAuthCode))
                    .to(() -> {
                        web.stage().get().close();
                        System.out.println("OK " + web.cookie("api_session_v2"));
                    });
        });
    }

    private static Signal<UIWeb> retrieveAuthCode(UIWeb web) {
        String code = new TextInputDialog() // need two-factor authentication code
                .showAndWait()
                .orElseThrow(() -> new IllegalArgumentException("二段階認証の確認コードが間違っています"))
                .trim();

        return web.click("form > label") //
                .$(input("#ConfirmationCode", code))
                .$(click("form > button"))
                .$(awaitContentLoading());
    }
}
