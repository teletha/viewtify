/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.dock;

import viewtify.ActivationPolicy;
import viewtify.Theme;
import viewtify.Viewtify;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;

public class DockSample extends View {

    class view extends ViewDSL {
        {
            $(DockSystem.UI);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        DockSystem.register("First").contents(tab -> new ViewPane("First"));
        DockSystem.register("Second").contents(tab -> new ViewPane("Second"));
        DockSystem.register("Fourth", o -> o.bottom().ratio(0.7)).contents(tab -> new ViewPane("Fourth"));
        DockSystem.register("Fifth", o -> o.bottom()).contents(tab -> new ViewPane("Fifth"));
        DockSystem.register("Third", o -> o.left().ratio(0.3)).contents(tab -> new ViewPane("Third"));
    }

    private class ViewPane extends View {

        private String id;

        /**
         * @param id
         */
        public ViewPane(String id) {
            System.out.println("Load " + id);
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
        protected void initialize() {
        }
    }

    public static void main(String[] args) {
        Viewtify.application().use(ActivationPolicy.Latest).use(Theme.Dark).activate(DockSample.class);
    }
}
