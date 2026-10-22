package org.apache.logging.log4j.spi;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import org.apache.logging.log4j.internal.map.UnmodifiableArrayBackedMap;
import org.apache.logging.log4j.util.BiConsumer;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.TriConsumer;

public class DefaultThreadContextMap implements ThreadContextMap, ReadOnlyStringMap {
   private static final long serialVersionUID = -2635197170958057849L;
   public static final String INHERITABLE_MAP = "isThreadContextMapInheritable";
   private ThreadLocal<Object[]> localState;

   public DefaultThreadContextMap() {
      this(PropertiesUtil.getProperties());
   }

   @Deprecated
   public DefaultThreadContextMap(final boolean ignored) {
      this(PropertiesUtil.getProperties());
   }

   DefaultThreadContextMap(final PropertiesUtil properties) {
      this.localState = (ThreadLocal<Object[]>)(properties.getBooleanProperty("isThreadContextMapInheritable") ? new InheritableThreadLocal<Object[]>() {
         protected Object[] childValue(final Object[] parentValue) {
            return parentValue;
         }
      } : new ThreadLocal<>());
   }

   @Override
   public void put(final String key, final String value) {
      Object[] state = this.localState.get();
      this.localState.set(UnmodifiableArrayBackedMap.getMap(state).copyAndPut(key, value).getBackingArray());
   }

   public void putAll(final Map<String, String> m) {
      Object[] state = this.localState.get();
      this.localState.set(UnmodifiableArrayBackedMap.getMap(state).copyAndPutAll(m).getBackingArray());
   }

   @Override
   public String get(final String key) {
      Object[] state = this.localState.get();
      return state == null ? null : UnmodifiableArrayBackedMap.getMap(state).get(key);
   }

   @Override
   public void remove(final String key) {
      Object[] state = this.localState.get();
      if (state != null) {
         this.localState.set(UnmodifiableArrayBackedMap.getMap(state).copyAndRemove(key).getBackingArray());
      }
   }

   public void removeAll(final Iterable<String> keys) {
      Object[] state = this.localState.get();
      if (state != null) {
         this.localState.set(UnmodifiableArrayBackedMap.getMap(state).copyAndRemoveAll(keys).getBackingArray());
      }
   }

   @Override
   public void clear() {
      this.localState.remove();
   }

   @Override
   public Map<String, String> toMap() {
      return this.getCopy();
   }

   @Override
   public boolean containsKey(final String key) {
      Object[] state = this.localState.get();
      return state != null && UnmodifiableArrayBackedMap.getMap(state).containsKey(key);
   }

   @Override
   public <V> void forEach(final BiConsumer<String, ? super V> action) {
      Object[] state = this.localState.get();
      if (state != null) {
         UnmodifiableArrayBackedMap.getMap(state).forEach(action);
      }
   }

   @Override
   public <V, S> void forEach(final TriConsumer<String, ? super V, S> action, final S state) {
      Object[] localState = this.localState.get();
      if (localState != null) {
         UnmodifiableArrayBackedMap.getMap(localState).forEach(action, state);
      }
   }

   @Override
   public <V> V getValue(final String key) {
      return (V)this.get(key);
   }

   @Override
   public Map<String, String> getCopy() {
      Object[] state = this.localState.get();
      if (state == null) {
         return new HashMap<>(0);
      } else {
         Map<String, String> map = UnmodifiableArrayBackedMap.getMap(state);
         if (map.isEmpty()) {
            return new HashMap<>();
         } else {
            HashMap<String, String> copy = new HashMap<>((int)((double)map.size() * 1.35));

            for (Entry<String, String> entry : map.entrySet()) {
               copy.put(entry.getKey(), entry.getValue());
            }

            return copy;
         }
      }
   }

   @Override
   public Map<String, String> getImmutableMapOrNull() {
      Object[] state = this.localState.get();
      return state == null ? null : UnmodifiableArrayBackedMap.getMap(state);
   }

   @Override
   public boolean isEmpty() {
      return this.size() == 0;
   }

   @Override
   public int size() {
      Object[] state = this.localState.get();
      return UnmodifiableArrayBackedMap.getMap(state).size();
   }

   @Override
   public String toString() {
      Object[] state = this.localState.get();
      return state == null ? "{}" : UnmodifiableArrayBackedMap.getMap(state).toString();
   }

   @Override
   public int hashCode() {
      return this.toMap().hashCode();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else {
         if (obj instanceof ReadOnlyStringMap) {
            if (this.size() != ((ReadOnlyStringMap)obj).size()) {
               return false;
            }

            obj = ((ReadOnlyStringMap)obj).toMap();
         }

         if (!(obj instanceof ThreadContextMap)) {
            return false;
         } else {
            ThreadContextMap other = (ThreadContextMap)obj;
            Map<String, String> map = UnmodifiableArrayBackedMap.getMap(this.localState.get());
            Map<String, String> otherMap = other.getImmutableMapOrNull();
            return Objects.equals(map, otherMap);
         }
      }
   }
}
