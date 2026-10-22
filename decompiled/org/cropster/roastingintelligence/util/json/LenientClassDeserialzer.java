package org.cropster.roastingintelligence.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LenientClassDeserialzer extends StdDeserializer<Class<?>> {
   private static final long serialVersionUID = 1L;
   private final Logger logger = LoggerFactory.getLogger(LenientClassDeserialzer.class);

   protected LenientClassDeserialzer() {
      super(Class.class);
   }

   public Class<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Class<?> clazz = null;
      if (jp.getCurrentToken() == JsonToken.VALUE_STRING) {
         String className = jp.getValueAsString();

         try {
            clazz = Class.forName(className);
         } catch (ClassNotFoundException var6) {
            this.logger.warn("Class not found: {}", className);
         }
      }

      return clazz;
   }
}
