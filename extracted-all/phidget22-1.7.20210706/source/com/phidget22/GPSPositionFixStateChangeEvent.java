package com.phidget22;

public class GPSPositionFixStateChangeEvent {
   GPS Source;
   boolean PositionFixState;

   public GPSPositionFixStateChangeEvent(GPS var1, boolean var2) {
      this.Source = var1;
      this.PositionFixState = var2;
   }

   public GPS getSource() {
      return this.Source;
   }

   public boolean getPositionFixState() {
      return this.PositionFixState;
   }

   @Override
   public String toString() {
      return "[GPSPositionFixStateChangeEvent](Source: " + this.Source + ", PositionFixState: " + this.PositionFixState + ")";
   }
}
