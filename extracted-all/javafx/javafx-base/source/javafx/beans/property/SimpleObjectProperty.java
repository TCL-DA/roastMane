package javafx.beans.property;

public class SimpleObjectProperty<T> extends ObjectPropertyBase<T> {
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

   public SimpleObjectProperty() {
      this(DEFAULT_BEAN, "");
   }

   public SimpleObjectProperty(T var1) {
      this(DEFAULT_BEAN, "", (T)var1);
   }

   public SimpleObjectProperty(Object var1, String var2) {
      this.bean = var1;
      this.name = var2 == null ? "" : var2;
   }

   public SimpleObjectProperty(Object var1, String var2, T var3) {
      super((T)var3);
      this.bean = var1;
      this.name = var2 == null ? "" : var2;
   }
}
