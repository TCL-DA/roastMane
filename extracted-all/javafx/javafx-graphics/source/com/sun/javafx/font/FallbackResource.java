package com.sun.javafx.font;

import com.sun.javafx.geom.transform.BaseTransform;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class FallbackResource implements CompositeFontResource {
   private ArrayList<String> linkedFontFiles;
   private ArrayList<String> linkedFontNames;
   private FontResource[] fallbacks;
   private FontResource[] nativeFallbacks;
   private boolean isBold;
   private boolean isItalic;
   private int aaMode;
   private CompositeGlyphMapper mapper;
   Map<FontStrikeDesc, WeakReference<FontStrike>> strikeMap = new ConcurrentHashMap<>();
   static FallbackResource[] greyFallBackResource = new FallbackResource[4];
   static FallbackResource[] lcdFallBackResource = new FallbackResource[4];

   @Override
   public Map<FontStrikeDesc, WeakReference<FontStrike>> getStrikeMap() {
      return this.strikeMap;
   }

   FallbackResource(boolean var1, boolean var2, int var3) {
      this.isBold = var1;
      this.isItalic = var2;
      this.aaMode = var3;
   }

   static FallbackResource getFallbackResource(boolean var0, boolean var1, int var2) {
      FallbackResource[] var3 = var2 == 0 ? greyFallBackResource : lcdFallBackResource;
      int var4 = var0 ? 1 : 0;
      if (var1) {
         var4 += 2;
      }

      FallbackResource var5 = var3[var4];
      if (var5 == null) {
         var5 = new FallbackResource(var0, var1, var2);
         var3[var4] = var5;
      }

      return var5;
   }

   @Override
   public int getDefaultAAMode() {
      return this.aaMode;
   }

   private String throwException() {
      throw new UnsupportedOperationException("Not supported");
   }

   @Override
   public String getFullName() {
      return this.throwException();
   }

   @Override
   public String getPSName() {
      return this.throwException();
   }

   @Override
   public String getFamilyName() {
      return this.throwException();
   }

   @Override
   public String getStyleName() {
      return this.throwException();
   }

   @Override
   public String getLocaleFullName() {
      return this.throwException();
   }

   @Override
   public String getLocaleFamilyName() {
      return this.throwException();
   }

   @Override
   public String getLocaleStyleName() {
      return this.throwException();
   }

   @Override
   public boolean isBold() {
      throw new UnsupportedOperationException("Not supported");
   }

   @Override
   public boolean isItalic() {
      throw new UnsupportedOperationException("Not supported");
   }

   @Override
   public int getFeatures() {
      throw new UnsupportedOperationException("Not supported");
   }

   @Override
   public String getFileName() {
      return this.throwException();
   }

   @Override
   public Object getPeer() {
      return null;
   }

   @Override
   public void setPeer(Object var1) {
      this.throwException();
   }

   @Override
   public boolean isEmbeddedFont() {
      return false;
   }

   @Override
   public CharToGlyphMapper getGlyphMapper() {
      if (this.mapper == null) {
         this.mapper = new CompositeGlyphMapper(this);
      }

      return this.mapper;
   }

   @Override
   public int getSlotForFont(String var1) {
      this.getLinkedFonts();
      int var2 = 0;

      for (String var4 : this.linkedFontNames) {
         if (var1.equalsIgnoreCase(var4)) {
            return var2;
         }

         var2++;
      }

      if (this.nativeFallbacks != null) {
         for (FontResource var6 : this.nativeFallbacks) {
            if (var1.equalsIgnoreCase(var6.getFullName())) {
               return var2;
            }

            var2++;
         }
      }

      if (var2 >= 126) {
         if (PrismFontFactory.debugFonts) {
            System.err.println("\tToo many font fallbacks!");
         }

         return -1;
      } else {
         PrismFontFactory var8 = PrismFontFactory.getFontFactory();
         FontResource var10 = var8.getFontResource(var1, null, false);
         if (var10 == null) {
            if (PrismFontFactory.debugFonts) {
               System.err.println("\t Font name not supported \"" + var1 + "\".");
            }

            return -1;
         } else {
            FontResource[] var11;
            if (this.nativeFallbacks == null) {
               var11 = new FontResource[1];
            } else {
               var11 = new FontResource[this.nativeFallbacks.length + 1];
               System.arraycopy(this.nativeFallbacks, 0, var11, 0, this.nativeFallbacks.length);
            }

            var11[var11.length - 1] = var10;
            this.nativeFallbacks = var11;
            return var2;
         }
      }
   }

   private void getLinkedFonts() {
      if (this.fallbacks == null) {
         if (PrismFontFactory.isLinux) {
            FontConfigManager.FcCompFont var1 = FontConfigManager.getFontConfigFont("sans", this.isBold, this.isItalic);
            this.linkedFontFiles = FontConfigManager.getFileNames(var1, false);
            this.linkedFontNames = FontConfigManager.getFontNames(var1, false);
            this.fallbacks = new FontResource[this.linkedFontFiles.size()];
         } else {
            ArrayList[] var2;
            if (PrismFontFactory.isMacOSX) {
               var2 = PrismFontFactory.getLinkedFonts("Arial Unicode MS", true);
            } else {
               var2 = PrismFontFactory.getLinkedFonts("Tahoma", true);
            }

            this.linkedFontFiles = var2[0];
            this.linkedFontNames = var2[1];
            this.fallbacks = new FontResource[this.linkedFontFiles.size()];
         }
      }
   }

   @Override
   public int getNumSlots() {
      this.getLinkedFonts();
      int var1 = this.linkedFontFiles.size();
      if (this.nativeFallbacks != null) {
         var1 += this.nativeFallbacks.length;
      }

      return var1;
   }

   @Override
   public float[] getGlyphBoundingBox(int var1, float var2, float[] var3) {
      int var4 = var1 >>> 24;
      int var5 = var1 & 16777215;
      FontResource var6 = this.getSlotResource(var4);
      return var6.getGlyphBoundingBox(var5, var2, var3);
   }

   @Override
   public float getAdvance(int var1, float var2) {
      int var3 = var1 >>> 24;
      int var4 = var1 & 16777215;
      FontResource var5 = this.getSlotResource(var3);
      return var5.getAdvance(var4, var2);
   }

   @Override
   public synchronized FontResource getSlotResource(int var1) {
      this.getLinkedFonts();
      if (var1 >= this.fallbacks.length) {
         var1 -= this.fallbacks.length;
         return this.nativeFallbacks != null && var1 < this.nativeFallbacks.length ? this.nativeFallbacks[var1] : null;
      } else {
         if (this.fallbacks[var1] == null) {
            String var2 = this.linkedFontFiles.get(var1);
            String var3 = this.linkedFontNames.get(var1);
            this.fallbacks[var1] = PrismFontFactory.getFontFactory().getFontResource(var3, var2, false);
         }

         return this.fallbacks[var1];
      }
   }

   @Override
   public FontStrike getStrike(float var1, BaseTransform var2) {
      return this.getStrike(var1, var2, this.getDefaultAAMode());
   }

   @Override
   public FontStrike getStrike(float var1, BaseTransform var2, int var3) {
      FontStrikeDesc var4 = new FontStrikeDesc(var1, var2, var3);
      WeakReference var5 = this.strikeMap.get(var4);
      CompositeStrike var6 = null;
      if (var5 != null) {
         var6 = (CompositeStrike)var5.get();
      }

      if (var6 == null) {
         var6 = new CompositeStrike(this, var1, var2, var3, var4);
         if (var6.disposer != null) {
            var5 = Disposer.addRecord(var6, var6.disposer);
         } else {
            var5 = new WeakReference<>(var6);
         }

         this.strikeMap.put(var4, var5);
      }

      return var6;
   }
}
