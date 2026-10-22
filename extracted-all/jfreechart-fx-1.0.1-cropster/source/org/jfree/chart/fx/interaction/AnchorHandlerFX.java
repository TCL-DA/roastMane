package org.jfree.chart.fx.interaction;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import javafx.scene.input.MouseEvent;
import org.jfree.chart.fx.ChartCanvas;

public class AnchorHandlerFX extends AbstractMouseHandlerFX {
   private Point2D mousePressedPoint;

   public AnchorHandlerFX(String id) {
      super(id, false, false, false, false);
   }

   @Override
   public void handleMousePressed(ChartCanvas canvas, MouseEvent e) {
      this.mousePressedPoint = new Double(e.getX(), e.getY());
   }

   @Override
   public void handleMouseClicked(ChartCanvas canvas, MouseEvent e) {
      if (this.mousePressedPoint != null) {
         Point2D currPt = new Double(e.getX(), e.getY());
         if (this.mousePressedPoint.distance(currPt) < 2.0) {
            canvas.setAnchor(currPt);
         }

         this.mousePressedPoint = null;
      }
   }
}
