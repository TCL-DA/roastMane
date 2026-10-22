package org.jfree.chart.annotations;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jfree.chart.HashUtils;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.text.TextUtils;
import org.jfree.chart.ui.RectangleEdge;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.PaintUtils;
import org.jfree.chart.util.PublicCloneable;
import org.jfree.chart.util.SerialUtils;

public class XYTextAnnotation extends AbstractXYAnnotation implements Cloneable, PublicCloneable, Serializable {
   private static final long serialVersionUID = -2946063342782506328L;
   public static final Font DEFAULT_FONT = new Font("SansSerif", 0, 10);
   public static final Paint DEFAULT_PAINT = Color.BLACK;
   public static final TextAnchor DEFAULT_TEXT_ANCHOR = TextAnchor.CENTER;
   public static final TextAnchor DEFAULT_ROTATION_ANCHOR = TextAnchor.CENTER;
   public static final double DEFAULT_ROTATION_ANGLE = 0.0;
   private String text;
   private Font font;
   private transient Paint paint;
   private double x;
   private double y;
   private TextAnchor textAnchor;
   private TextAnchor rotationAnchor;
   private double rotationAngle;
   private transient Paint backgroundPaint;
   private boolean outlineVisible;
   private transient Paint outlinePaint;
   private transient Stroke outlineStroke;

   public XYTextAnnotation(String text, double x, double y) {
      Args.nullNotPermitted(text, "text");
      this.text = text;
      this.font = DEFAULT_FONT;
      this.paint = DEFAULT_PAINT;
      this.x = x;
      this.y = y;
      this.textAnchor = DEFAULT_TEXT_ANCHOR;
      this.rotationAnchor = DEFAULT_ROTATION_ANCHOR;
      this.rotationAngle = 0.0;
      this.backgroundPaint = null;
      this.outlineVisible = false;
      this.outlinePaint = Color.BLACK;
      this.outlineStroke = new BasicStroke(0.5F);
   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      Args.nullNotPermitted(text, "text");
      this.text = text;
      this.fireAnnotationChanged();
   }

   public Font getFont() {
      return this.font;
   }

   public void setFont(Font font) {
      Args.nullNotPermitted(font, "font");
      this.font = font;
      this.fireAnnotationChanged();
   }

   public Paint getPaint() {
      return this.paint;
   }

   public void setPaint(Paint paint) {
      Args.nullNotPermitted(paint, "paint");
      this.paint = paint;
      this.fireAnnotationChanged();
   }

   public TextAnchor getTextAnchor() {
      return this.textAnchor;
   }

   public void setTextAnchor(TextAnchor anchor) {
      Args.nullNotPermitted(anchor, "anchor");
      this.textAnchor = anchor;
      this.fireAnnotationChanged();
   }

   public TextAnchor getRotationAnchor() {
      return this.rotationAnchor;
   }

   public void setRotationAnchor(TextAnchor anchor) {
      Args.nullNotPermitted(anchor, "anchor");
      this.rotationAnchor = anchor;
      this.fireAnnotationChanged();
   }

   public double getRotationAngle() {
      return this.rotationAngle;
   }

   public void setRotationAngle(double angle) {
      this.rotationAngle = angle;
      this.fireAnnotationChanged();
   }

   public double getX() {
      return this.x;
   }

   public void setX(double x) {
      this.x = x;
      this.fireAnnotationChanged();
   }

   public double getY() {
      return this.y;
   }

   public void setY(double y) {
      this.y = y;
      this.fireAnnotationChanged();
   }

   public Paint getBackgroundPaint() {
      return this.backgroundPaint;
   }

   public void setBackgroundPaint(Paint paint) {
      this.backgroundPaint = paint;
      this.fireAnnotationChanged();
   }

   public Paint getOutlinePaint() {
      return this.outlinePaint;
   }

   public void setOutlinePaint(Paint paint) {
      Args.nullNotPermitted(paint, "paint");
      this.outlinePaint = paint;
      this.fireAnnotationChanged();
   }

   public Stroke getOutlineStroke() {
      return this.outlineStroke;
   }

   public void setOutlineStroke(Stroke stroke) {
      Args.nullNotPermitted(stroke, "stroke");
      this.outlineStroke = stroke;
      this.fireAnnotationChanged();
   }

   public boolean isOutlineVisible() {
      return this.outlineVisible;
   }

   public void setOutlineVisible(boolean visible) {
      this.outlineVisible = visible;
      this.fireAnnotationChanged();
   }

   @Override
   public void draw(Graphics2D g2, XYPlot plot, Rectangle2D dataArea, ValueAxis domainAxis, ValueAxis rangeAxis, int rendererIndex, PlotRenderingInfo info) {
      PlotOrientation orientation = plot.getOrientation();
      RectangleEdge domainEdge = Plot.resolveDomainAxisLocation(plot.getDomainAxisLocation(), orientation);
      RectangleEdge rangeEdge = Plot.resolveRangeAxisLocation(plot.getRangeAxisLocation(), orientation);
      float anchorX = (float)domainAxis.valueToJava2D(this.x, dataArea, domainEdge);
      float anchorY = (float)rangeAxis.valueToJava2D(this.y, dataArea, rangeEdge);
      if (orientation == PlotOrientation.HORIZONTAL) {
         float tempAnchor = anchorX;
         anchorX = anchorY;
         anchorY = tempAnchor;
      }

      g2.setFont(this.getFont());
      Shape hotspot = TextUtils.calculateRotatedStringBounds(
         this.getText(), g2, anchorX, anchorY, this.getTextAnchor(), this.getRotationAngle(), this.getRotationAnchor()
      );
      if (this.backgroundPaint != null) {
         g2.setPaint(this.backgroundPaint);
         g2.fill(hotspot);
      }

      g2.setPaint(this.getPaint());
      TextUtils.drawRotatedString(this.getText(), g2, anchorX, anchorY, this.getTextAnchor(), this.getRotationAngle(), this.getRotationAnchor());
      if (this.outlineVisible) {
         g2.setStroke(this.outlineStroke);
         g2.setPaint(this.outlinePaint);
         g2.draw(hotspot);
      }

      String toolTip = this.getToolTipText();
      String url = this.getURL();
      if (toolTip != null || url != null) {
         this.addEntity(info, hotspot, rendererIndex, toolTip, url);
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof XYTextAnnotation)) {
         return false;
      } else {
         XYTextAnnotation that = (XYTextAnnotation)obj;
         if (!this.text.equals(that.text)) {
            return false;
         } else if (this.x != that.x) {
            return false;
         } else if (this.y != that.y) {
            return false;
         } else if (!this.font.equals(that.font)) {
            return false;
         } else if (!PaintUtils.equal(this.paint, that.paint)) {
            return false;
         } else if (!this.rotationAnchor.equals(that.rotationAnchor)) {
            return false;
         } else if (this.rotationAngle != that.rotationAngle) {
            return false;
         } else if (!this.textAnchor.equals(that.textAnchor)) {
            return false;
         } else if (this.outlineVisible != that.outlineVisible) {
            return false;
         } else if (!PaintUtils.equal(this.backgroundPaint, that.backgroundPaint)) {
            return false;
         } else if (!PaintUtils.equal(this.outlinePaint, that.outlinePaint)) {
            return false;
         } else {
            return !this.outlineStroke.equals(that.outlineStroke) ? false : super.equals(obj);
         }
      }
   }

   @Override
   public int hashCode() {
      int result = 193;
      result = 37 * result + this.text.hashCode();
      result = 37 * result + this.font.hashCode();
      result = 37 * result + HashUtils.hashCodeForPaint(this.paint);
      long temp = Double.doubleToLongBits(this.x);
      result = 37 * result + (int)(temp ^ temp >>> 32);
      temp = Double.doubleToLongBits(this.y);
      result = 37 * result + (int)(temp ^ temp >>> 32);
      result = 37 * result + this.textAnchor.hashCode();
      result = 37 * result + this.rotationAnchor.hashCode();
      temp = Double.doubleToLongBits(this.rotationAngle);
      return 37 * result + (int)(temp ^ temp >>> 32);
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      SerialUtils.writePaint(this.paint, stream);
      SerialUtils.writePaint(this.backgroundPaint, stream);
      SerialUtils.writePaint(this.outlinePaint, stream);
      SerialUtils.writeStroke(this.outlineStroke, stream);
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.paint = SerialUtils.readPaint(stream);
      this.backgroundPaint = SerialUtils.readPaint(stream);
      this.outlinePaint = SerialUtils.readPaint(stream);
      this.outlineStroke = SerialUtils.readStroke(stream);
   }
}
