package com.sun.javafx.font.freetype;

import com.sun.javafx.font.Glyph;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.Shape;

class FTGlyph implements Glyph {
   FTFontStrike strike;
   int glyphCode;
   byte[] buffer;
   FT_Bitmap bitmap;
   int bitmap_left;
   int bitmap_top;
   float advanceX;
   float advanceY;
   float userAdvance;
   boolean lcd;

   FTGlyph(FTFontStrike var1, int var2, boolean var3) {
      this.strike = var1;
      this.glyphCode = var2;
   }

   @Override
   public int getGlyphCode() {
      return this.glyphCode;
   }

   private void init() {
      if (this.bitmap == null) {
         this.strike.initGlyph(this);
      }
   }

   @Override
   public RectBounds getBBox() {
      float[] var1 = new float[4];
      FTFontFile var2 = this.strike.getFontResource();
      var2.getGlyphBoundingBox(this.glyphCode, this.strike.getSize(), var1);
      return new RectBounds(var1[0], var1[1], var1[2], var1[3]);
   }

   @Override
   public float getAdvance() {
      this.init();
      return this.userAdvance;
   }

   @Override
   public Shape getShape() {
      return this.strike.createGlyphOutline(this.glyphCode);
   }

   @Override
   public byte[] getPixelData() {
      this.init();
      return this.buffer;
   }

   @Override
   public byte[] getPixelData(int var1) {
      this.init();
      return this.buffer;
   }

   @Override
   public float getPixelXAdvance() {
      this.init();
      return this.advanceX;
   }

   @Override
   public float getPixelYAdvance() {
      this.init();
      return this.advanceY;
   }

   @Override
   public int getWidth() {
      this.init();
      return this.bitmap != null ? this.bitmap.width : 0;
   }

   @Override
   public int getHeight() {
      this.init();
      return this.bitmap != null ? this.bitmap.rows : 0;
   }

   @Override
   public int getOriginX() {
      this.init();
      return this.bitmap_left;
   }

   @Override
   public int getOriginY() {
      this.init();
      return -this.bitmap_top;
   }

   @Override
   public boolean isLCDGlyph() {
      return this.lcd;
   }
}
