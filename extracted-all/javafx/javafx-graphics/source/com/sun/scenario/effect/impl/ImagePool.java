package com.sun.scenario.effect.impl;

import com.sun.scenario.effect.Filterable;
import java.lang.ref.SoftReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ImagePool {
   public static long numEffects;
   static long numCreated;
   static long pixelsCreated;
   static long numAccessed;
   static long pixelsAccessed;
   static final int QUANT = 32;
   private final List<SoftReference<PoolFilterable>> unlocked = new ArrayList<>();
   private final List<SoftReference<PoolFilterable>> locked = new ArrayList<>();
   private final boolean usePurgatory = Boolean.getBoolean("decora.purgatory");
   private final List<Filterable> hardPurgatory = new ArrayList<>();
   private final List<SoftReference<PoolFilterable>> softPurgatory = new ArrayList<>();

   static void printStats() {
      System.out.println("effects executed:  " + numEffects);
      System.out.println("images created:    " + numCreated);
      System.out.println("pixels created:    " + pixelsCreated);
      System.out.println("images accessed:   " + numAccessed);
      System.out.println("pixels accessed:   " + pixelsAccessed);
      if (numEffects != 0L) {
         double var0 = (double)numAccessed / (double)numEffects;
         double var2 = (double)pixelsAccessed / (double)numEffects;
         System.out.println("images per effect: " + var0);
         System.out.println("pixels per effect: " + var2);
      }
   }

   ImagePool() {
   }

   public synchronized PoolFilterable checkOut(Renderer var1, int var2, int var3) {
      if (var2 <= 0 || var3 <= 0) {
         var3 = 1;
         var2 = 1;
      }

      var2 = (var2 + 32 - 1) / 32 * 32;
      var3 = (var3 + 32 - 1) / 32 * 32;
      var2 = var1.getCompatibleWidth(var2);
      var3 = var1.getCompatibleHeight(var3);
      numAccessed++;
      pixelsAccessed += (long)var2 * (long)var3;
      SoftReference var4 = null;
      PoolFilterable var5 = null;
      int var6 = Integer.MAX_VALUE;
      Iterator var7 = this.unlocked.iterator();

      while (var7.hasNext()) {
         SoftReference var8 = (SoftReference)var7.next();
         PoolFilterable var9 = (PoolFilterable)var8.get();
         if (var9 == null) {
            var7.remove();
         } else {
            int var10 = var9.getMaxContentWidth();
            int var11 = var9.getMaxContentHeight();
            if (var10 >= var2 && var11 >= var3 && var10 * var11 / 2 <= var2 * var3) {
               int var12 = (var10 - var2) * (var11 - var3);
               if (var4 == null || var12 < var6) {
                  var9.lock();
                  if (var9.isLost()) {
                     var7.remove();
                  } else {
                     if (var5 != null) {
                        var5.unlock();
                     }

                     var4 = var8;
                     var5 = var9;
                     var6 = var12;
                  }
               }
            }
         }
      }

      if (var4 != null) {
         this.unlocked.remove(var4);
         this.locked.add(var4);
         var1.clearImage(var5);
         return var5;
      } else {
         var7 = this.locked.iterator();

         while (var7.hasNext()) {
            SoftReference var20 = (SoftReference)var7.next();
            Filterable var22 = (Filterable)var20.get();
            if (var22 == null) {
               var7.remove();
            }
         }

         PoolFilterable var21 = null;

         try {
            var21 = var1.createCompatibleImage(var2, var3);
         } catch (OutOfMemoryError var14) {
         }

         if (var21 == null) {
            this.pruneCache();

            try {
               var21 = var1.createCompatibleImage(var2, var3);
            } catch (OutOfMemoryError var13) {
            }
         }

         if (var21 != null) {
            var21.setImagePool(this);
            this.locked.add(new SoftReference<>(var21));
            numCreated++;
            pixelsCreated += (long)var2 * (long)var3;
         }

         return var21;
      }
   }

   public synchronized void checkIn(PoolFilterable var1) {
      SoftReference var2 = null;
      Filterable var3 = null;
      Iterator var4 = this.locked.iterator();

      while (var4.hasNext()) {
         SoftReference var5 = (SoftReference)var4.next();
         Filterable var6 = (Filterable)var5.get();
         if (var6 == null) {
            var4.remove();
         } else if (var6 == var1) {
            var2 = var5;
            var3 = var6;
            var1.unlock();
            break;
         }
      }

      if (var2 != null) {
         this.locked.remove(var2);
         if (this.usePurgatory) {
            this.hardPurgatory.add(var3);
            this.softPurgatory.add(var2);
         } else {
            this.unlocked.add(var2);
         }
      }
   }

   public synchronized void releasePurgatory() {
      if (this.usePurgatory && !this.softPurgatory.isEmpty()) {
         this.unlocked.addAll(this.softPurgatory);
         this.softPurgatory.clear();
         this.hardPurgatory.clear();
      }
   }

   private void pruneCache() {
      for (SoftReference var2 : this.unlocked) {
         Filterable var3 = (Filterable)var2.get();
         if (var3 != null) {
            var3.flush();
         }
      }

      this.unlocked.clear();
      System.gc();
      System.runFinalization();
      System.gc();
      System.runFinalization();
   }

   public synchronized void dispose() {
      for (SoftReference var2 : this.unlocked) {
         Filterable var3 = (Filterable)var2.get();
         if (var3 != null) {
            var3.flush();
         }
      }

      this.unlocked.clear();
      this.locked.clear();
   }

   static {
      Object var0 = AccessController.doPrivileged((PrivilegedAction)(() -> {
         if (System.getProperty("decora.showstats") != null) {
            Runtime.getRuntime().addShutdownHook(new Thread() {
               @Override
               public void run() {
                  ImagePool.printStats();
               }
            });
         }

         return null;
      }));
   }
}
