package com.phidget22;

public class IRCode {
   public String code;
   public int bitCount;

   @Override
   public String toString() {
      return "[IRCode](code: " + this.code + ", bitCount: " + this.bitCount + ")";
   }
}
