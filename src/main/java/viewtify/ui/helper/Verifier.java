/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import kiss.Disposable;
import kiss.I;
import kiss.Signal;
import kiss.Variable;
import kiss.WiseConsumer;
import kiss.WiseRunnable;

public class Verifier implements Disposable {

    /** The validation message. */
    public final Variable<String> message = Variable.empty();

    /** The exposed validation result. */
    public final Signal<Boolean> valid = message.observing().map(m -> m == null || m.isEmpty());

    /** The exposed validation result. */
    public final Signal<Boolean> invalid = valid.map(v -> !v);

    /** The self verifier. */
    private final Set<Runnable> forSelf = new HashSet();

    /** Discards the currently associated translatable text. */
    private Disposable translatableText;

    /**
     * Associate an error message that can be translated at any time.
     * 
     * @param message
     */
    final void translatableMessage(Variable<String> message) {
        if (message != null) {
            // discard the previous text
            if (translatableText != null) translatableText.dispose();

            // bind the new text
            translatableText = message.observing().to(this.message::set);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void vandalize() {
        if (translatableText != null) {
            translatableText.dispose();
        }
    }

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
     * Make this verifier valid.
     * 
     * @return
     */
    public Verifier makeValid() {
        message.set((String) null);
        return this;
    }

    /**
     * Make this verifier invalid.
     * 
     * @return
     */
    public Verifier makeInvalid() {
        if (message.isAbsent()) {
            message.set("Invalid");
        }
        return this;
    }

    /**
     * Register the self verifier.
     * 
     * @param verifier
     * @return
     */
    public Verifier verifyBy(WiseRunnable verifier) {
        if (verifier != null) {
            forSelf.add(verifier);
            verifyNow(); // immediately
        }
        return this;
    }

    /**
     * Register the validation timing.
     * 
     * @param timing
     * @return
     */
    public Verifier verifyWhen(Signal<?> timing) {
        if (timing != null) {
            timing.to(this::verifyNow);
        }
        return this;
    }

    /**
     * Verify now!
     */
    private void verifyNow() {
        try {
            for (Runnable verifier : forSelf) {
                verifier.run();
            }

            this.message.set((String) null);
        } catch (Throwable e) {
            String message = e.getLocalizedMessage();

            if (message == null || message.isEmpty()) {
                message = I.translate("This is invalid value, please correct.").toString();
            }
            this.message.set(message);
        }
    }

    /**
     * @param other
     * @return
     */
    public Verifier merge(VerifyHelper<?> other) {
        Verifier merger = new Verifier();
        message.observing().combineLatest(other.verifier().message.observing()).to(x -> {
            if (x.ⅰ != null && !x.ⅰ.isEmpty()) {

            }

            if (x.ⅰ == null || x.ⅰ.isEmpty()) {

            }
        });

        return merger;
    }

    /**
     * Builtin verifier.
     */
    public static final WiseConsumer<ValueHelper<?, String>> Number = ui -> {
        String value = ui.value();

        if (value != null) {
            value = value.strip();
            if (value.length() != 0) {
                new BigDecimal(value); // validate as number
                return;
            }
        }
        throw error("Please input a number.");
    };

    /**
     * Builtin verifier.
     */
    public static final WiseConsumer<ValueHelper<?, String>> PositiveNumber = ui -> {
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
        throw error("Please input a positive number.");
    };

    /**
     * Builtin verifier.
     */
    public static final WiseConsumer<ValueHelper<?, String>> Text = ui -> {
        String value = ui.value();

        if (value != null) {
            value = value.strip();
            if (value.length() != 0) {
                return;
            }
        }
        throw error("Please input some text.");
    };

    public static final WiseConsumer<ValueHelper<?, Integer>> Integer(int start, int end) {
        return ui -> {
            Integer integer = ui.value();

            if (integer != null) {
                int i = integer.intValue();
                if (start <= i && i <= end) {
                    return;
                }
            }
            throw error("Please input integral number. [" + start + " - " + end + "]");
        };
    }

    private static Throwable error(String message) {
        return new IllegalArgumentException(I.translate(message).toString());
    }
}