/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.query;

import java.util.Objects;

import stylist.Style;
import stylist.StyleDSL;
import viewtify.style.FormStyles;
import viewtify.ui.UIComboBox;
import viewtify.ui.UILabel;
import viewtify.ui.UserInterface;
import viewtify.ui.UserInterfaceProvider;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.CollectableHelper;
import viewtify.ui.helper.ValueHelper;
import viewtify.ui.query.CompoundQuery.Query;
import viewtify.ui.query.CompoundQuery.Tester;

public class QueryView<M> extends View {

    /** The title pane. */
    private UILabel title;

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
                    $(new Editor(q));
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
        title.text(en("Search Condition"));
    }

    /**
     * {@link Query} editor UI.
     */
    class Editor<V> extends View {

        /** Extractor. */
        UILabel extractor;

        /** The {@link Tester}. */
        UIComboBox<Tester<V>> tester;

        /** The input UI. */
        final UserInterface input;

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
         * @param query
         */
        private Editor(Query<M, V> query) {
            this.query = query;
            this.input = UserInterfaceProvider.inputFor(query.type, query.input);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            extractor.text(query.name);
            tester.items(Tester.by(query.type))
                    .select(query.tester.or(tester.first()))
                    .renderByVariable(m -> m.description)
                    .syncTo(query.tester);

            ((ValueHelper<?, V>) input).observing(v -> tester.disable(v == null));

            if (query == initialFocus) {
                input.focus();
            }
        }
    }
}