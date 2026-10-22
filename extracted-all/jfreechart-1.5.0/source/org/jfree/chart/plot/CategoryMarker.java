package org.jfree.chart.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serializable;
import org.jfree.chart.event.MarkerChangeEvent;
import org.jfree.chart.ui.LengthAdjustmentType;
import org.jfree.chart.util.Args;

public class CategoryMarker extends Marker implements Cloneable, Serializable {
   private Comparable key;
   private boolean drawAsLine = false;

   public CategoryMarker(Comparable key) {
      this(key, Color.gray, new BasicStroke(1.0F));
   }

   public CategoryMarker(Comparable key, Paint paint, Stroke stroke) {
      this(key, paint, stroke, paint, stroke, 1.0F);
   }

   public CategoryMarker(Comparable key, Paint paint, Stroke stroke, Paint outlinePaint, Stroke outlineStroke, float alpha) {
      super(paint, stroke, outlinePaint, outlineStroke, alpha);
      this.key = key;
      this.setLabelOffsetType(LengthAdjustmentType.EXPAND);
   }

   public Comparable getKey() {
      return this.key;
   }

   public void setKey(Comparable key) {
      Args.nullNotPermitted(key, "key");
      this.key = key;
      this.notifyListeners(new MarkerChangeEvent(this));
   }

   public boolean getDrawAsLine() {
      return this.drawAsLine;
   }

   public void setDrawAsLine(boolean drawAsLine) {
      this.drawAsLine = drawAsLine;
      this.notifyListeners(new MarkerChangeEvent(this));
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (!(obj instanceof CategoryMarker)) {
         return false;
      } else if (!super.equals(obj)) {
         return false;
      } else {
         CategoryMarker that = (CategoryMarker)obj;
         return !this.key.equals(that.key) ? false : this.drawAsLine == that.drawAsLine;
      }
   }
}
