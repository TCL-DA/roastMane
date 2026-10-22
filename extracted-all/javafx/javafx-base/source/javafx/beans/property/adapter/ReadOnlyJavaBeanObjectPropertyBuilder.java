package javafx.beans.property.adapter;

import com.sun.javafx.property.adapter.ReadOnlyJavaBeanPropertyBuilderHelper;
import com.sun.javafx.property.adapter.ReadOnlyPropertyDescriptor;
import java.lang.reflect.Method;

public final class ReadOnlyJavaBeanObjectPropertyBuilder<T> {
   private final ReadOnlyJavaBeanPropertyBuilderHelper helper = new ReadOnlyJavaBeanPropertyBuilderHelper();

   private ReadOnlyJavaBeanObjectPropertyBuilder() {
   }

   public static <T> ReadOnlyJavaBeanObjectPropertyBuilder<T> create() {
      return new ReadOnlyJavaBeanObjectPropertyBuilder<>();
   }

   public ReadOnlyJavaBeanObjectProperty<T> build() throws NoSuchMethodException {
      ReadOnlyPropertyDescriptor var1 = this.helper.getDescriptor();
      return new ReadOnlyJavaBeanObjectProperty<>(var1, this.helper.getBean());
   }

   public ReadOnlyJavaBeanObjectPropertyBuilder<T> name(String var1) {
      this.helper.name(var1);
      return this;
   }

   public ReadOnlyJavaBeanObjectPropertyBuilder<T> bean(Object var1) {
      this.helper.bean(var1);
      return this;
   }

   public ReadOnlyJavaBeanObjectPropertyBuilder<T> beanClass(Class<?> var1) {
      this.helper.beanClass(var1);
      return this;
   }

   public ReadOnlyJavaBeanObjectPropertyBuilder<T> getter(String var1) {
      this.helper.getterName(var1);
      return this;
   }

   public ReadOnlyJavaBeanObjectPropertyBuilder<T> getter(Method var1) {
      this.helper.getter(var1);
      return this;
   }
}
