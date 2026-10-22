package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;

public class AsPropertyTypeSerializer extends AsArrayTypeSerializer {
   protected final String _typePropertyName;

   public AsPropertyTypeSerializer(TypeIdResolver idRes, BeanProperty property, String propName) {
      super(idRes, property);
      this._typePropertyName = propName;
   }

   public AsPropertyTypeSerializer forProperty(BeanProperty prop) {
      return this._property == prop ? this : new AsPropertyTypeSerializer(this._idResolver, prop, this._typePropertyName);
   }

   @Override
   public String getPropertyName() {
      return this._typePropertyName;
   }

   @Override
   public As getTypeInclusion() {
      return As.PROPERTY;
   }
}
