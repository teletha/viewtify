/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import javafx.beans.property.DoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.DialogPane;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import org.controlsfx.control.PopOver.ArrowLocation;

import kiss.Disposable;
import kiss.I;
import kiss.Model;
import kiss.Variable;
import kiss.WiseConsumer;
import kiss.WiseRunnable;
import psychopath.Directory;
import psychopath.File;
import psychopath.Locator;
import viewtify.style.FormStyles;
import viewtify.ui.UIButton;
import viewtify.ui.UILabel;
import viewtify.ui.UIText;
import viewtify.ui.UserInterface;
import viewtify.ui.UserInterfaceProvider;
import viewtify.ui.View;
import viewtify.ui.ViewDSL;
import viewtify.ui.anime.Anime;
import viewtify.ui.view.PrintPreview;
import viewtify.ui.view.PrintPreview.PrintInfo;
import viewtify.util.FXUtils;

/**
 * The specialized dialog builder.
 */
public final class ViewtyDialog<T> {

    private static final double animeation = 0.2;

    /** The associated window. */
    private Window baseWindow;

    /** The dialog style. */
    private StageStyle style = StageStyle.DECORATED;

    /** The dialog modality. */
    private Modality modality = Modality.APPLICATION_MODAL;

    /** The title of this dialog. */
    private Variable<String> title;

    /** The button set. */
    private List<ButtonType> buttons;

    /** The button's visibility. */
    private boolean hideButtons;

    /** The button's visibility. */
    private boolean disableButtons;

    /** The close button's visibility. */
    private boolean disableCloseButton;

    /** The button's order setting. */
    private boolean disableSystemButtonOrder;

    /** The width of this dialog. */
    private int width;

    /** The height of this dialog. */
    private int height;

    /** The translation mode. */
    private boolean translatable;

    /** The dialog effect. */
    private boolean fadable;

    /** The dialog effect. */
    private boolean blurable;

    /** The dialog effect. */
    private Side sliding;

    /** The dialog mode. */
    private boolean blockable = true;

    /** The location calculator. */
    private WiseConsumer<Node> locator;

    /** The diposer. */
    private final Disposable disposer = Disposable.empty();

    /**
     * Hide constructor.
     */
    ViewtyDialog() {
        this.baseWindow = FXUtils.findFocusedWindow().exact();
    }

    /**
     * Configure title of this dialog.
     * 
     * @param title A title.
     * @return Chainable API.
     */
    public ViewtyDialog<T> title(String title) {
        this.title = Variable.of(title);
        return this;
    }

    /**
     * Configure title of this dialog.
     * 
     * @param title A title.
     * @return Chainable API.
     */
    public ViewtyDialog<T> title(Variable<String> title) {
        this.title = title;
        return this;
    }

    /**
     * Configure style of this dialog.
     * 
     * @param style A dialog style.
     * @return Chainable API.
     */
    public ViewtyDialog<T> style(StageStyle style) {
        if (style != null) {
            this.style = style;
        }
        return this;
    }

    /**
     * Configure modality of this dialog.
     * 
     * @param modality A dialog modality.
     * @return Chainable API.
     */
    public ViewtyDialog<T> modal(Modality modality) {
        if (modality != null) {
            this.modality = modality;
        }
        return this;
    }

    /**
     * Configure the button set of this dialog.
     * 
     * @param buttons
     * @return
     */
    public ViewtyDialog<T> button(ButtonType... buttons) {
        this.buttons = List.of(buttons);
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
        this.buttons = I.signal(buttonOthers)
                .map(x -> new ButtonType(x, ButtonData.CANCEL_CLOSE))
                .startWith(new ButtonType(buttonOK, ButtonData.OK_DONE))
                .toList();

        return this;
    }

    /**
     * Configure automatic translation of this dialog.
     * 
     * @return
     */
    public ViewtyDialog<T> translatable() {
        this.translatable = true;
        return this;
    }

    /**
     * Configure the button enable / disable.
     * 
     * @return
     */
    public ViewtyDialog<T> disableButtons(boolean disable) {
        this.disableButtons = disable;
        return this;
    }

    /**
     * Configure the close button enable / disable.
     * 
     * @return
     */
    public ViewtyDialog<T> disableCloseButton(boolean disable) {
        this.disableCloseButton = disable;
        return this;
    }

    /**
     * Configure the button order.
     * 
     * @return
     */
    public ViewtyDialog<T> disableSystemButtonOrder() {
        this.disableSystemButtonOrder = true;
        return this;
    }

    /**
     * Congifure the loading effect.
     * 
     * @return
     */
    public ViewtyDialog<T> fadable() {
        return fadable(sliding);
    }

    /**
     * Congifure the loading effect.
     * 
     * @return
     */
    public ViewtyDialog<T> fadable(Side sliding) {
        this.fadable = true;
        this.sliding = sliding;
        return this;
    }

    /**
     * Congifure the loading effect.
     * 
     * @return
     */
    public ViewtyDialog<T> blurable() {
        blurable = true;
        return this;
    }

    /**
     * Congifure the loading mode.
     * 
     * @return
     */
    public ViewtyDialog<T> unblockable() {
        blockable = false;
        return this;
    }

    /**
     * Configure the size of dialog.
     * 
     * @return
     */
    public ViewtyDialog<T> width(int width) {
        this.width = width;
        return this;
    }

    /**
     * Configure the size of dialog.
     * 
     * @return
     */
    public ViewtyDialog<T> height(int height) {
        this.height = height;
        return this;
    }

    /**
     * Configure the location of dialog.
     * 
     * @param x A location of x-axis.
     * @param y A location of y-axis.
     * @return
     */
    public ViewtyDialog<T> location(double x, double y) {
        this.locator = ui -> {
            Window window = ui.getScene().getWindow();
            window.setY(y);
            window.setX(x);
        };
        return this;
    }

    /**
     * Configure the location of dialog.
     * 
     * @param source A invoker node.
     * @param arrow Specify the ground plane of the Invoker Node and pop-up area.
     * @return
     */
    public ViewtyDialog<T> location(Node source, ArrowLocation arrow) {
        this.locator = ui -> {
            ArrowLocation location = Objects.requireNonNullElse(arrow, ArrowLocation.TOP_CENTER);
            Bounds sourceBounds = source.localToScreen(source.getBoundsInLocal());
            Bounds popupBounds = ui.getBoundsInLocal();
            double x, y;
            double gap = 5;

            x = switch (location) {
            case TOP_CENTER, BOTTOM_CENTER -> sourceBounds.getCenterX() - popupBounds.getWidth() / 2;
            case TOP_LEFT, BOTTOM_LEFT -> sourceBounds.getMinX();
            case TOP_RIGHT, BOTTOM_RIGHT -> sourceBounds.getMaxX() - popupBounds.getWidth();
            case RIGHT_CENTER, RIGHT_BOTTOM, RIGHT_TOP -> sourceBounds.getMinX() - popupBounds.getWidth() - gap;
            case LEFT_TOP, LEFT_CENTER, LEFT_BOTTOM -> sourceBounds.getMaxX() + gap;
            };
            y = switch (location) {
            case RIGHT_TOP, LEFT_TOP -> sourceBounds.getMinY();
            case RIGHT_CENTER, LEFT_CENTER -> sourceBounds.getCenterY() - popupBounds.getHeight() / 2;
            case RIGHT_BOTTOM, LEFT_BOTTOM -> sourceBounds.getMaxY() - popupBounds.getHeight();
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT -> sourceBounds.getMaxY() + gap;
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> sourceBounds.getMinY() - popupBounds.getHeight() - gap;
            };

            Window window = ui.getScene().getWindow();
            window.setY(y);
            window.setX(x);
        };
        return this;
    }

    /**
     * Configure the size of dialog.
     * 
     * @return
     */
    public ViewtyDialog<T> size(int width, int height) {
        this.width = width;
        this.height = height;
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
    public <V, D extends DialogView<V>> Variable<V> show(D view) {
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
        Dialog<V> dialog = initialize(new Dialog());
        dialog.setResultConverter(x -> {
            switch (x.getButtonData()) {
            case APPLY:
            case OK_DONE:
            case FINISH:
                return view.value;

            default:
                return null;
            }
        });

        DialogPane dialogPane = dialog.getDialogPane();
        view.pane = dialogPane;

        Node ui = view.ui();
        if (initializer != null) {
            initializer.accept(view);
        }

        if (locator != null) {
            dialog.addEventHandler(DialogEvent.DIALOG_SHOWN, e -> {
                locator.accept(dialogPane);
            });
        }

        Window window = dialogPane.getScene().getWindow();

        if (fadable) {
            double distance = sliding == null ? 0 : sliding == Side.TOP || sliding == Side.LEFT ? 10 : -10;
            DoubleProperty location = sliding == null || sliding.isVertical() ? Viewtify.property(window::getX, window::setX)
                    : Viewtify.property(window::getY, window::setY);

            // shown effect
            dialog.addEventHandler(DialogEvent.DIALOG_SHOWN, e -> {
                double now = location.get();
                if (Double.isNaN(now)) {
                    window.centerOnScreen();
                    now = location.get();
                }

                // initialize
                window.setOpacity(0);
                location.set(now - distance);

                Anime.define().duration(animeation).effect(window.opacityProperty(), 1).effect(location, now).run();
            });

            // hidden effect
            EventHandler<Event> hidden = e -> {
                if (window.getOpacity() != 0) {
                    e.consume();
                    Anime.define()
                            .duration(animeation)
                            .effect(window.opacityProperty(), 0.5)
                            .effect(location, location.get() + distance)
                            .run(window::hide);
                }
            };
            window.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, hidden);
            dialog.addEventFilter(DialogEvent.DIALOG_CLOSE_REQUEST, hidden);
        }

        if (blurable) {
            Parent owner = dialog.getOwner().getScene().getRoot();
            owner.setEffect(new BoxBlur(5, 5, 8));
            dialog.getDialogPane().getScene().getWindow().addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> {
                owner.setEffect(null);
            });
        }

        dialogPane.setContent(ui);

        return showAndTell(dialog, disableButtons ? () -> view.value : () -> null);
    }

    /**
     * Show the notification dialog.
     * 
     * @param type
     * @param message
     * @return
     */
    public Variable<ButtonType> show(AlertType type, String message) {
        return show(type, Variable.of(message));
    }

    /**
     * Show the notification dialog.
     * 
     * @param type
     * @param message
     * @return
     */
    public Variable<ButtonType> show(AlertType type, Variable<String> message) {
        Alert dialog = initialize(new Alert(type));
        dialog.setContentText(null);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);

        message.observing().on(Viewtify.UIThread).to(dialog::setContentText, disposer);

        I.signal(type)
                .skip(AlertType.NONE)
                .map(x -> x.name().toLowerCase().replace("confirmation", "confirm"))
                .map(x -> "com/sun/javafx/scene/control/skin/caspian/dialog-" + x + ".png")
                .to(x -> {
                    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                    stage.getIcons().add(new Image(ClassLoader.getSystemResourceAsStream(x)));
                });

        return showAndTell(dialog, () -> ButtonType.CANCEL);
    }

    /**
     * Show the directory chooser dialog.
     */
    public Variable<Directory> showDirectory() {
        return showDirectory(null);
    }

    /**
     * Show the directory chooser dialog.
     */
    public Variable<Directory> showDirectory(Directory initial) {
        DirectoryChooser chooser = new DirectoryChooser();
        if (title != null) {
            title.observing().to(chooser::setTitle);
        }

        if (initial != null) {
            chooser.setInitialDirectory(initial.asJavaFile());
        }

        return Variable.of(chooser.showDialog(baseWindow)).map(dir -> Locator.directory(dir.toPath()));
    }

    /**
     * Show the file chooser dialog.
     */
    public Variable<File> showFile() {
        return showFile(null, null);
    }

    /**
     * Show the file chooser dialog.
     */
    public Variable<File> showFile(Directory initial, ExtensionFilter filter) {
        FileChooser chooser = new FileChooser();
        if (title != null) {
            title.observing().to(chooser::setTitle);
        }

        if (initial != null) {
            chooser.setInitialDirectory(initial.asJavaFile());
        }

        if (filter != null) {
            chooser.getExtensionFilters().add(filter);
        }

        java.io.File result = chooser.showOpenDialog(baseWindow);
        return Variable.of(result).map(x -> Locator.file(result.toPath()));
    }

    /**
     * Show the text input dialog.
     * 
     * @param message
     * @return
     */
    public Variable<String> showInput(String message) {
        return showInput(message, (WiseConsumer) null);
    }

    /**
     * Show the text input dialog.
     * 
     * @param message
     * @return
     */
    public Variable<String> showInput(String message, String defaultValue) {
        return showInput(message, ui -> ui.value(defaultValue));
    }

    /**
     * Show the text input dialog.
     * 
     * @param message
     * @return
     */
    public Variable<String> showInput(String message, WiseConsumer<UIText<String>> setting) {
        return button(ButtonType.OK, ButtonType.CANCEL).show(TextInputDialog.class, view -> {
            view.label.text(message);
            if (setting != null) {
                setting.accept(view.input);
            }
        });
    }

    private static Popup latestPopup;

    /**
     * @param builder
     */
    public void showPopup(Supplier<UserInterfaceProvider<? extends Node>> builder) {
        WiseRunnable showPopup = () -> {
            unblockable().disableButtons(true).style(StageStyle.TRANSPARENT).modal(Modality.NONE).show(new Popup(builder));
        };

        if (latestPopup == null) {
            showPopup.run();
        } else {
            latestPopup.requestClosing(latestPopup.builder == builder ? null : showPopup);
        }
    }

    /**
     * Special popup dialog.
     */
    private static class Popup extends DialogView<Object> {

        private final Supplier<UserInterfaceProvider<? extends Node>> builder;

        private WiseRunnable finisher;

        /**
         * @param builder
         */
        private Popup(Supplier<UserInterfaceProvider<? extends Node>> builder) {
            this.builder = builder;
        }

        @Override
        protected ViewDSL declareUI() {
            return new ViewDSL() {
                {
                    $(builder.get());
                }
            };
        }

        @Override
        protected void initialize() {
            latestPopup = this;

            pane.getScene().setFill(null);
            pane.getStyleClass().add("dialog-popup");

            Window window = pane.getScene().getWindow();
            window.focusedProperty().addListener((object, old, focused) -> {
                if (!focused) {
                    requestClosing(null);
                }
            });
            window.addEventHandler(WindowEvent.WINDOW_HIDDEN, e -> {
                latestPopup = null;
                if (finisher != null) finisher.run();
            });
        }

        /**
         * Request closing window.
         */
        private void requestClosing(WiseRunnable finisher) {
            if (finisher != null) this.finisher = finisher;

            Window window = pane.getScene().getWindow();
            window.fireEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST));
        }
    }

    /**
     * Show the print preview dialog.
     */
    public Variable<PrintInfo> showPrintPreview(WritableImage... images) {
        return fadable().blurable().button("Print", "Cancel").translatable().show(PrintPreview.class, preview -> preview.loadImage(images));
    }

    public <V> Variable<V> showWizard(Class<? extends DialogView<V>>... views) {
        Class<V> type = (Class<V>) Model.collectParameters(views[0], DialogView.class)[0];
        return showWizard(I.make(type), views);
    }

    public <V> Variable<V> showWizard(V value, Class<? extends DialogView<V>>... views) {
        hideButtons = true;

        return button(ButtonType.PREVIOUS, ButtonType.NEXT, ButtonType.FINISH, ButtonType.CANCEL).disableSystemButtonOrder()
                .show(new WizardDialog(value, views));
    }

    /**
     * Initialize the dialog.
     * 
     * @param dialog
     * @return
     */
    private <D extends Dialog<R>, R> D initialize(D dialog) {
        dialog.initOwner(baseWindow);
        dialog.initStyle(style);
        dialog.initModality(modality);
        DialogPane dialogPane = dialog.getDialogPane();

        if (title != null) {
            title.observing().to(dialog::setTitle, disposer);
        }

        ButtonBar buttonBar = (ButtonBar) dialogPane.lookup(".button-bar");
        buttonBar.setPadding(new Insets(12, 3, 12, 3));

        if (buttons != null) {
            dialogPane.getButtonTypes().clear();
            dialogPane.getButtonTypes().addAll(buttons);
        } else {
            dialogPane.getChildren().remove(buttonBar);
        }

        if (translatable) {
            for (ButtonType type : dialogPane.getButtonTypes()) {
                Button button = (Button) dialogPane.lookupButton(type);
                I.translate(type.getText()).observing().on(Viewtify.UIThread).to(button::setText, disposer);
            }
        }

        if (disableCloseButton) {
            ((Stage) dialogPane.getScene().getWindow()).setOnCloseRequest(WindowEvent::consume);
        }

        if (disableSystemButtonOrder) {
            buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_NONE);
        }

        if (disableButtons) {
            for (ButtonType type : dialogPane.getButtonTypes()) {
                dialogPane.lookupButton(type).setDisable(true);
            }
        }

        if (hideButtons) {
            for (ButtonType type : dialogPane.getButtonTypes()) {
                dialogPane.lookupButton(type).setVisible(false);
            }
        }

        if (0 < width) {
            dialogPane.setMinWidth(width);
            dialogPane.setPrefWidth(width);
        }

        if (0 < height) {
            dialogPane.setMinHeight(height);
            dialogPane.setPrefHeight(height);
        }

        return dialog;
    }

    /**
     * Show the dialog.
     * 
     * @param <V>
     * @param dialog
     * @param defaultValue
     * @return
     */
    private <V> Variable<V> showAndTell(Dialog<V> dialog, Supplier<V> defaultValue) {
        try {
            // TODO The JVM throws an error when exiting an application with a dialog open. Even if
            // I try to close the dialog before exiting the application, it throws an exception due
            // to a NestedLoop problem. I have no idea how to deal with this, so I just ignore the
            // error. This is a wall of grief.
            disposer.add(() -> {
                try {
                    dialog.close();
                } catch (IllegalArgumentException e) {
                    // wall of grief
                }
            });

            if (blockable) {
                return Variable.of(dialog.showAndWait().orElseGet(defaultValue));
            } else {
                dialog.show();
                return Variable.empty();

            }
        } finally {
            disposer.dispose();
        }
    }

    /**
     * Close all dialogs.
     */
    public static void close() {
        if (latestPopup != null) {
            latestPopup.requestClosing(null);
        }
    }

    /**
     * Specialized view for dialog.
     */
    public static abstract class DialogView<V> extends View {

        /** The value holder. */
        public V value;

        /** The associated dialog pane. */
        protected DialogPane pane;

        /** The page title. */
        @Override
        public Variable<String> title() {
            return Variable.of(getClass().getSimpleName());
        }

        /**
         * Find the dialog button.
         * 
         * @param type
         * @return
         */
        protected final Variable<UIButton> find(ButtonType type) {
            return find(type.getButtonData());
        }

        /**
         * Find the dialog button.
         * 
         * @param data
         * @return
         */
        protected final Variable<UIButton> find(ButtonData data) {
            return I.signal(pane.getButtonTypes())
                    .take(x -> x.getButtonData() == data)
                    .map(x -> pane.lookupButton(x))
                    .as(Button.class)
                    .first()
                    .map(x -> new UIButton(x, this))
                    .to();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
        }

        public boolean isInvalid() {
            return findUI(UserInterface.class).any(ui -> ui.verifier().isInvalid()).to().exact();
        }
    }

    /**
     * Specialized dialog.
     */
    private static class TextInputDialog extends DialogView<String> {

        private UILabel label;

        private UIText<String> input;

        /**
         * {@inheritDoc}
         */
        @Override
        protected ViewDSL declareUI() {
            return new ViewDSL() {
                {
                    $(vbox, () -> {
                        $(label, FormStyles.Row);
                        $(input, FormStyles.Row);
                    });
                }
            };
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void initialize() {
            input.focus().observing().to(x -> value = x);
        }
    }
}