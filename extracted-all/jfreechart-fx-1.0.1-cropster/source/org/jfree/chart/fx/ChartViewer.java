package org.jfree.chart.fx;

import java.io.File;
import java.io.IOException;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.fx.interaction.ZoomHandlerFX;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ExportUtils;

public class ChartViewer extends Region {
   private ChartCanvas canvas;
   private Rectangle zoomRectangle;
   private ContextMenu contextMenu;

   public ChartViewer() {
      this(null);
   }

   public ChartViewer(JFreeChart chart) {
      this(chart, true);
   }

   public ChartViewer(JFreeChart chart, boolean contextMenuEnabled) {
      this.canvas = new ChartCanvas(chart);
      this.canvas.setTooltipEnabled(true);
      this.canvas.addMouseHandler(new ZoomHandlerFX("zoom", this));
      this.setFocusTraversable(true);
      this.getChildren().add(this.canvas);
      this.zoomRectangle = new Rectangle(0.0, 0.0, new Color(0.0, 0.0, 1.0, 0.25));
      this.zoomRectangle.setManaged(false);
      this.zoomRectangle.setVisible(false);
      this.getChildren().add(this.zoomRectangle);
      this.contextMenu = this.createContextMenu();
      this.setOnContextMenuRequested(event -> this.contextMenu.show(this.getScene().getWindow(), event.getScreenX(), event.getScreenY()));
      this.contextMenu.setOnShowing(e -> this.getCanvas().setTooltipEnabled(false));
      this.contextMenu.setOnHiding(e -> this.getCanvas().setTooltipEnabled(true));
   }

   public JFreeChart getChart() {
      return this.canvas.getChart();
   }

   public void setChart(JFreeChart chart) {
      Args.nullNotPermitted(chart, "chart");
      this.canvas.setChart(chart);
   }

   public ChartCanvas getCanvas() {
      return this.canvas;
   }

   public ContextMenu getContextMenu() {
      return this.contextMenu;
   }

   public ChartRenderingInfo getRenderingInfo() {
      return this.getCanvas().getRenderingInfo();
   }

   public Paint getZoomFillPaint() {
      return this.zoomRectangle.getFill();
   }

   public void setZoomFillPaint(Paint paint) {
      this.zoomRectangle.setFill(paint);
   }

   protected void layoutChildren() {
      super.layoutChildren();
      this.canvas.setLayoutX(0.0);
      this.canvas.setLayoutY(0.0);
      this.canvas.setWidth(this.getWidth());
      this.canvas.setHeight(this.getHeight());
   }

   public void addChartMouseListener(ChartMouseListenerFX listener) {
      Args.nullNotPermitted(listener, "listener");
      this.canvas.addChartMouseListener(listener);
   }

   public void removeChartMouseListener(ChartMouseListenerFX listener) {
      Args.nullNotPermitted(listener, "listener");
      this.canvas.removeChartMouseListener(listener);
   }

   private ContextMenu createContextMenu() {
      ContextMenu menu = new ContextMenu();
      menu.setAutoHide(true);
      Menu export = new Menu("Export As");
      MenuItem pngItem = new MenuItem("PNG...");
      pngItem.setOnAction(e -> this.handleExportToPNG());
      export.getItems().add(pngItem);
      MenuItem jpegItem = new MenuItem("JPEG...");
      jpegItem.setOnAction(e -> this.handleExportToJPEG());
      export.getItems().add(jpegItem);
      if (ExportUtils.isOrsonPDFAvailable()) {
         MenuItem pdfItem = new MenuItem("PDF...");
         pdfItem.setOnAction(e -> this.handleExportToPDF());
         export.getItems().add(pdfItem);
      }

      if (ExportUtils.isJFreeSVGAvailable()) {
         MenuItem svgItem = new MenuItem("SVG...");
         svgItem.setOnAction(e -> this.handleExportToSVG());
         export.getItems().add(svgItem);
      }

      menu.getItems().add(export);
      return menu;
   }

   private void handleExportToPDF() {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Export to PDF");
      ExtensionFilter filter = new ExtensionFilter("Portable Document Format (PDF)", new String[]{"*.pdf"});
      chooser.getExtensionFilters().add(filter);
      File file = chooser.showSaveDialog(this.getScene().getWindow());
      if (file != null) {
         ExportUtils.writeAsPDF(this.canvas.getChart(), (int)this.getWidth(), (int)this.getHeight(), file);
      }
   }

   private void handleExportToSVG() {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Export to SVG");
      ExtensionFilter filter = new ExtensionFilter("Scalable Vector Graphics (SVG)", new String[]{"*.svg"});
      chooser.getExtensionFilters().add(filter);
      File file = chooser.showSaveDialog(this.getScene().getWindow());
      if (file != null) {
         ExportUtils.writeAsSVG(this.canvas.getChart(), (int)this.getWidth(), (int)this.getHeight(), file);
      }
   }

   private void handleExportToPNG() {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Export to PNG");
      ExtensionFilter filter = new ExtensionFilter("Portable Network Graphics (PNG)", new String[]{"*.png"});
      chooser.getExtensionFilters().add(filter);
      File file = chooser.showSaveDialog(this.getScene().getWindow());
      if (file != null) {
         try {
            ExportUtils.writeAsPNG(this.canvas.getChart(), (int)this.getWidth(), (int)this.getHeight(), file);
         } catch (IOException var5) {
            throw new RuntimeException(var5);
         }
      }
   }

   private void handleExportToJPEG() {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("Export to JPEG");
      ExtensionFilter filter = new ExtensionFilter("JPEG", new String[]{"*.jpg"});
      chooser.getExtensionFilters().add(filter);
      File file = chooser.showSaveDialog(this.getScene().getWindow());
      if (file != null) {
         try {
            ExportUtils.writeAsJPEG(this.canvas.getChart(), (int)this.getWidth(), (int)this.getHeight(), file);
         } catch (IOException var5) {
            throw new RuntimeException(var5);
         }
      }
   }

   public void showZoomRectangle(double x, double y, double w, double h) {
      this.zoomRectangle.setX(x);
      this.zoomRectangle.setY(y);
      this.zoomRectangle.setWidth(w);
      this.zoomRectangle.setHeight(h);
      this.zoomRectangle.setVisible(true);
   }

   public void hideZoomRectangle() {
      this.zoomRectangle.setVisible(false);
   }
}
