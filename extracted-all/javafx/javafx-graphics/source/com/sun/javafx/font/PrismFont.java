package com.sun.javafx.font;

import com.sun.javafx.geom.transform.BaseTransform;

class PrismFont implements PGFont {
   private String name;
   private float fontSize;
   protected FontResource fontResource;
   private int features;
   private int hash;

   PrismFont(FontResource var1, String var2, float var3) {
      this.fontResource = var1;
      this.name = var2;
      this.fontSize = var3;
   }

   @Override
   public String getFullName() {
      return this.fontResource.getFullName();
   }

   @Override
   public String getFamilyName() {
      return this.fontResource.getFamilyName();
   }

   @Override
   public String getStyleName() {
      return this.fontResource.getStyleName();
   }

   @Override
   public int getFeatures() {
      return this.features;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public float getSize() {
      return this.fontSize;
   }

   @Override
   public FontStrike getStrike(BaseTransform var1) {
      return this.fontResource.getStrike(this.fontSize, var1);
   }

   @Override
   public FontStrike getStrike(BaseTransform var1, int var2) {
      return this.fontResource.getStrike(this.fontSize, var1, var2);
   }

   @Override
   public FontResource getFontResource() {
      return this.fontResource;
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (!(var1 instanceof PrismFont)) {
         return false;
      } else {
         PrismFont var2 = (PrismFont)var1;
         return this.fontSize == var2.fontSize && this.fontResource.equals(var2.fontResource);
      }
   }

   @Override
   public int hashCode() {
      if (this.hash != 0) {
         return this.hash;
      } else {
         this.hash = 497 + Float.floatToIntBits(this.fontSize);
         this.hash = 71 * this.hash + this.fontResource.hashCode();
         return this.hash;
      }
   }
}
