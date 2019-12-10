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
