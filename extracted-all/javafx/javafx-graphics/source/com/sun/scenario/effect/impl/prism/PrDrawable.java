package com.sun.scenario.effect.impl.prism;

import com.sun.prism.Graphics;
import com.sun.prism.RTTexture;
import com.sun.scenario.effect.FilterContext;
import com.sun.scenario.effect.impl.ImagePool;
import com.sun.scenario.effect.impl.PoolFilterable;
import com.sun.scenario.effect.impl.Renderer;
import java.lang.ref.WeakReference;

public abstract class PrDrawable extends PrTexture<RTTexture> implements PoolFilterable {
   private WeakReference<ImagePool> pool;

   public static PrDrawable create(FilterContext var0, RTTexture var1) {
      return ((PrRenderer)Renderer.getRenderer(var0)).createDrawable(var1);
   }

   protected PrDrawable(RTTexture var1) {
      super(var1);
   }

   @Override
   public void setImagePool(ImagePool var1) {
      this.pool = new WeakReference<>(var1);
   }

   @Override
   public ImagePool getImagePool() {
      return this.pool == null ? null : this.pool.get();
   }

   @Override
   public float getPixelScale() {
      return 1.0F;
   }

   @Override
   public int getMaxContentWidth() {
      return this.getTextureObject().getMaxContentWidth();
   }

   @Override
   public int getMaxContentHeight() {
      return this.getTextureObject().getMaxContentHeight();
   }

   @Override
   public void setContentWidth(int var1) {
      this.getTextureObject().setContentWidth(var1);
   }

   @Override
   public void setContentHeight(int var1) {
      this.getTextureObject().setContentHeight(var1);
   }

   public abstract Graphics createGraphics();

   public void clear() {
      Graphics var1 = this.createGraphics();
      var1.clear();
   }
}
