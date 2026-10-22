package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.JsonbDateFormatter;
import org.eclipse.yasson.internal.JsonbNumberFormatter;
import org.eclipse.yasson.internal.components.AdapterBinding;

public class PropertyCustomization extends CustomizationBase {
   private final String jsonReadName;
   private final String jsonWriteName;
   private final JsonbNumberFormatter serializeNumberFormatter;
   private final JsonbNumberFormatter deserializeNumberFormatter;
   private final JsonbDateFormatter serializeDateFormatter;
   private final JsonbDateFormatter deserializeDateFormatter;
   private final AdapterBinding serializeAdapter;
   private final AdapterBinding deserializeAdapter;
   private final boolean readTransient;
   private final boolean writeTransient;
   private final Class<?> implementationClass;

   private PropertyCustomization(PropertyCustomization.Builder builder) {
      super(builder);
      this.serializeAdapter = builder.serializeAdapter;
      this.deserializeAdapter = builder.deserializeAdapter;
      this.jsonReadName = builder.jsonReadName;
      this.jsonWriteName = builder.jsonWriteName;
      this.serializeNumberFormatter = builder.serializeNumberFormatter;
      this.deserializeNumberFormatter = builder.deserializeNumberFormatter;
      this.serializeDateFormatter = builder.serializeDateFormatter;
      this.deserializeDateFormatter = builder.deserializeDateFormatter;
      this.readTransient = builder.readTransient;
      this.writeTransient = builder.writeTransient;
      this.implementationClass = builder.implementationClass;
   }

   public static PropertyCustomization.Builder builder() {
      return new PropertyCustomization.Builder();
   }

   public String getJsonReadName() {
      return this.jsonReadName;
   }

   public String getJsonWriteName() {
      return this.jsonWriteName;
   }

   @Override
   public JsonbNumberFormatter getSerializeNumberFormatter() {
      return this.serializeNumberFormatter;
   }

   @Override
   public JsonbNumberFormatter getDeserializeNumberFormatter() {
      return this.deserializeNumberFormatter;
   }

   @Override
   public JsonbDateFormatter getSerializeDateFormatter() {
      return this.serializeDateFormatter;
   }

   @Override
   public JsonbDateFormatter getDeserializeDateFormatter() {
      return this.deserializeDateFormatter;
   }

   public boolean isReadTransient() {
      return this.readTransient;
   }

   public boolean isWriteTransient() {
      return this.writeTransient;
   }

   public Class<?> getImplementationClass() {
      return this.implementationClass;
   }

   @Override
   public AdapterBinding getDeserializeAdapterBinding() {
      return this.deserializeAdapter;
   }

   @Override
   public AdapterBinding getSerializeAdapterBinding() {
      return this.serializeAdapter;
   }

   public static final class Builder extends CustomizationBase.Builder<PropertyCustomization.Builder, PropertyCustomization> {
      private String jsonReadName;
      private String jsonWriteName;
      private JsonbNumberFormatter serializeNumberFormatter;
      private JsonbNumberFormatter deserializeNumberFormatter;
      private JsonbDateFormatter serializeDateFormatter;
      private JsonbDateFormatter deserializeDateFormatter;
      private AdapterBinding serializeAdapter;
      private AdapterBinding deserializeAdapter;
      private boolean readTransient;
      private boolean writeTransient;
      private Class<?> implementationClass;

      private Builder() {
      }

      public PropertyCustomization.Builder of(PropertyCustomization customization) {
         this.jsonReadName = customization.jsonReadName;
         this.jsonWriteName = customization.jsonWriteName;
         this.serializeNumberFormatter = customization.serializeNumberFormatter;
         this.deserializeNumberFormatter = customization.deserializeNumberFormatter;
         this.serializeDateFormatter = customization.serializeDateFormatter;
         this.deserializeDateFormatter = customization.deserializeDateFormatter;
         this.serializeAdapter = customization.serializeAdapter;
         this.deserializeAdapter = customization.deserializeAdapter;
         this.readTransient = customization.readTransient;
         this.writeTransient = customization.writeTransient;
         this.implementationClass = customization.implementationClass;
         return (PropertyCustomization.Builder)super.of(customization);
      }

      public PropertyCustomization.Builder jsonReadName(String jsonReadName) {
         this.jsonReadName = jsonReadName;
         return this;
      }

      public PropertyCustomization.Builder jsonWriteName(String jsonWriteName) {
         this.jsonWriteName = jsonWriteName;
         return this;
      }

      public PropertyCustomization.Builder serializeNumberFormatter(JsonbNumberFormatter serializeNumberFormatter) {
         this.serializeNumberFormatter = serializeNumberFormatter;
         return this;
      }

      public PropertyCustomization.Builder deserializeNumberFormatter(JsonbNumberFormatter deserializeNumberFormatter) {
         this.deserializeNumberFormatter = deserializeNumberFormatter;
         return this;
      }

      public PropertyCustomization.Builder serializeDateFormatter(JsonbDateFormatter serializeDateFormatter) {
         this.serializeDateFormatter = serializeDateFormatter;
         return this;
      }

      public PropertyCustomization.Builder deserializeDateFormatter(JsonbDateFormatter deserializeDateFormatter) {
         this.deserializeDateFormatter = deserializeDateFormatter;
         return this;
      }

      public PropertyCustomization.Builder serializeAdapter(AdapterBinding serializeAdapter) {
         this.serializeAdapter = serializeAdapter;
         return this;
      }

      public PropertyCustomization.Builder deserializeAdapter(AdapterBinding deserializeAdapter) {
         this.deserializeAdapter = deserializeAdapter;
         return this;
      }

      public PropertyCustomization.Builder readTransient(boolean readTransient) {
         this.readTransient = readTransient;
         return this;
      }

      public boolean readTransient() {
         return this.readTransient;
      }

      public PropertyCustomization.Builder writeTransient(boolean writeTransient) {
         this.writeTransient = writeTransient;
         return this;
      }

      public boolean writeTransient() {
         return this.writeTransient;
      }

      public PropertyCustomization.Builder implementationClass(Class<?> implementationClass) {
         this.implementationClass = implementationClass;
         return this;
      }

      public PropertyCustomization build() {
         return new PropertyCustomization(this);
      }
   }
}
