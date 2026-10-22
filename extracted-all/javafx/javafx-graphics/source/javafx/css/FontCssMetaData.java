package javafx.css;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.css.converter.FontConverter;
import javafx.css.converter.SizeConverter;
import javafx.css.converter.StringConverter;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public abstract class FontCssMetaData<S extends Styleable> extends CssMetaData<S, Font> {
   public FontCssMetaData(String var1, Font var2) {
      super(var1, FontConverter.getInstance(), var2, true, createSubProperties(var1, var2));
   }

   private static <S extends Styleable> List<CssMetaData<? extends Styleable, ?>> createSubProperties(String var0, Font var1) {
      ArrayList var2 = new ArrayList();
      Font var3 = var1 != null ? var1 : Font.getDefault();
      CssMetaData var4 = new CssMetaData<S, String>(var0.concat("-family"), StringConverter.getInstance(), var3.getFamily(), true) {
         @Override
         public boolean isSettable(S var1) {
            return false;
         }

         @Override
         public StyleableProperty<String> getStyleableProperty(S var1) {
            return null;
         }
      };
      var2.add(var4);
      CssMetaData var5 = new CssMetaData<S, Number>(var0.concat("-size"), SizeConverter.getInstance(), var3.getSize(), true) {
         @Override
         public boolean isSettable(S var1) {
            return false;
         }

         @Override
         public StyleableProperty<Number> getStyleableProperty(S var1) {
            return null;
         }
      };
      var2.add(var5);
      CssMetaData var6 = new CssMetaData<S, FontPosture>(var0.concat("-style"), FontConverter.FontStyleConverter.getInstance(), FontPosture.REGULAR, true) {
         @Override
         public boolean isSettable(S var1) {
            return false;
         }

         @Override
         public StyleableProperty<FontPosture> getStyleableProperty(S var1) {
            return null;
         }
      };
      var2.add(var6);
      CssMetaData var7 = new CssMetaData<S, FontWeight>(var0.concat("-weight"), FontConverter.FontWeightConverter.getInstance(), FontWeight.NORMAL, true) {
         @Override
         public boolean isSettable(S var1) {
            return false;
         }

         @Override
         public StyleableProperty<FontWeight> getStyleableProperty(S var1) {
            return null;
         }
      };
      var2.add(var7);
      return Collections.unmodifiableList(var2);
   }
}
