package org.eclipse.yasson.internal.model.customization;

import org.eclipse.yasson.internal.components.AdapterBinding;
import org.eclipse.yasson.internal.components.DeserializerBinding;
import org.eclipse.yasson.internal.components.SerializerBinding;

abstract class CustomizationBase implements Customization, ComponentBoundCustomization {
   private final AdapterBinding adapterBinding;
   private final SerializerBinding<?> serializerBinding;
   private final DeserializerBinding<?> deserializerBinding;
   private final boolean nillable;

   CustomizationBase(CustomizationBase.Builder<?, ?> builder) {
      this.nillable = builder.nillable;
      this.adapterBinding = builder.adapterBinding;
      this.serializerBinding = builder.serializerBinding;
      this.deserializerBinding = builder.deserializerBinding;
   }

   @Override
   public boolean isNillable() {
      return this.nillable;
   }

   @Override
   public AdapterBinding getSerializeAdapterBinding() {
      return this.adapterBinding;
   }

   @Override
   public AdapterBinding getDeserializeAdapterBinding() {
      return this.adapterBinding;
   }

   @Override
   public SerializerBinding<?> getSerializerBinding() {
      return this.serializerBinding;
   }

   @Override
   public DeserializerBinding<?> getDeserializerBinding() {
      return this.deserializerBinding;
   }

   abstract static class Builder<T extends CustomizationBase.Builder<T, B>, B extends CustomizationBase> {
      private AdapterBinding adapterBinding;
      private SerializerBinding<?> serializerBinding;
      private DeserializerBinding<?> deserializerBinding;
      private boolean nillable;

      public T of(B customization) {
         this.adapterBinding = customization.getDeserializeAdapterBinding();
         this.serializerBinding = customization.getSerializerBinding();
         this.deserializerBinding = customization.getDeserializerBinding();
         this.nillable = customization.isNillable();
         return (T)this;
      }

      public T adapterBinding(AdapterBinding adapterBinding) {
         this.adapterBinding = adapterBinding;
         return (T)this;
      }

      public T serializerBinding(SerializerBinding<?> serializerBinding) {
         this.serializerBinding = serializerBinding;
         return (T)this;
      }

      public T deserializerBinding(DeserializerBinding<?> deserializerBinding) {
         this.deserializerBinding = deserializerBinding;
         return (T)this;
      }

      public T nillable(boolean nillable) {
         this.nillable = nillable;
         return (T)this;
      }

      public abstract B build();
   }
}
