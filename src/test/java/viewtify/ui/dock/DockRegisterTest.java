/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.dock;

import java.util.List;

import org.junit.jupiter.api.Test;

import kiss.I;
import kiss.Variable;
import viewtify.ui.View;

class DockRegisterTest {

    @Test
    void register() {
        @SuppressWarnings("unused")
        class Register extends DockRegister {
            public void dock() {
                register(ViewA.class);
            }
        }

        Register register = I.make(Register.class);
        List<DockItem> items = register.queryIndependentDocks();
        assert items.size() == 1;

        DockItem item = items.get(0);
        assert item.id.equals("dock");
        assert item.title.is("Title A");
        assert item.registration != null;
    }

    private static class ViewA extends View {
        @Override
        protected void initialize() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Variable<String> title() {
            return Variable.of("Title A");
        }
    }
}
