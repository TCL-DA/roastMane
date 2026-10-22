package com.sun.scenario.effect;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
import com.sun.javafx.util.Utils;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;

public class EffectHelper {
   private static EffectHelper.EffectAccessor effectAccessor;

   private EffectHelper() {
   }

   public static Effect getPeer(javafx.scene.effect.Effect var0) {
      return effectAccessor.getPeer(var0);
   }

   public static void sync(javafx.scene.effect.Effect var0) {
      effectAccessor.sync(var0);
   }

   public static IntegerProperty effectDirtyProperty(javafx.scene.effect.Effect var0) {
      return effectAccessor.effectDirtyProperty(var0);
   }

   public static boolean isEffectDirty(javafx.scene.effect.Effect var0) {
      return effectAccessor.isEffectDirty(var0);
   }

   public static BaseBounds getBounds(javafx.scene.effect.Effect var0, BaseBounds var1, BaseTransform var2, Node var3, BoundsAccessor var4) {
      return effectAccessor.getBounds(var0, var1, var2, var3, var4);
   }

   public static javafx.scene.effect.Effect copy(javafx.scene.effect.Effect var0) {
      return effectAccessor.copy(var0);
   }

   public static Blend.Mode getToolkitBlendMode(BlendMode var0) {
      return effectAccessor.getToolkitBlendMode(var0);
   }

   public static void setEffectAccessor(EffectHelper.EffectAccessor var0) {
      if (effectAccessor != null) {
         throw new IllegalStateException();
      } else {
         effectAccessor = var0;
      }
   }

   static {
      Utils.forceInit(javafx.scene.effect.Effect.class);
   }

   public interface EffectAccessor {
      Effect getPeer(javafx.scene.effect.Effect var1);

      void sync(javafx.scene.effect.Effect var1);

      IntegerProperty effectDirtyProperty(javafx.scene.effect.Effect var1);

      boolean isEffectDirty(javafx.scene.effect.Effect var1);

      BaseBounds getBounds(javafx.scene.effect.Effect var1, BaseBounds var2, BaseTransform var3, Node var4, BoundsAccessor var5);

      javafx.scene.effect.Effect copy(javafx.scene.effect.Effect var1);

      Blend.Mode getToolkitBlendMode(BlendMode var1);
   }
}
