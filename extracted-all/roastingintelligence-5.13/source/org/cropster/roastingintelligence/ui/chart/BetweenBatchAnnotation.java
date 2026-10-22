package org.cropster.roastingintelligence.ui.chart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;

public class BetweenBatchAnnotation extends AbstractXYAnnotation {
   private static final long serialVersionUID = -733113061493291960L;
   private static final Color COLOR_BACKGROUND = new Color(196, 196, 196);
   private static final Color COLOR_TEXT_BACKGROUND = new Color(226, 231, 233);
   private double _x1;
   private double _x2;
   private final Font _font;

   public BetweenBatchAnnotation(double x1, double x2, Font font) {
      this._x1 = x1;
      this._x2 = x2;
      this._font = font;
   }

   public double getX1() {
      return this._x1;
   }

   public void setX1(double x1) {
      this._x1 = x1;
   }

   public double getX2() {
      return this._x2;
   }

   public void setX2(double x2) {
      this._x2 = x2;
   }

   public void draw(Graphics2D g2d, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
      Composite originalComposite = g2d.getComposite();
      Paint originalPaint = g2d.getPaint();
      Font originalFont = g2d.getFont();
      if (this._font != null) {
         g2d.setFont(this._font);
      }

      g2d.setPaint(COLOR_BACKGROUND);
      PlotOrientation orientation = plot.getOrientation();
      RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
      double j2Dx1 = domainAxis.valueToJava2D(this._x1, dataArea, domainEdge);
      double j2Dx2 = domainAxis.valueToJava2D(this._x2, dataArea, domainEdge);
      double areaWidth = j2Dx2 - j2Dx1;
      g2d.setComposite(AlphaComposite.getInstance(3, 0.1F));
      g2d.fillRect((int)j2Dx1, (int)dataArea.getMinY(), (int)areaWidth, (int)dataArea.getHeight());
      if (this.getToolTipText() != null) {
         GlyphVector glyphVector = g2d.getFont().createGlyphVector(g2d.getFontRenderContext(), this.getToolTipText());
         float textX = (float)(j2Dx1 + 20.0);
         float textY = (float)(dataArea.getMaxY() - 20.0);
         g2d.setColor(COLOR_TEXT_BACKGROUND);
         g2d.setComposite(AlphaComposite.getInstance(3, 1.0F));
         Rectangle2D textBounds = glyphVector.getLogicalBounds();
         g2d.fillRect(
            (int)((double)textX + textBounds.getX() - 3.0),
            (int)((double)textY + textBounds.getY() - 3.0),
            (int)textBounds.getWidth() + 6,
            (int)textBounds.getHeight() + 6
         );
         g2d.setColor(Color.BLACK);
         g2d.drawString(this.getToolTipText(), textX, textY);
      }

      g2d.setComposite(originalComposite);
      g2d.setPaint(originalPaint);
      g2d.setFont(originalFont);
   }
}
