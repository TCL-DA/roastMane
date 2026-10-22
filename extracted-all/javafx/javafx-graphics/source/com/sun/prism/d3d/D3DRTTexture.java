package com.sun.prism.d3d;

import com.sun.glass.ui.Screen;
import com.sun.prism.Graphics;
import com.sun.prism.Image;
import com.sun.prism.PixelFormat;
import com.sun.prism.RTTexture;
import com.sun.prism.ReadbackRenderTarget;
import com.sun.prism.Texture;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

class D3DRTTexture extends D3DTexture implements D3DRenderTarget, RTTexture, ReadbackRenderTarget {
   private boolean opaque = false;

   D3DRTTexture(D3DContext var1, Texture.WrapMode var2, long var3, int var5, int var6, int var7, int var8) {
      super(var1, PixelFormat.INT_ARGB_PRE, var2, var3, var5, var6, var7, var8, true);
   }

   D3DRTTexture(D3DContext var1, Texture.WrapMode var2, long var3, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
      super(var1, PixelFormat.INT_ARGB_PRE, var2, var3, var5, var6, var7, var8, var9, var10, true, var11, false);
   }

   @Override
   public Texture getBackBuffer() {
      return this;
   }

   @Override
   public long getResourceHandle() {
      return this.resource.getResource().getResource();
   }

   @Override
   public Graphics createGraphics() {
      return D3DGraphics.create(this, this.getContext());
   }

   @Override
   public int[] getPixels() {
      return null;
   }

   @Override
   public boolean readPixels(Buffer var1, int var2, int var3, int var4, int var5) {
      if (var2 == this.getContentX() && var3 == this.getContentY() && var4 == this.getContentWidth() && var5 == this.getContentHeight()) {
         return this.readPixels(var1);
      } else {
         throw new IllegalArgumentException("reading subtexture not yet supported!");
      }
   }

   @Override
   public boolean readPixels(Buffer var1) {
      D3DContext var2 = this.getContext();
      if (var2.isDisposed()) {
         return false;
      } else {
         var2.flushVertexBuffer();
         long var3 = this.getContext().getContextHandle();
         int var5 = 0;
         if (var1 instanceof ByteBuffer) {
            ByteBuffer var6 = (ByteBuffer)var1;
            byte[] var7 = var6.hasArray() ? var6.array() : null;
            long var8 = (long)var6.capacity();
            var5 = D3DResourceFactory.nReadPixelsB(var3, this.getNativeSourceHandle(), var8, var1, var7, this.getContentWidth(), this.getContentHeight());
         } else {
            if (!(var1 instanceof IntBuffer)) {
               throw new IllegalArgumentException("Buffer of this type is not supported: " + var1);
            }

            IntBuffer var11 = (IntBuffer)var1;
            int[] var12 = var11.hasArray() ? var11.array() : null;
            long var13 = (long)(var11.capacity() * 4);
            var5 = D3DResourceFactory.nReadPixelsI(var3, this.getNativeSourceHandle(), var13, var1, var12, this.getContentWidth(), this.getContentHeight());
         }

         return var2.validatePresent(var5);
      }
   }

   @Override
   public Screen getAssociatedScreen() {
      return this.getContext().getAssociatedScreen();
   }

   @Override
   public void update(Image var1) {
      throw new UnsupportedOperationException("update() not supported for RTTextures");
   }

   @Override
   public void update(Image var1, int var2, int var3) {
      throw new UnsupportedOperationException("update() not supported for RTTextures");
   }

   @Override
   public void update(Image var1, int var2, int var3, int var4, int var5) {
      throw new UnsupportedOperationException("update() not supported for RTTextures");
   }

   @Override
   public void update(Image var1, int var2, int var3, int var4, int var5, boolean var6) {
      throw new UnsupportedOperationException("update() not supported for RTTextures");
   }

   @Override
   public void update(Buffer var1, PixelFormat var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10) {
      throw new UnsupportedOperationException("update() not supported for RTTextures");
   }

   @Override
   public void setOpaque(boolean var1) {
      this.opaque = var1;
   }

   @Override
   public boolean isOpaque() {
      return this.opaque;
   }

   @Override
   public boolean isVolatile() {
      return this.getContext().isRTTVolatile();
   }

   @Override
   public boolean isMSAA() {
      return this.resource.getResource().getSamples() != 0;
   }
}
