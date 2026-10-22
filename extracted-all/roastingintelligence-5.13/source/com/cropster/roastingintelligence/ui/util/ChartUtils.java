package com.cropster.roastingintelligence.ui.util;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import javafx.scene.input.ScrollEvent;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.fx.interaction.ScrollHandlerFX;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.Zoomable;

public final class ChartUtils {
   public static void replaceZoomHandler(ChartCanvas chartCanvas) {
      chartCanvas.removeAuxiliaryMouseHandler(chartCanvas.getMouseHandler("scroll"));
      chartCanvas.addAuxiliaryMouseHandler(new ScrollHandlerFX("scroll") {
         public void handleScroll(ChartCanvas canvas, ScrollEvent e) {
            if (e.isShortcutDown()) {
               super.handleScroll(canvas, e);
            }
         }
      });
   }

   public static void restoreZoom(ChartViewer chartViewer) {
      if (chartViewer != null) {
         JFreeChart chart = chartViewer.getChart();
         if (chart != null) {
            Plot plot = chart.getPlot();
            if (plot != null) {
               ChartCanvas canvas = chartViewer.getCanvas();
               if (canvas != null) {
                  ChartRenderingInfo renderingInfo = canvas.getRenderingInfo();
                  if (renderingInfo != null) {
                     boolean savedNotify = plot.isNotify();
                     plot.setNotify(false);
                     Point2D endPoint = new Double(0.0, 0.0);
                     Zoomable z = (Zoomable)plot;
                     PlotRenderingInfo pri = renderingInfo.getPlotInfo();
                     z.zoomDomainAxes(0.0, pri, endPoint);
                     z.zoomRangeAxes(0.0, pri, endPoint);
                     plot.setNotify(savedNotify);
                  }
               }
            }
         }
      }
   }

   private ChartUtils() {
   }
}
