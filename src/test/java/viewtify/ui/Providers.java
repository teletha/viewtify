/*
 * Copyright (C) 2023 Nameless Production Committee
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
import java.util.function.Supplier;
import java.util.stream.Stream;

import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.MenuItem;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import viewtify.ui.helper.LabelHelper;

public class Providers {

    private static final Map<Class, Supplier> builders = new HashMap();

    static {
        builders.put(UIButton.class, () -> new UIButton(null));
        builders.put(UICheckBox.class, () -> new UICheckBox(null));
        builders.put(UICheckMenuItem.class, () -> new UICheckMenuItem(new CheckMenuItem()));
        builders.put(UICheckSwitch.class, () -> new UICheckSwitch(null));
        builders.put(UIChoiceBox.class, () -> new UIChoiceBox(null));
        builders.put(UIColorPicker.class, () -> new UIColorPicker(null));
        builders.put(UIComboBox.class, () -> new UIComboBox(null));
        builders.put(UIComboCheckBox.class, () -> new UIComboCheckBox(null));
        builders.put(UIDatePicker.class, () -> new UIDatePicker(null));
        builders.put(UIFlowView.class, () -> new UIFlowView(null));
        builders.put(UIFontPicker.class, () -> new UIFontPicker(null));
        builders.put(UIGridView.class, () -> new UIGridView(null));
        builders.put(UIHBox.class, () -> new UIHBox(null));
        builders.put(UIImage.class, () -> new UIImage(null));
        builders.put(UILabel.class, () -> new UILabel(null));
        builders.put(UILineChart.class, () -> new UILineChart(null));
        builders.put(UIListView.class, () -> new UIListView(null));
        builders.put(UIMenuItem.class, () -> new UIMenuItem(new MenuItem()));
        builders.put(UIPane.class, () -> new UIPane(null));
        builders.put(UIPieChart.class, () -> new UIPieChart(null));
        builders.put(UIProgressBar.class, () -> new UIProgressBar(null));
        builders.put(UIScrollPane.class, () -> new UIScrollPane(null));
        builders.put(UISegmentedButton.class, () -> new UISegmentedButton(null));
        builders.put(UISelectPane.class, () -> new UISelectPane(null));
        builders.put(UISlidePane.class, () -> new UISlidePane(null));
        builders.put(UISpinner.class, () -> new UISpinner(null));
        builders.put(UISplitPane.class, () -> new UISplitPane(null));
        builders.put(UIStackPane.class, () -> new UIStackPane(null));
        builders.put(UITab.class, () -> new UITab(null));
        builders.put(UITableCheckBoxColumn.class, () -> new UITableCheckBoxColumn(null, String.class));
        builders.put(UITableColumn.class, () -> new UITableColumn(null, String.class, String.class));
        builders.put(UITableView.class, () -> new UITableView(null));
        builders.put(UITabPane.class, () -> new UITabPane(null));
        builders.put(UIText.class, () -> new UIText(null, String.class));
        builders.put(UITextArea.class, () -> new UITextArea(null));
        builders.put(UITileView.class, () -> new UITileView(null));
        builders.put(UIToggleButton.class, () -> new UIToggleButton(null));
        builders.put(UIToolBar.class, () -> new UIToolBar(null));
        builders.put(UITreeTableView.class, () -> new UITreeTableView(null));
        builders.put(UIVBox.class, () -> new UIVBox(null));
        builders.put(UIWeb.class, () -> new UIWeb(null));
    }

    private static <T> Stream<Arguments> collect(Class<T> type) {
        return builders.entrySet()
                .stream()
                .filter(e -> type.isAssignableFrom(e.getKey()))
                .map(e -> (T) e.getValue().get())
                .map(Arguments::of);
    }

    public static class LabelHelpers implements ArgumentsProvider {
        /**
         * {@inheritDoc}
         */
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
            return collect(LabelHelper.class);
        }
    }

}