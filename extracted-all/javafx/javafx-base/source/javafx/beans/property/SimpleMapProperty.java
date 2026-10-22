package javafx.beans.property;

import javafx.collections.ObservableMap;

public class SimpleMapProperty<K, V> extends MapPropertyBase<K, V> {
   private static final Object DEFAULT_BEAN = null;
   private static final String DEFAULT_NAME = "";
   private final Object bean;
   private final String name;

   @Override
   public Object getBean() {
      return this.bean;
   }

   @Override
   public String getName() {
      return this.name;
   }

   public SimpleMapProperty() {
      this(DEFAULT_BEAN, "");
   }

   public SimpleMapProperty(ObservableMap<K, V> var1) {
      this(DEFAULT_BEAN, "", var1);
   }

   public SimpleMapProperty(Object var1, String var2) {
      this.bean = var1;
      this.name = var2 == null ? "" : var2;
   }

   public SimpleMapProperty(Object var1, String var2, ObservableMap<K, V> var3) {
      super(var3);
      this.bean = var1;
      this.name = var2 == null ? "" : var2;
   }
}
