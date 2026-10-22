package org.eclipse.yasson.internal.components;

import jakarta.json.bind.adapter.JsonbAdapter;
import java.lang.reflect.Type;
import java.util.Objects;

public class AdapterBinding extends AbstractComponentBinding {
   private final Type toType;
   private final JsonbAdapter<?, ?> adapter;

   public AdapterBinding(Type fromType, Type toType, JsonbAdapter<?, ?> adapter) {
      super(fromType);
      Objects.requireNonNull(toType);
      Objects.requireNonNull(adapter);
      this.toType = toType;
      this.adapter = adapter;
   }

   public Type getToType() {
      return this.toType;
   }

   public JsonbAdapter<?, ?> getAdapter() {
      return this.adapter;
   }

   @Override
   public Class<?> getComponentClass() {
      return this.adapter.getClass();
   }
}
