package org.jfree.chart.fx.overlay;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;

public class CrosshairOverlayFX extends CrosshairOverlay implements OverlayFX {
   @Override
   public void paintOverlay(Graphics2D g2, ChartCanvas chartCanvas) {
      if (chartCanvas.getRenderingInfo() != null) {
         Shape savedClip = g2.getClip();
         Rectangle2D dataArea = chartCanvas.getRenderingInfo().getPlotInfo().getDataArea();
         g2.clip(dataArea);
         JFreeChart chart = chartCanvas.getChart();
         XYPlot plot = (XYPlot)chart.getPlot();
         ValueAxis xAxis = plot.getDomainAxis();
         RectangleEdge xAxisEdge = plot.getDomainAxisEdge();

         for (Crosshair ch : this.getDomainCrosshairs()) {
            if (ch.isVisible()) {
               double x = ch.getValue();
               double xx = xAxis.valueToJava2D(x, dataArea, xAxisEdge);
               if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                  this.drawVerticalCrosshair(g2, dataArea, xx, ch);
               } else {
                  this.drawHorizontalCrosshair(g2, dataArea, xx, ch);
               }
            }
         }

         ValueAxis yAxis = plot.getRangeAxis();
         RectangleEdge yAxisEdge = plot.getRangeAxisEdge();

         for (Crosshair chx : this.getRangeCrosshairs()) {
            if (chx.isVisible()) {
               double y = chx.getValue();
               double yy = yAxis.valueToJava2D(y, dataArea, yAxisEdge);
               if (plot.getOrientation() == PlotOrientation.VERTICAL) {
                  this.drawHorizontalCrosshair(g2, dataArea, yy, chx);
               } else {
                  this.drawVerticalCrosshair(g2, dataArea, yy, chx);
               }
            }
         }

         g2.setClip(savedClip);
      }
   }
}
