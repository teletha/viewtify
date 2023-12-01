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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
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
        String[] classes = {".label", ".button", ".check-box", ".hyperlink", " .toggle-button", ".cell"};
        boolean precondition = text == null || text.isBlank() || category().exact().contains(text);

        int shown = 0;
        Set<Node> shownDescriptions = new HashSet();

        List<Node> rows = new ArrayList();
        rows.addAll(ui().lookupAll(FormStyles.Row.selector()));
        rows.addAll(ui().lookupAll(".table-view"));

        for (Node row : rows) {
            Variable<Node> description = findDescription(row);
            if (precondition || searchLabel(row, text, classes) || searchCombo(row, text) || searchDescription(description, text, classes)) {
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
                if (node instanceof Labeled labeled && labeled.getText() != null && labeled.getText().toLowerCase().contains(text)) {
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

    private boolean searchDescription(Variable<Node> description, String text, String... classes) {
        if (description.isAbsent()) {
            return false;
        }

        for (String clazz : classes) {
            for (Node node : description.v.lookupAll(clazz)) {
                if (node instanceof Labeled labeled && labeled.getText().toLowerCase().contains(text)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Variable<Node> findDescription(Node row) {
        Node target = row;
        Parent node = target.getParent();
        while (node != null && !node.getStyleClass().contains(FormStyles.Preferences.className()[0])) {
            ObservableList<Node> children = node.getChildrenUnmodifiable();
            for (int i = children.indexOf(target) - 1; 0 <= i; i--) {
                Node child = children.get(i);
                ObservableList<String> classes = child.getStyleClass();
                if (classes.contains(FormStyles.Description.className()[0])) {
                    return Variable.of(child);
                }
            }
            target = node;
            node = node.getParent();
        }
        return Variable.empty();
    }
}
