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
import psychopath.Progress;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.property.SmartProperty;
import viewtify.task.Monitor;
import viewtify.task.MonitorableTask;
import viewtify.ui.UILabel;
import viewtify.ui.UIProgressBar;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.ValueHelper;
import viewtify.ui.helper.Verifier;
import viewtify.ui.helper.VerifyHelper;

class Updater extends View implements VerifyHelper<Updater>, ValueHelper<Updater, MonitorableTask> {

    /** The registered task. */
    static MonitorableTask<Progress> task;

    /** The updater UI. */
    private UILabel message;

    /** The updater UI. */
    private UILabel percentage;

    /** The updater UI. */
    private UILabel detail;

    /** The updater UI. */
    private UIProgressBar bar;

    /** Disabler */
    private final Verifier verifier = new Verifier().makeInvalid();

    /** USELESS. */
    private final SmartProperty<MonitorableTask> property = new SmartProperty();

    /**
     * UI definition.
     */
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

    /**
     * Style definition.
     */
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
        MonitorableTask update = task != null ? task : MonitorableTask.restore(System.getenv(Updater.class.getName()));

        Variable<String> mes = Variable.of("");
        mes.observe().switchVariable(x -> I.translate(x)).on(Viewtify.UIThread).to(x -> message.text(x));

        Variable<Double> per = Variable.of(0d);
        per.observe().on(Viewtify.UIThread).to(x -> {
            bar.value(x / 100d);
            percentage.text("(" + x.intValue() + "%)");
        });

        Viewtify.inWorker(() -> {
            try {
                update.accept(new Monitor<Progress>(mes, per, (monitor, progress) -> {
                    Thread.sleep(2);

                    monitor.complete(progress.rateByFiles());
                    Viewtify.inUI(() -> detail.text(progress.location));
                }));

                Viewtify.inUI(() -> {
                    percentage.text("");
                    detail.text("");
                    bar.value(1d);
                });

                verifier.makeValid();
                property.set(update);
            } catch (Throwable e) {
                mes.set(e.getMessage());
                throw I.quiet(e);
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
    public Property<MonitorableTask> valueProperty() {
        return property;
    }

    /**
     * Entry point for updater.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Viewtify.application().title("Updater").size(400, 150).activate(Updater.class);
    }
}
