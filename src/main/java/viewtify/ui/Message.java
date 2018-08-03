/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import kiss.Extensible;
import kiss.Manageable;
import kiss.Singleton;

/**
 * @version 2018/08/03 16:14:14
 */
@SuppressWarnings("unused")
@Manageable(lifestyle = Singleton.class)
class Message implements Extensible {

    /**
     * Error for Invalid value.
     * 
     * @return
     */
    String invalidValue() {
        return "This is invalid value, please correct.";
    }

    /**
     * Japanease bundle.
     * 
     * @version 2018/08/03 16:16:05
     */
    private static class Message_ja extends Message {

        /**
         * {@inheritDoc}
         */
        @Override
        String invalidValue() {
            return "不正な値です、修正して下さい。";
        }
    }
}
