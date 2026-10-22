package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsExternalTypeDeserializer extends AsArrayTypeDeserializer {
   private static final long serialVersionUID = 1L;

   public AsExternalTypeDeserializer(JavaType bt, TypeIdResolver idRes, String typePropertyName, boolean typeIdVisible, JavaType defaultImpl) {
      super(bt, idRes, typePropertyName, typeIdVisible, defaultImpl);
   }

   public AsExternalTypeDeserializer(AsExternalTypeDeserializer src, BeanProperty property) {
      super(src, property);
   }

   @Override
   public TypeDeserializer forProperty(BeanProperty prop) {
      return prop == this._property ? this : new AsExternalTypeDeserializer(this, prop);
   }

   @Override
   public As getTypeInclusion() {
      return As.EXTERNAL_PROPERTY;
   }

   @Override
   protected boolean _usesExternalId() {
      return true;
   }
}
