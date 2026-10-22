package org.cropster.roastingintelligence.ui.chart;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import org.jfree.chart.annotations.AbstractXYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;

public class PredictionAnnotation extends AbstractXYAnnotation {
   private static final long serialVersionUID = -8622516300627228740L;
   private double _x1;
   private double _x2;
   private final Color _color;

   public PredictionAnnotation(double x1, double x2, Color color) {
      this._x1 = x1;
      this._x2 = x2;
      this._color = color;
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

   public Color getColor() {
      return this._color;
   }

   public void draw(Graphics2D g2d, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
      Composite originalComposite = g2d.getComposite();
      Paint originalPaint = g2d.getPaint();
      g2d.setPaint(this._color);
      PlotOrientation orientation = plot.getOrientation();
      RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
      double j2Dx1 = domainAxis.valueToJava2D(this._x1, dataArea, domainEdge);
      double j2Dx2 = domainAxis.valueToJava2D(this._x2, dataArea, domainEdge);
      double areaWidth = j2Dx2 - j2Dx1;
      g2d.setComposite(AlphaComposite.getInstance(3, 0.1F));
      g2d.fillRect((int)j2Dx1, (int)dataArea.getMinY(), (int)areaWidth, (int)dataArea.getHeight());
      g2d.setComposite(AlphaComposite.getInstance(3, 0.5F));
      g2d.drawLine((int)j2Dx1, (int)dataArea.getMinY(), (int)j2Dx1, (int)dataArea.getMaxY());
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
