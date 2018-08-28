/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.util;

import java.util.function.Supplier;

import kiss.Extensible;
import kiss.I;
import kiss.Manageable;
import kiss.Singleton;
import kiss.WiseRunnable;

/**
 * @version 2018/08/28 9:44:14
 */
public class Require {

    static {
        I.load(Lang.class, false);
    }

    /** The message resource. */
    private static final Lang message = I.i18n(Lang.class);

    /**
     * The target must NOT BE empty {@link String}.
     * 
     * @param validationTarget
     * @return The validator.
     */
    public static WiseRunnable nonEmpty(Supplier<String> targetName, Supplier<String> validationTarget) {
        return nonEmpty(targetName.get(), validationTarget);
    }

    /**
     * The target must NOT BE empty {@link String}.
     * 
     * @param validationTarget
     * @return The validator.
     */
    public static WiseRunnable nonEmpty(String targetName, Supplier<String> validationTarget) {
        return () -> {
            String text = validationTarget.get();
            assert text != null : message.nonEmptyString(targetName);
            assert text.isEmpty() == false : message.nonEmptyString(targetName);
        };
    }

    /**
     * @version 2018/08/28 9:45:50
     */
    @SuppressWarnings("unused")
    @Manageable(lifestyle = Singleton.class)
    private static class Lang implements Extensible {

        /**
         * Message for non empty string.
         * 
         * @param target
         * @return
         */
        String nonEmptyString(String target) {
            return "There is no " + target + ", please specify it.";
        }

        /**
         * @version 2018/08/28 9:51:57
         */
        private static class Lang_ja extends Lang {

            /**
             * {@inheritDoc}
             */
            @Override
            String nonEmptyString(String target) {
                return target + "がありません、指定してください。";
            }
        }
    }
}
