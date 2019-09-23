/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
import kiss.I;
import kiss.WiseBiConsumer;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.ModifierAdjustment;
import net.bytebuddy.description.modifier.FieldManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * @version 2018/09/24 22:02:52
 */
@SuppressWarnings("unused")
final class CSS {

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
        if (size != null) {
            node.setViewOrder(-size.doubleValue());
        }
    });

    /**
     * Enhance CSS implemetation.
     */
    static void enhance() {
        try {
            Instrumentation instrument = ByteBuddyAgent.install();

            // expose javafx.scene.Node$StyleableProperties
            ModifierAdjustment modifier = new ModifierAdjustment() //
                    .withTypeModifiers(Visibility.PUBLIC)
                    .withFieldModifiers(Visibility.PUBLIC, FieldManifestation.PLAIN);

            // redefine
            new AgentBuilder.Default() //
                    .type(ElementMatchers.named("javafx.scene.Node$StyleableProperties"))
                    .transform((builder, desc, loader, module) -> builder.visit(modifier))
                    .installOn(instrument);

            // add extra properties
            Class clazz = Class.forName("javafx.scene.Node$StyleableProperties");
            Field field = clazz.getDeclaredField("STYLEABLES");
            List list = (List) field.get(null);

            List enhanced = new ArrayList();
            enhanced.addAll(list);
            enhanced.addAll(I.signal(CSS.class.getDeclaredFields()).take(f -> f.getType() == Meta.class).map(f -> f.get(null)).toList());

            field.set(null, Collections.unmodifiableList(enhanced));
        } catch (Exception e) {
            throw I.quiet(e);
        }
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
