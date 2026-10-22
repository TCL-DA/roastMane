package org.jfree.chart.fx.interaction;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import javafx.scene.input.MouseEvent;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.fx.ChartCanvas;

public class DispatchHandlerFX extends AbstractMouseHandlerFX {
   private Point2D mousePressedPoint;

   public DispatchHandlerFX(String id) {
      super(id, false, false, false, false);
   }

   @Override
   public void handleMousePressed(ChartCanvas canvas, MouseEvent e) {
      this.mousePressedPoint = new Double(e.getX(), e.getY());
   }

   @Override
   public void handleMouseMoved(ChartCanvas canvas, MouseEvent e) {
      double x = e.getX();
      double y = e.getY();
      ChartEntity entity = canvas.getRenderingInfo().getEntityCollection().getEntity(x, y);
      ChartMouseEventFX event = new ChartMouseEventFX(canvas.getChart(), e, entity);

      for (ChartMouseListenerFX listener : canvas.getChartMouseListeners()) {
         listener.chartMouseMoved(event);
      }
   }

   @Override
   public void handleMouseClicked(ChartCanvas canvas, MouseEvent e) {
      if (this.mousePressedPoint != null) {
         double x = e.getX();
         double y = e.getY();
         ChartEntity entity = canvas.getRenderingInfo().getEntityCollection().getEntity(x, y);
         ChartMouseEventFX event = new ChartMouseEventFX(canvas.getChart(), e, entity);

         for (ChartMouseListenerFX listener : canvas.getChartMouseListeners()) {
            listener.chartMouseClicked(event);
         }
      }
   }
}
