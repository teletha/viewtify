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
import viewtify.ui.View;

class DockRegisterTest {

    @Test
    void register() {
        @SuppressWarnings("unused")
        class Register extends DockRegister {
            public void dock() {
                register(MainView.class);
            }
        }

        Register register = I.make(Register.class);
        List<DockItem> items = register.queryIndependentDocks();
        assert items.size() == 1;

        DockItem item = items.get(0);
        assert item.id().equals("Dock");
        assert item.title().is("Main");
        assert item.registration() != null;
    }

    @Test
    void registerMultiple() {
        @SuppressWarnings("unused")
        class Register extends DockRegister {
            public void dockMain() {
                register(MainView.class);
            }

            public void dockSub() {
                register(SubView.class);
            }
        }

        Register register = I.make(Register.class);
        List<DockItem> items = register.queryIndependentDocks();
        assert items.size() == 2;

        DockItem item = items.get(0);
        assert item.id().equals("DockMain");
        assert item.title().is("Main");
        assert item.registration() != null;

        item = items.get(1);
        assert item.id().equals("DockSub");
        assert item.title().is("Sub");
        assert item.registration() != null;
    }

    private static class MainView extends View {
        @Override
        protected void initialize() {
        }
    }

    private static class SubView extends View {
        @Override
        protected void initialize() {
        }
    }
}
