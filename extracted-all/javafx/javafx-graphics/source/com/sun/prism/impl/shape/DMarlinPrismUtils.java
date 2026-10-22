package com.sun.prism.impl.shape;

import com.sun.javafx.geom.Path2D;
import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Shape;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.marlin.DPathConsumer2D;
import com.sun.marlin.MarlinConst;
import com.sun.marlin.MarlinProperties;
import com.sun.marlin.MarlinRenderer;
import com.sun.marlin.MarlinUtils;
import com.sun.marlin.RendererContext;
import com.sun.marlin.TransformingPathConsumer2D;
import com.sun.prism.BasicStroke;
import java.util.Arrays;

public final class DMarlinPrismUtils {
   private static final boolean FORCE_NO_AA = false;
   static final boolean DISABLE_2ND_STROKER_CLIPPING = true;
   static final boolean DO_TRACE_PATH = false;
   static final boolean DO_CLIP = MarlinProperties.isDoClip();
   static final boolean DO_CLIP_FILL = true;
   static final boolean DO_CLIP_RUNTIME_ENABLE = MarlinProperties.isDoClipRuntimeFlag();
   static final float UPPER_BND = 1.7014117E38F;
   static final float LOWER_BND = -1.7014117E38F;

   private DMarlinPrismUtils() {
   }

   private static DPathConsumer2D initStroker(RendererContext var0, BasicStroke var1, float var2, BaseTransform var3, DPathConsumer2D var4) {
      BaseTransform var5 = null;
      int var6 = -1;
      boolean var7 = false;
      double var8 = (double)var2;
      float[] var10 = var1.getDashArray();
      double[] var11 = null;
      double var12 = (double)var1.getDashPhase();
      if (var10 != null) {
         var7 = true;
         var6 = var10.length;
         var11 = var0.dasher.copyDashArray(var10);
      }

      if (var3 != null && !var3.isIdentity()) {
         double var14 = var3.getMxx();
         double var16 = var3.getMxy();
         double var18 = var3.getMyx();
         double var20 = var3.getMyy();
         if (nearZero(var14 * var16 + var18 * var20) && nearZero(var14 * var14 + var18 * var18 - (var16 * var16 + var20 * var20))) {
            double var22 = Math.sqrt(var14 * var14 + var18 * var18);
            if (var11 != null) {
               for (int var24 = 0; var24 < var6; var24++) {
                  var11[var24] *= var22;
               }

               var12 *= var22;
            }

            var8 *= var22;
         } else {
            var5 = var3;
         }
      } else {
         Object var25 = null;
      }

      Object var26 = var4;
      TransformingPathConsumer2D var15 = var0.transformerPC2D;
      if (MarlinConst.USE_SIMPLIFIER) {
         var26 = var0.simplifier.init(var4);
      }

      var26 = var15.deltaTransformConsumer((DPathConsumer2D)var26, var5);
      var26 = var0.stroker.init((DPathConsumer2D)var26, var8, var1.getEndCap(), var1.getLineJoin(), (double)var1.getMiterLimit(), var11 == null);
      var0.monotonizer.init(var8);
      if (var11 != null) {
         var26 = var0.dasher.init((DPathConsumer2D)var26, var11, var6, var12, var7);
         var0.stroker.disableClipping();
      } else if (var0.doClip && var1.getEndCap() != 0) {
         var26 = var15.detectClosedPath((DPathConsumer2D)var26);
      }

      return var15.inverseDeltaTransformConsumer((DPathConsumer2D)var26, var5);
   }

   private static boolean nearZero(double var0) {
      return Math.abs(var0) < 2.0 * Math.ulp(var0);
   }

   private static DPathConsumer2D initRenderer(RendererContext var0, BasicStroke var1, BaseTransform var2, Rectangle var3, int var4, MarlinRenderer var5) {
      if (DO_CLIP || DO_CLIP_RUNTIME_ENABLE && MarlinProperties.isDoClipAtRuntime()) {
         double[] var6 = var0.clipRect;
         double var7 = var5.getOffsetX();
         double var9 = var5.getOffsetY();
         var6[0] = (double)var3.y - 0.001 + var9;
         var6[1] = (double)(var3.y + var3.height) + 0.001 + var9;
         var6[2] = (double)var3.x - 0.001 + var7;
         var6[3] = (double)(var3.x + var3.width) + 0.001 + var7;
         if (MarlinConst.DO_LOG_CLIP) {
            MarlinUtils.logInfo("clipRect (clip): " + Arrays.toString(var0.clipRect));
         }

         var0.doClip = true;
      }

      if (var1 != null) {
         var5.init(var3.x, var3.y, var3.width, var3.height, 1);
         return initStroker(var0, var1, var1.getLineWidth(), var2, var5);
      } else {
         int var11 = var4 == 0 ? 0 : 1;
         var5.init(var3.x, var3.y, var3.width, var3.height, var11);
         Object var12 = var5;
         TransformingPathConsumer2D var8 = var0.transformerPC2D;
         if (var0.doClip) {
            var12 = var0.transformerPC2D.pathClipper(var5);
         }

         return (DPathConsumer2D)var12;
      }
   }

   public static MarlinRenderer setupRenderer(RendererContext var0, Shape var1, BasicStroke var2, BaseTransform var3, Rectangle var4, boolean var5) {
      BaseTransform var6 = var3 != null && !var3.isIdentity() ? var3 : null;
      Object var7 = var5 ? var0.renderer : var0.getRendererNoAA();
      if (var1 instanceof Path2D) {
         Path2D var8 = (Path2D)var1;
         DPathConsumer2D var9 = initRenderer(var0, var2, var6, var4, var8.getWindingRule(), (MarlinRenderer)var7);
         feedConsumer(var0, var8, var6, var9);
      } else {
         PathIterator var10 = var1.getPathIterator(var6);
         DPathConsumer2D var11 = initRenderer(var0, var2, var6, var4, var10.getWindingRule(), (MarlinRenderer)var7);
         feedConsumer(var0, var10, var11);
      }

      return (MarlinRenderer)var7;
   }

   public static void strokeTo(RendererContext var0, Shape var1, BasicStroke var2, float var3, DPathConsumer2D var4) {
      DPathConsumer2D var5 = initStroker(var0, var2, var3, null, var4);
      if (var1 instanceof Path2D) {
         feedConsumer(var0, (Path2D)var1, null, var5);
      } else {
         feedConsumer(var0, var1.getPathIterator(null), var5);
      }
   }

   private static void feedConsumer(RendererContext var0, PathIterator var1, DPathConsumer2D var2) {
      if (MarlinConst.USE_PATH_SIMPLIFIER) {
         var2 = var0.pathSimplifier.init((DPathConsumer2D)var2);
      }

      var0.dirty = true;
      float[] var3 = var0.float6;
      boolean var4 = false;

      while (!var1.isDone()) {
         switch (var1.currentSegment(var3)) {
            case 0:
               if (var3[0] < 1.7014117E38F && var3[0] > -1.7014117E38F && var3[1] < 1.7014117E38F && var3[1] > -1.7014117E38F) {
                  ((DPathConsumer2D)var2).moveTo((double)var3[0], (double)var3[1]);
                  var4 = true;
               }
               break;
            case 1:
               if (var3[0] < 1.7014117E38F && var3[0] > -1.7014117E38F && var3[1] < 1.7014117E38F && var3[1] > -1.7014117E38F) {
                  if (var4) {
                     ((DPathConsumer2D)var2).lineTo((double)var3[0], (double)var3[1]);
                  } else {
                     ((DPathConsumer2D)var2).moveTo((double)var3[0], (double)var3[1]);
                     var4 = true;
                  }
               }
               break;
            case 2:
               if (var3[2] < 1.7014117E38F && var3[2] > -1.7014117E38F && var3[3] < 1.7014117E38F && var3[3] > -1.7014117E38F) {
                  if (var4) {
                     if (var3[0] < 1.7014117E38F && var3[0] > -1.7014117E38F && var3[1] < 1.7014117E38F && var3[1] > -1.7014117E38F) {
                        ((DPathConsumer2D)var2).quadTo((double)var3[0], (double)var3[1], (double)var3[2], (double)var3[3]);
                     } else {
                        ((DPathConsumer2D)var2).lineTo((double)var3[2], (double)var3[3]);
                     }
                  } else {
                     ((DPathConsumer2D)var2).moveTo((double)var3[2], (double)var3[3]);
                     var4 = true;
                  }
               }
               break;
            case 3:
               if (var3[4] < 1.7014117E38F && var3[4] > -1.7014117E38F && var3[5] < 1.7014117E38F && var3[5] > -1.7014117E38F) {
                  if (var4) {
                     if (var3[0] < 1.7014117E38F
                        && var3[0] > -1.7014117E38F
                        && var3[1] < 1.7014117E38F
                        && var3[1] > -1.7014117E38F
                        && var3[2] < 1.7014117E38F
                        && var3[2] > -1.7014117E38F
                        && var3[3] < 1.7014117E38F
                        && var3[3] > -1.7014117E38F) {
                        ((DPathConsumer2D)var2).curveTo((double)var3[0], (double)var3[1], (double)var3[2], (double)var3[3], (double)var3[4], (double)var3[5]);
                     } else {
                        ((DPathConsumer2D)var2).lineTo((double)var3[4], (double)var3[5]);
                     }
                  } else {
                     ((DPathConsumer2D)var2).moveTo((double)var3[4], (double)var3[5]);
                     var4 = true;
                  }
               }
               break;
            case 4:
               if (var4) {
                  ((DPathConsumer2D)var2).closePath();
               }
         }

         var1.next();
      }

      ((DPathConsumer2D)var2).pathDone();
      var0.dirty = false;
   }

   private static void feedConsumer(RendererContext var0, Path2D var1, BaseTransform var2, DPathConsumer2D var3) {
      if (MarlinConst.USE_PATH_SIMPLIFIER) {
         var3 = var0.pathSimplifier.init((DPathConsumer2D)var3);
      }

      var0.dirty = true;
      float[] var4 = var0.float6;
      boolean var5 = false;
      float[] var6 = var1.getFloatCoordsNoClone();
      byte[] var7 = var1.getCommandsNoClone();
      int var8 = var1.getNumCommands();
      int var9 = 0;

      for (byte var10 = 0; var9 < var8; var9++) {
         switch (var7[var9]) {
            case 0:
               if (var2 == null) {
                  var4[0] = var6[var10];
                  var4[1] = var6[var10 + 1];
               } else {
                  var2.transform(var6, var10, var4, 0, 1);
               }

               var10 += 2;
               if (var4[0] < 1.7014117E38F && var4[0] > -1.7014117E38F && var4[1] < 1.7014117E38F && var4[1] > -1.7014117E38F) {
                  ((DPathConsumer2D)var3).moveTo((double)var4[0], (double)var4[1]);
                  var5 = true;
               }
               break;
            case 1:
               if (var2 == null) {
                  var4[0] = var6[var10];
                  var4[1] = var6[var10 + 1];
               } else {
                  var2.transform(var6, var10, var4, 0, 1);
               }

               var10 += 2;
               if (var4[0] < 1.7014117E38F && var4[0] > -1.7014117E38F && var4[1] < 1.7014117E38F && var4[1] > -1.7014117E38F) {
                  if (var5) {
                     ((DPathConsumer2D)var3).lineTo((double)var4[0], (double)var4[1]);
                  } else {
                     ((DPathConsumer2D)var3).moveTo((double)var4[0], (double)var4[1]);
                     var5 = true;
                  }
               }
               break;
            case 2:
               if (var2 == null) {
                  var4[0] = var6[var10];
                  var4[1] = var6[var10 + 1];
                  var4[2] = var6[var10 + 2];
                  var4[3] = var6[var10 + 3];
               } else {
                  var2.transform(var6, var10, var4, 0, 2);
               }

               var10 += 4;
               if (var4[2] < 1.7014117E38F && var4[2] > -1.7014117E38F && var4[3] < 1.7014117E38F && var4[3] > -1.7014117E38F) {
                  if (var5) {
                     if (var4[0] < 1.7014117E38F && var4[0] > -1.7014117E38F && var4[1] < 1.7014117E38F && var4[1] > -1.7014117E38F) {
                        ((DPathConsumer2D)var3).quadTo((double)var4[0], (double)var4[1], (double)var4[2], (double)var4[3]);
                     } else {
                        ((DPathConsumer2D)var3).lineTo((double)var4[2], (double)var4[3]);
                     }
                  } else {
                     ((DPathConsumer2D)var3).moveTo((double)var4[2], (double)var4[3]);
                     var5 = true;
                  }
               }
               break;
            case 3:
               if (var2 == null) {
                  var4[0] = var6[var10];
                  var4[1] = var6[var10 + 1];
                  var4[2] = var6[var10 + 2];
                  var4[3] = var6[var10 + 3];
                  var4[4] = var6[var10 + 4];
                  var4[5] = var6[var10 + 5];
               } else {
                  var2.transform(var6, var10, var4, 0, 3);
               }

               var10 += 6;
               if (var4[4] < 1.7014117E38F && var4[4] > -1.7014117E38F && var4[5] < 1.7014117E38F && var4[5] > -1.7014117E38F) {
                  if (var5) {
                     if (var4[0] < 1.7014117E38F
                        && var4[0] > -1.7014117E38F
                        && var4[1] < 1.7014117E38F
                        && var4[1] > -1.7014117E38F
                        && var4[2] < 1.7014117E38F
                        && var4[2] > -1.7014117E38F
                        && var4[3] < 1.7014117E38F
                        && var4[3] > -1.7014117E38F) {
                        ((DPathConsumer2D)var3).curveTo((double)var4[0], (double)var4[1], (double)var4[2], (double)var4[3], (double)var4[4], (double)var4[5]);
                     } else {
                        ((DPathConsumer2D)var3).lineTo((double)var4[4], (double)var4[5]);
                     }
                  } else {
                     ((DPathConsumer2D)var3).moveTo((double)var4[4], (double)var4[5]);
                     var5 = true;
                  }
               }
               break;
            case 4:
               if (var5) {
                  ((DPathConsumer2D)var3).closePath();
               }
         }
      }

      ((DPathConsumer2D)var3).pathDone();
      var0.dirty = false;
   }
}
