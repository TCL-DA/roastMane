package org.cropster.roastingintelligence.util;

import net.sourceforge.snap7.moka7.S7;

public enum Storage {
   FLOAT(2),
   INTEGER(2),
   INTEGER_16(1),
   DECIMAL1(2),
   DECIMAL1_16(1),
   DECIMAL2(2),
   DECIMAL2_16(1),
   DECIMAL3(2),
   DECIMAL3_16(1);

   private final int registerCount;

   private Storage(int registerCount) {
      this.registerCount = registerCount;
   }

   public int getRegisterCount() {
      return this.registerCount;
   }

   public double extractS7(byte[] buffer) {
      double value = switch (this) {
         case FLOAT -> (double)S7.GetFloatAt(buffer, 0);
         case INTEGER, DECIMAL1 -> (double)S7.GetDIntAt(buffer, 0);
         case INTEGER_16, DECIMAL1_16 -> (double)S7.GetShortAt(buffer, 0);
         default -> throw new IllegalStateException("Unsupported storage format");
      };
      if (DECIMAL1.equals(this) || DECIMAL1_16.equals(this)) {
         value = MathUtil.roundHalfToEven(value / 10.0, 1);
      }

      return value;
   }

   public int extract(int[] registerValues, boolean bigEndian) {
      switch (this) {
         case INTEGER_16:
         case DECIMAL1_16:
         case DECIMAL2_16:
         case DECIMAL3_16:
            return registerValues[0];
         default:
            int lower = registerValues[bigEndian ? 1 : 0];
            int higher = registerValues[bigEndian ? 0 : 1];
            return higher << 16 | lower;
      }
   }

   public double decode(int value) {
      switch (this) {
         case FLOAT:
            return (double)Float.intBitsToFloat(value);
         case INTEGER:
         case INTEGER_16:
         default:
            return (double)value;
         case DECIMAL1:
         case DECIMAL1_16:
            return (double)value / 10.0;
         case DECIMAL2_16:
         case DECIMAL2:
            return (double)value / 100.0;
         case DECIMAL3_16:
         case DECIMAL3:
            return (double)value / 1000.0;
      }
   }
}
