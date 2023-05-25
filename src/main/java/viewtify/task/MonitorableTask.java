/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.task;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

import kiss.I;
import kiss.WiseConsumer;

public interface MonitorableTask<P> extends WiseConsumer<Monitor<P>>, Serializable {

    /**
     * Store all tasks.
     * 
     * @return
     */
    static String store(MonitorableTask task) {
        ByteArrayOutputStream array = new ByteArrayOutputStream();

        try (ObjectOutputStream output = new ObjectOutputStream(array)) {
            output.writeObject(task);
            return Base64.getEncoder().encodeToString(array.toByteArray());
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Restore tasks.
     * 
     * @param task
     * @return
     */
    static <P> MonitorableTask<P> restore(String task) {
        ByteArrayInputStream array = new ByteArrayInputStream(Base64.getDecoder().decode(task));

        try (ObjectInputStream input = new ObjectInputStream(array)) {
            return (MonitorableTask) input.readObject();
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }
}
