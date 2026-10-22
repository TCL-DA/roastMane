package org.apache.logging.log4j.core.impl;

import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.spi.CopyOnWrite;
import org.apache.logging.log4j.spi.DefaultThreadContextMap;
import org.apache.logging.log4j.spi.ReadOnlyThreadContextMap;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;

public class ContextDataInjectorFactory {
   private static final String CONTEXT_DATA_INJECTOR_PROPERTY = "log4j2.ContextDataInjector";

   public static ContextDataInjector createInjector() {
      try {
         return (ContextDataInjector)LoaderUtil.newCheckedInstanceOfProperty(
            "log4j2.ContextDataInjector", ContextDataInjector.class, ContextDataInjectorFactory::createDefaultInjector
         );
      } catch (ReflectiveOperationException var1) {
         StatusLogger.getLogger().warn("Could not create ContextDataInjector: {}", var1.getMessage(), var1);
         return createDefaultInjector();
      }
   }

   private static ContextDataInjector createDefaultInjector() {
      ReadOnlyThreadContextMap threadContextMap = ThreadContext.getThreadContextMap();
      if (threadContextMap instanceof DefaultThreadContextMap || threadContextMap == null) {
         return new ThreadContextDataInjector.ForDefaultThreadContextMap();
      } else {
         return (ContextDataInjector)(threadContextMap instanceof CopyOnWrite
            ? new ThreadContextDataInjector.ForCopyOnWriteThreadContextMap()
            : new ThreadContextDataInjector.ForGarbageFreeThreadContextMap());
      }
   }
}
