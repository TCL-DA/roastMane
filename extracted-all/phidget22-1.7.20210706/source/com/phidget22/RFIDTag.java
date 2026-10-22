package com.phidget22;

public class RFIDTag {
   public String tagString;
   public RFIDProtocol protocol;

   @Override
   public String toString() {
      return "[RFIDTag](tagString: " + this.tagString + ", protocol: " + this.protocol + ")";
   }
}
