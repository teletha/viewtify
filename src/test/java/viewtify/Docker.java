/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.dock.DragNDropManager;
import viewtify.ui.dock.DragNDropManagerImpl;
import viewtify.ui.dock.WindowManagerImpl;

public class Docker extends View {

    private WindowManagerImpl manager = new WindowManagerImpl();

    private DragNDropManager dnd = new DragNDropManagerImpl(manager);

    class view extends ViewDSL {
        {
            $(() -> manager.getRootPane());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        manager.init();
        dnd.init();

        manager.register(new ViewPane("First"));
        manager.register(new ViewPane("Second"));
        manager.register(new ViewPane("Third"));
        manager.register(new ViewPane("Fourth"));

        // Pane pane = new Pane();
        // RootArea root = new RootArea(pane, dnd, true);
        // root.add(new ViewStatus(new ViewPane("Fifth Luna")), Position.CENTER);
        // manager.register(root);
    }

    private class ViewPane extends View {

        private String id;

        /**
         * @param id
         */
        public ViewPane(String id) {
            this.id = id;
        }

        class view extends ViewDSL {
            {
                label(id + " area");
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String id() {
            return id;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
        }
    }

    public static void main(String[] args) {
        Viewtify.application().use(ActivationPolicy.Latest).use(Theme.Dark).activate(Docker.class);
    }
}
