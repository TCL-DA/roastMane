package org.cropster.roastingintelligence.ui.chart;

import com.cropster.util.text.DurationFormat;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import org.cropster.roastingintelligence.Defaults;
import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;

public class FirstCrackAnnotation extends AbstractXYAnnotation {
   private static final long serialVersionUID = -8622516300627228741L;
   private static final int HEIGHT = 10;
   private static final Stroke STROKE_DASHED = new BasicStroke(2.0F, 2, 1, 1.0F, new float[]{10.0F, 5.0F}, 0.0F);
   private final DurationFormat _durationFormat = DurationFormat.getDurationInstance(true);
   private long _percentile10;
   private long _percentile50;
   private long _percentile90;

   public FirstCrackAnnotation(long percentile10, long percentile50, long percentile90) {
      this._percentile10 = percentile10;
      this._percentile50 = percentile50;
      this._percentile90 = percentile90;
   }

   public double getPercentile10() {
      return (double)this._percentile10;
   }

   public void setPercentile10(long percentile10) {
      this._percentile10 = percentile10;
   }

   public double getPercentile50() {
      return (double)this._percentile50;
   }

   public void setPercentile50(long percentile50) {
      this._percentile50 = percentile50;
   }

   public double getPercentile90() {
      return (double)this._percentile90;
   }

   public void setPercentile90(long percentile90) {
      this._percentile90 = percentile90;
   }

   public void draw(Graphics2D g2d, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
      Composite originalComposite = g2d.getComposite();
      Paint originalPaint = g2d.getPaint();
      Stroke originalStroke = g2d.getStroke();
      g2d.setPaint(Defaults.COLOR_DEVELOPMENT);
      PlotOrientation orientation = plot.getOrientation();
      RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
      double j2Dx1 = domainAxis.valueToJava2D((double)this._percentile10, dataArea, domainEdge);
      double j2Dx2 = domainAxis.valueToJava2D((double)this._percentile50, dataArea, domainEdge);
      double j2Dx3 = domainAxis.valueToJava2D((double)this._percentile90, dataArea, domainEdge);
      double areaWidth = j2Dx3 - j2Dx1;
      g2d.setComposite(AlphaComposite.getInstance(3, 0.25F));
      g2d.fillRect((int)j2Dx1, (int)dataArea.getMaxY() - 10, (int)areaWidth, 10);
      g2d.setComposite(AlphaComposite.getInstance(3, 1.0F));
      g2d.setStroke(STROKE_DASHED);
      g2d.drawLine((int)j2Dx2, (int)dataArea.getMinY(), (int)j2Dx2, (int)dataArea.getMaxY());
      g2d.drawLine((int)j2Dx1, (int)dataArea.getMaxY(), (int)j2Dx1, (int)dataArea.getMaxY() - 10);
      g2d.drawLine((int)j2Dx3, (int)dataArea.getMaxY(), (int)j2Dx3, (int)dataArea.getMaxY() - 10);
      String text = String.format(
         "%s%n%s (~%ds)",
         org.cropster.roastingintelligence.util.Messages.m_("First crack prediction"),
         this._durationFormat.format(this._percentile50),
         Math.max(Math.abs(this._percentile50 - this._percentile90), Math.abs(this._percentile50 - this._percentile10)) / 1000L
      );
      GlyphVector glyphVector = g2d.getFont().createGlyphVector(g2d.getFontRenderContext(), text);
      double textHeight = glyphVector.getLogicalBounds().getHeight();
      float textX = (float)(j2Dx2 + 5.0);
      float textY = (float)(dataArea.getMaxY() - 10.0 - textHeight - 10.0);
      g2d.setPaint(Color.BLACK);
      g2d.drawString(text, textX, textY);
      g2d.setStroke(originalStroke);
      g2d.setComposite(originalComposite);
      g2d.setPaint(originalPaint);
   }

   public boolean equals(Object obj) {
      return this == obj;
   }

   public int hashCode() {
      return super.hashCode();
   }
}
