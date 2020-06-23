/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.validation;

import java.util.HashSet;
import java.util.Set;

import kiss.Signal;
import kiss.Variable;
import kiss.WiseRunnable;
import transcript.Transcript;

public class Validation {

    /** The validation message. */
    public final Variable<String> message = Variable.empty();

    /** The exposed validation result. */
    public final Signal<Boolean> valid = message.observing().map(m -> m == null || m.isEmpty());

    /** The exposed validation result. */
    public final Signal<Boolean> invalid = valid.map(v -> !v);

    /** The self verifier. */
    private final Set<Runnable> forSelf = new HashSet();

    /** The value supplier. */
    Object supplier;

    /**
     * The current state of validation.
     * 
     * @return
     */
    public boolean isValid() {
        return message.isAbsent();
    }

    /**
     * The current state of validation.
     * 
     * @return
     */
    public boolean isInvalid() {
        return message.isPresent();
    }

    /**
     * Register the self verifier.
     * 
     * @param verifier
     * @return
     */
    public Validation verifyBy(WiseRunnable verifier) {
        if (verifier != null) {
            forSelf.add(verifier);
            verify(); // immediately
        }
        return this;
    }

    /**
     * Register the validation timing.
     * 
     * @param timing
     * @return
     */
    public Validation verifyWhen(Signal<?> timing) {
        if (timing != null) {
            timing.to(this::verify);
        }
        return this;
    }

    /**
     * Verify now!
     */
    private void verify() {
        try {
            for (Runnable verifier : forSelf) {
                verifier.run();
            }

            this.message.set((String) null);
        } catch (Throwable e) {
            String message = e.getLocalizedMessage();

            if (message == null || message.isEmpty()) {
                message = Transcript.en("This is invalid value, please correct.").get();
            }
            this.message.set(message);
        }
    }
}
