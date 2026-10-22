package org.cropster.roastingintelligence.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.Date;
import org.cropster.roastingintelligence.Defaults;

public class ScheduledDeserializer extends StdDeserializer<String> {
   private static final long serialVersionUID = 531524279028225555L;

   protected ScheduledDeserializer() {
      super(String.class);
   }

   public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
      Date date = null;
      String localDate = null;

      while (jp.nextToken() != JsonToken.END_OBJECT) {
         if ("dateTime".equalsIgnoreCase(jp.getCurrentName()) && jp.nextToken() == JsonToken.VALUE_NUMBER_INT) {
            date = new Date(jp.getLongValue());
         } else if ("localDate".equalsIgnoreCase(jp.getCurrentName()) && jp.nextToken() == JsonToken.START_ARRAY) {
            localDate = String.format("%04d-%02d-%02d", jp.nextIntValue(0), jp.nextIntValue(0), jp.nextIntValue(0));
            jp.nextToken();
         }
      }

      return localDate != null ? localDate : Defaults.DATE_FORMAT.format(date);
   }
}
