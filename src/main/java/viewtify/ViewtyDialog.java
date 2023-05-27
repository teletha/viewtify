/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;

import kiss.Disposable;
import kiss.I;
import kiss.Variable;
import kiss.WiseConsumer;
import viewtify.ui.View;
import viewtify.ui.helper.Verifier;

/**
 * The specialized dialog builder.
 */
public final class ViewtyDialog<T> {

    /** The actual dialog. */
    private final Dialog<ButtonType> dialog;

    /** The actual dialog. */
    private final DialogPane dialogPane;

    /** The diposer. */
    private final Disposable disposer = Disposable.empty();

    /**
     * Hide constructor.
     */
    ViewtyDialog() {
        dialog = new Dialog();
        dialogPane = dialog.getDialogPane();
    }

    /**
     * Configure title of this dialog.
     * 
     * @param title A title.
     * @return Chainable API.
     */
    public ViewtyDialog<T> title(String title) {
        dialog.setTitle(title);
        return this;
    }

    /**
     * Configure title of this dialog.
     * 
     * @param title A title.
     * @return Chainable API.
     */
    public ViewtyDialog<T> title(Variable<String> title) {
        title.observing().to(this::title, disposer);
        return this;
    }

    /**
     * Configure the button set of this dialog.
     * 
     * @param buttons
     * @return
     */
    public ViewtyDialog<T> button(ButtonType... buttons) {
        ObservableList<ButtonType> list = dialogPane.getButtonTypes();
        list.addAll(buttons);
        return this;
    }

    /**
     * Configure the button order.
     * 
     * @return
     */
    public ViewtyDialog<T> disableDefaultButtonOrder() {
        ButtonBar buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
        buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
        return this;
    }

    /**
     * Show and wait dialog.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V, D extends DialogView<V>> Variable<V> show(Class<D> view) {
        return show(view, null);
    }

    /**
     * Show and wait dialog.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V> Variable<V> show(DialogView<V> view) {
        return show(view, null);
    }

    /**
     * Show and wait dialog.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V, D extends DialogView<V>> Variable<V> show(Class<D> view, WiseConsumer<D> initializer) {
        return show(I.make(view), initializer);
    }

    /**
     * Show and wait dialog.
     * 
     * @param <V>
     * @param view
     * @return
     */
    public <V, D extends DialogView<V>> Variable<V> show(D view, WiseConsumer<D> initializer) {
        if (view != null) {
            DialogPane pane = dialog.getDialogPane();
            pane.setContent(view.ui());
        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isEmpty() || view == null) {
            return Variable.empty();
        } else {
            return view.value;
        }
    }

    /**
     * Specialized view for dialog.
     */
    public static abstract class DialogView<V> extends View {

        /** The value holder. */
        protected final Variable value = Variable.empty();

        /** The verifier. */
        protected final Verifier verifier = new Verifier();
    }
}
