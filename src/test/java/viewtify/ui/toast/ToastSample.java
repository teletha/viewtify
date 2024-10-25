/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.toast;

import java.util.Random;

import net.bytebuddy.utility.RandomString;
import viewtify.Theme;
import viewtify.Viewtify;
import viewtify.ui.UIButton;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.User;
import viewtify.util.Corner;
import viewtify.util.ScreenSelector;

public class ToastSample {

    public static void main(String[] args) {
        Viewtify.application().use(Theme.Dark).activate(Main.class);
    }

    static class Main extends View {

        private UIButton button;

        class view extends ViewDSL {
            {
                $(button);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            Random random = new Random();

            button.text("Show").when(User.LeftClick).to(() -> {
                Toast.setting.max.set(10d);
                Toast.setting.screen.set(ScreenSelector.Mouse);
                Toast.setting.area.set(Corner.BottomLeft);
                Toast.setting.autoHide.set(4d);

                Toast.show(RandomString.make(Math.max(15, random.nextInt(100))));
            });
        }
    }
}