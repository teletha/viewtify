/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.view;

import java.util.Set;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Labeled;
import kiss.Extensible;
import kiss.Variable;
import viewtify.style.FormStyles;
import viewtify.ui.View;

public abstract class PreferenceViewBase extends View implements Extensible {

    /**
     * Define the category of preferences.
     * 
     * @return
     */
    public abstract Variable<String> category();

    /**
     * @param text
     */
    void searchPreferenceBy(String text) {
        boolean precondition = text == null || text.isBlank() || category().exact().contains(text);

        int shown = 0;
        Set<Node> nodes = ui().lookupAll(FormStyles.Row.selector());
        for (Node row : nodes) {
            if (precondition || searchLabel(row, text, ".label", ".button", ".check-box", ".hyperlink", " .toggle-button") || searchCombo(row, text)) {
                row.setManaged(true);
                row.setVisible(true);
                shown++;
            } else {
                row.setManaged(false);
                row.setVisible(false);
            }
        }

        Node title = ui().getParent();
        title.setVisible(precondition || shown != 0);
        title.setManaged(precondition || shown != 0);
    }

    private boolean searchLabel(Node row, String text, String... classes) {
        for (String clazz : classes) {
            for (Node node : row.lookupAll(clazz)) {
                if (node instanceof Labeled labeled) {
                    return labeled.getText().toLowerCase().contains(text);
                }
            }
        }
        return false;
    }

    private boolean searchCombo(Node row, String text, String... classes) {
        for (Node node : row.lookupAll(".combo-box")) {
            if (node instanceof ComboBox combo) {
                for (Object object : combo.getItems()) {
                    if (object.toString().toLowerCase().contains(text)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
