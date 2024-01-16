/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.awt.Label;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.HiddenSidesPane;
import org.controlsfx.control.SegmentedButton;

import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseRunnable;
import lycoris.Tree;
import stylist.Style;
import viewtify.Viewtify;
import viewtify.style.FormStyles;
import viewtify.ui.helper.StyleHelper;
import viewtify.util.TextNotation;

/**
 * Domain specific language for user interface declaration.
 */
public class ViewDSL extends Tree<UserInterfaceProvider, ViewDSL.UINode> implements UserInterfaceProvider<Node> {

    /** The horizontal box. */
    protected static final UserInterfaceProvider hbox = new Box(HBox.class);

    /** The vertical box. */
    protected static final UserInterfaceProvider vbox = new Box(VBox.class);

    /** The stack box. */
    protected static final UserInterfaceProvider sbox = new Box(StackPane.class);

    /** The horizontal line. */
    protected static final UserInterfaceProvider hsep = new Sep(Orientation.HORIZONTAL);

    /** The horizontal line. */
    protected static final UserInterfaceProvider vsep = new Sep(Orientation.VERTICAL);

    /**
     * 
     */
    protected ViewDSL() {
        super(ViewDSL.UINode::new, null, (follower, current) -> {
            if (follower instanceof Style css) {
                if (current.node instanceof CheckComboBox check) {
                    // Why use Viewtify#observe to delay the application of style classes?
                    //
                    // Because CheckComboBoxSkin uses Bindings#bindContent to link the external and
                    // internal UI style classes, all class names previously set for the external UI
                    // will be unintentionally deleted. To avoid this, class settings are delayed.
                    // This is a very dirty solution and we are looking for a better solution.
                    Viewtify.observe(check.getChildrenUnmodifiable()).flatIterable(list -> list).first().to(node -> {
                        StyleHelper.of(node).style(css);
                    });
                } else {
                    StyleHelper.of(current.node).style(css);
                }
            } else {
                follower.accept(current);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Node ui() {
        return (Node) root.get(0).node;
    }

    /**
     * Convert {@link Node} to {@link UserInterfaceProvider}.
     * 
     * @param node A node to convert.
     * @return
     */
    protected final UserInterfaceProvider ui(Node node) {
        return () -> node;
    }

    protected final <E extends UserInterfaceProvider<? extends Node>> void $(UIGridView<E> view, int rowSize, int columnSize, E[] children, Consumer<UINode>... containerAttributes) {
        $(view, I.array(containerAttributes, node -> {
            foŕ(rowSize, row -> {
                foŕ(columnSize, column -> {
                    view.ui.add(children[row * column].ui(), row, column);
                });
            });
        }));
    }

    /**
     * Declare the dynamic child {@link UserInterfaceProvider}.
     * 
     * @param provider A child UI provider.
     * @param containerAttributes Attributes for container UI.
     */
    protected final void $(Variable<? extends UserInterfaceProvider> provider, Consumer<UINode>... containerAttributes) {
        $(() -> new Pane(), provider.observing().skipNull().map(I::list), containerAttributes);
    }

    /**
     * Declare the dynamic children {@link UserInterfaceProvider}s.
     * 
     * @param container A container UI provider.
     * @param providers A children UI provider.
     * @param containerAttributes Attributes for container UI.
     */
    protected final void $(UserInterfaceProvider<Pane> container, ObservableList<? extends UserInterfaceProvider<? extends Node>> providers, Consumer<UINode>... containerAttributes) {
        $(container, Viewtify.observing(providers), containerAttributes);
    }

    /**
     * Declare the dynamic children {@link UserInterfaceProvider}s.
     * 
     * @param container A container UI provider.
     * @param providers A children UI provider.
     * @param containerAttributes Attributes for container UI.
     */
    private void $(UserInterfaceProvider<Pane> container, Signal<? extends List<? extends UserInterfaceProvider<? extends Node>>> providers, Consumer<UINode>... containerAttributes) {
        Pane pane = container.ui();
        providers.to(list -> {
            pane.getChildren().setAll(I.signal(list).map(UserInterfaceProvider::ui).toList());
        });

        $(() -> pane, containerAttributes);
    }

    /**
     * Declare the {@link Label}.
     * 
     * @param text A label text.
     * @param followers
     */
    protected final void label(Object text, Consumer<UINode>... followers) {
        $(() -> TextNotation.parse(String.valueOf(text)), followers);
    }

    /**
     * Declare the {@link Label}.
     * 
     * @param text A label text.
     * @param followers
     */
    protected final void label(Variable text, Consumer<UINode>... followers) {
        $(() -> TextNotation.parse(text), followers);
    }

    /**
     * Declare the label text.
     * 
     * @param text
     * @return
     */
    protected final UILabel text(String text) {
        return new UILabel(null).text(text);
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     */
    protected final void title(String label, String... details) {
        $(vbox, FormStyles.Description, () -> {
            $(() -> TextNotation.parse(label), FormStyles.DescriptionTitle);
            for (String detail : details) {
                $(() -> TextNotation.parse(detail), FormStyles.DescriptionDetail);
            }
        });
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     */
    protected final void title(Variable<String> label, Variable<String>... details) {
        $(vbox, FormStyles.Description, () -> {
            $(() -> TextNotation.parse(label), FormStyles.DescriptionTitle);
            for (Variable<String> detail : details) {
                $(() -> TextNotation.parse(detail), FormStyles.DescriptionDetail);
            }
        });
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     * @param description A label description.
     */
    protected final void form(UserInterfaceProvider label, Object description) {
        form(label, null, text(String.valueOf(description)));
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     * @param userInterfaces A list of form controls.
     */
    protected final void form(String label, UserInterfaceProvider... userInterfaces) {
        form(() -> TextNotation.parse(label), null, userInterfaces);
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     * @param style Additional style for controls.
     * @param userInterfaces A list of form controls.
     */
    protected final void form(String label, Style style, UserInterfaceProvider... userInterfaces) {
        form(() -> TextNotation.parse(label), style, userInterfaces);
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     * @param userInterfaces A list of form controls.
     */
    protected final void form(Variable label, UserInterfaceProvider... userInterfaces) {
        form(label, null, userInterfaces);
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     * @param style Additional style for controls.
     * @param userInterfaces A list of form controls.
     */
    protected final void form(Variable label, Style style, UserInterfaceProvider... userInterfaces) {
        form(() -> TextNotation.parse(label), style, userInterfaces);
    }

    /**
     * Declare Form UI simply.
     * 
     * @param style Additional style for controls.
     * @param userInterfaces A list of form controls.
     */
    protected final void form(Style style, UserInterfaceProvider... userInterfaces) {
        form((UserInterfaceProvider) null, style, userInterfaces);
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     * @param style Additional style for controls.
     * @param providers A list of form controls.
     */
    private void form(UserInterfaceProvider label, Style style, UserInterfaceProvider... providers) {
        form(label, () -> extracted(style, providers));
    }

    private void extracted(Style style, UserInterfaceProvider... providers) {
        Style[] styles = style == null ? new Style[] {FormStyles.Input} : new Style[] {FormStyles.Input, style};
        for (int i = 0; i < providers.length; i++) {
            if (i != providers.length - 1) styles = I.array(styles, FormStyles.Sequencial);
            if (providers[i] instanceof UICheckBox) styles = I.array(styles, FormStyles.CheckBox);
            $(providers[i], styles);
        }
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     */
    protected final void form(String label, WiseRunnable process) {
        form(() -> TextNotation.parse(label), process);
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     */
    protected final void form(Variable<String> label, WiseRunnable process) {
        form(() -> TextNotation.parse(label), process);
    }

    /**
     * Declare Form UI simply.
     * 
     * @param label A form label.
     */
    protected final void form(UserInterfaceProvider label, WiseRunnable process) {
        $(hbox, FormStyles.Row, () -> {
            if (label != null) {
                $(label, FormStyles.Label);
            }
            process.run();
        });
    }

    /**
     * UI tree structure node for javafx's {@link Node}.
     */
    static class UINode implements Consumer<UINode> {

        /**
         * The actual javafx {@link Node} like object (using {@link Styleable} because
         * {@link TableColumn} and {@link MenuItem} are not {@link Node}).
         */
        private Styleable node;

        /**
         * Internal tree node.
         * 
         * @param provider Actual UI provider.
         * @param id Unused.
         * @param context Unused.
         */
        private UINode(UserInterfaceProvider provider, int id, Object context) {
            this.node = provider.ui();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void accept(UINode context) {
            if (context.node instanceof TableView table) {
                table.getColumns().add(node);
            } else if (context.node instanceof TreeTableView table) {
                table.getColumns().add(node);
            } else if (context.node instanceof ScrollPane pane) {
                pane.setContent((Node) node);
            } else if (context.node instanceof SplitPane pane) {
                pane.getItems().add((Node) node);
            } else if (context.node instanceof HiddenSidesPane pane) {
                pane.setContent((Node) node);
            } else if (context.node instanceof SegmentedButton segmented && node instanceof ToggleButton button) {
                segmented.getButtons().add(button);
            } else if (context.node instanceof ToolBar bar) {
                bar.getItems().add((Node) node);
            } else if (context.node instanceof Group group) {
                group.getChildren().add((Node) node);
            } else if (context.node instanceof Pane pane) {
                pane.getChildren().add((Node) node);
            }
        }
    }

    /**
     * General box container.
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

    /**
     * General separator.
     */
    private static final class Sep implements UserInterfaceProvider<Separator> {

        /** The type. */
        private final Orientation orientation;

        /**
         * The typed separator.
         * 
         * @param orientation A separator type.
         */
        private Sep(Orientation orientation) {
            this.orientation = orientation;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Separator ui() {
            return new Separator(orientation);
        }
    }
}