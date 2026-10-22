package org.eclipse.yasson.internal.components;

import java.lang.reflect.Type;

public class ComponentBindings {
   private final Type bindingType;
   private final SerializerBinding serializer;
   private final DeserializerBinding deserializer;
   private final AdapterBinding adapterInfo;

   public ComponentBindings(Type bindingType) {
      this(bindingType, null, null, null);
   }

   public ComponentBindings(Type bindingType, SerializerBinding serializer, DeserializerBinding deserializer, AdapterBinding adapter) {
      this.bindingType = bindingType;
      this.serializer = serializer;
      this.deserializer = deserializer;
      this.adapterInfo = adapter;
   }

   public Type getBindingType() {
      return this.bindingType;
   }

   public SerializerBinding getSerializer() {
      return this.serializer;
   }

   public DeserializerBinding getDeserializer() {
      return this.deserializer;
   }

   public AdapterBinding getAdapterInfo() {
      return this.adapterInfo;
   }
}
