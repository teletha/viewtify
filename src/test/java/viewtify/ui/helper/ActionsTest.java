/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.SingleSelectionModel;

class ActionsTest {

    @Test
    void selectNext() {
        Model model = new Model();
        model.items.add(new Item(0));
        model.items.add(new Item(1));
        model.items.add(new Item(2));
        model.select(0);

        assert model.getSelectedIndex() == 0;
        Actions.selectNext(model);
        assert model.getSelectedIndex() == 1;
        Actions.selectNext(model);
        assert model.getSelectedIndex() == 2;
    }

    @Test
    void selectNextWithoutSelection() {
        Model model = new Model();
        model.items.add(new Item(0));
        model.items.add(new Item(1));
        model.items.add(new Item(2));

        assert model.getSelectedIndex() == -1;
        Actions.selectNext(model);
        assert model.getSelectedIndex() == 0;
        Actions.selectNext(model);
        assert model.getSelectedIndex() == 1;
    }

    @Test
    void selectNextSkippingDisabled() {
        Model model = new Model();
        model.items.add(new Item(0));
        model.items.add(new Item(1, true));
        model.items.add(new Item(2));
        model.items.add(new Item(3, true));
        model.items.add(new Item(4, true));
        model.items.add(new Item(5));
        model.select(0);

        assert model.getSelectedIndex() == 0;
        Actions.selectNext(model);
        assert model.getSelectedIndex() == 2;
        Actions.selectNext(model);
        assert model.getSelectedIndex() == 5;
    }

    @Test
    void selectPrev() {
        Model model = new Model();
        model.items.add(new Item(0));
        model.items.add(new Item(1));
        model.items.add(new Item(2));
        model.select(2);

        assert model.getSelectedIndex() == 2;
        Actions.selectPrev(model);
        assert model.getSelectedIndex() == 1;
        Actions.selectPrev(model);
        assert model.getSelectedIndex() == 0;
    }

    @Test
    void selectPrevWithoutSelection() {
        Model model = new Model();
        model.items.add(new Item(0));
        model.items.add(new Item(1));
        model.items.add(new Item(2));

        assert model.getSelectedIndex() == -1;
        Actions.selectPrev(model);
        assert model.getSelectedIndex() == -1;
    }

    @Test
    void selectPrevSkippingDisabled() {
        Model model = new Model();
        model.items.add(new Item(0));
        model.items.add(new Item(1, true));
        model.items.add(new Item(2));
        model.items.add(new Item(3, true));
        model.items.add(new Item(4, true));
        model.items.add(new Item(5));
        model.select(5);

        assert model.getSelectedIndex() == 5;
        Actions.selectPrev(model);
        assert model.getSelectedIndex() == 2;
        Actions.selectPrev(model);
        assert model.getSelectedIndex() == 0;
    }

    /**
     * 
     */
    static class Item implements PropertyAccessHelper {

        private BooleanProperty disable = new SimpleBooleanProperty();

        public final int id;

        public Item(int id) {
            this(id, false);
        }

        public Item(int id, boolean disable) {
            this.id = id;
            this.disable.set(disable);
        }

        @Override
        public Object ui() {
            return this;
        }

        public BooleanProperty disableProperty() {
            return disable;
        }
    }

    /**
     * 
     */
    private static class Model extends SingleSelectionModel<Item> {

        private ArrayList<Item> items = new ArrayList();

        @Override
        protected Item getModelItem(int index) {
            return items.get(index);
        }

        @Override
        protected int getItemCount() {
            return items.size();
        }
    }
}