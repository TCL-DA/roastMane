package org.cropster.roastingintelligence.ui.chart;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;

public class MultiLineInfo extends AbstractXYAnnotation {
   private static final long serialVersionUID = -764739026297438678L;
   private final String[] _lines;
   private Stroke _stroke = new BasicStroke(2.0F);
   private Paint _paint = Color.GRAY;
   private Font _font = CurveChart.FONT_MEDIUM;
   private int _position = 4;

   public MultiLineInfo(String... lines) {
      this._lines = lines;
      this.setToolTipText(lines[0]);
   }

   public void setStroke(Stroke stroke) {
      this._stroke = stroke;
   }

   public void setPaint(Paint paint) {
      this._paint = paint;
   }

   public void setFont(Font font) {
      this._font = font;
   }

   public void setPosition(int position) {
      this._position = position;
   }

   public void draw(Graphics2D g, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
      AlphaComposite composite = AlphaComposite.getInstance(3, 0.9F);
      Graphics2D g2d = (Graphics2D)g.create();
      g2d.setComposite(composite);
      g2d.setStroke(this._stroke);
      g2d.setPaint(this._paint);
      g2d.setFont(this._font);
      double maxWidth = 0.0;

      for (String line : this._lines) {
         GlyphVector glyphVector = g2d.getFont().createGlyphVector(g2d.getFontRenderContext(), line);
         maxWidth = Math.max(maxWidth, glyphVector.getVisualBounds().getWidth());
      }

      double lineHeight = (double)(g2d.getFont().getSize2D() + 2.0F);
      float x;
      if (this._position == 2) {
         x = (float)dataArea.getMinX() + 10.0F;
      } else {
         x = (float)(dataArea.getMaxX() - maxWidth - 10.0);
      }

      float y = (float)(dataArea.getMaxY() - 50.0);
      AlphaComposite transparentComposite = AlphaComposite.getInstance(3, 0.66F);
      g2d.setComposite(transparentComposite);
      g2d.setPaint(Color.WHITE);
      int height = (int)((double)this._lines.length * lineHeight + 20.0);
      g2d.fillRect((int)(x - 10.0F), (int)(y - (float)height + 10.0F), (int)(maxWidth + 20.0), height);
      g2d.setComposite(composite);
      g2d.setPaint(this._paint);

      for (int i = this._lines.length - 1; i >= 0; i--) {
         g2d.drawString(this._lines[i], x, y);
         y = (float)((double)y - lineHeight);
      }
   }
}
