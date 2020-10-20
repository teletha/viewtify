/*
 * Copyright (C) 2020 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.Objects;
import java.util.function.Predicate;

import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseConsumer;
import kiss.WiseRunnable;

public interface VerifyHelper<Self extends VerifyHelper<Self>> {

    /**
     * Retrieve the associated {@link Verifier}.
     */
    Verifier verifier();

    /**
     * Mark as valid.
     * 
     * @return Chainable API.
     */
    default Self valid() {
        verifier().message.set((String) null);
        return (Self) this;
    }

    /**
     * Mark as invalid.
     * 
     * @param message Details of the problem.
     * @return Chainable API.
     */
    default Self invalid(Object message) {
        verifier().message.set(String.valueOf(message));
        return (Self) this;
    }

    /**
     * Mark as invalid.
     * 
     * @param message Details of the problem.
     * @return Chainable API.
     */
    default Self invalid(Variable<String> message) {
        verifier().translatableMessage(message);
        return (Self) this;
    }

    /**
     * Get the current validity. true if all conditions registered in this verifier are met,
     * otherwise returns false.
     */
    default Signal<Boolean> isValid() {
        return verifier().valid;
    }

    /**
     * Get the current validity. false if all conditions registered in this verifier are met,
     * otherwise returns true.
     */
    default Signal<Boolean> isInvalid() {
        return verifier().invalid;
    }

    /**
     * Register the specified verifier. Thereafter, this verification will be performed at every
     * specified timing ({@link #verifyWhen(Signal...)}). The verification results can be obtained
     * by using {@link #isValid()} or {@link #isInvalid()}.
     * 
     * @param prerequisiteDescription Description of Requirements. This will be displayed as an
     *            error message if the condition is not met.
     * @param verifier Definition of Requirements.
     * @return Chainable API.
     */
    default Self verify(Object prerequisiteDescription, Predicate<? super Self> verifier) {
        return verifyBy(() -> {
            if (verifier.test((Self) this) == false) {
                throw new AssertionError(Objects.toString(prerequisiteDescription));
            }
        });
    }

    /**
     * Register the specified verifier. Thereafter, this verification will be performed at every
     * specified timing ({@link #verifyWhen(Signal...)}). The verification results can be obtained
     * by using {@link #isValid()} or {@link #isInvalid()}.
     * 
     * @param verifier Definition of Requirements.
     * @return Chainable API.
     */
    default Self verifyBy(WiseRunnable verifier) {
        verifier().verifyBy(verifier);

        return (Self) this;
    }

    /**
     * Register the specified verifier. Thereafter, this verification will be performed at every
     * specified timing ({@link #verifyWhen(Signal...)}). The verification results can be obtained
     * by using {@link #isValid()} or {@link #isInvalid()}.
     * 
     * @param verifier Definition of Requirements.
     * @return Chainable API.
     */
    default Self verifyBy(WiseConsumer<? super Self> verifier) {
        return verifyBy(() -> verifier.accept((Self) this));
    }

    /**
     * Register with the timing of the verification. The verification results can be obtained by
     * using {@link #isValid()} or {@link #isInvalid()}.
     * 
     * @param timing Timing of verification.
     * @return Chainable API.
     */
    default Self verifyWhen(Signal<?>... timings) {
        I.signal(timings).skipNull().to(verifier()::verifyWhen);

        return (Self) this;
    }

    /**
     * Register with the timing of the verification. The verification results can be obtained by
     * using {@link #isValid()} or {@link #isInvalid()}.
     * 
     * @param timing Timing of verification.
     * @return Chainable API.
     */
    default Self verifyWhenChanging(ValueHelper... timings) {
        I.signal(timings).skipNull().map(ValueHelper::isChanged).to(verifier()::verifyWhen);

        return (Self) this;
    }
}