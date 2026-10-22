package com.sun.prism;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public final class MultiTexture implements Texture {
   private int width;
   private int height;
   private PixelFormat format;
   private Texture.WrapMode wrapMode;
   private boolean linearFiltering = true;
   private final ArrayList<Texture> textures;
   private int lastImageSerial;

   public MultiTexture(PixelFormat var1, Texture.WrapMode var2, int var3, int var4) {
      this.width = var3;
      this.height = var4;
      this.format = var1;
      this.wrapMode = var2;
      this.textures = new ArrayList<>(4);
   }

   private MultiTexture(MultiTexture var1, Texture.WrapMode var2) {
      this(var1.format, var2, var1.width, var1.height);

      for (int var3 = 0; var3 < var1.textureCount(); var3++) {
         Texture var4 = var1.getTexture(var3);
         this.setTexture(var4.getSharedTexture(var2), var3);
      }

      this.linearFiltering = var1.linearFiltering;
      this.lastImageSerial = var1.lastImageSerial;
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

         MultiTexture var2 = new MultiTexture(this, var1);
         var2.lock();
         return var2;
      }
   }

   public int textureCount() {
      return this.textures.size();
   }

   public void setTexture(Texture var1, int var2) {
      if (!var1.getWrapMode().isCompatibleWith(this.wrapMode)) {
         throw new IllegalArgumentException("texture wrap mode must match multi-texture mode");
      } else {
         if (this.textures.size() < var2 + 1) {
            for (int var3 = this.textures.size(); var3 < var2; var3++) {
               this.textures.add(null);
            }

            this.textures.add(var1);
         } else {
            this.textures.set(var2, var1);
         }

         var1.setLinearFiltering(this.linearFiltering);
      }
   }

   public Texture getTexture(int var1) {
      return this.textures.get(var1);
   }

   public Texture[] getTextures() {
      return this.textures.toArray(new Texture[this.textures.size()]);
   }

   public void removeTexture(Texture var1) {
      this.textures.remove(var1);
   }

   public void removeTexture(int var1) {
      this.textures.remove(var1);
   }

   @Override
   public PixelFormat getPixelFormat() {
      return this.format;
   }

   @Override
   public int getPhysicalWidth() {
      return this.width;
   }

   @Override
   public int getPhysicalHeight() {
      return this.height;
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
      return this.width;
   }

   @Override
   public int getContentHeight() {
      return this.height;
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
      throw new UnsupportedOperationException("Update from Image not supported");
   }

   @Override
   public void update(Image var1, int var2, int var3) {
      throw new UnsupportedOperationException("Update from Image not supported");
   }

   @Override
   public void update(Image var1, int var2, int var3, int var4, int var5) {
      throw new UnsupportedOperationException("Update from Image not supported");
   }

   @Override
   public void update(Image var1, int var2, int var3, int var4, int var5, boolean var6) {
      throw new UnsupportedOperationException("Update from Image not supported");
   }

   @Override
   public void update(Buffer var1, PixelFormat var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, boolean var10) {
      throw new UnsupportedOperationException("Update from generic Buffer not supported");
   }

   @Override
   public void update(MediaFrame var1, boolean var2) {
      if (var1.getPixelFormat() != PixelFormat.MULTI_YCbCr_420) {
         throw new IllegalArgumentException("Invalid pixel format in MediaFrame");
      } else {
         int var4 = var1.getEncodedWidth();
         int var5 = var1.getEncodedHeight();

         for (int var6 = 0; var6 < var1.planeCount(); var6++) {
            Texture var3 = this.textures.get(var6);
            if (null != var3) {
               int var7 = var4;
               int var8 = var5;
               if (var6 == 2 || var6 == 1) {
                  var7 = var4 / 2;
                  var8 = var5 / 2;
               }

               ByteBuffer var9 = var1.getBufferForPlane(var6);
               var3.update(var9, PixelFormat.BYTE_ALPHA, 0, 0, 0, 0, var7, var8, var1.strideForPlane(var6), var2);
            }
         }
      }
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
   public boolean getLinearFiltering() {
      return this.linearFiltering;
   }

   @Override
   public void setLinearFiltering(boolean var1) {
      this.linearFiltering = var1;

      for (Texture var3 : this.textures) {
         var3.setLinearFiltering(var1);
      }
   }

   @Override
   public void lock() {
      for (Texture var2 : this.textures) {
         var2.lock();
      }
   }

   @Override
   public void unlock() {
      for (Texture var2 : this.textures) {
         var2.unlock();
      }
   }

   @Override
   public boolean isLocked() {
      for (Texture var2 : this.textures) {
         if (var2.isLocked()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public int getLockCount() {
      int var1 = 0;

      for (Texture var3 : this.textures) {
         var1 = Math.max(var1, var3.getLockCount());
      }

      return var1;
   }

   @Override
   public void assertLocked() {
      for (Texture var2 : this.textures) {
         var2.assertLocked();
      }
   }

   @Override
   public void makePermanent() {
      for (Texture var2 : this.textures) {
         var2.makePermanent();
      }
   }

   @Override
   public void contentsUseful() {
      for (Texture var2 : this.textures) {
         var2.contentsUseful();
      }
   }

   @Override
   public void contentsNotUseful() {
      for (Texture var2 : this.textures) {
         var2.contentsNotUseful();
      }
   }

   @Override
   public boolean isSurfaceLost() {
      for (Texture var2 : this.textures) {
         if (var2.isSurfaceLost()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void dispose() {
      for (Texture var2 : this.textures) {
         var2.dispose();
      }

      this.textures.clear();
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
   public void setContentWidth(int var1) {
      throw new UnsupportedOperationException("Not supported.");
   }

   @Override
   public void setContentHeight(int var1) {
      throw new UnsupportedOperationException("Not supported.");
   }
}
