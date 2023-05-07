/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.update;

public class UpdateResult {

    public final boolean success;

    public final String reason;

    /**
     * @param success
     * @param reason
     */
    UpdateResult(boolean success, String reason) {
        this.success = success;
        this.reason = reason;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "UpdateResult [success=" + success + ", reason=" + reason + "]";
    }
}
