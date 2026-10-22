package org.cropster.roastingintelligence.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;

public class ReferenceDeserializer extends StdDeserializer<Long> {
   private static final long serialVersionUID = 1L;

   public ReferenceDeserializer() {
      super(Long.class);
   }

   public Long deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Long id = null;
      if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
         if (jp.getCurrentToken() == JsonToken.VALUE_NUMBER_INT) {
            id = jp.getLongValue();
         } else if (jp.getCurrentToken() != JsonToken.VALUE_NULL) {
            throw new IllegalStateException("No valid reference format!");
         }
      } else {
         while (jp.nextValue() != JsonToken.END_OBJECT) {
            if ("id".equalsIgnoreCase(jp.getCurrentName())) {
               id = jp.getLongValue();
            }
         }

         if (id == null) {
            throw new IllegalStateException("Field \"id\" expected!");
         }
      }

      return id;
   }
}
