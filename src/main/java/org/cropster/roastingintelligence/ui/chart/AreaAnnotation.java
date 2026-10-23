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

public class AreaAnnotation extends AbstractXYAnnotation {
   private static final long serialVersionUID = 4518650309181602483L;
   private static final int HEIGHT = 10;
   private final double _x1;
   private final double _x2;
   private final Color _color;
   private final Font _font;

   public AreaAnnotation(double x1, double x2, Font font, Color color) {
      this._x1 = x1;
      this._x2 = x2;
      this._font = font;
      this._color = color;
   }

   public double getX1() {
      return this._x1;
   }

   public double getX2() {
      return this._x2;
   }

   public Color getColor() {
      return this._color;
   }

   public void draw(Graphics2D g2d, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
      Composite originalComposite = g2d.getComposite();
      Font originalFont = g2d.getFont();
      Paint originalPaint = g2d.getPaint();
      AlphaComposite composite = AlphaComposite.getInstance(3, 0.9F);
      g2d.setComposite(composite);
      if (this._font != null) {
         g2d.setFont(this._font);
      }

      g2d.setPaint(this._color);
      PlotOrientation orientation = plot.getOrientation();
      RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
      double j2Dx1 = domainAxis.valueToJava2D(this._x1, dataArea, domainEdge);
      double j2Dx2 = domainAxis.valueToJava2D(this._x2, dataArea, domainEdge);
      double areaWidth = j2Dx2 - j2Dx1;
      g2d.fillRect((int)j2Dx1, (int)dataArea.getMaxY() - 10, (int)areaWidth, 10);
      if (this.getToolTipText() != null) {
         GlyphVector glyphVector = g2d.getFont().createGlyphVector(g2d.getFontRenderContext(), this.getToolTipText());
         double textWidth = glyphVector.getVisualBounds().getWidth();
         float textX = (float)(j2Dx1 + areaWidth / 2.0 - textWidth / 2.0);
         float textY = (float)(dataArea.getMaxY() - 20.0);
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
      g2d.setFont(originalFont);
      g2d.setPaint(originalPaint);
   }
}
