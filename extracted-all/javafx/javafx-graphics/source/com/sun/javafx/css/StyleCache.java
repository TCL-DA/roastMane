package com.sun.javafx.css;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public final class StyleCache {
   private Map<StyleCacheEntry.Key, StyleCacheEntry> entries;

   public void clear() {
      if (this.entries != null) {
         Thread.dumpStack();
         this.entries.clear();
      }
   }

   public StyleCacheEntry getStyleCacheEntry(StyleCacheEntry.Key var1) {
      StyleCacheEntry var2 = null;
      if (this.entries != null) {
         var2 = this.entries.get(var1);
      }

      return var2;
   }

   public void addStyleCacheEntry(StyleCacheEntry.Key var1, StyleCacheEntry var2) {
      if (this.entries == null) {
         this.entries = new HashMap<>(5);
      }

      this.entries.put(var1, var2);
   }

   public static final class Key {
      final int[] styleMapIds;
      private int hash = Integer.MIN_VALUE;

      public Key(int[] var1, int var2) {
         this.styleMapIds = new int[var2];
         System.arraycopy(var1, 0, this.styleMapIds, 0, var2);
      }

      public Key(StyleCache.Key var1) {
         this(var1.styleMapIds, var1.styleMapIds.length);
      }

      public int[] getStyleMapIds() {
         return this.styleMapIds;
      }

      @Override
      public String toString() {
         return Arrays.toString(this.styleMapIds);
      }

      @Override
      public int hashCode() {
         if (this.hash == Integer.MIN_VALUE) {
            this.hash = 3;
            if (this.styleMapIds != null) {
               for (int var1 = 0; var1 < this.styleMapIds.length; var1++) {
                  int var2 = this.styleMapIds[var1];
                  this.hash = 17 * (this.hash + var2);
               }
            }
         }

         return this.hash;
      }

      @Override
      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (var1 != null && var1.getClass() == this.getClass()) {
            StyleCache.Key var2 = (StyleCache.Key)var1;
            if (this.hash != var2.hash) {
               return false;
            } else if (this.styleMapIds == null ^ var2.styleMapIds == null) {
               return false;
            } else if (this.styleMapIds == null) {
               return true;
            } else if (this.styleMapIds.length != var2.styleMapIds.length) {
               return false;
            } else {
               for (int var3 = 0; var3 < this.styleMapIds.length; var3++) {
                  if (this.styleMapIds[var3] != var2.styleMapIds[var3]) {
                     return false;
                  }
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }
}
