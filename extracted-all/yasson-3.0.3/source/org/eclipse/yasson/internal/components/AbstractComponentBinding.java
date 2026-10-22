package org.eclipse.yasson.internal.components;

import java.lang.reflect.Type;
import java.util.Objects;

public abstract class AbstractComponentBinding {
   private final Type bindingType;

   public AbstractComponentBinding(Type bindingType) {
      Objects.requireNonNull(bindingType);
      this.bindingType = bindingType;
   }

   public Type getBindingType() {
      return this.bindingType;
   }

   public abstract Class<?> getComponentClass();
}
