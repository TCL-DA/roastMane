package org.cropster.roastingintelligence.model;

import com.cropster.util.model.Unit;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.EnumSet;

public class MeasureDefinition {
   private final String _name;
   private EnumSet<Unit> _units;

   public MeasureDefinition(@JsonProperty("name") String name) {
      this._name = name;
      this._units = EnumSet.noneOf(Unit.class);
   }

   public String getName() {
      return this._name;
   }

   public EnumSet<Unit> getUnits() {
      return this._units;
   }

   public void setUnits(EnumSet<Unit> units) {
      this._units = units;
   }
}
