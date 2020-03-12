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
import viewtify.ui.dock.WindowManager;

public class Docker extends View {

    class view extends ViewDSL {
        {
            $(() -> WindowManager.getRootPane());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        WindowManager.register(new ViewPane("First"));
        WindowManager.register(new ViewPane("Second"));
        WindowManager.register(new ViewPane("Third"));
        WindowManager.register(new ViewPane("Fourth"));
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
