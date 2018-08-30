/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.dsl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import kiss.I;
import kiss.Tree;
import viewtify.ui.UserInterfaceProvider;
import viewtify.util.TextNotation;

/**
 * @version 2018/08/29 11:22:43
 */
public class UIDefinition extends Tree<UserInterfaceProvider, UIDefinition.UINode> {

    /**
     * 
     */
    public UIDefinition() {
        super(UIDefinition.UINode::new, null);
    }

    public Node build() {
        return root.get(0).build();
    }

    /**
     * Declare the specified {@link Node}.
     * 
     * @param node
     */
    protected final void $(Node node) {
        $(new JavaFxNode(node));
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
        $(new LabelNode(text), nodes);
    }

    protected final UserInterfaceProvider<Label> label(String text) {
        return new LabelNode(text);
    }

    /**
     * @version 2018/08/29 11:25:30
     */
    public static class UINode implements Consumer<UINode> {

        protected UserInterfaceProvider ui;

        private List<UINode> children = new ArrayList();

        List<String> classes = new ArrayList();

        /**
         * @param name
         */
        private UINode(UserInterfaceProvider ui, int id, Object context) {
            this.ui = ui;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(UINode context) {
            context.children.add(this);
        }

        /**
         * Build {@link Node}.
         * 
         * @return
         */
        private Node build() {
            Node node = ui.ui();
            node.getStyleClass().addAll(classes);

            if (node instanceof Pane) {
                return build((Pane) node);
            } else {
                return node;
            }
        }

        private <P extends Pane> P build(P pane) {
            for (UINode child : children) {
                pane.getChildren().add(child.build());
            }
            return pane;
        }
    }

    /**
     * @version 2018/08/29 12:05:24
     */
    private static final class PaneNode<P extends Pane> implements UserInterfaceProvider<P> {

        private final Class<P> pane;

        /**
        * 
        */
        private PaneNode(Class<P> pane) {
            this.pane = Objects.requireNonNull(pane);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public P ui() {
            return I.make(pane);
        }
    }

    /**
     * @version 2018/08/29 12:05:24
     */
    private static final class LabelNode implements UserInterfaceProvider {

        private final String text;

        /**
        * 
        */
        private LabelNode(Object text) {
            this.text = String.valueOf(text);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Node ui() {
            return TextNotation.parse(text);
        }
    }

    /**
     * @version 2018/08/30 9:15:13
     */
    private static final class JavaFxNode implements UserInterfaceProvider {

        /** The actual node. */
        private final Node node;

        /**
         * @param node
         */
        private JavaFxNode(Node node) {
            this.node = node;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Node ui() {
            return node;
        }
    }
}
