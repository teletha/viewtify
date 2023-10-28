/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.HashMap;
import java.util.Map;

public class Modena {

    static final Map<String, String> colors = new HashMap();

    static {
        colors.put("-fx-base", "#ececec");
        colors.put("-fx-control-inner-background", "derive(-fx-base,80%)");
        colors.put("-fx-color", "-fx-base");
        colors.put("-fx-text-base-color", "-fx-color");
        colors.put("-fx-light-text-color", "white");
        colors.put("-fx-dark-text-color", "black");
        colors.put("-fx-accent", "#0096C9");
        colors.put("-fx-background", "-fx-base");
        colors.put("-fx-error", "hsb(0, 72%, 75%)");
        colors.put("-fx-warning", "hsb(60, 92%, 95%)");
    }

}
