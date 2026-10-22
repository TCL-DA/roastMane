package org.jfree.chart.plot;

import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.event.MarkerChangeEvent;

public class ValueMarker extends Marker {
   private double value;

   public ValueMarker(double value) {
      this.value = value;
   }

   public ValueMarker(double value, Paint paint, Stroke stroke) {
      this(value, paint, stroke, paint, stroke, 1.0F);
   }

   public ValueMarker(double value, Paint paint, Stroke stroke, Paint outlinePaint, Stroke outlineStroke, float alpha) {
      super(paint, stroke, outlinePaint, outlineStroke, alpha);
      this.value = value;
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double value) {
      this.value = value;
      this.notifyListeners(new MarkerChangeEvent(this));
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!super.equals(obj)) {
         return false;
      } else if (!(obj instanceof ValueMarker)) {
         return false;
      } else {
         ValueMarker that = (ValueMarker)obj;
         return this.value == that.value;
      }
   }
}
