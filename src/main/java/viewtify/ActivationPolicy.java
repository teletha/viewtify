/*
 * Copyright (C) 2019 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

/**
 * Application activation policy.
 * 
 * @version 2017/11/26 8:33:23
 */
public enum ActivationPolicy {

    /**
     * Continue to process the earliest application. The subsequent applications will not start up.
     */
    Earliest,

    /**
     * Application has multiple processes.
     */
    Multiple,

    /**
     * Terminate the prior applications immediately, then the latest application will start up.
     */
    Latest;
}
