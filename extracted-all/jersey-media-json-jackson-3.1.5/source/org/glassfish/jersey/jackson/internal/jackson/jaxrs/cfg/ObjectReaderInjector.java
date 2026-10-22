package org.glassfish.jersey.jackson.internal.jackson.jaxrs.cfg;

import java.util.concurrent.atomic.AtomicBoolean;

public class ObjectReaderInjector {
   protected static final ThreadLocal<ObjectReaderModifier> _threadLocal = new ThreadLocal<>();
   protected static final AtomicBoolean _hasBeenSet = new AtomicBoolean(false);

   private ObjectReaderInjector() {
   }

   public static void set(ObjectReaderModifier mod) {
      _hasBeenSet.set(true);
      _threadLocal.set(mod);
   }

   public static ObjectReaderModifier get() {
      return _hasBeenSet.get() ? _threadLocal.get() : null;
   }

   public static ObjectReaderModifier getAndClear() {
      ObjectReaderModifier mod = get();
      if (mod != null) {
         _threadLocal.remove();
      }

      return mod;
   }
}
