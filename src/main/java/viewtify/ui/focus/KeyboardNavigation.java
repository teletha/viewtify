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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import com.sun.javafx.scene.traversal.Direction;
import com.sun.javafx.scene.traversal.TopMostTraversalEngine;
import com.sun.javafx.scene.traversal.TraversalMethod;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import kiss.I;
import viewtify.Key;
import viewtify.ui.UICheckBox;
import viewtify.ui.UIComboBox;
import viewtify.ui.UISpinner;
import viewtify.ui.UIText;
import viewtify.ui.UserInterface;
import viewtify.ui.View;

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

    private boolean focusable;

    private final PopOver pop = new PopOver();

    private final TextNavigator text = new TextNavigator();

    private final ComboBoxNavigator combo = new ComboBoxNavigator();

    private final CheckBoxNavigator checkbox = new CheckBoxNavigator();

    private final SpinnerNavigator spinner = new SpinnerNavigator();

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
     * Configure root node.
     * 
     * @param root
     * @return
     */
    public KeyboardNavigation root(UserInterface root) {
        return root(root.ui());
    }

    /**
     * Configure root node.
     * 
     * @param root
     * @return
     */
    public KeyboardNavigation root(View root) {
        return root(root.ui()).group(root);
    }

    /**
     * Configure root node.
     * 
     * @param root
     * @return
     */
    public KeyboardNavigation root(Node root) {
        engine = new TopMostTraversalEngine() {

            /**
             * {@inheritDoc}
             */
            @Override
            protected Parent getRoot() {
                if (root instanceof Parent) {
                    return (Parent) root;
                } else {
                    return root.getParent();
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
            System.out.println(node);
            if (node instanceof UIText) {
                text.register((UIText) node);
            } else if (node instanceof UIComboBox) {
                combo.register((UIComboBox) node);
            } else if (node instanceof UICheckBox) {
                checkbox.register((UICheckBox) node);
            } else if (node instanceof UISpinner) {
                spinner.register((UISpinner) node);
            }
        }
        return this;
    }

    /**
     * Group nodes.
     * 
     * @param view
     * @return
     */
    public KeyboardNavigation group(View view) {
        try {
            for (Field field : view.getClass().getFields()) {
                Class<?> type = field.getType();
                if (View.class.isAssignableFrom(type)) {
                    group((View) field.get(view));
                } else if (UserInterface.class.isAssignableFrom(type)) {
                    group((UserInterface) field.get(view));
                }
            }
        } catch (Exception e) {
            throw I.quiet(e);
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

    /**
     * Configure loopable UI.
     * 
     * @param enable
     * @return
     */
    public KeyboardNavigation loopable(boolean enable) {
        this.canLoop = enable;
        return this;
    }

    /**
     * Configure auto focusable UI.
     * 
     * @param enable
     * @return
     */
    public KeyboardNavigation focusable(boolean enable) {
        this.focusable = enable;
        return this;
    }

    /**
     * Focust first UI.
     * 
     * @return
     */
    public KeyboardNavigation focusFirst() {
        if (!table.isEmpty()) {
            List<UserInterface> nest = table.get(0);
            if (!nest.isEmpty()) {
                nest.get(0).focus();
                System.out.println("focust " + nest.get(0).ui);
            }
        }
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
                Node node = (Node) event.getSource();

                if (next.match(event)) {
                    focusNext(node);
                    event.consume();
                } else if (previous.match(event)) {
                    focusPrevious(node);
                    event.consume();
                } else if (nextGroup.match(event)) {
                    focusNextGroup(node);
                    event.consume();
                } else if (previousGroup.match(event)) {
                    focusPreviousGroup(node);
                    event.consume();
                } else if (right.match(event)) {
                    focusRight(node);
                    event.consume();
                } else if (left.match(event)) {
                    focusLeft(node);
                    event.consume();
                } else if (up.match(event)) {
                    focusUp(node);
                    event.consume();
                } else if (down.match(event)) {
                    focusDown(node);
                    event.consume();
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

        protected void focusNext(Node source) {
            Platform.runLater(() -> {
                engine.trav(source, Direction.NEXT, TraversalMethod.DEFAULT);
            });
        }

        protected void focusPrevious(Node source) {
            Platform.runLater(() -> {
                engine.trav(source, Direction.PREVIOUS, TraversalMethod.DEFAULT);
            });
        }

        /**
         * Traverse to the next group.
         */
        protected void focusNextGroup(Node source) {
            Platform.runLater(() -> {
                int[] location = locate(source);
                moveTo(location[0] + 1, 0);
            });
        }

        /**
         * Traverse to the next group.
         */
        protected void focusPreviousGroup(Node source) {
            Platform.runLater(() -> {
                int[] location = locate(source);
                moveTo(location[0] - 1, 0);
            });
        }

        /**
         * Traverse to the next form.
         * 
         * @param source
         */
        protected void focusRight(Node source) {
            Platform.runLater(() -> {
                engine.trav(source, Direction.RIGHT, TraversalMethod.DEFAULT);
            });
        }

        /**
         * Traverse to the previous form.
         * 
         * @param source
         */
        protected void focusLeft(Node source) {
            Platform.runLater(() -> {
                engine.trav(source, Direction.LEFT, TraversalMethod.DEFAULT);
            });
        }

        /**
         * Traverse to the next form.
         * 
         * @param source
         */
        protected void focusUp(Node source) {
            Platform.runLater(() -> {
                engine.trav(source, Direction.UP, TraversalMethod.DEFAULT);
            });
        }

        /**
         * Traverse to the previous form.
         * 
         * @param source
         */
        protected void focusDown(Node source) {
            Platform.runLater(() -> {
                engine.trav(source, Direction.DOWN, TraversalMethod.DEFAULT);
            });
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

        /**
         * {@inheritDoc}
         */
        @Override
        protected void register(UIText<?> node) {
            node.ui.focusedProperty().addListener((v, o, n) -> {
                current = node;

                if (n) {
                    node.ui.addEventFilter(KeyEvent.KEY_PRESSED, this);
                    node.ui.textProperty().addListener(autofocus);
                } else {
                    node.ui.removeEventFilter(KeyEvent.KEY_PRESSED, this);
                    node.ui.textProperty().removeListener(autofocus);
                }
            });
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean canNavigate(UIText<?> node, KeyEvent event) {
            switch (event.getCode()) {
            case BACK_SPACE:
            case LEFT:
                if (node.isTextSelected()) {
                    return false;
                } else {
                    return node.ui.getCaretPosition() == 0;
                }

            case RIGHT:
                if (node.isTextSelected()) {
                    return false;
                } else {
                    return node.ui.getCaretPosition() == node.length();
                }

            default:
                return true;
            }
        }

        /**
         * Support to focus the next input automatically.
         */
        private final ChangeListener<Object> autofocus = (v, o, n) -> {
            if (focusable && !o.equals(n) && current.verifier().isValid()) {
                int max = current.maximumInput();
                if (0 < max && max <= current.length()) {
                    focusNext(current.ui);
                }
            }
        };
    }

    /**
     * Special Navigator for {@link ComboBox}.
     */
    private class ComboBoxNavigator extends Navigator<UIComboBox<?>> {

        /** For combo box. */
        private final EventHandler<KeyEvent> operation = e -> {
            Node node = (Node) e.getSource();

            if (Key.Space.match(e)) {
                current.toggle();
            } else if (Key.Numpad1.match(e) || Key.Digit1.match(e)) {
                current.selectAt(0);
                if (focusable) focusNext(node);
            } else if (Key.Numpad2.match(e) || Key.Digit2.match(e)) {
                current.selectAt(1);
                if (focusable) focusNext(node);
            } else if (Key.Numpad3.match(e) || Key.Digit3.match(e)) {
                current.selectAt(2);
                if (focusable) focusNext(node);
            } else if (Key.Numpad4.match(e) || Key.Digit4.match(e)) {
                current.selectAt(3);
                if (focusable) focusNext(node);
            } else if (Key.Numpad5.match(e) || Key.Digit5.match(e)) {
                current.selectAt(4);
                if (focusable) focusNext(node);
            } else if (Key.Numpad6.match(e) || Key.Digit6.match(e)) {
                current.selectAt(5);
                if (focusable) focusNext(node);
            } else if (Key.Numpad7.match(e) || Key.Digit7.match(e)) {
                current.selectAt(6);
                if (focusable) focusNext(node);
            } else if (Key.Numpad8.match(e) || Key.Digit8.match(e)) {
                current.selectAt(7);
                if (focusable) focusNext(node);
            } else if (Key.Numpad9.match(e) || Key.Digit9.match(e)) {
                current.selectAt(8);
                if (focusable) focusNext(node);
            }
        };

        /** For popup list view. */
        private final EventHandler<KeyEvent> popupOperation = e -> {
            if (Key.Enter.match(e)) {
                engine.trav(current.ui, Direction.NEXT, TraversalMethod.DEFAULT);
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
                    node.ui.addEventHandler(KeyEvent.KEY_PRESSED, operation);
                } else {
                    node.ui.removeEventFilter(KeyEvent.KEY_PRESSED, this);
                    node.ui.removeEventHandler(KeyEvent.KEY_PRESSED, operation);
                }
            });
        }

        /**
         * Keyboard operation.
         */
        private final EventHandler<KeyEvent> operation = e -> {
            switch (e.getCode()) {
            case DIGIT0:
            case NUMPAD0:
                current.value(false);
                if (focusable) focusNext(current.ui);
                break;

            case DIGIT1:
            case NUMPAD1:
                current.value(true);
                if (focusable) focusNext(current.ui);
                break;

            default:
                break;
            }
        };
    }

    /**
     * Special Navigator for {@link UISpinner}.
     */
    private class SpinnerNavigator extends Navigator<UISpinner> {

        /**
         * {@inheritDoc}
         */
        @Override
        protected void register(UISpinner node) {
            node.ui.focusedProperty().addListener((v, o, n) -> {
                current = node;
                if (n) {
                    node.ui.addEventFilter(KeyEvent.KEY_PRESSED, this);
                    node.ui.addEventFilter(KeyEvent.KEY_PRESSED, operation);

                } else {
                    node.ui.removeEventFilter(KeyEvent.KEY_PRESSED, this);
                    node.ui.removeEventFilter(KeyEvent.KEY_PRESSED, operation);
                }
            });
        }

        /** For combo box. */
        private final EventHandler<KeyEvent> operation = e -> {
            Node node = (Node) e.getSource();

            if (Key.Numpad1.match(e) || Key.Digit1.match(e)) {
                current.value(1);
                if (focusable) focusNext(node);
            } else if (Key.Numpad2.match(e) || Key.Digit2.match(e)) {
                current.value(2);
                if (focusable) focusNext(node);
            } else if (Key.Numpad3.match(e) || Key.Digit3.match(e)) {
                current.value(3);
                if (focusable) focusNext(node);
            } else if (Key.Numpad4.match(e) || Key.Digit4.match(e)) {
                current.value(4);
                if (focusable) focusNext(node);
            } else if (Key.Numpad5.match(e) || Key.Digit5.match(e)) {
                current.value(5);
                if (focusable) focusNext(node);
            } else if (Key.Numpad6.match(e) || Key.Digit6.match(e)) {
                current.value(6);
                if (focusable) focusNext(node);
            } else if (Key.Numpad7.match(e) || Key.Digit7.match(e)) {
                current.value(7);
                if (focusable) focusNext(node);
            } else if (Key.Numpad8.match(e) || Key.Digit8.match(e)) {
                current.value(8);
                if (focusable) focusNext(node);
            } else if (Key.Numpad9.match(e) || Key.Digit9.match(e)) {
                current.value(9);
                if (focusable) focusNext(node);
            } else if (Key.Numpad0.match(e) || Key.Digit0.match(e)) {
                current.value(0);
                if (focusable) focusNext(node);
            }
        };
    }
}
