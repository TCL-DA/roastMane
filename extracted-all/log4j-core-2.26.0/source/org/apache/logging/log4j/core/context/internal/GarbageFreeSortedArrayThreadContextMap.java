package org.apache.logging.log4j.core.context.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.spi.ObjectThreadContextMap;
import org.apache.logging.log4j.spi.ReadOnlyThreadContextMap;
import org.apache.logging.log4j.spi.ThreadContextMap;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.apache.logging.log4j.util.StringMap;

public class GarbageFreeSortedArrayThreadContextMap implements ReadOnlyThreadContextMap, ObjectThreadContextMap {
   public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";
   protected static final int DEFAULT_INITIAL_CAPACITY = 16;
   protected static final String PROPERTY_NAME_INITIAL_CAPACITY = "log4j2.ThreadContext.initial.capacity";
   private final int initialCapacity;
   protected final ThreadLocal<StringMap> localMap;

   public GarbageFreeSortedArrayThreadContextMap() {
      this(PropertiesUtil.getProperties());
   }

   GarbageFreeSortedArrayThreadContextMap(final PropertiesUtil properties) {
      this.initialCapacity = properties.getIntegerProperty("log4j2.ThreadContext.initial.capacity", 16);
      this.localMap = (ThreadLocal<StringMap>)(properties.getBooleanProperty("isThreadContextMapInheritable") ? new InheritableThreadLocal<StringMap>() {
         protected StringMap childValue(final StringMap parentValue) {
            return parentValue != null ? GarbageFreeSortedArrayThreadContextMap.this.createStringMap(parentValue) : null;
         }
      } : new ThreadLocal<>());
   }

   protected StringMap createStringMap() {
      return new SortedArrayStringMap(this.initialCapacity);
   }

   protected StringMap createStringMap(final ReadOnlyStringMap original) {
      return new SortedArrayStringMap(original);
   }

   private StringMap getThreadLocalMap() {
      StringMap map = this.localMap.get();
      if (map == null) {
         map = this.createStringMap();
         this.localMap.set(map);
      }

      return map;
   }

   public void put(final String key, final String value) {
      this.getThreadLocalMap().putValue(key, value);
   }

   public void putValue(final String key, final Object value) {
      this.getThreadLocalMap().putValue(key, value);
   }

   public void putAll(final Map<String, String> values) {
      if (values != null && !values.isEmpty()) {
         StringMap map = this.getThreadLocalMap();

         for (Entry<String, String> entry : values.entrySet()) {
            map.putValue(entry.getKey(), entry.getValue());
         }
      }
   }

   public <V> void putAllValues(final Map<String, V> values) {
      if (values != null && !values.isEmpty()) {
         StringMap map = this.getThreadLocalMap();

         for (Entry<String, V> entry : values.entrySet()) {
            map.putValue(entry.getKey(), entry.getValue());
         }
      }
   }

   public String get(final String key) {
      return this.getValue(key);
   }

   public <V> V getValue(final String key) {
      StringMap map = this.localMap.get();
      return (V)(map == null ? null : map.getValue(key));
   }

   public void remove(final String key) {
      StringMap map = this.localMap.get();
      if (map != null) {
         map.remove(key);
      }
   }

   public void removeAll(final Iterable<String> keys) {
      StringMap map = this.localMap.get();
      if (map != null) {
         for (String key : keys) {
            map.remove(key);
         }
      }
   }

   public void clear() {
      StringMap map = this.localMap.get();
      if (map != null) {
         map.clear();
      }
   }

   public boolean containsKey(final String key) {
      StringMap map = this.localMap.get();
      return map != null && map.containsKey(key);
   }

   public Map<String, String> getCopy() {
      StringMap map = this.localMap.get();
      return (Map<String, String>)(map == null ? new HashMap<>() : map.toMap());
   }

   public StringMap getReadOnlyContextData() {
      StringMap map = this.localMap.get();
      if (map == null) {
         map = this.createStringMap();
         this.localMap.set(map);
      }

      return map;
   }

   public Map<String, String> getImmutableMapOrNull() {
      StringMap map = this.localMap.get();
      return map == null ? null : Collections.unmodifiableMap(map.toMap());
   }

   public boolean isEmpty() {
      StringMap map = this.localMap.get();
      return map == null || map.isEmpty();
   }

   @Override
   public String toString() {
      StringMap map = this.localMap.get();
      return map == null ? "{}" : map.toString();
   }

   @Override
   public int hashCode() {
      int prime = 31;
      int result = 1;
      StringMap map = this.localMap.get();
      return 31 * result + (map == null ? 0 : map.hashCode());
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (!(obj instanceof ThreadContextMap)) {
         return false;
      } else {
         ThreadContextMap other = (ThreadContextMap)obj;
         Map<String, String> map = this.getImmutableMapOrNull();
         Map<String, String> otherMap = other.getImmutableMapOrNull();
         return Objects.equals(map, otherMap);
      }
   }
}
