package com.segment.analytics.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Date;

public class ISO8601DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {
   public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(Iso8601Utils.format(src));
   }

   public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return Iso8601Utils.parse(json.getAsString());
   }
}
