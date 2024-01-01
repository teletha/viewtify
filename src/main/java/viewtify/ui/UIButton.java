/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.scene.control.Button;
import kiss.I;
import viewtify.keys.Command;
import viewtify.ui.helper.ContextMenuHelper;
import viewtify.ui.helper.LabelHelper;
import viewtify.ui.helper.User;

public class UIButton extends UserInterface<UIButton, Button> implements LabelHelper<UIButton>, ContextMenuHelper<UIButton> {

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIButton(View view) {
        super(new Button(), view);
    }

    /**
     * Enchanced view.
     * 
     * @param view A {@link View} to which the widget belongs.
     */
    public UIButton(Button button, View view) {
        super(button, view);
    }

    /**
     * Contribute the specified {@link Command} to this button.
     * 
     * @param command An associated {@link Command}.
     * @return
     */
    public <C extends Command> UIButton contribute(Class<C> command) {
        if (command != null) {
            contribute(I.make(command));
        }
        return this;
    }

    /**
     * Assign the specified {@link Command} to this button. Contribute
     * 
     * @param command An associated {@link Command}.
     * @return
     */
    public UIButton contribute(Command command) {
        if (command != null) {
            text(command.name());
            when(User.Action, () -> {
                I.schedule(() -> {
                    try {
                        disable(true);
                        command.run();
                    } catch (Exception e) {
                        throw I.quiet(e);
                    } finally {
                        disable(false);
                    }
                });
            });
        }
        return this;
    }

    /**
     * Emulate the user action.
     * 
     * @return
     */
    public UIButton fire() {
        ui.fire();
        return this;
    }
}