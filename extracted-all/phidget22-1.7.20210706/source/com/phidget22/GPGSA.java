package com.phidget22;

import java.util.Arrays;

public class GPGSA {
   public char mode;
   public short fixType;
   public short[] satUsed;
   public double posnDilution;
   public double horizDilution;
   public double vertDilution;

   @Override
   public String toString() {
      return "[GPGSA](mode: "
         + this.mode
         + ", fixType: "
         + this.fixType
         + ", satUsed: "
         + Arrays.toString(this.satUsed)
         + ", posnDilution: "
         + this.posnDilution
         + ", horizDilution: "
         + this.horizDilution
         + ", vertDilution: "
         + this.vertDilution
         + ")";
   }
}
