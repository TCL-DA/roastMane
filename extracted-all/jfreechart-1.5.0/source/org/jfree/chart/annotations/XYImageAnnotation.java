package org.jfree.chart.annotations;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.geom.Rectangle2D.Float;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleAnchor;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;
import org.jfree.chart.util.PublicCloneable;

public class XYImageAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {
   private static final long serialVersionUID = -4364694501921559958L;
   private double x;
   private double y;
   private transient Image image;
   private RectangleAnchor anchor;

   public XYImageAnnotation(double x, double y, Image image) {
      this(x, y, image, RectangleAnchor.CENTER);
   }

   public XYImageAnnotation(double x, double y, Image image, RectangleAnchor anchor) {
      Args.nullNotPermitted(image, "image");
      Args.nullNotPermitted(anchor, "anchor");
      this.x = x;
      this.y = y;
      this.image = image;
      this.anchor = anchor;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public Image getImage() {
      return this.image;
   }

   public RectangleAnchor getImageAnchor() {
      return this.anchor;
   }

   @Override
   public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
      PlotOrientation orientation = plot.getOrientation();
      AxisLocation domainAxisLocation = plot.getDomainAxisLocation();
      AxisLocation rangeAxisLocation = plot.getRangeAxisLocation();
      RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(domainAxisLocation, orientation);
      RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(rangeAxisLocation, orientation);
      float j2DX = (float)domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
      float j2DY = (float)rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
      float xx = 0.0F;
      float yy = 0.0F;
      if (orientation == PlotOrientation.HORIZONTAL) {
         xx = j2DY;
         yy = j2DX;
      } else if (orientation == PlotOrientation.VERTICAL) {
         xx = j2DX;
         yy = j2DY;
      }

      int w = this.image.getWidth(null);
      int h = this.image.getHeight(null);
      Rectangle2D imageRect = new Double(0.0, 0.0, (double)w, (double)h);
      Point2D anchorPoint = this.anchor.getAnchorPoint(imageRect);
      xx -= (float)anchorPoint.getX();
      yy -= (float)anchorPoint.getY();
      g2.drawImage(this.image, (int)xx, (int)yy, null);
      String toolTip = this.getToolTipText();
      String url = this.getURL();
      if (toolTip != null || url != null) {
         this.addEntity(info, new Float(xx, yy, (float)w, (float)h), rendererIndex, toolTip, url);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!super.equals(obj)) {
         return false;
      } else if (!(obj instanceof XYImageAnnotation)) {
         return false;
      } else {
         XYImageAnnotation that = (XYImageAnnotation)obj;
         if (this.x != that.x) {
            return false;
         } else if (this.y != that.y) {
            return false;
         } else {
            return !ObjectUtils.equal(this.image, that.image) ? false : this.anchor.equals(that.anchor);
         }
      }
   }

   @Override
   public int hashCode() {
      return this.image.hashCode();
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
   }
}
