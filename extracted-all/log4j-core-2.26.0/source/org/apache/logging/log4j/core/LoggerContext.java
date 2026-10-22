package org.apache.logging.log4j.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationListener;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.NullConfiguration;
import org.apache.logging.log4j.core.config.Reconfigurable;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.core.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.core.jmx.Server;
import org.apache.logging.log4j.core.jmx.internal.JmxUtil;
import org.apache.logging.log4j.core.util.Cancellable;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.ExecutorServices;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.core.util.ShutdownCallbackRegistry;
import org.apache.logging.log4j.core.util.internal.InternalLoggerRegistry;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.FlowMessageFactory;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedMessageFactory;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.LoggerContextShutdownAware;
import org.apache.logging.log4j.spi.LoggerContextShutdownEnabled;
import org.apache.logging.log4j.spi.LoggerRegistry;
import org.apache.logging.log4j.spi.Terminable;
import org.apache.logging.log4j.spi.ThreadContextMapFactory;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.jspecify.annotations.Nullable;

public class LoggerContext
   extends AbstractLifeCycle
   implements org.apache.logging.log4j.spi.LoggerContext,
   AutoCloseable,
   Terminable,
   ConfigurationListener,
   LoggerContextShutdownEnabled {
   public static final String PROPERTY_CONFIG = "config";
   private static final String EXTERNAL_CONTEXT_KEY = "__EXTERNAL_CONTEXT_KEY__";
   private static final String MESSAGE_FACTORY_PROPERTY_NAME = "log4j2.messageFactory";
   private static final MessageFactory DEFAULT_MESSAGE_FACTORY = createInstanceFromFactoryProperty(
      MessageFactory.class,
      "log4j2.messageFactory",
      (MessageFactory)(Constants.ENABLE_THREADLOCALS ? ReusableMessageFactory.INSTANCE : ParameterizedMessageFactory.INSTANCE)
   );
   private static final String FLOW_MESSAGE_FACTORY_PROPERTY_NAME = "log4j2.flowMessageFactory";
   static final FlowMessageFactory DEFAULT_FLOW_MESSAGE_FACTORY = createInstanceFromFactoryProperty(
      FlowMessageFactory.class, "log4j2.flowMessageFactory", DefaultFlowMessageFactory.INSTANCE
   );
   private static final Configuration NULL_CONFIGURATION = new NullConfiguration();
   private final InternalLoggerRegistry loggerRegistry = new InternalLoggerRegistry();
   private final CopyOnWriteArrayList<PropertyChangeListener> propertyChangeListeners = new CopyOnWriteArrayList<>();
   private volatile List<LoggerContextShutdownAware> listeners;
   private volatile Configuration configuration = new DefaultConfiguration();
   private final ConcurrentMap<String, Object> externalMap = new ConcurrentHashMap<>();
   private String contextName;
   private volatile URI configLocation;
   private Cancellable shutdownCallback;
   private final Lock configLock = new ReentrantLock();

   private static <V> V createInstanceFromFactoryProperty(final Class<V> instanceType, final String propertyName, final V fallbackInstance) {
      try {
         return (V)LoaderUtil.newCheckedInstanceOfProperty(propertyName, instanceType, () -> fallbackInstance);
      } catch (Exception var5) {
         String message = String.format("failed instantiating the class pointed by the `%s` property", propertyName);
         throw new RuntimeException(message, var5);
      }
   }

   public LoggerContext(final String name) {
      this(name, null, (URI)null);
   }

   public LoggerContext(final String name, final Object externalContext) {
      this(name, externalContext, (URI)null);
   }

   public LoggerContext(final String name, final Object externalContext, final URI configLocn) {
      this.contextName = name;
      if (externalContext != null) {
         this.externalMap.put("__EXTERNAL_CONTEXT_KEY__", externalContext);
      }

      this.configLocation = configLocn;
   }

   @SuppressFBWarnings(
      value = {"PATH_TRAVERSAL_IN"},
      justification = "The configLocn comes from a secure source (Log4j properties)"
   )
   public LoggerContext(final String name, final Object externalContext, final String configLocn) {
      this.contextName = name;
      if (externalContext != null) {
         this.externalMap.put("__EXTERNAL_CONTEXT_KEY__", externalContext);
      }

      if (configLocn != null) {
         URI uri;
         try {
            uri = new File(configLocn).toURI();
         } catch (Exception var6) {
            uri = null;
         }

         this.configLocation = uri;
      } else {
         this.configLocation = null;
      }
   }

   public void addShutdownListener(final LoggerContextShutdownAware listener) {
      if (this.listeners == null) {
         synchronized (this) {
            if (this.listeners == null) {
               this.listeners = new CopyOnWriteArrayList<>();
            }
         }
      }

      this.listeners.add(listener);
   }

   public List<LoggerContextShutdownAware> getListeners() {
      return this.listeners;
   }

   public static LoggerContext getContext() {
      return (LoggerContext)LogManager.getContext();
   }

   public static LoggerContext getContext(final boolean currentContext) {
      return (LoggerContext)LogManager.getContext(currentContext);
   }

   public static LoggerContext getContext(final ClassLoader loader, final boolean currentContext, final URI configLocation) {
      return (LoggerContext)LogManager.getContext(loader, currentContext, configLocation);
   }

   @Override
   public void start() {
      LOGGER.debug("Starting LoggerContext[name={}, {}]...", this.getName(), this);
      if (PropertiesUtil.getProperties().getBooleanProperty("log4j.LoggerContext.stacktrace.on.start", false)) {
         LOGGER.debug("Stack trace to locate invoker", new Exception("Not a real error, showing stack trace to locate invoker"));
      }

      if (this.configLock.tryLock()) {
         try {
            if (this.isInitialized() || this.isStopped()) {
               this.setStarting();
               this.reconfigure();
               if (this.configuration.isShutdownHookEnabled()) {
                  this.setUpShutdownHook();
               }

               this.setStarted();
            }
         } finally {
            this.configLock.unlock();
         }
      }

      LOGGER.debug("LoggerContext[name={}, {}] started OK.", this.getName(), this);
   }

   public void start(final Configuration config) {
      LOGGER.info("Starting {}[name={}] with configuration {}...", this.getClass().getSimpleName(), this.getName(), config);
      if (this.configLock.tryLock()) {
         try {
            if (!this.isInitialized() && !this.isStopped()) {
               this.reconfigure(config);
            } else {
               this.setStarting();
               this.reconfigure(config);
               if (this.configuration.isShutdownHookEnabled()) {
                  this.setUpShutdownHook();
               }

               this.setStarted();
            }
         } finally {
            this.configLock.unlock();
         }
      }

      LOGGER.info("{}[name={}] started with configuration {}.", this.getClass().getSimpleName(), this.getName(), config);
   }

   private void setUpShutdownHook() {
      if (this.shutdownCallback == null) {
         LoggerContextFactory factory = LogManager.getFactory();
         if (factory instanceof ShutdownCallbackRegistry) {
            LOGGER.debug(ShutdownCallbackRegistry.SHUTDOWN_HOOK_MARKER, "Shutdown hook enabled. Registering a new one.");
            ExecutorServices.ensureInitialized();

            try {
               final long shutdownTimeoutMillis = this.configuration.getShutdownTimeoutMillis();
               this.shutdownCallback = ((ShutdownCallbackRegistry)factory)
                  .addShutdownCallback(
                     new Runnable() {
                        @Override
                        public void run() {
                           LoggerContext context = LoggerContext.this;
                           AbstractLifeCycle.LOGGER
                              .debug(ShutdownCallbackRegistry.SHUTDOWN_HOOK_MARKER, "Stopping LoggerContext[name={}, {}]", context.getName(), context);
                           context.stop(shutdownTimeoutMillis, TimeUnit.MILLISECONDS);
                        }

                        @Override
                        public String toString() {
                           return "Shutdown callback for LoggerContext[name=" + LoggerContext.this.getName() + ']';
                        }
                     }
                  );
            } catch (IllegalStateException var4) {
               throw new IllegalStateException("Unable to register Log4j shutdown hook because JVM is shutting down.", var4);
            } catch (SecurityException var5) {
               LOGGER.error(ShutdownCallbackRegistry.SHUTDOWN_HOOK_MARKER, "Unable to register shutdown hook due to security restrictions", var5);
            }
         }
      }
   }

   @Override
   public void close() {
      this.stop();
   }

   public void terminate() {
      this.stop();
   }

   @Override
   public boolean stop(final long timeout, final TimeUnit timeUnit) {
      LOGGER.debug("Stopping LoggerContext[name={}, {}]...", this.getName(), this);
      this.configLock.lock();

      try {
         if (this.isStopped()) {
            return true;
         }

         this.setStopping();
         this.unregisterJmxBeans();
         if (this.shutdownCallback != null) {
            this.shutdownCallback.cancel();
            this.shutdownCallback = null;
         }

         Configuration prev = this.configuration;
         this.configuration = NULL_CONFIGURATION;
         this.updateLoggers();
         if (prev instanceof LifeCycle2) {
            ((LifeCycle2)prev).stop(timeout, timeUnit);
         } else {
            prev.stop();
         }

         this.externalMap.clear();
         LogManager.getFactory().removeContext(this);
      } finally {
         this.configLock.unlock();
         this.setStopped();
      }

      if (this.listeners != null) {
         for (LoggerContextShutdownAware listener : this.listeners) {
            try {
               listener.contextShutdown(this);
            } catch (Exception var9) {
            }
         }
      }

      LOGGER.debug("Stopped LoggerContext[name={}, {}] with status {}", this.getName(), this, true);
      return true;
   }

   private void unregisterJmxBeans() {
      if (!JmxUtil.isJmxDisabled()) {
         try {
            Server.unregisterLoggerContext(this.getName());
         } catch (Exception | LinkageError var2) {
            LOGGER.error("Unable to unregister MBeans", var2);
         }
      }
   }

   public String getName() {
      return this.contextName;
   }

   public Logger getRootLogger() {
      return this.getLogger("");
   }

   public void setName(final String name) {
      this.contextName = Objects.requireNonNull(name);
   }

   public Object getObject(final String key) {
      return this.externalMap.get(key);
   }

   public Object putObject(final String key, final Object value) {
      return this.externalMap.put(key, value);
   }

   public Object putObjectIfAbsent(final String key, final Object value) {
      return this.externalMap.putIfAbsent(key, value);
   }

   public Object removeObject(final String key) {
      return this.externalMap.remove(key);
   }

   public boolean removeObject(final String key, final Object value) {
      return this.externalMap.remove(key, value);
   }

   public void setExternalContext(final Object context) {
      if (context != null) {
         this.externalMap.put("__EXTERNAL_CONTEXT_KEY__", context);
      } else {
         this.externalMap.remove("__EXTERNAL_CONTEXT_KEY__");
      }
   }

   public Object getExternalContext() {
      return this.externalMap.get("__EXTERNAL_CONTEXT_KEY__");
   }

   public Logger getLogger(final String name) {
      return this.getLogger(name, DEFAULT_MESSAGE_FACTORY);
   }

   public Collection<Logger> getLoggers() {
      return this.loggerRegistry.getLoggers();
   }

   public Logger getLogger(final String name, @Nullable final MessageFactory messageFactory) {
      MessageFactory effectiveMessageFactory = messageFactory != null ? messageFactory : DEFAULT_MESSAGE_FACTORY;
      return this.loggerRegistry.computeIfAbsent(name, effectiveMessageFactory, this::newInstance);
   }

   @Deprecated
   public LoggerRegistry<Logger> getLoggerRegistry() {
      LoggerRegistry<Logger> result = new LoggerRegistry();
      this.loggerRegistry.getLoggers().forEach(l -> result.putIfAbsent(l.getName(), l.getMessageFactory(), l));
      return result;
   }

   public boolean hasLogger(final String name) {
      return this.loggerRegistry.hasLogger(name, DEFAULT_MESSAGE_FACTORY);
   }

   public boolean hasLogger(final String name, @Nullable final MessageFactory messageFactory) {
      MessageFactory effectiveMessageFactory = messageFactory != null ? messageFactory : DEFAULT_MESSAGE_FACTORY;
      return this.loggerRegistry.hasLogger(name, effectiveMessageFactory);
   }

   public boolean hasLogger(final String name, final Class<? extends MessageFactory> messageFactoryClass) {
      return this.loggerRegistry.hasLogger(name, messageFactoryClass);
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public void addFilter(final Filter filter) {
      this.configuration.addFilter(filter);
   }

   public void removeFilter(final Filter filter) {
      this.configuration.removeFilter(filter);
   }

   public Configuration setConfiguration(final Configuration config) {
      if (config == null) {
         LOGGER.error("No configuration found for context '{}'.", this.contextName);
         return this.configuration;
      } else {
         this.configLock.lock();

         Configuration ex;
         try {
            Configuration prev = this.configuration;
            config.addListener(this);
            ConcurrentMap<String, String> map = config.getComponent("ContextProperties");

            try {
               map.computeIfAbsent("hostName", s -> NetUtils.getLocalHostname());
            } catch (Exception var8) {
               LOGGER.debug("Ignoring {}, setting hostName to 'unknown'", var8.toString());
               map.putIfAbsent("hostName", "unknown");
            }

            map.putIfAbsent("contextName", this.contextName);
            config.start();
            this.configuration = config;
            this.updateLoggers();
            if (prev != null) {
               prev.removeListener(this);
               prev.stop();
            }

            this.firePropertyChangeEvent(new PropertyChangeEvent(this, "config", prev, config));
            registerJmxBeans();
            Log4jLogEvent.setNanoClock(this.configuration.getNanoClock());
            ex = prev;
         } finally {
            this.configLock.unlock();
         }

         return ex;
      }
   }

   private static void registerJmxBeans() {
      if (!JmxUtil.isJmxDisabled()) {
         try {
            Server.reregisterMBeansAfterReconfigure();
         } catch (Exception | LinkageError var1) {
            LOGGER.error("Could not reconfigure JMX", var1);
         }
      }
   }

   private void firePropertyChangeEvent(final PropertyChangeEvent event) {
      for (PropertyChangeListener listener : this.propertyChangeListeners) {
         listener.propertyChange(event);
      }
   }

   public void addPropertyChangeListener(final PropertyChangeListener listener) {
      this.propertyChangeListeners.add(Objects.requireNonNull(listener, "listener"));
   }

   public void removePropertyChangeListener(final PropertyChangeListener listener) {
      this.propertyChangeListeners.remove(listener);
   }

   public URI getConfigLocation() {
      return this.configLocation;
   }

   public void setConfigLocation(final URI configLocation) {
      this.configLocation = configLocation;
      this.reconfigure(configLocation);
   }

   private void reconfigure(final URI configURI) {
      Object externalContext = this.externalMap.get("__EXTERNAL_CONTEXT_KEY__");
      ClassLoader cl = externalContext instanceof ClassLoader ? (ClassLoader)externalContext : null;
      LOGGER.debug("Reconfiguration started for context[name={}] at URI {} ({}) with optional ClassLoader: {}", this.contextName, configURI, this, cl);
      Configuration instance = ConfigurationFactory.getInstance().getConfiguration(this, this.contextName, configURI, cl);
      if (instance == null) {
         LOGGER.error("Reconfiguration failed: No configuration found for '{}' at '{}' in '{}'", this.contextName, configURI, cl);
      } else {
         this.setConfiguration(instance);
         String location = this.configuration == null ? "?" : String.valueOf(this.configuration.getConfigurationSource());
         LOGGER.debug("Reconfiguration complete for context[name={}] at URI {} ({}) with optional ClassLoader: {}", this.contextName, location, this, cl);
      }
   }

   public void reconfigure() {
      this.reconfigure(this.configLocation);
   }

   public void reconfigure(Configuration configuration) {
      this.setConfiguration(configuration);
      ConfigurationSource source = configuration.getConfigurationSource();
      if (source != null) {
         URI uri = source.getURI();
         if (uri != null) {
            this.configLocation = uri;
         }
      }
   }

   public void updateLoggers() {
      this.updateLoggers(this.configuration);
   }

   public void updateLoggers(final Configuration config) {
      Configuration old = this.configuration;
      this.loggerRegistry.getLoggers().forEach(logger -> logger.updateConfiguration(config));
      this.firePropertyChangeEvent(new PropertyChangeEvent(this, "config", old, config));
   }

   @Override
   public synchronized void onChange(final Reconfigurable reconfigurable) {
      long startMillis = System.currentTimeMillis();
      LOGGER.debug("Reconfiguration started for context {} ({})", this.contextName, this);
      this.initApiModule();
      Configuration newConfig = reconfigurable.reconfigure();
      if (newConfig != null) {
         this.setConfiguration(newConfig);
         LOGGER.debug("Reconfiguration completed for {} ({}) in {} milliseconds.", this.contextName, this, System.currentTimeMillis() - startMillis);
      } else {
         LOGGER.debug("Reconfiguration failed for {} ({}) in {} milliseconds.", this.contextName, this, System.currentTimeMillis() - startMillis);
      }
   }

   private void initApiModule() {
      ThreadContextMapFactory.init();
   }

   private Logger newInstance(final String name, final MessageFactory messageFactory) {
      return this.newInstance(this, name, messageFactory);
   }

   protected Logger newInstance(LoggerContext context, String name, MessageFactory messageFactory) {
      return new Logger(context, name, messageFactory);
   }
}
