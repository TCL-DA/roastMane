package com.sun.javafx.webkit.prism;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.prism.Graphics;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.RTTexture;
import com.sun.prism.ResourceFactory;
import com.sun.prism.ResourceFactoryListener;
import com.sun.prism.Texture.WrapMode;
import com.sun.webkit.graphics.WCCamera;
import com.sun.webkit.graphics.WCGraphicsContext;
import com.sun.webkit.graphics.WCGraphicsManager;
import com.sun.webkit.graphics.WCPageBackBuffer;
import java.lang.ref.WeakReference;

final class WCPageBackBufferImpl extends WCPageBackBuffer implements ResourceFactoryListener {
   private RTTexture texture;
   private WeakReference<ResourceFactory> registeredWithFactory = null;
   private boolean firstValidate = true;
   private float pixelScale;
   private static final PlatformLogger log = PlatformLogger.getLogger(WCPageBackBufferImpl.class.getName());

   WCPageBackBufferImpl(float var1) {
      this.pixelScale = var1;
   }

   private static RTTexture createTexture(int var0, int var1) {
      return GraphicsPipeline.getDefaultResourceFactory().createRTTexture(var0, var1, WrapMode.CLAMP_NOT_NEEDED);
   }

   @Override
   public WCGraphicsContext createGraphics() {
      Graphics var1 = this.texture.createGraphics();
      var1.setCamera(WCCamera.INSTANCE);
      var1.scale(this.pixelScale, this.pixelScale);
      return WCGraphicsManager.getGraphicsManager().createGraphicsContext(var1);
   }

   @Override
   public void disposeGraphics(WCGraphicsContext var1) {
      var1.dispose();
   }

   @Override
   public void flush(WCGraphicsContext var1, int var2, int var3, int var4, int var5) {
      int var6 = var2 + var4;
      int var7 = var3 + var5;
      ((Graphics)var1.getPlatformGraphics())
         .drawTexture(
            this.texture,
            (float)var2,
            (float)var3,
            (float)var6,
            (float)var7,
            (float)var2 * this.pixelScale,
            (float)var3 * this.pixelScale,
            (float)var6 * this.pixelScale,
            (float)var7 * this.pixelScale
         );
      this.texture.unlock();
   }

   @Override
   protected void copyArea(int var1, int var2, int var3, int var4, int var5, int var6) {
      var1 = (int)((float)var1 * this.pixelScale);
      var2 = (int)((float)var2 * this.pixelScale);
      var3 = (int)Math.ceil((double)((float)var3 * this.pixelScale));
      var4 = (int)Math.ceil((double)((float)var4 * this.pixelScale));
      var5 = (int)((float)var5 * this.pixelScale);
      var6 = (int)((float)var6 * this.pixelScale);
      RTTexture var7 = createTexture(var3, var4);
      var7.createGraphics()
         .drawTexture(this.texture, 0.0F, 0.0F, (float)var3, (float)var4, (float)var1, (float)var2, (float)(var1 + var3), (float)(var2 + var4));
      this.texture
         .createGraphics()
         .drawTexture(
            var7, (float)(var1 + var5), (float)(var2 + var6), (float)(var1 + var3 + var5), (float)(var2 + var4 + var6), 0.0F, 0.0F, (float)var3, (float)var4
         );
      var7.dispose();
   }

   @Override
   public boolean validate(int var1, int var2) {
      ResourceFactory var3 = GraphicsPipeline.getDefaultResourceFactory();
      if (var3 != null && !var3.isDisposed()) {
         var1 = (int)Math.ceil((double)((float)var1 * this.pixelScale));
         var2 = (int)Math.ceil((double)((float)var2 * this.pixelScale));
         if (this.texture != null) {
            this.texture.lock();
            if (this.texture.isSurfaceLost()) {
               this.texture.dispose();
               this.texture = null;
            }
         }

         if (this.texture == null) {
            this.texture = createTexture(var1, var2);
            this.texture.contentsUseful();
            if (this.registeredWithFactory == null || this.registeredWithFactory.get() != var3) {
               var3.addFactoryListener(this);
               this.registeredWithFactory = new WeakReference<>(var3);
            }

            if (!this.firstValidate) {
               this.texture.unlock();
               return false;
            }

            this.firstValidate = false;
         } else {
            int var4 = this.texture.getContentWidth();
            int var5 = this.texture.getContentHeight();
            if (var4 != var1 || var5 != var2) {
               RTTexture var6 = createTexture(var1, var2);
               var6.contentsUseful();
               var6.createGraphics().drawTexture(this.texture, 0.0F, 0.0F, (float)Math.min(var1, var4), (float)Math.min(var2, var5));
               this.texture.dispose();
               this.texture = var6;
            }
         }

         return true;
      } else {
         log.fine("WCPageBackBufferImpl::validate : device disposed or not ready");
         return false;
      }
   }

   public void factoryReset() {
      if (this.texture != null) {
         this.texture.dispose();
         this.texture = null;
      }
   }

   public void factoryReleased() {
      log.fine("WCPageBackBufferImpl: resource factory released");
      if (this.texture != null) {
         this.texture.dispose();
         this.texture = null;
      }
   }
}
