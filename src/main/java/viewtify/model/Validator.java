/*
 * Copyright (C) 2019 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.model;

import kiss.Signal;
import kiss.Variable;
import kiss.WiseConsumer;
import kiss.WiseRunnable;

/**
 * 
 */
public class Validator {

    public final Variable<String> message = Variable.empty();

    public <T> Validator asserts(Signal<T> timing, WiseConsumer<T> assertion) {
        timing.effect(assertion::accept).mapTo("").recoverWhen(s -> s.map(Throwable::getMessage)).to(message);

        return this;
    }

    public Validator asserts(Signal<?> timing, WiseRunnable assertion) {
        return asserts(timing, assertion.widen());
    }
}
