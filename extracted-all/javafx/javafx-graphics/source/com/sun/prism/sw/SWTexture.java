package com.sun.prism.sw;

import com.sun.prism.Image;
import com.sun.prism.PixelFormat;
import com.sun.prism.Texture;
import com.sun.prism.impl.PrismSettings;

abstract class SWTexture implements Texture {
   boolean allocated = false;
   int physicalWidth;
   int physicalHeight;
   int contentWidth;
   int contentHeight;
   private SWResourceFactory factory;
   private int lastImageSerial;
   private final Texture.WrapMode wrapMode;
   private boolean linearFiltering = true;
   private int lockcount;
   boolean permanent;
   int employcount;

   static Texture create(SWResourceFactory var0, PixelFormat var1, Texture.WrapMode var2, int var3, int var4) {
      switch (var1) {
         case BYTE_ALPHA:
            return new SWMaskTexture(var0, var2, var3, var4);
         default:
            return new SWArgbPreTexture(var0, var2, var3, var4);
      }
   }

   SWTexture(SWResourceFactory var1, Texture.WrapMode var2, int var3, int var4) {
      this.factory = var1;
      this.wrapMode = var2;
      this.physicalWidth = var3;
      this.physicalHeight = var4;
      this.contentWidth = var3;
      this.contentHeight = var4;
      this.lock();
   }

   SWTexture(SWTexture var1, Texture.WrapMode var2) {
      this.allocated = var1.allocated;
      this.physicalWidth = var1.physicalWidth;
      this.physicalHeight = var1.physicalHeight;
      this.contentWidth = var1.contentWidth;
      this.contentHeight = var1.contentHeight;
      this.factory = var1.factory;
      this.lastImageSerial = var1.lastImageSerial;
      this.linearFiltering = var1.linearFiltering;
      this.wrapMode = var2;
      this.lock();
   }

   SWResourceFactory getResourceFactory() {
      return this.factory;
   }

   int getOffset() {
      return 0;
   }

   @Override
   public void lock() {
      this.lockcount++;
   }

   @Override
   public void unlock() {
      this.assertLocked();
      this.lockcount--;
   }

   @Override
   public boolean isLocked() {
      return this.lockcount > 0;
   }

   @Override
   public int getLockCount() {
      return this.lockcount;
   }

   @Override
   public void assertLocked() {
      if (this.lockcount <= 0) {
         throw new IllegalStateException("texture not locked");
      }
   }

   @Override
   public void makePermanent() {
      this.permanent = true;
   }

   @Override
   public void contentsUseful() {
      this.assertLocked();
      this.employcount++;
   }

   @Override
   public void contentsNotUseful() {
      if (this.employcount <= 0) {
         throw new IllegalStateException("Resource obsoleted too many times");
      } else {
         this.employcount--;
      }
   }

   @Override
   public boolean isSurfaceLost() {
      return false;
   }

   @Override
   public void dispose() {
   }

   @Override
   public int getPhysicalWidth() {
      return this.physicalWidth;
   }

   @Override
   public int getPhysicalHeight() {
      return this.physicalHeight;
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
   public int getContentWidth() {
      return this.contentWidth;
   }

   @Override
   public void setContentWidth(int var1) {
      if (var1 > this.physicalWidth) {
         throw new IllegalArgumentException("contentWidth cannot exceed physicalWidth");
      } else {
         this.contentWidth = var1;
      }
   }

   @Override
   public int getContentHeight() {
      return this.contentHeight;
   }

   @Override
   public void setContentHeight(int var1) {
      if (var1 > this.physicalHeight) {
         throw new IllegalArgumentException("contentHeight cannot exceed physicalHeight");
      } else {
         this.contentHeight = var1;
      }
   }

   @Override
   public int getMaxContentWidth() {
      return this.getPhysicalWidth();
   }

   @Override
   public int getMaxContentHeight() {
      return this.getPhysicalHeight();
   }

   @Override
   public int getLastImageSerial() {
      return this.lastImageSerial;
   }

   @Override
   public void setLastImageSerial(int var1) {
      this.lastImageSerial = var1;
   }

   @Override
   public void update(Image var1) {
      this.update(var1, 0, 0);
   }

   @Override
   public void update(Image var1, int var2, int var3) {
      this.update(var1, var2, var3, var1.getWidth(), var1.getHeight());
   }

   @Override
   public void update(Image var1, int var2, int var3, int var4, int var5) {
      this.update(var1, var2, var3, var4, var5, false);
   }

   @Override
   public void update(Image var1, int var2, int var3, int var4, int var5, boolean var6) {
      if (PrismSettings.debug) {
         System.out.println("IMG.Bytes per pixel: " + var1.getBytesPerPixelUnit());
         System.out.println("IMG.scanline: " + var1.getScanlineStride());
      }

      this.update(var1.getPixelBuffer(), var1.getPixelFormat(), var2, var3, var1.getMinX(), var1.getMinY(), var4, var5, var1.getScanlineStride(), var6);
   }

   @Override
   public Texture.WrapMode getWrapMode() {
      return this.wrapMode;
   }

   @Override
   public boolean getUseMipmap() {
      return false;
   }

   @Override
   public Texture getSharedTexture(Texture.WrapMode var1) {
      this.assertLocked();
      if (this.wrapMode == var1) {
         this.lock();
         return this;
      } else {
         switch (var1) {
            case REPEAT:
               if (this.wrapMode != Texture.WrapMode.CLAMP_TO_EDGE) {
                  return null;
               }
               break;
            case CLAMP_TO_EDGE:
               if (this.wrapMode != Texture.WrapMode.REPEAT) {
                  return null;
               }
               break;
            default:
               return null;
         }

         return this.createSharedLockedTexture(var1);
      }
   }

   @Override
   public boolean getLinearFiltering() {
      return this.linearFiltering;
   }

   @Override
   public void setLinearFiltering(boolean var1) {
      this.linearFiltering = var1;
   }

   void allocate() {
      if (!this.allocated) {
         if (PrismSettings.debug) {
            System.out.println("PCS Texture allocating buffer: " + this + ", " + this.physicalWidth + "x" + this.physicalHeight);
         }

         this.allocateBuffer();
         this.allocated = true;
      }
   }

   abstract void allocateBuffer();

   abstract Texture createSharedLockedTexture(Texture.WrapMode var1);
}
