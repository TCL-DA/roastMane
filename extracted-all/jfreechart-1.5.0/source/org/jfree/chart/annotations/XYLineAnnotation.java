package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D.Float;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.LineUtils;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.PaintUtils;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtils;
import org.jfree.chart.util.ShapeUtils;

public class XYLineAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {
   private static final long serialVersionUID = -80535465244091334L;
   private double x1;
   private double y1;
   private double x2;
   private double y2;
   private transient Stroke stroke;
   private transient Paint paint;

   public XYLineAnnotation(double x1, double y1, double x2, double y2) {
      this(x1, y1, x2, y2, new BasicStroke(1.0F), Color.BLACK);
   }

   public XYLineAnnotation(double x1, double y1, double x2, double y2, Stroke stroke, Paint paint) {
      Args.nullNotPermitted(stroke, "stroke");
      Args.nullNotPermitted(paint, "paint");
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.stroke = stroke;
      this.paint = paint;
   }

   @Override
   public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
      PlotOrientation orientation = plot.getOrientation();
      RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
      RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
      float j2DX1 = 0.0F;
      float j2DX2 = 0.0F;
      float j2DY1 = 0.0F;
      float j2DY2 = 0.0F;
      if (orientation == PlotOrientation.VERTICAL) {
         j2DX1 = (float)domainAxis.valueToJava2D(this.x1, dataArea, domainEdge);
         j2DY1 = (float)rangeAxis.valueToJava2D(this.y1, dataArea, rangeEdge);
         j2DX2 = (float)domainAxis.valueToJava2D(this.x2, dataArea, domainEdge);
         j2DY2 = (float)rangeAxis.valueToJava2D(this.y2, dataArea, rangeEdge);
      } else if (orientation == PlotOrientation.HORIZONTAL) {
         j2DY1 = (float)domainAxis.valueToJava2D(this.x1, dataArea, domainEdge);
         j2DX1 = (float)rangeAxis.valueToJava2D(this.y1, dataArea, rangeEdge);
         j2DY2 = (float)domainAxis.valueToJava2D(this.x2, dataArea, domainEdge);
         j2DX2 = (float)rangeAxis.valueToJava2D(this.y2, dataArea, rangeEdge);
      }

      g2.setPaint(this.paint);
      g2.setStroke(this.stroke);
      Line2D line = new Float(j2DX1, j2DY1, j2DX2, j2DY2);
      boolean visible = LineUtils.clipLine(line, dataArea);
      if (visible) {
         g2.draw(line);
      }

      String toolTip = this.getToolTipText();
      String url = this.getURL();
      if (toolTip != null || url != null) {
         this.addEntity(info, ShapeUtils.createLineRegion(line, 1.0F), rendererIndex, toolTip, url);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!super.equals(obj)) {
         return false;
      } else if (!(obj instanceof XYLineAnnotation)) {
         return false;
      } else {
         XYLineAnnotation that = (XYLineAnnotation)obj;
         if (this.x1 != that.x1) {
            return false;
         } else if (this.y1 != that.y1) {
            return false;
         } else if (this.x2 != that.x2) {
            return false;
         } else if (this.y2 != that.y2) {
            return false;
         } else {
            return !PaintUtils.equal(this.paint, that.paint) ? false : ObjectUtils.equal(this.stroke, that.stroke);
         }
      }
   }

   @Override
   public int hashCode() {
      long temp = Double.doubleToLongBits(this.x1);
      int result = (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.x2);
      result = 29 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.y1);
      result = 29 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.y2);
      return 29 * result + (int)(temp ^ temp >>> 32);
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      SerialUtils.writePaint(this.paint, stream);
      SerialUtils.writeStroke(this.stroke, stream);
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.paint = SerialUtils.readPaint(stream);
      this.stroke = SerialUtils.readStroke(stream);
   }
}
