package org.cropster.roastingintelligence.model;

import com.cropster.util.model.Unit;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javafx.beans.NamedArg;
import org.cropster.roastingintelligence.util.MathUtil;

public class Measure {
   private final double _amount;
   private final Unit _unit;

   @JsonCreator
   public Measure(@JsonProperty("amount") @NamedArg("amount") double amount, @JsonProperty(value = "unit",required = true) @NamedArg("unit") Unit unit) {
      if (unit == null) {
         throw new IllegalArgumentException("unit must not be null");
      } else {
         this._amount = MathUtil.roundHalfToEven(amount, 3);
         this._unit = unit;
      }
   }

   public double getAmount() {
      return this._amount;
   }

   public Unit getUnit() {
      return this._unit;
   }

   public Measure abs() {
      return new Measure(Math.abs(this._amount), this._unit);
   }

   public Measure add(Measure value) {
      if (!this._unit.equals(value._unit)) {
         throw new IllegalArgumentException("Units do not match! (" + this._unit + ", " + value._unit + ")");
      } else {
         return new Measure(this._amount + value._amount, this._unit);
      }
   }

   public Measure subtract(Measure value) {
      return this.add(new Measure(-value._amount, value._unit));
   }

   public Measure multiply(double factor) {
      Double newValue = MathUtil.roundHalfToEven(this._amount * factor, 3);
      return new Measure(newValue, this._unit);
   }

   public double divide(Measure value) {
      if (!this._unit.equals(value._unit)) {
         throw new IllegalArgumentException("Units do not match! (\" + _unit + \", \" + value._unit + \")");
      } else {
         return MathUtil.roundHalfToEven(this._amount / value.getAmount(), 3);
      }
   }

   public Measure toUnit(Unit unit) {
      if (this._unit.equals(unit)) {
         return this;
      } else {
         double converted = unit.from(BigDecimal.valueOf(this.getAmount()), this.getUnit()).doubleValue();
         return new Measure(converted, unit);
      }
   }

   @Override
   public String toString() {
      return this._amount + " " + this._unit.toString();
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      long temp = Double.doubleToLongBits(this._amount);
      result = 31 * result + (int)(temp ^ temp >>> 32);
      return 31 * result + (this._unit == null ? 0 : this._unit.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Measure other = (Measure)obj;
         return Double.doubleToLongBits(this._amount) != Double.doubleToLongBits(other._amount) ? false : this._unit == other._unit;
      }
   }
}
