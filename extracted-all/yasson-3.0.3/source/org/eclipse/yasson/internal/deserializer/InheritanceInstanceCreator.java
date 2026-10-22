package org.eclipse.yasson.internal.deserializer;

import jakarta.json.JsonObject;
import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.jsonstructure.JsonStructureToParserAdapter;
import org.eclipse.yasson.internal.model.customization.TypeInheritanceConfiguration;

class InheritanceInstanceCreator implements ModelDeserializer<JsonParser> {
   private final Class<?> processedType;
   private final Map<String, Class<?>> resolvedClasses = new ConcurrentHashMap<>();
   private final DeserializationModelCreator deserializationModelCreator;
   private final TypeInheritanceConfiguration typeInheritanceConfiguration;
   private final ModelDeserializer<JsonParser> defaultProcessor;

   InheritanceInstanceCreator(
      Class<?> processedType,
      DeserializationModelCreator deserializationModelCreator,
      TypeInheritanceConfiguration typeInheritanceConfiguration,
      ModelDeserializer<JsonParser> defaultProcessor
   ) {
      this.processedType = processedType;
      this.deserializationModelCreator = deserializationModelCreator;
      this.typeInheritanceConfiguration = typeInheritanceConfiguration;
      this.defaultProcessor = defaultProcessor;
   }

   public Object deserialize(JsonParser parser, DeserializationContextImpl context) {
      String polymorphismKeyName = this.typeInheritanceConfiguration.getFieldName();
      JsonObject object = parser.getObject();
      String alias = object.getString(polymorphismKeyName, null);
      JsonObject newJsonObject = context.getJsonbContext().getJsonProvider().createObjectBuilder(object).remove(polymorphismKeyName).build();
      JsonParser jsonParser = new JsonStructureToParserAdapter(newJsonObject);
      Event event = jsonParser.next();
      context.setLastValueEvent(event);
      if (alias == null) {
         return this.defaultProcessor.deserialize(jsonParser, context);
      } else {
         Class<?> polymorphicTypeClass = this.getPolymorphicTypeClass(alias);
         if (polymorphicTypeClass.equals(this.processedType)) {
            return this.defaultProcessor.deserialize(jsonParser, context);
         } else {
            ModelDeserializer<JsonParser> deserializer = this.deserializationModelCreator.deserializerChain(polymorphicTypeClass);
            return deserializer.deserialize(jsonParser, context);
         }
      }
   }

   @Override
   public String toString() {
      return "Property " + this.typeInheritanceConfiguration.getFieldName() + " polymorphic information handler";
   }

   private Class<?> getPolymorphicTypeClass(String alias) {
      if (this.resolvedClasses.containsKey(alias)) {
         return this.resolvedClasses.get(alias);
      } else {
         for (Entry<Class<?>, String> entry : this.typeInheritanceConfiguration.getAliases().entrySet()) {
            if (entry.getValue().equals(alias)) {
               this.resolvedClasses.put(alias, entry.getKey());
               return entry.getKey();
            }
         }

         throw new JsonbException(
            "Unknown alias \""
               + alias
               + "\" of the type "
               + this.processedType.getName()
               + ". Known aliases: "
               + this.typeInheritanceConfiguration.getAliases().values()
         );
      }
   }
}
