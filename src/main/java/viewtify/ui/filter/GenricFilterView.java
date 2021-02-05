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

import org.controlsfx.glyphfont.FontAwesome.Glyph;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kiss.I;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.style.FormStyles;
import viewtify.ui.UIButton;
import viewtify.ui.UIComboBox;
import viewtify.ui.UIText;
import viewtify.ui.UIToggleButton;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.filter.CompoundQuery.Extractor;
import viewtify.ui.filter.CompoundQuery.Matcher;
import viewtify.ui.filter.CompoundQuery.Query;
import viewtify.ui.helper.User;

public class GenricFilterView<M> extends View {

    /** {@link Query} builder UI. */
    private final ObservableList<Builder> builders = FXCollections.observableArrayList();

    private UIToggleButton optionAll;

    private UIToggleButton optionCaseSensitive;

    private UIButton add;

    public final CompoundQuery<M> compound = new CompoundQuery();

    class view extends ViewDSL {
        {
            $(vbox, () -> {
                $(vbox, builders);
                $(hbox, FormStyles.FormRow, () -> {
                    $(optionAll);
                    $(optionCaseSensitive);
                    $(add, style.addition);
                });
            });
        }
    }

    interface style extends StyleDSL {
        Style addition = () -> {
            text.align.right();
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        addNewQuery(String.class);

        optionAll.text("Match All");
        optionCaseSensitive.text("Match Case");
        add.text(Glyph.PLUS).when(User.Action, () -> addNewQuery(compound.extractors().get(0).type()));
    }

    private <V> void addNewQuery(Class<V> type) {
        builders.add(new Builder(type));
    }

    /**
     * {@link Query} builder UI.
     */
    class Builder<V> extends View {

        /** Extractor selector. */
        UIComboBox<Extractor<M, V>> extractor;

        /** The user input. */
        UIText<String> input;

        /** The {@link Matcher} selector. */
        UIComboBox<Matcher<V>> matcher;

        /** The deleter. */
        UIButton delete;

        /**
         * Declare view.
         */
        class view extends ViewDSL {
            {
                $(hbox, () -> {
                    $(extractor);
                    $(matcher);
                    $(input);
                    $(delete);
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
        private Builder(Class<V> type) {
            query = compound.addNewQuery(type);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            extractor.items(compound.extractors()).selectFirst().renderByProperty(Extractor::description).syncTo(query.extractor);
            input.observing().to(v -> query.input.set(I.transform(v, query.type)));
            matcher.items(BuiltinMatchers.by(query.type)).selectFirst().renderByVariable(Matcher::description).syncTo(query.matcher);
            delete.text(Glyph.MINUS).disableWhen(compound.size.observing().map(i -> i <= 1)).when(User.Action, () -> {
                builders.remove(this);
                compound.remove(query);
            });

            input.ui.requestFocus();
        }
    }
}
