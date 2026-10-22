package com.sun.javafx.binding;

import java.util.function.Predicate;
import javafx.beans.WeakListener;

public class ExpressionHelperBase {
   protected static int trim(int var0, Object[] var1) {
      Predicate var2 = var0x -> var0x instanceof WeakListener && ((WeakListener)var0x).wasGarbageCollected();
      int var3 = 0;

      while (var3 < var0 && !var2.test(var1[var3])) {
         var3++;
      }

      if (var3 < var0) {
         for (int var4 = var3 + 1; var4 < var0; var4++) {
            if (!var2.test(var1[var4])) {
               var1[var3++] = var1[var4];
            }
         }

         int var5 = var0;

         for (var0 = var3; var3 < var5; var3++) {
            var1[var3] = null;
         }
      }

      return var0;
   }
}
