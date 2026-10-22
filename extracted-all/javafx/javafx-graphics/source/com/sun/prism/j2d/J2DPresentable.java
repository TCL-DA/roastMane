package com.sun.prism.j2d;

import com.sun.glass.ui.Pixels;
import com.sun.glass.ui.Screen;
import com.sun.javafx.geom.Rectangle;
import com.sun.prism.Graphics;
import com.sun.prism.Presentable;
import com.sun.prism.PresentableState;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.impl.PrismSettings;
import com.sun.prism.impl.QueuedPixelSource;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public abstract class J2DPresentable implements Presentable {
   J2DResourceFactory factory;
   boolean needsResize;
   BufferedImage buffer;
   IntBuffer ib;
   J2DRTTexture readbackBuffer;

   static J2DPresentable create(PresentableState var0, J2DResourceFactory var1) {
      return new J2DPresentable.Glass(var0, var1);
   }

   static J2DPresentable create(BufferedImage var0, J2DResourceFactory var1) {
      return new J2DPresentable.Bimg(var0, var1);
   }

   J2DPresentable(BufferedImage var1, J2DResourceFactory var2) {
      this.buffer = var1;
      this.factory = var2;
   }

   ResourceFactory getResourceFactory() {
      return this.factory;
   }

   public abstract BufferedImage createBuffer(int var1, int var2);

   @Override
   public Graphics createGraphics() {
      if (this.needsResize) {
         int var1 = this.getContentWidth();
         int var2 = this.getContentHeight();
         this.buffer = null;
         this.readbackBuffer = null;
         this.buffer = this.createBuffer(var1, var2);
         WritableRaster var3 = this.buffer.getRaster();
         DataBuffer var4 = var3.getDataBuffer();
         SinglePixelPackedSampleModel var5 = (SinglePixelPackedSampleModel)var3.getSampleModel();
         int[] var6 = ((DataBufferInt)var4).getData();
         this.ib = IntBuffer.wrap(var6, var4.getOffset(), var4.getSize());
         this.needsResize = false;
      }

      Graphics2D var7 = this.buffer.createGraphics();
      return this.factory.createJ2DPrismGraphics(this, var7);
   }

   J2DRTTexture getReadbackBuffer() {
      if (this.readbackBuffer == null) {
         this.readbackBuffer = (J2DRTTexture)this.factory.createRTTexture(this.getContentWidth(), this.getContentHeight(), Texture.WrapMode.CLAMP_NOT_NEEDED);
         this.readbackBuffer.makePermanent();
      }

      return this.readbackBuffer;
   }

   BufferedImage getBackBuffer() {
      return this.buffer;
   }

   @Override
   public Screen getAssociatedScreen() {
      return this.factory.getScreen();
   }

   @Override
   public int getContentX() {
      return 0;
   }

   @Override
   public int getContentY() {
      return 0;
   }

   @Override
   public float getPixelScaleFactorX() {
      return 1.0F;
   }

   @Override
   public float getPixelScaleFactorY() {
      return 1.0F;
   }

   @Override
   public int getPhysicalWidth() {
      return this.buffer == null ? this.getContentWidth() : this.buffer.getWidth();
   }

   @Override
   public int getPhysicalHeight() {
      return this.buffer == null ? this.getContentHeight() : this.buffer.getHeight();
   }

   @Override
   public boolean isMSAA() {
      return false;
   }

   private static class Bimg extends J2DPresentable {
      private boolean opaque;

      public Bimg(BufferedImage var1, J2DResourceFactory var2) {
         super(var1, var2);
      }

      @Override
      public BufferedImage createBuffer(int var1, int var2) {
         throw new UnsupportedOperationException("cannot create new buffers for image");
      }

      @Override
      public boolean lockResources(PresentableState var1) {
         return false;
      }

      @Override
      public boolean prepare(Rectangle var1) {
         throw new UnsupportedOperationException("cannot prepare/present on image");
      }

      @Override
      public boolean present() {
         throw new UnsupportedOperationException("cannot prepare/present on image");
      }

      @Override
      public int getContentWidth() {
         return this.buffer.getWidth();
      }

      @Override
      public int getContentHeight() {
         return this.buffer.getHeight();
      }

      @Override
      public void setOpaque(boolean var1) {
         this.opaque = var1;
      }

      @Override
      public boolean isOpaque() {
         return this.opaque;
      }
   }

   private static class Glass extends J2DPresentable {
      private final PresentableState pState;
      private final int theFormat;
      private Pixels pixels;
      private QueuedPixelSource pixelSource = new QueuedPixelSource(false);
      private boolean opaque;

      Glass(PresentableState var1, J2DResourceFactory var2) {
         super(null, var2);
         this.pState = var1;
         this.theFormat = var1.getPixelFormat();
         this.needsResize = true;
      }

      @Override
      public BufferedImage createBuffer(int var1, int var2) {
         if (PrismSettings.verbose) {
            System.out.println("Glass native format: " + this.theFormat);
         }

         ByteOrder var3 = ByteOrder.nativeOrder();
         switch (this.theFormat) {
            case 1:
               if (var3 == ByteOrder.LITTLE_ENDIAN) {
                  return new BufferedImage(var1, var2, 3);
               }

               throw new UnsupportedOperationException("BYTE_BGRA_PRE pixel format on BIG_ENDIAN");
            case 2:
               if (var3 == ByteOrder.BIG_ENDIAN) {
                  return new BufferedImage(var1, var2, 2);
               }

               throw new UnsupportedOperationException("BYTE_ARGB pixel format on LITTLE_ENDIAN");
            default:
               throw new UnsupportedOperationException("unrecognized pixel format: " + this.theFormat);
         }
      }

      @Override
      public boolean lockResources(PresentableState var1) {
         if (this.pState == var1 && this.theFormat == var1.getPixelFormat()) {
            this.needsResize = this.buffer == null || this.buffer.getWidth() != var1.getWidth() || this.buffer.getHeight() != var1.getHeight();
            return false;
         } else {
            return true;
         }
      }

      @Override
      public boolean prepare(Rectangle var1) {
         if (!this.pState.isViewClosed()) {
            int var2 = this.getPhysicalWidth();
            int var3 = this.getPhysicalHeight();
            this.pixels = this.pixelSource.getUnusedPixels(var2, var3, 1.0F, 1.0F);
            IntBuffer var4 = (IntBuffer)this.pixels.getPixels();

            assert this.ib.hasArray();

            System.arraycopy(this.ib.array(), 0, var4.array(), 0, var2 * var3);
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean present() {
         this.pixelSource.enqueuePixels(this.pixels);
         this.pState.uploadPixels(this.pixelSource);
         return true;
      }

      @Override
      public int getContentWidth() {
         return this.pState.getWidth();
      }

      @Override
      public int getContentHeight() {
         return this.pState.getHeight();
      }

      @Override
      public void setOpaque(boolean var1) {
         this.opaque = var1;
      }

      @Override
      public boolean isOpaque() {
         return this.opaque;
      }
   }
}
