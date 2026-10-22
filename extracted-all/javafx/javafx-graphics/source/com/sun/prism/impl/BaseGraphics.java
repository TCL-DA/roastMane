package com.sun.prism.impl;

import com.sun.glass.ui.Screen;
import com.sun.javafx.geom.Ellipse2D;
import com.sun.javafx.geom.Line2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.RoundRectangle2D;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.Affine3D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NodePath;
import com.sun.prism.BasicStroke;
import com.sun.prism.CompositeMode;
import com.sun.prism.PixelFormat;
import com.sun.prism.RectShadowGraphics;
import com.sun.prism.RenderTarget;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Paint;

public abstract class BaseGraphics implements RectShadowGraphics {
   private static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0F, 2, 0, 10.0F);
   private static final Paint DEFAULT_PAINT = Color.WHITE;
   protected static final RoundRectangle2D scratchRRect = new RoundRectangle2D();
   protected static final Ellipse2D scratchEllipse = new Ellipse2D();
   protected static final Line2D scratchLine = new Line2D();
   protected static final BaseTransform IDENT = BaseTransform.IDENTITY_TRANSFORM;
   private final Affine3D transform3D = new Affine3D();
   private NGCamera camera = NGCamera.INSTANCE;
   private RectBounds devClipRect;
   private RectBounds finalClipRect;
   protected RectBounds nodeBounds = null;
   private Rectangle clipRect;
   private int clipRectIndex;
   private boolean hasPreCullingBits = false;
   private float extraAlpha = 1.0F;
   private CompositeMode compMode;
   private boolean antialiasedShape = true;
   private boolean depthBuffer = false;
   private boolean depthTest = false;
   protected Paint paint = DEFAULT_PAINT;
   protected BasicStroke stroke = DEFAULT_STROKE;
   protected boolean isSimpleTranslate = true;
   protected float transX;
   protected float transY;
   private final BaseContext context;
   private final RenderTarget renderTarget;
   private boolean state3D = false;
   private float pixelScaleX = 1.0F;
   private float pixelScaleY = 1.0F;
   private NodePath renderRoot;

   protected BaseGraphics(BaseContext var1, RenderTarget var2) {
      this.context = var1;
      this.renderTarget = var2;
      this.devClipRect = new RectBounds(0.0F, 0.0F, (float)var2.getContentWidth(), (float)var2.getContentHeight());
      this.finalClipRect = new RectBounds(this.devClipRect);
      this.compMode = CompositeMode.SRC_OVER;
      if (var1 != null) {
         var1.setRenderTarget(this);
      }
   }

   protected NGCamera getCamera() {
      return this.camera;
   }

   @Override
   public RenderTarget getRenderTarget() {
      return this.renderTarget;
   }

   @Override
   public void setState3D(boolean var1) {
      this.state3D = var1;
   }

   @Override
   public boolean isState3D() {
      return this.state3D;
   }

   @Override
   public Screen getAssociatedScreen() {
      return this.context.getAssociatedScreen();
   }

   @Override
   public ResourceFactory getResourceFactory() {
      return this.context.getResourceFactory();
   }

   @Override
   public BaseTransform getTransformNoClone() {
      return this.transform3D;
   }

   @Override
   public void setPerspectiveTransform(GeneralTransform3D var1) {
      this.context.setPerspectiveTransform(var1);
   }

   @Override
   public void setTransform(BaseTransform var1) {
      if (var1 == null) {
         this.transform3D.setToIdentity();
      } else {
         this.transform3D.setTransform(var1);
      }

      this.validateTransformAndPaint();
   }

   @Override
   public void setTransform(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.transform3D.setTransform(var1, var3, var5, var7, var9, var11);
      this.validateTransformAndPaint();
   }

   @Override
   public void setTransform3D(
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
      this.transform3D.setTransform(var1, var3, var5, var7, var9, var11, var13, var15, var17, var19, var21, var23);
      this.validateTransformAndPaint();
   }

   @Override
   public void transform(BaseTransform var1) {
      this.transform3D.concatenate(var1);
      this.validateTransformAndPaint();
   }

   @Override
   public void translate(float var1, float var2) {
      if (var1 != 0.0F || var2 != 0.0F) {
         this.transform3D.translate((double)var1, (double)var2);
         this.validateTransformAndPaint();
      }
   }

   @Override
   public void translate(float var1, float var2, float var3) {
      if (var1 != 0.0F || var2 != 0.0F || var3 != 0.0F) {
         this.transform3D.translate((double)var1, (double)var2, (double)var3);
         this.validateTransformAndPaint();
      }
   }

   @Override
   public void scale(float var1, float var2) {
      if (var1 != 1.0F || var2 != 1.0F) {
         this.transform3D.scale((double)var1, (double)var2);
         this.validateTransformAndPaint();
      }
   }

   @Override
   public void scale(float var1, float var2, float var3) {
      if (var1 != 1.0F || var2 != 1.0F || var3 != 1.0F) {
         this.transform3D.scale((double)var1, (double)var2, (double)var3);
         this.validateTransformAndPaint();
      }
   }

   @Override
   public void setClipRectIndex(int var1) {
      this.clipRectIndex = var1;
   }

   @Override
   public int getClipRectIndex() {
      return this.clipRectIndex;
   }

   @Override
   public void setHasPreCullingBits(boolean var1) {
      this.hasPreCullingBits = var1;
   }

   @Override
   public boolean hasPreCullingBits() {
      return this.hasPreCullingBits;
   }

   @Override
   public final void setRenderRoot(NodePath var1) {
      this.renderRoot = var1;
   }

   @Override
   public final NodePath getRenderRoot() {
      return this.renderRoot;
   }

   private void validateTransformAndPaint() {
      if (this.transform3D.isTranslateOrIdentity() && this.paint.getType() == Paint.Type.COLOR) {
         this.isSimpleTranslate = true;
         this.transX = (float)this.transform3D.getMxt();
         this.transY = (float)this.transform3D.getMyt();
      } else {
         this.isSimpleTranslate = false;
         this.transX = 0.0F;
         this.transY = 0.0F;
      }
   }

   @Override
   public NGCamera getCameraNoClone() {
      return this.camera;
   }

   @Override
   public void setDepthTest(boolean var1) {
      this.depthTest = var1;
   }

   @Override
   public boolean isDepthTest() {
      return this.depthTest;
   }

   @Override
   public void setDepthBuffer(boolean var1) {
      this.depthBuffer = var1;
   }

   @Override
   public boolean isDepthBuffer() {
      return this.depthBuffer;
   }

   @Override
   public boolean isAlphaTestShader() {
      return PrismSettings.forceAlphaTestShader || this.isDepthTest() && this.isDepthBuffer();
   }

   @Override
   public void setAntialiasedShape(boolean var1) {
      this.antialiasedShape = var1;
   }

   @Override
   public boolean isAntialiasedShape() {
      return this.antialiasedShape;
   }

   @Override
   public void setPixelScaleFactors(float var1, float var2) {
      this.pixelScaleX = var1;
      this.pixelScaleY = var2;
   }

   @Override
   public float getPixelScaleFactorX() {
      return this.pixelScaleX;
   }

   @Override
   public float getPixelScaleFactorY() {
      return this.pixelScaleY;
   }

   @Override
   public void setCamera(NGCamera var1) {
      this.camera = var1;
   }

   @Override
   public Rectangle getClipRect() {
      return this.clipRect != null ? new Rectangle(this.clipRect) : null;
   }

   @Override
   public Rectangle getClipRectNoClone() {
      return this.clipRect;
   }

   @Override
   public RectBounds getFinalClipNoClone() {
      return this.finalClipRect;
   }

   @Override
   public void setClipRect(Rectangle var1) {
      this.finalClipRect.setBounds(this.devClipRect);
      if (var1 == null) {
         this.clipRect = null;
      } else {
         this.clipRect = new Rectangle(var1);
         this.finalClipRect.intersectWith(var1);
      }
   }

   @Override
   public float getExtraAlpha() {
      return this.extraAlpha;
   }

   @Override
   public void setExtraAlpha(float var1) {
      this.extraAlpha = var1;
   }

   @Override
   public CompositeMode getCompositeMode() {
      return this.compMode;
   }

   @Override
   public void setCompositeMode(CompositeMode var1) {
      this.compMode = var1;
   }

   @Override
   public Paint getPaint() {
      return this.paint;
   }

   @Override
   public void setPaint(Paint var1) {
      this.paint = var1;
      this.validateTransformAndPaint();
   }

   @Override
   public BasicStroke getStroke() {
      return this.stroke;
   }

   @Override
   public void setStroke(BasicStroke var1) {
      this.stroke = var1;
   }

   @Override
   public void clear() {
      this.clear(Color.TRANSPARENT);
   }

   protected abstract void renderShape(Shape var1, BasicStroke var2, float var3, float var4, float var5, float var6);

   @Override
   public void fill(Shape var1) {
      float var2 = 0.0F;
      float var3 = 0.0F;
      float var4 = 0.0F;
      float var5 = 0.0F;
      if (this.paint.isProportional()) {
         if (this.nodeBounds != null) {
            var2 = this.nodeBounds.getMinX();
            var3 = this.nodeBounds.getMinY();
            var4 = this.nodeBounds.getWidth();
            var5 = this.nodeBounds.getHeight();
         } else {
            float[] var6 = new float[]{Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
            Shape.accumulate(var6, var1, BaseTransform.IDENTITY_TRANSFORM);
            var2 = var6[0];
            var3 = var6[1];
            var4 = var6[2] - var2;
            var5 = var6[3] - var3;
         }
      }

      this.renderShape(var1, null, var2, var3, var4, var5);
   }

   @Override
   public void draw(Shape var1) {
      float var2 = 0.0F;
      float var3 = 0.0F;
      float var4 = 0.0F;
      float var5 = 0.0F;
      if (this.paint.isProportional()) {
         if (this.nodeBounds != null) {
            var2 = this.nodeBounds.getMinX();
            var3 = this.nodeBounds.getMinY();
            var4 = this.nodeBounds.getWidth();
            var5 = this.nodeBounds.getHeight();
         } else {
            float[] var6 = new float[]{Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY};
            Shape.accumulate(var6, var1, BaseTransform.IDENTITY_TRANSFORM);
            var2 = var6[0];
            var3 = var6[1];
            var4 = var6[2] - var2;
            var5 = var6[3] - var3;
         }
      }

      this.renderShape(var1, this.stroke, var2, var3, var4, var5);
   }

   @Override
   public void drawTexture(Texture var1, float var2, float var3, float var4, float var5) {
      this.drawTexture(var1, var2, var3, var2 + var4, var3 + var5, 0.0F, 0.0F, var4, var5);
   }

   @Override
   public void drawTexture(Texture var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      BaseTransform var10 = this.isSimpleTranslate ? IDENT : this.getTransformNoClone();
      PixelFormat var11 = var1.getPixelFormat();
      if (var11 == PixelFormat.BYTE_ALPHA) {
         this.context.validatePaintOp(this, var10, var1, var2, var3, var4 - var2, var5 - var3);
      } else {
         this.context.validateTextureOp(this, var10, var1, var11);
      }

      if (this.isSimpleTranslate) {
         var2 += this.transX;
         var3 += this.transY;
         var4 += this.transX;
         var5 += this.transY;
      }

      float var12 = (float)var1.getPhysicalWidth();
      float var13 = (float)var1.getPhysicalHeight();
      float var14 = (float)var1.getContentX();
      float var15 = (float)var1.getContentY();
      float var16 = (var14 + var6) / var12;
      float var17 = (var15 + var7) / var13;
      float var18 = (var14 + var8) / var12;
      float var19 = (var15 + var9) / var13;
      VertexBuffer var20 = this.context.getVertexBuffer();
      if (this.context.isSuperShaderEnabled()) {
         var20.addSuperQuad(var2, var3, var4, var5, var16, var17, var18, var19, false);
      } else {
         var20.addQuad(var2, var3, var4, var5, var16, var17, var18, var19);
      }
   }

   @Override
   public void drawTexture3SliceH(
      Texture var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13
   ) {
      BaseTransform var14 = this.isSimpleTranslate ? IDENT : this.getTransformNoClone();
      PixelFormat var15 = var1.getPixelFormat();
      if (var15 == PixelFormat.BYTE_ALPHA) {
         this.context.validatePaintOp(this, var14, var1, var2, var3, var4 - var2, var5 - var3);
      } else {
         this.context.validateTextureOp(this, var14, var1, var15);
      }

      if (this.isSimpleTranslate) {
         var2 += this.transX;
         var3 += this.transY;
         var4 += this.transX;
         var5 += this.transY;
         var10 += this.transX;
         var11 += this.transX;
      }

      float var16 = (float)var1.getPhysicalWidth();
      float var17 = (float)var1.getPhysicalHeight();
      float var18 = (float)var1.getContentX();
      float var19 = (float)var1.getContentY();
      float var20 = (var18 + var6) / var16;
      float var21 = (var19 + var7) / var17;
      float var22 = (var18 + var8) / var16;
      float var23 = (var19 + var9) / var17;
      float var24 = (var18 + var12) / var16;
      float var25 = (var18 + var13) / var16;
      VertexBuffer var26 = this.context.getVertexBuffer();
      if (this.context.isSuperShaderEnabled()) {
         var26.addSuperQuad(var2, var3, var10, var5, var20, var21, var24, var23, false);
         var26.addSuperQuad(var10, var3, var11, var5, var24, var21, var25, var23, false);
         var26.addSuperQuad(var11, var3, var4, var5, var25, var21, var22, var23, false);
      } else {
         var26.addQuad(var2, var3, var10, var5, var20, var21, var24, var23);
         var26.addQuad(var10, var3, var11, var5, var24, var21, var25, var23);
         var26.addQuad(var11, var3, var4, var5, var25, var21, var22, var23);
      }
   }

   @Override
   public void drawTexture3SliceV(
      Texture var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13
   ) {
      BaseTransform var14 = this.isSimpleTranslate ? IDENT : this.getTransformNoClone();
      PixelFormat var15 = var1.getPixelFormat();
      if (var15 == PixelFormat.BYTE_ALPHA) {
         this.context.validatePaintOp(this, var14, var1, var2, var3, var4 - var2, var5 - var3);
      } else {
         this.context.validateTextureOp(this, var14, var1, var15);
      }

      if (this.isSimpleTranslate) {
         var2 += this.transX;
         var3 += this.transY;
         var4 += this.transX;
         var5 += this.transY;
         var10 += this.transY;
         var11 += this.transY;
      }

      float var16 = (float)var1.getPhysicalWidth();
      float var17 = (float)var1.getPhysicalHeight();
      float var18 = (float)var1.getContentX();
      float var19 = (float)var1.getContentY();
      float var20 = (var18 + var6) / var16;
      float var21 = (var19 + var7) / var17;
      float var22 = (var18 + var8) / var16;
      float var23 = (var19 + var9) / var17;
      float var24 = (var19 + var12) / var17;
      float var25 = (var19 + var13) / var17;
      VertexBuffer var26 = this.context.getVertexBuffer();
      if (this.context.isSuperShaderEnabled()) {
         var26.addSuperQuad(var2, var3, var4, var10, var20, var21, var22, var24, false);
         var26.addSuperQuad(var2, var10, var4, var11, var20, var24, var22, var25, false);
         var26.addSuperQuad(var2, var11, var4, var5, var20, var25, var22, var23, false);
      } else {
         var26.addQuad(var2, var3, var4, var10, var20, var21, var22, var24);
         var26.addQuad(var2, var10, var4, var11, var20, var24, var22, var25);
         var26.addQuad(var2, var11, var4, var5, var20, var25, var22, var23);
      }
   }

   @Override
   public void drawTexture9Slice(
      Texture var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float var15,
      float var16,
      float var17
   ) {
      BaseTransform var18 = this.isSimpleTranslate ? IDENT : this.getTransformNoClone();
      PixelFormat var19 = var1.getPixelFormat();
      if (var19 == PixelFormat.BYTE_ALPHA) {
         this.context.validatePaintOp(this, var18, var1, var2, var3, var4 - var2, var5 - var3);
      } else {
         this.context.validateTextureOp(this, var18, var1, var19);
      }

      if (this.isSimpleTranslate) {
         var2 += this.transX;
         var3 += this.transY;
         var4 += this.transX;
         var5 += this.transY;
         var10 += this.transX;
         var11 += this.transY;
         var12 += this.transX;
         var13 += this.transY;
      }

      float var20 = (float)var1.getPhysicalWidth();
      float var21 = (float)var1.getPhysicalHeight();
      float var22 = (float)var1.getContentX();
      float var23 = (float)var1.getContentY();
      float var24 = (var22 + var6) / var20;
      float var25 = (var23 + var7) / var21;
      float var26 = (var22 + var8) / var20;
      float var27 = (var23 + var9) / var21;
      float var28 = (var22 + var14) / var20;
      float var29 = (var23 + var15) / var21;
      float var30 = (var22 + var16) / var20;
      float var31 = (var23 + var17) / var21;
      VertexBuffer var32 = this.context.getVertexBuffer();
      if (this.context.isSuperShaderEnabled()) {
         var32.addSuperQuad(var2, var3, var10, var11, var24, var25, var28, var29, false);
         var32.addSuperQuad(var10, var3, var12, var11, var28, var25, var30, var29, false);
         var32.addSuperQuad(var12, var3, var4, var11, var30, var25, var26, var29, false);
         var32.addSuperQuad(var2, var11, var10, var13, var24, var29, var28, var31, false);
         var32.addSuperQuad(var10, var11, var12, var13, var28, var29, var30, var31, false);
         var32.addSuperQuad(var12, var11, var4, var13, var30, var29, var26, var31, false);
         var32.addSuperQuad(var2, var13, var10, var5, var24, var31, var28, var27, false);
         var32.addSuperQuad(var10, var13, var12, var5, var28, var31, var30, var27, false);
         var32.addSuperQuad(var12, var13, var4, var5, var30, var31, var26, var27, false);
      } else {
         var32.addQuad(var2, var3, var10, var11, var24, var25, var28, var29);
         var32.addQuad(var10, var3, var12, var11, var28, var25, var30, var29);
         var32.addQuad(var12, var3, var4, var11, var30, var25, var26, var29);
         var32.addQuad(var2, var11, var10, var13, var24, var29, var28, var31);
         var32.addQuad(var10, var11, var12, var13, var28, var29, var30, var31);
         var32.addQuad(var12, var11, var4, var13, var30, var29, var26, var31);
         var32.addQuad(var2, var13, var10, var5, var24, var31, var28, var27);
         var32.addQuad(var10, var13, var12, var5, var28, var31, var30, var27);
         var32.addQuad(var12, var13, var4, var5, var30, var31, var26, var27);
      }
   }

   @Override
   public void drawTextureVO(
      Texture var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11
   ) {
      BaseTransform var12 = this.isSimpleTranslate ? IDENT : this.getTransformNoClone();
      PixelFormat var13 = var1.getPixelFormat();
      if (var13 == PixelFormat.BYTE_ALPHA) {
         this.context.validatePaintOp(this, var12, var1, var4, var5, var6 - var4, var7 - var5);
      } else {
         this.context.validateTextureOp(this, var12, var1, var13);
      }

      if (this.isSimpleTranslate) {
         var4 += this.transX;
         var5 += this.transY;
         var6 += this.transX;
         var7 += this.transY;
      }

      float var14 = (float)var1.getPhysicalWidth();
      float var15 = (float)var1.getPhysicalHeight();
      float var16 = (float)var1.getContentX();
      float var17 = (float)var1.getContentY();
      float var18 = (var16 + var8) / var14;
      float var19 = (var17 + var9) / var15;
      float var20 = (var16 + var10) / var14;
      float var21 = (var17 + var11) / var15;
      VertexBuffer var22 = this.context.getVertexBuffer();
      if (var2 == 1.0F && var3 == 1.0F) {
         var22.addQuad(var4, var5, var6, var7, var18, var19, var20, var21);
      } else {
         var2 *= this.getExtraAlpha();
         var3 *= this.getExtraAlpha();
         var22.addQuadVO(var2, var3, var4, var5, var6, var7, var18, var19, var20, var21);
      }
   }

   @Override
   public void drawTextureRaw(Texture var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      float var10 = var2;
      float var11 = var3;
      float var12 = var4 - var2;
      float var13 = var5 - var3;
      BaseTransform var14 = this.getTransformNoClone();
      if (this.isSimpleTranslate) {
         var14 = IDENT;
         var2 += this.transX;
         var3 += this.transY;
         var4 += this.transX;
         var5 += this.transY;
      }

      PixelFormat var15 = var1.getPixelFormat();
      if (var15 == PixelFormat.BYTE_ALPHA) {
         this.context.validatePaintOp(this, var14, var1, var10, var11, var12, var13);
      } else {
         this.context.validateTextureOp(this, var14, var1, var15);
      }

      VertexBuffer var16 = this.context.getVertexBuffer();
      var16.addQuad(var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void drawMappedTextureRaw(
      Texture var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      float var12,
      float var13
   ) {
      float var14 = var2;
      float var15 = var3;
      float var16 = var4 - var2;
      float var17 = var5 - var3;
      BaseTransform var18 = this.getTransformNoClone();
      if (this.isSimpleTranslate) {
         var18 = IDENT;
         var2 += this.transX;
         var3 += this.transY;
         var4 += this.transX;
         var5 += this.transY;
      }

      PixelFormat var19 = var1.getPixelFormat();
      if (var19 == PixelFormat.BYTE_ALPHA) {
         this.context.validatePaintOp(this, var18, var1, var14, var15, var16, var17);
      } else {
         this.context.validateTextureOp(this, var18, var1, var19);
      }

      VertexBuffer var20 = this.context.getVertexBuffer();
      var20.addMappedQuad(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }
}
