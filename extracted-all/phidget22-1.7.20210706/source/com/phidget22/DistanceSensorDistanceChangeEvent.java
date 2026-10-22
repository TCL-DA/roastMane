package com.phidget22;

public class DistanceSensorDistanceChangeEvent {
   DistanceSensor Source;
   int Distance;

   public DistanceSensorDistanceChangeEvent(DistanceSensor var1, int var2) {
      this.Source = var1;
      this.Distance = var2;
   }

   public DistanceSensor getSource() {
      return this.Source;
   }

   public int getDistance() {
      return this.Distance;
   }

   @Override
   public String toString() {
      return "[DistanceSensorDistanceChangeEvent](Source: " + this.Source + ", Distance: " + this.Distance + ")";
   }
}
