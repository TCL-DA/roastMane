package com.sun.marlin;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.misc.Unsafe;

final class OffHeapArray {
   static final Unsafe UNSAFE;
   static final int SIZE_INT = Unsafe.ARRAY_INT_INDEX_SCALE;
   long address;
   long length;
   int used;

   OffHeapArray(Object var1, long var2) {
      this.address = UNSAFE.allocateMemory(var2);
      this.length = var2;
      this.used = 0;
      if (MarlinConst.LOG_UNSAFE_MALLOC) {
         MarlinUtils.logInfo(System.currentTimeMillis() + ": OffHeapArray.allocateMemory =   " + var2 + " to addr = " + this.address);
      }

      MarlinUtils.getCleaner().register(var1, () -> this.free());
   }

   void resize(long var1) {
      this.address = UNSAFE.reallocateMemory(this.address, var1);
      this.length = var1;
      if (MarlinConst.LOG_UNSAFE_MALLOC) {
         MarlinUtils.logInfo(System.currentTimeMillis() + ": OffHeapArray.reallocateMemory = " + var1 + " to addr = " + this.address);
      }
   }

   void free() {
      UNSAFE.freeMemory(this.address);
      if (MarlinConst.LOG_UNSAFE_MALLOC) {
         MarlinUtils.logInfo(System.currentTimeMillis() + ": OffHeapArray.freeMemory =       " + this.length + " at addr = " + this.address);
      }

      this.address = 0L;
   }

   void fill(byte var1) {
      UNSAFE.setMemory(this.address, this.length, var1);
   }

   static {
      Unsafe var0 = AccessController.doPrivileged(new PrivilegedAction<Unsafe>() {
         public Unsafe run() {
            Object var1 = null;

            try {
               Field var2 = Unsafe.class.getDeclaredField("theUnsafe");
               var2.setAccessible(true);
               return (Unsafe)var2.get(null);
            } catch (Exception var3) {
               throw new InternalError("Unable to get sun.misc.Unsafe instance", var3);
            }
         }
      });
      UNSAFE = var0;
   }
}
