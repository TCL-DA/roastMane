package com.cropster.roastingintelligence.ui.chart;

import com.cropster.roastingintelligence.ui.components.FlickPredictionPanel;
import com.cropster.roastingintelligence.ui.components.PredictionPanel;
import java.awt.geom.Rectangle2D;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.layout.AnchorPane;
import org.cropster.roastingintelligence.ui.chart.CurveChart;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.fx.ChartViewer;

public class RoastChartWrapper extends ChartWrapper {
   private final ChangeListener<Number> chartSizeListner = (v, o, n) -> this.setFlickPredictionPanelPosition();
   private FlickPredictionPanel flickPredictionPanel;
   private PredictionPanel predictionPanel;

   public void init(CurveChart curveChart, boolean flickPredictionPossible) {
      this.init(curveChart);
      ChartViewer roastingChartViewer = this.getChartViewer();
      roastingChartViewer.setMinSize(300.0, 300.0);
      this.predictionPanel = new PredictionPanel();
      this.addToLeftToolbarSide(this.predictionPanel);
      if (flickPredictionPossible) {
         this.flickPredictionPanel = new FlickPredictionPanel();
         this.getChartAnchorPane().getChildren().add(this.flickPredictionPanel);
         roastingChartViewer.heightProperty().addListener(new WeakChangeListener(this.chartSizeListner));
         roastingChartViewer.widthProperty().addListener(new WeakChangeListener(this.chartSizeListner));
         this.flickPredictionPanel.getFlickPredictionMessage().heightProperty().addListener(new WeakChangeListener(this.chartSizeListner));
         PlotChangeListener plotChangeListener = pce -> {
            if (pce.getType().equals(ChartChangeEventType.GENERAL)) {
               this.setFlickPredictionPanelPosition();
            }
         };
         roastingChartViewer.getChart().getXYPlot().addChangeListener(plotChangeListener);
         AnchorPane.setRightAnchor(this.flickPredictionPanel, 0.0);
         AnchorPane.setTopAnchor(this.flickPredictionPanel, 0.0);
         AnchorPane.setBottomAnchor(this.flickPredictionPanel, 0.0);
      }
   }

   private void setFlickPredictionPanelPosition() {
      ChartViewer roastingChartViewer = this.getChartViewer();
      ChartRenderingInfo renderingInfo = roastingChartViewer.getCanvas().getRenderingInfo();
      if (renderingInfo != null) {
         Rectangle2D dataArea = renderingInfo.getPlotInfo().getSubplotInfo(0).getDataArea();
         if (dataArea != null) {
            double rightOffset = roastingChartViewer.widthProperty().get() - dataArea.getMaxX();
            this.flickPredictionPanel.getRightPadding().minWidthProperty().set(rightOffset);
            this.flickPredictionPanel.getRightPadding().maxWidthProperty().set(rightOffset);
            this.flickPredictionPanel.getRightPadding().prefWidthProperty().set(rightOffset);
            double topOffset = roastingChartViewer.getPadding().getTop()
               + dataArea.getMaxY() / 2.0
               - this.flickPredictionPanel.getFlickPredictionMessage().getHeight() / 2.0;
            this.flickPredictionPanel.getTopPadding().minHeightProperty().set(topOffset);
            this.flickPredictionPanel.getTopPadding().maxHeightProperty().set(topOffset);
            this.flickPredictionPanel.getTopPadding().prefHeightProperty().set(topOffset);
         }
      }
   }

   public FlickPredictionPanel getFlickPredictionPanel() {
      return this.flickPredictionPanel;
   }

   public PredictionPanel getPredictionPanel() {
      return this.predictionPanel;
   }
}
