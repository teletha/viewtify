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
import psychopath.File;
import psychopath.Locator;
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

public class InstructionView extends View implements VerifyHelper<InstructionView>, ValueHelper<InstructionView, Instruction> {

    UILabel message;

    UILabel detail;

    UIProgressBar bar;

    private final Verifier verifier = new Verifier().makeInvalid();

    private final SmartProperty<Instruction> property = new SmartProperty();

    class UI extends ViewDSL {
        {
            $(vbox, style.root, () -> {
                $(message);
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
        Viewtify.inWorker(() -> {
            String value = System.getenv("updater");
            if (value == null) {
                value = System.getProperty("updater");
            }

            Instruction inst = Instruction.restore(value);
            Viewtify.observe(inst.message).on(Viewtify.UIThread).to(text -> message.text(text));
            inst.execute();

            // try {
            // double total = tasks.stream().mapToDouble(Task::weight).sum();
            // double parts = 0;
            //
            // for (int i = 0; i < tasks.size(); i++) {
            // Task task = tasks.get(i);
            // Variable<String> m = I.translate(task.message);
            // Viewtify.inUI(() -> message.text(m));
            //
            // double part = task.weight() / total;
            // task.accept(update, progress -> {
            // try {
            // Thread.sleep(10);
            // } catch (InterruptedException e) {
            // // ignore
            // }
            //
            // bar.value(v -> v + (part / progress.totalFiles));
            // Viewtify.inUI(() -> {
            // message.text(m + " (" + progress.rateByFiles() + "%)");
            // detail.text(progress.location);
            // });
            // });
            //
            // bar.value(parts += part);
            // Viewtify.inUI(() -> detail.text(""));
            // }
            // verifier.makeValid();
            //
            // property.set(update);
            // } catch (Throwable e) {
            // try {
            // e.printStackTrace(new PrintStream(new File("update-error.log")));
            // } catch (FileNotFoundException e1) {
            // throw I.quiet(e);
            // }
            // I.error(e.getMessage());
            // I.error(e);
            // message.text(I.translate(e.getMessage()));
            // }
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
    public Property<Instruction> valueProperty() {
        return property;
    }

    /**
     * Create update instruction.
     * 
     * @param archive
     * @return
     */
    public static Instruction update(File archive) {
        File zip = archive.absolutize();

        return Instruction.create(inst -> {
            inst.message("Verify new version.");
            inst.message("Download new version");
            inst.message("Prepare to update.");
            inst.message("Ready for update.");

            // Viewtify.dialog("Updater", InstructionView.class, ButtonType.APPLY,
            // ButtonType.CLOSE).ifPresent(tasks -> {

            // Instruction next = Instruction.create(sub -> {
            // sub.message("Installing the new version, please wait a minute.");
            // Directory temp =
            //
            // archive.trackUnpackingTo(destination, o -> o.replaceDifferent()).to(listener);
            // });
            //
            // tasks.unpack("Installing the new version, please wait a minute.", tasks.archive,
            // tasks.root);
            // tasks.cleanup("Clean up old files.", tasks.origin.locateLibrary());
            // tasks.reboot("Update is completed, reboot.");
            //
            // tasks.updater.boot(Map.of("updater", I.write(tasks)));
            // Viewtify.application().deactivate();
            // });
        });
    }

    /**
     * Entry point for updater.
     * 
     * @param args
     */
    public static void main(String[] args) {
        Instruction update = update(Locator.file("test.zip"));
        System.setProperty("updater", update.store());

        Viewtify.application().title("Updater").error((mes, e) -> {
            I.error(mes);
            I.error(e);
        }).size(400, 150).activate(InstructionView.class);
    }
}
