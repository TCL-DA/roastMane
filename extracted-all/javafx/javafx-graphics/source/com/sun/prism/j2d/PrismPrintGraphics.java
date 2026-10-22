package com.sun.prism.j2d;

import com.sun.javafx.geom.Rectangle;
import com.sun.prism.PresentableState;
import com.sun.prism.PrinterGraphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public final class PrismPrintGraphics extends J2DPrismGraphics implements PrinterGraphics {
   private AffineTransform origTx2D;

   @Override
   protected void setTransformG2D(AffineTransform var1) {
      this.g2d.setTransform(this.origTx2D);
      this.g2d.transform(var1);
   }

   @Override
   protected void captureTransform(Graphics2D var1) {
      this.origTx2D = var1.getTransform();
   }

   public PrismPrintGraphics(Graphics2D var1, int var2, int var3) {
      super(new PrismPrintGraphics.PagePresentable(var2, var3), var1);
      this.setClipRect(new Rectangle(0, 0, var2, var3));
   }

   PrismPrintGraphics(J2DPresentable var1, Graphics2D var2) {
      super(var1, var2);
   }

   static class PagePresentable extends J2DPresentable {
      private int width;
      private int height;
      static J2DResourceFactory factory = new PrismPrintGraphics.PrintResourceFactory();
      private boolean opaque;

      PagePresentable(int var1, int var2) {
         super(null, factory);
         this.width = var1;
         this.height = var2;
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
         throw new UnsupportedOperationException("Cannot prepare an image");
      }

      @Override
      public boolean present() {
         throw new UnsupportedOperationException("Cannot present on image");
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
      public void setOpaque(boolean var1) {
         this.opaque = var1;
      }

      @Override
      public boolean isOpaque() {
         return this.opaque;
      }
   }

   static class PrintResourceFactory extends J2DResourceFactory {
      PrintResourceFactory() {
         super(null);
      }

      @Override
      J2DPrismGraphics createJ2DPrismGraphics(J2DPresentable var1, Graphics2D var2) {
         PrismPrintGraphics var3 = new PrismPrintGraphics(var1, var2);
         Rectangle var4 = new Rectangle(0, 0, var1.getContentWidth(), var1.getContentHeight());
         var3.setClipRect(var4);
         return var3;
      }
   }
}
