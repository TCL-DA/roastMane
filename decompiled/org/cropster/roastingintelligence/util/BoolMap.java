package org.cropster.roastingintelligence.util;

import java.util.HashMap;

public class BoolMap<T> extends HashMap<T, Boolean> {
   private static final long serialVersionUID = 4335302546806494400L;

   public boolean getOr() {
      if (this.isEmpty()) {
         throw new IllegalStateException("Map is empty!");
      } else {
         Boolean result = null;

         for (Boolean b : this.values()) {
            result = result == null ? b : result || b;
         }

         return result;
      }
   }

   public boolean getAnd() {
      if (this.isEmpty()) {
         throw new IllegalStateException("Map is empty!");
      } else {
         Boolean result = null;

         for (Boolean b : this.values()) {
            result = result == null ? b : result && b;
         }

         return result;
      }
   }

   public int getTrueCount() {
      int count = 0;

      for (Boolean b : this.values()) {
         if (Boolean.TRUE.equals(b)) {
            count++;
         }
      }

      return count;
   }

   public int getFalseCount() {
      int count = 0;

      for (Boolean b : this.values()) {
         if (Boolean.FALSE.equals(b)) {
            count++;
         }
      }

      return count;
   }
}
