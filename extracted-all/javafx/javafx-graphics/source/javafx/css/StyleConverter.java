package javafx.css;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.scene.layout.region.BackgroundPositionConverter;
import com.sun.javafx.scene.layout.region.BackgroundSizeConverter;
import com.sun.javafx.scene.layout.region.BorderImageSliceConverter;
import com.sun.javafx.scene.layout.region.BorderImageWidthConverter;
import com.sun.javafx.scene.layout.region.BorderImageWidthsSequenceConverter;
import com.sun.javafx.scene.layout.region.BorderStrokeStyleSequenceConverter;
import com.sun.javafx.scene.layout.region.BorderStyleConverter;
import com.sun.javafx.scene.layout.region.CornerRadiiConverter;
import com.sun.javafx.scene.layout.region.LayeredBackgroundPositionConverter;
import com.sun.javafx.scene.layout.region.LayeredBackgroundSizeConverter;
import com.sun.javafx.scene.layout.region.LayeredBorderPaintConverter;
import com.sun.javafx.scene.layout.region.LayeredBorderStyleConverter;
import com.sun.javafx.scene.layout.region.Margins;
import com.sun.javafx.scene.layout.region.RepeatStructConverter;
import com.sun.javafx.scene.layout.region.SliceSequenceConverter;
import com.sun.javafx.scene.layout.region.StrokeBorderPaintConverter;
import com.sun.javafx.util.Logging;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.ColorConverter;
import javafx.css.converter.CursorConverter;
import javafx.css.converter.DeriveColorConverter;
import javafx.css.converter.DeriveSizeConverter;
import javafx.css.converter.DurationConverter;
import javafx.css.converter.EffectConverter;
import javafx.css.converter.EnumConverter;
import javafx.css.converter.FontConverter;
import javafx.css.converter.InsetsConverter;
import javafx.css.converter.LadderConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StopConverter;
import javafx.css.converter.StringConverter;
import javafx.css.converter.URLConverter;
import javafx.geometry.Insets;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class StyleConverter<F, T> {
   private static Map<ParsedValue, Object> cache;
   private static Map<String, StyleConverter<?, ?>> tmap;

   public T convert(ParsedValue<F, T> var1, Font var2) {
      return (T)var1.getValue();
   }

   public static StyleConverter<String, Boolean> getBooleanConverter() {
      return BooleanConverter.getInstance();
   }

   public static StyleConverter<?, Duration> getDurationConverter() {
      return DurationConverter.getInstance();
   }

   public static StyleConverter<String, Color> getColorConverter() {
      return ColorConverter.getInstance();
   }

   public static StyleConverter<ParsedValue[], Effect> getEffectConverter() {
      return EffectConverter.getInstance();
   }

   public static <E extends Enum<E>> StyleConverter<String, E> getEnumConverter(Class<E> var0) {
      return new EnumConverter<>(var0);
   }

   public static StyleConverter<ParsedValue[], Font> getFontConverter() {
      return FontConverter.getInstance();
   }

   public static StyleConverter<ParsedValue[], Insets> getInsetsConverter() {
      return InsetsConverter.getInstance();
   }

   public static StyleConverter<ParsedValue<?, Paint>, Paint> getPaintConverter() {
      return PaintConverter.getInstance();
   }

   public static StyleConverter<?, Number> getSizeConverter() {
      return SizeConverter.getInstance();
   }

   public static StyleConverter<String, String> getStringConverter() {
      return StringConverter.getInstance();
   }

   public static StyleConverter<ParsedValue[], String> getUrlConverter() {
      return URLConverter.getInstance();
   }

   public T convert(Map<CssMetaData<? extends Styleable, ?>, Object> var1) {
      return null;
   }

   public void writeBinary(DataOutputStream var1, StyleConverter.StringStore var2) throws IOException {
      String var3 = this.getClass().getName();
      int var4 = var2.addString(var3);
      var1.writeShort(var4);
   }

   public static void clearCache() {
      if (cache != null) {
         cache.clear();
      }
   }

   protected T getCachedValue(ParsedValue var1) {
      return (T)(cache != null ? cache.get(var1) : null);
   }

   protected void cacheValue(ParsedValue var1, Object var2) {
      if (cache == null) {
         cache = new WeakHashMap<>();
      }

      cache.put(var1, var2);
   }

   public static StyleConverter<?, ?> readBinary(DataInputStream var0, String[] var1) throws IOException {
      short var2 = var0.readShort();
      String var3 = var1[var2];
      if (var3 != null && !var3.isEmpty()) {
         if (var3.startsWith("com.sun.javafx.css.converters.")) {
            var3 = "javafx.css.converter." + var3.substring("com.sun.javafx.css.converters.".length());
         }

         if (var3.startsWith("javafx.css.converter.EnumConverter")) {
            return EnumConverter.readBinary(var0, var1);
         } else if (tmap != null && tmap.containsKey(var3)) {
            return tmap.get(var3);
         } else {
            StyleConverter var4 = getInstance(var3);
            if (var4 == null) {
               PlatformLogger var5 = Logging.getCSSLogger();
               if (var5.isLoggable(Level.SEVERE)) {
                  var5.severe("could not deserialize " + var3);
               }
            }

            if (var4 == null) {
               System.err.println("could not deserialize " + var3);
            }

            if (tmap == null) {
               tmap = new HashMap<>();
            }

            tmap.put(var3, var4);
            return var4;
         }
      } else {
         return null;
      }
   }

   static StyleConverter<?, ?> getInstance(String var0) {
      Object var1 = null;
      switch (var0) {
         case "javafx.css.converter.BooleanConverter":
            var1 = BooleanConverter.getInstance();
            break;
         case "javafx.css.converter.ColorConverter":
            var1 = ColorConverter.getInstance();
            break;
         case "javafx.css.converter.CursorConverter":
            var1 = CursorConverter.getInstance();
            break;
         case "javafx.css.converter.EffectConverter":
            var1 = EffectConverter.getInstance();
            break;
         case "javafx.css.converter.EffectConverter$DropShadowConverter":
            var1 = EffectConverter.DropShadowConverter.getInstance();
            break;
         case "javafx.css.converter.EffectConverter$InnerShadowConverter":
            var1 = EffectConverter.InnerShadowConverter.getInstance();
            break;
         case "javafx.css.converter.FontConverter":
            var1 = FontConverter.getInstance();
            break;
         case "javafx.css.converter.FontConverter$FontStyleConverter":
         case "javafx.css.converter.FontConverter$StyleConverter":
            var1 = FontConverter.FontStyleConverter.getInstance();
            break;
         case "javafx.css.converter.FontConverter$FontWeightConverter":
         case "javafx.css.converter.FontConverter$WeightConverter":
            var1 = FontConverter.FontWeightConverter.getInstance();
            break;
         case "javafx.css.converter.FontConverter$FontSizeConverter":
         case "javafx.css.converter.FontConverter$SizeConverter":
            var1 = FontConverter.FontSizeConverter.getInstance();
            break;
         case "javafx.css.converter.InsetsConverter":
            var1 = InsetsConverter.getInstance();
            break;
         case "javafx.css.converter.InsetsConverter$SequenceConverter":
            var1 = InsetsConverter.SequenceConverter.getInstance();
            break;
         case "javafx.css.converter.PaintConverter":
            var1 = PaintConverter.getInstance();
            break;
         case "javafx.css.converter.PaintConverter$SequenceConverter":
            var1 = PaintConverter.SequenceConverter.getInstance();
            break;
         case "javafx.css.converter.PaintConverter$LinearGradientConverter":
            var1 = PaintConverter.LinearGradientConverter.getInstance();
            break;
         case "javafx.css.converter.PaintConverter$RadialGradientConverter":
            var1 = PaintConverter.RadialGradientConverter.getInstance();
            break;
         case "javafx.css.converter.SizeConverter":
            var1 = SizeConverter.getInstance();
            break;
         case "javafx.css.converter.SizeConverter$SequenceConverter":
            var1 = SizeConverter.SequenceConverter.getInstance();
            break;
         case "javafx.css.converter.StringConverter":
            var1 = StringConverter.getInstance();
            break;
         case "javafx.css.converter.StringConverter$SequenceConverter":
            var1 = StringConverter.SequenceConverter.getInstance();
            break;
         case "javafx.css.converter.URLConverter":
            var1 = URLConverter.getInstance();
            break;
         case "javafx.css.converter.URLConverter$SequenceConverter":
            var1 = URLConverter.SequenceConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.BackgroundPositionConverter":
         case "com.sun.javafx.scene.layout.region.BackgroundImage$BackgroundPositionConverter":
            var1 = BackgroundPositionConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.BackgroundSizeConverter":
         case "com.sun.javafx.scene.layout.region.BackgroundImage$BackgroundSizeConverter":
            var1 = BackgroundSizeConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.BorderImageSliceConverter":
         case "com.sun.javafx.scene.layout.region.BorderImage$SliceConverter":
            var1 = BorderImageSliceConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.BorderImageWidthConverter":
            var1 = BorderImageWidthConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.BorderImageWidthsSequenceConverter":
            var1 = BorderImageWidthsSequenceConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.BorderStrokeStyleSequenceConverter":
         case "com.sun.javafx.scene.layout.region.StrokeBorder$BorderStyleSequenceConverter":
            var1 = BorderStrokeStyleSequenceConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.BorderStyleConverter":
         case "com.sun.javafx.scene.layout.region.StrokeBorder$BorderStyleConverter":
            var1 = BorderStyleConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.LayeredBackgroundPositionConverter":
         case "com.sun.javafx.scene.layout.region.BackgroundImage$LayeredBackgroundPositionConverter":
            var1 = LayeredBackgroundPositionConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.LayeredBackgroundSizeConverter":
         case "com.sun.javafx.scene.layout.region.BackgroundImage$LayeredBackgroundSizeConverter":
            var1 = LayeredBackgroundSizeConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.LayeredBorderPaintConverter":
         case "com.sun.javafx.scene.layout.region.StrokeBorder$LayeredBorderPaintConverter":
            var1 = LayeredBorderPaintConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.LayeredBorderStyleConverter":
         case "com.sun.javafx.scene.layout.region.StrokeBorder$LayeredBorderStyleConverter":
            var1 = LayeredBorderStyleConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.RepeatStructConverter":
         case "com.sun.javafx.scene.layout.region.BackgroundImage$BackgroundRepeatConverter":
         case "com.sun.javafx.scene.layout.region.BorderImage$RepeatConverter":
            var1 = RepeatStructConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.SliceSequenceConverter":
         case "com.sun.javafx.scene.layout.region.BorderImage$SliceSequenceConverter":
            var1 = SliceSequenceConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.StrokeBorderPaintConverter":
         case "com.sun.javafx.scene.layout.region.StrokeBorder$BorderPaintConverter":
            var1 = StrokeBorderPaintConverter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.Margins$Converter":
            var1 = Margins.Converter.getInstance();
            break;
         case "com.sun.javafx.scene.layout.region.Margins$SequenceConverter":
            var1 = Margins.SequenceConverter.getInstance();
            break;
         case "javafx.scene.layout.CornerRadiiConverter":
         case "com.sun.javafx.scene.layout.region.CornerRadiiConverter":
            var1 = CornerRadiiConverter.getInstance();
            break;
         case "javafx.css.converter.DeriveColorConverter":
         case "com.sun.javafx.css.parser.DeriveColorConverter":
            var1 = DeriveColorConverter.getInstance();
            break;
         case "javafx.css.converter.DeriveSizeConverter":
         case "com.sun.javafx.css.parser.DeriveSizeConverter":
            var1 = DeriveSizeConverter.getInstance();
            break;
         case "javafx.css.converter.LadderConverter":
         case "com.sun.javafx.css.parser.LadderConverter":
            var1 = LadderConverter.getInstance();
            break;
         case "javafx.css.converter.StopConverter":
         case "com.sun.javafx.css.parser.StopConverter":
            var1 = StopConverter.getInstance();
            break;
         default:
            PlatformLogger var4 = Logging.getCSSLogger();
            if (var4.isLoggable(Level.SEVERE)) {
               var4.severe("StyleConverter : converter Class is null for : " + var0);
            }
      }

      return (StyleConverter<?, ?>)var1;
   }

   public static class StringStore {
      private final Map<String, Integer> stringMap = new HashMap<>();
      public final List<String> strings = new ArrayList<>();

      public int addString(String var1) {
         Integer var2 = this.stringMap.get(var1);
         if (var2 == null) {
            var2 = this.strings.size();
            this.strings.add(var1);
            this.stringMap.put(var1, var2);
         }

         return var2;
      }

      public void writeBinary(DataOutputStream var1) throws IOException {
         var1.writeShort(this.strings.size());
         if (this.stringMap.containsKey(null)) {
            Integer var2 = this.stringMap.get(null);
            var1.writeShort(var2);
         } else {
            var1.writeShort(-1);
         }

         for (int var4 = 0; var4 < this.strings.size(); var4++) {
            String var3 = this.strings.get(var4);
            if (var3 != null) {
               var1.writeUTF(var3);
            }
         }
      }

      public static String[] readBinary(DataInputStream var0) throws IOException {
         short var1 = var0.readShort();
         short var2 = var0.readShort();
         String[] var3 = new String[var1];
         Arrays.fill(var3, null);

         for (int var4 = 0; var4 < var1; var4++) {
            if (var4 != var2) {
               var3[var4] = var0.readUTF();
            }
         }

         return var3;
      }
   }
}
