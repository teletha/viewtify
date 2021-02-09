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

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private CompoundQuery<M> compound;

    private final ObservableList<Builder> builders = FXCollections.observableArrayList();

    /**
     * Declare UI.
     */
    class view extends ViewDSL {
        {
            $(vbox, () -> {
                $(title, FormStyles.FormLabelMin);
                $(vbox, builders);
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
    public QueryView(CollectableHelper<?, M> collectable) {
        this(collectable.query());
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
        for (Query query : compound.queries()) {
            builders.add(new Builder(query));
        }
    }

    /**
     * @param textProperty
     * @return
     */
    public QueryView<M> focusOn(StringProperty queryName) {
        System.out.println(builders);
        for (Builder builder : builders) {
            System.out.println(builder.extractor.text() + "  " + queryName.get());
            if (builder.extractor.text().equals(queryName.get())) {
                builder.input.focus();
                break;
            }
        }
        return this;
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
        }
    }
}
