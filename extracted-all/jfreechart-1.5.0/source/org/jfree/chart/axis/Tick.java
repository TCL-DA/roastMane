package org.jfree.chart.axis;

import java.io.Serializable;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.chart.util.Args;
import org.jfree.chart.util.ObjectUtils;

public abstract class Tick implements Serializable, Cloneable {
   private static final long serialVersionUID = 6668230383875149773L;
   private String text;
   private TextAnchor textAnchor;
   private TextAnchor rotationAnchor;
   private double angle;

   public Tick(String text, TextAnchor textAnchor, TextAnchor rotationAnchor, double angle) {
      Args.nullNotPermitted(textAnchor, "textAnchor");
      Args.nullNotPermitted(rotationAnchor, "rotationAnchor");
      this.text = text;
      this.textAnchor = textAnchor;
      this.rotationAnchor = rotationAnchor;
      this.angle = angle;
   }

   public String getText() {
      return this.text;
   }

   public TextAnchor getTextAnchor() {
      return this.textAnchor;
   }

   public TextAnchor getRotationAnchor() {
      return this.rotationAnchor;
   }

   public double getAngle() {
      return this.angle;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof Tick) {
         Tick t = (Tick)obj;
         if (!ObjectUtils.equal(this.text, t.text)) {
            return false;
         } else if (!ObjectUtils.equal(this.textAnchor, t.textAnchor)) {
            return false;
         } else {
            return !ObjectUtils.equal(this.rotationAnchor, t.rotationAnchor) ? false : this.angle == t.angle;
         }
      } else {
         return false;
      }
   }

   @Override
   public Object clone() throws CloneNotSupportedException {
      return (Tick)super.clone();
   }

   @Override
   public String toString() {
      return this.text;
   }
}
