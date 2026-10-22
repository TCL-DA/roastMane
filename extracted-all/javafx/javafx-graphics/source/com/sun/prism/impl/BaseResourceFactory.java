package com.sun.prism.impl;

import com.sun.javafx.geom.Rectangle;
import com.sun.prism.Image;
import com.sun.prism.PixelFormat;
import com.sun.prism.ResourceFactory;
import com.sun.prism.ResourceFactoryListener;
import com.sun.prism.Texture;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.util.Pair;

public abstract class BaseResourceFactory implements ResourceFactory {
   private final Map<Image, Texture> clampTexCache;
   private final Map<Image, Texture> repeatTexCache;
   private final Map<Image, Texture> mipmapTexCache;
   private final WeakHashMap<ResourceFactoryListener, Boolean> listenerMap = new WeakHashMap<>();
   private boolean disposed = false;
   private Texture regionTexture;
   private Texture glyphTexture;
   private boolean superShaderAllowed;

   public BaseResourceFactory() {
      this(new WeakHashMap<>(), new WeakHashMap<>(), new WeakHashMap<>());
   }

   public BaseResourceFactory(Map<Image, Texture> var1, Map<Image, Texture> var2, Map<Image, Texture> var3) {
      this.clampTexCache = var1;
      this.repeatTexCache = var2;
      this.mipmapTexCache = var3;
   }

   @Override
   public void addFactoryListener(ResourceFactoryListener var1) {
      this.listenerMap.put(var1, Boolean.TRUE);
   }

   @Override
   public void removeFactoryListener(ResourceFactoryListener var1) {
      this.listenerMap.remove(var1);
   }

   @Override
   public boolean isDeviceReady() {
      return !this.isDisposed();
   }

   protected void clearTextureCache() {
      this.clearTextureCache(this.clampTexCache);
      this.clearTextureCache(this.repeatTexCache);
      this.clearTextureCache(this.mipmapTexCache);
   }

   protected void clearTextureCache(Map<Image, Texture> var1) {
      for (Texture var4 : var1.values()) {
         var4.dispose();
      }

      var1.clear();
   }

   protected ResourceFactoryListener[] getFactoryListeners() {
      return this.listenerMap.keySet().toArray(new ResourceFactoryListener[0]);
   }

   private void disposeResources() {
      this.clampTexCache.clear();
      this.repeatTexCache.clear();
      this.mipmapTexCache.clear();
      if (this.regionTexture != null) {
         this.regionTexture.dispose();
         this.regionTexture = null;
      }

      if (this.glyphTexture != null) {
         this.glyphTexture.dispose();
         this.glyphTexture = null;
      }
   }

   protected void notifyReset() {
      this.disposeResources();
      ResourceFactoryListener[] var1 = this.getFactoryListeners();

      for (ResourceFactoryListener var5 : var1) {
         if (null != var5) {
            var5.factoryReset();
         }
      }
   }

   @Override
   public void dispose() {
      this.disposeResources();
      this.disposed = true;
      ResourceFactoryListener[] var1 = this.getFactoryListeners();

      for (ResourceFactoryListener var5 : var1) {
         if (null != var5) {
            var5.factoryReleased();
         }
      }
   }

   static long sizeWithMipMap(int var0, int var1, PixelFormat var2) {
      long var3 = 0L;
      int var5 = var2.getBytesPerPixelUnit();

      while (var0 > 1 && var1 > 1) {
         var3 += (long)var0 * (long)var1;
         var0 = var0 + 1 >> 1;
         var1 = var1 + 1 >> 1;
      }

      return ++var3 * (long)var5;
   }

   @Override
   public Texture getCachedTexture(Image var1, Texture.WrapMode var2) {
      return this.checkDisposed() ? null : this.getCachedTexture(var1, var2, false);
   }

   @Override
   public Texture getCachedTexture(Image var1, Texture.WrapMode var2, boolean var3) {
      if (this.checkDisposed()) {
         return null;
      } else if (var1 == null) {
         throw new IllegalArgumentException("Image must be non-null");
      } else {
         Map var4;
         if (var2 == Texture.WrapMode.CLAMP_TO_EDGE) {
            if (var3) {
               throw new IllegalArgumentException("Mipmap not supported with CLAMP mode: useMipmap = " + var3 + ", wrapMode = " + var2);
            }

            var4 = this.clampTexCache;
         } else {
            if (var2 != Texture.WrapMode.REPEAT) {
               throw new IllegalArgumentException("no caching for " + var2);
            }

            var4 = var3 ? this.mipmapTexCache : this.repeatTexCache;
         }

         Texture var5 = (Texture)var4.get(var1);
         if (var5 != null) {
            var5.lock();
            if (var5.isSurfaceLost()) {
               var4.remove(var1);
               var5 = null;
            }
         }

         if (!var3 && var5 == null) {
            Texture var6 = (var2 == Texture.WrapMode.REPEAT ? this.clampTexCache : this.repeatTexCache).get(var1);
            if (var6 != null) {
               var6.lock();
               if (!var6.isSurfaceLost()) {
                  var5 = var6.getSharedTexture(var2);
                  if (var5 != null) {
                     var5.contentsUseful();
                     var4.put(var1, var5);
                  }
               }

               var6.unlock();
            }
         }

         Pair var12 = var1.getSerial().getIdRect();
         if (var5 == null) {
            int var7 = var1.getWidth();
            int var8 = var1.getHeight();
            TextureResourcePool var9 = this.getTextureResourcePool();
            long var10 = var3 ? sizeWithMipMap(var7, var8, var1.getPixelFormat()) : var9.estimateTextureSize(var7, var8, var1.getPixelFormat());
            if (!var9.prepareForAllocation(var10)) {
               return null;
            }

            var5 = this.createTexture(var1, Texture.Usage.DEFAULT, var2, var3);
            if (var5 != null) {
               var5.setLastImageSerial((Integer)var12.getKey());
               var4.put(var1, var5);
            }
         } else if (var5.getLastImageSerial() != (Integer)var12.getKey()) {
            if ((Integer)var12.getKey() - var5.getLastImageSerial() == 1 && var12.getValue() != null) {
               Rectangle var13 = (Rectangle)var12.getValue();
               var5.update(
                  var1.getPixelBuffer(), var1.getPixelFormat(), var13.x, var13.y, var13.x, var13.y, var13.width, var13.height, var1.getScanlineStride(), false
               );
            } else {
               var5.update(var1, 0, 0, var1.getWidth(), var1.getHeight(), false);
            }

            var5.setLastImageSerial((Integer)var12.getKey());
         }

         return var5;
      }
   }

   @Override
   public Texture createTexture(Image var1, Texture.Usage var2, Texture.WrapMode var3) {
      return this.checkDisposed() ? null : this.createTexture(var1, var2, var3, false);
   }

   @Override
   public Texture createTexture(Image var1, Texture.Usage var2, Texture.WrapMode var3, boolean var4) {
      if (this.checkDisposed()) {
         return null;
      } else {
         PixelFormat var5 = var1.getPixelFormat();
         int var6 = var1.getWidth();
         int var7 = var1.getHeight();
         Texture var8 = this.createTexture(var5, var2, var3, var6, var7, var4);
         if (var8 != null) {
            var8.update(var1, 0, 0, var6, var7, true);
            var8.contentsUseful();
         }

         return var8;
      }
   }

   @Override
   public Texture createMaskTexture(int var1, int var2, Texture.WrapMode var3) {
      return this.createTexture(PixelFormat.BYTE_ALPHA, Texture.Usage.DEFAULT, var3, var1, var2);
   }

   @Override
   public Texture createFloatTexture(int var1, int var2) {
      return this.createTexture(PixelFormat.FLOAT_XYZW, Texture.Usage.DEFAULT, Texture.WrapMode.CLAMP_TO_ZERO, var1, var2);
   }

   @Override
   public void setRegionTexture(Texture var1) {
      if (!this.checkDisposed()) {
         this.regionTexture = var1;
         this.superShaderAllowed = PrismSettings.superShader && this.regionTexture != null && this.glyphTexture != null;
      }
   }

   @Override
   public Texture getRegionTexture() {
      return this.regionTexture;
   }

   @Override
   public void setGlyphTexture(Texture var1) {
      if (!this.checkDisposed()) {
         this.glyphTexture = var1;
         this.superShaderAllowed = PrismSettings.superShader && this.regionTexture != null && this.glyphTexture != null;
      }
   }

   @Override
   public Texture getGlyphTexture() {
      return this.glyphTexture;
   }

   @Override
   public boolean isSuperShaderAllowed() {
      return this.superShaderAllowed;
   }

   protected boolean canClampToZero() {
      return true;
   }

   protected boolean canClampToEdge() {
      return true;
   }

   protected boolean canRepeat() {
      return true;
   }

   @Override
   public boolean isWrapModeSupported(Texture.WrapMode var1) {
      switch (var1) {
         case CLAMP_NOT_NEEDED:
            return true;
         case CLAMP_TO_EDGE:
            return this.canClampToEdge();
         case REPEAT:
            return this.canRepeat();
         case CLAMP_TO_ZERO:
            return this.canClampToZero();
         case CLAMP_TO_EDGE_SIMULATED:
         case CLAMP_TO_ZERO_SIMULATED:
         case REPEAT_SIMULATED:
            throw new InternalError("Cannot test support for simulated wrap modes");
         default:
            throw new InternalError("Unrecognized wrap mode: " + var1);
      }
   }

   @Override
   public boolean isDisposed() {
      return this.disposed;
   }

   protected boolean checkDisposed() {
      if (PrismSettings.verbose && this.isDisposed()) {
         try {
            throw new IllegalStateException("attempt to use resource after factory is disposed");
         } catch (RuntimeException var2) {
            var2.printStackTrace();
         }
      }

      return this.isDisposed();
   }
}
