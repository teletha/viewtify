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

import java.util.HashSet;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
        Set<Node> shownDescriptions = new HashSet();

        for (Node row : ui().lookupAll(FormStyles.Row.selector())) {
            Variable<Node> description = findDescription(row);
            if (precondition || searchLabel(row, text, ".label", ".button", ".check-box", ".hyperlink", " .toggle-button") || searchCombo(row, text) || searchDescription(description, text)) {
                shown++;
                row.setManaged(true);
                row.setVisible(true);
                description.to(x -> {
                    x.setManaged(true);
                    x.setVisible(true);
                    shownDescriptions.add(x);
                });
            } else {
                row.setManaged(false);
                row.setVisible(false);
                description.to(x -> {
                    if (!shownDescriptions.contains(x)) {
                        x.setManaged(false);
                        x.setVisible(false);
                    }
                });
            }
        }

        Node title = ui().getParent();
        title.setVisible(precondition || shown != 0);
        title.setManaged(precondition || shown != 0);
    }

    private boolean searchLabel(Node row, String text, String... classes) {
        for (String clazz : classes) {
            for (Node node : row.lookupAll(clazz)) {
                if (node instanceof Labeled labeled && labeled.getText().toLowerCase().contains(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean searchCombo(Node row, String text) {
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

    private boolean searchDescription(Variable<Node> description, String text) {
        if (description.isAbsent()) {
            return false;
        }

        for (Node node : description.v.lookupAll(".label")) {
            if (node instanceof Label label && label.getText().toLowerCase().contains(text)) {
                return true;
            }
        }
        return false;
    }

    private Variable<Node> findDescription(Node row) {
        ObservableList<Node> children = row.getParent().getChildrenUnmodifiable();
        for (int i = children.indexOf(row) - 1; 0 <= i; i--) {
            Node child = children.get(i);
            ObservableList<String> classes = child.getStyleClass();
            if (classes.contains(FormStyles.Description.className()[0])) {
                return Variable.of(child);
            }
        }
        return Variable.empty();
    }
}
