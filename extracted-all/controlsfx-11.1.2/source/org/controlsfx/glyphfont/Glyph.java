package org.controlsfx.glyphfont;

import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import org.controlsfx.tools.Duplicatable;

public class Glyph extends Label implements Duplicatable<Glyph> {
   public static final String DEFAULT_CSS_CLASS = "glyph-font";
   public static final String STYLE_GRADIENT = "gradient";
   public static final String STYLE_HOVER_EFFECT = "hover-effect";
   private final ObjectProperty<Object> icon = new SimpleObjectProperty();

   public static Glyph create(String fontAndGlyph) {
      String[] args = fontAndGlyph.split("\\|");
      return new Glyph(args[0], args[1]);
   }

   public Glyph() {
      this.getStyleClass().add("glyph-font");
      this.icon.addListener(x -> this.updateIcon());
      this.fontProperty().addListener(x -> this.updateIcon());
   }

   public Glyph(String fontFamily, char unicode) {
      this();
      this.setFontFamily(fontFamily);
      this.setTextUnicode(unicode);
   }

   public Glyph(String fontFamily, Object icon) {
      this();
      this.setFontFamily(fontFamily);
      this.setIcon(icon);
   }

   public Glyph fontFamily(String fontFamily) {
      this.setFontFamily(fontFamily);
      return this;
   }

   public Glyph color(Color color) {
      this.setColor(color);
      return this;
   }

   public Glyph size(double size) {
      this.setFontSize(size);
      return this;
   }

   public Glyph sizeFactor(int factor) {
      Optional.ofNullable(GlyphFontRegistry.font(this.getFont().getFamily()))
         .ifPresent(glyphFont -> this.setFontSize(glyphFont.getDefaultSize() * (double)(factor < 1 ? 1 : factor)));
      return this;
   }

   public Glyph useHoverEffect() {
      this.getStyleClass().add("hover-effect");
      return this;
   }

   public Glyph useGradientEffect() {
      if (this.getTextFill() instanceof Color) {
         Color currentColor = (Color)this.getTextFill();
         Stop[] stops = new Stop[]{new Stop(0.0, Color.BLACK), new Stop(1.0, currentColor)};
         LinearGradient lg1 = new LinearGradient(0.0, 0.0, 1.0, 0.0, true, CycleMethod.NO_CYCLE, stops);
         this.setTextFill(lg1);
      }

      this.getStyleClass().add("gradient");
      return this;
   }

   public Glyph duplicate() {
      final Paint color = this.getTextFill();
      final Object icon = this.getIcon();
      final ObservableList<String> classes = this.getStyleClass();
      return (new Glyph() {
         {
            this.setIcon(icon);
            this.setTextFill(color);
            this.getStyleClass().addAll(classes);
         }
      }).fontFamily(this.getFontFamily()).size(this.getFontSize());
   }

   public String getUserAgentStylesheet() {
      return Glyph.class.getResource("glyphfont.css").toExternalForm();
   }

   public void setFontFamily(String family) {
      if (!this.getFont().getFamily().equals(family)) {
         Optional.ofNullable(GlyphFontRegistry.font(family)).ifPresent(glyphFont -> {
            glyphFont.ensureFontIsLoaded();
            Font newFont = Font.font(family, glyphFont.getDefaultSize());
            this.setFont(newFont);
         });
      }
   }

   public String getFontFamily() {
      return this.getFont().getFamily();
   }

   public void setFontSize(double size) {
      Font newFont = Font.font(this.getFont().getFamily(), size);
      this.setFont(newFont);
   }

   public double getFontSize() {
      return this.getFont().getSize();
   }

   public void setColor(Color color) {
      this.setTextFill(color);
   }

   public ObjectProperty<Object> iconProperty() {
      return this.icon;
   }

   public void setIcon(Object iconValue) {
      this.icon.set(iconValue);
   }

   public Object getIcon() {
      return this.icon.get();
   }

   private void updateIcon() {
      Object iconValue = this.getIcon();
      if (iconValue != null) {
         if (iconValue instanceof Character) {
            this.setTextUnicode((Character)iconValue);
         } else {
            GlyphFont glyphFont = GlyphFontRegistry.font(this.getFontFamily());
            if (glyphFont != null) {
               String name = iconValue.toString();
               Character unicode = glyphFont.getCharacter(name);
               if (unicode != null) {
                  this.setTextUnicode(unicode);
               } else {
                  this.setText(name);
               }
            }
         }
      }
   }

   private void setTextUnicode(char unicode) {
      this.setText(String.valueOf(unicode));
   }
}
