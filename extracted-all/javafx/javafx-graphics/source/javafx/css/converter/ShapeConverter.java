package javafx.css.converter;

import java.util.Map;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;

public class ShapeConverter extends StyleConverter<String, Shape> {
   private static final ShapeConverter INSTANCE = new ShapeConverter();
   private static Map<ParsedValue<String, Shape>, Shape> cache;

   public static StyleConverter<String, Shape> getInstance() {
      return INSTANCE;
   }

   private ShapeConverter() {
   }

   public Shape convert(ParsedValue<String, Shape> var1, Font var2) {
      Shape var3 = (Shape)super.getCachedValue(var1);
      if (var3 != null) {
         return var3;
      } else {
         String var4 = (String)var1.getValue();
         if (var4 != null && !var4.isEmpty()) {
            SVGPath var5 = new SVGPath();
            var5.setContent(var4);
            super.cacheValue(var1, var5);
            return var5;
         } else {
            return null;
         }
      }
   }

   public static void clearCache() {
      if (cache != null) {
         cache.clear();
      }
   }
}
