package javafx.css;

import javafx.beans.NamedArg;

public class SimpleStyleableObjectProperty<T> extends StyleableObjectProperty<T> {
   private static final Object DEFAULT_BEAN = null;
   private static final String DEFAULT_NAME = "";
   private final Object bean;
   private final String name;
   private final CssMetaData<? extends Styleable, T> cssMetaData;

   public SimpleStyleableObjectProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, T> var1) {
      this(var1, DEFAULT_BEAN, "");
   }

   public SimpleStyleableObjectProperty(@NamedArg("cssMetaData") CssMetaData<? extends Styleable, T> var1, @NamedArg("initialValue") T var2) {
      this(var1, DEFAULT_BEAN, "", (T)var2);
   }

   public SimpleStyleableObjectProperty(
      @NamedArg("cssMetaData") CssMetaData<? extends Styleable, T> var1, @NamedArg("bean") Object var2, @NamedArg("name") String var3
   ) {
      this.bean = var2;
      this.name = var3 == null ? "" : var3;
      this.cssMetaData = var1;
   }

   public SimpleStyleableObjectProperty(
      @NamedArg("cssMetaData") CssMetaData<? extends Styleable, T> var1,
      @NamedArg("bean") Object var2,
      @NamedArg("name") String var3,
      @NamedArg("initialValue") T var4
   ) {
      super((T)var4);
      this.bean = var2;
      this.name = var3 == null ? "" : var3;
      this.cssMetaData = var1;
   }

   public Object getBean() {
      return this.bean;
   }

   public String getName() {
      return this.name;
   }

   @Override
   public final CssMetaData<? extends Styleable, T> getCssMetaData() {
      return this.cssMetaData;
   }
}
