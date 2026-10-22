package org.cropster.roastingintelligence.util;

public enum Smoothing {
   LOW(5, 10, -1),
   STANDARD(10, 18, 0),
   HIGH(10, 30, -1);

   private int _windowLength;
   private int _interval;
   private final int _offset;

   private Smoothing(int windowLength, int interval, int offset) {
      this._windowLength = windowLength;
      this._interval = interval;
      this._offset = offset;
   }

   public int getWindowLength() {
      return this._windowLength;
   }

   public int getInterval() {
      return this._interval;
   }

   public int getOffset() {
      return this._offset;
   }
}
