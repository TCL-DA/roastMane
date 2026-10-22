package org.cropster.roastingintelligence.model;

import com.cropster.util.model.Unit;
import java.util.SortedMap;
import java.util.TreeMap;

public class Curve extends TreeMap<Long, Double> implements CurveData {
   private static final long serialVersionUID = -771484998290838684L;
   private final Unit _unit;

   public Curve(Unit unit) {
      this._unit = unit;
   }

   public Curve(Curve curve) {
      super(curve);
      this._unit = curve.getUnit();
   }

   @Override
   public Unit getUnit() {
      return this._unit;
   }

   @Override
   public SortedMap<Long, Double> getValues() {
      return this;
   }

   public Curve clone() {
      return (Curve)super.clone();
   }
}
