/*
 * Copyright (C) 2018 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui;

import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import kiss.Extensible;
import kiss.I;
import kiss.Manageable;
import kiss.Singleton;
import viewtify.ui.helper.PreferenceHelper;

/**
 * @version 2018/08/28 12:51:50
 */
public abstract class AbstractTextField<Self extends AbstractTextField, F extends TextField> extends UserInterface<Self, F>
        implements PreferenceHelper<Self, String> {

    /** The message resource. */
    private static final Lang $ = I.i18n(Lang.class);

    /**
     * Enchanced view.
     * 
     * @param ui
     */
    protected AbstractTextField(F ui, View view) {
        super(ui, view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final StringProperty model() {
        return ui.textProperty();
    }

    /**
     * Check whether this field is empty or not.
     * 
     * @return
     */
    public final boolean isEmpty() {
        String text = value();
        return text == null || text.isEmpty();
    }

    /**
     * Check whether this field is empty or not.
     * 
     * @return
     */
    public final boolean isNotEmpty() {
        return !isEmpty();
    }

    private String findLabel() {
        String id = "#" + ui.getId() + "Label";
        Node label = ui.getParent().lookup(id);

        if (label instanceof Label) {
            return ((Label) label).getText();
        }
        return $.value();
    }

    /**
     * @version 2018/08/28 9:45:50
     */
    @SuppressWarnings("unused")
    @Manageable(lifestyle = Singleton.class)
    private static class Lang implements Extensible {

        /**
         * Word for "value".
         * 
         * @return
         */
        String value() {
            return "value";
        }

        /**
         * Message for empty string.
         * 
         * @param target
         * @return
         */
        String empty(String target) {
            return "There is no " + target + ", please specify it.";
        }

        /**
         * @version 2018/08/28 9:51:57
         */
        private static class Lang_ja extends Lang {

            /**
             * {@inheritDoc}
             */
            @Override
            String value() {
                return "値";
            }

            /**
             * {@inheritDoc}
             */
            @Override
            String empty(String target) {
                return target + "がありません、指定してください。";
            }
        }
    }
}
