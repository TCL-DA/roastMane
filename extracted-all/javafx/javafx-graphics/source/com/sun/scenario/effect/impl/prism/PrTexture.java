package com.sun.scenario.effect.impl.prism;

import com.sun.javafx.geom.Rectangle;
import com.sun.prism.Texture;
import com.sun.scenario.effect.LockableResource;

public class PrTexture<T extends Texture> implements LockableResource {
   private final T tex;
   private final Rectangle bounds;

   public PrTexture(T var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("Texture must be non-null");
      } else {
         this.tex = (T)var1;
         this.bounds = new Rectangle(var1.getPhysicalWidth(), var1.getPhysicalHeight());
      }
   }

   @Override
   public void lock() {
      if (this.tex != null) {
         this.tex.lock();
      }
   }

   @Override
   public void unlock() {
      if (this.tex != null) {
         this.tex.unlock();
      }
   }

   @Override
   public boolean isLost() {
      return this.tex.isSurfaceLost();
   }

   public Rectangle getNativeBounds() {
      return this.bounds;
   }

   public T getTextureObject() {
      return this.tex;
   }
}
