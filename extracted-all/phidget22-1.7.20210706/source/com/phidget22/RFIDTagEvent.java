package com.phidget22;

public class RFIDTagEvent {
   RFID Source;
   String Tag;
   RFIDProtocol Protocol;

   public RFIDTagEvent(RFID var1, String var2, RFIDProtocol var3) {
      this.Source = var1;
      this.Tag = var2;
      this.Protocol = var3;
   }

   public RFID getSource() {
      return this.Source;
   }

   public String getTag() {
      return this.Tag;
   }

   public RFIDProtocol getProtocol() {
      return this.Protocol;
   }

   @Override
   public String toString() {
      return "[RFIDTagEvent](Source: " + this.Source + ", Tag: " + this.Tag + ", Protocol: " + this.Protocol + ")";
   }
}
