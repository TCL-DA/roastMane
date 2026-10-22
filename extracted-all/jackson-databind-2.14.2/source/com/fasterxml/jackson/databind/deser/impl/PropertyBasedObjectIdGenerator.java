package com.fasterxml.jackson.databind.deser.impl;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerator.IdKey;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.PropertyGenerator;

public class PropertyBasedObjectIdGenerator extends PropertyGenerator {
   private static final long serialVersionUID = 1L;

   public PropertyBasedObjectIdGenerator(Class<?> scope) {
      super(scope);
   }

   public Object generateId(Object forPojo) {
      throw new UnsupportedOperationException();
   }

   public ObjectIdGenerator<Object> forScope(Class<?> scope) {
      return scope == this._scope ? this : new PropertyBasedObjectIdGenerator(scope);
   }

   public ObjectIdGenerator<Object> newForSerialization(Object context) {
      return this;
   }

   public IdKey key(Object key) {
      return key == null ? null : new IdKey(this.getClass(), this._scope, key);
   }
}
