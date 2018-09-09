/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javafx.collections.ObservableMap;
import javafx.css.CssMetaData;
import javafx.css.StyleConverter;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.InsetsConverter;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import kiss.WiseBiConsumer;

/**
 * @version 2018/09/10 1:45:21
 */
class EnhancedCSSProperty {

    /** The ehnhanced property. */
    private static final CSSMeta<Insets> Margin = new CSSMeta<>("-fx-margin", InsetsConverter.getInstance(), (node, insets) -> {
        Parent parent = node.getParent();

        if (parent instanceof HBox) {
            HBox.setMargin(node, insets);
        } else if (parent instanceof VBox) {
            VBox.setMargin(node, insets);
        }
    });

    /** The enhanced properties. */
    private static final List<CssMetaData<? extends Styleable, ?>> EnhancedProperties = List.of(Margin);

    /** The cache of CSS metadata. */
    private static final Map<List<CssMetaData<? extends Styleable, ?>>, List<CssMetaData<? extends Styleable, ?>>> MetaCache = new HashMap();

    /**
     * Build {@link CssMetaData}.
     * 
     * @param parentMetadata
     * @return
     */
    static List<CssMetaData<? extends Styleable, ?>> metadata(List<CssMetaData<? extends Styleable, ?>> parentMetadata) {
        return MetaCache.computeIfAbsent(parentMetadata, parents -> {
            List<CssMetaData<? extends Styleable, ?>> list = new ArrayList();
            list.addAll(parents);
            list.addAll(EnhancedProperties);

            return Collections.unmodifiableList(list);
        });
    }

    /**
     * @version 2018/09/10 2:02:14
     */
    private static class CSSMeta<V> extends CssMetaData<Node, V> {

        /** The property applyer. */
        private final WiseBiConsumer<Node, V> applyer;

        /**
         * @param property
         * @param converter
         * @param applyer
         */
        private CSSMeta(String property, StyleConverter<?, V> converter, WiseBiConsumer<Node, V> applyer) {
            super(property, converter);

            this.applyer = Objects.requireNonNull(applyer);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isSettable(Node node) {
            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StyleableProperty<V> getStyleableProperty(Node node) {
            ObservableMap<String, StyleableProperty<V>> properties = (ObservableMap<String, StyleableProperty<V>>) (Object) node
                    .getProperties();
            return properties.computeIfAbsent(getProperty(), key -> {
                return new Styler<V>(node, applyer);
            });
        }
    }

    /**
     * @version 2018/09/10 1:21:22
     */
    private static class Styler<V> implements StyleableProperty<V> {

        /** The associated node. */
        private final Node node;

        /** The style applyer. */
        private final WiseBiConsumer<Node, V> applyer;

        /**
         * @param node
         * @param applyer
         */
        private Styler(Node node, WiseBiConsumer<Node, V> applyer) {
            this.node = node;
            this.applyer = applyer;
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
            applyer.accept(node, value);
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
            return null;
        }
    }
}
