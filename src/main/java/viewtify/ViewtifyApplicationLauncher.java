/*
 * Copyright (C) 2018 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package viewtify;

import javafx.stage.StageStyle;

import kiss.Disposable;
import viewtify.ui.View;

/**
 * @version 2018/09/15 1:55:44
 */
public class ViewtifyApplicationLauncher {

    /** The configurable setting. */
    ActivationPolicy policy = ActivationPolicy.Latest;

    /** The configurable setting. */
    StageStyle stageStyle = StageStyle.DECORATED;

    /** The configurable setting. */
    Disposable terminations = Disposable.empty();

    /**
     * Configure {@link ActivationPolicy}.
     * 
     * @param policy
     * @return
     */
    public ViewtifyApplicationLauncher policy(ActivationPolicy policy) {
        if (policy != null) {
            this.policy = policy;
        }
        return this;
    }

    /**
     * Configure {@link StageStyle}.
     * 
     * @param style
     * @return
     */
    public ViewtifyApplicationLauncher style(StageStyle style) {
        if (style != null) {
            this.stageStyle = style;
        }
        return this;
    }

    /**
     * Add termination action.
     * 
     * @param termination
     * @return
     */
    public ViewtifyApplicationLauncher cleanup(Runnable termination) {
        if (termination != null) {
            terminations.add(termination::run);
        }
        return this;
    }

    public void activate(Class<? extends View> application) {

    }
}
