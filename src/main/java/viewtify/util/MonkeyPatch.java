/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import java.lang.reflect.Field;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.control.skin.TextInputControlSkin;
import javafx.scene.input.InputMethodEvent;

import kiss.I;

public class MonkeyPatch {

    /**
     * Apply all patches to the specified {@link Node} and its children.
     * 
     * @param node
     */
    public static void applyAll(Node node) {
        I.signal(node.lookupAll(".text-area")).startWith(node).as(TextArea.class).to(MonkeyPatch::fix);
        I.signal(node.lookupAll(".text-field")).startWith(node).as(TextField.class).to(MonkeyPatch::fix);
    }

    /**
     * Fix {@link TextArea}.
     * 
     * @param node
     */
    public static void fix(TextArea node) {
        TextAreaSkin skin = (TextAreaSkin) node.getSkin();
        if (skin == null) {
            node.setSkin(skin = new TextAreaSkin(node));
        }
        fixIMEBehavior(node, skin);
    }

    /**
     * Fix {@link TextField}.
     * 
     * @param node
     */
    public static void fix(TextField node) {
        TextFieldSkin skin = (TextFieldSkin) node.getSkin();
        if (skin == null) {
            node.setSkin(skin = new TextFieldSkin(node));
        }
        fixIMEBehavior(node, skin);
    }

    /**
     * Fix ime behavior on text node.
     * 
     * @param node
     * @param skin
     */
    private static void fixIMEBehavior(TextInputControl node, TextInputControlSkin skin) {
        try {
            Field field = TextInputControlSkin.class.getDeclaredField("inputMethodTextChangedHandler");
            field.setAccessible(true);

            EventHandler<InputMethodEvent> ime = (EventHandler<InputMethodEvent>) field.get(skin);
            node.setOnInputMethodTextChanged(ime);
            node.removeEventHandler(InputMethodEvent.INPUT_METHOD_TEXT_CHANGED, ime);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }
}
