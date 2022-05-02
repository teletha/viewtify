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

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import kiss.I;
import viewtify.Key;
import viewtify.ui.UserInterface;

public class Traverser {

    /** The managed nodes. */
    private final List<List<Node>> table = new ArrayList();

    private Key next;

    private Key previous;

    private Key nextGroup;

    private Key previousGroup;

    private Key up;

    private Key down;

    private Key left;

    private Key right;

    private boolean canLoop = true;

    /**
     * Key handling.
     */
    private EventHandler<KeyEvent> listener = e -> {
        Node n = (Node) e.getSource();

        if (n instanceof TextField == false) {
            return;
        }

        if (next != null && next.match(e)) {
            int[] location = locate((Node) e.getSource());
            moveTo(location[0], location[1] + 1);
        } else if (previous != null && previous.match(e)) {
            int[] location = locate((Node) e.getSource());
            moveTo(location[0], location[1] - 1);
        } else if (nextGroup != null && nextGroup.match(e)) {
            focusNextGroup((Node) e.getSource());
        } else if (previousGroup != null && previousGroup.match(e)) {
            int[] location = locate((Node) e.getSource());
            moveTo(location[0] - 1, 0);
        } else if (up != null && up.match(e)) {
            List<Node> row = locateRow((Node) e.getSource());
            double relative = computeRelativeHorizon(row, (Node) e.getSource());
            int index = table.indexOf(row);
            if (index != 0) {
                List<Node> up = table.get(index - 1);
                Node near = computeNear(up, relative);
                near.requestFocus();
            }
        } else if (down != null && down.match(e)) {
            List<Node> row = locateRow((Node) e.getSource());
            double relative = computeRelativeHorizon(row, (Node) e.getSource());
            int index = table.indexOf(row);
            if (index < table.size() - 1) {
                List<Node> down = table.get(index + 1);
                Node near = computeNear(down, relative);
                near.requestFocus();
            }
        } else if (left != null && left.match(e)) {
            List<Node> row = locateRow(n);
            int index = row.indexOf(n);
            if (0 < index) row.get(index - 1).requestFocus();
        } else if (right != null && right.match(e)) {
            List<Node> row = locateRow(n);
            int index = row.indexOf(n);
            if (index < row.size() - 1) row.get(index + 1).requestFocus();
        }
    };

    private void moveTo(double x, double y) {
        for (int i = 0; i < table.size(); i++) {
            List<Node> row = table.get(i);
            for (int j = 0; j < row.size(); j++) {
                Node item = row.get(j);
                Bounds b = item.localToScene(item.getBoundsInLocal());
                if (b.contains(x, y)) {
                    item.requestFocus();
                    return;
                }
            }
        }
    }

    private int[] locate(Node node) {
        for (int i = 0; i < table.size(); i++) {
            List<Node> row = table.get(i);
            for (int j = 0; j < row.size(); j++) {
                Node item = row.get(j);
                if (item == node) {
                    return new int[] {i, j};
                }
            }
        }
        throw new Error();
    }

    private List<Node> locateRow(Node node) {
        for (int i = 0; i < table.size(); i++) {
            List<Node> row = table.get(i);
            for (int j = 0; j < row.size(); j++) {
                Node item = row.get(j);
                if (item == node) {
                    return row;
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

        List<Node> row = table.get(rowIndex);

        if (columnIndex < 0) {
            moveTo(rowIndex - 1, 0);
        } else if (row.size() <= columnIndex) {
            moveTo(rowIndex + 1, 0);
        } else {
            row.get(columnIndex).requestFocus();;
        }
    }

    /**
     * @param row
     * @param source
     * @return
     */
    private double computeRelativeHorizon(List<Node> row, Node source) {
        double min = 0;
        for (int i = 0; i < row.size(); i++) {
            min = Math.min(min, row.get(i).localToScene(row.get(i).getBoundsInLocal()).getMinX());
        }
        return source.localToScene(source.getBoundsInLocal()).getCenterX() - min;
    }

    /**
     * @param up2
     * @param relative
     * @return
     */
    private Node computeNear(List<Node> row, double relative) {
        double min = 0;
        for (int i = 0; i < row.size(); i++) {
            min = Math.min(min, row.get(i).localToScene(row.get(i).getBoundsInLocal()).getMinX());
        }

        relative += min;

        Node mn = null;
        double m = Double.MAX_VALUE;

        for (int i = 0; i < row.size(); i++) {
            double x = Math.abs(row.get(i).localToScene(row.get(i).getBoundsInLocal()).getCenterX() - min - relative);

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
     * @param nodes
     * @return
     */
    public Traverser group(Node... nodes) {
        table.add(I.list(nodes));
        for (Node node : nodes) {
            node.addEventHandler(KeyEvent.KEY_PRESSED, listener);
        }
        return this;
    }

    /**
     * Group nodes.
     * 
     * @param ui
     * @return
     */
    public Traverser group(UserInterface... ui) {
        Node[] nodes = new Node[ui.length];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = ui[i].ui;
        }
        return group(nodes);
    }

    /**
     * @param key
     */
    public Traverser traverseNextBy(Key key) {
        this.next = key;
        return this;
    }

    /**
     * @param key
     */
    public Traverser traversePreviousBy(Key key) {
        this.previous = key;
        return this;
    }

    /**
     * @param key
     */
    public Traverser traverseNextGroupBy(Key key) {
        this.nextGroup = key;
        return this;
    }

    /**
     * @param key
     */
    public Traverser traversePreviousGroupBy(Key key) {
        this.previousGroup = key;
        return this;
    }

    /**
     * @return
     */
    public Traverser traversePhysicalLocationByArrowKey() {
        return traversePhysicalLocationBy(Key.Up, Key.Down, Key.Left, Key.Right);
    }

    /**
     * @return
     */
    public Traverser traversePhysicalLocationBy(Key up, Key down, Key left, Key right) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        return this;
    }

    public Traverser loopable(boolean enable) {
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
