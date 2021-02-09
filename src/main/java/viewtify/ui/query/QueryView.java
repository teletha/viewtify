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
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.query.CompoundQuery.Query;
import viewtify.ui.query.CompoundQuery.Tester;

public class QueryView<M> extends View {

    /** The title pane. */
    private UILabel title = new UILabel(this).text(en("Search Condition"));

    /** The query model. */
    private final CompoundQuery<M> compound;

    /** The initial focused query. (may be null) */
    private final Query initialFocus;

    /**
     * Declare UI.
     */
    class view extends ViewDSL {
        {
            $(vbox, () -> {
                $(title, FormStyles.FormLabelMin);
                for (Query q : compound.queries()) {
                    $(new Builder(q));
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
    public QueryView(CollectableHelper<?, M> collectable, Query... initialFocus) {
        this(collectable.query(), initialFocus);
    }

    /**
     * Build new UI for {@link CompoundQuery}.
     */
    public QueryView(CompoundQuery<M> compound, Query... initialFocus) {
        this.compound = Objects.requireNonNull(compound);
        this.initialFocus = initialFocus == null || initialFocus.length == 0 ? null : initialFocus[0];
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
            input.value(I.transform(query.input.v, String.class)).observing().to(v -> query.input.set(I.transform(v, query.type)));
            tester.items(Tester.by(query.type))
                    .select(query.tester.or(tester.first()))
                    .renderByVariable(m -> m.description)
                    .syncTo(query.tester);

            if (query == initialFocus) {
                input.focus();
                if (!input.isEmpty()) {
                    input.ui.selectAll();
                }
            }
        }
    }
}
