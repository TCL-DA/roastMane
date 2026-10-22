package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsArrayTypeSerializer extends TypeSerializerBase {
   public AsArrayTypeSerializer(TypeIdResolver idRes, BeanProperty property) {
      super(idRes, property);
   }

   public AsArrayTypeSerializer forProperty(BeanProperty prop) {
      return this._property == prop ? this : new AsArrayTypeSerializer(this._idResolver, prop);
   }

   @Override
   public As getTypeInclusion() {
      return As.WRAPPER_ARRAY;
   }
}
