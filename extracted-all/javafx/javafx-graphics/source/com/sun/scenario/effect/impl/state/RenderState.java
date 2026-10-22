package com.sun.scenario.effect.impl.state;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;

public interface RenderState {
   RenderState UserSpaceRenderState = new RenderState() {
      @Override
      public RenderState.EffectCoordinateSpace getEffectTransformSpace() {
         return RenderState.EffectCoordinateSpace.UserSpace;
      }

      @Override
      public BaseTransform getInputTransform(BaseTransform var1) {
         return BaseTransform.IDENTITY_TRANSFORM;
      }

      @Override
      public BaseTransform getResultTransform(BaseTransform var1) {
         return var1;
      }

      @Override
      public Rectangle getInputClip(int var1, Rectangle var2) {
         return var2;
      }
   };
   RenderState UnclippedUserSpaceRenderState = new RenderState() {
      @Override
      public RenderState.EffectCoordinateSpace getEffectTransformSpace() {
         return RenderState.EffectCoordinateSpace.UserSpace;
      }

      @Override
      public BaseTransform getInputTransform(BaseTransform var1) {
         return BaseTransform.IDENTITY_TRANSFORM;
      }

      @Override
      public BaseTransform getResultTransform(BaseTransform var1) {
         return var1;
      }

      @Override
      public Rectangle getInputClip(int var1, Rectangle var2) {
         return null;
      }
   };
   RenderState RenderSpaceRenderState = new RenderState() {
      @Override
      public RenderState.EffectCoordinateSpace getEffectTransformSpace() {
         return RenderState.EffectCoordinateSpace.RenderSpace;
      }

      @Override
      public BaseTransform getInputTransform(BaseTransform var1) {
         return var1;
      }

      @Override
      public BaseTransform getResultTransform(BaseTransform var1) {
         return BaseTransform.IDENTITY_TRANSFORM;
      }

      @Override
      public Rectangle getInputClip(int var1, Rectangle var2) {
         return var2;
      }
   };

   RenderState.EffectCoordinateSpace getEffectTransformSpace();

   BaseTransform getInputTransform(BaseTransform var1);

   BaseTransform getResultTransform(BaseTransform var1);

   Rectangle getInputClip(int var1, Rectangle var2);

   public static enum EffectCoordinateSpace {
      UserSpace,
      CustomSpace,
      RenderSpace;
   }
}
