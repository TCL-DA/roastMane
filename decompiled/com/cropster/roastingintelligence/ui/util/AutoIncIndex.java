package com.cropster.roastingintelligence.ui.util;

public class AutoIncIndex {
   private int index;

   public int getIdx() {
      return this.index;
   }

   public int getInc() {
      return ++this.index;
   }
}
