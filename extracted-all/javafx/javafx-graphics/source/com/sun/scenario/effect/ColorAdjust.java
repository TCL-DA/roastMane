package com.sun.scenario.effect;

import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.scenario.effect.impl.state.RenderState;

public class ColorAdjust extends CoreEffect<RenderState> {
   private float hue = 0.0F;
   private float saturation = 0.0F;
   private float brightness = 0.0F;
   private float contrast = 0.0F;

   public ColorAdjust() {
      this(DefaultInput);
   }

   public ColorAdjust(Effect var1) {
      super(var1);
      this.updatePeerKey("ColorAdjust");
   }

   public final Effect getInput() {
      return this.getInputs().get(0);
   }

   public void setInput(Effect var1) {
      this.setInput(0, var1);
   }

   public float getHue() {
      return this.hue;
   }

   public void setHue(float var1) {
      if (!(var1 < -1.0F) && !(var1 > 1.0F)) {
         float var2 = this.hue;
         this.hue = var1;
      } else {
         throw new IllegalArgumentException("Hue must be in the range [-1, 1]");
      }
   }

   public float getSaturation() {
      return this.saturation;
   }

   public void setSaturation(float var1) {
      if (!(var1 < -1.0F) && !(var1 > 1.0F)) {
         float var2 = this.saturation;
         this.saturation = var1;
      } else {
         throw new IllegalArgumentException("Saturation must be in the range [-1, 1]");
      }
   }

   public float getBrightness() {
      return this.brightness;
   }

   public void setBrightness(float var1) {
      if (!(var1 < -1.0F) && !(var1 > 1.0F)) {
         float var2 = this.brightness;
         this.brightness = var1;
      } else {
         throw new IllegalArgumentException("Brightness must be in the range [-1, 1]");
      }
   }

   public float getContrast() {
      return this.contrast;
   }

   public void setContrast(float var1) {
      if (!(var1 < -1.0F) && !(var1 > 1.0F)) {
         float var2 = this.contrast;
         this.contrast = var1;
      } else {
         throw new IllegalArgumentException("Contrast must be in the range [-1, 1]");
      }
   }

   @Override
   public RenderState getRenderState(FilterContext var1, BaseTransform var2, Rectangle var3, Object var4, Effect var5) {
      return RenderState.RenderSpaceRenderState;
   }

   @Override
   public boolean reducesOpaquePixels() {
      Effect var1 = this.getInput();
      return var1 != null && var1.reducesOpaquePixels();
   }
}
