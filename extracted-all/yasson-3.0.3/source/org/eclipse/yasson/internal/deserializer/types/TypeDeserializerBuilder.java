package org.eclipse.yasson.internal.deserializer.types;

import java.util.Objects;
import org.eclipse.yasson.internal.JsonbConfigProperties;
import org.eclipse.yasson.internal.deserializer.ModelDeserializer;
import org.eclipse.yasson.internal.model.customization.ClassCustomization;
import org.eclipse.yasson.internal.model.customization.Customization;

class TypeDeserializerBuilder {
   private final Class<?> clazz;
   private final Customization customization;
   private final JsonbConfigProperties configProperties;
   private final ModelDeserializer<Object> delegate;

   TypeDeserializerBuilder(Class<?> clazz, Customization customization, JsonbConfigProperties configProperties, ModelDeserializer<Object> delegate) {
      this.clazz = Objects.requireNonNull(clazz);
      this.customization = (Customization)(customization == null ? ClassCustomization.empty() : customization);
      this.configProperties = configProperties;
      this.delegate = Objects.requireNonNull(delegate);
   }

   public Class<?> getClazz() {
      return this.clazz;
   }

   public JsonbConfigProperties getConfigProperties() {
      return this.configProperties;
   }

   public ModelDeserializer<Object> getDelegate() {
      return this.delegate;
   }

   public Customization getCustomization() {
      return this.customization;
   }
}
