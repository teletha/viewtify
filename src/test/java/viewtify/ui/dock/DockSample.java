/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
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
        DockSystem.register("First").contentsLazy(tab -> new ViewPane("First"));
        DockSystem.register("Second").contentsLazy(tab -> new ViewPane("Second"));
        DockSystem.register("Fourth", o -> o.bottom().ratio(0.7)).contentsLazy(tab -> new ViewPane("Fourth"));
        DockSystem.register("Fifth", o -> o.bottom()).contentsLazy(tab -> new ViewPane("Fifth"));
        DockSystem.register("Third", o -> o.left().ratio(0.3)).contentsLazy(tab -> new ViewPane("Third"));
        DockSystem.register("Tab1").contentsLazy(tab -> new ViewPane("Tab1"));
        DockSystem.register("Tab2").contentsLazy(tab -> new ViewPane("Tab2"));
        DockSystem.register("Tab3").contentsLazy(tab -> new ViewPane("Tab3"));
        DockSystem.register("Tab4").contentsLazy(tab -> new ViewPane("Tab4"));

        for (int i = 0; i < 40; i++) {
            DockSystem.register("Auto" + i).contentsLazy(tab -> new ViewPane("Auto"));
        }
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