package org.glassfish.jersey.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class MapPropertiesDelegate implements PropertiesDelegate {
   private final Map<String, Object> store;

   public MapPropertiesDelegate() {
      this.store = new HashMap<>();
   }

   public MapPropertiesDelegate(Map<String, Object> store) {
      this.store = store;
   }

   public MapPropertiesDelegate(PropertiesDelegate that) {
      if (that instanceof MapPropertiesDelegate) {
         this.store = new HashMap<>(((MapPropertiesDelegate)that).store);
      } else {
         this.store = new HashMap<>();

         for (String name : that.getPropertyNames()) {
            this.store.put(name, that.getProperty(name));
         }
      }
   }

   @Override
   public boolean hasProperty(String name) {
      return this.store.containsKey(name);
   }

   @Override
   public Object getProperty(String name) {
      return this.store.get(name);
   }

   @Override
   public Collection<String> getPropertyNames() {
      return Collections.unmodifiableCollection(this.store.keySet());
   }

   @Override
   public void setProperty(String name, Object value) {
      this.store.put(name, value);
   }

   @Override
   public void removeProperty(String name) {
      this.store.remove(name);
   }
}
