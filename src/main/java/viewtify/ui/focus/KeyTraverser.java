/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.focus;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import org.controlsfx.control.PopOver;

import kiss.I;
import kiss.Variable;
import kiss.Ⅱ;
import viewtify.Key;
import viewtify.ui.UIComboBox;
import viewtify.ui.UIText;
import viewtify.ui.UserInterface;
import viewtify.ui.helper.User;

public class KeyTraverser {

    /** The managed nodes. */
    private final List<List<UserInterface>> table = new ArrayList();

    private Key next;

    private Key previous;

    private Key nextGroup;

    private Key previousGroup;

    private Key up;

    private Key down;

    private Key left;

    private Key right;

    private boolean canLoop = true;

    private PopOver pop = new PopOver();

    /**
     * Key handling.
     */
    private EventHandler<KeyEvent> navigator = e -> {
        Node n = (Node) e.getSource();

        if (next != null && next.match(e)) {
            int[] location = locate((Node) e.getSource());
            moveTo(location[0], location[1] + 1);
            e.consume();
        } else if (previous != null && previous.match(e)) {
            int[] location = locate((Node) e.getSource());
            moveTo(location[0], location[1] - 1);
            e.consume();
        } else if (nextGroup != null && nextGroup.match(e)) {
            focusNextGroup((Node) e.getSource());
            e.consume();
        } else if (previousGroup != null && previousGroup.match(e)) {
            int[] location = locate((Node) e.getSource());
            moveTo(location[0] - 1, 0);
            e.consume();
        } else if (up != null && up.match(e)) {
            Ⅱ<List<UserInterface>, Integer> row = locateRow((Node) e.getSource());
            double relative = computeRelativeHorizon(row.ⅰ, (Node) e.getSource());
            int index = table.indexOf(row.ⅰ);
            if (index != 0) {
                List<UserInterface> up = table.get(index - 1);
                UserInterface near = computeNear(up, relative);
                near.ui().requestFocus();
                e.consume();
            }
        } else if (down != null && down.match(e)) {
            Ⅱ<List<UserInterface>, Integer> row = locateRow((Node) e.getSource());
            double relative = computeRelativeHorizon(row.ⅰ, (Node) e.getSource());
            int index = table.indexOf(row.ⅰ);
            if (index < table.size() - 1) {
                List<UserInterface> down = table.get(index + 1);
                UserInterface near = computeNear(down, relative);
                near.ui().requestFocus();
                e.consume();
            }
        } else if (left != null && left.match(e)) {
            Ⅱ<List<UserInterface>, Integer> row = locateRow(n);
            if (0 < row.ⅱ) row.ⅰ.get(row.ⅱ - 1).ui().requestFocus();
            e.consume();
        } else if (right != null && right.match(e)) {
            Ⅱ<List<UserInterface>, Integer> row = locateRow(n);
            if (row.ⅱ < row.ⅰ.size() - 1) row.ⅰ.get(row.ⅱ + 1).ui().requestFocus();
            e.consume();
        }
    };

    private int[] locate(Node node) {
        for (int i = 0; i < table.size(); i++) {
            List<UserInterface> row = table.get(i);
            for (int j = 0; j < row.size(); j++) {
                Node item = row.get(j).ui();
                if (item == node) {
                    return new int[] {i, j};
                }
            }
        }
        throw new Error();
    }

    private Ⅱ<List<UserInterface>, Integer> locateRow(Node node) {
        for (int i = 0; i < table.size(); i++) {
            List<UserInterface> row = table.get(i);
            for (int j = 0; j < row.size(); j++) {
                if (node == row.get(j).ui) {
                    return I.pair(row, j);
                }
            }
        }
        throw new Error();
    }

    private void moveTo(int rowIndex, int columnIndex) {
        if (rowIndex < 0) {
            rowIndex = canLoop ? table.size() - 1 : 0;
        } else if (table.size() <= rowIndex) {
            rowIndex = canLoop ? 0 : table.size() - 1;
        }

        List<UserInterface> row = table.get(rowIndex);

        if (columnIndex < 0) {
            moveTo(rowIndex - 1, 0);
        } else if (row.size() <= columnIndex) {
            moveTo(rowIndex + 1, 0);
        } else {
            row.get(columnIndex).ui.requestFocus();
        }
    }

    /**
     * @param row
     * @param source
     * @return
     */
    private double computeRelativeHorizon(List<UserInterface> row, Node source) {
        double min = 0;
        for (int i = 0; i < row.size(); i++) {
            min = Math.min(min, row.get(i).ui().localToScene(row.get(i).ui().getBoundsInLocal()).getMinX());
        }
        return source.localToScene(source.getBoundsInLocal()).getCenterX() - min;
    }

    /**
     * @param up2
     * @param relative
     * @return
     */
    private UserInterface computeNear(List<UserInterface> row, double relative) {
        double min = 0;
        for (int i = 0; i < row.size(); i++) {
            min = Math.min(min, row.get(i).ui().localToScene(row.get(i).ui().getBoundsInLocal()).getMinX());
        }

        relative += min;

        UserInterface mn = null;
        double m = Double.MAX_VALUE;

        for (int i = 0; i < row.size(); i++) {
            double x = Math.abs(row.get(i).ui().localToScene(row.get(i).ui().getBoundsInLocal()).getCenterX() - min - relative);

            if (x < m) {
                m = x;
                mn = row.get(i);
            }
        }
        return mn;
    }

    /**
     * Group nodes.
     * 
     * @param ui
     * @return
     */
    public KeyTraverser group(UserInterface... ui) {
        table.add(I.list(ui));

        for (UserInterface<?, Node> userInterface : ui) {
            // userInterface.when(User.KeyPress, e -> navigator.handle(e));

            if (userInterface instanceof UIText) {
                register((UIText) userInterface);
            } else if (userInterface instanceof UIComboBox) {
                register((UIComboBox) userInterface);
            }
        }
        return this;
    }

    private static class FocusedTextInput {

        private final Variable<Integer> caret = Variable.empty();

        private final Variable<Integer> selection = Variable.empty();

        private final ChangeListener<ObservableValue<Integer>, Integer, Integer> observeCaretPosition = null;

        private void register(TextField node) {
            node.focusedProperty().addListener((v, o, n) -> {
                if (n) {
                    focused(node);
                } else {
                    unfocused(node);
                }
            });
        }

        private void focused(TextField node) {
            node.caretPositionProperty().addListener(observeCaretPosition);
        }

        private void unfocused(TextField node) {

        }

        private void observeCaretPosition() {

        }

        private void observeSelectionRange() {

        }
    }

    /**
     * Specialization.
     * 
     * @param node
     */
    private void register(UIText<?> ui) {
        Variable<Integer> position = Variable.of(ui.ui.getCaretPosition());

        ui.when(User.KeyRelease, e -> {
            int oldPosition = position.v;
            int newPosition = ui.ui.getCaretPosition();
            position.set(newPosition);

            switch (e.getCode()) {
            case BACK_SPACE:
            case LEFT:
                if (oldPosition == 0 && newPosition == 0) {
                    traversePrevious(ui);
                }
                break;

            case RIGHT:
                if (oldPosition == ui.length() && newPosition == ui.length()) {
                    traverseNext(ui);
                }
                break;

            default:
                break;
            }

            if (e.getCode() == KeyCode.BACK_SPACE) {

            }
            System.out.println(oldPosition + "  " + newPosition);
        });
    }

    /**
     * Specialization.
     * 
     * @param node
     */
    private void register(UIComboBox<?> ui) {

    }

    public KeyTraverser traverseNext(UserInterface ui) {
        System.out.println("Next");
        return this;
    }

    public KeyTraverser traversePrevious(UserInterface ui) {
        System.out.println("Previous");
        return this;
    }

    /**
     * @param key
     */
    public KeyTraverser traverseNextBy(Key key) {
        this.next = key;
        return this;
    }

    /**
     * @param key
     */
    public KeyTraverser traversePreviousBy(Key key) {
        this.previous = key;
        return this;
    }

    /**
     * @param key
     */
    public KeyTraverser traverseNextGroupBy(Key key) {
        this.nextGroup = key;
        return this;
    }

    /**
     * @param key
     */
    public KeyTraverser traversePreviousGroupBy(Key key) {
        this.previousGroup = key;
        return this;
    }

    /**
     * @return
     */
    public KeyTraverser traversePhysicalLocationByArrowKey() {
        return traversePhysicalLocationBy(Key.Up, Key.Down, Key.Left, Key.Right);
    }

    /**
     * @return
     */
    public KeyTraverser traversePhysicalLocationBy(Key up, Key down, Key left, Key right) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        return this;
    }

    public KeyTraverser loopable(boolean enable) {
        this.canLoop = enable;
        return this;
    }

    /**
     * Traverse to the next group.
     */
    public void focusNextGroup(Node base) {
        int[] location = locate(base);
        moveTo(location[0] + 1, 0);
    }
}
