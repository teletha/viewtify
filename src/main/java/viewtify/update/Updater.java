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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import javafx.beans.property.Property;
import kiss.I;
import kiss.Variable;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.property.SmartProperty;
import viewtify.ui.UILabel;
import viewtify.ui.UIProgressBar;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.ValueHelper;
import viewtify.ui.helper.Verifier;
import viewtify.ui.helper.VerifyHelper;
import viewtify.update.UpdateTask.Monitor;

public class Updater extends View implements VerifyHelper<Updater>, ValueHelper<Updater, UpdateTask> {

    /** The dirty variable bridge. */
    static UpdateTask tasks;

    UILabel message;

    UILabel percentage;

    UILabel detail;

    UIProgressBar bar;

    private final Verifier verifier = new Verifier().makeInvalid();

    private final SmartProperty<UpdateTask> property = new SmartProperty();

    class UI extends ViewDSL {
        {
            $(vbox, style.root, () -> {
                $(hbox, () -> {
                    $(message);
                    $(percentage);
                });
                $(bar, style.bar);
                $(detail);
            });
        }
    }

    interface style extends StyleDSL {

        Style root = () -> {
            padding.size(10, px);
        };

        Style bar = () -> {
            display.width.fill();
            margin.vertical(5, px);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        message.text("Updating...");
        bar.value(0d);

        execute();
    }

    private void execute() {
        UpdateTask update = tasks != null ? tasks : UpdateTask.restore(System.getenv("updater"));

        Viewtify.inWorker(() -> {
            try {
                Variable<String> m = Variable.of("");
                m.observe().switchVariable(x -> I.translate(x)).on(Viewtify.UIThread).to(x -> message.text(x));

                update.task.accept(update, new Monitor(m, progress -> {
                    Thread.sleep(15);

                    Viewtify.inUI(() -> {
                        bar.value(progress.rateByFiles() / 100d);
                        percentage.text(" (" + progress.rateByFiles() + "%)");
                        detail.text(progress.location);
                    });
                }));

                Viewtify.inUI(() -> {
                    percentage.text("");
                    detail.text("");
                });
                verifier.makeValid();

                property.set(update);
            } catch (Throwable e) {
                try {
                    e.printStackTrace(new PrintStream(new File("update-error.log")));
                } catch (FileNotFoundException e1) {
                    throw I.quiet(e);
                }
                I.error(e.getMessage());
                I.error(e);
                message.text(e.getMessage());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Verifier verifier() {
        return verifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<UpdateTask> valueProperty() {
        return property;
    }

    /**
     * Entry point for updater.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Viewtify.application().title("Updater").error((mes, e) -> {
            I.error(mes);
            I.error(e);
        }).size(400, 150).activate(Updater.class);
    }
}
