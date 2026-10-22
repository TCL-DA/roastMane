package com.cropster.roastingintelligence.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
   private final ObjectMapper objectMapper;

   public ObjectMapperProvider(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
   }

   public ObjectMapper getContext(Class<?> type) {
      return this.objectMapper;
   }
}
