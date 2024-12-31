/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package stylist;

import stylist.util.Properties;

public class StyleExtractor {

    /**
     * Extract style definition.
     * 
     * @param style
     * @return
     */
    public static Properties extract(Style style) {
        return Stylist.create(style).properties;
    }
}
