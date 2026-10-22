package org.eclipse.yasson.internal.deserializer;

import jakarta.json.stream.JsonParser;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.InstanceCreator;
import org.eclipse.yasson.internal.JsonbConfigProperties;

class MapInstanceCreator implements ModelDeserializer<JsonParser> {
   private final MapDeserializer delegate;
   private final JsonbConfigProperties configProperties;
   private final Class<?> clazz;

   MapInstanceCreator(MapDeserializer delegate, JsonbConfigProperties configProperties, Class<?> clazz) {
      this.delegate = delegate;
      this.configProperties = configProperties;
      this.clazz = clazz;
   }

   public Object deserialize(JsonParser value, DeserializationContextImpl context) {
      Map<?, ?> map = this.createInstance(this.clazz);
      context.setInstance(map);
      return this.delegate.deserialize(value, context);
   }

   private Map<?, ?> createInstance(Class<?> clazz) {
      return clazz.isInterface() ? this.getMapImpl(clazz) : InstanceCreator.createInstance((Class<Map<?, ?>>)clazz);
   }

   private Map<?, ?> getMapImpl(Class<?> ifcType) {
      if (ConcurrentMap.class.isAssignableFrom(ifcType)) {
         return (Map<?, ?>)(!SortedMap.class.isAssignableFrom(ifcType) && !NavigableMap.class.isAssignableFrom(ifcType)
            ? new ConcurrentHashMap()
            : new ConcurrentSkipListMap());
      } else if (SortedMap.class.isAssignableFrom(ifcType)) {
         Class<?> defaultMapImplType = this.configProperties.getDefaultMapImplType();
         return (Map<?, ?>)(SortedMap.class.isAssignableFrom(defaultMapImplType)
            ? InstanceCreator.createInstance((Class<Map<?, ?>>)defaultMapImplType)
            : new TreeMap());
      } else {
         return new HashMap();
      }
   }
}
