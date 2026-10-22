package com.phidget22;

import java.util.Arrays;

public class IRRawDataEvent {
   IR Source;
   int[] Data;

   public IRRawDataEvent(IR var1, int[] var2) {
      this.Source = var1;
      this.Data = var2;
   }

   public IR getSource() {
      return this.Source;
   }

   public int[] getData() {
      return this.Data;
   }

   @Override
   public String toString() {
      return "[IRRawDataEvent](Source: " + this.Source + ", Data: " + Arrays.toString(this.Data) + ")";
   }
}
