package org.glassfish.jersey.internal.util.collection;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Constructor;
import java.util.List;

public class MultivaluedStringMap extends MultivaluedHashMap<String, String> {
   static final long serialVersionUID = -6052320403766368902L;

   public MultivaluedStringMap(MultivaluedMap<? extends String, ? extends String> map) {
      super(map);
   }

   public MultivaluedStringMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public MultivaluedStringMap(int initialCapacity) {
      super(initialCapacity);
   }

   public MultivaluedStringMap() {
   }

   protected void addFirstNull(List<String> values) {
      values.add("");
   }

   protected void addNull(List<String> values) {
      values.add(0, "");
   }

   public final <A> A getFirst(String key, Class<A> type) {
      String value = (String)this.getFirst(key);
      if (value == null) {
         return null;
      } else {
         Constructor<A> c = null;

         try {
            c = type.getConstructor(String.class);
         } catch (Exception var8) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", var8);
         }

         A retVal = null;

         try {
            retVal = c.newInstance(value);
         } catch (Exception var7) {
         }

         return retVal;
      }
   }

   public final <A> A getFirst(String key, A defaultValue) {
      String value = (String)this.getFirst(key);
      if (value == null) {
         return defaultValue;
      } else {
         Class<A> type = (Class<A>)defaultValue.getClass();
         Constructor<A> c = null;

         try {
            c = type.getConstructor(String.class);
         } catch (Exception var9) {
            throw new IllegalArgumentException(type.getName() + " has no String constructor", var9);
         }

         A retVal = defaultValue;

         try {
            retVal = c.newInstance(value);
         } catch (Exception var8) {
         }

         return retVal;
      }
   }
}
