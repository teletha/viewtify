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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import viewtify.ui.UIComboBox;
import viewtify.ui.UIText;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class GenricFilterView<M> extends View {

    /** Property editing UIs */
    private final ObservableList<Editor> editors = FXCollections.observableArrayList();

    private final List<FilterModel> filters = new ArrayList();

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
        editors.add(new Editor());
    }

    public <T> void register(String name, Class<T> type, Function<M, T> extractor) {
        filters.add(new FilterModel(name, type, extractor));
    }

    private static class FilterModel<X, T> {
        String name;

        Function<X, T> extractor;

        /**
         * @param name
         * @param type
         * @param extractor
         */
        FilterModel(String name, Class<T> type, Function<X, T> extractor) {
            this.name = name;
            this.extractor = extractor;
        }
    }

    private class Editor extends View {

        UIComboBox<FilterModel> name;

        UIText<String> tester;

        UIComboBox<Filter> filter;

        class view extends ViewDSL {
            {
                $(hbox, () -> {
                    $(name);
                    $(tester);
                    $(filter);
                });
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            name.items(filters).render(m -> m.name);
            filter.items(Filter.by(String.class)).render(m -> m.getClass().getSimpleName());
        }
    }
}
