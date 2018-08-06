/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.model;

import java.util.HashSet;
import java.util.Set;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import kiss.Extensible;
import kiss.I;
import kiss.Manageable;
import kiss.Signal;
import kiss.Singleton;
import kiss.Variable;
import kiss.WiseRunnable;

/**
 * @version 2018/08/06 14:33:52
 */
public class Validation {

    /** The internal validation result. */
    private final BooleanProperty $valid = new SimpleBooleanProperty();

    /** The exposed validation result. */
    public final BooleanBinding invalid = $valid.not();

    /** The exposed validation result. */
    public final BooleanBinding valid = invalid.not();

    /** The validation message. */
    public final Variable<String> message = Variable.empty();

    /** The list of validators. */
    private final Set<Runnable> validators = new HashSet();

    /**
     * Register the validator.
     * 
     * @param validator
     * @return
     */
    public Validation require(WiseRunnable validator) {
        if (validator != null) {
            validators.add(validator);

            // apply validation immediately
            validate();
        }
        return this;
    }

    /**
     * Register the validation timing.
     * 
     * @param timing
     * @return
     */
    public Validation when(Signal<?> timing) {
        if (timing != null) {
            timing.to(this::validate);
        }
        return this;
    }

    /**
     * 
     */
    private void validate() {
        try {
            for (Runnable validator : validators) {
                validator.run();
            }
            $valid.set(true);
            this.message.set((String) null);
        } catch (Throwable e) {
            String message = e.getLocalizedMessage();

            if (message == null || message.isEmpty()) {
                message = I.i18n(Message::invalidValue);
            }
            $valid.set(false);
            this.message.set(message);
        }
    }

    /**
     * @version 2018/08/03 16:14:14
     */
    @SuppressWarnings("unused")
    @Manageable(lifestyle = Singleton.class)
    private static class Message implements Extensible {

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

}