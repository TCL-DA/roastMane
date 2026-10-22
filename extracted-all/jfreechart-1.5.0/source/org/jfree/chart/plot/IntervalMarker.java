package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.ui.GradientPaintTransformer;
import org.jfree.chart.ui.LengthAdjustmentType;
import org.jfree.chart.util.ObjectUtils;

public class IntervalMarker extends Marker implements Cloneable, Serializable {
   private static final long serialVersionUID = -1762344775267627916L;
   private double startValue;
   private double endValue;
   private GradientPaintTransformer gradientPaintTransformer;

   public IntervalMarker(double start, double end) {
      this(start, end, Color.gray, new BasicStroke(0.5F), Color.gray, new BasicStroke(0.5F), 0.8F);
   }

   public IntervalMarker(double start, double end, Paint paint) {
      this(start, end, paint, new BasicStroke(0.5F), null, null, 0.8F);
   }

   public IntervalMarker(double start, double end, Paint paint, Stroke stroke, Paint outlinePaint, Stroke outlineStroke, float alpha) {
      super(paint, stroke, outlinePaint, outlineStroke, alpha);
      this.startValue = start;
      this.endValue = end;
      this.gradientPaintTransformer = null;
      this.setLabelOffsetType(LengthAdjustmentType.CONTRACT);
   }

   public double getStartValue() {
      return this.startValue;
   }

   public void setStartValue(double value) {
      this.startValue = value;
      this.notifyListeners(new MarkerChangeEvent(this));
   }

   public double getEndValue() {
      return this.endValue;
   }

   public void setEndValue(double value) {
      this.endValue = value;
      this.notifyListeners(new MarkerChangeEvent(this));
   }

   public GradientPaintTransformer getGradientPaintTransformer() {
      return this.gradientPaintTransformer;
   }

   public void setGradientPaintTransformer(GradientPaintTransformer transformer) {
      this.gradientPaintTransformer = transformer;
      this.notifyListeners(new MarkerChangeEvent(this));
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof IntervalMarker)) {
         return false;
      } else if (!super.equals(obj)) {
         return false;
      } else {
         IntervalMarker that = (IntervalMarker)obj;
         if (this.startValue != that.startValue) {
            return false;
         } else {
            return this.endValue != that.endValue ? false : ObjectUtils.equal(this.gradientPaintTransformer, that.gradientPaintTransformer);
         }
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
