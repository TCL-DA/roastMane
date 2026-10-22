package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.model.CreatorModel;
import org.eclipse.yasson.internal.model.JsonbCreator;
import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

class JsonbCreatorDeserializer implements ModelDeserializer<JsonParser> {
   private final Map<String, ModelDeserializer<JsonParser>> propertyDeserializerChains;
   private final Map<String, ModelDeserializer<Object>> defaultCreatorValues;
   private final List<String> creatorParams;
   private final Set<String> ignoredProperties;
   private final JsonbCreator creator;
   private final Class<?> clazz;
   private final Function<String, String> renamer;
   private final boolean failOnUnknownProperties;

   JsonbCreatorDeserializer(
      Map<String, ModelDeserializer<JsonParser>> propertyDeserializerChains,
      Map<String, ModelDeserializer<Object>> defaultCreatorValues,
      JsonbCreator creator,
      Class<?> clazz,
      Function<String, String> renamer,
      boolean failOnUnknownProperties,
      Set<String> ignoredProperties
   ) {
      this.propertyDeserializerChains = propertyDeserializerChains;
      this.defaultCreatorValues = defaultCreatorValues;
      this.creatorParams = Arrays.stream(creator.getParams()).map(CreatorModel::getName).collect(Collectors.toList());
      this.ignoredProperties = Set.copyOf(ignoredProperties);
      this.creator = creator;
      this.clazz = clazz;
      this.renamer = renamer;
      this.failOnUnknownProperties = failOnUnknownProperties;
   }

   public Object deserialize(JsonParser parser, DeserializationContextImpl context) {
      String key = null;
      Map<String, Object> paramValues = new HashMap<>();

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
                     Object o = this.propertyDeserializerChains.get(key).deserialize(parser, context);
                     if (this.creatorParams.contains(key)) {
                        paramValues.put(key, o);
                     }
                  } catch (JsonbException var9) {
                     throw new JsonbException("Unable to deserialize property '" + key + "' because of: " + var9.getMessage(), var9);
                  }
               } else {
                  if (this.failOnUnknownProperties && !this.ignoredProperties.contains(key)) {
                     throw new JsonbException(Messages.getMessage(MessageKeys.UNKNOWN_JSON_PROPERTY, key, this.clazz));
                  }

                  ObjectDeserializer.VALUE_SKIPPERS.getOrDefault(next, ObjectDeserializer.NOOP).accept(parser);
               }
               break;
            case END_OBJECT:
               Object[] params = new Object[this.creatorParams.size()];

               for (int i = 0; i < this.creatorParams.size(); i++) {
                  String param = this.creatorParams.get(i);
                  if (paramValues.containsKey(param)) {
                     params[i] = paramValues.get(param);
                  } else {
                     params[i] = this.defaultCreatorValues.get(param).deserialize(null, context);
                  }
               }

               context.setInstance(this.creator.call(params, this.clazz));
               context.getDeferredDeserializers().forEach(Runnable::run);
               context.getDeferredDeserializers().clear();
               return context.getInstance();
            default:
               throw new JsonbException("Unexpected state: " + next);
         }
      }

      return context.getInstance();
   }

   @Override
   public String toString() {
      return "ObjectInstanceCreator{parameters=" + this.creatorParams + ", clazz=" + this.clazz + "}";
   }
}
