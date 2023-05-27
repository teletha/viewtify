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

import java.util.List;
import java.util.Optional;

import javafx.beans.property.Property;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;

import kiss.Disposable;
import kiss.I;
import kiss.Variable;
import kiss.WiseConsumer;
import viewtify.ui.UIButton;
import viewtify.ui.View;
import viewtify.ui.helper.ValueHelper;
import viewtify.ui.helper.Verifier;
import viewtify.ui.helper.VerifyHelper;

/**
 * The specialized dialog builder.
 */
public final class ViewtyDialog<T> {

    /** The actual dialog. */
    private final Dialog<ButtonType> dialog;

    /** The actual dialog. */
    private final DialogPane dialogPane;

    /** The actual stage. */
    private final Stage dialogStage;

    /** The diposer. */
    private final Disposable disposer = Disposable.empty();

    /**
     * Hide constructor.
     */
    ViewtyDialog() {
        dialog = new Dialog();
        dialogPane = dialog.getDialogPane();
        dialogStage = (Stage) dialogPane.getScene().getWindow();
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
     * Configure the button set of this dialog.
     * 
     * @param buttonOK
     * @param buttonOthers
     * @return
     */
    public ViewtyDialog<T> button(String buttonOK, String... buttonOthers) {
        List<ButtonType> types = I.signal(buttonOthers)
                .map(ButtonType::new)
                .startWith(new ButtonType(buttonOK, ButtonData.OK_DONE))
                .toList();

        ObservableList<ButtonType> list = dialogPane.getButtonTypes();
        list.addAll(types);

        for (ButtonType type : types) {
            Button button = (Button) dialogPane.lookupButton(type);
            I.translate(type.getText()).observing().on(Viewtify.UIThread).to(button::setText, disposer);
        }
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

            dialogPane.getButtonTypes()
                    .stream()
                    .filter(x -> x.getButtonData() == ButtonData.OK_DONE)
                    .map(x -> (Button) dialogPane.lookupButton(x))
                    .findFirst()
                    .ifPresent(b -> view.button = new UIButton(b, view));

            Node ui = view.ui();
            if (initializer != null) {
                initializer.accept(view);
            }
            pane.setContent(ui);
        }

        dialog.setOnCloseRequest(e -> {
            System.out.println("CLOSE");
        });
        dialogStage.setOnCloseRequest(e -> {
            System.out.println("CLOSE WINDOW");
        });

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
    public static abstract class DialogView<V> extends View implements VerifyHelper<DialogView<V>>, ValueHelper<DialogView<V>, V> {

        /** The value holder. */
        public final Variable<V> value = Variable.empty();

        private Property<V> p = Viewtify.property(value);

        /** The value holder. */
        protected final Verifier verifier = new Verifier();

        /** The button. */
        protected UIButton button;

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
        public Property<V> valueProperty() {
            return p;
        }
    }
}
