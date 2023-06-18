/*
 * Copyright (C) 2023 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
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

public abstract class AbstractPane<E, P extends Pane, Self extends AbstractPane> extends UserInterface<AbstractPane<E, P, Self>, P>
        implements CollectableHelper<AbstractPane<E, P, Self>, E> {

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
                Map<Integer, Integer> mapping = new HashMap();
                for (int i = c.getFrom(); i < c.getTo(); i++) {
                    int start = i;
                    int end = c.getPermutation(i);

                    if (start != end && (!mapping.containsKey(end) || !mapping.containsValue(start))) {
                        mapping.put(start, end);
                        swapUI(i, c.getPermutation(i));
                    }
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
    protected AbstractPane(P pane, View view) {
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

            ObservableList<E> list = models.get();
            if (list != null) {
                removeUI(list);
                addUI(list);
            }
        }
        return (Self) this;
    }

    /**
     * Add UI.
     * 
     * @param models
     */
    private void addUI(Iterable<? extends E> models) {
        if (renderer != null) {
            for (E added : models) {
                View view = renderer.apply(added);
                Node node = view.ui();
                views.put(added, view);
                animator.observe(node);

                ui.getChildren().add(this.models.get().indexOf(added), node);

                ShowAnime.FadeIn(1).run(ui, node);
            }
        }
    }

    /**
     * Remove UI.
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
                    view.dispose();
                });
            }
        }
    }

    /**
     * Swap UI.
     * 
     * @param start
     * @param end
     */
    private void swapUI(int start, int end) {
        if (start != end) {
            ObservableList<Node> nodes = ui.getChildren();
            if (start < end) {
                Node swap = nodes.get(start);
                nodes.set(start, nodes.remove(end));
                nodes.add(end, swap);
            } else {
                Node swap = nodes.get(end);
                nodes.set(end, nodes.remove(start));
                nodes.add(start, swap);
            }
        }
    }
}