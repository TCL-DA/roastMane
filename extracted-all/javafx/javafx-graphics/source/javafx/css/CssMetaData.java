package javafx.css;

import java.util.Collections;
import java.util.List;

public abstract class CssMetaData<S extends Styleable, V> {
   private final String property;
   private final StyleConverter<?, V> converter;
   private final V initialValue;
   private final List<CssMetaData<? extends Styleable, ?>> subProperties;
   private final boolean inherits;

   @Deprecated(
      since = "8"
   )
   public void set(S var1, V var2, StyleOrigin var3) {
      StyleableProperty var4 = this.getStyleableProperty((S)var1);
      StyleOrigin var5 = var4.getStyleOrigin();
      Object var6 = var4.getValue();
      if (var5 != var3 || (var6 != null ? !var6.equals(var2) : var2 != null)) {
         var4.applyStyle(var3, var2);
      }
   }

   public abstract boolean isSettable(S var1);

   public abstract StyleableProperty<V> getStyleableProperty(S var1);

   public final String getProperty() {
      return this.property;
   }

   public final StyleConverter<?, V> getConverter() {
      return this.converter;
   }

   public V getInitialValue(S var1) {
      return this.initialValue;
   }

   public final List<CssMetaData<? extends Styleable, ?>> getSubProperties() {
      return this.subProperties;
   }

   public final boolean isInherits() {
      return this.inherits;
   }

   protected CssMetaData(String var1, StyleConverter<?, V> var2, V var3, boolean var4, List<CssMetaData<? extends Styleable, ?>> var5) {
      this.property = var1;
      this.converter = var2;
      this.initialValue = (V)var3;
      this.inherits = var4;
      this.subProperties = var5 != null ? Collections.unmodifiableList(var5) : null;
      if (this.property == null || this.converter == null) {
         throw new IllegalArgumentException("neither property nor converter can be null");
      }
   }

   protected CssMetaData(String var1, StyleConverter<?, V> var2, V var3, boolean var4) {
      this(var1, var2, (V)var3, var4, null);
   }

   protected CssMetaData(String var1, StyleConverter<?, V> var2, V var3) {
      this(var1, var2, (V)var3, false, null);
   }

   protected CssMetaData(String var1, StyleConverter<?, V> var2) {
      this(var1, var2, null, false, null);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 == null) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         CssMetaData var2 = (CssMetaData)var1;
         return this.property == null ? var2.property == null : this.property.equals(var2.property);
      }
   }

   @Override
   public int hashCode() {
      byte var1 = 3;
      return 19 * var1 + (this.property != null ? this.property.hashCode() : 0);
   }

   @Override
   public String toString() {
      return "CSSProperty {"
         + "property: "
         + this.property
         + ", converter: "
         + this.converter.toString()
         + ", initalValue: "
         + this.initialValue
         + ", inherits: "
         + this.inherits
         + ", subProperties: "
         + (this.subProperties != null ? this.subProperties.toString() : "[]")
         + "}";
   }
}
