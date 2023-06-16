/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.keys;

import kiss.I;
import stylist.Style;
import stylist.StyleDSL;
import viewtify.Viewtify;
import viewtify.ViewtyDialog.DialogView;
import viewtify.style.FormStyles;
import viewtify.ui.UITableColumn;
import viewtify.ui.UITableView;
import viewtify.ui.UIText;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.helper.User;

public class KeyBindingView extends View {

    private UIText<String> filter;

    private UITableView<Command> table;

    private UITableColumn<Command, String> name;

    private UITableColumn<Command, Key> key;

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(vbox, style.root, () -> {
                    $(filter);
                    $(table, style.table, () -> {
                        $(name, style.name);
                        $(key, style.key);
                    });
                });
            }
        };
    }

    interface style extends StyleDSL {
        Style root = () -> {
            display.maxWidth(380, px).maxHeight(250, px);
            margin.top(5, px);
        };

        Style table = () -> {
            margin.top(5, px);
        };

        Style name = () -> {
            display.width(240, px);
            text.align.left();
            padding.left(10, px);
        };

        Style key = () -> {
            display.width(120, px);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        ShortcutManager manager = I.make(ShortcutManager.class);
        filter.placeholder("Search command");

        name.text("Command").render(x -> x.ⅰ.name());
        key.text("Key").modelByVar(x -> manager.detectKey(x)).render(x -> x.ⅱ.toString());

        table.items(I.find(Command.class)).take(filter.observing().map(text -> {
            if (text == null || text.isEmpty()) {
                return I::accept;
            } else {
                return command -> command.name().contains(text) || command.shortcutCode().contains(text);
            }
        })).when(User.DoubleClick).to(() -> {
            Viewtify.dialog()
                    .title("Change shortcut key")
                    .button("Modify", "Cancel")
                    .disableCloseButton(true)
                    .translateButtons()
                    .show(new Change());
        });
    }

    /**
     * 
     */
    private class Change extends DialogView<Key> {

        private Command command = table.selectedItem().v;

        private UIText<String> input;

        /**
         * {@inheritDoc}
         */
        @Override
        protected ViewDSL declareUI() {
            return new ViewDSL() {
                {
                    $(vbox, () -> {
                        label(I.translate("Enter a new shortcut key to assign to {0}", command.name()));
                        $(hbox, FormStyles.FormRow, () -> {
                            $(input, FormStyles.FormInput);
                        });
                    });
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
        }
    }
}
