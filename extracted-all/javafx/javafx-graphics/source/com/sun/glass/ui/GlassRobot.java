package com.sun.glass.ui;

import com.sun.javafx.image.PixelUtils;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

public abstract class GlassRobot {
   public static final int MOUSE_LEFT_BTN = 1;
   public static final int MOUSE_RIGHT_BTN = 2;
   public static final int MOUSE_MIDDLE_BTN = 4;
   public static final int MOUSE_BACK_BTN = 8;
   public static final int MOUSE_FORWARD_BTN = 16;

   public abstract void create();

   public abstract void destroy();

   public abstract void keyPress(KeyCode var1);

   public abstract void keyRelease(KeyCode var1);

   public abstract double getMouseX();

   public abstract double getMouseY();

   public abstract void mouseMove(double var1, double var3);

   public abstract void mousePress(MouseButton... var1);

   public abstract void mouseRelease(MouseButton... var1);

   public abstract void mouseWheel(int var1);

   public abstract Color getPixelColor(double var1, double var3);

   public void getScreenCapture(int var1, int var2, int var3, int var4, int[] var5, boolean var6) {
      throw new InternalError("not implemented");
   }

   public WritableImage getScreenCapture(WritableImage var1, double var2, double var4, double var6, double var8, boolean var10) {
      if (var6 <= 0.0) {
         throw new IllegalArgumentException("width must be > 0");
      } else if (var8 <= 0.0) {
         throw new IllegalArgumentException("height must be > 0");
      } else {
         javafx.stage.Screen var11 = javafx.stage.Screen.getPrimary();
         Objects.requireNonNull(var11);
         double var12 = var11.getOutputScaleX();
         double var14 = var11.getOutputScaleY();
         int[] var16;
         int var17;
         int var18;
         if (var12 == 1.0 && var14 == 1.0) {
            var16 = new int[(int)(var6 * var8)];
            this.getScreenCapture((int)var2, (int)var4, (int)var6, (int)var8, var16, var10);
            var17 = (int)var6;
            var18 = (int)var8;
         } else {
            int var19 = (int)Math.floor(var2 * var12);
            int var20 = (int)Math.floor(var4 * var14);
            int var21 = (int)Math.ceil((var2 + var6) * var12);
            int var22 = (int)Math.ceil((var4 + var8) * var14);
            int var23 = var21 - var19;
            int var24 = var22 - var20;
            int[] var25 = new int[var23 * var24];
            this.getScreenCapture(var19, var20, var23, var24, var25, var10);
            var17 = var23;
            var18 = var24;
            if (!var10) {
               var16 = var25;
            } else {
               var16 = new int[(int)(var6 * var8)];
               int var26 = 0;

               for (int var27 = 0; (double)var27 < var8; var27++) {
                  double var28 = (var4 + (double)var27 + 0.5) * var14 - (double)((float)var20 + 0.5F);
                  int var30 = (int)Math.floor(var28);
                  int var31 = (int)((var28 - (double)var30) * 256.0);

                  for (int var32 = 0; (double)var32 < var6; var32++) {
                     double var33 = (var2 + (double)var32 + 0.5) * var12 - (double)((float)var19 + 0.5F);
                     int var35 = (int)Math.floor(var33);
                     int var36 = (int)((var33 - (double)var35) * 256.0);
                     var16[var26++] = interp(var25, var35, var30, var23, var24, var36, var31);
                  }
               }

               var17 = (int)var6;
               var18 = (int)var8;
            }
         }

         return convertFromPixels(var1, Application.GetApplication().createPixels(var17, var18, IntBuffer.wrap(var16)));
      }
   }

   public static int convertToRobotMouseButton(MouseButton[] var0) {
      byte var1 = 0;

      for (MouseButton var5 : var0) {
         switch (var5) {
            case PRIMARY:
               var1 |= 1;
               break;
            case SECONDARY:
               var1 |= 2;
               break;
            case MIDDLE:
               var1 |= 4;
               break;
            case BACK:
               var1 |= 8;
               break;
            case FORWARD:
               var1 |= 16;
               break;
            default:
               throw new IllegalArgumentException("MouseButton: " + var5 + " not supported by Robot");
         }
      }

      return var1;
   }

   public static Color convertFromIntArgb(int var0) {
      int var1 = var0 >> 24 & 0xFF;
      int var2 = var0 >> 16 & 0xFF;
      int var3 = var0 >> 8 & 0xFF;
      int var4 = var0 & 0xFF;
      return new Color((double)var2 / 255.0, (double)var3 / 255.0, (double)var4 / 255.0, (double)var1 / 255.0);
   }

   protected static WritableImage convertFromPixels(WritableImage var0, Pixels var1) {
      Objects.requireNonNull(var1);
      int var2 = var1.getWidth();
      int var3 = var1.getHeight();
      if (var0 == null || var0.getWidth() != (double)var2 || var0.getHeight() != (double)var3) {
         var0 = new WritableImage(var2, var3);
      }

      int var4 = var1.getBytesPerComponent();
      if (var4 == 4) {
         IntBuffer var5 = (IntBuffer)var1.getPixels();
         writeIntBufferToImage(var5, var0);
      } else {
         if (var4 != 1) {
            throw new IllegalArgumentException("bytesPerComponent must be either 4 or 1 but was: " + var4);
         }

         ByteBuffer var6 = (ByteBuffer)var1.getPixels();
         writeByteBufferToImage(var6, var0);
      }

      return var0;
   }

   private static void writeIntBufferToImage(IntBuffer var0, WritableImage var1) {
      Objects.requireNonNull(var1);
      PixelWriter var2 = var1.getPixelWriter();
      double var3 = var1.getWidth();
      double var5 = var1.getHeight();

      for (int var7 = 0; (double)var7 < var5; var7++) {
         for (int var8 = 0; (double)var8 < var3; var8++) {
            int var9 = var0.get();
            var2.setArgb(var8, var7, var9);
         }
      }
   }

   private static void writeByteBufferToImage(ByteBuffer var0, WritableImage var1) {
      Objects.requireNonNull(var1);
      PixelWriter var2 = var1.getPixelWriter();
      double var3 = var1.getWidth();
      double var5 = var1.getHeight();
      int var7 = Pixels.getNativeFormat();

      for (int var8 = 0; (double)var8 < var5; var8++) {
         for (int var9 = 0; (double)var9 < var3; var9++) {
            if (var7 == 1) {
               var2.setArgb(var9, var8, PixelUtils.PretoNonPre(bgraPreToRgbaPre(var0.getInt())));
            } else {
               if (var7 != 2) {
                  throw new IllegalArgumentException("format must be either BYTE_BGRA_PRE or BYTE_ARGB");
               }

               var2.setArgb(var9, var8, var0.getInt());
            }
         }
      }
   }

   private static int bgraPreToRgbaPre(int var0) {
      return Integer.reverseBytes(var0);
   }

   private static int interp(int[] var0, int var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = 256 - var5;
      int var8 = 256 - var6;
      int var9 = var2 * var3 + var1;
      int var10 = var1 >= 0 && var2 >= 0 && var1 < var3 && var2 < var4 ? var0[var9] : 0;
      if (var6 == 0) {
         if (var5 == 0) {
            return var10;
         } else {
            int var15 = var2 >= 0 && var1 + 1 < var3 && var2 < var4 ? var0[var9 + 1] : 0;
            return interp(var10, var15, var7, var5);
         }
      } else if (var5 == 0) {
         int var14 = var1 >= 0 && var1 < var3 && var2 + 1 < var4 ? var0[var9 + var3] : 0;
         return interp(var10, var14, var8, var6);
      } else {
         int var11 = var2 >= 0 && var1 + 1 < var3 && var2 < var4 ? var0[var9 + 1] : 0;
         int var12 = var1 >= 0 && var1 < var3 && var2 + 1 < var4 ? var0[var9 + var3] : 0;
         int var13 = var1 + 1 < var3 && var2 + 1 < var4 ? var0[var9 + var3 + 1] : 0;
         return interp(interp(var10, var11, var7, var5), interp(var12, var13, var7, var5), var8, var6);
      }
   }

   private static int interp(int var0, int var1, int var2, int var3) {
      int var4 = var0 >> 24 & 0xFF;
      int var5 = var0 >> 16 & 0xFF;
      int var6 = var0 >> 8 & 0xFF;
      int var7 = var0 & 0xFF;
      int var8 = var1 >> 24 & 0xFF;
      int var9 = var1 >> 16 & 0xFF;
      int var10 = var1 >> 8 & 0xFF;
      int var11 = var1 & 0xFF;
      int var12 = var4 * var2 + var8 * var3 >> 8;
      int var13 = var5 * var2 + var9 * var3 >> 8;
      int var14 = var6 * var2 + var10 * var3 >> 8;
      int var15 = var7 * var2 + var11 * var3 >> 8;
      return var12 << 24 | var13 << 16 | var14 << 8 | var15;
   }
}
