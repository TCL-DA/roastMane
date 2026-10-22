package com.sun.javafx.sg.prism;

import com.sun.glass.ui.Screen;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.application.PlatformImpl;
import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.logging.PulseLogger;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.tk.Toolkit;
import com.sun.prism.BasicStroke;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.PrinterGraphics;
import com.sun.prism.RTTexture;
import com.sun.prism.Texture;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.paint.Paint;
import com.sun.scenario.effect.Offset;
import java.util.Collections;
import java.util.List;
import java.util.WeakHashMap;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderImage;
import javafx.scene.layout.BorderRepeat;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;

public class NGRegion extends NGGroup {
   private static final Affine2D SCRATCH_AFFINE = new Affine2D();
   private static final Rectangle TEMP_RECT = new Rectangle();
   private static WeakHashMap<Screen, RegionImageCache> imageCacheMap = new WeakHashMap<>();
   private static final int CACHE_SLICE_V = 1;
   private static final int CACHE_SLICE_H = 2;
   private Background background = Background.EMPTY;
   private Insets backgroundInsets = Insets.EMPTY;
   private Border border = Border.EMPTY;
   private List<CornerRadii> normalizedFillCorners;
   private List<CornerRadii> normalizedStrokeCorners;
   private Shape shape;
   private NGShape ngShape;
   private boolean scaleShape = true;
   private boolean centerShape = true;
   private boolean cacheShape = false;
   private float opaqueTop = Float.NaN;
   private float opaqueRight = Float.NaN;
   private float opaqueBottom = Float.NaN;
   private float opaqueLeft = Float.NaN;
   private float width;
   private float height;
   private int cacheMode;
   private Integer cacheKey;
   private static final Offset nopEffect = new Offset(0, 0, null);
   private EffectFilter nopEffectFilter;

   static Paint getPlatformPaint(javafx.scene.paint.Paint var0) {
      return (Paint)Toolkit.getPaintAccessor().getPlatformPaint(var0);
   }

   public void updateShape(Object var1, boolean var2, boolean var3, boolean var4) {
      this.ngShape = var1 == null ? null : NodeHelper.getPeer((javafx.scene.shape.Shape)var1);
      this.shape = var1 == null ? null : this.ngShape.getShape();
      this.scaleShape = var2;
      this.centerShape = var3;
      this.cacheShape = var4;
      this.invalidateOpaqueRegion();
      this.cacheKey = null;
      this.visualsChanged();
   }

   public void setSize(float var1, float var2) {
      this.width = var1;
      this.height = var2;
      this.invalidateOpaqueRegion();
      this.cacheKey = null;
      this.visualsChanged();
      if (this.background != null && this.background.isFillPercentageBased()) {
         this.backgroundInsets = null;
      }
   }

   public void imagesUpdated() {
      this.visualsChanged();
   }

   public void updateBorder(Border var1) {
      Border var2 = this.border;
      this.border = var1 == null ? Border.EMPTY : var1;
      if (!this.border.getOutsets().equals(var2.getOutsets())) {
         this.geometryChanged();
      } else {
         this.visualsChanged();
      }
   }

   public void updateStrokeCorners(List<CornerRadii> var1) {
      this.normalizedStrokeCorners = var1;
   }

   private CornerRadii getNormalizedStrokeRadii(int var1) {
      return this.normalizedStrokeCorners == null ? this.border.getStrokes().get(var1).getRadii() : this.normalizedStrokeCorners.get(var1);
   }

   public void updateBackground(Background var1) {
      Background var2 = this.background;
      this.background = var1 == null ? Background.EMPTY : var1;
      List var3 = this.background.getFills();
      this.cacheMode = 0;
      if (!PrismSettings.disableRegionCaching && !var3.isEmpty() && (this.shape == null || this.cacheShape)) {
         this.cacheMode = 3;
         int var4 = 0;

         for (int var5 = var3.size(); var4 < var5 && this.cacheMode != 0; var4++) {
            BackgroundFill var6 = (BackgroundFill)var3.get(var4);
            javafx.scene.paint.Paint var7 = var6.getFill();
            if (this.shape == null) {
               if (var7 instanceof LinearGradient) {
                  LinearGradient var8 = (LinearGradient)var7;
                  if (var8.getStartX() != var8.getEndX()) {
                     this.cacheMode &= -3;
                  }

                  if (var8.getStartY() != var8.getEndY()) {
                     this.cacheMode &= -2;
                  }
               } else if (!(var7 instanceof Color)) {
                  this.cacheMode = 0;
               }
            } else if (var7 instanceof ImagePattern) {
               this.cacheMode = 0;
            }
         }
      }

      this.backgroundInsets = null;
      this.cacheKey = null;
      if (!this.background.getOutsets().equals(var2.getOutsets())) {
         this.geometryChanged();
      } else {
         this.visualsChanged();
      }
   }

   public void updateFillCorners(List<CornerRadii> var1) {
      this.normalizedFillCorners = var1;
   }

   private CornerRadii getNormalizedFillRadii(int var1) {
      return this.normalizedFillCorners == null ? this.background.getFills().get(var1).getRadii() : this.normalizedFillCorners.get(var1);
   }

   public void setOpaqueInsets(float var1, float var2, float var3, float var4) {
      this.opaqueTop = var1;
      this.opaqueRight = var2;
      this.opaqueBottom = var3;
      this.opaqueLeft = var4;
      this.invalidateOpaqueRegion();
   }

   @Override
   public void clearDirtyTree() {
      super.clearDirtyTree();
      if (this.ngShape != null) {
         this.ngShape.clearDirtyTree();
      }
   }

   private RegionImageCache getImageCache(Graphics var1) {
      Screen var2 = var1.getAssociatedScreen();
      RegionImageCache var3 = imageCacheMap.get(var2);
      if (var3 != null) {
         RTTexture var4 = var3.getBackingStore();
         if (var4.isSurfaceLost()) {
            imageCacheMap.remove(var2);
            var3 = null;
         }
      }

      if (var3 == null) {
         var3 = new RegionImageCache(var1.getResourceFactory());
         imageCacheMap.put(var2, var3);
      }

      return var3;
   }

   private Integer getCacheKey(int var1, int var2) {
      if (this.cacheKey == null) {
         int var3 = 31 * var1;
         var3 = var3 * 37 + var2;
         var3 = var3 * 47 + this.background.hashCode();
         if (this.shape != null) {
            var3 = var3 * 73 + this.shape.hashCode();
         }

         this.cacheKey = var3;
      }

      return this.cacheKey;
   }

   @Override
   protected boolean supportsOpaqueRegions() {
      return true;
   }

   @Override
   protected boolean hasOpaqueRegion() {
      return super.hasOpaqueRegion()
         && !Float.isNaN(this.opaqueTop)
         && !Float.isNaN(this.opaqueRight)
         && !Float.isNaN(this.opaqueBottom)
         && !Float.isNaN(this.opaqueLeft);
   }

   @Override
   protected RectBounds computeOpaqueRegion(RectBounds var1) {
      return (RectBounds)var1.deriveWithNewBounds(this.opaqueLeft, this.opaqueTop, 0.0F, this.width - this.opaqueRight, this.height - this.opaqueBottom, 0.0F);
   }

   @Override
   protected NGNode.RenderRootResult computeRenderRoot(NodePath var1, RectBounds var2, int var3, BaseTransform var4, GeneralTransform3D var5) {
      NGNode.RenderRootResult var6 = super.computeRenderRoot(var1, var2, var3, var4, var5);
      if (var6 == NGNode.RenderRootResult.NO_RENDER_ROOT) {
         var6 = this.computeNodeRenderRoot(var1, var2, var3, var4, var5);
      }

      return var6;
   }

   @Override
   protected boolean hasVisuals() {
      return !this.border.isEmpty() || !this.background.isEmpty();
   }

   @Override
   protected boolean hasOverlappingContents() {
      return true;
   }

   @Override
   protected void renderContent(Graphics var1) {
      if (!var1.getTransformNoClone().is2D() && this.isContentBounds2D()) {
         assert this.getEffectFilter() == null;

         if (this.nopEffectFilter == null) {
            this.nopEffectFilter = new EffectFilter(nopEffect, this);
         }

         this.nopEffectFilter.render(var1);
      } else {
         if (this.shape != null) {
            this.renderAsShape(var1);
         } else if (this.width > 0.0F && this.height > 0.0F) {
            this.renderAsRectangle(var1);
         }

         super.renderContent(var1);
      }
   }

   private void renderAsShape(Graphics var1) {
      if (!this.background.isEmpty()) {
         Insets var2 = this.background.getOutsets();
         Shape var3 = this.resizeShape((float)(-var2.getTop()), (float)(-var2.getRight()), (float)(-var2.getBottom()), (float)(-var2.getLeft()));
         RectBounds var4 = var3.getBounds();
         int var5 = Math.round(var4.getWidth());
         int var6 = Math.round(var4.getHeight());
         RTTexture var7 = null;
         Rectangle var8 = null;
         if (this.cacheMode != 0 && var1.getTransformNoClone().isTranslateOrIdentity() && !(var1 instanceof PrinterGraphics)) {
            RegionImageCache var9 = this.getImageCache(var1);
            if (var9.isImageCachable(var5, var6)) {
               Integer var10 = this.getCacheKey(var5, var6);
               var8 = TEMP_RECT;
               var8.setBounds(0, 0, var5 + 1, var6 + 1);
               boolean var11 = var9.getImageLocation(var10, var8, this.background, this.shape, var1);
               if (!var8.isEmpty()) {
                  var7 = var9.getBackingStore();
               }

               if (var7 != null && var11) {
                  Graphics var12 = var7.createGraphics();
                  var12.translate((float)var8.x - var4.getMinX(), (float)var8.y - var4.getMinY());
                  this.renderBackgroundShape(var12);
                  if (PulseLogger.PULSE_LOGGING_ENABLED) {
                     PulseLogger.incrementCounter("Rendering region shape image to cache");
                  }
               }
            }
         }

         if (var7 != null) {
            float var22 = var4.getMinX();
            float var23 = var4.getMinY();
            float var24 = var4.getMaxX();
            float var25 = var4.getMaxY();
            float var13 = (float)var8.x;
            float var14 = (float)var8.y;
            float var15 = var13 + (float)var5;
            float var16 = var14 + (float)var6;
            var1.drawTexture(var7, var22, var23, var24, var25, var13, var14, var15, var16);
            if (PulseLogger.PULSE_LOGGING_ENABLED) {
               PulseLogger.incrementCounter("Cached region shape image used");
            }
         } else {
            this.renderBackgroundShape(var1);
         }
      }

      if (!this.border.isEmpty()) {
         List var17 = this.border.getStrokes();
         int var18 = 0;

         for (int var19 = var17.size(); var18 < var19; var18++) {
            BorderStroke var20 = (BorderStroke)var17.get(var18);
            this.setBorderStyle(var1, var20, -1.0, false);
            Insets var21 = var20.getInsets();
            var1.draw(this.resizeShape((float)var21.getTop(), (float)var21.getRight(), (float)var21.getBottom(), (float)var21.getLeft()));
         }
      }
   }

   private void renderBackgroundShape(Graphics var1) {
      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.incrementCounter("NGRegion renderBackgroundShape slow path");
         PulseLogger.addMessage("Slow shape path for " + this.getName());
      }

      List var2 = this.background.getFills();
      int var3 = 0;

      for (int var4 = var2.size(); var3 < var4; var3++) {
         BackgroundFill var5 = (BackgroundFill)var2.get(var3);
         Paint var6 = getPlatformPaint(var5.getFill());

         assert var6 != null;

         var1.setPaint(var6);
         Insets var7 = var5.getInsets();
         var1.fill(this.resizeShape((float)var7.getTop(), (float)var7.getRight(), (float)var7.getBottom(), (float)var7.getLeft()));
      }

      List var11 = this.background.getImages();
      int var12 = 0;

      for (int var13 = var11.size(); var12 < var13; var12++) {
         BackgroundImage var14 = (BackgroundImage)var11.get(var12);
         Image var15 = (Image)Toolkit.getImageAccessor().getPlatformImage(var14.getImage());
         if (var15 != null) {
            Shape var8 = this.resizeShape(0.0F, 0.0F, 0.0F, 0.0F);
            RectBounds var9 = var8.getBounds();
            com.sun.prism.paint.ImagePattern var10 = var14.getSize().isCover()
               ? new com.sun.prism.paint.ImagePattern(var15, var9.getMinX(), var9.getMinY(), var9.getWidth(), var9.getHeight(), false, false)
               : new com.sun.prism.paint.ImagePattern(var15, var9.getMinX(), var9.getMinY(), (float)var15.getWidth(), (float)var15.getHeight(), false, false);
            var1.setPaint(var10);
            var1.fill(var8);
         }
      }
   }

   private void renderAsRectangle(Graphics var1) {
      if (!this.background.isEmpty()) {
         this.renderBackgroundRectangle(var1);
      }

      if (!this.border.isEmpty()) {
         this.renderBorderRectangle(var1);
      }
   }

   private void renderBackgroundRectangle(Graphics var1) {
      if (this.backgroundInsets == null) {
         this.updateBackgroundInsets();
      }

      double var2 = this.backgroundInsets.getLeft() + 1.0;
      double var4 = this.backgroundInsets.getRight() + 1.0;
      double var6 = this.backgroundInsets.getTop() + 1.0;
      double var8 = this.backgroundInsets.getBottom() + 1.0;
      int var10 = this.roundUp((double)this.width);
      if ((this.cacheMode & 2) != 0) {
         var10 = Math.min(var10, (int)(var2 + var4));
      }

      int var11 = this.roundUp((double)this.height);
      if ((this.cacheMode & 1) != 0) {
         var11 = Math.min(var11, (int)(var6 + var8));
      }

      Insets var12 = this.background.getOutsets();
      int var13 = this.roundUp(var12.getTop());
      int var14 = this.roundUp(var12.getRight());
      int var15 = this.roundUp(var12.getBottom());
      int var16 = this.roundUp(var12.getLeft());
      int var17 = var16 + var10 + var14;
      int var18 = var13 + var11 + var15;
      boolean var19 = this.background.getFills().size() > 1
         && this.cacheMode != 0
         && var1.getTransformNoClone().isTranslateOrIdentity()
         && !(var1 instanceof PrinterGraphics);
      RTTexture var20 = null;
      Rectangle var21 = null;
      if (var19) {
         RegionImageCache var22 = this.getImageCache(var1);
         if (var22.isImageCachable(var17, var18)) {
            Integer var23 = this.getCacheKey(var17, var18);
            var21 = TEMP_RECT;
            var21.setBounds(0, 0, var17 + 1, var18 + 1);
            boolean var24 = var22.getImageLocation(var23, var21, this.background, this.shape, var1);
            if (!var21.isEmpty()) {
               var20 = var22.getBackingStore();
            }

            if (var20 != null && var24) {
               Graphics var25 = var20.createGraphics();
               var25.translate((float)(var21.x + var16), (float)(var21.y + var13));
               this.renderBackgroundRectanglesDirectly(var25, (float)var10, (float)var11);
               if (PulseLogger.PULSE_LOGGING_ENABLED) {
                  PulseLogger.incrementCounter("Rendering region background image to cache");
               }
            }
         }
      }

      if (var20 != null) {
         this.renderBackgroundRectangleFromCache(var1, var20, var21, var17, var18, var6, var4, var8, var2, var13, var14, var15, var16);
      } else {
         this.renderBackgroundRectanglesDirectly(var1, this.width, this.height);
      }

      List var47 = this.background.getImages();
      int var48 = 0;

      for (int var49 = var47.size(); var48 < var49; var48++) {
         BackgroundImage var50 = (BackgroundImage)var47.get(var48);
         Image var26 = (Image)Toolkit.getImageAccessor().getPlatformImage(var50.getImage());
         if (var26 != null) {
            int var27 = (int)var50.getImage().getWidth();
            int var28 = (int)var50.getImage().getHeight();
            int var29 = var26.getWidth();
            int var30 = var26.getHeight();
            if (var29 != 0 && var30 != 0) {
               BackgroundSize var31 = var50.getSize();
               if (var31.isCover()) {
                  float var32 = Math.max(this.width / (float)var29, this.height / (float)var30);
                  Texture var33 = var1.getResourceFactory().getCachedTexture(var26, Texture.WrapMode.CLAMP_TO_EDGE);
                  var1.drawTexture(var33, 0.0F, 0.0F, this.width, this.height, 0.0F, 0.0F, this.width / var32, this.height / var32);
                  var33.unlock();
               } else {
                  double var51 = var31.isWidthAsPercentage() ? var31.getWidth() * (double)this.width : var31.getWidth();
                  double var34 = var31.isHeightAsPercentage() ? var31.getHeight() * (double)this.height : var31.getHeight();
                  double var36;
                  double var38;
                  if (var31.isContain()) {
                     float var53 = this.width / (float)var27;
                     float var41 = this.height / (float)var28;
                     float var42 = Math.min(var53, var41);
                     var36 = Math.ceil((double)(var42 * (float)var27));
                     var38 = Math.ceil((double)(var42 * (float)var28));
                  } else if (var31.getWidth() >= 0.0 && var31.getHeight() >= 0.0) {
                     var36 = var51;
                     var38 = var34;
                  } else if (var51 >= 0.0) {
                     var36 = var51;
                     double var40 = var51 / (double)var27;
                     var38 = (double)var28 * var40;
                  } else if (var34 >= 0.0) {
                     var38 = var34;
                     double var52 = var34 / (double)var28;
                     var36 = (double)var27 * var52;
                  } else {
                     var36 = (double)var27;
                     var38 = (double)var28;
                  }

                  BackgroundPosition var54 = var50.getPosition();
                  double var55;
                  if (var54.getHorizontalSide() == Side.LEFT) {
                     double var45 = var54.getHorizontalPosition();
                     if (var54.isHorizontalAsPercentage()) {
                        var55 = var45 * (double)this.width - var45 * var36;
                     } else {
                        var55 = var45;
                     }
                  } else if (var54.isHorizontalAsPercentage()) {
                     double var56 = 1.0 - var54.getHorizontalPosition();
                     var55 = var56 * (double)this.width - var56 * var36;
                  } else {
                     var55 = (double)this.width - var36 - var54.getHorizontalPosition();
                  }

                  double var43;
                  if (var54.getVerticalSide() == Side.TOP) {
                     double var57 = var54.getVerticalPosition();
                     if (var54.isVerticalAsPercentage()) {
                        var43 = var57 * (double)this.height - var57 * var38;
                     } else {
                        var43 = var57;
                     }
                  } else if (var54.isVerticalAsPercentage()) {
                     double var58 = 1.0 - var54.getVerticalPosition();
                     var43 = var58 * (double)this.height - var58 * var38;
                  } else {
                     var43 = (double)this.height - var38 - var54.getVerticalPosition();
                  }

                  this.paintTiles(
                     var1,
                     var26,
                     var50.getRepeatX(),
                     var50.getRepeatY(),
                     var54.getHorizontalSide(),
                     var54.getVerticalSide(),
                     0.0F,
                     0.0F,
                     this.width,
                     this.height,
                     0,
                     0,
                     var29,
                     var30,
                     (float)var55,
                     (float)var43,
                     (float)var36,
                     (float)var38
                  );
               }
            }
         }
      }
   }

   private void renderBackgroundRectangleFromCache(
      Graphics var1,
      RTTexture var2,
      Rectangle var3,
      int var4,
      int var5,
      double var6,
      double var8,
      double var10,
      double var12,
      int var14,
      int var15,
      int var16,
      int var17
   ) {
      float var18 = (float)var17 + this.width + (float)var15;
      float var19 = (float)var14 + this.height + (float)var16;
      boolean var20 = (float)var4 == var18;
      boolean var21 = (float)var5 == var19;
      float var22 = (float)(-var17) - 0.49609375F;
      float var23 = (float)(-var14) - 0.49609375F;
      float var24 = this.width + (float)var15 + 0.49609375F;
      float var25 = this.height + (float)var16 + 0.49609375F;
      float var26 = (float)var3.x - 0.49609375F;
      float var27 = (float)var3.y - 0.49609375F;
      float var28 = (float)(var3.x + var4) + 0.49609375F;
      float var29 = (float)(var3.y + var5) + 0.49609375F;
      double var30 = var12;
      double var32 = var8;
      double var34 = var6;
      double var36 = var10;
      if (var12 + var8 > (double)this.width) {
         double var38 = (double)this.width / (var12 + var8);
         var30 = var12 * var38;
         var32 = var8 * var38;
      }

      if (var6 + var10 > (double)this.height) {
         double var50 = (double)this.height / (var6 + var10);
         var34 = var6 * var50;
         var36 = var10 * var50;
      }

      if (var20 && var21) {
         var1.drawTexture(var2, var22, var23, var24, var25, var26, var27, var28, var29);
      } else if (var21) {
         float var51 = 0.49609375F + (float)(var30 + (double)var17);
         float var39 = 0.49609375F + (float)(var32 + (double)var15);
         float var40 = var22 + var51;
         float var41 = var24 - var39;
         float var42 = var26 + var51;
         float var43 = var28 - var39;
         var1.drawTexture3SliceH(var2, var22, var23, var24, var25, var26, var27, var28, var29, var40, var41, var42, var43);
      } else if (var20) {
         float var52 = 0.49609375F + (float)(var34 + (double)var14);
         float var54 = 0.49609375F + (float)(var36 + (double)var16);
         float var56 = var23 + var52;
         float var58 = var25 - var54;
         float var60 = var27 + var52;
         float var62 = var29 - var54;
         var1.drawTexture3SliceV(var2, var22, var23, var24, var25, var26, var27, var28, var29, var56, var58, var60, var62);
      } else {
         float var53 = 0.49609375F + (float)(var30 + (double)var17);
         float var55 = 0.49609375F + (float)(var34 + (double)var14);
         float var57 = 0.49609375F + (float)(var32 + (double)var15);
         float var59 = 0.49609375F + (float)(var36 + (double)var16);
         float var61 = var22 + var53;
         float var63 = var24 - var57;
         float var44 = var26 + var53;
         float var45 = var28 - var57;
         float var46 = var23 + var55;
         float var47 = var25 - var59;
         float var48 = var27 + var55;
         float var49 = var29 - var59;
         var1.drawTexture9Slice(var2, var22, var23, var24, var25, var26, var27, var28, var29, var61, var46, var63, var47, var44, var48, var45, var49);
      }

      if (PulseLogger.PULSE_LOGGING_ENABLED) {
         PulseLogger.incrementCounter("Cached region background image used");
      }
   }

   private void renderBackgroundRectanglesDirectly(Graphics var1, float var2, float var3) {
      List var4 = this.background.getFills();
      int var5 = 0;

      for (int var6 = var4.size(); var5 < var6; var5++) {
         BackgroundFill var7 = (BackgroundFill)var4.get(var5);
         Insets var8 = var7.getInsets();
         float var9 = (float)var8.getTop();
         float var10 = (float)var8.getLeft();
         float var11 = (float)var8.getBottom();
         float var12 = (float)var8.getRight();
         float var13 = var2 - var10 - var12;
         float var14 = var3 - var9 - var11;
         if (var13 > 0.0F && var14 > 0.0F) {
            Paint var15 = getPlatformPaint(var7.getFill());
            var1.setPaint(var15);
            CornerRadii var16 = this.getNormalizedFillRadii(var5);
            if (var16.isUniform()
               && (
                  PlatformImpl.isCaspian()
                     || PlatformUtil.isEmbedded()
                     || PlatformUtil.isIOS()
                     || !(var16.getTopLeftHorizontalRadius() > 0.0)
                     || !(var16.getTopLeftHorizontalRadius() <= 4.0)
               )) {
               float var17 = (float)var16.getTopLeftHorizontalRadius();
               float var18 = (float)var16.getTopLeftVerticalRadius();
               if (var17 == 0.0F && var18 == 0.0F) {
                  var1.fillRect(var10, var9, var13, var14);
               } else {
                  float var19 = var17 + var17;
                  float var20 = var18 + var18;
                  if (var19 > var13) {
                     var19 = var13;
                  }

                  if (var20 > var14) {
                     var20 = var14;
                  }

                  var1.fillRoundRect(var10, var9, var13, var14, var19, var20);
               }
            } else {
               if (PulseLogger.PULSE_LOGGING_ENABLED) {
                  PulseLogger.incrementCounter("NGRegion renderBackgrounds slow path");
                  PulseLogger.addMessage("Slow background path for " + this.getName());
               }

               var1.fill(this.createPath(var2, var3, var9, var10, var11, var12, var16));
            }
         }
      }
   }

   private void renderBorderRectangle(Graphics var1) {
      List var2 = this.border.getImages();
      List var3 = var2.isEmpty() ? this.border.getStrokes() : Collections.emptyList();
      int var4 = 0;

      for (int var5 = var3.size(); var4 < var5; var4++) {
         BorderStroke var6 = (BorderStroke)var3.get(var4);
         BorderWidths var7 = var6.getWidths();
         CornerRadii var8 = this.getNormalizedStrokeRadii(var4);
         Insets var9 = var6.getInsets();
         javafx.scene.paint.Paint var10 = var6.getTopStroke();
         javafx.scene.paint.Paint var11 = var6.getRightStroke();
         javafx.scene.paint.Paint var12 = var6.getBottomStroke();
         javafx.scene.paint.Paint var13 = var6.getLeftStroke();
         float var14 = (float)var9.getTop();
         float var15 = (float)var9.getRight();
         float var16 = (float)var9.getBottom();
         float var17 = (float)var9.getLeft();
         float var18 = (float)(var7.isTopAsPercentage() ? (double)this.height * var7.getTop() : var7.getTop());
         float var19 = (float)(var7.isRightAsPercentage() ? (double)this.width * var7.getRight() : var7.getRight());
         float var20 = (float)(var7.isBottomAsPercentage() ? (double)this.height * var7.getBottom() : var7.getBottom());
         float var21 = (float)(var7.isLeftAsPercentage() ? (double)this.width * var7.getLeft() : var7.getLeft());
         BorderStrokeStyle var22 = var6.getTopStyle();
         BorderStrokeStyle var23 = var6.getRightStyle();
         BorderStrokeStyle var24 = var6.getBottomStyle();
         BorderStrokeStyle var25 = var6.getLeftStyle();
         StrokeType var26 = var22.getType();
         StrokeType var27 = var23.getType();
         StrokeType var28 = var24.getType();
         StrokeType var29 = var25.getType();
         float var30 = var14 + (var26 == StrokeType.OUTSIDE ? -var18 / 2.0F : (var26 == StrokeType.INSIDE ? var18 / 2.0F : 0.0F));
         float var31 = var17 + (var29 == StrokeType.OUTSIDE ? -var21 / 2.0F : (var29 == StrokeType.INSIDE ? var21 / 2.0F : 0.0F));
         float var32 = var16 + (var28 == StrokeType.OUTSIDE ? -var20 / 2.0F : (var28 == StrokeType.INSIDE ? var20 / 2.0F : 0.0F));
         float var33 = var15 + (var27 == StrokeType.OUTSIDE ? -var19 / 2.0F : (var27 == StrokeType.INSIDE ? var19 / 2.0F : 0.0F));
         float var34 = (float)var8.getTopLeftHorizontalRadius();
         if (var6.isStrokeUniform()) {
            if ((!(var10 instanceof Color) || ((Color)var10).getOpacity() != 0.0) && var22 != BorderStrokeStyle.NONE) {
               float var35 = this.width - var31 - var33;
               float var36 = this.height - var30 - var32;
               double var37 = 2.0 * var8.getTopLeftHorizontalRadius();
               double var39 = var37 * Math.PI;
               double var41 = var39 + 2.0 * ((double)var35 - var37) + 2.0 * ((double)var36 - var37);
               if (var35 >= 0.0F && var36 >= 0.0F) {
                  this.setBorderStyle(var1, var6, var41, true);
                  if (var8.isUniform() && var34 == 0.0F) {
                     var1.drawRect(var31, var30, var35, var36);
                  } else if (var8.isUniform()) {
                     float var43 = var34 + var34;
                     if (var43 > var35) {
                        var43 = var35;
                     }

                     if (var43 > var36) {
                        var43 = var36;
                     }

                     var1.drawRoundRect(var31, var30, var35, var36, var43, var43);
                  } else {
                     var1.draw(this.createPath(this.width, this.height, var30, var31, var32, var33, var8));
                  }
               }
            }
         } else if (var8.isUniform() && var34 == 0.0F) {
            if ((!(var10 instanceof Color) || ((Color)var10).getOpacity() != 0.0) && var22 != BorderStrokeStyle.NONE) {
               var1.setPaint(getPlatformPaint(var10));
               if (BorderStrokeStyle.SOLID == var22) {
                  var1.fillRect(var17, var14, this.width - var17 - var15, var18);
               } else {
                  var1.setStroke(this.createStroke(var22, (double)var18, (double)this.width, true));
                  var1.drawLine(var31, var30, this.width - var33, var30);
               }
            }

            if ((!(var11 instanceof Color) || ((Color)var11).getOpacity() != 0.0) && var23 != BorderStrokeStyle.NONE) {
               var1.setPaint(getPlatformPaint(var11));
               if (BorderStrokeStyle.SOLID == var23) {
                  var1.fillRect(this.width - var15 - var19, var14, var19, this.height - var14 - var16);
               } else {
                  var1.setStroke(this.createStroke(var23, (double)var19, (double)this.height, true));
                  var1.drawLine(this.width - var33, var30, this.width - var33, this.height - var32);
               }
            }

            if ((!(var12 instanceof Color) || ((Color)var12).getOpacity() != 0.0) && var24 != BorderStrokeStyle.NONE) {
               var1.setPaint(getPlatformPaint(var12));
               if (BorderStrokeStyle.SOLID == var24) {
                  var1.fillRect(var17, this.height - var16 - var20, this.width - var17 - var15, var20);
               } else {
                  var1.setStroke(this.createStroke(var24, (double)var20, (double)this.width, true));
                  var1.drawLine(var31, this.height - var32, this.width - var33, this.height - var32);
               }
            }

            if ((!(var13 instanceof Color) || ((Color)var13).getOpacity() != 0.0) && var25 != BorderStrokeStyle.NONE) {
               var1.setPaint(getPlatformPaint(var13));
               if (BorderStrokeStyle.SOLID == var25) {
                  var1.fillRect(var17, var14, var21, this.height - var14 - var16);
               } else {
                  var1.setStroke(this.createStroke(var25, (double)var21, (double)this.height, true));
                  var1.drawLine(var31, var30, var31, this.height - var32);
               }
            }
         } else {
            Path2D[] var78 = this.createPaths(var30, var31, var32, var33, var8);
            if (var22 != BorderStrokeStyle.NONE) {
               double var83 = var8.getTopLeftHorizontalRadius() + var8.getTopRightHorizontalRadius();
               double var38 = (double)this.width + var83 * -0.21460183660255172;
               var1.setStroke(this.createStroke(var22, (double)var18, var38, true));
               var1.setPaint(getPlatformPaint(var10));
               var1.draw(var78[0]);
            }

            if (var23 != BorderStrokeStyle.NONE) {
               double var84 = var8.getTopRightVerticalRadius() + var8.getBottomRightVerticalRadius();
               double var89 = (double)this.height + var84 * -0.21460183660255172;
               var1.setStroke(this.createStroke(var23, (double)var19, var89, true));
               var1.setPaint(getPlatformPaint(var11));
               var1.draw(var78[1]);
            }

            if (var24 != BorderStrokeStyle.NONE) {
               double var85 = var8.getBottomLeftHorizontalRadius() + var8.getBottomRightHorizontalRadius();
               double var90 = (double)this.width + var85 * -0.21460183660255172;
               var1.setStroke(this.createStroke(var24, (double)var20, var90, true));
               var1.setPaint(getPlatformPaint(var12));
               var1.draw(var78[2]);
            }

            if (var25 != BorderStrokeStyle.NONE) {
               double var86 = var8.getTopLeftVerticalRadius() + var8.getBottomLeftVerticalRadius();
               double var91 = (double)this.height + var86 * -0.21460183660255172;
               var1.setStroke(this.createStroke(var25, (double)var21, var91, true));
               var1.setPaint(getPlatformPaint(var13));
               var1.draw(var78[3]);
            }
         }
      }

      var4 = 0;

      for (int var45 = var2.size(); var4 < var45; var4++) {
         BorderImage var46 = (BorderImage)var2.get(var4);
         Image var47 = (Image)Toolkit.getImageAccessor().getPlatformImage(var46.getImage());
         if (var47 != null) {
            int var48 = var47.getWidth();
            int var49 = var47.getHeight();
            float var50 = var47.getPixelScale();
            BorderWidths var51 = var46.getWidths();
            Insets var52 = var46.getInsets();
            BorderWidths var53 = var46.getSlices();
            int var54 = (int)Math.round(var52.getTop());
            int var55 = (int)Math.round(var52.getRight());
            int var56 = (int)Math.round(var52.getBottom());
            int var57 = (int)Math.round(var52.getLeft());
            int var58 = this.widthSize(var51.isTopAsPercentage(), var51.getTop(), this.height);
            int var59 = this.widthSize(var51.isRightAsPercentage(), var51.getRight(), this.width);
            int var60 = this.widthSize(var51.isBottomAsPercentage(), var51.getBottom(), this.height);
            int var61 = this.widthSize(var51.isLeftAsPercentage(), var51.getLeft(), this.width);
            int var62 = this.sliceSize(var53.isTopAsPercentage(), var53.getTop(), (float)var49, var50);
            int var63 = this.sliceSize(var53.isRightAsPercentage(), var53.getRight(), (float)var48, var50);
            int var64 = this.sliceSize(var53.isBottomAsPercentage(), var53.getBottom(), (float)var49, var50);
            int var65 = this.sliceSize(var53.isLeftAsPercentage(), var53.getLeft(), (float)var48, var50);
            if (!((float)(var57 + var61 + var55 + var59) > this.width) && !((float)(var54 + var58 + var56 + var60) > this.height)) {
               int var66 = var57 + var61;
               int var67 = var54 + var58;
               int var68 = Math.round(this.width) - var55 - var59 - var66;
               int var69 = Math.round(this.height) - var56 - var60 - var67;
               int var70 = var68 + var66;
               int var71 = var69 + var67;
               int var72 = var48 - var65 - var63;
               int var73 = var49 - var62 - var64;
               this.paintTiles(
                  var1,
                  var47,
                  BorderRepeat.STRETCH,
                  BorderRepeat.STRETCH,
                  Side.LEFT,
                  Side.TOP,
                  (float)var57,
                  (float)var54,
                  (float)var61,
                  (float)var58,
                  0,
                  0,
                  var65,
                  var62,
                  0.0F,
                  0.0F,
                  (float)var61,
                  (float)var58
               );
               float var74 = var46.getRepeatX() == BorderRepeat.STRETCH ? (float)var68 : (float)(var62 > 0 ? var72 * var58 / var62 : 0);
               float var79 = (float)var58;
               this.paintTiles(
                  var1,
                  var47,
                  var46.getRepeatX(),
                  BorderRepeat.STRETCH,
                  Side.LEFT,
                  Side.TOP,
                  (float)var66,
                  (float)var54,
                  (float)var68,
                  (float)var58,
                  var65,
                  0,
                  var72,
                  var62,
                  ((float)var68 - var74) / 2.0F,
                  0.0F,
                  var74,
                  var79
               );
               this.paintTiles(
                  var1,
                  var47,
                  BorderRepeat.STRETCH,
                  BorderRepeat.STRETCH,
                  Side.LEFT,
                  Side.TOP,
                  (float)var70,
                  (float)var54,
                  (float)var59,
                  (float)var58,
                  var48 - var63,
                  0,
                  var63,
                  var62,
                  0.0F,
                  0.0F,
                  (float)var59,
                  (float)var58
               );
               var74 = (float)var61;
               var79 = var46.getRepeatY() == BorderRepeat.STRETCH ? (float)var69 : (float)(var65 > 0 ? var61 * var73 / var65 : 0);
               this.paintTiles(
                  var1,
                  var47,
                  BorderRepeat.STRETCH,
                  var46.getRepeatY(),
                  Side.LEFT,
                  Side.TOP,
                  (float)var57,
                  (float)var67,
                  (float)var61,
                  (float)var69,
                  0,
                  var62,
                  var65,
                  var73,
                  0.0F,
                  ((float)var69 - var79) / 2.0F,
                  var74,
                  var79
               );
               var74 = (float)var59;
               var79 = var46.getRepeatY() == BorderRepeat.STRETCH ? (float)var69 : (float)(var63 > 0 ? var59 * var73 / var63 : 0);
               this.paintTiles(
                  var1,
                  var47,
                  BorderRepeat.STRETCH,
                  var46.getRepeatY(),
                  Side.LEFT,
                  Side.TOP,
                  (float)var70,
                  (float)var67,
                  (float)var59,
                  (float)var69,
                  var48 - var63,
                  var62,
                  var63,
                  var73,
                  0.0F,
                  ((float)var69 - var79) / 2.0F,
                  var74,
                  var79
               );
               this.paintTiles(
                  var1,
                  var47,
                  BorderRepeat.STRETCH,
                  BorderRepeat.STRETCH,
                  Side.LEFT,
                  Side.TOP,
                  (float)var57,
                  (float)var71,
                  (float)var61,
                  (float)var60,
                  0,
                  var49 - var64,
                  var65,
                  var64,
                  0.0F,
                  0.0F,
                  (float)var61,
                  (float)var60
               );
               var74 = var46.getRepeatX() == BorderRepeat.STRETCH ? (float)var68 : (float)(var64 > 0 ? var72 * var60 / var64 : 0);
               var79 = (float)var60;
               this.paintTiles(
                  var1,
                  var47,
                  var46.getRepeatX(),
                  BorderRepeat.STRETCH,
                  Side.LEFT,
                  Side.TOP,
                  (float)var66,
                  (float)var71,
                  (float)var68,
                  (float)var60,
                  var65,
                  var49 - var64,
                  var72,
                  var64,
                  ((float)var68 - var74) / 2.0F,
                  0.0F,
                  var74,
                  var79
               );
               this.paintTiles(
                  var1,
                  var47,
                  BorderRepeat.STRETCH,
                  BorderRepeat.STRETCH,
                  Side.LEFT,
                  Side.TOP,
                  (float)var70,
                  (float)var71,
                  (float)var59,
                  (float)var60,
                  var48 - var63,
                  var49 - var64,
                  var63,
                  var64,
                  0.0F,
                  0.0F,
                  (float)var59,
                  (float)var60
               );
               if (var46.isFilled()) {
                  float var87 = var46.getRepeatX() == BorderRepeat.STRETCH ? (float)var68 : (float)var72;
                  float var88 = var46.getRepeatY() == BorderRepeat.STRETCH ? (float)var69 : (float)var73;
                  this.paintTiles(
                     var1,
                     var47,
                     var46.getRepeatX(),
                     var46.getRepeatY(),
                     Side.LEFT,
                     Side.TOP,
                     (float)var66,
                     (float)var67,
                     (float)var68,
                     (float)var69,
                     var65,
                     var62,
                     var72,
                     var73,
                     0.0F,
                     0.0F,
                     var87,
                     var88
                  );
               }
            }
         }
      }
   }

   private void updateBackgroundInsets() {
      float var1 = 0.0F;
      float var2 = 0.0F;
      float var3 = 0.0F;
      float var4 = 0.0F;
      List var5 = this.background.getFills();
      int var6 = 0;

      for (int var7 = var5.size(); var6 < var7; var6++) {
         BackgroundFill var8 = (BackgroundFill)var5.get(var6);
         Insets var9 = var8.getInsets();
         CornerRadii var10 = this.getNormalizedFillRadii(var6);
         var1 = (float)Math.max((double)var1, var9.getTop() + Math.max(var10.getTopLeftVerticalRadius(), var10.getTopRightVerticalRadius()));
         var2 = (float)Math.max((double)var2, var9.getRight() + Math.max(var10.getTopRightHorizontalRadius(), var10.getBottomRightHorizontalRadius()));
         var3 = (float)Math.max((double)var3, var9.getBottom() + Math.max(var10.getBottomRightVerticalRadius(), var10.getBottomLeftVerticalRadius()));
         var4 = (float)Math.max((double)var4, var9.getLeft() + Math.max(var10.getTopLeftHorizontalRadius(), var10.getBottomLeftHorizontalRadius()));
      }

      this.backgroundInsets = new Insets(
         (double)this.roundUp((double)var1), (double)this.roundUp((double)var2), (double)this.roundUp((double)var3), (double)this.roundUp((double)var4)
      );
   }

   private int widthSize(boolean var1, double var2, float var4) {
      return (int)Math.round(var1 ? var2 * (double)var4 : var2);
   }

   private int sliceSize(boolean var1, double var2, float var4, float var5) {
      if (var1) {
         var2 *= (double)var4;
      }

      if (var2 > (double)var4) {
         var2 = (double)var4;
      }

      return (int)Math.round(var2 * (double)var5);
   }

   private int roundUp(double var1) {
      return var1 - (double)((int)var1) == 0.0 ? (int)var1 : (int)(var1 + 1.0);
   }

   private BasicStroke createStroke(BorderStrokeStyle var1, double var2, double var4, boolean var6) {
      byte var7;
      if (var1.getLineCap() == StrokeLineCap.BUTT) {
         var7 = 0;
      } else if (var1.getLineCap() == StrokeLineCap.SQUARE) {
         var7 = 2;
      } else {
         var7 = 1;
      }

      byte var8;
      if (var1.getLineJoin() == StrokeLineJoin.BEVEL) {
         var8 = 2;
      } else if (var1.getLineJoin() == StrokeLineJoin.MITER) {
         var8 = 0;
      } else {
         var8 = 1;
      }

      byte var9;
      if (var6) {
         var9 = 0;
      } else if (this.scaleShape) {
         var9 = 1;
      } else {
         switch (var1.getType()) {
            case INSIDE:
               var9 = 1;
               break;
            case OUTSIDE:
               var9 = 2;
               break;
            case CENTERED:
            default:
               var9 = 0;
         }
      }

      if (var1 == BorderStrokeStyle.NONE) {
         throw new AssertionError("Should never have been asked to draw a border with NONE");
      } else {
         BasicStroke var10;
         if (var2 <= 0.0) {
            var10 = new BasicStroke((float)var2, var7, var8, (float)var1.getMiterLimit());
         } else if (var1.getDashArray().size() > 0) {
            List var11 = var1.getDashArray();
            double[] var12;
            float var13;
            if (var11 == BorderStrokeStyle.DOTTED.getDashArray()) {
               if (var4 > 0.0) {
                  double var27 = var4 % (var2 * 2.0);
                  double var28 = var4 / (var2 * 2.0);
                  double var29 = var2 * 2.0 + var27 / var28;
                  var12 = new double[]{0.0, var29};
                  var13 = 0.0F;
               } else {
                  var12 = new double[]{0.0, var2 * 2.0};
                  var13 = 0.0F;
               }
            } else if (var11 == BorderStrokeStyle.DASHED.getDashArray()) {
               if (var4 > 0.0) {
                  double var26 = var2 * 2.0;
                  double var16 = var2 * 1.4;
                  double var18 = var26 + var16;
                  double var20 = var4 / var18;
                  double var22 = (double)((int)var20);
                  if (var22 > 0.0) {
                     double var24 = var22 * var26;
                     var16 = (var4 - var24) / var22;
                  }

                  var12 = new double[]{var26, var16};
                  var13 = (float)(var26 * 0.6);
               } else {
                  var12 = new double[]{2.0 * var2, 1.4 * var2};
                  var13 = 0.0F;
               }
            } else {
               var12 = new double[var11.size()];

               for (int var14 = 0; var14 < var12.length; var14++) {
                  var12[var14] = (Double)var11.get(var14);
               }

               var13 = (float)var1.getDashOffset();
            }

            var10 = new BasicStroke(var9, (float)var2, var7, var8, (float)var1.getMiterLimit(), var12, var13);
         } else {
            var10 = new BasicStroke(var9, (float)var2, var7, var8, (float)var1.getMiterLimit());
         }

         return var10;
      }
   }

   private void setBorderStyle(Graphics var1, BorderStroke var2, double var3, boolean var5) {
      BorderWidths var6 = var2.getWidths();
      BorderStrokeStyle var7 = var2.getTopStyle();
      double var8 = var6.isTopAsPercentage() ? (double)this.height * var6.getTop() : var6.getTop();
      Paint var10 = getPlatformPaint(var2.getTopStroke());
      if (var7 == null) {
         var7 = var2.getLeftStyle();
         var8 = var6.isLeftAsPercentage() ? (double)this.width * var6.getLeft() : var6.getLeft();
         var10 = getPlatformPaint(var2.getLeftStroke());
         if (var7 == null) {
            var7 = var2.getBottomStyle();
            var8 = var6.isBottomAsPercentage() ? (double)this.height * var6.getBottom() : var6.getBottom();
            var10 = getPlatformPaint(var2.getBottomStroke());
            if (var7 == null) {
               var7 = var2.getRightStyle();
               var8 = var6.isRightAsPercentage() ? (double)this.width * var6.getRight() : var6.getRight();
               var10 = getPlatformPaint(var2.getRightStroke());
            }
         }
      }

      if (var7 != null && var7 != BorderStrokeStyle.NONE) {
         var1.setStroke(this.createStroke(var7, var8, var3, var5));
         var1.setPaint(var10);
      }
   }

   private void doCorner(Path2D var1, CornerRadii var2, float var3, float var4, int var5, float var6, float var7, boolean var8) {
      float var9;
      float var10;
      float var11;
      float var12;
      float var13;
      float var14;
      switch (var5 & 3) {
         case 0:
            var13 = (float)var2.getTopLeftHorizontalRadius();
            var14 = (float)var2.getTopLeftVerticalRadius();
            var9 = 0.0F;
            var10 = var14;
            var11 = var13;
            var12 = 0.0F;
            break;
         case 1:
            var13 = (float)var2.getTopRightHorizontalRadius();
            var14 = (float)var2.getTopRightVerticalRadius();
            var9 = -var13;
            var10 = 0.0F;
            var11 = 0.0F;
            var12 = var14;
            break;
         case 2:
            var13 = (float)var2.getBottomRightHorizontalRadius();
            var14 = (float)var2.getBottomRightVerticalRadius();
            var9 = 0.0F;
            var10 = -var14;
            var11 = -var13;
            var12 = 0.0F;
            break;
         case 3:
            var13 = (float)var2.getBottomLeftHorizontalRadius();
            var14 = (float)var2.getBottomLeftVerticalRadius();
            var9 = var13;
            var10 = 0.0F;
            var11 = 0.0F;
            var12 = -var14;
            break;
         default:
            return;
      }

      if (var13 > 0.0F && var14 > 0.0F) {
         var1.appendOvalQuadrant(
            var3 + var9,
            var4 + var10,
            var3,
            var4,
            var3 + var11,
            var4 + var12,
            var6,
            var7,
            var8 ? Path2D.CornerPrefix.MOVE_THEN_CORNER : Path2D.CornerPrefix.LINE_THEN_CORNER
         );
      } else if (var8) {
         var1.moveTo(var3, var4);
      } else {
         var1.lineTo(var3, var4);
      }
   }

   private Path2D createPath(float var1, float var2, float var3, float var4, float var5, float var6, CornerRadii var7) {
      float var8 = var1 - var6;
      float var9 = var2 - var5;
      Path2D var10 = new Path2D();
      this.doCorner(var10, var7, var4, var3, 0, 0.0F, 1.0F, true);
      this.doCorner(var10, var7, var8, var3, 1, 0.0F, 1.0F, false);
      this.doCorner(var10, var7, var8, var9, 2, 0.0F, 1.0F, false);
      this.doCorner(var10, var7, var4, var9, 3, 0.0F, 1.0F, false);
      var10.closePath();
      return var10;
   }

   private Path2D makeRoundedEdge(CornerRadii var1, float var2, float var3, float var4, float var5, int var6) {
      Path2D var7 = new Path2D();
      this.doCorner(var7, var1, var2, var3, var6, 0.5F, 1.0F, true);
      this.doCorner(var7, var1, var4, var5, var6 + 1, 0.0F, 0.5F, false);
      return var7;
   }

   private Path2D[] createPaths(float var1, float var2, float var3, float var4, CornerRadii var5) {
      float var6 = this.width - var4;
      float var7 = this.height - var3;
      return new Path2D[]{
         this.makeRoundedEdge(var5, var2, var1, var6, var1, 0),
         this.makeRoundedEdge(var5, var6, var1, var6, var7, 1),
         this.makeRoundedEdge(var5, var6, var7, var2, var7, 2),
         this.makeRoundedEdge(var5, var2, var7, var2, var1, 3)
      };
   }

   private Shape resizeShape(float var1, float var2, float var3, float var4) {
      RectBounds var5 = this.shape.getBounds();
      if (this.scaleShape) {
         SCRATCH_AFFINE.setToIdentity();
         SCRATCH_AFFINE.translate((double)var4, (double)var1);
         float var11 = this.width - var4 - var2;
         float var13 = this.height - var1 - var3;
         SCRATCH_AFFINE.scale((double)(var11 / var5.getWidth()), (double)(var13 / var5.getHeight()));
         if (this.centerShape) {
            SCRATCH_AFFINE.translate((double)(-var5.getMinX()), (double)(-var5.getMinY()));
         }

         return SCRATCH_AFFINE.createTransformedShape(this.shape);
      } else if (!this.centerShape) {
         if (var1 == 0.0F && var2 == 0.0F && var3 == 0.0F && var4 == 0.0F) {
            return this.shape;
         } else {
            float var10 = var5.getWidth() - var4 - var2;
            float var12 = var5.getHeight() - var1 - var3;
            SCRATCH_AFFINE.setToIdentity();
            SCRATCH_AFFINE.translate((double)var4, (double)var1);
            SCRATCH_AFFINE.translate((double)var5.getMinX(), (double)var5.getMinY());
            SCRATCH_AFFINE.scale((double)(var10 / var5.getWidth()), (double)(var12 / var5.getHeight()));
            SCRATCH_AFFINE.translate((double)(-var5.getMinX()), (double)(-var5.getMinY()));
            return SCRATCH_AFFINE.createTransformedShape(this.shape);
         }
      } else {
         float var6 = var5.getWidth();
         float var7 = var5.getHeight();
         float var8 = var6 - var4 - var2;
         float var9 = var7 - var1 - var3;
         SCRATCH_AFFINE.setToIdentity();
         SCRATCH_AFFINE.translate((double)(var4 + (this.width - var6) / 2.0F - var5.getMinX()), (double)(var1 + (this.height - var7) / 2.0F - var5.getMinY()));
         if (var9 != var7 || var8 != var6) {
            SCRATCH_AFFINE.translate((double)var5.getMinX(), (double)var5.getMinY());
            SCRATCH_AFFINE.scale((double)(var8 / var6), (double)(var9 / var7));
            SCRATCH_AFFINE.translate((double)(-var5.getMinX()), (double)(-var5.getMinY()));
         }

         return SCRATCH_AFFINE.createTransformedShape(this.shape);
      }
   }

   private void paintTiles(
      Graphics var1,
      Image var2,
      BorderRepeat var3,
      BorderRepeat var4,
      Side var5,
      Side var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11,
      int var12,
      int var13,
      int var14,
      float var15,
      float var16,
      float var17,
      float var18
   ) {
      BackgroundRepeat var19 = null;
      BackgroundRepeat var20 = null;
      switch (var3) {
         case REPEAT:
            var19 = BackgroundRepeat.REPEAT;
            break;
         case STRETCH:
            var19 = BackgroundRepeat.NO_REPEAT;
            break;
         case ROUND:
            var19 = BackgroundRepeat.ROUND;
            break;
         case SPACE:
            var19 = BackgroundRepeat.SPACE;
      }

      switch (var4) {
         case REPEAT:
            var20 = BackgroundRepeat.REPEAT;
            break;
         case STRETCH:
            var20 = BackgroundRepeat.NO_REPEAT;
            break;
         case ROUND:
            var20 = BackgroundRepeat.ROUND;
            break;
         case SPACE:
            var20 = BackgroundRepeat.SPACE;
      }

      this.paintTiles(var1, var2, var19, var20, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18);
   }

   private void paintTiles(
      Graphics var1,
      Image var2,
      BackgroundRepeat var3,
      BackgroundRepeat var4,
      Side var5,
      Side var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11,
      int var12,
      int var13,
      int var14,
      float var15,
      float var16,
      float var17,
      float var18
   ) {
      if (!(var9 <= 0.0F) && !(var10 <= 0.0F) && var13 > 0 && var14 > 0) {
         assert var11 >= 0 && var12 >= 0 && var13 > 0 && var14 > 0;

         if (var15 == 0.0F && var16 == 0.0F && var3 == BackgroundRepeat.REPEAT && var4 == BackgroundRepeat.REPEAT) {
            if (var11 != 0 || var12 != 0 || var13 != var2.getWidth() || var14 != var2.getHeight()) {
               var2 = var2.createSubImage(var11, var12, var13, var14);
            }

            var1.setPaint(new com.sun.prism.paint.ImagePattern(var2, 0.0F, 0.0F, var17, var18, false, false));
            var1.fillRect(var7, var8, var9, var10);
         } else {
            if (var3 == BackgroundRepeat.SPACE && var9 < var17 * 2.0F) {
               var3 = BackgroundRepeat.NO_REPEAT;
            }

            if (var4 == BackgroundRepeat.SPACE && var10 < var18 * 2.0F) {
               var4 = BackgroundRepeat.NO_REPEAT;
            }

            int var19;
            float var21;
            if (var3 == BackgroundRepeat.REPEAT) {
               float var23 = 0.0F;
               if (var15 != 0.0F) {
                  float var24 = var15 % var17;
                  var15 = var24 == 0.0F ? 0.0F : (var15 < 0.0F ? var24 : var24 - var17);
                  var23 = var15;
               }

               var19 = (int)Math.max(1.0, Math.ceil((double)((var9 - var23) / var17)));
               var21 = var5 == Side.RIGHT ? -var17 : var17;
            } else if (var3 == BackgroundRepeat.SPACE) {
               var15 = 0.0F;
               var19 = (int)(var9 / var17);
               float var43 = var9 % var17;
               var21 = var17 + var43 / (float)(var19 - 1);
            } else if (var3 == BackgroundRepeat.ROUND) {
               var15 = 0.0F;
               var19 = (int)(var9 / var17);
               var17 = var9 / (float)((int)(var9 / var17));
               var21 = var17;
            } else {
               var19 = 1;
               var21 = var5 == Side.RIGHT ? -var17 : var17;
            }

            int var20;
            float var22;
            if (var4 == BackgroundRepeat.REPEAT) {
               float var44 = 0.0F;
               if (var16 != 0.0F) {
                  float var47 = var16 % var18;
                  var16 = var47 == 0.0F ? 0.0F : (var16 < 0.0F ? var47 : var47 - var18);
                  var44 = var16;
               }

               var20 = (int)Math.max(1.0, Math.ceil((double)((var10 - var44) / var18)));
               var22 = var6 == Side.BOTTOM ? -var18 : var18;
            } else if (var4 == BackgroundRepeat.SPACE) {
               var16 = 0.0F;
               var20 = (int)(var10 / var18);
               float var45 = var10 % var18;
               var22 = var18 + var45 / (float)(var20 - 1);
            } else if (var4 == BackgroundRepeat.ROUND) {
               var16 = 0.0F;
               var20 = (int)(var10 / var18);
               var18 = var10 / (float)((int)(var10 / var18));
               var22 = var18;
            } else {
               var20 = 1;
               var22 = var6 == Side.BOTTOM ? -var18 : var18;
            }

            Texture var46 = var1.getResourceFactory().getCachedTexture(var2, Texture.WrapMode.CLAMP_TO_EDGE);
            int var48 = var11 + var13;
            int var25 = var12 + var14;
            float var26 = var7 + var9;
            float var27 = var8 + var10;
            float var28 = var8 + var16;

            for (int var29 = 0; var29 < var20; var29++) {
               float var30 = var28 + var18;
               float var31 = var7 + var15;

               for (int var32 = 0; var32 < var19; var32++) {
                  float var33 = var31 + var17;
                  boolean var34 = false;
                  float var35 = var31 < var7 ? var7 : var31;
                  float var36 = var28 < var8 ? var8 : var28;
                  if (var35 > var26 || var36 > var27) {
                     var34 = true;
                  }

                  float var37 = var33 > var26 ? var26 : var33;
                  float var38 = var30 > var27 ? var27 : var30;
                  if (var37 < var7 || var38 < var8) {
                     var34 = true;
                  }

                  if (!var34) {
                     float var39 = var31 < var7 ? (float)var11 + (float)var13 * (-var15 / var17) : (float)var11;
                     float var40 = var28 < var8 ? (float)var12 + (float)var14 * (-var16 / var18) : (float)var12;
                     float var41 = var33 > var26 ? (float)var48 - (float)var13 * ((var33 - var26) / var17) : (float)var48;
                     float var42 = var30 > var27 ? (float)var25 - (float)var14 * ((var30 - var27) / var18) : (float)var25;
                     var1.drawTexture(var46, var35, var36, var37, var38, var39, var40, var41, var42);
                  }

                  var31 += var21;
               }

               var28 += var22;
            }

            var46.unlock();
         }
      }
   }

   final Border getBorder() {
      return this.border;
   }

   final Background getBackground() {
      return this.background;
   }

   final float getWidth() {
      return this.width;
   }

   final float getHeight() {
      return this.height;
   }
}
