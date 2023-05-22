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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import kiss.I;

/**
 * Serializable task set.
 */
public class Instruction implements Serializable {

    private static final long serialVersionUID = -1237562370107755382L;

    /** The actual task set. */
    final ArrayList<Code> tasks = new ArrayList();

    /** The origin environment. */
    final ApplicationPlatform origin = ApplicationPlatform.current();

    /** The remote environment. */
    final ApplicationPlatform remote = origin.updater();

    transient StringProperty message = new SimpleStringProperty();

    transient DoubleProperty progress = new SimpleDoubleProperty();

    /**
     * Hide constructor.
     */
    private Instruction() {
    }

    /**
     * Set the message.
     * 
     * @param message
     */
    public void message(String message) {
        this.message.set(message);
    }

    /**
     */
    public void execute() {
        try {
            for (Code task : tasks) {
                task.execute(this);
            }
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Intercept deserialization.
     * 
     * @param in
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();

        this.message = new SimpleStringProperty();
        this.progress = new SimpleDoubleProperty();
    }

    /**
     * Store all tasks.
     * 
     * @return
     */
    public String store() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();
            byte[] bytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Restore tasks.
     * 
     * @param tasks
     * @return
     */
    public static Instruction restore(String tasks) {
        try {
            byte[] bytes = Base64.getDecoder().decode(tasks);
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (Instruction) ois.readObject();
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Create your instruction.
     * 
     * @return
     */
    public static Instruction create(Code task) {
        Instruction tasks = new Instruction();
        tasks.tasks.add(task);
        return tasks;
    }

    /**
     * Serializable task.
     */
    public interface Code extends Serializable {
        void execute(Instruction tasks) throws Exception;
    }
}
