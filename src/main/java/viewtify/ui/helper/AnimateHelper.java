/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import kiss.WiseRunnable;
import viewtify.ui.UserInterfaceProvider;
import viewtify.ui.anime.Animatable;

public interface AnimateHelper<Self extends AnimateHelper<Self>> {

    /**
     * Animate this UI.
     * 
     * @param animatable
     * @param finisher
     * @return
     */
    default Self animate(Animatable animatable, WiseRunnable finisher) {
        if (animatable != null && this instanceof UserInterfaceProvider provider) {
            animatable.play(provider, finisher);
        }

        return (Self) this;
    }
}
