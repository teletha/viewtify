/*
 * Copyright (C) 2021 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.filter;

import kiss.I;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.style.FormStyles;
import viewtify.ui.UIComboBox;
import viewtify.ui.UILabel;
import viewtify.ui.UIText;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.filter.CompoundQuery.Matcher;
import viewtify.ui.filter.CompoundQuery.Query;

public class GenricFilterView<M> extends View {

    UILabel title = new UILabel(this).text(en("Search Condition"));

    class view extends ViewDSL {
        {
            $(vbox, () -> {
                $(title, FormStyles.FormLabelMin);
                for (Query query : compound.queries) {
                    $(new Builder(query));
                }
            });
        }
    }

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

    private final CompoundQuery<M> compound;

    /**
     * @param compound
     */
    public GenricFilterView(CompoundQuery<M> compound) {
        this.compound = compound;
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

        /** Extractor selector. */
        UILabel label;

        /** The user input. */
        UIText<String> input;

        /** The {@link Matcher} selector. */
        UIComboBox<Matcher<V>> matcher;

        /**
         * Declare view.
         */
        class view extends ViewDSL {
            {
                $(hbox, FormStyles.FormRow, () -> {
                    $(label, FormStyles.FormLabelMin);
                    $(input, FormStyles.FormInput);
                    $(matcher);
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
            this.query = query;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            label.text(query.extractor.description());
            input.observing().to(v -> query.input.set(I.transform(v, query.extractor.type())));
            matcher.items(BuiltinMatchers.by(query.extractor.type()))
                    .selectFirst()
                    .renderByVariable(Matcher::description)
                    .syncTo(query.matcher);

            input.ui.requestFocus();
        }
    }
}
