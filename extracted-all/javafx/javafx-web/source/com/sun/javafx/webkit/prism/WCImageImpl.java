package com.sun.javafx.webkit.prism;

import com.sun.javafx.iio.ImageFrame;
import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.PrinterGraphics;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import com.sun.prism.Texture.Usage;
import com.sun.prism.Texture.WrapMode;
import com.sun.prism.image.CompoundCoords;
import com.sun.prism.image.CompoundTexture;
import com.sun.prism.image.Coords;
import com.sun.prism.image.ViewPort;
import java.nio.ByteBuffer;
import javafx.scene.image.PixelFormat;

final class WCImageImpl extends PrismImage {
   private static final PlatformLogger log = PlatformLogger.getLogger(WCImageImpl.class.getName());
   private final Image img;
   private Texture texture;
   private CompoundTexture compoundTexture;

   WCImageImpl(int var1, int var2) {
      if (log.isLoggable(Level.FINE)) {
         log.fine("Creating empty image({0},{1})", new Object[]{var1, var2});
      }

      this.img = Image.fromIntArgbPreData(new int[var1 * var2], var1, var2);
   }

   WCImageImpl(int[] var1, int var2, int var3) {
      if (log.isLoggable(Level.FINE)) {
         log.fine("Creating image({0},{1}) from buffer", new Object[]{var2, var3});
      }

      this.img = Image.fromIntArgbPreData(var1, var2, var3);
   }

   WCImageImpl(ImageFrame var1) {
      if (log.isLoggable(Level.FINE)) {
         log.fine("Creating image {0}x{1} of type {2} from buffer", new Object[]{var1.getWidth(), var1.getHeight(), var1.getImageType()});
      }

      this.img = Image.convertImageFrame(var1);
   }

   @Override
   Image getImage() {
      return this.img;
   }

   @Override
   Graphics getGraphics() {
      return null;
   }

   @Override
   void draw(Graphics var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      ResourceFactory var10 = var1.getResourceFactory();
      if (var10.isDisposed()) {
         log.fine("WCImageImpl::draw : skip because device disposed or not ready");
      } else if (var1 instanceof PrinterGraphics) {
         Texture var15 = var10.createTexture(this.img, Usage.STATIC, WrapMode.CLAMP_NOT_NEEDED);
         var1.drawTexture(var15, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7, (float)var8, (float)var9);
         var15.dispose();
      } else {
         if (this.texture != null) {
            this.texture.lock();
            if (this.texture.isSurfaceLost()) {
               this.texture = null;
            }
         }

         if (this.texture == null && this.compoundTexture == null) {
            int var11 = var10.getMaximumTextureSize();
            if (this.img.getWidth() <= var11 && this.img.getHeight() <= var11) {
               this.texture = var10.createTexture(this.img, Usage.DEFAULT, WrapMode.CLAMP_TO_EDGE);

               assert this.texture != null;
            } else {
               this.compoundTexture = new CompoundTexture(this.img, var11);
            }
         }

         if (this.texture != null) {
            assert this.compoundTexture == null;

            var1.drawTexture(this.texture, (float)var2, (float)var3, (float)var4, (float)var5, (float)var6, (float)var7, (float)var8, (float)var9);
            this.texture.unlock();
         } else {
            assert this.compoundTexture != null;

            ViewPort var14 = new ViewPort((float)var6, (float)var7, (float)(var8 - var6), (float)(var9 - var7));
            Coords var12 = new Coords((float)(var4 - var2), (float)(var5 - var3), var14);
            CompoundCoords var13 = new CompoundCoords(this.compoundTexture, var12);
            var13.draw(var1, this.compoundTexture, (float)var2, (float)var3);
         }
      }
   }

   @Override
   void dispose() {
      if (this.texture != null) {
         this.texture.dispose();
         this.texture = null;
      }

      if (this.compoundTexture != null) {
         this.compoundTexture.dispose();
         this.compoundTexture = null;
      }
   }

   @Override
   public int getWidth() {
      return this.img.getWidth();
   }

   @Override
   public int getHeight() {
      return this.img.getHeight();
   }

   @Override
   public ByteBuffer getPixelBuffer() {
      int var1 = this.img.getWidth();
      int var2 = this.img.getHeight();
      int var3 = var1 * 4;
      ByteBuffer var4 = ByteBuffer.allocate(var3 * var2);
      this.img.getPixels(0, 0, var1, var2, PixelFormat.getByteBgraInstance(), var4, var3);
      return var4;
   }

   @Override
   public float getPixelScale() {
      return this.img.getPixelScale();
   }
}
