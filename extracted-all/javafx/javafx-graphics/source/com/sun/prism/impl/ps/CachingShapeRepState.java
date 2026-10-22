package com.sun.prism.impl.ps;

import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.prism.BasicStroke;
import com.sun.prism.Graphics;
import com.sun.prism.Texture;
import com.sun.prism.impl.Disposer;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.impl.VertexBuffer;
import com.sun.prism.impl.shape.MaskData;
import com.sun.prism.impl.shape.ShapeUtil;
import com.sun.prism.paint.Paint;
import com.sun.prism.ps.Shader;
import java.util.Arrays;
import java.util.Comparator;

class CachingShapeRepState {
   private static final BaseTransform IDENT = BaseTransform.IDENTITY_TRANSFORM;
   private static final CachingShapeRepState.MaskCache maskCache = new CachingShapeRepState.MaskCache();
   private static final int CACHE_THRESHOLD = 2;
   private int renderCount;
   private Boolean tryCache;
   private BaseTransform lastXform;
   private final CachingShapeRepState.MaskTexData texData;
   private float[] bbox;
   private final Object disposerReferent = new Object();
   private final Disposer.Record disposerRecord;

   private static boolean equalsIgnoreTranslation(BaseTransform var0, BaseTransform var1) {
      return var0 == var1
         ? true
         : var0.getMxx() == var1.getMxx() && var0.getMxy() == var1.getMxy() && var0.getMyx() == var1.getMyx() && var0.getMyy() == var1.getMyy();
   }

   CachingShapeRepState() {
      this.texData = new CachingShapeRepState.MaskTexData();
      this.disposerRecord = new CachingShapeRepState.CSRDisposerRecord(this.texData);
      Disposer.addRecord(this.disposerReferent, this.disposerRecord);
   }

   void fillNoCache(Graphics var1, Shape var2) {
      var1.fill(var2);
   }

   void drawNoCache(Graphics var1, Shape var2) {
      var1.draw(var2);
   }

   void invalidate() {
      this.renderCount = 0;
      this.tryCache = null;
      this.lastXform = null;
      this.bbox = null;
   }

   private void invalidateMaskTexData() {
      this.tryCache = null;
      this.lastXform = null;
      maskCache.unref(this.texData);
   }

   void render(Graphics var1, Shape var2, RectBounds var3, BasicStroke var4) {
      BaseTransform var5 = var1.getTransformNoClone();
      boolean var6;
      boolean var7;
      if (this.lastXform == null) {
         var7 = true;
         var6 = true;
      } else if (equalsIgnoreTranslation(var5, this.lastXform)) {
         var6 = false;
         var7 = var5.getMxt() != this.lastXform.getMxt() || var5.getMyt() != this.lastXform.getMyt();
      } else {
         var7 = true;
         var6 = true;
      }

      if (var6) {
         this.invalidateMaskTexData();
         this.renderCount = 0;
      }

      if (var6 || var7) {
         if (this.lastXform == null) {
            this.lastXform = var5.copy();
         } else {
            this.lastXform.setTransform(var5);
         }
      }

      if (this.texData.cacheEntry != null) {
         this.texData.maskTex.lock();
         if (this.texData.maskTex.isSurfaceLost()) {
            this.texData.maskTex.unlock();
            this.invalidateMaskTexData();
         }
      }

      RectBounds var8 = null;
      boolean var9 = false;
      if (this.tryCache == null) {
         if (var5.isIdentity()) {
            var8 = var3;
         } else {
            var8 = new RectBounds();
            var9 = true;
            var8 = (RectBounds)var5.transform(var3, var8);
         }

         this.tryCache = !var8.isEmpty() && maskCache.hasRoom(var8);
      }

      this.renderCount++;
      if (this.tryCache != Boolean.FALSE && this.renderCount >= 2 && var1 instanceof BaseShaderGraphics && !((BaseShaderGraphics)var1).isComplexPaint()) {
         BaseShaderGraphics var10 = (BaseShaderGraphics)var1;
         BaseShaderContext var11 = var10.getContext();
         if (var7 || this.texData.cacheEntry == null) {
            if (var8 == null) {
               if (var5.isIdentity()) {
                  var8 = var3;
               } else {
                  var8 = new RectBounds();
                  var9 = true;
                  var8 = (RectBounds)var5.transform(var3, var8);
               }
            }

            if (this.texData.cacheEntry != null) {
               this.texData.adjustOrigin(var5);
            } else {
               maskCache.get(var11, this.texData, var2, var4, var5, var8, var9, var1.isAntialiasedShape());
            }
         }

         Paint var12 = var10.getPaint();
         float var13 = 0.0F;
         float var14 = 0.0F;
         float var15 = 0.0F;
         float var16 = 0.0F;
         if (var12.isProportional()) {
            if (this.bbox == null) {
               this.bbox = new float[]{Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
               Shape.accumulate(this.bbox, var2, BaseTransform.IDENTITY_TRANSFORM);
            }

            var13 = this.bbox[0];
            var14 = this.bbox[1];
            var15 = this.bbox[2] - var13;
            var16 = this.bbox[3] - var14;
         }

         int var17 = this.texData.maskW;
         int var18 = this.texData.maskH;
         Texture var19 = this.texData.maskTex;
         float var20 = (float)var19.getPhysicalWidth();
         float var21 = (float)var19.getPhysicalHeight();
         float var22 = this.texData.maskX;
         float var23 = this.texData.maskY;
         float var24 = var22 + (float)var17;
         float var25 = var23 + (float)var18;
         float var26 = (float)var19.getContentX() / var20;
         float var27 = (float)var19.getContentY() / var21;
         float var28 = var26 + (float)var17 / var20;
         float var29 = var27 + (float)var18 / var21;
         if (PrismSettings.primTextureSize != 0) {
            Shader var30 = var11.validatePaintOp(var10, IDENT, BaseShaderContext.MaskType.ALPHA_TEXTURE, this.texData.maskTex, var13, var14, var15, var16);
            VertexBuffer var31 = var11.getVertexBuffer();
            var31.addQuad(var22, var23, var24, var25, var26, var27, var28, var29, var10.getPaintTextureTx(var5, var30, var13, var14, var15, var16));
         } else {
            var11.validatePaintOp(var10, IDENT, this.texData.maskTex, var13, var14, var15, var16);
            VertexBuffer var34 = var11.getVertexBuffer();
            var34.addQuad(var22, var23, var24, var25, var26, var27, var28, var29);
         }

         var19.unlock();
      } else {
         if (var4 == null) {
            this.fillNoCache(var1, var2);
         } else {
            this.drawNoCache(var1, var2);
         }
      }
   }

   void dispose() {
      this.invalidate();
   }

   private static class CSRDisposerRecord implements Disposer.Record {
      private CachingShapeRepState.MaskTexData texData;

      private CSRDisposerRecord(CachingShapeRepState.MaskTexData var1) {
         this.texData = var1;
      }

      @Override
      public void dispose() {
         if (this.texData != null) {
            CachingShapeRepState.maskCache.unref(this.texData);
            this.texData = null;
         }
      }
   }

   private static class CacheEntry {
      Shape shape;
      BasicStroke stroke;
      BaseTransform xform;
      RectBounds xformBounds;
      CachingShapeRepState.MaskTexData texData;
      boolean antialiasedShape;
      int refCount;
   }

   private static class MaskCache {
      private static final int MAX_MASK_DIM = 512;
      private static final int MAX_SIZE_IN_PIXELS = 4194304;
      private static Comparator<CachingShapeRepState.CacheEntry> comparator = (var0, var1) -> {
         int var2 = Float.compare(var0.xformBounds.getWidth(), var1.xformBounds.getWidth());
         return var2 != 0 ? var2 : Float.compare(var0.xformBounds.getHeight(), var1.xformBounds.getHeight());
      };
      private CachingShapeRepState.CacheEntry[] entries = new CachingShapeRepState.CacheEntry[8];
      private int entriesSize = 0;
      private int totalPixels;
      private CachingShapeRepState.CacheEntry tmpKey = new CachingShapeRepState.CacheEntry();

      private MaskCache() {
         this.tmpKey.xformBounds = new RectBounds();
      }

      private void ensureSize(int var1) {
         if (this.entries.length < var1) {
            CachingShapeRepState.CacheEntry[] var2 = new CachingShapeRepState.CacheEntry[var1 * 3 / 2];
            System.arraycopy(this.entries, 0, var2, 0, this.entries.length);
            this.entries = var2;
         }
      }

      private void addEntry(CachingShapeRepState.CacheEntry var1) {
         this.ensureSize(this.entriesSize + 1);
         int var2 = Arrays.binarySearch(this.entries, 0, this.entriesSize, var1, comparator);
         if (var2 < 0) {
            var2 = ~var2;
         }

         System.arraycopy(this.entries, var2, this.entries, var2 + 1, this.entriesSize - var2);
         this.entries[var2] = var1;
         this.entriesSize++;
      }

      private void removeEntry(CachingShapeRepState.CacheEntry var1) {
         int var2 = Arrays.binarySearch(this.entries, 0, this.entriesSize, var1, comparator);
         if (var2 < 0) {
            throw new IllegalStateException("Trying to remove a cached item that's not in the cache");
         } else {
            if (this.entries[var2] != var1) {
               this.tmpKey
                  .xformBounds
                  .deriveWithNewBounds(
                     0.0F, 0.0F, 0.0F, var1.xformBounds.getWidth(), Math.nextAfter(var1.xformBounds.getHeight(), Double.NEGATIVE_INFINITY), 0.0F
                  );
               var2 = Arrays.binarySearch(this.entries, 0, this.entriesSize, this.tmpKey, comparator);
               if (var2 < 0) {
                  var2 = ~var2;
               }

               this.tmpKey
                  .xformBounds
                  .deriveWithNewBounds(
                     0.0F, 0.0F, 0.0F, var1.xformBounds.getWidth(), Math.nextAfter(var1.xformBounds.getHeight(), Double.POSITIVE_INFINITY), 0.0F
                  );
               int var3 = Arrays.binarySearch(this.entries, 0, this.entriesSize, this.tmpKey, comparator);
               if (var3 < 0) {
                  var3 = ~var3;
               }

               while (this.entries[var2] != var1 && var2 < var3) {
                  var2++;
               }

               if (var2 >= var3) {
                  throw new IllegalStateException("Trying to remove a cached item that's not in the cache");
               }
            }

            System.arraycopy(this.entries, var2 + 1, this.entries, var2, this.entriesSize - var2 - 1);
            this.entriesSize--;
         }
      }

      boolean hasRoom(RectBounds var1) {
         int var2 = (int)(var1.getWidth() + 0.5F);
         int var3 = (int)(var1.getHeight() + 0.5F);
         int var4 = var2 * var3;
         return var2 <= 512 && var3 <= 512 && this.totalPixels + var4 <= 4194304;
      }

      boolean entryMatches(CachingShapeRepState.CacheEntry var1, Shape var2, BasicStroke var3, BaseTransform var4, boolean var5) {
         return var1.antialiasedShape == var5
            && CachingShapeRepState.equalsIgnoreTranslation(var4, var1.xform)
            && var1.shape.equals(var2)
            && (var3 == null ? var1.stroke == null : var3.equals(var1.stroke));
      }

      void get(
         BaseShaderContext var1,
         CachingShapeRepState.MaskTexData var2,
         Shape var3,
         BasicStroke var4,
         BaseTransform var5,
         RectBounds var6,
         boolean var7,
         boolean var8
      ) {
         if (var2 == null) {
            throw new InternalError("MaskTexData must be non-null");
         } else if (var2.cacheEntry != null) {
            throw new InternalError("CacheEntry should already be null");
         } else {
            this.tmpKey.xformBounds.deriveWithNewBounds(0.0F, 0.0F, 0.0F, var6.getWidth(), Math.nextAfter(var6.getHeight(), Double.NEGATIVE_INFINITY), 0.0F);
            int var9 = Arrays.binarySearch(this.entries, 0, this.entriesSize, this.tmpKey, comparator);
            if (var9 < 0) {
               var9 = ~var9;
            }

            this.tmpKey.xformBounds.deriveWithNewBounds(0.0F, 0.0F, 0.0F, var6.getWidth(), Math.nextAfter(var6.getHeight(), Double.POSITIVE_INFINITY), 0.0F);
            int var10 = Arrays.binarySearch(this.entries, 0, this.entriesSize, this.tmpKey, comparator);
            if (var10 < 0) {
               var10 = ~var10;
            }

            for (; var9 < var10; var9++) {
               CachingShapeRepState.CacheEntry var11 = this.entries[var9];
               if (this.entryMatches(var11, var3, var4, var5, var8)) {
                  var11.texData.maskTex.lock();
                  if (!var11.texData.maskTex.isSurfaceLost()) {
                     var11.refCount++;
                     var11.texData.copyInto(var2);
                     var2.cacheEntry = var11;
                     var2.adjustOrigin(var5);
                     return;
                  }

                  var11.texData.maskTex.unlock();
               }
            }

            MaskData var15 = ShapeUtil.rasterizeShape(var3, var4, var6, var5, true, var8);
            int var12 = var15.getWidth();
            int var13 = var15.getHeight();
            var2.maskX = (float)var15.getOriginX();
            var2.maskY = (float)var15.getOriginY();
            var2.maskW = var12;
            var2.maskH = var13;
            var2.maskTex = var1.getResourceFactory().createMaskTexture(var12, var13, Texture.WrapMode.CLAMP_TO_ZERO);
            var15.uploadToTexture(var2.maskTex, 0, 0, false);
            var2.maskTex.contentsUseful();
            CachingShapeRepState.CacheEntry var14 = new CachingShapeRepState.CacheEntry();
            var14.shape = var3.copy();
            if (var4 != null) {
               var14.stroke = var4.copy();
            }

            var14.xform = var5.copy();
            var14.xformBounds = var7 ? var6 : (RectBounds)var6.copy();
            var14.texData = var2.copy();
            var14.antialiasedShape = var8;
            var14.refCount = 1;
            var2.cacheEntry = var14;
            this.addEntry(var14);
            this.totalPixels += var12 * var13;
         }
      }

      void unref(CachingShapeRepState.MaskTexData var1) {
         if (var1 == null) {
            throw new InternalError("MaskTexData must be non-null");
         } else {
            CachingShapeRepState.CacheEntry var2 = var1.cacheEntry;
            if (var2 != null) {
               var1.cacheEntry = null;
               var1.maskTex = null;
               var2.refCount--;
               if (var2.refCount <= 0) {
                  this.removeEntry(var2);
                  var2.shape = null;
                  var2.stroke = null;
                  var2.xform = null;
                  var2.xformBounds = null;
                  var2.texData.maskTex.dispose();
                  var2.antialiasedShape = false;
                  var2.texData = null;
                  this.totalPixels = this.totalPixels - var1.maskW * var1.maskH;
               }
            }
         }
      }
   }

   private static class MaskTexData {
      private CachingShapeRepState.CacheEntry cacheEntry;
      private Texture maskTex;
      private float maskX;
      private float maskY;
      private int maskW;
      private int maskH;

      void adjustOrigin(BaseTransform var1) {
         float var2 = (float)(var1.getMxt() - this.cacheEntry.xform.getMxt());
         float var3 = (float)(var1.getMyt() - this.cacheEntry.xform.getMyt());
         this.maskX = this.cacheEntry.texData.maskX + var2;
         this.maskY = this.cacheEntry.texData.maskY + var3;
      }

      CachingShapeRepState.MaskTexData copy() {
         CachingShapeRepState.MaskTexData var1 = new CachingShapeRepState.MaskTexData();
         var1.cacheEntry = this.cacheEntry;
         var1.maskTex = this.maskTex;
         var1.maskX = this.maskX;
         var1.maskY = this.maskY;
         var1.maskW = this.maskW;
         var1.maskH = this.maskH;
         return var1;
      }

      void copyInto(CachingShapeRepState.MaskTexData var1) {
         if (var1 == null) {
            throw new InternalError("MaskTexData must be non-null");
         } else {
            var1.cacheEntry = this.cacheEntry;
            var1.maskTex = this.maskTex;
            var1.maskX = this.maskX;
            var1.maskY = this.maskY;
            var1.maskW = this.maskW;
            var1.maskH = this.maskH;
         }
      }
   }
}
