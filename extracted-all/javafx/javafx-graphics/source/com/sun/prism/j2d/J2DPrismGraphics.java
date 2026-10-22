package com.sun.prism.j2d;

import com.sun.glass.ui.Screen;
import com.sun.javafx.PlatformUtil;
import com.sun.javafx.font.CompositeStrike;
import com.sun.javafx.font.FontResource;
import com.sun.javafx.font.FontStrike;
import com.sun.javafx.font.Metrics;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.transform.Affine2D;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.geom.transform.GeneralTransform3D;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.sg.prism.NGCamera;
import com.sun.javafx.sg.prism.NGLightBase;
import com.sun.javafx.sg.prism.NodePath;
import com.sun.prism.BasicStroke;
import com.sun.prism.CompositeMode;
import com.sun.prism.MaskTextureGraphics;
import com.sun.prism.RTTexture;
import com.sun.prism.ReadbackGraphics;
import com.sun.prism.RenderTarget;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.j2d.paint.MultipleGradientPaint;
import com.sun.prism.j2d.paint.RadialGradientPaint;
import com.sun.prism.paint.Color;
import com.sun.prism.paint.Gradient;
import com.sun.prism.paint.ImagePattern;
import com.sun.prism.paint.LinearGradient;
import com.sun.prism.paint.Paint;
import com.sun.prism.paint.RadialGradient;
import com.sun.prism.paint.Stop;
import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.LinearGradientPaint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.geom.Path2D.Float;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class J2DPrismGraphics implements ReadbackGraphics, MaskTextureGraphics {
   static final CycleMethod[] LGP_CYCLE_METHODS = new CycleMethod[]{CycleMethod.NO_CYCLE, CycleMethod.REFLECT, CycleMethod.REPEAT};
   static final MultipleGradientPaint.CycleMethod[] RGP_CYCLE_METHODS = new MultipleGradientPaint.CycleMethod[]{
      MultipleGradientPaint.CycleMethod.NO_CYCLE, MultipleGradientPaint.CycleMethod.REFLECT, MultipleGradientPaint.CycleMethod.REPEAT
   };
   private static final BasicStroke DEFAULT_STROKE = new BasicStroke(1.0F, 2, 0, 10.0F);
   private static final Paint DEFAULT_PAINT = Color.WHITE;
   static AffineTransform J2D_IDENTITY = new AffineTransform();
   private int clipRectIndex;
   private boolean hasPreCullingBits = false;
   private float pixelScaleX = 1.0F;
   private float pixelScaleY = 1.0F;
   private static ConcurrentHashMap<Font, WeakReference<Font>> fontMap = new ConcurrentHashMap<>();
   private static volatile int cleared = 0;
   private static AffineTransform tmpAT = new AffineTransform();
   private static Path2D tmpQuadShape = new Float();
   private static java.awt.geom.Rectangle2D.Float tmpRect = new java.awt.geom.Rectangle2D.Float();
   private static Ellipse2D tmpEllipse = new java.awt.geom.Ellipse2D.Float();
   private static RoundRectangle2D tmpRRect = new java.awt.geom.RoundRectangle2D.Float();
   private static Line2D tmpLine = new java.awt.geom.Line2D.Float();
   private static J2DPrismGraphics.AdaptorShape tmpAdaptor = new J2DPrismGraphics.AdaptorShape();
   private boolean antialiasedShape = true;
   J2DPresentable target;
   Graphics2D g2d;
   Affine2D transform;
   Rectangle clipRect;
   RectBounds devClipRect;
   RectBounds finalClipRect;
   Paint paint;
   boolean paintWasProportional;
   BasicStroke stroke;
   boolean cull;
   Rectangle2D nodeBounds = null;
   private NodePath renderRoot;

   static java.awt.Color toJ2DColor(Color var0) {
      return new java.awt.Color(var0.getRed(), var0.getGreen(), var0.getBlue(), var0.getAlpha());
   }

   static int fixFractions(float[] var0, java.awt.Color[] var1) {
      float var2 = var0[0];
      int var3 = 1;
      int var4 = 1;

      while (var3 < var0.length) {
         float var5 = var0[var3];
         java.awt.Color var6 = var1[var3++];
         if (var5 <= var2) {
            if (var5 >= 1.0F) {
               break;
            }

            var5 = var2 + Math.ulp(var2);

            while (var3 < var0.length && !(var0[var3] > var5)) {
               var6 = var1[var3++];
            }
         }

         var2 = var5;
         var0[var4] = var5;
         var1[var4++] = var6;
      }

      return var4;
   }

   java.awt.Paint toJ2DPaint(Paint var1, Rectangle2D var2) {
      if (var1 instanceof Color) {
         return toJ2DColor((Color)var1);
      } else {
         if (var1 instanceof Gradient) {
            Gradient var3 = (Gradient)var1;
            if (var3.isProportional() && var2 == null) {
               return null;
            }

            List var4 = var3.getStops();
            int var5 = var4.size();
            float[] var6 = new float[var5];
            java.awt.Color[] var7 = new java.awt.Color[var5];
            float var8 = -1.0F;
            boolean var9 = false;

            for (int var10 = 0; var10 < var5; var10++) {
               Stop var11 = (Stop)var4.get(var10);
               float var12 = var11.getOffset();
               var9 = var9 || var12 <= var8;
               var8 = var12;
               var6[var10] = var12;
               var7[var10] = toJ2DColor(var11.getColor());
            }

            if (var9) {
               var5 = fixFractions(var6, var7);
               if (var5 < var6.length) {
                  float[] var39 = new float[var5];
                  System.arraycopy(var6, 0, var39, 0, var5);
                  var6 = var39;
                  java.awt.Color[] var43 = new java.awt.Color[var5];
                  System.arraycopy(var7, 0, var43, 0, var5);
                  var7 = var43;
               }
            }

            if (var3 instanceof LinearGradient) {
               LinearGradient var41 = (LinearGradient)var1;
               float var45 = var41.getX1();
               float var48 = var41.getY1();
               float var49 = var41.getX2();
               float var50 = var41.getY2();
               if (var3.isProportional()) {
                  float var15 = (float)var2.getX();
                  float var52 = (float)var2.getY();
                  float var54 = (float)var2.getWidth();
                  float var57 = (float)var2.getHeight();
                  var45 = var15 + var54 * var45;
                  var48 = var52 + var57 * var48;
                  var49 = var15 + var54 * var49;
                  var50 = var52 + var57 * var50;
               }

               if (var45 == var49 && var48 == var50) {
                  return var7[0];
               }

               java.awt.geom.Point2D.Float var51 = new java.awt.geom.Point2D.Float(var45, var48);
               java.awt.geom.Point2D.Float var53 = new java.awt.geom.Point2D.Float(var49, var50);
               CycleMethod var55 = LGP_CYCLE_METHODS[var3.getSpreadMethod()];
               return new LinearGradientPaint(var51, var53, var6, var7, var55);
            }

            if (var3 instanceof RadialGradient) {
               RadialGradient var40 = (RadialGradient)var3;
               float var44 = var40.getCenterX();
               float var47 = var40.getCenterY();
               float var13 = var40.getRadius();
               double var14 = Math.toRadians((double)var40.getFocusAngle());
               float var16 = var40.getFocusDistance();
               AffineTransform var17 = J2D_IDENTITY;
               if (var3.isProportional()) {
                  float var18 = (float)var2.getX();
                  float var19 = (float)var2.getY();
                  float var20 = (float)var2.getWidth();
                  float var21 = (float)var2.getHeight();
                  float var22 = Math.min(var20, var21);
                  float var23 = var18 + var20 * 0.5F;
                  float var24 = var19 + var21 * 0.5F;
                  var44 = var23 + (var44 - 0.5F) * var22;
                  var47 = var24 + (var47 - 0.5F) * var22;
                  var13 *= var22;
                  if (var20 != var21 && (double)var20 != 0.0 && (double)var21 != 0.0) {
                     var17 = AffineTransform.getTranslateInstance((double)var23, (double)var24);
                     var17.scale((double)(var20 / var22), (double)(var21 / var22));
                     var17.translate((double)(-var23), (double)(-var24));
                  }
               }

               java.awt.geom.Point2D.Float var56 = new java.awt.geom.Point2D.Float(var44, var47);
               float var58 = (float)((double)var44 + (double)(var16 * var13) * Math.cos(var14));
               float var59 = (float)((double)var47 + (double)(var16 * var13) * Math.sin(var14));
               java.awt.geom.Point2D.Float var60 = new java.awt.geom.Point2D.Float(var58, var59);
               MultipleGradientPaint.CycleMethod var61 = RGP_CYCLE_METHODS[var3.getSpreadMethod()];
               return new RadialGradientPaint(var56, var13, var60, var6, var7, var61, MultipleGradientPaint.ColorSpaceType.SRGB, var17);
            }
         } else if (var1 instanceof ImagePattern) {
            ImagePattern var25 = (ImagePattern)var1;
            float var26 = var25.getX();
            float var28 = var25.getY();
            float var29 = var25.getWidth();
            float var32 = var25.getHeight();
            if (var1.isProportional()) {
               if (var2 == null) {
                  return null;
               }

               float var35 = (float)var2.getX();
               float var37 = (float)var2.getY();
               float var42 = (float)var2.getWidth();
               float var46 = (float)var2.getHeight();
               var29 += var26;
               var32 += var28;
               var26 = var35 + var26 * var42;
               var28 = var37 + var28 * var46;
               var29 = var35 + var29 * var42;
               var32 = var37 + var32 * var46;
               var29 -= var26;
               var32 -= var28;
            }

            Texture var36 = this.getResourceFactory().getCachedTexture(var25.getImage(), Texture.WrapMode.REPEAT);
            BufferedImage var38 = ((J2DTexture)var36).getBufferedImage();
            var36.unlock();
            return new TexturePaint(var38, tmpRect(var26, var28, var29, var32));
         }

         throw new UnsupportedOperationException("Paint " + var1 + " not supported yet.");
      }
   }

   static Stroke toJ2DStroke(BasicStroke var0) {
      float var1 = var0.getLineWidth();
      int var2 = var0.getType();
      if (var2 != 0) {
         var1 *= 2.0F;
      }

      java.awt.BasicStroke var3 = new java.awt.BasicStroke(
         var1, var0.getEndCap(), var0.getLineJoin(), var0.getMiterLimit(), var0.getDashArray(), var0.getDashPhase()
      );
      if (var2 == 1) {
         return new J2DPrismGraphics.InnerStroke(var3);
      } else {
         return (Stroke)(var2 == 2 ? new J2DPrismGraphics.OuterStroke(var3) : var3);
      }
   }

   private static Font toJ2DFont(FontStrike var0) {
      FontResource var1 = var0.getFontResource();
      Object var3 = var1.getPeer();
      if (var3 == null && var1.isEmbeddedFont()) {
         J2DFontFactory.registerFont(var1);
         var3 = var1.getPeer();
      }

      Font var2;
      if (var3 != null && var3 instanceof Font) {
         var2 = (Font)var3;
      } else {
         if (PlatformUtil.isMac()) {
            String var4 = var1.getPSName();
            var2 = new Font(var4, 0, 12);
            if (!var2.getPSName().equals(var4)) {
               int var5 = var1.isBold() ? 1 : 0;
               var5 |= var1.isItalic() ? 2 : 0;
               var2 = new Font(var1.getFamilyName(), var5, 12);
               if (!var2.getPSName().equals(var4)) {
                  Font[] var6 = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();

                  for (Font var10 : var6) {
                     if (var10.getPSName().equals(var4)) {
                        var2 = var10;
                        break;
                     }
                  }
               }
            }
         } else {
            var2 = new Font(var1.getFullName(), 0, 12);
         }

         var1.setPeer(var2);
      }

      var2 = var2.deriveFont(var0.getSize());
      Font var12 = null;
      WeakReference var14 = fontMap.get(var2);
      if (var14 != null) {
         var12 = (Font)var14.get();
         if (var12 == null) {
            cleared++;
         }
      }

      if (var12 == null) {
         if (fontMap.size() > 100 && cleared > 10) {
            for (Font var18 : fontMap.keySet()) {
               var14 = fontMap.get(var18);
               if (var14 == null || var14.get() == null) {
                  fontMap.remove(var18);
               }
            }

            cleared = 0;
         }

         var12 = J2DFontFactory.getCompositeFont(var2);
         var14 = new WeakReference<>(var12);
         fontMap.put(var2, var14);
      }

      return var12;
   }

   public static AffineTransform toJ2DTransform(BaseTransform var0) {
      return new AffineTransform(var0.getMxx(), var0.getMyx(), var0.getMxy(), var0.getMyy(), var0.getMxt(), var0.getMyt());
   }

   static AffineTransform tmpJ2DTransform(BaseTransform var0) {
      tmpAT.setTransform(var0.getMxx(), var0.getMyx(), var0.getMxy(), var0.getMyy(), var0.getMxt(), var0.getMyt());
      return tmpAT;
   }

   static BaseTransform toPrTransform(AffineTransform var0) {
      return BaseTransform.getInstance(var0.getScaleX(), var0.getShearY(), var0.getShearX(), var0.getScaleY(), var0.getTranslateX(), var0.getTranslateY());
   }

   static Rectangle toPrRect(java.awt.Rectangle var0) {
      return new Rectangle(var0.x, var0.y, var0.width, var0.height);
   }

   private static Shape tmpQuad(float var0, float var1, float var2, float var3) {
      tmpQuadShape.reset();
      tmpQuadShape.moveTo((double)var0, (double)var1);
      tmpQuadShape.lineTo((double)var2, (double)var1);
      tmpQuadShape.lineTo((double)var2, (double)var3);
      tmpQuadShape.lineTo((double)var0, (double)var3);
      tmpQuadShape.closePath();
      return tmpQuadShape;
   }

   private static Rectangle2D tmpRect(float var0, float var1, float var2, float var3) {
      tmpRect.setRect(var0, var1, var2, var3);
      return tmpRect;
   }

   private static Shape tmpEllipse(float var0, float var1, float var2, float var3) {
      tmpEllipse.setFrame((double)var0, (double)var1, (double)var2, (double)var3);
      return tmpEllipse;
   }

   private static Shape tmpRRect(float var0, float var1, float var2, float var3, float var4, float var5) {
      tmpRRect.setRoundRect((double)var0, (double)var1, (double)var2, (double)var3, (double)var4, (double)var5);
      return tmpRRect;
   }

   private static Shape tmpLine(float var0, float var1, float var2, float var3) {
      tmpLine.setLine((double)var0, (double)var1, (double)var2, (double)var3);
      return tmpLine;
   }

   private static Shape tmpShape(com.sun.javafx.geom.Shape var0) {
      tmpAdaptor.setShape(var0);
      return tmpAdaptor;
   }

   J2DPrismGraphics(J2DPresentable var1, Graphics2D var2) {
      this(var2, var1.getContentWidth(), var1.getContentHeight());
      this.target = var1;
   }

   J2DPrismGraphics(Graphics2D var1, int var2, int var3) {
      this.g2d = var1;
      this.captureTransform(var1);
      this.transform = new Affine2D();
      this.devClipRect = new RectBounds(0.0F, 0.0F, (float)var2, (float)var3);
      this.finalClipRect = new RectBounds(0.0F, 0.0F, (float)var2, (float)var3);
      this.cull = true;
      var1.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
      var1.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      var1.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      var1.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      var1.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
      this.setTransform(BaseTransform.IDENTITY_TRANSFORM);
      this.setPaint(DEFAULT_PAINT);
      this.setStroke(DEFAULT_STROKE);
   }

   @Override
   public RenderTarget getRenderTarget() {
      return this.target;
   }

   @Override
   public Screen getAssociatedScreen() {
      return this.target.getAssociatedScreen();
   }

   @Override
   public ResourceFactory getResourceFactory() {
      return this.target.getResourceFactory();
   }

   public void reset() {
   }

   @Override
   public Rectangle getClipRect() {
      return this.clipRect == null ? null : new Rectangle(this.clipRect);
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
         this.g2d.setClip(null);
      } else {
         this.clipRect = new Rectangle(var1);
         this.finalClipRect.intersectWith(var1);
         this.setTransformG2D(J2D_IDENTITY);
         this.g2d.setClip(var1.x, var1.y, var1.width, var1.height);
         this.setTransformG2D(tmpJ2DTransform(this.transform));
      }
   }

   private AlphaComposite getAWTComposite() {
      return (AlphaComposite)this.g2d.getComposite();
   }

   @Override
   public float getExtraAlpha() {
      return this.getAWTComposite().getAlpha();
   }

   @Override
   public void setExtraAlpha(float var1) {
      this.g2d.setComposite(this.getAWTComposite().derive(var1));
   }

   @Override
   public CompositeMode getCompositeMode() {
      int var1 = this.getAWTComposite().getRule();
      switch (var1) {
         case 1:
            return CompositeMode.CLEAR;
         case 2:
            return CompositeMode.SRC;
         case 3:
            return CompositeMode.SRC_OVER;
         default:
            throw new InternalError("Unrecognized AlphaCompsite rule: " + var1);
      }
   }

   @Override
   public void setCompositeMode(CompositeMode var1) {
      AlphaComposite var2 = this.getAWTComposite();
      switch (var1) {
         case CLEAR:
            var2 = var2.derive(1);
            break;
         case SRC:
            var2 = var2.derive(2);
            break;
         case SRC_OVER:
            var2 = var2.derive(3);
            break;
         default:
            throw new InternalError("Unrecognized composite mode: " + var1);
      }

      this.g2d.setComposite(var2);
   }

   @Override
   public Paint getPaint() {
      return this.paint;
   }

   @Override
   public void setPaint(Paint var1) {
      this.paint = var1;
      java.awt.Paint var2 = this.toJ2DPaint(var1, null);
      if (var2 == null) {
         this.paintWasProportional = true;
      } else {
         this.paintWasProportional = false;
         this.g2d.setPaint(var2);
      }
   }

   @Override
   public BasicStroke getStroke() {
      return this.stroke;
   }

   @Override
   public void setStroke(BasicStroke var1) {
      this.stroke = var1;
      this.g2d.setStroke(toJ2DStroke(var1));
   }

   @Override
   public BaseTransform getTransformNoClone() {
      return this.transform;
   }

   @Override
   public void translate(float var1, float var2) {
      this.transform.translate((double)var1, (double)var2);
      this.g2d.translate((double)var1, (double)var2);
   }

   @Override
   public void scale(float var1, float var2) {
      this.transform.scale((double)var1, (double)var2);
      this.g2d.scale((double)var1, (double)var2);
   }

   @Override
   public void transform(BaseTransform var1) {
      if (var1.is2D()) {
         this.transform.concatenate(var1);
         this.setTransformG2D(tmpJ2DTransform(this.transform));
      }
   }

   @Override
   public void setTransform(BaseTransform var1) {
      if (var1 == null) {
         var1 = BaseTransform.IDENTITY_TRANSFORM;
      }

      this.transform.setTransform(var1);
      this.setTransformG2D(tmpJ2DTransform(this.transform));
   }

   @Override
   public void setTransform(double var1, double var3, double var5, double var7, double var9, double var11) {
      this.transform.setTransform(var1, var3, var5, var7, var9, var11);
      this.setTransformG2D(tmpJ2DTransform(this.transform));
   }

   @Override
   public void clear() {
      this.clear(Color.TRANSPARENT);
   }

   @Override
   public void clear(Color var1) {
      this.getRenderTarget().setOpaque(var1.isOpaque());
      this.clear(toJ2DColor(var1));
   }

   void clear(java.awt.Color var1) {
      Graphics2D var2 = (Graphics2D)this.g2d.create();
      var2.setTransform(J2D_IDENTITY);
      var2.setComposite(AlphaComposite.Src);
      var2.setColor(var1);
      var2.fillRect(0, 0, this.target.getContentWidth(), this.target.getContentHeight());
      var2.dispose();
   }

   @Override
   public void clearQuad(float var1, float var2, float var3, float var4) {
      this.g2d.setComposite(AlphaComposite.Clear);
      this.g2d.fill(tmpQuad(var1, var2, var3, var4));
   }

   void fill(Shape var1) {
      if (this.paintWasProportional) {
         if (this.nodeBounds != null) {
            this.g2d.setPaint(this.toJ2DPaint(this.paint, this.nodeBounds));
         } else {
            this.g2d.setPaint(this.toJ2DPaint(this.paint, var1.getBounds2D()));
         }
      }

      if (this.paint.getType() == Paint.Type.IMAGE_PATTERN) {
         ImagePattern var2 = (ImagePattern)this.paint;
         AffineTransform var3 = toJ2DTransform(var2.getPatternTransformNoClone());
         if (!var3.isIdentity()) {
            this.g2d.setClip(var1);
            this.g2d.transform(var3);
            tmpAT.setTransform(var3);

            try {
               tmpAT.invert();
               this.g2d.fill(tmpAT.createTransformedShape(var1));
            } catch (NoninvertibleTransformException var5) {
            }

            this.setTransform(this.transform);
            this.setClipRect(this.clipRect);
            return;
         }
      }

      this.g2d.fill(var1);
   }

   @Override
   public void fill(com.sun.javafx.geom.Shape var1) {
      this.fill(tmpShape(var1));
   }

   @Override
   public void fillRect(float var1, float var2, float var3, float var4) {
      this.fill(tmpRect(var1, var2, var3, var4));
   }

   @Override
   public void fillRoundRect(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.fill(tmpRRect(var1, var2, var3, var4, var5, var6));
   }

   @Override
   public void fillEllipse(float var1, float var2, float var3, float var4) {
      this.fill(tmpEllipse(var1, var2, var3, var4));
   }

   @Override
   public void fillQuad(float var1, float var2, float var3, float var4) {
      this.fill(tmpQuad(var1, var2, var3, var4));
   }

   void draw(Shape var1) {
      if (this.paintWasProportional) {
         if (this.nodeBounds != null) {
            this.g2d.setPaint(this.toJ2DPaint(this.paint, this.nodeBounds));
         } else {
            this.g2d.setPaint(this.toJ2DPaint(this.paint, var1.getBounds2D()));
         }
      }

      try {
         this.g2d.draw(var1);
      } catch (Throwable var3) {
      }
   }

   @Override
   public void draw(com.sun.javafx.geom.Shape var1) {
      this.draw(tmpShape(var1));
   }

   @Override
   public void drawLine(float var1, float var2, float var3, float var4) {
      this.draw(tmpLine(var1, var2, var3, var4));
   }

   @Override
   public void drawRect(float var1, float var2, float var3, float var4) {
      this.draw(tmpRect(var1, var2, var3, var4));
   }

   @Override
   public void drawRoundRect(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.draw(tmpRRect(var1, var2, var3, var4, var5, var6));
   }

   @Override
   public void drawEllipse(float var1, float var2, float var3, float var4) {
      this.draw(tmpEllipse(var1, var2, var3, var4));
   }

   @Override
   public void setNodeBounds(RectBounds var1) {
      this.nodeBounds = var1 != null ? new java.awt.geom.Rectangle2D.Float(var1.getMinX(), var1.getMinY(), var1.getWidth(), var1.getHeight()) : null;
   }

   private void drawString(GlyphList var1, int var2, int var3, FontStrike var4, float var5, float var6) {
      if (var2 != var3) {
         int var7 = var3 - var2;
         int[] var8 = new int[var7];

         for (int var9 = 0; var9 < var7; var9++) {
            var8[var9] = var1.getGlyphCode(var2 + var9) & 16777215;
         }

         Font var13 = toJ2DFont(var4);
         GlyphVector var10 = var13.createGlyphVector(this.g2d.getFontRenderContext(), var8);
         java.awt.geom.Point2D.Float var11 = new java.awt.geom.Point2D.Float();

         for (int var12 = 0; var12 < var7; var12++) {
            var11.setLocation((double)var1.getPosX(var2 + var12), (double)var1.getPosY(var2 + var12));
            var10.setGlyphPosition(var12, var11);
         }

         this.g2d.drawGlyphVector(var10, var5, var6);
      }
   }

   @Override
   public void drawString(GlyphList var1, FontStrike var2, float var3, float var4, Color var5, int var6, int var7) {
      int var8 = var1.getGlyphCount();
      if (var8 != 0) {
         this.g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
         if (var2.getAAMode() == 1) {
            this.g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
         }

         if (this.paintWasProportional) {
            Object var9 = this.nodeBounds;
            if (var9 == null) {
               Metrics var10 = var2.getMetrics();
               var9 = new java.awt.geom.Rectangle2D.Float(0.0F, var10.getAscent(), var1.getWidth(), var10.getLineHeight());
            }

            this.g2d.setPaint(this.toJ2DPaint(this.paint, (Rectangle2D)var9));
         }

         CompositeStrike var18 = null;
         int var19 = 0;
         if (var2 instanceof CompositeStrike) {
            var18 = (CompositeStrike)var2;
            int var11 = var1.getGlyphCode(0);
            var19 = var18.getStrikeSlotForGlyph(var11);
         }

         java.awt.Color var20 = null;
         java.awt.Color var12 = null;
         boolean var13 = false;
         if (var5 != null) {
            var20 = toJ2DColor(var5);
            var12 = this.g2d.getColor();
            int var14 = var1.getCharOffset(0);
            var13 = var6 <= var14 && var14 < var7;
         }

         int var21 = 0;
         if (var20 != null || var18 != null) {
            for (int var15 = 1; var15 < var8; var15++) {
               if (var20 != null) {
                  int var16 = var1.getCharOffset(var15);
                  boolean var17 = var6 <= var16 && var16 < var7;
                  if (var13 != var17) {
                     if (var18 != null) {
                        var2 = var18.getStrikeSlot(var19);
                     }

                     this.g2d.setColor(var13 ? var20 : var12);
                     this.drawString(var1, var21, var15, var2, var3, var4);
                     var21 = var15;
                     var13 = var17;
                  }
               }

               if (var18 != null) {
                  int var22 = var1.getGlyphCode(var15);
                  int var23 = var18.getStrikeSlotForGlyph(var22);
                  if (var19 != var23) {
                     var2 = var18.getStrikeSlot(var19);
                     if (var20 != null) {
                        this.g2d.setColor(var13 ? var20 : var12);
                     }

                     this.drawString(var1, var21, var15, var2, var3, var4);
                     var21 = var15;
                     var19 = var23;
                  }
               }
            }

            if (var18 != null) {
               var2 = var18.getStrikeSlot(var19);
            }

            if (var20 != null) {
               this.g2d.setColor(var13 ? var20 : var12);
            }
         }

         this.drawString(var1, var21, var8, var2, var3, var4);
         if (var5 != null) {
            this.g2d.setColor(var12);
         }

         this.g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
         this.g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }
   }

   protected void setTransformG2D(AffineTransform var1) {
      this.g2d.setTransform(var1);
   }

   protected void captureTransform(Graphics2D var1) {
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
      BufferedImage var14 = ((J2DTexture)var1).getBufferedImage();
      float var15 = var8 - var6;
      float var16 = var9 - var7;
      float var17 = var10 - var6;
      float var18 = var11 - var7;
      this.setTransformG2D(J2D_IDENTITY);
      tmpAT.setTransform((double)var15, (double)var16, (double)var17, (double)var18, (double)var6, (double)var7);

      try {
         tmpAT.invert();
         this.g2d.translate((double)var2, (double)var3);
         this.g2d.scale((double)(var4 - var2), (double)(var5 - var3));
         this.g2d.transform(tmpAT);
         this.g2d.drawImage(var14, 0, 0, 1, 1, null);
      } catch (NoninvertibleTransformException var20) {
      }

      this.setTransform(this.transform);
   }

   @Override
   public void drawTexture(Texture var1, float var2, float var3, float var4, float var5) {
      BufferedImage var6 = ((J2DTexture)var1).getBufferedImage();
      this.g2d.drawImage(var6, (int)var2, (int)var3, (int)(var2 + var4), (int)(var3 + var5), 0, 0, (int)var4, (int)var5, null);
   }

   @Override
   public void drawTexture(Texture var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      BufferedImage var10 = ((J2DTexture)var1).getBufferedImage();
      this.g2d.drawImage(var10, (int)var2, (int)var3, (int)var4, (int)var5, (int)var6, (int)var7, (int)var8, (int)var9, null);
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
      if (var12 + 0.1F > var13) {
         var13++;
      }

      this.drawTexture(var1, var2, var3, var10, var5, var6, var7, var12, var9);
      this.drawTexture(var1, var10, var3, var11, var5, var12, var7, var13, var9);
      this.drawTexture(var1, var11, var3, var4, var5, var13, var7, var8, var9);
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
      if (var12 + 0.1F > var13) {
         var13++;
      }

      this.drawTexture(var1, var2, var3, var4, var10, var6, var7, var8, var12);
      this.drawTexture(var1, var2, var10, var4, var11, var6, var12, var8, var13);
      this.drawTexture(var1, var2, var11, var4, var5, var6, var13, var8, var9);
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
      if (var14 + 0.1F > var16) {
         var16++;
      }

      if (var15 + 0.1F > var17) {
         var17++;
      }

      this.drawTexture(var1, var2, var3, var10, var11, var6, var7, var14, var15);
      this.drawTexture(var1, var10, var3, var12, var11, var14, var7, var16, var15);
      this.drawTexture(var1, var12, var3, var4, var11, var16, var7, var8, var15);
      this.drawTexture(var1, var2, var11, var10, var13, var6, var15, var14, var17);
      this.drawTexture(var1, var10, var11, var12, var13, var14, var15, var16, var17);
      this.drawTexture(var1, var12, var11, var4, var13, var16, var15, var8, var17);
      this.drawTexture(var1, var2, var13, var10, var5, var6, var17, var14, var9);
      this.drawTexture(var1, var10, var13, var12, var5, var14, var17, var16, var9);
      this.drawTexture(var1, var12, var13, var4, var5, var16, var17, var8, var9);
   }

   @Override
   public void drawTextureRaw(Texture var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      int var10 = var1.getContentWidth();
      int var11 = var1.getContentHeight();
      var6 *= (float)var10;
      var7 *= (float)var11;
      var8 *= (float)var10;
      var9 *= (float)var11;
      this.drawTexture(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   @Override
   public void drawTextureVO(
      Texture var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11
   ) {
      java.awt.Paint var12 = this.g2d.getPaint();
      Composite var13 = this.g2d.getComposite();
      java.awt.Color var14 = new java.awt.Color(1.0F, 1.0F, 1.0F, var2);
      java.awt.Color var15 = new java.awt.Color(1.0F, 1.0F, 1.0F, var3);
      this.g2d.setPaint(new GradientPaint(0.0F, var5, var14, 0.0F, var7, var15, true));
      this.g2d.setComposite(AlphaComposite.Src);
      int var16 = (int)Math.floor((double)Math.min(var4, var6));
      int var17 = (int)Math.floor((double)Math.min(var5, var7));
      int var18 = (int)Math.ceil((double)Math.max(var4, var6)) - var16;
      int var19 = (int)Math.ceil((double)Math.max(var5, var7)) - var17;
      this.g2d.fillRect(var16, var17, var18, var19);
      this.g2d.setComposite(AlphaComposite.SrcIn);
      this.drawTexture(var1, var4, var5, var6, var7, var8, var9, var10, var11);
      this.g2d.setComposite(var13);
      this.g2d.setPaint(var12);
   }

   @Override
   public void drawPixelsMasked(RTTexture var1, RTTexture var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      this.doDrawMaskTexture((J2DRTTexture)var1, (J2DRTTexture)var2, var3, var4, var5, var6, var7, var8, var9, var10, true);
   }

   @Override
   public void maskInterpolatePixels(RTTexture var1, RTTexture var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      this.doDrawMaskTexture((J2DRTTexture)var1, (J2DRTTexture)var2, var3, var4, var5, var6, var7, var8, var9, var10, false);
   }

   private void doDrawMaskTexture(
      J2DRTTexture var1, J2DRTTexture var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, boolean var11
   ) {
      int var12 = this.clipRect.x;
      int var13 = this.clipRect.y;
      int var14 = var12 + this.clipRect.width;
      int var15 = var13 + this.clipRect.height;
      if (var5 > 0 && var6 > 0) {
         if (var3 < var12) {
            int var16 = var12 - var3;
            if ((var5 -= var16) <= 0) {
               return;
            }

            var7 += var16;
            var9 += var16;
            var3 = var12;
         }

         if (var4 < var13) {
            int var38 = var13 - var4;
            if ((var6 -= var38) <= 0) {
               return;
            }

            var8 += var38;
            var10 += var38;
            var4 = var13;
         }

         if (var3 + var5 <= var14 || (var5 = var14 - var3) > 0) {
            if (var4 + var6 <= var15 || (var6 = var15 - var4) > 0) {
               int var39 = var1.getContentWidth();
               int var17 = var1.getContentHeight();
               if (var7 < 0) {
                  if ((var5 += var7) <= 0) {
                     return;
                  }

                  var3 -= var7;
                  var9 -= var7;
                  var7 = 0;
               }

               if (var8 < 0) {
                  if ((var6 += var8) <= 0) {
                     return;
                  }

                  var4 -= var8;
                  var10 -= var8;
                  var8 = 0;
               }

               if (var7 + var5 <= var39 || (var5 = var39 - var7) > 0) {
                  if (var8 + var6 <= var17 || (var6 = var17 - var8) > 0) {
                     int var18 = var2.getContentWidth();
                     int var19 = var2.getContentHeight();
                     if (var9 < 0) {
                        if ((var5 += var9) <= 0) {
                           return;
                        }

                        var3 -= var9;
                        var7 -= var9;
                        var9 = 0;
                     }

                     if (var10 < 0) {
                        if ((var6 += var10) <= 0) {
                           return;
                        }

                        var4 -= var10;
                        var8 -= var10;
                        var10 = 0;
                     }

                     if (var9 + var5 <= var18 || (var5 = var18 - var9) > 0) {
                        if (var10 + var6 <= var19 || (var6 = var19 - var10) > 0) {
                           int[] var20 = var1.getPixels();
                           int[] var21 = var2.getPixels();
                           DataBuffer var22 = this.target.getBackBuffer().getRaster().getDataBuffer();
                           int[] var23 = ((DataBufferInt)var22).getData();
                           int var24 = var1.getBufferedImage().getWidth();
                           int var25 = var2.getBufferedImage().getWidth();
                           int var26 = this.target.getBackBuffer().getWidth();
                           int var27 = var8 * var24 + var7;
                           int var28 = var10 * var25 + var9;
                           int var29 = var4 * var26 + var3;
                           if (var11) {
                              for (int var30 = 0; var30 < var6; var30++) {
                                 for (int var31 = 0; var31 < var5; var31++) {
                                    int var36 = var21[var28 + var31] >>> 24;
                                    if (var36 != 0) {
                                       int var37 = var20[var27 + var31];
                                       int var32 = var37 >>> 24;
                                       if (var32 != 0) {
                                          int var33;
                                          int var34;
                                          int var35;
                                          if (var36 < 255) {
                                             var36 += var36 >> 7;
                                             var32 *= var36;
                                             var33 = (var37 >> 16 & 0xFF) * var36;
                                             var34 = (var37 >> 8 & 0xFF) * var36;
                                             var35 = (var37 & 0xFF) * var36;
                                          } else {
                                             if (var32 >= 255) {
                                                var23[var29 + var31] = var37;
                                                continue;
                                             }

                                             var32 <<= 8;
                                             var33 = (var37 >> 16 & 0xFF) << 8;
                                             var34 = (var37 >> 8 & 0xFF) << 8;
                                             var35 = (var37 & 0xFF) << 8;
                                          }

                                          var36 = var32 + 128 >> 8;
                                          var36 += var36 >> 7;
                                          var36 = 256 - var36;
                                          var37 = var23[var29 + var31];
                                          var32 += (var37 >>> 24) * var36 + 128;
                                          var33 += (var37 >> 16 & 0xFF) * var36 + 128;
                                          var34 += (var37 >> 8 & 0xFF) * var36 + 128;
                                          var35 += (var37 & 0xFF) * var36 + 128;
                                          var37 = (var32 >> 8 << 24) + (var33 >> 8 << 16) + (var34 >> 8 << 8) + (var35 >> 8);
                                          var23[var29 + var31] = var37;
                                       }
                                    }
                                 }

                                 var27 += var24;
                                 var28 += var25;
                                 var29 += var26;
                              }
                           } else {
                              for (int var40 = 0; var40 < var6; var40++) {
                                 for (int var41 = 0; var41 < var5; var41++) {
                                    int var44 = var21[var28 + var41] >>> 24;
                                    if (var44 != 0) {
                                       int var48 = var20[var27 + var41];
                                       if (var44 < 255) {
                                          var44 += var44 >> 7;
                                          int var51 = (var48 >>> 24) * var44;
                                          int var54 = (var48 >> 16 & 0xFF) * var44;
                                          int var60 = (var48 >> 8 & 0xFF) * var44;
                                          int var64 = (var48 & 0xFF) * var44;
                                          var44 = 256 - var44;
                                          var48 = var23[var29 + var41];
                                          var51 += (var48 >>> 24) * var44 + 128;
                                          var54 += (var48 >> 16 & 0xFF) * var44 + 128;
                                          var60 += (var48 >> 8 & 0xFF) * var44 + 128;
                                          var64 += (var48 & 0xFF) * var44 + 128;
                                          var48 = (var51 >> 8 << 24) + (var54 >> 8 << 16) + (var60 >> 8 << 8) + (var64 >> 8);
                                       }

                                       var23[var29 + var41] = var48;
                                    }
                                 }

                                 var27 += var24;
                                 var28 += var25;
                                 var29 += var26;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean canReadBack() {
      return true;
   }

   @Override
   public RTTexture readBack(Rectangle var1) {
      J2DRTTexture var2 = this.target.getReadbackBuffer();
      Graphics2D var3 = var2.createAWTGraphics2D();
      var3.setComposite(AlphaComposite.Src);
      int var4 = var1.x;
      int var5 = var1.y;
      int var6 = var1.width;
      int var7 = var1.height;
      int var8 = var4 + var6;
      int var9 = var5 + var7;
      var3.drawImage(this.target.getBackBuffer(), 0, 0, var6, var7, var4, var5, var8, var9, null);
      var3.dispose();
      return var2;
   }

   @Override
   public void releaseReadBackBuffer(RTTexture var1) {
   }

   @Override
   public NGCamera getCameraNoClone() {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void setPerspectiveTransform(GeneralTransform3D var1) {
   }

   @Override
   public boolean isDepthBuffer() {
      return false;
   }

   @Override
   public boolean isDepthTest() {
      return false;
   }

   @Override
   public boolean isAlphaTestShader() {
      if (PrismSettings.verbose && PrismSettings.forceAlphaTestShader) {
         System.out.println("J2D pipe doesn't support shader with alpha testing");
      }

      return false;
   }

   @Override
   public void setAntialiasedShape(boolean var1) {
      this.antialiasedShape = var1;
      this.g2d
         .setRenderingHint(RenderingHints.KEY_ANTIALIASING, this.antialiasedShape ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
   }

   @Override
   public boolean isAntialiasedShape() {
      return this.antialiasedShape;
   }

   @Override
   public void scale(float var1, float var2, float var3) {
      throw new UnsupportedOperationException("Not supported yet.");
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
      if (var5 == 0.0 && var13 == 0.0 && var17 == 0.0 && var19 == 0.0 && var21 == 1.0 && var23 == 0.0) {
         this.setTransform(var1, var9, var3, var11, var7, var15);
      } else {
         throw new UnsupportedOperationException("3D transforms not supported.");
      }
   }

   @Override
   public void setCamera(NGCamera var1) {
   }

   @Override
   public void setDepthBuffer(boolean var1) {
   }

   @Override
   public void setDepthTest(boolean var1) {
   }

   @Override
   public void sync() {
   }

   @Override
   public void translate(float var1, float var2, float var3) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void setCulling(boolean var1) {
      this.cull = var1;
   }

   public boolean isCulling() {
      return this.cull;
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
   public void setRenderRoot(NodePath var1) {
      this.renderRoot = var1;
   }

   @Override
   public NodePath getRenderRoot() {
      return this.renderRoot;
   }

   @Override
   public void setState3D(boolean var1) {
   }

   @Override
   public boolean isState3D() {
      return false;
   }

   @Override
   public void setup3DRendering() {
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
   public void blit(RTTexture var1, RTTexture var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   @Override
   public void setLights(NGLightBase[] var1) {
   }

   @Override
   public NGLightBase[] getLights() {
      return null;
   }

   private static class AdaptorPathIterator implements PathIterator {
      private static int[] NUM_COORDS = new int[]{2, 2, 4, 6, 0};
      com.sun.javafx.geom.PathIterator priterator;
      float[] tmpcoords;

      public void setIterator(com.sun.javafx.geom.PathIterator var1) {
         this.priterator = var1;
      }

      @Override
      public int currentSegment(float[] var1) {
         return this.priterator.currentSegment(var1);
      }

      @Override
      public int currentSegment(double[] var1) {
         if (this.tmpcoords == null) {
            this.tmpcoords = new float[6];
         }

         int var2 = this.priterator.currentSegment(this.tmpcoords);

         for (int var3 = 0; var3 < NUM_COORDS[var2]; var3++) {
            var1[var3] = (double)this.tmpcoords[var3];
         }

         return var2;
      }

      @Override
      public int getWindingRule() {
         return this.priterator.getWindingRule();
      }

      @Override
      public boolean isDone() {
         return this.priterator.isDone();
      }

      @Override
      public void next() {
         this.priterator.next();
      }
   }

   private static class AdaptorShape implements Shape {
      private com.sun.javafx.geom.Shape prshape;
      private static J2DPrismGraphics.AdaptorPathIterator tmpAdaptor = new J2DPrismGraphics.AdaptorPathIterator();

      public void setShape(com.sun.javafx.geom.Shape var1) {
         this.prshape = var1;
      }

      @Override
      public boolean contains(double var1, double var3) {
         return this.prshape.contains((float)var1, (float)var3);
      }

      @Override
      public boolean contains(Point2D var1) {
         return this.contains(var1.getX(), var1.getY());
      }

      @Override
      public boolean contains(double var1, double var3, double var5, double var7) {
         return this.prshape.contains((float)var1, (float)var3, (float)var5, (float)var7);
      }

      @Override
      public boolean contains(Rectangle2D var1) {
         return this.contains(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
      }

      @Override
      public boolean intersects(double var1, double var3, double var5, double var7) {
         return this.prshape.intersects((float)var1, (float)var3, (float)var5, (float)var7);
      }

      @Override
      public boolean intersects(Rectangle2D var1) {
         return this.intersects(var1.getX(), var1.getY(), var1.getWidth(), var1.getHeight());
      }

      @Override
      public java.awt.Rectangle getBounds() {
         return this.getBounds2D().getBounds();
      }

      @Override
      public Rectangle2D getBounds2D() {
         RectBounds var1 = this.prshape.getBounds();
         java.awt.geom.Rectangle2D.Float var2 = new java.awt.geom.Rectangle2D.Float();
         var2.setFrameFromDiagonal((double)var1.getMinX(), (double)var1.getMinY(), (double)var1.getMaxX(), (double)var1.getMaxY());
         return var2;
      }

      private static PathIterator tmpAdaptor(com.sun.javafx.geom.PathIterator var0) {
         tmpAdaptor.setIterator(var0);
         return tmpAdaptor;
      }

      @Override
      public PathIterator getPathIterator(AffineTransform var1) {
         BaseTransform var2 = var1 == null ? null : J2DPrismGraphics.toPrTransform(var1);
         return tmpAdaptor(this.prshape.getPathIterator(var2));
      }

      @Override
      public PathIterator getPathIterator(AffineTransform var1, double var2) {
         BaseTransform var4 = var1 == null ? null : J2DPrismGraphics.toPrTransform(var1);
         return tmpAdaptor(this.prshape.getPathIterator(var4, (float)var2));
      }
   }

   abstract static class FilterStroke implements Stroke {
      protected java.awt.BasicStroke stroke;
      static final double CtrlVal = 0.5522847498307933;

      FilterStroke(java.awt.BasicStroke var1) {
         this.stroke = var1;
      }

      protected abstract Shape makeStrokedRect(Rectangle2D var1);

      protected abstract Shape makeStrokedShape(Shape var1);

      @Override
      public Shape createStrokedShape(Shape var1) {
         if (var1 instanceof Rectangle2D) {
            Shape var2 = this.makeStrokedRect((Rectangle2D)var1);
            if (var2 != null) {
               return var2;
            }
         }

         return this.makeStrokedShape(var1);
      }

      static Point2D cornerArc(GeneralPath var0, float var1, float var2, float var3, float var4, float var5, float var6) {
         return cornerArc(var0, var1, var2, var3, var4, var5, var6, 0.5F);
      }

      static Point2D cornerArc(GeneralPath var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7) {
         float var8 = (float)((double)var1 + 0.5522847498307933 * (double)(var3 - var1));
         float var9 = (float)((double)var2 + 0.5522847498307933 * (double)(var4 - var2));
         float var10 = (float)((double)var5 + 0.5522847498307933 * (double)(var3 - var5));
         float var11 = (float)((double)var6 + 0.5522847498307933 * (double)(var4 - var6));
         var0.curveTo(var8, var9, var10, var11, var5, var6);
         return new java.awt.geom.Point2D.Float(eval(var1, var8, var10, var5, var7), eval(var2, var9, var11, var6, var7));
      }

      static float eval(float var0, float var1, float var2, float var3, float var4) {
         var0 += (var1 - var0) * var4;
         var1 += (var2 - var1) * var4;
         var2 += (var3 - var2) * var4;
         var0 += (var1 - var0) * var4;
         var1 += (var2 - var1) * var4;
         return var0 + (var1 - var0) * var4;
      }
   }

   static class InnerStroke extends J2DPrismGraphics.FilterStroke {
      InnerStroke(java.awt.BasicStroke var1) {
         super(var1);
      }

      @Override
      protected Shape makeStrokedRect(Rectangle2D var1) {
         if (this.stroke.getDashArray() != null) {
            return null;
         } else {
            float var2 = this.stroke.getLineWidth() / 2.0F;
            if (!((double)var2 >= var1.getWidth()) && !((double)var2 >= var1.getHeight())) {
               float var3 = (float)var1.getX();
               float var4 = (float)var1.getY();
               float var5 = var3 + (float)var1.getWidth();
               float var6 = var4 + (float)var1.getHeight();
               GeneralPath var7 = new GeneralPath();
               var7.moveTo(var3, var4);
               var7.lineTo(var5, var4);
               var7.lineTo(var5, var6);
               var7.lineTo(var3, var6);
               var7.closePath();
               var3 += var2;
               var4 += var2;
               var5 -= var2;
               var6 -= var2;
               var7.moveTo(var3, var4);
               var7.lineTo(var3, var6);
               var7.lineTo(var5, var6);
               var7.lineTo(var5, var4);
               var7.closePath();
               return var7;
            } else {
               return var1;
            }
         }
      }

      protected Shape makeStrokedEllipse(Ellipse2D var1) {
         if (this.stroke.getDashArray() != null) {
            return null;
         } else {
            float var2 = this.stroke.getLineWidth() / 2.0F;
            float var3 = (float)var1.getWidth();
            float var4 = (float)var1.getHeight();
            if (var3 - 2.0F * var2 > var4 * 2.0F || var4 - 2.0F * var2 > var3 * 2.0F) {
               return null;
            } else if (!(var2 >= var3) && !(var2 >= var4)) {
               float var5 = (float)var1.getX();
               float var6 = (float)var1.getY();
               float var7 = var5 + var3 / 2.0F;
               float var8 = var6 + var4 / 2.0F;
               float var9 = var5 + var3;
               float var10 = var6 + var4;
               GeneralPath var11 = new GeneralPath();
               var11.moveTo(var7, var6);
               cornerArc(var11, var7, var6, var9, var6, var9, var8);
               cornerArc(var11, var9, var8, var9, var10, var7, var10);
               cornerArc(var11, var7, var10, var5, var10, var5, var8);
               cornerArc(var11, var5, var8, var5, var6, var7, var6);
               var11.closePath();
               var5 += var2;
               var6 += var2;
               var9 -= var2;
               var10 -= var2;
               var11.moveTo(var7, var6);
               cornerArc(var11, var7, var6, var5, var6, var5, var8);
               cornerArc(var11, var5, var8, var5, var10, var7, var10);
               cornerArc(var11, var7, var10, var9, var10, var9, var8);
               cornerArc(var11, var9, var8, var9, var6, var7, var6);
               var11.closePath();
               return var11;
            } else {
               return var1;
            }
         }
      }

      @Override
      protected Shape makeStrokedShape(Shape var1) {
         Shape var2 = this.stroke.createStrokedShape(var1);
         Area var3 = new Area(var2);
         var3.intersect(new Area(var1));
         return var3;
      }
   }

   static class OuterStroke extends J2DPrismGraphics.FilterStroke {
      static double SQRT_2 = Math.sqrt(2.0);

      OuterStroke(java.awt.BasicStroke var1) {
         super(var1);
      }

      @Override
      protected Shape makeStrokedRect(Rectangle2D var1) {
         if (this.stroke.getDashArray() != null) {
            return null;
         } else {
            float var2 = this.stroke.getLineWidth() / 2.0F;
            float var3 = (float)var1.getX();
            float var4 = (float)var1.getY();
            float var5 = var3 + (float)var1.getWidth();
            float var6 = var4 + (float)var1.getHeight();
            GeneralPath var7 = new GeneralPath();
            var7.moveTo(var3, var4);
            var7.lineTo(var5, var4);
            var7.lineTo(var5, var6);
            var7.lineTo(var3, var6);
            var7.closePath();
            float var8 = var3 - var2;
            float var9 = var4 - var2;
            float var10 = var5 + var2;
            float var11 = var6 + var2;
            switch (this.stroke.getLineJoin()) {
               case 0:
                  if ((double)this.stroke.getMiterLimit() >= SQRT_2) {
                     var7.moveTo(var8, var9);
                     var7.lineTo(var8, var11);
                     var7.lineTo(var10, var11);
                     var7.lineTo(var10, var9);
                     var7.closePath();
                     break;
                  }
               case 2:
                  var7.moveTo(var8, var4);
                  var7.lineTo(var8, var6);
                  var7.lineTo(var3, var11);
                  var7.lineTo(var5, var11);
                  var7.lineTo(var10, var6);
                  var7.lineTo(var10, var4);
                  var7.lineTo(var5, var9);
                  var7.lineTo(var3, var9);
                  var7.closePath();
                  break;
               case 1:
                  var7.moveTo(var8, var4);
                  var7.lineTo(var8, var6);
                  cornerArc(var7, var8, var6, var8, var11, var3, var11);
                  var7.lineTo(var5, var11);
                  cornerArc(var7, var5, var11, var10, var11, var10, var6);
                  var7.lineTo(var10, var4);
                  cornerArc(var7, var10, var4, var10, var9, var5, var9);
                  var7.lineTo(var3, var9);
                  cornerArc(var7, var3, var9, var8, var9, var8, var4);
                  var7.closePath();
                  break;
               default:
                  throw new InternalError("Unrecognized line join style");
            }

            return var7;
         }
      }

      protected Shape makeStrokedEllipse(Ellipse2D var1) {
         if (this.stroke.getDashArray() != null) {
            return null;
         } else {
            float var2 = this.stroke.getLineWidth() / 2.0F;
            float var3 = (float)var1.getWidth();
            float var4 = (float)var1.getHeight();
            if (!(var3 > var4 * 2.0F) && !(var4 > var3 * 2.0F)) {
               float var5 = (float)var1.getX();
               float var6 = (float)var1.getY();
               float var7 = var5 + var3 / 2.0F;
               float var8 = var6 + var4 / 2.0F;
               float var9 = var5 + var3;
               float var10 = var6 + var4;
               GeneralPath var11 = new GeneralPath();
               var11.moveTo(var7, var6);
               cornerArc(var11, var7, var6, var9, var6, var9, var8);
               cornerArc(var11, var9, var8, var9, var10, var7, var10);
               cornerArc(var11, var7, var10, var5, var10, var5, var8);
               cornerArc(var11, var5, var8, var5, var6, var7, var6);
               var11.closePath();
               var5 -= var2;
               var6 -= var2;
               var9 += var2;
               var10 += var2;
               var11.moveTo(var7, var6);
               cornerArc(var11, var7, var6, var5, var6, var5, var8);
               cornerArc(var11, var5, var8, var5, var10, var7, var10);
               cornerArc(var11, var7, var10, var9, var10, var9, var8);
               cornerArc(var11, var9, var8, var9, var6, var7, var6);
               var11.closePath();
               return var11;
            } else {
               return null;
            }
         }
      }

      @Override
      protected Shape makeStrokedShape(Shape var1) {
         Shape var2 = this.stroke.createStrokedShape(var1);
         Area var3 = new Area(var2);
         var3.subtract(new Area(var1));
         return var3;
      }
   }
}
