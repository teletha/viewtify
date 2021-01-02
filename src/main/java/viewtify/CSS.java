/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StringConverter;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import kiss.I;
import kiss.WiseBiConsumer;

/**
 * Declare viewtify's enhanced css properties.
 */
@SuppressWarnings("unused")
final class CSS {

    /** The extra property. */
    private static final Meta<Node, String> HeightKeyword = new Meta<>("-fx-height", StringConverter.getInstance(), (node, value) -> {
        if ("fill".equals(value)) {
            VBox.setVgrow(node, Priority.ALWAYS);
        }
    });

    /** The extra property. */
    private static final Meta<Node, String> WidthKeyword = new Meta<>("-fx-width", StringConverter.getInstance(), (node, value) -> {
        if ("fill".equals(value)) {
            HBox.setHgrow(node, Priority.ALWAYS);
        }
    });

    /** The extra property. */
    private static final Meta<Node, Insets> Margin = new Meta<>("-fx-margin", InsetsConverter.getInstance(), (node, insets) -> {
        Parent parent = node.getParent();

        if (parent instanceof HBox) {
            HBox.setMargin(node, insets);
        } else if (parent instanceof VBox) {
            VBox.setMargin(node, insets);
        }
    });

    /** The extra property. */
    private static final Meta<Node, Number> PositionTop = new Meta<>("-fx-top", SizeConverter.getInstance(), (node, size) -> {
        Parent parent = node.getParent();

        if (parent instanceof StackPane) {
            layoutStackPaneConstraints(node, Pos.TOP_CENTER, size);
        }
    });

    /** The extra property. */
    private static final Meta<Node, Number> PositionRight = new Meta<>("-fx-right", SizeConverter.getInstance(), (node, size) -> {
        Parent parent = node.getParent();

        if (parent instanceof StackPane) {
            layoutStackPaneConstraints(node, Pos.CENTER_RIGHT, size);
        }
    });

    /** The extra property. */
    private static final Meta<Node, Number> PositionBottom = new Meta<>("-fx-bottom", SizeConverter.getInstance(), (node, size) -> {
        Parent parent = node.getParent();

        if (parent instanceof StackPane) {
            layoutStackPaneConstraints(node, Pos.BOTTOM_CENTER, size);
        }
    });

    /** The extra property. */
    private static final Meta<Node, Number> PositionLeft = new Meta<>("-fx-left", SizeConverter.getInstance(), (node, size) -> {
        Parent parent = node.getParent();

        if (parent instanceof StackPane) {
            layoutStackPaneConstraints(node, Pos.CENTER_LEFT, size);
        }
    });

    /**
     * Apply position and margin for {@link StackPane}.
     * 
     * @param node A target child node of {@link StackPane}.
     * @param marginSide A margin side.
     * @param marginSize A margin size.
     */
    private static final void layoutStackPaneConstraints(Node node, Pos marginSide, Number marginSize) {
        Insets m = StackPane.getMargin(node);
        if (m == null) {
            m = new Insets(0);
        }
        if (marginSize == null) {
            marginSize = 0;
        }

        Pos p = StackPane.getAlignment(node);
        if (p == null) {
            p = marginSide;
        }

        switch (marginSide) {
        case TOP_CENTER:
            m = new Insets(marginSize.doubleValue(), m.getRight(), m.getBottom(), m.getLeft());
            p = compose(VPos.TOP, p.getHpos());
            break;
        case BOTTOM_CENTER:
            m = new Insets(m.getTop(), m.getRight(), marginSize.doubleValue(), m.getLeft());
            p = compose(VPos.BOTTOM, p.getHpos());
            break;
        case CENTER_RIGHT:
            m = new Insets(m.getTop(), marginSize.doubleValue(), m.getBottom(), m.getLeft());
            p = compose(p.getVpos(), HPos.RIGHT);
            break;
        case CENTER_LEFT:
            m = new Insets(m.getTop(), m.getRight(), m.getBottom(), marginSize.doubleValue());
            p = compose(p.getVpos(), HPos.LEFT);
            break;

        default:
            // If this exception will be thrown, it is bug of this program. So we must rethrow the
            // wrapped error in here.
            throw new Error();
        }
        StackPane.setMargin(node, m);
        StackPane.setAlignment(node, p);
    }

    /**
     * Build {@link Pos} by {@link VPos} and {@link HPos}.
     * 
     * @param v
     * @param h
     * @return
     */
    private static final Pos compose(VPos v, HPos h) {
        return Pos.valueOf(v.name() + "_" + h.name());
    }

    /** The extra property. */
    private static final Meta<Node, Number> ZIndex = new Meta<>("-fx-z-index", SizeConverter.getInstance(), (node, size) -> {
        if (size != null) {
            node.setViewOrder(-size.doubleValue());
        }
    });

    /**
     * Enhance CSS implemetation.
     */
    static void enhance() {
        List enhanced = new ArrayList();
        enhanced.addAll(Node.StyleableProperties.STYLEABLES);
        enhanced.addAll(I.signal(CSS.class.getDeclaredFields()).take(f -> f.getType() == Meta.class).map(f -> f.get(null)).toList());

        Node.StyleableProperties.STYLEABLES = enhanced;
    }

    /**
     * @version 2018/09/10 9:16:40
     */
    private static class Meta<N extends Node, V> extends CssMetaData<N, V> {

        /** The style assignor. */
        private final WiseBiConsumer<Node, V> assignor;

        /**
         * Static CSS metadata.
         * 
         * @param property A property name.
         * @param converter A property value converter.
         * @param assignor A style assign process.
         */
        private Meta(String property, StyleConverter<?, V> converter, WiseBiConsumer<Node, V> assignor) {
            super(property, converter);

            this.assignor = Objects.requireNonNull(assignor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSettable(N node) {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StyleableProperty<V> getStyleableProperty(N node) {
            return (StyleableProperty<V>) node.getProperties().computeIfAbsent(getProperty(), key -> new Assignor<>(this, node));
        }
    }

    /**
     * @version 2018/09/10 9:16:08
     */
    private static class Assignor<N extends Node, V> implements StyleableProperty<V> {

        /** The metadata. */
        private final Meta meta;

        /** The associated node. */
        private final N node;

        /**
         * @param meta
         * @param node
         */
        private Assignor(Meta meta, N node) {
            this.meta = meta;
            this.node = node;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public V getValue() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setValue(V value) {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void applyStyle(StyleOrigin origin, V value) {
            meta.assignor.accept(node, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StyleOrigin getStyleOrigin() {
            return StyleOrigin.AUTHOR;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CssMetaData<? extends Styleable, V> getCssMetaData() {
            return meta;
        }
    }
}