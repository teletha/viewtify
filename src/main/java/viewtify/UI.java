/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.function.Consumer;

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
 * Declared user interface.
 * 
 * @version 2018/09/09 23:05:01
 */
public class UI extends Tree<UserInterfaceProvider, UI.UINode> {

    /**
     * 
     */
    public UI() {
        super(UI.UINode::new, null);
    }

    public final Node build() {
        return (Node) root.get(0).node;
    }

    /**
     * Declare the specified {@link Node}.
     * 
     * @param node
     */
    protected final void $(Node node) {
        $(() -> node);
    }

    protected final void vbox(UserInterfaceProvider... children) {
        vbox(() -> {
            for (UserInterfaceProvider child : children) {
                $(child);
            }
        });
    }

    protected final void vbox(Runnable children) {
        $(new PaneNode(VBox.class), children);
    }

    protected final void vbox(Consumer<UINode> child, Runnable children) {
        $(new PaneNode(VBox.class), child, children);
    }

    protected final void hbox(UserInterfaceProvider... children) {
        hbox(() -> {
            for (UserInterfaceProvider child : children) {
                $(child);
            }
        });
    }

    protected final void hbox(Runnable children) {
        $(new PaneNode(HBox.class), children);
    }

    protected final void hbox(Consumer<UINode> child, Runnable children) {
        $(new PaneNode(HBox.class), child, children);
    }

    protected final void hbox(Consumer<UINode> attribute, UserInterfaceProvider... children) {
        $(new PaneNode(HBox.class), attribute, () -> {
            for (UserInterfaceProvider child : children) {
                $(child);
            }
        });

    }

    protected final void label(Object text, Consumer<UINode>... nodes) {
        $(() -> TextNotation.parse(String.valueOf(text)), nodes);
    }

    protected final UserInterfaceProvider<Node> label(String text) {
        return () -> TextNotation.parse(text);
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
     * @version 2018/08/29 12:05:24
     */
    private static final class PaneNode<P extends Pane> implements UserInterfaceProvider<P> {

        private final P pane;

        /**
        * 
        */
        private PaneNode(Class<P> type) {
            this.pane = I.make(type);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public P ui() {
            return pane;
        }
    }
}
