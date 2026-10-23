package com.cropster.roastingintelligence.ui.chart;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.apache.commons.lang3.tuple.Triple;
import org.cropster.roastingintelligence.util.Measures;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ChartMouseEventFX;
import org.jfree.chart.fx.interaction.ChartMouseListenerFX;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.data.general.DatasetUtils;
import org.jfree.data.xy.XYDataset;

public class MouseCoordinatesTracker implements ChartMouseListenerFX {
   private static final String BEAN_TEMPERATURE_ROR = "beanTemperatureRoR";
   private static final String BEAN_TEMPERATURE_BB = Measures.curveNameForBetweenBatch("beanTemperature");
   private static final String DEFAULT_TEXT = "--";
   private static final String DEFAULT_TEXT_COLOR_STYLE = "-fx-text-fill: -fx-color-text;";
   private static final String HIGHLIGHT_STYLE_CLASS = "highlight-crosshair";
   private static final String NUMBER_FORMAT = "{0,number,#0.0}{1}";
   private static final double HIGHLIGH_DOT_RADIUS = 3.33;
   private final AnchorPane anchorPane;
   private final List<MouseCoordinatesTracker.DotData> dots = new ArrayList<>(2);
   private final MouseCoordinatesTracker.VerticalCrosshair verticakCrosshair = new MouseCoordinatesTracker.VerticalCrosshair();
   private double chartMouseX = -1.0;
   private ChartViewer chartViewer;
   private MouseCoordinatesPane mouseCoordinatesPane;

   public MouseCoordinatesTracker(ChartViewer chartViewer, MouseCoordinatesPane mouseCoordinatesPane, AnchorPane anchorPane) {
      this.anchorPane = anchorPane;
      this.chartViewer = chartViewer;
      this.mouseCoordinatesPane = mouseCoordinatesPane;
      chartViewer.addChartMouseListener(this);
      anchorPane.getChildren().add(this.verticakCrosshair);
      this.resetLabels();
      chartViewer.getChart().addChangeListener(e -> {
         if (this.chartMouseX >= 0.0) {
            this.drawCrosshairs(this.chartMouseX, chartViewer.getCanvas().getRenderingInfo().getPlotInfo());
         }
      });
   }

   public void chartMouseClicked(ChartMouseEventFX event) {
   }

   public void chartMouseMoved(ChartMouseEventFX e) {
      PlotRenderingInfo plotInfo = this.chartViewer.getCanvas().getRenderingInfo().getPlotInfo();
      double mx = e.getTrigger().getX();
      double my = e.getTrigger().getY();
      if (plotInfo.getDataArea().contains(mx, my)) {
         this.drawCrosshairs(mx, plotInfo);
         this.chartMouseX = mx;
      } else if (this.verticakCrosshair.isVisible()) {
         this.verticakCrosshair.setVisible(false);
         this.dots.forEach(d -> d.dot().setVisible(false));
         this.resetLabels();
         this.chartMouseX = -1.0;
      }
   }

   private void drawCrosshairs(double mx, PlotRenderingInfo plotInfo) {
      Rectangle2D area = plotInfo.getDataArea();
      Bounds bip = this.chartViewer.getBoundsInParent();
      double offsetX = bip.getMinX();
      double offsetY = bip.getMinY();
      double cx = mx + offsetX;
      this.verticakCrosshair.setStartX(cx);
      this.verticakCrosshair.setEndX(cx);
      this.verticakCrosshair.setStartY(area.getMinY() + offsetY);
      this.verticakCrosshair.setEndY(area.getMaxY() + offsetY);
      if (!this.verticakCrosshair.isVisible()) {
         this.verticakCrosshair.setVisible(true);
      }

      JFreeChart chart;
      double x;
      String var10000;
      label19: {
         chart = this.chartViewer.getChart();
         XYPlot plot = (XYPlot)chart.getPlot();
         ValueAxis xAxis = plot.getDomainAxis();
         x = xAxis.java2DToValue(mx, area, RectangleEdge.BOTTOM);
         if (xAxis instanceof NumberAxis numAxis && xAxis.getRange().contains(x)) {
            var10000 = numAxis.getTickUnit().valueToString(x);
            break label19;
         }

         var10000 = "--";
      }

      String time = var10000;
      if (!time.equals(this.mouseCoordinatesPane.getTimeLabel().getText())) {
         this.mouseCoordinatesPane.getTimeLabel().setText(time);
      }

      this.dots.forEach(dd -> {
         double y = DatasetUtils.findYValue(dd.dataset(), 0, x);
         this.drawHorizontalCrosshair(dd, plotInfo, chart, mx, y, offsetX, offsetY);
      });
      this.updateVerticalCrosshairStyle();
   }

   private void drawHorizontalCrosshair(
      MouseCoordinatesTracker.DotData dd, PlotRenderingInfo plotInfo, JFreeChart chart, double dx, double y, double offsetX, double offsetY
   ) {
      MouseCoordinatesTracker.DotCrosshair dot = dd.dot();
      XYPlot xyp = dd.plot();
      int datasetIndex = dd.datasetIndex();
      XYDataset dataset = xyp.getDataset(datasetIndex);
      if (dataset != null) {
         Object seriesKey = dataset.getSeriesKey(0);
         boolean beanTemperature = "beanTemperature".equals(seriesKey) || BEAN_TEMPERATURE_BB.equals(seriesKey);
         ValueAxis yAxis = xyp.getRangeAxisForDataset(datasetIndex);
         if (yAxis != null) {
            RectangleEdge yAxisEdge = xyp.getRangeAxisEdge(xyp.getRangeAxisIndex(yAxis));
            int i = ((CombinedDomainXYPlot)chart.getXYPlot()).getSubplots().indexOf(xyp);
            if (i >= 0) {
               PlotRenderingInfo subplotInfo = plotInfo.getSubplotInfo(i);
               Rectangle2D dArea = subplotInfo.getDataArea();
               double dy = yAxis.valueToJava2D(y, dArea, yAxisEdge);
               if (dArea.contains(dx, dy)) {
                  this.drawDot(dx, offsetX, offsetY, dot, dy);
                  boolean beanTemperatureRor = "beanTemperatureRoR".equals(seriesKey);
                  if (beanTemperature || beanTemperatureRor) {
                     this.updateLabels(dd, y, beanTemperature, yAxis, xyp, dataset);
                  }

                  return;
               }
            }
         }

         this.hideDot(dot, beanTemperature);
      }
   }

   private void drawDot(double dx, double offsetX, double offsetY, MouseCoordinatesTracker.DotCrosshair dot, double dy) {
      dot.setCenterX(dx + offsetX);
      dot.setCenterY(dy + offsetY);
      if (!dot.isVisible()) {
         dot.setVisible(true);
      }
   }

   private void hideDot(MouseCoordinatesTracker.DotCrosshair dot, boolean beanTemperature) {
      if (dot.isVisible()) {
         dot.setVisible(false);
         if (beanTemperature) {
            this.mouseCoordinatesPane.getTemperatureLabel().setText("--");
            this.mouseCoordinatesPane.getTemperatureLabel().setStyle("-fx-text-fill: -fx-color-text;");
         } else {
            this.mouseCoordinatesPane.getRorLabel().setText("--");
            this.mouseCoordinatesPane.getRorLabel().setStyle("-fx-text-fill: -fx-color-text;");
         }
      }
   }

   private void updateLabels(MouseCoordinatesTracker.DotData dd, double y, boolean beanTemperature, ValueAxis yAxis, XYPlot xyp, XYDataset dataset) {
      String currentValue = beanTemperature ? this.mouseCoordinatesPane.getTemperatureLabel().getText() : this.mouseCoordinatesPane.getRorLabel().getText();
      String value = yAxis.getRange().contains(y) ? MessageFormat.format("{0,number,#0.0}{1}", y, xyp.getRangeAxis().getLabel()) : "--";
      if (!value.equals(currentValue)) {
         Paint paint = xyp.getRendererForDataset(dataset).getSeriesPaint(0);
         String textColorStyle = dd.dot().getTextColorStyle((Color)paint);
         if (beanTemperature) {
            this.mouseCoordinatesPane.getTemperatureLabel().setText(value);
            this.mouseCoordinatesPane.getTemperatureLabel().setStyle(textColorStyle);
         } else {
            this.mouseCoordinatesPane.getRorLabel().setText(value);
            this.mouseCoordinatesPane.getRorLabel().setStyle(textColorStyle);
         }
      }
   }

   private void updateVerticalCrosshairStyle() {
      ObservableList<String> styleClassList = this.verticakCrosshair.getStyleClass();
      if (this.dots.stream().anyMatch(dd -> dd.dot().isVisible())) {
         if (!styleClassList.contains("highlight-crosshair")) {
            styleClassList.add("highlight-crosshair");
         }
      } else if (styleClassList.contains("highlight-crosshair")) {
         styleClassList.remove("highlight-crosshair");
      }
   }

   public void updateCrosshairs() {
      this.dots.forEach(d -> this.anchorPane.getChildren().remove(d.dot()));
      this.dots.clear();
      this.mouseCoordinatesPane.setRorVisible(false);
      JFreeChart chart = this.chartViewer.getChart();
      ((CombinedDomainXYPlot)chart.getXYPlot()).getSubplots().stream().flatMap(sp -> {
         XYPlot xyp = (XYPlot)sp;
         List<Triple<XYPlot, Integer, XYDataset>> datasets = new ArrayList<>();

         for (int i = 0; i < xyp.getDatasetCount(); i++) {
            XYDataset ds = xyp.getDataset(i);
            if (ds != null) {
               Object k = ds.getSeriesKey(0);
               boolean bt = "beanTemperature".equals(k) || BEAN_TEMPERATURE_BB.equals(k);
               boolean btror = "beanTemperatureRoR".equals(k);
               if (btror) {
                  this.mouseCoordinatesPane.setRorVisible(true);
               }

               if (bt || btror) {
                  datasets.add(Triple.of(xyp, i, ds));
               }
            }
         }

         return datasets.stream();
      }).forEach(t -> {
         Triple<XYPlot, Integer, XYDataset> tt = (Triple<XYPlot, Integer, XYDataset>)t;
         XYPlot xyp = (XYPlot)tt.getLeft();
         XYDataset dataset = (XYDataset)tt.getRight();
         MouseCoordinatesTracker.DotCrosshair dot = new MouseCoordinatesTracker.DotCrosshair((Color)xyp.getRendererForDataset(dataset).getSeriesPaint(0));
         this.dots.add(new MouseCoordinatesTracker.DotData(xyp, (Integer)tt.getMiddle(), dataset, dot));
         this.anchorPane.getChildren().add(dot);
      });
      this.resetLabels();
   }

   private void resetLabels() {
      this.mouseCoordinatesPane.getTemperatureLabel().setText("--");
      this.mouseCoordinatesPane.getTimeLabel().setText("--");
      this.mouseCoordinatesPane.getRorLabel().setText("--");
   }

   private class DotCrosshair extends Circle {
      DotCrosshair(Color fillColor) {
         super(3.33);
         this.getStyleClass().add("dot-crosshair");
         this.setStyle(this.getDotColorStyle(fillColor));
         this.setManaged(false);
         this.setMouseTransparent(true);
         this.setVisible(false);
      }

      public String getTextColorStyle(Color color) {
         return "-fx-text-fill: #" + JavaFxUtil.colorToHexString(JavaFxUtil.colorFromAwt(color)) + ";";
      }

      private String getDotColorStyle(Color color) {
         return "-fx-fill: #" + JavaFxUtil.colorToHexString(JavaFxUtil.colorFromAwt(color)) + ";";
      }
   }

   private static record DotData(XYPlot plot, int datasetIndex, XYDataset dataset, MouseCoordinatesTracker.DotCrosshair dot) {
   }

   private class VerticalCrosshair extends Line {
      VerticalCrosshair() {
         this.getStyleClass().add("vertical-crosshair");
         this.setManaged(false);
         this.setMouseTransparent(true);
         this.setVisible(false);
      }
   }
}
