package com.fasterxml.jackson.databind.jsontype.impl;

import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.WritableTypeId;
import com.fasterxml.jackson.databind.BeanProperty;
import java.io.IOException;

public class AsDeductionTypeSerializer extends TypeSerializerBase {
   private static final AsDeductionTypeSerializer INSTANCE = new AsDeductionTypeSerializer();

   protected AsDeductionTypeSerializer() {
      super(null, null);
   }

   public static AsDeductionTypeSerializer instance() {
      return INSTANCE;
   }

   public AsDeductionTypeSerializer forProperty(BeanProperty prop) {
      return this;
   }

   @Override
   public As getTypeInclusion() {
      return As.EXISTING_PROPERTY;
   }

   @Override
   public WritableTypeId writeTypePrefix(JsonGenerator g, WritableTypeId idMetadata) throws IOException {
      return idMetadata.valueShape.isStructStart() && !g.canWriteTypeId() ? g.writeTypePrefix(idMetadata) : null;
   }

   @Override
   public WritableTypeId writeTypeSuffix(JsonGenerator g, WritableTypeId idMetadata) throws IOException {
      return idMetadata == null ? null : g.writeTypeSuffix(idMetadata);
   }
}
