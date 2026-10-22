package org.controlsfx.glyphfont;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public final class GlyphFontRegistry {
   private static Map<String, GlyphFont> fontMap = new HashMap<>();

   private GlyphFontRegistry() {
   }

   public static void register(String familyName, String uri, int defaultSize) {
      register(new GlyphFont(familyName, defaultSize, uri));
   }

   public static void register(String familyName, InputStream in, int defaultSize) {
      register(new GlyphFont(familyName, defaultSize, in));
   }

   public static void register(GlyphFont font) {
      if (font != null) {
         fontMap.put(font.getName(), font);
      }
   }

   public static GlyphFont font(String familyName) {
      GlyphFont font = fontMap.get(familyName);
      if (font != null) {
         font.ensureFontIsLoaded();
      }

      return font;
   }

   static {
      for (GlyphFont font : ServiceLoader.load(GlyphFont.class)) {
         register(font);
      }
   }
}
