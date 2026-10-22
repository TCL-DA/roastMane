package com.phidget22;

import java.util.Arrays;

public class IRCodeInfo {
   public int bitCount;
   public IRCodeEncoding encoding;
   public IRCodeLength length;
   public int gap;
   public int trail;
   public int[] header;
   public int[] one;
   public int[] zero;
   public int[] repeat;
   public int minRepeat;
   public double dutyCycle;
   public int carrierFrequency;
   public String toggleMask;

   @Override
   public String toString() {
      return "[IRCodeInfo](bitCount: "
         + this.bitCount
         + ", encoding: "
         + this.encoding
         + ", length: "
         + this.length
         + ", gap: "
         + this.gap
         + ", trail: "
         + this.trail
         + ", header: "
         + Arrays.toString(this.header)
         + ", one: "
         + Arrays.toString(this.one)
         + ", zero: "
         + Arrays.toString(this.zero)
         + ", repeat: "
         + Arrays.toString(this.repeat)
         + ", minRepeat: "
         + this.minRepeat
         + ", dutyCycle: "
         + this.dutyCycle
         + ", carrierFrequency: "
         + this.carrierFrequency
         + ", toggleMask: "
         + this.toggleMask
         + ")";
   }
}
