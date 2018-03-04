/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package toybox;

import java.nio.file.Path;
import java.util.ArrayList;

import kiss.Storable;

/**
 * @version 2018/03/04 16:12:23
 */
public class ConsoleList extends ArrayList<Path> implements Storable<ConsoleList> {
}
