package com.phidget22;

public class UnitInfo {
   public Unit unit;
   public String name;
   public String symbol;

   @Override
   public String toString() {
      return "[UnitInfo](unit: " + this.unit + ", name: " + this.name + ", symbol: " + this.symbol + ")";
   }
}
