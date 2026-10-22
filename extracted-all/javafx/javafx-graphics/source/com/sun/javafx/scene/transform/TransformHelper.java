package com.sun.javafx.scene.transform;

import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.util.Utils;
import javafx.scene.Node;
import javafx.scene.transform.Transform;

public class TransformHelper {
   private static TransformHelper.TransformAccessor transformAccessor;

   private TransformHelper() {
   }

   public static void add(Transform var0, Node var1) {
      transformAccessor.add(var0, var1);
   }

   public static void remove(Transform var0, Node var1) {
      transformAccessor.remove(var0, var1);
   }

   public static void apply(Transform var0, Affine3D var1) {
      transformAccessor.apply(var0, var1);
   }

   public static BaseTransform derive(Transform var0, BaseTransform var1) {
      return transformAccessor.derive(var0, var1);
   }

   public static Transform createImmutableTransform() {
      return transformAccessor.createImmutableTransform();
   }

   public static Transform createImmutableTransform(
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
      return transformAccessor.createImmutableTransform(var0, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20, var22);
   }

   public static Transform createImmutableTransform(
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
      return transformAccessor.createImmutableTransform(var0, var1, var3, var5, var7, var9, var11, var13, var15, var17, var19, var21, var23);
   }

   public static Transform createImmutableTransform(Transform var0, Transform var1, Transform var2) {
      return transformAccessor.createImmutableTransform(var0, var1, var2);
   }

   public static void setTransformAccessor(TransformHelper.TransformAccessor var0) {
      if (transformAccessor != null) {
         throw new IllegalStateException();
      } else {
         transformAccessor = var0;
      }
   }

   static {
      Utils.forceInit(Transform.class);
   }

   public interface TransformAccessor {
      void add(Transform var1, Node var2);

      void remove(Transform var1, Node var2);

      void apply(Transform var1, Affine3D var2);

      BaseTransform derive(Transform var1, BaseTransform var2);

      Transform createImmutableTransform();

      Transform createImmutableTransform(
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
      );

      Transform createImmutableTransform(
         Transform var1,
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
         double var22,
         double var24
      );

      Transform createImmutableTransform(Transform var1, Transform var2, Transform var3);
   }
}
