/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.util;

import java.lang.reflect.Field;

import com.sun.javafx.scene.control.behavior.TextInputControlBehavior;

import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.skin.TextAreaSkin;
import javafx.scene.control.skin.TextFieldSkin;
import javafx.scene.control.skin.TextInputControlSkin;
import javafx.scene.input.InputMethodEvent;
import kiss.I;
import viewtify.ui.helper.EnhancedContextMenu;

public class MonkeyPatch {

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
        fixContextMenuBehavior(node, skin, TextAreaSkin.class);
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
        fixContextMenuBehavior(node, skin, TextFieldSkin.class);
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

    /**
     * Fix context menu behavior on text node.
     * 
     * @param node
     * @param skin
     */
    private static <T extends TextInputControlSkin> void fixContextMenuBehavior(TextInputControl node, T skin, Class<T> type) {
        try {
            Field behaviorField = type.getDeclaredField("behavior");
            behaviorField.setAccessible(true);
            TextInputControlBehavior behavior = (TextInputControlBehavior) behaviorField.get(skin);
            Field contextField = TextInputControlBehavior.class.getDeclaredField("contextMenu");
            contextField.setAccessible(true);
            ContextMenu context = (ContextMenu) contextField.get(behavior);

            EnhancedContextMenu enhanced = new EnhancedContextMenu();
            enhanced.getItems().addAll(context.getItems());

            contextField.set(behavior, enhanced);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }
}