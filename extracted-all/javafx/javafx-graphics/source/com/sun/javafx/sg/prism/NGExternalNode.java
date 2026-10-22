package com.sun.javafx.sg.prism;

import com.sun.javafx.geom.Rectangle;
import com.sun.prism.Graphics;
import com.sun.prism.PixelFormat;
import com.sun.prism.ResourceFactory;
import com.sun.prism.Texture;
import java.nio.Buffer;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class NGExternalNode extends NGNode {
   private Texture dsttexture;
   private NGExternalNode.BufferData bufferData;
   private final AtomicReference<NGExternalNode.RenderData> renderData = new AtomicReference<>(null);
   private NGExternalNode.RenderData rd;
   private volatile ReentrantLock bufferLock;

   @Override
   protected void renderContent(Graphics var1) {
      NGExternalNode.RenderData var2 = this.renderData.getAndSet(null);
      if (var2 != null) {
         this.rd = var2;
      }

      if (this.rd != null) {
         int var3 = this.rd.bdata.srcbounds.x;
         int var4 = this.rd.bdata.srcbounds.y;
         int var5 = this.rd.bdata.srcbounds.width;
         int var6 = this.rd.bdata.srcbounds.height;
         if (this.dsttexture != null) {
            this.dsttexture.lock();
            if (this.dsttexture.isSurfaceLost() || this.dsttexture.getContentWidth() != var5 || this.dsttexture.getContentHeight() != var6) {
               this.dsttexture.unlock();
               this.dsttexture.dispose();
               this.rd = this.rd.copyAddDirtyRect(0, 0, var5, var6);
               this.dsttexture = this.createTexture(var1, this.rd);
            }
         } else {
            this.dsttexture = this.createTexture(var1, this.rd);
         }

         if (this.dsttexture != null) {
            try {
               if (var2 != null) {
                  this.bufferLock.lock();

                  try {
                     this.dsttexture
                        .update(
                           this.rd.bdata.srcbuffer,
                           PixelFormat.INT_ARGB_PRE,
                           this.rd.dirtyRect.x,
                           this.rd.dirtyRect.y,
                           var3 + this.rd.dirtyRect.x,
                           var4 + this.rd.dirtyRect.y,
                           this.rd.dirtyRect.width,
                           this.rd.dirtyRect.height,
                           this.rd.bdata.linestride * 4,
                           false
                        );
                  } finally {
                     this.bufferLock.unlock();
                  }

                  if (this.rd.clearTarget) {
                     var1.clearQuad(0.0F, 0.0F, this.rd.bdata.usrwidth, this.rd.bdata.usrheight);
                  }
               }

               var1.drawTexture(this.dsttexture, 0.0F, 0.0F, this.rd.bdata.usrwidth, this.rd.bdata.usrheight, 0.0F, 0.0F, (float)var5, (float)var6);
            } finally {
               this.dsttexture.unlock();
            }
         }
      }
   }

   private Texture createTexture(Graphics var1, NGExternalNode.RenderData var2) {
      ResourceFactory var3 = var1.getResourceFactory();
      if (var3.isDisposed()) {
         return null;
      } else {
         Texture var4 = var3.createTexture(
            PixelFormat.INT_ARGB_PRE, Texture.Usage.DYNAMIC, Texture.WrapMode.CLAMP_NOT_NEEDED, var2.bdata.srcbounds.width, var2.bdata.srcbounds.height
         );
         if (var4 != null) {
            var4.contentsUseful();
         } else {
            System.err.println("NGExternalNode: failed to create a texture");
         }

         return var4;
      }
   }

   public void setLock(ReentrantLock var1) {
      this.bufferLock = var1;
   }

   public void setImageBuffer(Buffer var1, int var2, int var3, int var4, int var5, float var6, float var7, int var8, double var9, double var11) {
      this.bufferData = new NGExternalNode.BufferData(var1, var8, var2, var3, var4, var5, var6, var7, var9, var11);
      this.renderData.set(new NGExternalNode.RenderData(this.bufferData, var2, var3, var4, var5, true));
   }

   public void setImageBounds(int var1, int var2, int var3, int var4, float var5, float var6) {
      boolean var7 = (float)var3 < this.bufferData.usrwidth || (float)var4 < this.bufferData.usrheight;
      this.bufferData = this.bufferData.copyWithBounds(var1, var2, var3, var4, var5, var6);
      this.renderData.updateAndGet(var6x -> {
         boolean var7x = var6x != null ? var6x.clearTarget : false;
         return new NGExternalNode.RenderData(this.bufferData, var1, var2, var3, var4, var7x | var7);
      });
   }

   public void repaintDirtyRegion(int var1, int var2, int var3, int var4) {
      this.renderData
         .updateAndGet(
            var5 -> var5 != null
                  ? var5.copyAddDirtyRect(var1, var2, var3, var4)
                  : new NGExternalNode.RenderData(this.bufferData, var1, var2, var3, var4, false)
         );
   }

   public void markContentDirty() {
      this.visualsChanged();
   }

   @Override
   protected boolean hasOverlappingContents() {
      return false;
   }

   private static class BufferData {
      final Buffer srcbuffer;
      final int linestride;
      final Rectangle srcbounds;
      final float usrwidth;
      final float usrheight;
      final double scaleX;
      final double scaleY;

      BufferData(Buffer var1, int var2, int var3, int var4, int var5, int var6, float var7, float var8, double var9, double var11) {
         this.srcbuffer = var1;
         this.scaleX = var9;
         this.scaleY = var11;
         this.linestride = var2;
         this.srcbounds = this.scale(new Rectangle(var3, var4, var5, var6));
         this.usrwidth = var7;
         this.usrheight = var8;
      }

      Rectangle scale(Rectangle var1) {
         int var2 = var1.x;
         var1.x = (int)Math.round((double)var2 * this.scaleX);
         int var3 = var1.y;
         var1.y = (int)Math.round((double)var3 * this.scaleY);
         var1.width = (int)Math.round((double)var1.width * this.scaleX);
         var1.height = (int)Math.round((double)var1.height * this.scaleY);
         return var1;
      }

      NGExternalNode.BufferData copyWithBounds(int var1, int var2, int var3, int var4, float var5, float var6) {
         return new NGExternalNode.BufferData(this.srcbuffer, this.linestride, var1, var2, var3, var4, var5, var6, this.scaleX, this.scaleY);
      }
   }

   private static class RenderData {
      final NGExternalNode.BufferData bdata;
      final Rectangle dirtyRect;
      final boolean clearTarget;

      RenderData(NGExternalNode.BufferData var1, int var2, int var3, int var4, int var5, boolean var6) {
         this(var1, var2, var3, var4, var5, var6, true);
      }

      RenderData(NGExternalNode.BufferData var1, int var2, int var3, int var4, int var5, boolean var6, boolean var7) {
         this.bdata = var1;
         Rectangle var8 = new Rectangle(var2, var3, var4, var5);
         this.dirtyRect = var7 ? var1.scale(var8) : var8;
         this.dirtyRect.intersectWith(var1.srcbounds);
         this.clearTarget = var6;
      }

      NGExternalNode.RenderData copyAddDirtyRect(int var1, int var2, int var3, int var4) {
         Rectangle var5 = this.bdata.scale(new Rectangle(var1, var2, var3, var4));
         var5.add(this.dirtyRect);
         return new NGExternalNode.RenderData(this.bdata, var5.x, var5.y, var5.width, var5.height, this.clearTarget, false);
      }
   }
}
