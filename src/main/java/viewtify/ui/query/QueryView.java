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
                    Editor editor = new Editor();
                    editor.query = q;

                    $(editor);
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
        private Query<M, V> query;

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            extractor.text(query.name);
            input.value(I.transform(query.input.v, String.class)).clearable().observing().to(v -> {
                try {
                    query.input.set(v == null || v.isBlank() ? null : I.transform(v, query.type));
                } catch (Throwable e) {
                    // ignore
                }
            });
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

    class StringEditor extends Editor<String> {

    }
}
