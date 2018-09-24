/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import kiss.WiseBiConsumer;

/**
 * @version 2018/09/24 16:48:16
 */
final class ExtraCSS {

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
    private static final Meta<Node, Number> ZIndex = new Meta<>("-fx-z-index", SizeConverter.getInstance(), (node, size) -> {
        System.out.println("ZINdex");
        System.out.println(size + "  " + size.doubleValue() + "   " + (-size.doubleValue()));
        node.setViewOrder(-size.doubleValue());
    });

    /** The extra properties. */
    private static final List<CssMetaData<? extends Styleable, ?>> extra = List.of(Margin, ZIndex);

    /** The cache of CSS metadata. */
    private static final Map<List<CssMetaData<? extends Styleable, ?>>, List<CssMetaData<? extends Styleable, ?>>> cache = new HashMap();

    /**
     * Append extra CSS properties to the specified metadata.
     * 
     * @param meta A parent metadata to be appended.
     * @return A synthesized metadata which is cached properly.
     */
    static List<CssMetaData<? extends Styleable, ?>> metadata(List<CssMetaData<? extends Styleable, ?>> meta) {
        return cache.computeIfAbsent(meta, parent -> {
            List<CssMetaData<? extends Styleable, ?>> synthesized = new ArrayList();
            synthesized.addAll(parent);
            synthesized.addAll(extra);

            return Collections.unmodifiableList(synthesized);
        });
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
