package com.cropster.roastingintelligence.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MinusPlusIterator implements Iterator<Integer> {
   private int lastValue;
   private int value;

   public MinusPlusIterator(int lastValue) {
      this.lastValue = lastValue;
   }

   @Override
   public boolean hasNext() {
      return this.lastValue >= 0 ? this.value < this.lastValue : this.value > this.lastValue;
   }

   public Integer next() {
      if (!this.hasNext()) {
         throw new NoSuchElementException("Last value is " + this.lastValue);
      } else {
         if (this.value < 0) {
            this.value = Math.abs(this.value);
         } else {
            this.value = -(this.value + 1);
         }

         return this.value;
      }
   }
}
