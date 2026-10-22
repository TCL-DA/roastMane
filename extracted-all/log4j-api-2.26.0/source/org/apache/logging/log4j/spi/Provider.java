package org.apache.logging.log4j.spi;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.simple.SimpleLoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class Provider {
   protected static final String CURRENT_VERSION = "2.6.0";
   @Deprecated
   public static final String FACTORY_PRIORITY = "FactoryPriority";
   @Deprecated
   public static final String THREAD_CONTEXT_MAP = "ThreadContextMap";
   @Deprecated
   public static final String LOGGER_CONTEXT_FACTORY = "LoggerContextFactory";
   public static final String PROVIDER_PROPERTY_NAME = "log4j.provider";
   private static final String DISABLE_CONTEXT_MAP = "log4j2.disableThreadContextMap";
   private static final String DISABLE_THREAD_CONTEXT = "log4j2.disableThreadContext";
   private static final int DEFAULT_PRIORITY = -1;
   private static final Logger LOGGER = StatusLogger.getLogger();
   private final int priority;
   @Deprecated
   @Nullable
   private final String className;
   @Nullable
   private final Class<? extends LoggerContextFactory> loggerContextFactoryClass;
   @Deprecated
   @Nullable
   private final String threadContextMap;
   @Nullable
   private final Class<? extends ThreadContextMap> threadContextMapClass;
   @Nullable
   private final String versions;
   @Deprecated
   @Nullable
   private final URL url;
   @Deprecated
   private final WeakReference<ClassLoader> classLoader;

   @Deprecated
   public Provider(final Properties props, final URL url, final ClassLoader classLoader) {
      this.url = url;
      this.classLoader = new WeakReference<>(classLoader);
      String weight = props.getProperty("FactoryPriority");
      this.priority = weight == null ? -1 : Integer.parseInt(weight);
      this.className = props.getProperty("LoggerContextFactory");
      this.threadContextMap = props.getProperty("ThreadContextMap");
      this.loggerContextFactoryClass = null;
      this.threadContextMapClass = null;
      this.versions = null;
   }

   public Provider(@Nullable final Integer priority, final String versions) {
      this(priority, versions, null, null);
   }

   public Provider(@Nullable final Integer priority, final String versions, @Nullable final Class<? extends LoggerContextFactory> loggerContextFactoryClass) {
      this(priority, versions, loggerContextFactoryClass, null);
   }

   public Provider(
      @Nullable final Integer priority,
      final String versions,
      @Nullable final Class<? extends LoggerContextFactory> loggerContextFactoryClass,
      @Nullable final Class<? extends ThreadContextMap> threadContextMapClass
   ) {
      this.priority = priority != null ? priority : -1;
      this.versions = versions;
      this.loggerContextFactoryClass = loggerContextFactoryClass;
      this.threadContextMapClass = threadContextMapClass;
      this.className = null;
      this.threadContextMap = null;
      this.url = null;
      this.classLoader = new WeakReference<>(null);
   }

   public String getVersions() {
      return this.versions != null ? this.versions : "";
   }

   public Integer getPriority() {
      return this.priority;
   }

   @Nullable
   public String getClassName() {
      return this.loggerContextFactoryClass != null ? this.loggerContextFactoryClass.getName() : this.className;
   }

   @Nullable
   public Class<? extends LoggerContextFactory> loadLoggerContextFactory() {
      if (this.loggerContextFactoryClass != null) {
         return this.loggerContextFactoryClass;
      } else {
         String className = this.getClassName();
         ClassLoader loader = this.classLoader.get();
         if (loader != null && className != null) {
            try {
               Class<?> clazz = loader.loadClass(className);
               if (LoggerContextFactory.class.isAssignableFrom(clazz)) {
                  return clazz.asSubclass(LoggerContextFactory.class);
               }

               LOGGER.error("Class {} specified in {} does not extend {}", className, this.getUrl(), LoggerContextFactory.class.getName());
            } catch (Exception var4) {
               LOGGER.error("Unable to create class {} specified in {}", className, this.getUrl(), var4);
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public LoggerContextFactory getLoggerContextFactory() {
      Class<? extends LoggerContextFactory> implementation = this.loadLoggerContextFactory();
      if (implementation != null) {
         try {
            return LoaderUtil.newInstanceOf(implementation);
         } catch (ReflectiveOperationException var3) {
            LOGGER.error("Failed to instantiate logger context factory {}.", implementation.getName(), var3);
         }
      }

      LOGGER.error("Falling back to simple logger context factory: {}", SimpleLoggerContextFactory.class.getName());
      return SimpleLoggerContextFactory.INSTANCE;
   }

   @Nullable
   public String getThreadContextMap() {
      return this.threadContextMapClass != null ? this.threadContextMapClass.getName() : this.threadContextMap;
   }

   @Nullable
   public Class<? extends ThreadContextMap> loadThreadContextMap() {
      if (this.threadContextMapClass != null) {
         return this.threadContextMapClass;
      } else {
         String threadContextMap = this.getThreadContextMap();
         ClassLoader loader = this.classLoader.get();
         if (loader != null && threadContextMap != null) {
            try {
               Class<?> clazz = loader.loadClass(threadContextMap);
               if (ThreadContextMap.class.isAssignableFrom(clazz)) {
                  return clazz.asSubclass(ThreadContextMap.class);
               }

               LOGGER.error("Class {} specified in {} does not extend {}", threadContextMap, this.getUrl(), ThreadContextMap.class.getName());
            } catch (Exception var4) {
               LOGGER.error("Unable to load class {} specified in {}", threadContextMap, this.url, var4);
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public ThreadContextMap getThreadContextMapInstance() {
      Class<? extends ThreadContextMap> implementation = this.loadThreadContextMap();
      if (implementation != null) {
         try {
            return LoaderUtil.newInstanceOf(implementation);
         } catch (ReflectiveOperationException var3) {
            LOGGER.error("Failed to instantiate logger context factory {}.", implementation.getName(), var3);
         }
      }

      PropertiesUtil props = PropertiesUtil.getProperties();
      return (ThreadContextMap)(!props.getBooleanProperty("log4j2.disableThreadContextMap") && !props.getBooleanProperty("log4j2.disableThreadContext")
         ? new DefaultThreadContextMap()
         : NoOpThreadContextMap.INSTANCE);
   }

   @Deprecated
   @Nullable
   public URL getUrl() {
      return this.url;
   }

   @Override
   public String toString() {
      StringBuilder result = new StringBuilder("Provider '").append(this.getClass().getName()).append("'");
      if (this.priority != -1) {
         result.append("\n\tpriority = ").append(this.priority);
      }

      String threadContextMap = this.getThreadContextMap();
      if (threadContextMap != null) {
         result.append("\n\tthreadContextMap = ").append(threadContextMap);
      }

      String loggerContextFactory = this.getClassName();
      if (loggerContextFactory != null) {
         result.append("\n\tloggerContextFactory = ").append(loggerContextFactory);
      }

      if (this.url != null) {
         result.append("\n\turl = ").append(this.url);
      }

      if (Provider.class.equals(this.getClass())) {
         ClassLoader loader = this.classLoader.get();
         if (loader == null) {
            result.append("\n\tclassLoader = null or not reachable");
         } else {
            result.append("\n\tclassLoader = ").append(loader);
         }
      }

      return result.toString();
   }

   @Override
   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof Provider)) {
         return false;
      } else {
         Provider provider = (Provider)o;
         return Objects.equals(this.priority, provider.priority)
            && Objects.equals(this.className, provider.className)
            && Objects.equals(this.loggerContextFactoryClass, provider.loggerContextFactoryClass)
            && Objects.equals(this.versions, provider.versions);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.priority, this.className, this.loggerContextFactoryClass, this.versions);
   }
}
