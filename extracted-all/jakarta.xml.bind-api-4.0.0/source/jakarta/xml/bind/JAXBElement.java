package jakarta.xml.bind;

import java.io.Serializable;
import javax.xml.namespace.QName;

public class JAXBElement<T> implements Serializable {
   protected final QName name;
   protected final Class<T> declaredType;
   protected final Class<?> scope;
   protected T value;
   protected boolean nil = false;
   private static final long serialVersionUID = 1L;

   public JAXBElement(QName name, Class<T> declaredType, Class<?> scope, T value) {
      if (declaredType != null && name != null) {
         this.declaredType = declaredType;
         if (scope == null) {
            scope = JAXBElement.GlobalScope.class;
         }

         this.scope = scope;
         this.name = name;
         this.setValue(value);
      } else {
         throw new IllegalArgumentException();
      }
   }

   public JAXBElement(QName name, Class<T> declaredType, T value) {
      this(name, declaredType, JAXBElement.GlobalScope.class, value);
   }

   public Class<T> getDeclaredType() {
      return this.declaredType;
   }

   public QName getName() {
      return this.name;
   }

   public void setValue(T t) {
      this.value = t;
   }

   public T getValue() {
      return this.value;
   }

   public Class<?> getScope() {
      return this.scope;
   }

   public boolean isNil() {
      return this.value == null || this.nil;
   }

   public void setNil(boolean value) {
      this.nil = value;
   }

   public boolean isGlobalScope() {
      return this.scope == JAXBElement.GlobalScope.class;
   }

   public boolean isTypeSubstituted() {
      return this.value == null ? false : this.value.getClass() != this.declaredType;
   }

   public static final class GlobalScope {
      private GlobalScope() {
      }
   }
}
