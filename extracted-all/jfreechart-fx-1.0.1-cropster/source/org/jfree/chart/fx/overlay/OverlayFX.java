package org.jfree.chart.fx.overlay;

import java.awt.Graphics2D;
import org.jfree.chart.fx.ChartCanvas;
import org.jfree.chart.panel.Overlay;

public interface OverlayFX extends Overlay {
   void paintOverlay(Graphics2D var1, ChartCanvas var2);
}
