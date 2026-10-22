package org.eclipse.yasson.internal.serializer.types;

import java.lang.reflect.Type;
import java.util.List;
import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.model.customization.Customization;

class TypeSerializerBuilder {
   private final List<Type> chain;
   private final Class<?> clazz;
   private final Customization customization;
   private final JsonbContext jsonbContext;
   private final boolean key;

   TypeSerializerBuilder(List<Type> chain, Class<?> clazz, Customization customization, JsonbContext jsonbContext, boolean key) {
      this.chain = chain;
      this.clazz = clazz;
      this.customization = customization;
      this.jsonbContext = jsonbContext;
      this.key = key;
   }

   public List<Type> getChain() {
      return this.chain;
   }

   public Class<?> getClazz() {
      return this.clazz;
   }

   public Customization getCustomization() {
      return this.customization;
   }

   public JsonbContext getJsonbContext() {
      return this.jsonbContext;
   }

   public boolean isKey() {
      return this.key;
   }
}
