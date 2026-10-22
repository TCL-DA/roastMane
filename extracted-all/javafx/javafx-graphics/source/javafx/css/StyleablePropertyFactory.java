package javafx.css;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import javafx.beans.property.Property;
import javafx.css.converter.EnumConverter;
import javafx.geometry.Insets;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;
import javafx.util.Pair;

public class StyleablePropertyFactory<S extends Styleable> {
   private final Map<String, Pair<Class, CssMetaData<S, ?>>> metaDataMap;
   private final List<CssMetaData<? extends Styleable, ?>> unmodifiableMetaDataList;
   private final List<CssMetaData<? extends Styleable, ?>> metaDataList = new ArrayList<>();

   public StyleablePropertyFactory(List<CssMetaData<? extends Styleable, ?>> var1) {
      this.unmodifiableMetaDataList = Collections.unmodifiableList(this.metaDataList);
      if (var1 != null) {
         this.metaDataList.addAll(var1);
      }

      this.metaDataMap = new HashMap<>();
   }

   public final List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
      return this.unmodifiableMetaDataList;
   }

   public final StyleableProperty<Boolean> createStyleableBooleanProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Boolean>> var4, boolean var5, boolean var6
   ) {
      CssMetaData var7 = this.createBooleanCssMetaData(var3, var4, var5, var6);
      return new SimpleStyleableBooleanProperty(var7, var1, var2, var5);
   }

   public final StyleableProperty<Boolean> createStyleableBooleanProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Boolean>> var4, boolean var5
   ) {
      return this.createStyleableBooleanProperty((S)var1, var2, var3, var4, var5, false);
   }

   public final StyleableProperty<Boolean> createStyleableBooleanProperty(S var1, String var2, String var3, Function<S, StyleableProperty<Boolean>> var4) {
      return this.createStyleableBooleanProperty((S)var1, var2, var3, var4, false, false);
   }

   public final StyleableProperty<Boolean> createStyleableBooleanProperty(S var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var4 = this.getCssMetaData(Boolean.class, var3);
         return new SimpleStyleableBooleanProperty(var4, var1, var2, (Boolean)var4.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final StyleableProperty<Color> createStyleableColorProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Color>> var4, Color var5, boolean var6
   ) {
      CssMetaData var7 = this.createColorCssMetaData(var3, var4, var5, var6);
      return new SimpleStyleableObjectProperty<>(var7, var1, var2, var5);
   }

   public final StyleableProperty<Color> createStyleableColorProperty(S var1, String var2, String var3, Function<S, StyleableProperty<Color>> var4, Color var5) {
      return this.createStyleableColorProperty((S)var1, var2, var3, var4, var5, false);
   }

   public final StyleableProperty<Color> createStyleableColorProperty(S var1, String var2, String var3, Function<S, StyleableProperty<Color>> var4) {
      return this.createStyleableColorProperty((S)var1, var2, var3, var4, Color.BLACK, false);
   }

   public final StyleableProperty<Color> createStyleableColorProperty(S var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var4 = this.getCssMetaData(Color.class, var3);
         return new SimpleStyleableObjectProperty<>(var4, var1, var2, (Color)var4.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final StyleableProperty<Duration> createStyleableDurationProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Duration>> var4, Duration var5, boolean var6
   ) {
      CssMetaData var7 = this.createDurationCssMetaData(var3, var4, var5, var6);
      return new SimpleStyleableObjectProperty<>(var7, var1, var2, var5);
   }

   public final StyleableProperty<Duration> createStyleableDurationProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Duration>> var4, Duration var5
   ) {
      return this.createStyleableDurationProperty((S)var1, var2, var3, var4, var5, false);
   }

   public final StyleableProperty<Duration> createStyleableDurationProperty(S var1, String var2, String var3, Function<S, StyleableProperty<Duration>> var4) {
      return this.createStyleableDurationProperty((S)var1, var2, var3, var4, Duration.UNKNOWN, false);
   }

   public final StyleableProperty<Duration> createStyleableDurationProperty(S var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var4 = this.getCssMetaData(Duration.class, var3);
         return new SimpleStyleableObjectProperty<>(var4, var1, var2, (Duration)var4.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final <E extends Effect> StyleableProperty<E> createStyleableEffectProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<E>> var4, E var5, boolean var6
   ) {
      CssMetaData var7 = this.createEffectCssMetaData(var3, var4, var5, var6);
      return new SimpleStyleableObjectProperty<>(var7, var1, var2, (E)var5);
   }

   public final <E extends Effect> StyleableProperty<E> createStyleableEffectProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<E>> var4, E var5
   ) {
      return this.createStyleableEffectProperty((S)var1, var2, var3, var4, (E)var5, false);
   }

   public final <E extends Effect> StyleableProperty<E> createStyleableEffectProperty(S var1, String var2, String var3, Function<S, StyleableProperty<E>> var4) {
      return this.createStyleableEffectProperty((S)var1, var2, var3, var4, null, false);
   }

   public final StyleableProperty<Effect> createStyleableEffectProperty(S var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var4 = this.getCssMetaData(Effect.class, var3);
         return new SimpleStyleableObjectProperty<>(var4, var1, var2, (Effect)var4.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final <E extends Enum<E>> StyleableProperty<E> createStyleableEnumProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<E>> var4, Class<E> var5, E var6, boolean var7
   ) {
      CssMetaData var8 = this.createEnumCssMetaData(var5, var3, var4, (E)var6, var7);
      return new SimpleStyleableObjectProperty<>(var8, var1, var2, (E)var6);
   }

   public final <E extends Enum<E>> StyleableProperty<E> createStyleableEnumProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<E>> var4, Class<E> var5, E var6
   ) {
      return this.createStyleableEnumProperty((S)var1, var2, var3, var4, var5, (E)var6, false);
   }

   public final <E extends Enum<E>> StyleableProperty<E> createStyleableEnumProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<E>> var4, Class<E> var5
   ) {
      return this.createStyleableEnumProperty((S)var1, var2, var3, var4, var5, null, false);
   }

   public final <E extends Enum<E>> StyleableProperty<E> createStyleableEffectProperty(S var1, String var2, String var3, Class<E> var4) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var5 = this.getCssMetaData(var4, var3);
         return new SimpleStyleableObjectProperty<>(var5, var1, var2, (E)var5.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final StyleableProperty<Font> createStyleableFontProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Font>> var4, Font var5, boolean var6
   ) {
      CssMetaData var7 = this.createFontCssMetaData(var3, var4, var5, var6);
      return new SimpleStyleableObjectProperty<>(var7, var1, var2, var5);
   }

   public final StyleableProperty<Font> createStyleableFontProperty(S var1, String var2, String var3, Function<S, StyleableProperty<Font>> var4, Font var5) {
      return this.createStyleableFontProperty((S)var1, var2, var3, var4, var5, true);
   }

   public final StyleableProperty<Font> createStyleableFontProperty(S var1, String var2, String var3, Function<S, StyleableProperty<Font>> var4) {
      return this.createStyleableFontProperty((S)var1, var2, var3, var4, Font.getDefault(), true);
   }

   public final StyleableProperty<Font> createStyleableFontProperty(S var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var4 = this.getCssMetaData(Font.class, var3);
         return new SimpleStyleableObjectProperty<>(var4, var1, var2, (Font)var4.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final StyleableProperty<Insets> createStyleableInsetsProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Insets>> var4, Insets var5, boolean var6
   ) {
      CssMetaData var7 = this.createInsetsCssMetaData(var3, var4, var5, var6);
      return new SimpleStyleableObjectProperty<>(var7, var1, var2, var5);
   }

   public final StyleableProperty<Insets> createStyleableInsetsProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Insets>> var4, Insets var5
   ) {
      return this.createStyleableInsetsProperty((S)var1, var2, var3, var4, var5, false);
   }

   public final StyleableProperty<Insets> createStyleableInsetsProperty(S var1, String var2, String var3, Function<S, StyleableProperty<Insets>> var4) {
      return this.createStyleableInsetsProperty((S)var1, var2, var3, var4, Insets.EMPTY, false);
   }

   public final StyleableProperty<Insets> createStyleableInsetsProperty(S var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var4 = this.getCssMetaData(Insets.class, var3);
         return new SimpleStyleableObjectProperty<>(var4, var1, var2, (Insets)var4.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final StyleableProperty<Paint> createStyleablePaintProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Paint>> var4, Paint var5, boolean var6
   ) {
      CssMetaData var7 = this.createPaintCssMetaData(var3, var4, var5, var6);
      return new SimpleStyleableObjectProperty<>(var7, var1, var2, var5);
   }

   public final StyleableProperty<Paint> createStyleablePaintProperty(S var1, String var2, String var3, Function<S, StyleableProperty<Paint>> var4, Paint var5) {
      return this.createStyleablePaintProperty((S)var1, var2, var3, var4, var5, false);
   }

   public final StyleableProperty<Paint> createStyleablePaintProperty(S var1, String var2, String var3, Function<S, StyleableProperty<Paint>> var4) {
      return this.createStyleablePaintProperty((S)var1, var2, var3, var4, Color.BLACK, false);
   }

   public final StyleableProperty<Paint> createStyleablePaintProperty(S var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var4 = this.getCssMetaData(Paint.class, var3);
         return new SimpleStyleableObjectProperty<>(var4, var1, var2, (Paint)var4.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final StyleableProperty<Number> createStyleableNumberProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Number>> var4, Number var5, boolean var6
   ) {
      CssMetaData var7 = this.createSizeCssMetaData(var3, var4, var5, var6);
      return new SimpleStyleableObjectProperty<>(var7, var1, var2, var5);
   }

   public final StyleableProperty<Number> createStyleableNumberProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<Number>> var4, Number var5
   ) {
      return this.createStyleableNumberProperty((S)var1, var2, var3, var4, var5, false);
   }

   public final StyleableProperty<Number> createStyleableNumberProperty(S var1, String var2, String var3, Function<S, StyleableProperty<Number>> var4) {
      return this.createStyleableNumberProperty((S)var1, var2, var3, var4, 0.0, false);
   }

   public final StyleableProperty<Number> createStyleableNumberProperty(S var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var4 = this.getCssMetaData(Number.class, var3);
         return new SimpleStyleableObjectProperty<>(var4, var1, var2, (Number)var4.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final StyleableProperty<String> createStyleableStringProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<String>> var4, String var5, boolean var6
   ) {
      CssMetaData var7 = this.createStringCssMetaData(var3, var4, var5, var6);
      return new SimpleStyleableStringProperty(var7, var1, var2, var5);
   }

   public final StyleableProperty<String> createStyleableStringProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<String>> var4, String var5
   ) {
      return this.createStyleableStringProperty((S)var1, var2, var3, var4, var5, false);
   }

   public final StyleableProperty<String> createStyleableStringProperty(S var1, String var2, String var3, Function<S, StyleableProperty<String>> var4) {
      return this.createStyleableStringProperty((S)var1, var2, var3, var4, null, false);
   }

   public final StyleableProperty<String> createStyleableStringProperty(S var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var4 = this.getCssMetaData(String.class, var3);
         return new SimpleStyleableStringProperty(var4, var1, var2, (String)var4.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final StyleableProperty<String> createStyleableUrlProperty(
      S var1, String var2, String var3, Function<S, StyleableProperty<String>> var4, String var5, boolean var6
   ) {
      CssMetaData var7 = this.createUrlCssMetaData(var3, var4, var5, var6);
      return new SimpleStyleableStringProperty(var7, var1, var2, var5);
   }

   public final StyleableProperty<String> createStyleableUrlProperty(S var1, String var2, String var3, Function<S, StyleableProperty<String>> var4, String var5) {
      return this.createStyleableUrlProperty((S)var1, var2, var3, var4, var5, false);
   }

   public final StyleableProperty<String> createStyleableUrlProperty(S var1, String var2, String var3, Function<S, StyleableProperty<String>> var4) {
      return this.createStyleableUrlProperty((S)var1, var2, var3, var4, null, false);
   }

   public final StyleableProperty<String> createStyleableUrlProperty(S var1, String var2, String var3) {
      if (var3 != null && !var3.isEmpty()) {
         CssMetaData var4 = this.getCssMetaData(String.class, var3);
         return new SimpleStyleableStringProperty(var4, var1, var2, (String)var4.getInitialValue(var1));
      } else {
         throw new IllegalArgumentException("cssProperty cannot be null or empty string");
      }
   }

   public final CssMetaData<S, Boolean> createBooleanCssMetaData(String var1, Function<S, StyleableProperty<Boolean>> var2, boolean var3, boolean var4) {
      if (var1 == null || var1.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var2 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, Boolean>)this.getCssMetaData(Boolean.class, var1, var3x -> {
            StyleConverter var4x = StyleConverter.getBooleanConverter();
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var3x, var2, var4x, var3, var4);
         });
      }
   }

   public final CssMetaData<S, Boolean> createBooleanCssMetaData(String var1, Function<S, StyleableProperty<Boolean>> var2, boolean var3) {
      return this.createBooleanCssMetaData(var1, var2, var3, false);
   }

   public final CssMetaData<S, Boolean> createBooleanCssMetaData(String var1, Function<S, StyleableProperty<Boolean>> var2) {
      return this.createBooleanCssMetaData(var1, var2, false, false);
   }

   public final CssMetaData<S, Color> createColorCssMetaData(String var1, Function<S, StyleableProperty<Color>> var2, Color var3, boolean var4) {
      if (var1 == null || var1.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var2 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, Color>)this.getCssMetaData(Color.class, var1, var4x -> {
            StyleConverter var5 = StyleConverter.getColorConverter();
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var1, var2, var5, var3, var4);
         });
      }
   }

   public final CssMetaData<S, Color> createColorCssMetaData(String var1, Function<S, StyleableProperty<Color>> var2, Color var3) {
      return this.createColorCssMetaData(var1, var2, var3, false);
   }

   public final CssMetaData<S, Color> createColorCssMetaData(String var1, Function<S, StyleableProperty<Color>> var2) {
      return this.createColorCssMetaData(var1, var2, Color.BLACK, false);
   }

   public final CssMetaData<S, Duration> createDurationCssMetaData(String var1, Function<S, StyleableProperty<Duration>> var2, Duration var3, boolean var4) {
      if (var1 == null || var1.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var2 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, Duration>)this.getCssMetaData(Duration.class, var1, var4x -> {
            StyleConverter var5 = StyleConverter.getDurationConverter();
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var1, var2, var5, var3, var4);
         });
      }
   }

   public final CssMetaData<S, Duration> createDurationCssMetaData(String var1, Function<S, StyleableProperty<Duration>> var2, Duration var3) {
      return this.createDurationCssMetaData(var1, var2, var3, false);
   }

   public final CssMetaData<S, Duration> createDurationCssMetaData(String var1, Function<S, StyleableProperty<Duration>> var2) {
      return this.createDurationCssMetaData(var1, var2, Duration.UNKNOWN, false);
   }

   public final <E extends Effect> CssMetaData<S, E> createEffectCssMetaData(String var1, Function<S, StyleableProperty<E>> var2, E var3, boolean var4) {
      if (var1 == null || var1.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var2 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, E>)this.getCssMetaData(Effect.class, var1, var4x -> {
            StyleConverter var5 = StyleConverter.getEffectConverter();
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var1, var2, var5, var3, var4);
         });
      }
   }

   public final <E extends Effect> CssMetaData<S, E> createEffectCssMetaData(String var1, Function<S, StyleableProperty<E>> var2, E var3) {
      return this.createEffectCssMetaData(var1, var2, (E)var3, false);
   }

   public final <E extends Effect> CssMetaData<S, E> createEffectCssMetaData(String var1, Function<S, StyleableProperty<E>> var2) {
      return this.createEffectCssMetaData(var1, var2, null, false);
   }

   public final <E extends Enum<E>> CssMetaData<S, E> createEnumCssMetaData(
      Class<? extends Enum> var1, String var2, Function<S, StyleableProperty<E>> var3, E var4, boolean var5
   ) {
      if (var2 == null || var2.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var3 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, E>)this.getCssMetaData(var1, var2, var5x -> {
            EnumConverter var6 = new EnumConverter(var1);
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var2, var3, var6, (E)var4, var5);
         });
      }
   }

   public final <E extends Enum<E>> CssMetaData<S, E> createEnumCssMetaData(
      Class<? extends Enum> var1, String var2, Function<S, StyleableProperty<E>> var3, E var4
   ) {
      return this.createEnumCssMetaData(var1, var2, var3, (E)var4, false);
   }

   public final <E extends Enum<E>> CssMetaData<S, E> createEnumCssMetaData(Class<? extends Enum> var1, String var2, Function<S, StyleableProperty<E>> var3) {
      return this.createEnumCssMetaData(var1, var2, var3, null, false);
   }

   public final CssMetaData<S, Font> createFontCssMetaData(String var1, Function<S, StyleableProperty<Font>> var2, Font var3, boolean var4) {
      if (var1 == null || var1.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var2 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, Font>)this.getCssMetaData(Font.class, var1, var4x -> {
            StyleConverter var5 = StyleConverter.getFontConverter();
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var1, var2, var5, var3, var4);
         });
      }
   }

   public final CssMetaData<S, Font> createFontCssMetaData(String var1, Function<S, StyleableProperty<Font>> var2, Font var3) {
      return this.createFontCssMetaData(var1, var2, var3, true);
   }

   public final CssMetaData<S, Font> createFontCssMetaData(String var1, Function<S, StyleableProperty<Font>> var2) {
      return this.createFontCssMetaData(var1, var2, Font.getDefault(), true);
   }

   public final CssMetaData<S, Insets> createInsetsCssMetaData(String var1, Function<S, StyleableProperty<Insets>> var2, Insets var3, boolean var4) {
      if (var1 == null || var1.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var2 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, Insets>)this.getCssMetaData(Insets.class, var1, var4x -> {
            StyleConverter var5 = StyleConverter.getInsetsConverter();
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var1, var2, var5, var3, var4);
         });
      }
   }

   public final CssMetaData<S, Insets> createInsetsCssMetaData(String var1, Function<S, StyleableProperty<Insets>> var2, Insets var3) {
      return this.createInsetsCssMetaData(var1, var2, var3, false);
   }

   public final CssMetaData<S, Insets> createInsetsCssMetaData(String var1, Function<S, StyleableProperty<Insets>> var2) {
      return this.createInsetsCssMetaData(var1, var2, Insets.EMPTY, false);
   }

   public final CssMetaData<S, Paint> createPaintCssMetaData(String var1, Function<S, StyleableProperty<Paint>> var2, Paint var3, boolean var4) {
      if (var1 == null || var1.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var2 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, Paint>)this.getCssMetaData(Paint.class, var1, var4x -> {
            StyleConverter var5 = StyleConverter.getPaintConverter();
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var1, var2, var5, var3, var4);
         });
      }
   }

   public final CssMetaData<S, Paint> createPaintCssMetaData(String var1, Function<S, StyleableProperty<Paint>> var2, Paint var3) {
      return this.createPaintCssMetaData(var1, var2, var3, false);
   }

   public final CssMetaData<S, Paint> createPaintCssMetaData(String var1, Function<S, StyleableProperty<Paint>> var2) {
      return this.createPaintCssMetaData(var1, var2, Color.BLACK, false);
   }

   public final CssMetaData<S, Number> createSizeCssMetaData(String var1, Function<S, StyleableProperty<Number>> var2, Number var3, boolean var4) {
      if (var1 == null || var1.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var2 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, Number>)this.getCssMetaData(Number.class, var1, var4x -> {
            StyleConverter var5 = StyleConverter.getSizeConverter();
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var1, var2, var5, var3, var4);
         });
      }
   }

   public final CssMetaData<S, Number> createSizeCssMetaData(String var1, Function<S, StyleableProperty<Number>> var2, Number var3) {
      return this.createSizeCssMetaData(var1, var2, var3, false);
   }

   public final CssMetaData<S, Number> createSizeCssMetaData(String var1, Function<S, StyleableProperty<Number>> var2) {
      return this.createSizeCssMetaData(var1, var2, 0.0, false);
   }

   public final CssMetaData<S, String> createStringCssMetaData(String var1, Function<S, StyleableProperty<String>> var2, String var3, boolean var4) {
      if (var1 == null || var1.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var2 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, String>)this.getCssMetaData(String.class, var1, var4x -> {
            StyleConverter var5 = StyleConverter.getStringConverter();
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var1, var2, var5, var3, var4);
         });
      }
   }

   public final CssMetaData<S, String> createStringCssMetaData(String var1, Function<S, StyleableProperty<String>> var2, String var3) {
      return this.createStringCssMetaData(var1, var2, var3, false);
   }

   public final CssMetaData<S, String> createStringCssMetaData(String var1, Function<S, StyleableProperty<String>> var2) {
      return this.createStringCssMetaData(var1, var2, null, false);
   }

   public final CssMetaData<S, String> createUrlCssMetaData(String var1, Function<S, StyleableProperty<String>> var2, String var3, boolean var4) {
      if (var1 == null || var1.isEmpty()) {
         throw new IllegalArgumentException("property cannot be null or empty string");
      } else if (var2 == null) {
         throw new IllegalArgumentException("function cannot be null");
      } else {
         return (CssMetaData<S, String>)this.getCssMetaData(URL.class, var1, var4x -> {
            StyleConverter var5 = StyleConverter.getUrlConverter();
            return new StyleablePropertyFactory.SimpleCssMetaData<>(var1, var2, var5, var3, var4);
         });
      }
   }

   public final CssMetaData<S, String> createUrlCssMetaData(String var1, Function<S, StyleableProperty<String>> var2, String var3) {
      return this.createUrlCssMetaData(var1, var2, var3, false);
   }

   public final CssMetaData<S, String> createUrlCssMetaData(String var1, Function<S, StyleableProperty<String>> var2) {
      return this.createUrlCssMetaData(var1, var2, null, false);
   }

   void clearDataForTesting() {
      this.metaDataMap.clear();
      this.metaDataList.clear();
   }

   private CssMetaData<S, ?> getCssMetaData(Class var1, String var2) {
      return this.getCssMetaData(var1, var2, null);
   }

   private CssMetaData<S, ?> getCssMetaData(Class var1, String var2, Function<String, CssMetaData<S, ?>> var3) {
      String var4 = var2.toLowerCase();
      Pair var5 = this.metaDataMap.get(var4);
      if (var5 != null) {
         if (var5.getKey() == var1) {
            return (CssMetaData<S, ?>)var5.getValue();
         } else {
            throw new ClassCastException("CssMetaData value is not " + var1 + ": " + var5.getValue());
         }
      } else if (var3 == null) {
         throw new NoSuchElementException("No CssMetaData for " + var4);
      } else {
         CssMetaData var6 = (CssMetaData)var3.apply(var4);
         this.metaDataMap.put(var4, new Pair(var1, var6));
         this.metaDataList.add(var6);
         return var6;
      }
   }

   private static class SimpleCssMetaData<S extends Styleable, V> extends CssMetaData<S, V> {
      private final Function<S, StyleableProperty<V>> function;

      SimpleCssMetaData(String var1, Function<S, StyleableProperty<V>> var2, StyleConverter<?, V> var3, V var4, boolean var5) {
         super(var1, var3, (V)var4, var5);
         this.function = var2;
      }

      @Override
      public final boolean isSettable(S var1) {
         StyleableProperty var2 = this.getStyleableProperty((S)var1);
         return var2 instanceof Property ? !((Property)var2).isBound() : var2 != null;
      }

      @Override
      public final StyleableProperty<V> getStyleableProperty(S var1) {
         return var1 != null ? this.function.apply((S)var1) : null;
      }
   }
}
