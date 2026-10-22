package org.jfree.chart.fx.interaction;

import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import org.jfree.chart.fx.ChartCanvas;

public interface MouseHandlerFX {
   String getID();

   boolean isEnabled();

   boolean hasMatchingModifiers(MouseEvent var1);

   void handleMouseMoved(ChartCanvas var1, MouseEvent var2);

   void handleMouseClicked(ChartCanvas var1, MouseEvent var2);

   void handleMousePressed(ChartCanvas var1, MouseEvent var2);

   void handleMouseDragged(ChartCanvas var1, MouseEvent var2);

   void handleMouseReleased(ChartCanvas var1, MouseEvent var2);

   void handleScroll(ChartCanvas var1, ScrollEvent var2);
}
