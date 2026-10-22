package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.Serializable;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.Drawable;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.PublicCloneable;

public class XYDrawableAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {
   private static final long serialVersionUID = -6540812859722691020L;
   private double drawScaleFactor;
   private double x;
   private double y;
   private double displayWidth;
   private double displayHeight;
   private Drawable drawable;

   public XYDrawableAnnotation(double x, double y, double width, double height, Drawable drawable) {
      this(x, y, width, height, 1.0, drawable);
   }

   public XYDrawableAnnotation(double x, double y, double displayWidth, double displayHeight, double drawScaleFactor, Drawable drawable) {
      Args.nullNotPermitted(drawable, "drawable");
      this.x = x;
      this.y = y;
      this.displayWidth = displayWidth;
      this.displayHeight = displayHeight;
      this.drawScaleFactor = drawScaleFactor;
      this.drawable = drawable;
   }

   @Override
   public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
      PlotOrientation orientation = plot.getOrientation();
      RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
      RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
      float j2DX = (float)domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
      float j2DY = (float)rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
      Rectangle2D displayArea = new Double(
         (double)j2DX - this.displayWidth / 2.0, (double)j2DY - this.displayHeight / 2.0, this.displayWidth, this.displayHeight
      );
      AffineTransform savedTransform = g2.getTransform();
      Rectangle2D drawArea = new Double(0.0, 0.0, this.displayWidth * this.drawScaleFactor, this.displayHeight * this.drawScaleFactor);
      g2.scale(1.0 / this.drawScaleFactor, 1.0 / this.drawScaleFactor);
      g2.translate(((double)j2DX - this.displayWidth / 2.0) * this.drawScaleFactor, ((double)j2DY - this.displayHeight / 2.0) * this.drawScaleFactor);
      this.drawable.draw(g2, drawArea);
      g2.setTransform(savedTransform);
      String toolTip = this.getToolTipText();
      String url = this.getURL();
      if (toolTip != null || url != null) {
         this.addEntity(info, displayArea, rendererIndex, toolTip, url);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!super.equals(obj)) {
         return false;
      } else if (!(obj instanceof XYDrawableAnnotation)) {
         return false;
      } else {
         XYDrawableAnnotation that = (XYDrawableAnnotation)obj;
         if (this.x != that.x) {
            return false;
         } else if (this.y != that.y) {
            return false;
         } else if (this.displayWidth != that.displayWidth) {
            return false;
         } else if (this.displayHeight != that.displayHeight) {
            return false;
         } else {
            return this.drawScaleFactor != that.drawScaleFactor ? false : ObjectUtils.equal(this.drawable, that.drawable);
         }
      }
   }

   @Override
   public int hashCode() {
      long temp = java.lang.Double.doubleToLongBits(this.x);
      int result = (int)(temp ^ temp >>> 32);
      temp = java.lang.Double.doubleToLongBits(this.y);
      result = 29 * result + (int)(temp ^ temp >>> 32);
      temp = java.lang.Double.doubleToLongBits(this.displayWidth);
      result = 29 * result + (int)(temp ^ temp >>> 32);
      temp = java.lang.Double.doubleToLongBits(this.displayHeight);
      return 29 * result + (int)(temp ^ temp >>> 32);
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
