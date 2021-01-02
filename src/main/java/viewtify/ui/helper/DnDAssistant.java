/*
 * Copyright (C) 2021 viewtify Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.helper;

import java.util.function.BiConsumer;
import java.util.function.Function;

import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import viewtify.ui.UserInterfaceProvider;

/**
 * Drag and Drop supporter.
 */
public class DnDAssistant<T> {

    /** The data holder. */
    private T data;

    /**
     * Register as DnD source.
     * 
     * @param <Source>
     * @param source
     */
    public <Source extends ValueHelper<?, T> & UserInterfaceProvider<? extends Node> & UserActionHelper<?>> DnDAssistant<T> source(Source source) {
        return source(source, Source::value);
    }

    /**
     * Register as DnD source.
     * 
     * @param <Source>
     * @param source
     */
    public <Source extends UserInterfaceProvider<? extends Node> & UserActionHelper<?>> DnDAssistant<T> source(Source source, Function<Source, T> dataSupplier) {
        source.when(User.DragStart, e -> {
            data = dataSupplier.apply(source);

            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(data));

            Dragboard db = source.ui().startDragAndDrop(TransferMode.ANY);
            db.setContent(content);

            e.consume();
        });
        return this;
    }

    /**
     * Register as DnD target.
     * 
     * @param <Target>
     * @param target
     */
    public <Target extends ValueHelper<?, T> & UserInterfaceProvider<? extends Node> & UserActionHelper<?>> DnDAssistant<T> target(Target target) {
        return target(target, Target::value);
    }

    /**
     * Register as DnD target.
     * 
     * @param <Target>
     * @param target
     * @param dataTransfer
     */
    public <Target extends UserInterfaceProvider<? extends Node> & UserActionHelper<?>> DnDAssistant<T> target(Target target, BiConsumer<Target, T> dataTransfer) {
        target.when(User.DragOver, e -> {
            if (data != null && e.getGestureSource() != target.ui()) {
                e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                e.consume();
            }
        }).when(User.DragDrop, e -> {
            if (data != null && e.getGestureSource() != target.ui()) {
                e.setDropCompleted(true);
                e.consume();

                dataTransfer.accept(target, data);
                data = null;
            }
        });
        return this;
    }
}