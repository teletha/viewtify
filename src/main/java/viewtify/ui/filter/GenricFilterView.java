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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import viewtify.ui.UIComboBox;
import viewtify.ui.UIText;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.filter.FilterSet.Pointer;
import viewtify.ui.filter.FilterSet.Query;

public class GenricFilterView<M> extends View {

    /** Property editing UIs */
    private final ObservableList<Editor> editors = FXCollections.observableArrayList();

    public final FilterSet<M> set = new FilterSet();

    class view extends ViewDSL {
        {
            $(vbox, editors);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        editors.add(new Editor(set.addEmptyQuery()));
    }

    private class Editor extends View {

        UIComboBox<Pointer> name;

        UIText<String> tester;

        UIComboBox<Filter> filter;

        final Query query;

        class view extends ViewDSL {
            {
                $(hbox, () -> {
                    $(name);
                    $(filter);
                    $(tester);
                });
            }
        }

        private Editor(Query query) {
            this.query = query;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            name.items(set.pointers).selectFirst().renderByProperty(p -> p.name).renderSelected(p -> p.name.get()).syncTo(query.pointer);
            tester.syncTo(query.tester);
            filter.items(Filter.by(String.class)).selectFirst().renderByVariable(m -> m.description).syncTo(query.filter);
        }
    }
}
