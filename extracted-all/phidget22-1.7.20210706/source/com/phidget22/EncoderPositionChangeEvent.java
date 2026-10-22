package com.phidget22;

public class EncoderPositionChangeEvent {
   Encoder Source;
   int PositionChange;
   double TimeChange;
   boolean IndexTriggered;

   public EncoderPositionChangeEvent(Encoder var1, int var2, double var3, boolean var5) {
      this.Source = var1;
      this.PositionChange = var2;
      this.TimeChange = var3;
      this.IndexTriggered = var5;
   }

   public Encoder getSource() {
      return this.Source;
   }

   public int getPositionChange() {
      return this.PositionChange;
   }

   public double getTimeChange() {
      return this.TimeChange;
   }

   public boolean getIndexTriggered() {
      return this.IndexTriggered;
   }

   @Override
   public String toString() {
      return "[EncoderPositionChangeEvent](Source: "
         + this.Source
         + ", PositionChange: "
         + this.PositionChange
         + ", TimeChange: "
         + this.TimeChange
         + ", IndexTriggered: "
         + this.IndexTriggered
         + ")";
   }
}
