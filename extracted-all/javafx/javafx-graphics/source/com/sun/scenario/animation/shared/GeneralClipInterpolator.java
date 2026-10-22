package com.sun.scenario.animation.shared;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.value.WritableValue;

class GeneralClipInterpolator extends ClipInterpolator {
   private KeyFrame[] keyFrames;
   private long[] keyFrameTicks;
   private InterpolationInterval[][] interval = new InterpolationInterval[0][];
   private int[] undefinedStartValues = new int[0];
   private boolean invalid = true;

   GeneralClipInterpolator(KeyFrame[] var1, long[] var2) {
      this.keyFrames = var1;
      this.keyFrameTicks = var2;
   }

   @Override
   ClipInterpolator setKeyFrames(KeyFrame[] var1, long[] var2) {
      if (ClipInterpolator.getRealKeyFrameCount(var1) == 2) {
         return ClipInterpolator.create(var1, var2);
      } else {
         this.keyFrames = var1;
         this.keyFrameTicks = var2;
         this.invalid = true;
         return this;
      }
   }

   @Override
   void validate(boolean var1) {
      if (this.invalid) {
         HashMap var2 = new HashMap();
         int var3 = this.keyFrames.length;

         int var4;
         for (var4 = 0; var4 < var3; var4++) {
            KeyFrame var5 = this.keyFrames[var4];
            if (this.keyFrameTicks[var4] != 0L) {
               break;
            }

            for (KeyValue var7 : var5.getValues()) {
               var2.put(var7.getTarget(), var7);
            }
         }

         HashMap var18 = new HashMap();

         HashSet var19;
         for (var19 = new HashSet(); var4 < var3; var4++) {
            KeyFrame var20 = this.keyFrames[var4];
            long var8 = this.keyFrameTicks[var4];

            for (KeyValue var11 : var20.getValues()) {
               WritableValue var12 = var11.getTarget();
               List var13 = (List)var18.get(var12);
               KeyValue var14 = (KeyValue)var2.get(var12);
               if (var13 == null) {
                  ArrayList var26 = new ArrayList();
                  var18.put(var12, var26);
                  if (var14 == null) {
                     var26.add(InterpolationInterval.create(var11, var8));
                     var19.add(var12);
                  } else {
                     var26.add(InterpolationInterval.create(var11, var8, var14, var8));
                  }
               } else {
                  assert var14 != null;

                  var13.add(InterpolationInterval.create(var11, var8, var14, var8 - ((InterpolationInterval)var13.get(var13.size() - 1)).ticks));
               }

               var2.put(var12, var11);
            }
         }

         int var21 = var18.size();
         if (this.interval.length != var21) {
            this.interval = new InterpolationInterval[var21][];
         }

         int var22 = var19.size();
         if (this.undefinedStartValues.length != var22) {
            this.undefinedStartValues = new int[var22];
         }

         int var9 = 0;
         Iterator var23 = var18.entrySet().iterator();

         for (int var24 = 0; var24 < var21; var24++) {
            Entry var25 = (Entry)var23.next();
            this.interval[var24] = new InterpolationInterval[((List)var25.getValue()).size()];
            ((List)var25.getValue()).toArray(this.interval[var24]);
            if (var19.contains(var25.getKey())) {
               this.undefinedStartValues[var9++] = var24;
            }
         }

         this.invalid = false;
      } else if (var1) {
         int var15 = this.undefinedStartValues.length;

         for (int var16 = 0; var16 < var15; var16++) {
            int var17 = this.undefinedStartValues[var16];
            this.interval[var17][0].recalculateStartValue();
         }
      }
   }

   @Override
   void interpolate(long var1) {
      int var3 = this.interval.length;

      label24:
      for (int var4 = 0; var4 < var3; var4++) {
         InterpolationInterval[] var5 = this.interval[var4];
         int var6 = var5.length;
         long var7 = 0L;

         for (int var9 = 0; var9 < var6 - 1; var9++) {
            InterpolationInterval var10 = var5[var9];
            long var11 = var10.ticks;
            if (var1 <= var11) {
               double var13 = (double)(var1 - var7) / (double)(var11 - var7);
               var10.interpolate(var13);
               continue label24;
            }

            var7 = var11;
         }

         InterpolationInterval var15 = var5[var6 - 1];
         double var16 = Math.min(1.0, (double)(var1 - var7) / (double)(var15.ticks - var7));
         var15.interpolate(var16);
      }
   }
}
