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
            display.minWidth(380, px);
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

        UpdateTask update = tasks != null ? tasks : UpdateTask.restore(System.getenv("updater"));

        Variable<String> m = Variable.of("");
        m.observe().switchVariable(x -> I.translate(x)).on(Viewtify.UIThread).to(x -> message.text(x));

        Viewtify.inWorker(() -> {
                update.code.accept(update, new Monitor(m, progress -> {
                    Thread.sleep(5);

                    Viewtify.inUI(() -> {
                        bar.value(progress.rateByFiles() / 100d);
                        percentage.text(" (" + progress.rateByFiles() + "%)");
                        detail.text(progress.location);
                    });
                }));

                Viewtify.inUI(() -> {
                    percentage.text("");
                    detail.text("");
                    bar.value(1d);
                });
                verifier.makeValid();

                property.set(update);
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
