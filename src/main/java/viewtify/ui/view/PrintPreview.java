/*
 * Copyright (C) 2024 The VIEWTIFY Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package viewtify.ui.view;

import static javafx.print.PageOrientation.PORTRAIT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.SegmentedButton;

import javafx.event.Event;
import javafx.geometry.Bounds;
import javafx.print.JobSettings;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrintColor;
import javafx.print.PrintQuality;
import javafx.print.PrintSides;
import javafx.print.Printer;
import javafx.print.Printer.MarginType;
import javafx.print.PrinterJob;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import kiss.I;
import kiss.WiseSupplier;
import stylist.Style;
import stylist.StyleDSL;
import stylist.value.Color;
import viewtify.Viewtify;
import viewtify.ViewtyDialog.DialogView;
import viewtify.style.FormStyles;
import viewtify.ui.UIComboBox;
import viewtify.ui.UIHBox;
import viewtify.ui.UIImage;
import viewtify.ui.UILabel;
import viewtify.ui.UISpinner;
import viewtify.ui.UIText;
import viewtify.ui.UIToggleButton;
import viewtify.ui.ViewDSL;
import viewtify.ui.anime.Anime;
import viewtify.ui.helper.User;
import viewtify.ui.view.PrintPreview.PrintInfo;
import viewtify.util.FXUtils;

public class PrintPreview extends DialogView<PrintInfo> {

    private static final List<Paper> JP = List.of(Paper.A3, Paper.A4, Paper.A5, Paper.JIS_B5, Paper.JIS_B6, Paper.JAPANESE_POSTCARD);

    private static final Map<String, List<Paper>> PaperSet = Map.of("jp", JP);

    /** The root pane. */
    private UIHBox rootBox;

    /** The main image view. */
    private UIImage view;

    /** The printer UI. */
    private UILabel pageSize;

    /** The printer UI. */
    private UISpinner<Integer> copies;

    /** The printer UI. */
    private UIText<Integer> pager;

    /** The printer UI. */
    private UIComboBox<Printer> printer;

    /** The printer UI. */
    private UIComboBox<Paper> paper;

    /** The printer UI. */
    private UIComboBox<PrintColor> color;

    /** The printer UI. */
    private UIComboBox<PageOrientation> orientation;

    /** The printer UI. */
    private UIComboBox<PrintSides> side;

    /** The printer UI. */
    private UIComboBox<PrintQuality> quality;

    /** The max page size. */
    private int maxPage;

    /** The current page index. */
    private int currentPage = -1;

    /** The page image set. */
    private WritableImage[] images;

    /** The navigation UI. */
    private UIToggleButton start;

    /** The navigation UI. */
    private UIToggleButton prev;

    /** The navigation UI. */
    private UIToggleButton location;

    /** The navigation UI. */
    private UIToggleButton next;

    /** The navigation UI. */
    private UIToggleButton end;

    /** The navigation UI. */
    private SegmentedButton navi = new SegmentedButton();

    private boolean naviShowing;

    public PrintPreview() {
        this.value = new PrintInfo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ViewDSL declareUI() {
        return new ViewDSL() {
            {
                $(rootBox, () -> {
                    $(sbox, () -> {
                        $(view);
                        $(() -> navi, style.navi);
                    });
                    $(vbox, style.side, FormStyles.Label90, FormStyles.LabelCenter, () -> {
                        $(hbox, FormStyles.Row, style.title, () -> {
                            label(en("Settings"));
                            $(pageSize, FormStyles.Column5, style.pageSize);
                        });

                        form(en("Number of copies"), FormStyles.Column5, copies);
                        form(en("Page"), FormStyles.Column5, pager);
                        form(en("Printer"), FormStyles.Column5, printer);
                        $(hbox, FormStyles.Row, style.advanced, () -> {
                            label(en("Advanced Settings"));
                        });
                        form(en("Paper"), FormStyles.Column5, paper);
                        form(en("Color mode"), FormStyles.Column5, color);
                        form(en("Print orientation"), FormStyles.Column5, orientation);
                        form(en("Print side"), FormStyles.Column5, side);
                        form(en("Quality"), FormStyles.Column5, quality);
                    });
                });
            }
        };
    }

    interface style extends StyleDSL {
        Style side = () -> {
            display.minWidth(250, px);
            margin.left(22, px);
        };

        Style pageSize = () -> {
            text.align.right();
        };

        Style title = () -> {
            border.bottom.solid().width(1, px).color(Color.White);
            margin.bottom(10, px);
            padding.bottom(7, px);
            font.weight.bolder();
        };

        Style advanced = () -> {
            title.style();
            margin.top(33, px);
        };

        Style navi = () -> {
            position.absolute().top(5, px);
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initialize() {
        copies.ui.setValueFactory(new IntegerSpinnerValueFactory(1, 300));

        pager.placeholder(en("all pages")).disable(true);
        printer.items(Printer.getAllPrinters()).value(Printer.getDefaultPrinter()).render(Printer::getName);
        paper.items(PaperSet.getOrDefault(I.Lang.v, JP)).placeholder(en("Default")).renderByVariable(x -> en(x.getName()));
        color.items(PrintColor.values()).value(PrintColor.COLOR).renderByVariable(x -> en(x.name()));
        orientation.items(PageOrientation.values()).placeholder(en("Default")).renderByVariable(x -> en(x.name()));
        side.items(PrintSides.values()).value(PrintSides.ONE_SIDED).renderByVariable(x -> en(x.name()));
        quality.items(PrintQuality.values()).value(PrintQuality.NORMAL).renderByVariable(x -> en(x.name()));

        start.text("<<").focusable(false).when(User.MousePress, e -> drawPage(0, e));
        prev.text("<").focusable(false).when(User.MousePress, e -> drawPage(currentPage - 1, e));
        location.focusable(false).ignore(User.MousePress);
        next.text(">").focusable(false).when(User.MousePress, e -> drawPage(currentPage + 1, e));
        end.text(">>").focusable(false).when(User.MousePress, e -> drawPage(maxPage - 1, e));
        navi.getButtons().addAll(start.ui, prev.ui, location.ui, next.ui, end.ui);
        navi.setVisible(false);
        navi.setOpacity(0);

        // bind configuration
        copies.observing().to(v -> value.copies = v);
        paper.observing().to(v -> value.paper = v);
        pager.observing(true).to(v -> value.pageSize = v);
        printer.observing().to(v -> value.printer = v);
        color.observing().to(v -> value.color = v);
        orientation.observing().to(v -> value.orientation = v);
        side.observing().to(v -> value.side = v);
        quality.observing().to(v -> value.quality = v);

        view.when(User.Scroll, e -> {
            if (0 < e.getDeltaY()) {
                drawPage(currentPage - 1);
            } else {
                drawPage(currentPage + 1);
            }
        });

        rootBox.when(User.MouseMove, e -> {
            if (view.ui.getLayoutBounds().contains(e.getX() - 10, e.getY() - 23)) {
                if (!naviShowing) {
                    naviShowing = true;
                    Anime.define().opacity(navi, 1).run();
                }
            } else {
                if (naviShowing) {
                    naviShowing = false;
                    Anime.define().opacity(navi, 0).run();
                }
            }
        });
    }

    /**
     * @param images
     */
    public void loadImage(WritableImage... images) {
        this.maxPage = images.length;
        this.images = images;

        navi.setVisible(maxPage > 1);
        pageSize.text(en("{0} pages", images.length));

        value.pageSize = images.length;
        for (int i = 0; i < images.length; i++) {
            value.pages.add(i);
        }

        drawPage(0);
    }

    /**
     * @param builder
     */
    public void loadImageLazy(int width, int height, PageOrientation orientation, String placeholder, WiseSupplier<List<WritableImage>> builder) {
        view.value(orientation == PORTRAIT ? spacer(width, height, placeholder) : spacer(height, width, placeholder));

        Viewtify.inWorker(() -> {
            loadImage(builder.get().toArray(WritableImage[]::new));
        });
    }

    /**
     * Create spacer image.
     * 
     * @param width
     * @param height
     * @param placeholder
     * @return
     */
    private WritableImage spacer(int width, int height, String placeholder) {
        WritableImage image = new WritableImage(width, height);
        PixelWriter writer = image.getPixelWriter();

        Text text = new Text(placeholder);
        text.setFont(Font.font("Arial", 24));
        text.setFill(FXUtils.color(Color.rgb(50, 50, 50, 0.6)));

        Bounds bounds = text.getBoundsInLocal();
        int textX = width / 2 - (int) (bounds.getWidth() / 2);
        int textY = height / 2 + (int) (bounds.getHeight() / 2);

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(javafx.scene.paint.Color.TRANSPARENT);
        WritableImage textImage = text.snapshot(params, null);

        for (int x = 0; x < textImage.getWidth(); x++) {
            for (int y = 0; y < textImage.getHeight(); y++) {
                javafx.scene.paint.Color color = textImage.getPixelReader().getColor(x, y);
                if (!color.equals(javafx.scene.paint.Color.TRANSPARENT)) {
                    writer.setColor(textX + x, textY + y, color);
                }
            }
        }

        return image;
    }

    /**
     * Draw the specified page.
     */
    private void drawPage(int page, Event e) {
        e.consume();
        drawPage(page);
    }

    /**
     * Draw the specified page.
     */
    private void drawPage(int page) {
        if (page < 0 || maxPage <= page || (page == currentPage)) {
            return;
        }

        currentPage = page;

        Viewtify.inUI(() -> {
            location.text("  " + (currentPage + 1) + " / " + maxPage + "  ");
        });

        WritableImage image = images[currentPage];
        view.ui.setFitWidth(image.getWidth());
        view.ui.setFitHeight(image.getHeight());

        Anime.define().opacity(view, 0.1).run(() -> {
            view.value(image);
            Anime.define().opacity(view, 1).run();
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        super.dispose();

        view.value((Image) null);
        view = null;
        navi = null;
    }

    /**
     * 
     */
    public static class PrintInfo {
        public int copies;

        public int pageSize;

        public List<Integer> pages = new ArrayList();

        public Paper paper;

        public Printer printer;

        public PrintColor color;

        public PageOrientation orientation;

        public PrintSides side;

        public PrintQuality quality;

        /**
         * Convenience method to print image.
         */
        public void print(WritableImage image) {
            ImageView view = new ImageView(image);

            PrinterJob printerJob = PrinterJob.createPrinterJob();
            if (printerJob != null) {
                JobSettings setting = printerJob.getJobSettings();
                if (0 < copies) setting.setCopies(copies);
                if (color != null) setting.setPrintColor(color);
                if (quality != null) setting.setPrintQuality(quality);
                if (side != null) setting.setPrintSides(side);
                if (pages != null && orientation != null)
                    setting.setPageLayout(printerJob.getPrinter().createPageLayout(paper, orientation, MarginType.DEFAULT));

                view.setFitWidth(setting.getPageLayout().getPrintableWidth());
                view.setFitHeight(setting.getPageLayout().getPrintableHeight());

                if (printerJob.printPage(view)) {
                    printerJob.endJob();
                }
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "PrintInfo [copies=" + copies + ", pageSize=" + pageSize + ", pages=" + pages + ", paper=" + paper + ", printer=" + printer + ", color=" + color + ", orientation=" + orientation + ", side=" + side + ", quality=" + quality + "]";
        }
    }
}