/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.translator;

/**
 * 
 */
public interface Localizer {

    static Lang Locale() {
        return Lang.EN;
    }

    default Lang locale2() {
        return Lang.EN;
    }
}
