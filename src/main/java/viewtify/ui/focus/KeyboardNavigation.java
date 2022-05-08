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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.TopMostTraversalEngine;

import kiss.I;
import viewtify.Key;
import viewtify.ui.UICheckBox;
import viewtify.ui.UIComboBox;
import viewtify.ui.UIText;
import viewtify.ui.UserInterface;
import viewtify.util.Combi;

public class KeyboardNavigation {

    /** The managed nodes. */
    private final List<List<UserInterface>> table = new ArrayList();

    private Key next = Key.None;

    private Key previous = Key.None;

    private Key nextGroup = Key.None;

    private Key previousGroup = Key.None;

    private Key up = Key.None;

    private Key down = Key.None;

    private Key left = Key.None;

    private Key right = Key.None;

    private boolean canLoop = true;

    private final PopOver pop = new PopOver();

    private final TextNavigator text = new TextNavigator();

    private final ComboBoxNavigator combo = new ComboBoxNavigator();

    private final CheckBoxNavigator checkbox = new CheckBoxNavigator();

    private TopMostTraversalEngine engine;

    /**
     * 
     */
    public KeyboardNavigation() {
        pop.setFadeOutDuration(Duration.ZERO);
        pop.setArrowSize(4);
        pop.setArrowLocation(ArrowLocation.TOP_LEFT);
    }

    /**
     * Set root {@link Node}.
     * 
     * @param ui
     * @return
     */
    public KeyboardNavigation root(UserInterface ui) {
        engine = new TopMostTraversalEngine() {

            /**
             * {@inheritDoc}
             */
            @Override
            protected Parent getRoot() {
                if (ui.ui instanceof Parent) {
                    return (Parent) ui.ui;
                } else {
                    return ui.ui.getParent();
                }
            }
        };
        return this;
    }

    /**
     * Group nodes.
     * 
     * @param ui
     * @return
     */
    public KeyboardNavigation group(UserInterface... ui) {
        table.add(I.list(ui));

        for (UserInterface node : ui) {
            if (node instanceof UIText) {
                text.register((UIText) node);
            } else if (node instanceof UIComboBox) {
                combo.register((UIComboBox) node);
            } else if (node instanceof UICheckBox) {
                checkbox.register((UICheckBox) node);
            }
        }
        return this;
    }

    /**
     * @param key
     */
    public KeyboardNavigation traverseNextBy(Key key) {
        this.next = key;
        return this;
    }

    /**
     * @param key
     */
    public KeyboardNavigation traversePreviousBy(Key key) {
        this.previous = key;
        return this;
    }

    /**
     * @param key
     */
    public KeyboardNavigation traverseNextGroupBy(Key key) {
        this.nextGroup = key;
        return this;
    }

    /**
     * @param key
     */
    public KeyboardNavigation traversePreviousGroupBy(Key key) {
        this.previousGroup = key;
        return this;
    }

    /**
     * @return
     */
    public KeyboardNavigation traversePhysicalLocationByArrowKey() {
        return traversePhysicalLocationBy(Key.Up, Key.Down, Key.Left, Key.Right);
    }

    /**
     * @return
     */
    public KeyboardNavigation traversePhysicalLocationBy(Key up, Key down, Key left, Key right) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
        return this;
    }

    public KeyboardNavigation loopable(boolean enable) {
        this.canLoop = enable;
        return this;
    }

    /**
     * 
     */
    private abstract class Navigator<N> implements EventHandler<KeyEvent> {

        protected N current;

        /**
         * {@inheritDoc}
         */
        @Override
        public void handle(KeyEvent event) {
            if (canNavigate(current, event)) {
                if (next.match(event)) {
                    focusNext(event);
                } else if (previous.match(event)) {
                    focusPrevious(event);
                } else if (nextGroup.match(event)) {
                    focusNextGroup(event);
                } else if (previousGroup.match(event)) {
                    focusPreviousGroup(event);
                } else if (right.match(event)) {
                    focusRight(event);
                } else if (left.match(event)) {
                    focusLeft(event);
                } else if (up.match(event)) {
                    focusUp(event);
                } else if (down.match(event)) {
                    focusDown(event);
                }
            }
        }

        /**
         * Register the specified node.
         * 
         * @param node
         */
        protected abstract void register(N node);

        /**
         * Filter the navigatable key.
         * 
         * @param event
         * @return
         */
        protected boolean canNavigate(N node, KeyEvent event) {
            return true;
        }

        protected void focusNext(KeyEvent event) {
            Node source = (Node) event.getSource();
            engine.trav(source, Direction.NEXT);

            event.consume();
        }

        protected void focusPrevious(KeyEvent event) {
            Node source = (Node) event.getSource();
            engine.trav(source, Direction.PREVIOUS);

            event.consume();
        }

        /**
         * Traverse to the next group.
         */
        protected void focusNextGroup(KeyEvent event) {
            int[] location = locate((Node) event.getSource());
            moveTo(location[0] + 1, 0);

            event.consume();
        }

        /**
         * Traverse to the next group.
         */
        protected void focusPreviousGroup(KeyEvent event) {
            int[] location = locate((Node) event.getSource());
            moveTo(location[0] - 1, 0);

            event.consume();
        }

        /**
         * Traverse to the next form.
         * 
         * @param event
         */
        protected void focusRight(KeyEvent event) {
            Node source = (Node) event.getSource();
            engine.trav(source, Direction.RIGHT);
            event.consume();
        }

        /**
         * Traverse to the previous form.
         * 
         * @param event
         */
        protected void focusLeft(KeyEvent event) {
            Node source = (Node) event.getSource();
            engine.trav(source, Direction.LEFT);
            event.consume();
        }

        /**
         * Traverse to the next form.
         * 
         * @param event
         */
        protected void focusUp(KeyEvent event) {
            Node source = (Node) event.getSource();
            engine.trav(source, Direction.UP);
            event.consume();
        }

        /**
         * Traverse to the previous form.
         * 
         * @param event
         */
        protected void focusDown(KeyEvent event) {
            Node source = (Node) event.getSource();
            engine.trav(source, Direction.DOWN);
            event.consume();
        }

        private int[] locate(Node node) {
            for (int i = 0; i < table.size(); i++) {
                List<UserInterface> row = table.get(i);
                for (int j = 0; j < row.size(); j++) {
                    Node item = row.get(j).ui;
                    if (item == node) {
                        return new int[] {i, j};
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
    }

    /**
     * Special Navigator for {@link TextField}.
     */
    private class TextNavigator extends Navigator<UIText<?>> {

        private final Combi<Integer> caret = new Combi(0);

        private final EventHandler<MouseEvent> captureMouse = e -> {
            caret.set(((TextField) e.getSource()).getCaretPosition());
        };

        /**
         * {@inheritDoc}
         */
        @Override
        protected void register(UIText node) {
            node.ui.focusedProperty().addListener((v, o, n) -> {
                current = node;

                if (n) {
                    caret.reset(0);
                    node.ui.addEventHandler(KeyEvent.KEY_PRESSED, this);
                    node.ui.addEventHandler(MouseEvent.MOUSE_CLICKED, captureMouse);
                } else {
                    node.ui.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    node.ui.removeEventHandler(MouseEvent.MOUSE_CLICKED, captureMouse);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean canNavigate(UIText<?> node, KeyEvent event) {
            caret.set(node.ui.getCaretPosition());

            switch (event.getCode()) {
            case BACK_SPACE:
            case LEFT:
                return caret.previous == 0 && caret.next == 0;

            case RIGHT:
                return caret.previous == node.length() && caret.next == node.length();

            default:
                return true;
            }
        };
    }

    /**
     * Special Navigator for {@link ComboBox}.
     */
    private class ComboBoxNavigator extends Navigator<UIComboBox<?>> {

        /** For combo box. */
        private final EventHandler<KeyEvent> operation = e -> {
            if (Key.Space.match(e)) {
                current.toggle();
            } else if (Key.Numpad1.match(e) || Key.Digit1.match(e)) {
                current.selectAt(0);
                focusNext(e);
            } else if (Key.Numpad2.match(e) || Key.Digit2.match(e)) {
                current.selectAt(1);
                focusNext(e);
            } else if (Key.Numpad3.match(e) || Key.Digit3.match(e)) {
                current.selectAt(2);
                focusNext(e);
            } else if (Key.Numpad4.match(e) || Key.Digit4.match(e)) {
                current.selectAt(3);
                focusNext(e);
            } else if (Key.Numpad5.match(e) || Key.Digit5.match(e)) {
                current.selectAt(4);
                focusNext(e);
            } else if (Key.Numpad6.match(e) || Key.Digit6.match(e)) {
                current.selectAt(5);
                focusNext(e);
            } else if (Key.Numpad7.match(e) || Key.Digit7.match(e)) {
                current.selectAt(6);
                focusNext(e);
            } else if (Key.Numpad8.match(e) || Key.Digit8.match(e)) {
                current.selectAt(7);
                focusNext(e);
            } else if (Key.Numpad9.match(e) || Key.Digit9.match(e)) {
                current.selectAt(8);
                focusNext(e);
            } else if (Key.Numpad0.match(e) || Key.Digit0.match(e)) {
                current.selectAt(9);
                focusNext(e);
            }
        };

        /** For popup list view. */
        private final EventHandler<KeyEvent> popupOperation = e -> {
            if (Key.Enter.match(e)) {
                engine.trav(current.ui, Direction.NEXT);
            }
        };

        /**
         * {@inheritDoc}
         */
        @Override
        protected void register(UIComboBox<?> node) {
            node.ui.focusedProperty().addListener((v, o, n) -> {

                if (n) {
                    current = node;
                    node.ui.addEventFilter(KeyEvent.KEY_PRESSED, this);
                    node.ui.addEventHandler(KeyEvent.KEY_PRESSED, operation);
                    node.listView().addEventFilter(KeyEvent.KEY_PRESSED, popupOperation);
                } else {
                    current = null;
                    node.ui.removeEventFilter(KeyEvent.KEY_PRESSED, this);
                    node.ui.removeEventHandler(KeyEvent.KEY_PRESSED, operation);
                    node.listView().removeEventFilter(KeyEvent.KEY_PRESSED, popupOperation);
                }
            });
        }

    }

    /**
     * Special Navigator for {@link UICheckBox}.
     */
    private class CheckBoxNavigator extends Navigator<UICheckBox> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected void register(UICheckBox node) {
            node.ui.focusedProperty().addListener((v, o, n) -> {
                current = node;

                if (n) {
                    node.ui.addEventFilter(KeyEvent.KEY_PRESSED, this);
                } else {
                    node.ui.removeEventFilter(KeyEvent.KEY_PRESSED, this);
                }
            });
        }
    }
}
