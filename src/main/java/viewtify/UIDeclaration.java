/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.awt.Label;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import kiss.I;
import kiss.Tree;
import viewtify.ui.UserInterfaceProvider;
import viewtify.util.TextNotation;

/**
 * Declaration for user interface.
 * 
 * @version 2018/09/10 16:25:21
 */
public class UIDeclaration extends Tree<UserInterfaceProvider, UIDeclaration.UINode> {

    /** The horizontal box. */
    protected static final UserInterfaceProvider hbox = new Box(HBox.class);

    /** The vertical box. */
    protected static final UserInterfaceProvider vbox = new Box(VBox.class);

    /**
     * 
     */
    public UIDeclaration() {
        super(UIDeclaration.UINode::new, null);
    }

    /**
     * Build node tree.
     * 
     * @return
     */
    public final Node build() {
        return (Node) root.get(0).node;
    }

    /**
     * Declare the specified {@link Node}.
     * 
     * @param node A JavaFX {@link Node} to compose.
     */
    protected final void $(Node node) {
        $(() -> node);
    }

    /**
     * Declare the {@link Label}.
     * 
     * @param text A label text.
     * @param followers
     */
    protected final void label(Object text, Consumer<UINode>... followers) {
        label(() -> String.valueOf(text), followers);
    }

    /**
     * Declare the {@link Label}.
     * 
     * @param text A label text.
     * @param followers
     */
    protected final void label(Supplier<String> text, Consumer<UINode>... followers) {
        $(() -> TextNotation.parse(String.valueOf(text.get())), followers);
    }

    /**
     * @version 2018/08/29 11:25:30
     */
    public static class UINode implements Consumer<UINode> {

        protected Object node;

        /**
         * @param name
         */
        private UINode(UserInterfaceProvider ui, int id, Object context) {
            this.node = ui.ui();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(UINode context) {
            if (context.node instanceof TableView) {
                ((TableView) context.node).getColumns().add(node);
            } else if (context.node instanceof TreeTableView) {
                ((TreeTableView) context.node).getColumns().add(node);
            } else if (context.node instanceof Group) {
                ((Group) context.node).getChildren().add((Node) node);
            } else if (context.node instanceof Pane) {
                ((Pane) context.node).getChildren().add((Node) node);
            }
        }
    }

    /**
     * @version 2018/09/10 15:28:21
     */
    private static final class Box<P extends Pane> implements UserInterfaceProvider<P> {

        /** The box type. */
        private final Class<P> type;

        /**
         * The typed box.
         * 
         * @param type A box type.
         */
        private Box(Class<P> type) {
            this.type = Objects.requireNonNull(type);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public P ui() {
            return I.make(type);
        }
    }
}
