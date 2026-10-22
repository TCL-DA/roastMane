package com.sun.javafx.scene.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ObservableValue;
import javafx.css.CssMetaData;
import javafx.css.FontCssMetaData;
import javafx.css.SimpleStyleableObjectProperty;
import javafx.css.StyleOrigin;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.scene.control.Labeled;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class LabeledText extends Text {
   private final Labeled labeled;
   private LabeledText.StyleablePropertyMirror<Font> fontMirror = null;
   private static final CssMetaData<LabeledText, Font> FONT = new FontCssMetaData<LabeledText>("-fx-font", Font.getDefault()) {
      public boolean isSettable(LabeledText var1) {
         return var1.labeled != null ? !var1.labeled.fontProperty().isBound() : true;
      }

      public StyleableProperty<Font> getStyleableProperty(LabeledText var1) {
         return var1.fontMirror();
      }
   };
   private LabeledText.StyleablePropertyMirror<Paint> fillMirror;
   private static final CssMetaData<LabeledText, Paint> FILL = new CssMetaData<LabeledText, Paint>("-fx-fill", PaintConverter.getInstance(), Color.BLACK) {
      public boolean isSettable(LabeledText var1) {
         return !var1.labeled.textFillProperty().isBound();
      }

      public StyleableProperty<Paint> getStyleableProperty(LabeledText var1) {
         return var1.fillMirror();
      }
   };
   private LabeledText.StyleablePropertyMirror<TextAlignment> textAlignmentMirror;
   private static final CssMetaData<LabeledText, TextAlignment> TEXT_ALIGNMENT = new CssMetaData<LabeledText, TextAlignment>(
      "-fx-text-alignment", new EnumConverter(TextAlignment.class), TextAlignment.LEFT
   ) {
      public boolean isSettable(LabeledText var1) {
         return !var1.labeled.textAlignmentProperty().isBound();
      }

      public StyleableProperty<TextAlignment> getStyleableProperty(LabeledText var1) {
         return var1.textAlignmentMirror();
      }
   };
   private LabeledText.StyleablePropertyMirror<Boolean> underlineMirror;
   private static final CssMetaData<LabeledText, Boolean> UNDERLINE = new CssMetaData<LabeledText, Boolean>(
      "-fx-underline", BooleanConverter.getInstance(), Boolean.FALSE
   ) {
      public boolean isSettable(LabeledText var1) {
         return !var1.labeled.underlineProperty().isBound();
      }

      public StyleableProperty<Boolean> getStyleableProperty(LabeledText var1) {
         return var1.underlineMirror();
      }
   };
   private LabeledText.StyleablePropertyMirror<Number> lineSpacingMirror;
   private static final CssMetaData<LabeledText, Number> LINE_SPACING = new CssMetaData<LabeledText, Number>("-fx-line-spacing", SizeConverter.getInstance(), 0) {
      public boolean isSettable(LabeledText var1) {
         return !var1.labeled.lineSpacingProperty().isBound();
      }

      public StyleableProperty<Number> getStyleableProperty(LabeledText var1) {
         return var1.lineSpacingMirror();
      }
   };
   private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

   public LabeledText(Labeled var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("labeled cannot be null");
      } else {
         this.labeled = var1;
         this.setFill(this.labeled.getTextFill());
         this.setFont(this.labeled.getFont());
         this.setTextAlignment(this.labeled.getTextAlignment());
         this.setUnderline(this.labeled.isUnderline());
         this.setLineSpacing(this.labeled.getLineSpacing());
         this.fillProperty().bind(this.labeled.textFillProperty());
         this.fontProperty().bind(this.labeled.fontProperty());
         this.textAlignmentProperty().bind(this.labeled.textAlignmentProperty());
         this.underlineProperty().bind(this.labeled.underlineProperty());
         this.lineSpacingProperty().bind(this.labeled.lineSpacingProperty());
         this.getStyleClass().addAll(new String[]{"text"});
      }
   }

   public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
      return STYLEABLES;
   }

   public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return getClassCssMetaData();
   }

   private StyleableProperty<Font> fontMirror() {
      if (this.fontMirror == null) {
         this.fontMirror = new LabeledText.StyleablePropertyMirror<>(
            FONT, "fontMirror", Font.getDefault(), (StyleableProperty<Font>)this.labeled.fontProperty()
         );
         this.fontProperty().addListener(this.fontMirror);
      }

      return this.fontMirror;
   }

   private StyleableProperty<Paint> fillMirror() {
      if (this.fillMirror == null) {
         this.fillMirror = new LabeledText.StyleablePropertyMirror<>(FILL, "fillMirror", Color.BLACK, (StyleableProperty<Paint>)this.labeled.textFillProperty());
         this.fillProperty().addListener(this.fillMirror);
      }

      return this.fillMirror;
   }

   private StyleableProperty<TextAlignment> textAlignmentMirror() {
      if (this.textAlignmentMirror == null) {
         this.textAlignmentMirror = new LabeledText.StyleablePropertyMirror<>(
            TEXT_ALIGNMENT, "textAlignmentMirror", TextAlignment.LEFT, (StyleableProperty<TextAlignment>)this.labeled.textAlignmentProperty()
         );
         this.textAlignmentProperty().addListener(this.textAlignmentMirror);
      }

      return this.textAlignmentMirror;
   }

   private StyleableProperty<Boolean> underlineMirror() {
      if (this.underlineMirror == null) {
         this.underlineMirror = new LabeledText.StyleablePropertyMirror<>(
            UNDERLINE, "underLineMirror", Boolean.FALSE, (StyleableProperty<Boolean>)this.labeled.underlineProperty()
         );
         this.underlineProperty().addListener(this.underlineMirror);
      }

      return this.underlineMirror;
   }

   private StyleableProperty<Number> lineSpacingMirror() {
      if (this.lineSpacingMirror == null) {
         this.lineSpacingMirror = new LabeledText.StyleablePropertyMirror<>(
            LINE_SPACING, "lineSpacingMirror", 0.0, (StyleableProperty<Number>)this.labeled.lineSpacingProperty()
         );
         this.lineSpacingProperty().addListener(this.lineSpacingMirror);
      }

      return this.lineSpacingMirror;
   }

   static {
      ArrayList var0 = new ArrayList(Text.getClassCssMetaData());
      int var1 = 0;

      for (int var2 = var0.size(); var1 < var2; var1++) {
         String var3 = ((CssMetaData)var0.get(var1)).getProperty();
         if ("-fx-fill".equals(var3)) {
            var0.set(var1, FILL);
         } else if ("-fx-font".equals(var3)) {
            var0.set(var1, FONT);
         } else if ("-fx-text-alignment".equals(var3)) {
            var0.set(var1, TEXT_ALIGNMENT);
         } else if ("-fx-underline".equals(var3)) {
            var0.set(var1, UNDERLINE);
         } else if ("-fx-line-spacing".equals(var3)) {
            var0.set(var1, LINE_SPACING);
         }
      }

      STYLEABLES = Collections.unmodifiableList(var0);
   }

   private class StyleablePropertyMirror<T> extends SimpleStyleableObjectProperty<T> implements InvalidationListener {
      boolean applying;
      private final StyleableProperty<T> property;

      private StyleablePropertyMirror(CssMetaData<LabeledText, T> var2, String var3, T var4, StyleableProperty<T> var5) {
         super(var2, LabeledText.this, var3, var4);
         this.property = var5;
         this.applying = false;
      }

      public void invalidated(Observable var1) {
         if (!this.applying) {
            super.applyStyle(null, ((ObservableValue)var1).getValue());
         }
      }

      public void applyStyle(StyleOrigin var1, T var2) {
         this.applying = true;
         StyleOrigin var3 = this.property.getStyleOrigin();
         if (var3 == null || (var1 != null ? var3.compareTo(var1) <= 0 : var3 != StyleOrigin.USER)) {
            super.applyStyle(var1, var2);
            this.property.applyStyle(var1, var2);
         }

         this.applying = false;
      }

      public StyleOrigin getStyleOrigin() {
         return this.property.getStyleOrigin();
      }
   }
}
