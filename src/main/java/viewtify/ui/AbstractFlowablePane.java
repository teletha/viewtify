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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import kiss.WiseFunction;
import viewtify.ui.anime.HideAnime;
import viewtify.ui.anime.LayoutAnimator;
import viewtify.ui.anime.ShowAnime;
import viewtify.ui.helper.CollectableHelper;

public abstract class AbstractFlowablePane<E, P extends Pane, Self extends AbstractFlowablePane>
        extends UserInterface<AbstractFlowablePane<E, P, Self>, P>
        implements CollectableHelper<AbstractFlowablePane<E, P, Self>, E> {

    /** The model. */
    private final SimpleObjectProperty<ObservableList<E>> models = new SimpleObjectProperty();

    /** The view. */
    private final Map<E, View> views = new HashMap();

    private final LayoutAnimator animator = new LayoutAnimator();

    /** The model renderer. */
    private WiseFunction<E, View> renderer;

    /** The model modifier. */
    private final ListChangeListener<E> modelModifier = c -> {
        while (c.next()) {
            if (c.wasPermutated()) {
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    moveUI(i, c.getPermutation(i));
                }
            } else if (c.wasUpdated()) {
                // ignore
            } else {
                List<? extends E> added = c.getAddedSubList();
                List<? extends E> removed = c.getRemoved();
                List<? extends E> intersect = added.stream().filter(removed::contains).toList();
                List<E> addedPure = new ArrayList(added);
                addedPure.removeAll(intersect);
                List<E> removedPure = new ArrayList(removed);
                removedPure.removeAll(intersect);

                removeUI(removedPure);
                addUI(addedPure);
            }
        }
    };

    /** The model holder modifier. */
    private final ChangeListener<ObservableList<E>> modelsModifier = (property, oldList, newList) -> {
        if (oldList != null) {
            removeUI(oldList);
            oldList.removeListener(modelModifier);
        }

        if (newList != null) {
            newList.addListener(modelModifier);
            addUI(newList);
        }
    };

    /**
     * @param view
     */
    protected AbstractFlowablePane(P pane, View view) {
        super(pane, view);

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
    public Self render(WiseFunction<E, View> renderer) {
        if (renderer != null && this.renderer != renderer) {
            this.renderer = renderer;

            removeUI(models.get());
            addUI(models.get());
        }
        return (Self) this;
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
                ui.getChildren().add(this.models.get().indexOf(added), node);
                views.put(added, view);
                animator.observe(node);

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
                    animator.unobserve(node);
                    view.disposer.dispose();
                });
            }
        }
    }

    private void moveUI(int start, int end) {

    }
}
