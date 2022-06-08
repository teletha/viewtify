/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify.ui;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.TilePane;
import kiss.WiseFunction;
import viewtify.ui.anime.HideAnime;
import viewtify.ui.anime.LayoutAnimator;
import viewtify.ui.anime.ShowAnime;
import viewtify.ui.helper.CollectableHelper;

public class UITileView<E> extends UserInterface<UITileView<E>, TilePane> implements CollectableHelper<UITileView<E>, E> {

    /** The model. */
    private final SimpleObjectProperty<ObservableList<E>> models = new SimpleObjectProperty(FXCollections.observableArrayList());

    /** The view. */
    private final Map<E, View> views = new HashMap();

    private final LayoutAnimator animator = new LayoutAnimator();

    /** The model renderer. */
    private WiseFunction<E, View> renderer;

    /** The model modifier. */
    private final ListChangeListener<E> modelModifier = c -> {
        if (c.next()) {
            removeUI(c.getRemoved());
            addUI(c.getAddedSubList());

            ui.layout();
        }
    };

    /** The model holder modifier. */
    private final ChangeListener<ObservableList<E>> modelsModifier = (property, oldList, newList) -> {
        oldList.removeListener(modelModifier);
        removeUI(oldList);

        newList.addListener(modelModifier);
        addUI(newList);
    };

    /**
     * @param view
     */
    public UITileView(View view) {
        super(new TilePane(), view);

        models.addListener(modelsModifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Property<ObservableList<E>> itemsProperty() {
        return models;
    }

    /**
     * Configure model renderer.
     * 
     * @param renderer
     * @return
     */
    public UITileView<E> render(WiseFunction<E, View> renderer) {
        if (renderer != null && this.renderer != renderer) {
            this.renderer = renderer;

            removeUI(models.get());
            addUI(models.get());
        }
        return this;
    }

    /**
     * Add models.
     * 
     * @param models
     */
    private void addUI(Iterable<? extends E> models) {
        if (renderer != null) {
            for (E added : models) {
                View view = renderer.apply(added);
                Node node = view.ui();
                ui.getChildren().add(node);
                views.put(added, view);

                ShowAnime.FadeIn.run(ui, node, null);
            }
        }
    }

    /**
     * Remove models.
     * 
     * @param models
     */
    private void removeUI(Iterable<? extends E> models) {
        for (E removed : models) {
            View view = views.get(removed);
            if (view != null) {
                Node node = view.ui();
                HideAnime.FadeOut.run(ui, node, () -> {
                    ui.getChildren().remove(node);
                });
            }
        }
    }
}
