/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.helper;

import java.math.BigDecimal;

import kiss.WiseConsumer;
import transcript.Transcript;

public class ValueCondition {

    public static final WiseConsumer<ValueHelper<?, String>> Positive = ui -> {
        String value = ui.value();

        if (value != null) {
            value = value.strip();
            if (value.length() != 0) {
                BigDecimal num = new BigDecimal(value); // validate as number

                if (num.signum() == 1) {
                    return;
                }
            }
        }
        throw error("Please enter a positive number.");
    };

    private static Throwable error(String message) {
        return new IllegalArgumentException(Transcript.en(message).get());
    }
}
