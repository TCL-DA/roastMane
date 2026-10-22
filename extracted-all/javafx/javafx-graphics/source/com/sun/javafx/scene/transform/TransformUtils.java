package com.sun.javafx.scene.transform;

import javafx.scene.transform.Transform;

public class TransformUtils {
   public static Transform immutableTransform(
      double var0,
      double var2,
      double var4,
      double var6,
      double var8,
      double var10,
      double var12,
      double var14,
      double var16,
      double var18,
      double var20,
      double var22
   ) {
      return TransformHelper.createImmutableTransform(var0, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20, var22);
   }

   public static Transform immutableTransform(Transform var0) {
      return TransformHelper.createImmutableTransform(
         var0.getMxx(),
         var0.getMxy(),
         var0.getMxz(),
         var0.getTx(),
         var0.getMyx(),
         var0.getMyy(),
         var0.getMyz(),
         var0.getTy(),
         var0.getMzx(),
         var0.getMzy(),
         var0.getMzz(),
         var0.getTz()
      );
   }

   public static Transform immutableTransform(
      Transform var0,
      double var1,
      double var3,
      double var5,
      double var7,
      double var9,
      double var11,
      double var13,
      double var15,
      double var17,
      double var19,
      double var21,
      double var23
   ) {
      return TransformHelper.createImmutableTransform(var0, var1, var3, var5, var7, var9, var11, var13, var15, var17, var19, var21, var23);
   }

   public static Transform immutableTransform(Transform var0, Transform var1) {
      return TransformHelper.createImmutableTransform(
         var0,
         var1.getMxx(),
         var1.getMxy(),
         var1.getMxz(),
         var1.getTx(),
         var1.getMyx(),
         var1.getMyy(),
         var1.getMyz(),
         var1.getTy(),
         var1.getMzx(),
         var1.getMzy(),
         var1.getMzz(),
         var1.getTz()
      );
   }

   public static Transform immutableTransform(Transform var0, Transform var1, Transform var2) {
      return TransformHelper.createImmutableTransform(var0, var1, var2);
   }
}
