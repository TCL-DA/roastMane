package org.apache.logging.log4j.core.impl;

import aQute.bnd.annotation.spi.ServiceProvider;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.context.internal.GarbageFreeSortedArrayThreadContextMap;
import org.apache.logging.log4j.spi.DefaultThreadContextMap;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.NoOpThreadContextMap;
import org.apache.logging.log4j.spi.Provider;
import org.apache.logging.log4j.spi.ThreadContextMap;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Lazy;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.jspecify.annotations.NullMarked;

@NullMarked
@ServiceProvider(
   value = Provider.class,
   resolution = "optional"
)
public class Log4jProvider extends Provider {
   private static final String NO_OP_CONTEXT_MAP = "NoOp";
   private static final String WEB_APP_CONTEXT_MAP = "WebApp";
   private static final String GARBAGE_FREE_CONTEXT_MAP = "GarbageFree";
   private static final String DISABLE_CONTEXT_MAP = "log4j2.disableThreadContextMap";
   private static final String DISABLE_THREAD_CONTEXT = "log4j2.disableThreadContext";
   private static final String THREAD_CONTEXT_MAP_PROPERTY = "log4j2.threadContextMap";
   private static final String GC_FREE_THREAD_CONTEXT_PROPERTY = "log4j2.garbagefree.threadContextMap";
   private static final String WEB_APP_CLASS_NAME = "org.apache.logging.log4j.spi.DefaultThreadContextMap";
   private static final String GARBAGE_FREE_CLASS_NAME = "org.apache.logging.log4j.core.context.internal.GarbageFreeSortedArrayThreadContextMap";
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final Lazy<LoggerContextFactory> loggerContextFactoryLazy = Lazy.lazy(Log4jContextFactory::new);
   private final Lazy<ThreadContextMap> threadContextMapLazy = Lazy.lazy(this::createThreadContextMap);

   public Log4jProvider() {
      super(10, "2.6.0", Log4jContextFactory.class);
   }

   public LoggerContextFactory getLoggerContextFactory() {
      return (LoggerContextFactory)this.loggerContextFactoryLazy.get();
   }

   public ThreadContextMap getThreadContextMapInstance() {
      return (ThreadContextMap)this.threadContextMapLazy.get();
   }

   private ThreadContextMap createThreadContextMap() {
      PropertiesUtil props = PropertiesUtil.getProperties();
      if (!props.getBooleanProperty("log4j2.disableThreadContextMap") && !props.getBooleanProperty("log4j2.disableThreadContext")) {
         String threadContextMapClass = props.getStringProperty("log4j2.threadContextMap");
         if (threadContextMapClass == null) {
            threadContextMapClass = props.getBooleanProperty("log4j2.garbagefree.threadContextMap") ? "GarbageFree" : "WebApp";
         }

         switch (threadContextMapClass) {
            case "NoOp":
               return NoOpThreadContextMap.INSTANCE;
            case "WebApp":
            case "org.apache.logging.log4j.spi.DefaultThreadContextMap":
               return new DefaultThreadContextMap();
            case "org.apache.logging.log4j.spi.GarbageFreeSortedArrayThreadContextMap":
            case "GarbageFree":
            case "org.apache.logging.log4j.core.context.internal.GarbageFreeSortedArrayThreadContextMap":
               return new GarbageFreeSortedArrayThreadContextMap();
            default:
               try {
                  return (ThreadContextMap)LoaderUtil.newCheckedInstanceOf(threadContextMapClass, ThreadContextMap.class);
               } catch (Exception var6) {
                  LOGGER.error("Unable to create instance of class {}.", threadContextMapClass, var6);
                  LOGGER.warn("Falling back to {}.", NoOpThreadContextMap.class.getName());
                  return NoOpThreadContextMap.INSTANCE;
               }
         }
      } else {
         return NoOpThreadContextMap.INSTANCE;
      }
   }

   void resetThreadContextMap() {
      this.threadContextMapLazy.set(null);
   }
}
