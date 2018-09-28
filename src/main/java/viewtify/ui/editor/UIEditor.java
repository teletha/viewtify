/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.editor;

import javafx.scene.web.WebView;

import netscape.javascript.JSObject;
import viewtify.Viewtify;
import viewtify.ui.UserInterface;
import viewtify.ui.View;

/**
 * @version 2018/09/27 17:23:51
 */
public class UIEditor extends UserInterface<UIEditor, WebView> {

    /**
     * @param ui
     * @param view
     */
    private UIEditor(View view) {
        super(new WebView(), view);

        ui.getEngine().load(Viewtify.class.getResource("editor.html").toExternalForm());
    }

    public String text() {
        return (String) ui.getEngine().executeScript("editor.getValue()");
    }

    public int lineNumber() {
        return (int) ui.getEngine().executeScript("editor.session.getLength()");
    }

    public String cusor() {
        JSObject executeScript = (JSObject) ui.getEngine().executeScript("editor.selection.getCursor()");

        return "OKOKO";
    }
}
