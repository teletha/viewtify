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

import java.util.function.Predicate;

import kiss.I;
import kiss.Signal;
import kiss.WiseConsumer;
import kiss.WiseRunnable;
import viewtify.ui.UserInterface;
import viewtify.validation.Validation;

public interface ValidationHelper<Self extends ValidationHelper<Self>> {

    /**
     * Retrieve the associated {@link Validation}.
     */
    Validation validation();

    /**
     * Set the validator for this {@link UserInterface}.
     * 
     * @param validator A validator.
     * @return Chainable API.
     */
    default Self require(CharSequence message, Predicate<? super Self> validator) {
        return require(() -> {
            if (validator.test((Self) this) == false) {
                throw new AssertionError(message.toString());
            }
        });
    }

    /**
     * Set the validator for this {@link UserInterface}.
     * 
     * @param validator A validator.
     * @return Chainable API.
     */
    default Self require(WiseRunnable validator) {
        validation().verifyBy(validator);

        return (Self) this;
    }

    /**
     * Set the validator for this {@link UserInterface}.
     * 
     * @param validator A validator.
     * @return Chainable API.
     */
    default Self require(WiseConsumer<? super Self> validator) {
        return require(() -> validator.accept((Self) this));
    }

    /**
     * Register the validation timing.
     * 
     * @param timing
     * @return
     */
    default Self requireWhen(Signal<?>... timings) {
        I.signal(timings).skipNull().to(validation()::verifyWhen);

        return (Self) this;
    }

    // /**
    // * Register the validation timing.
    // *
    // * @param timing
    // * @return
    // */
    // default Self requireWhen(ValidationHelper... timings) {
    // I.signal(timings).skipNull().map(v -> v.validation().valid).to(validation()::verifyWhen);
    //
    // return (Self) this;
    // }
}
