package org.apache.logging.log4j.core.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.StringMap;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.TriConsumer;

public class JdkMapAdapterStringMap implements StringMap {
   private static final long serialVersionUID = -7348247784983193612L;
   private static final String FROZEN = "Frozen collection cannot be modified";
   private static final Comparator<? super String> NULL_FIRST_COMPARATOR = (left, right) -> {
      if (left == null) {
         return -1;
      } else {
         return right == null ? 1 : left.compareTo(right);
      }
   };
   private static Map<Class<?>, Void> UNMODIFIABLE_MAPS_CACHE = new WeakHashMap<>();
   private final Map<String, String> map;
   private boolean immutable = false;
   private transient String[] sortedKeys;
   private static TriConsumer<String, String, Map<String, String>> PUT_ALL = (key, value, stringStringMap) -> stringStringMap.put(key, value);

   public JdkMapAdapterStringMap() {
      this(new HashMap<>(), false);
   }

   @Deprecated
   public JdkMapAdapterStringMap(final Map<String, String> map) {
      this.map = Objects.requireNonNull(map, "map");
      if (UNMODIFIABLE_MAPS_CACHE.containsKey(map.getClass())) {
         this.immutable = true;
      } else {
         try {
            map.replace("", "", "");
         } catch (UnsupportedOperationException var4) {
            WeakHashMap<Class<?>, Void> cache = new WeakHashMap<>(UNMODIFIABLE_MAPS_CACHE);
            cache.put(map.getClass(), null);
            UNMODIFIABLE_MAPS_CACHE = cache;
            this.immutable = true;
         }
      }
   }

   public JdkMapAdapterStringMap(final Map<String, String> map, final boolean frozen) {
      this.map = Objects.requireNonNull(map, "map");
      this.immutable = frozen;
   }

   public Map<String, String> toMap() {
      return new HashMap<>(this.map);
   }

   private void assertNotFrozen() {
      if (this.immutable) {
         throw new UnsupportedOperationException("Frozen collection cannot be modified");
      }
   }

   public boolean containsKey(final String key) {
      return this.map.containsKey(key);
   }

   public <V> void forEach(final BiConsumer<String, ? super V> action) {
      String[] keys = this.getSortedKeys();

      for (int i = 0; i < keys.length; i++) {
         action.accept(keys[i], this.map.get(keys[i]));
      }
   }

   public <V, S> void forEach(final TriConsumer<String, ? super V, S> action, final S state) {
      String[] keys = this.getSortedKeys();

      for (int i = 0; i < keys.length; i++) {
         action.accept(keys[i], this.map.get(keys[i]), state);
      }
   }

   private String[] getSortedKeys() {
      if (this.sortedKeys == null) {
         this.sortedKeys = this.map.keySet().toArray(Strings.EMPTY_ARRAY);
         Arrays.sort(this.sortedKeys, NULL_FIRST_COMPARATOR);
      }

      return this.sortedKeys;
   }

   public <V> V getValue(final String key) {
      return (V)this.map.get(key);
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public int size() {
      return this.map.size();
   }

   public void clear() {
      if (!this.map.isEmpty()) {
         this.assertNotFrozen();
         this.map.clear();
         this.sortedKeys = null;
      }
   }

   public void freeze() {
      this.immutable = true;
   }

   public boolean isFrozen() {
      return this.immutable;
   }

   public void putAll(final ReadOnlyStringMap source) {
      this.assertNotFrozen();
      source.forEach(PUT_ALL, this.map);
      this.sortedKeys = null;
   }

   public void putValue(final String key, final Object value) {
      this.assertNotFrozen();
      this.map.put(key, value == null ? null : String.valueOf(value));
      this.sortedKeys = null;
   }

   public void remove(final String key) {
      if (this.map.containsKey(key)) {
         this.assertNotFrozen();
         this.map.remove(key);
         this.sortedKeys = null;
      }
   }

   @Override
   public String toString() {
      StringBuilder result = new StringBuilder(this.map.size() * 13);
      result.append('{');
      String[] keys = this.getSortedKeys();

      for (int i = 0; i < keys.length; i++) {
         if (i > 0) {
            result.append(", ");
         }

         result.append(keys[i]).append('=').append(this.map.get(keys[i]));
      }

      result.append('}');
      return result.toString();
   }

   @Override
   public boolean equals(final Object object) {
      if (object == this) {
         return true;
      } else if (!(object instanceof ReadOnlyStringMap)) {
         return false;
      } else if (this.size() != ((ReadOnlyStringMap)object).size()) {
         return false;
      } else {
         Map<String, String> thisMap = this.toMap();
         Map<String, String> otherMap = ((ReadOnlyStringMap)object).toMap();
         return thisMap.equals(otherMap);
      }
   }

   @Override
   public int hashCode() {
      return this.toMap().hashCode();
   }
}
