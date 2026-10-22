package org.controlsfx.glyphfont;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.text.Font;

public class GlyphFont {
   private final Map<String, Character> namedGlyphs = new HashMap<>();
   private final Runnable fontLoader;
   private final String fontName;
   private final double defaultSize;
   private boolean fontLoaded = false;

   public GlyphFont(String fontName, int defaultSize, InputStream in) {
      this(fontName, defaultSize, in, false);
   }

   public GlyphFont(String fontName, int defaultSize, String urlStr) {
      this(fontName, defaultSize, urlStr, false);
   }

   public GlyphFont(String fontName, int defaultSize, InputStream in, boolean lazyLoad) {
      this(fontName, defaultSize, () -> Font.loadFont(in, -1.0), lazyLoad);
   }

   public GlyphFont(String fontName, int defaultSize, String urlStr, boolean lazyLoad) {
      this(fontName, defaultSize, () -> Font.loadFont(urlStr, -1.0), lazyLoad);
   }

   private GlyphFont(String fontName, int defaultSize, Runnable fontLoader, boolean lazyLoad) {
      this.fontName = fontName;
      this.defaultSize = (double)defaultSize;
      this.fontLoader = fontLoader;
      if (!lazyLoad) {
         this.ensureFontIsLoaded();
      }
   }

   public String getName() {
      return this.fontName;
   }

   public double getDefaultSize() {
      return this.defaultSize;
   }

   public Glyph create(char character) {
      return new Glyph(this.fontName, character);
   }

   public Glyph create(String glyphName) {
      return new Glyph(this.fontName, glyphName);
   }

   public Glyph create(Enum<?> glyph) {
      return new Glyph(this.fontName, glyph);
   }

   public Character getCharacter(String glyphName) {
      return this.namedGlyphs.get(glyphName.toUpperCase());
   }

   public void registerAll(Iterable<? extends INamedCharacter> namedCharacters) {
      for (INamedCharacter e : namedCharacters) {
         this.register(e.name(), e.getChar());
      }
   }

   public void register(String name, Character character) {
      this.namedGlyphs.put(name.toUpperCase(), character);
   }

   synchronized void ensureFontIsLoaded() {
      if (!this.fontLoaded) {
         this.fontLoader.run();
         this.fontLoaded = true;
      }
   }
}
