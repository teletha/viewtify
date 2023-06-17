/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.user;

import kiss.Variable;

/**
 * User of your applicaiton.
 */
public class User {

    /** The name. */
    public final Variable<String> name = Variable.empty();

    /** The password. */
    public final Variable<String> password = Variable.empty();

    /** The role. */
    public final Variable<String> role = Variable.empty();
}
