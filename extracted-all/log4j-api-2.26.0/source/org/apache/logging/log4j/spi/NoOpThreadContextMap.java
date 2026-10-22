package org.apache.logging.log4j.spi;

import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class NoOpThreadContextMap implements ThreadContextMap {
   public static final ThreadContextMap INSTANCE = new NoOpThreadContextMap();

   @Override
   public void clear() {
   }

   @Override
   public boolean containsKey(final String key) {
      return false;
   }

   @Nullable
   @Override
   public String get(final String key) {
      return null;
   }

   @Override
   public Map<String, String> getCopy() {
      return new HashMap<>();
   }

   @Nullable
   @Override
   public Map<String, String> getImmutableMapOrNull() {
      return null;
   }

   @Override
   public boolean isEmpty() {
      return true;
   }

   @Override
   public void put(final String key, final String value) {
   }

   @Override
   public void remove(final String key) {
   }
}
