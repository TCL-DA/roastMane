package com.sun.javafx.css;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javafx.css.FontFace;
import javafx.css.StyleConverter;

public final class FontFaceImpl extends FontFace {
   private final Map<String, String> descriptors;
   private final List<FontFaceImpl.FontFaceSrc> sources;

   public FontFaceImpl(Map<String, String> var1, List<FontFaceImpl.FontFaceSrc> var2) {
      this.descriptors = var1;
      this.sources = var2;
   }

   public Map<String, String> getDescriptors() {
      return this.descriptors;
   }

   public List<FontFaceImpl.FontFaceSrc> getSources() {
      return this.sources;
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder("@font-face { ");

      for (Entry var3 : this.descriptors.entrySet()) {
         var1.append((String)var3.getKey());
         var1.append(" : ");
         var1.append((String)var3.getValue());
         var1.append("; ");
      }

      var1.append("src : ");

      for (FontFaceImpl.FontFaceSrc var5 : this.sources) {
         var1.append(var5.getType());
         var1.append(" \"");
         var1.append(var5.getSrc());
         var1.append("\", ");
      }

      var1.append("; ");
      var1.append(" }");
      return var1.toString();
   }

   public final void writeBinary(DataOutputStream var1, StyleConverter.StringStore var2) throws IOException {
      Set var3 = this.getDescriptors() != null ? this.getDescriptors().entrySet() : null;
      int var4 = var3 != null ? var3.size() : 0;
      var1.writeShort(var4);
      if (var3 != null) {
         for (Entry var6 : var3) {
            int var7 = var2.addString((String)var6.getKey());
            var1.writeInt(var7);
            var7 = var2.addString((String)var6.getValue());
            var1.writeInt(var7);
         }
      }

      List var9 = this.getSources();
      var4 = var9 != null ? var9.size() : 0;
      var1.writeShort(var4);

      for (int var10 = 0; var10 < var4; var10++) {
         FontFaceImpl.FontFaceSrc var12 = (FontFaceImpl.FontFaceSrc)var9.get(var10);
         var12.writeBinary(var1, var2);
      }
   }

   public static final FontFaceImpl readBinary(int var0, DataInputStream var1, String[] var2) throws IOException {
      short var3 = var1.readShort();
      HashMap var4 = new HashMap(var3);

      for (int var5 = 0; var5 < var3; var5++) {
         int var6 = var1.readInt();
         String var7 = var2[var6];
         var6 = var1.readInt();
         String var8 = var2[var6];
         var4.put(var7, var8);
      }

      var3 = var1.readShort();
      ArrayList var10 = new ArrayList(var3);

      for (int var12 = 0; var12 < var3; var12++) {
         FontFaceImpl.FontFaceSrc var13 = FontFaceImpl.FontFaceSrc.readBinary(var0, var1, var2);
         var10.add(var13);
      }

      return new FontFaceImpl(var4, var10);
   }

   public static class FontFaceSrc {
      private final FontFaceImpl.FontFaceSrcType type;
      private final String src;
      private final String format;

      public FontFaceSrc(FontFaceImpl.FontFaceSrcType var1, String var2, String var3) {
         this.type = var1;
         this.src = var2;
         this.format = var3;
      }

      public FontFaceSrc(FontFaceImpl.FontFaceSrcType var1, String var2) {
         this.type = var1;
         this.src = var2;
         this.format = null;
      }

      public FontFaceImpl.FontFaceSrcType getType() {
         return this.type;
      }

      public String getSrc() {
         return this.src;
      }

      public String getFormat() {
         return this.format;
      }

      final void writeBinary(DataOutputStream var1, StyleConverter.StringStore var2) throws IOException {
         var1.writeInt(var2.addString(this.type.name()));
         var1.writeInt(var2.addString(this.src));
         var1.writeInt(var2.addString(this.format));
      }

      static final FontFaceImpl.FontFaceSrc readBinary(int var0, DataInputStream var1, String[] var2) throws IOException {
         int var3 = var1.readInt();
         FontFaceImpl.FontFaceSrcType var4 = var2[var3] != null ? FontFaceImpl.FontFaceSrcType.valueOf(var2[var3]) : null;
         var3 = var1.readInt();
         String var5 = var2[var3];
         var3 = var1.readInt();
         String var6 = var2[var3];
         return new FontFaceImpl.FontFaceSrc(var4, var5, var6);
      }
   }

   public static enum FontFaceSrcType {
      URL,
      LOCAL,
      REFERENCE;
   }
}
