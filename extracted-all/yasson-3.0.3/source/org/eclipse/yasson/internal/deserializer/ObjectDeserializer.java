package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class ObjectDeserializer implements ModelDeserializer<JsonParser> {
   static final Consumer<JsonParser> NOOP = jsonParser -> {
   };
   static final EnumMap<Event, Consumer<JsonParser>> VALUE_SKIPPERS = new EnumMap<>(Event.class);
   private final Map<String, ModelDeserializer<JsonParser>> propertyDeserializerChains;
   private final Function<String, String> renamer;
   private final Class<?> rawClass;
   private final boolean failOnUnknownProperty;
   private final Set<String> ignoredProperties;

   ObjectDeserializer(
      Map<String, ModelDeserializer<JsonParser>> propertyDeserializerChains,
      Function<String, String> renamer,
      Class<?> rawClass,
      boolean failOnUnknownProperty,
      Set<String> ignoredProperties
   ) {
      this.propertyDeserializerChains = Map.copyOf(propertyDeserializerChains);
      this.renamer = renamer;
      this.rawClass = rawClass;
      this.failOnUnknownProperty = failOnUnknownProperty;
      this.ignoredProperties = Set.copyOf(ignoredProperties);
   }

   public Object deserialize(JsonParser parser, DeserializationContextImpl context) {
      String key = null;

      while (parser.hasNext()) {
         Event next = parser.next();
         context.setLastValueEvent(next);
         switch (next) {
            case KEY_NAME:
               key = this.renamer.apply(parser.getString());
               break;
            case VALUE_NULL:
            case START_OBJECT:
            case START_ARRAY:
            case VALUE_STRING:
            case VALUE_NUMBER:
            case VALUE_FALSE:
            case VALUE_TRUE:
               if (this.propertyDeserializerChains.containsKey(key)) {
                  try {
                     this.propertyDeserializerChains.get(key).deserialize(parser, context);
                  } catch (JsonbException var6) {
                     throw new JsonbException("Unable to deserialize property '" + key + "' because of: " + var6.getMessage(), var6);
                  }
               } else {
                  if (this.failOnUnknownProperty && !this.ignoredProperties.contains(key)) {
                     throw new JsonbException(Messages.getMessage(MessageKeys.UNKNOWN_JSON_PROPERTY, key, this.rawClass));
                  }

                  VALUE_SKIPPERS.getOrDefault(next, NOOP).accept(parser);
               }
            case END_ARRAY:
               break;
            case END_OBJECT:
               return context.getInstance();
            default:
               throw new JsonbException("Unexpected state: " + next);
         }
      }

      return context.getInstance();
   }

   static {
      VALUE_SKIPPERS.put(Event.START_OBJECT, JsonParser::skipObject);
      VALUE_SKIPPERS.put(Event.START_ARRAY, JsonParser::skipArray);
   }
}
