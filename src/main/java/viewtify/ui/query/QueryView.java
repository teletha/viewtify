/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.query;

import java.util.Objects;

import kiss.I;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.style.FormStyles;
import viewtify.ui.UIComboBox;
import viewtify.ui.UILabel;
import viewtify.ui.UIText;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.query.CompoundQuery.Query;
import viewtify.ui.query.CompoundQuery.Tester;

public class QueryView<M> extends View {

    /** The title pane. */
    private UILabel title = new UILabel(this).text(en("Search Condition"));

    /** The query model. */
    private CompoundQuery<M> compound;

    /**
     * Declare UI.
     */
    class view extends ViewDSL {
        {
            $(vbox, () -> {
                $(title, FormStyles.FormLabelMin);
                for (Query query : compound.queries()) {
                    $(new Builder(query));
                }
            });
        }
    }

    /**
     * Declare styles.
     */
    interface style extends StyleDSL {
        Style addition = () -> {
            text.align.right();
        };

        Style block = () -> {
            margin.right(2, px);
        };

        Style blockEnd = () -> {
            margin.right(2, px);
        };
    }

    /**
     * Build new UI for {@link CompoundQuery}.
     */
    public QueryView(CompoundQuery<M> compound) {
        this.compound = Objects.requireNonNull(compound);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
    }

    /**
     * {@link Query} builder UI.
     */
    class Builder<V> extends View {

        /** Extractor. */
        UILabel extractor;

        /** The user input. */
        UIText<String> input;

        /** The {@link Tester}. */
        UIComboBox<Tester<V>> tester;

        /**
         * Declare view.
         */
        class view extends ViewDSL {
            {
                $(hbox, FormStyles.FormRow, () -> {
                    $(extractor, FormStyles.FormLabelMin);
                    $(input, FormStyles.FormInput);
                    $(tester);
                });
            }
        }

        /** The associated {@link Query}. */
        private final Query<M, V> query;

        /**
         * Create new {@link Query} builder.
         * 
         * @param type
         */
        private Builder(Query<M, V> query) {
            this.query = Objects.requireNonNull(query);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            extractor.text(query.description);
            input.observing().to(v -> query.input.set(I.transform(v, query.type)));
            tester.items(Tester.by(query.type)).selectFirst().renderByVariable(m -> m.description).syncTo(query.tester);

            input.ui.requestFocus();
        }
    }
}
