/*
 * Copyright (C) 2017 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui.functionality;

/**
 * @version 2017/12/02 18:28:48
 */
public interface UserInterface<U> {
    /**
     * Return the associated user interface.
     * 
     * @return
     */
    U ui();
}
