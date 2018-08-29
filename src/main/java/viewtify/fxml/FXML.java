/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.fxml;

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

/**
 * @version 2018/08/29 11:22:43
 */
public class FXML extends Tree<UserInterfaceProvider, FXML.UINode> {

    /**
     * 
     */
    public FXML() {
        super(FXML.UINode::new, null);
    }

    public Node build() {
        return root.get(0).build();
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

    protected final void label(String text, Consumer<UINode>... nodes) {
        $(new LabelNode(text), nodes);
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
    private static final class LabelNode implements UserInterfaceProvider<Label> {

        private final String text;;

        /**
        * 
        */
        private LabelNode(String text) {
            this.text = text;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Label ui() {
            return new Label(text);
        }
    }
}
