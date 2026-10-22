package org.eclipse.yasson.internal.deserializer;

import jakarta.json.bind.JsonbException;
import jakarta.json.stream.JsonParser;
import jakarta.json.stream.JsonParser.Event;
import java.util.Map;
import org.eclipse.yasson.internal.DeserializationContextImpl;

class MapDeserializer implements ModelDeserializer<JsonParser> {
   private final ModelDeserializer<JsonParser> keyDelegate;
   private final ModelDeserializer<JsonParser> valueDelegate;

   MapDeserializer(ModelDeserializer<JsonParser> keyDelegate, ModelDeserializer<JsonParser> valueDelegate) {
      this.keyDelegate = keyDelegate;
      this.valueDelegate = valueDelegate;
   }

   public Object deserialize(JsonParser parser, DeserializationContextImpl context) {
      Map<Object, Object> map = (Map<Object, Object>)context.getInstance();
      Object key = null;
      Object keyValue = null;
      String keyName = null;
      MapDeserializer.Mode mode = MapDeserializer.Mode.NONE;
      MapDeserializer.State state = MapDeserializer.State.NEXT;

      while (parser.hasNext()) {
         Event next = parser.next();
         context.setLastValueEvent(next);
         switch (next) {
            case KEY_NAME:
               mode = mode == MapDeserializer.Mode.NONE ? MapDeserializer.Mode.NORMAL : mode;
               if (mode == MapDeserializer.Mode.NORMAL) {
                  keyValue = this.deserializeValue(parser, context, this.keyDelegate);
               }

               keyName = parser.getString();
               break;
            case START_OBJECT:
               mode = mode == MapDeserializer.Mode.NONE ? MapDeserializer.Mode.OBJECT : mode;
            case START_ARRAY:
            case VALUE_STRING:
            case VALUE_TRUE:
            case VALUE_FALSE:
            case VALUE_NUMBER:
            case VALUE_NULL:
               if (mode == MapDeserializer.Mode.OBJECT) {
                  if (state == MapDeserializer.State.NEXT) {
                     state = MapDeserializer.State.KEY;
                  } else if (state == MapDeserializer.State.KEY) {
                     this.validateKeyName(keyName, state);
                     key = this.deserializeValue(parser, context, this.keyDelegate);
                     state = MapDeserializer.State.VALUE;
                  } else {
                     if (state != MapDeserializer.State.VALUE) {
                        throw new JsonbException("Only attributes 'key' and 'value' allowed!");
                     }

                     this.validateKeyName(keyName, state);
                     Object value = this.deserializeValue(parser, context, this.valueDelegate);
                     map.put(key, value);
                     state = MapDeserializer.State.DONE;
                  }
               } else {
                  Object value = this.deserializeValue(parser, context, this.valueDelegate);
                  map.put(keyValue, value);
               }
               break;
            case END_OBJECT:
               state = MapDeserializer.State.NEXT;
               if (mode == MapDeserializer.Mode.OBJECT) {
                  break;
               }
            case END_ARRAY:
               return map;
            default:
               throw new JsonbException("Unexpected state: " + next);
         }
      }

      return map;
   }

   private void validateKeyName(String keyName, MapDeserializer.State state) {
      if (state == MapDeserializer.State.KEY && !keyName.equals("key")) {
         throw new JsonbException("Attribute name has to be 'key' when representing map entry key. Got: " + keyName);
      } else if (state == MapDeserializer.State.VALUE && !keyName.equals("value")) {
         throw new JsonbException("Attribute name has to be 'value' when representing map entry value. Got: " + keyName);
      }
   }

   private Object deserializeValue(JsonParser parser, DeserializationContextImpl context, ModelDeserializer<JsonParser> deserializer) {
      DeserializationContextImpl keyContext = new DeserializationContextImpl(context);
      return deserializer.deserialize(parser, keyContext);
   }

   private static enum Mode {
      NONE,
      NORMAL,
      OBJECT;
   }

   private static enum State {
      NEXT,
      VALUE,
      KEY,
      DONE;
   }
}
