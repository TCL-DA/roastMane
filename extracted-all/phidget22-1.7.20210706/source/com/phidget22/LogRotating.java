package com.phidget22;

public class LogRotating {
   public long size;
   public int keepCount;

   @Override
   public String toString() {
      return "[LogRotating](size: " + this.size + ", keepCount: " + this.keepCount + ")";
   }
}
