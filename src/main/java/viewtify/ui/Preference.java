/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.HashMap;

import kiss.Manageable;
import kiss.Singleton;
import kiss.Storable;

/**
 * @version 2017/11/17 23:23:06
 */
@Manageable(lifestyle = Singleton.class)
class Preference extends HashMap<String, String> implements Storable<Preference> {
}
