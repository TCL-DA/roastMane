package org.apache.logging.log4j.core.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.Version;
import org.apache.logging.log4j.core.appender.AsyncAppender;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.async.AsyncLoggerConfig;
import org.apache.logging.log4j.core.async.AsyncLoggerConfigDelegate;
import org.apache.logging.log4j.core.async.AsyncLoggerConfigDisruptor;
import org.apache.logging.log4j.core.async.AsyncWaitStrategyFactory;
import org.apache.logging.log4j.core.async.AsyncWaitStrategyFactoryConfig;
import org.apache.logging.log4j.core.config.arbiters.Arbiter;
import org.apache.logging.log4j.core.config.arbiters.SelectArbiter;
import org.apache.logging.log4j.core.config.plugins.util.PluginBuilder;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;
import org.apache.logging.log4j.core.config.plugins.util.PluginType;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.lookup.ConfigurationStrSubstitutor;
import org.apache.logging.log4j.core.lookup.Interpolator;
import org.apache.logging.log4j.core.lookup.PropertiesLookup;
import org.apache.logging.log4j.core.lookup.RuntimeStrSubstitutor;
import org.apache.logging.log4j.core.lookup.StrLookup;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.core.net.Advertiser;
import org.apache.logging.log4j.core.script.AbstractScript;
import org.apache.logging.log4j.core.script.ScriptManager;
import org.apache.logging.log4j.core.script.ScriptRef;
import org.apache.logging.log4j.core.util.DummyNanoClock;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.core.util.NameUtil;
import org.apache.logging.log4j.core.util.NanoClock;
import org.apache.logging.log4j.core.util.Source;
import org.apache.logging.log4j.core.util.WatchManager;
import org.apache.logging.log4j.core.util.Watcher;
import org.apache.logging.log4j.core.util.WatcherFactory;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

public abstract class AbstractConfiguration extends AbstractFilterable implements Configuration {
   private static final int BUF_SIZE = 16384;
   protected Node rootNode;
   protected final List<ConfigurationListener> listeners = new CopyOnWriteArrayList<>();
   protected final List<String> pluginPackages = new ArrayList<>();
   protected PluginManager pluginManager;
   protected boolean isShutdownHookEnabled = true;
   protected long shutdownTimeoutMillis;
   protected ScriptManager scriptManager;
   private Advertiser advertiser = new DefaultAdvertiser();
   private Node advertiserNode;
   private Object advertisement;
   private String name;
   private ConcurrentMap<String, Appender> appenders = new ConcurrentHashMap<>();
   private ConcurrentMap<String, LoggerConfig> loggerConfigs = new ConcurrentHashMap<>();
   private List<CustomLevelConfig> customLevels = Collections.emptyList();
   private Set<MonitorResource> monitorResources = Collections.emptySet();
   private final ConcurrentMap<String, String> propertyMap = new ConcurrentHashMap<>();
   private final Interpolator tempLookup = new Interpolator(this.propertyMap);
   private final StrSubstitutor runtimeStrSubstitutor = new RuntimeStrSubstitutor(this.tempLookup);
   private final StrSubstitutor configurationStrSubstitutor = new ConfigurationStrSubstitutor(this.runtimeStrSubstitutor);
   private LoggerConfig root = new LoggerConfig();
   private final ConcurrentMap<String, Object> componentMap = new ConcurrentHashMap<>();
   private final ConfigurationSource configurationSource;
   private final ConfigurationScheduler configurationScheduler = new ConfigurationScheduler();
   private final WatchManager watchManager = new WatchManager(this.configurationScheduler);
   private AsyncLoggerConfigDisruptor asyncLoggerConfigDisruptor;
   private AsyncWaitStrategyFactory asyncWaitStrategyFactory;
   private NanoClock nanoClock = new DummyNanoClock();
   private final WeakReference<LoggerContext> loggerContext;

   protected AbstractConfiguration(final LoggerContext loggerContext, final ConfigurationSource configurationSource) {
      this.loggerContext = new WeakReference<>(loggerContext);
      this.tempLookup.setLoggerContext(loggerContext);
      this.configurationSource = Objects.requireNonNull(configurationSource, "configurationSource is null");
      this.componentMap.put("ContextProperties", this.propertyMap);
      this.pluginManager = new PluginManager("Core");
      this.rootNode = new Node();
      this.setState(LifeCycle.State.INITIALIZING);
   }

   @Override
   public ConfigurationSource getConfigurationSource() {
      return this.configurationSource;
   }

   @Override
   public List<String> getPluginPackages() {
      return this.pluginPackages;
   }

   @Override
   public Map<String, String> getProperties() {
      return this.propertyMap;
   }

   @Override
   public ScriptManager getScriptManager() {
      return this.scriptManager;
   }

   public void setScriptManager(final ScriptManager scriptManager) {
      this.scriptManager = scriptManager;
   }

   public PluginManager getPluginManager() {
      return this.pluginManager;
   }

   public void setPluginManager(final PluginManager pluginManager) {
      this.pluginManager = pluginManager;
   }

   @Override
   public WatchManager getWatchManager() {
      return this.watchManager;
   }

   @Override
   public ConfigurationScheduler getScheduler() {
      return this.configurationScheduler;
   }

   public Node getRootNode() {
      return this.rootNode;
   }

   @Override
   public AsyncLoggerConfigDelegate getAsyncLoggerConfigDelegate() {
      if (this.asyncLoggerConfigDisruptor == null) {
         this.asyncLoggerConfigDisruptor = new AsyncLoggerConfigDisruptor(this.asyncWaitStrategyFactory);
      }

      return this.asyncLoggerConfigDisruptor;
   }

   @Override
   public AsyncWaitStrategyFactory getAsyncWaitStrategyFactory() {
      return this.asyncWaitStrategyFactory;
   }

   @Override
   public void initialize() {
      LOGGER.debug(Version.getProductString() + " initializing configuration {}", this);
      this.runtimeStrSubstitutor.setConfiguration(this);
      this.configurationStrSubstitutor.setConfiguration(this);
      String scriptLanguages = PropertiesUtil.getProperties().getStringProperty("log4j2.Script.enableLanguages");
      if (scriptLanguages != null) {
         try {
            this.scriptManager = new ScriptManager(this, this.watchManager, scriptLanguages);
         } catch (Exception | LinkageError var8) {
            LOGGER.info("Cannot initialize scripting support because this JRE does not support it.", var8);
         }
      }

      if (!this.pluginPackages.isEmpty()) {
         LOGGER.warn(
            "The use of package scanning to locate Log4j plugins is deprecated.\nPlease remove the `packages` attribute from your configuration file.\nSee https://logging.apache.org/log4j/2.x/faq.html#package-scanning for details."
         );
      }

      this.pluginManager.collectPlugins(this.pluginPackages);
      PluginManager levelPlugins = new PluginManager("Level");
      levelPlugins.collectPlugins(this.pluginPackages);
      Map<String, PluginType<?>> plugins = levelPlugins.getPlugins();
      if (plugins != null) {
         for (PluginType<?> type : plugins.values()) {
            try {
               Loader.initializeClass(type.getPluginClass().getName(), type.getPluginClass().getClassLoader());
            } catch (Exception var7) {
               LOGGER.error("Unable to initialize {} due to {}", type.getPluginClass().getName(), var7.getClass().getSimpleName(), var7);
            }
         }
      }

      this.setup();
      this.setupAdvertisement();
      this.doConfigure();
      this.watchMonitorResources();
      this.setState(LifeCycle.State.INITIALIZED);
      LOGGER.debug("Configuration {} initialized", this);
   }

   protected void initializeWatchers(final Reconfigurable reconfigurable, final ConfigurationSource configSource, final int monitorIntervalSeconds) {
      if (configSource != null && (configSource.getFile() != null || configSource.getURL() != null)) {
         if (monitorIntervalSeconds > 0) {
            this.watchManager.setIntervalSeconds(monitorIntervalSeconds);
            File file = configSource.getFile();
            if (file != null) {
               Source cfgSource = new Source(file);
               long lastModified = file.lastModified();
               ConfigurationFileWatcher watcher = new ConfigurationFileWatcher(this, reconfigurable, this.listeners, lastModified);
               this.watchManager.watch(cfgSource, watcher);
            } else if (configSource.getURL() != null) {
               this.monitorSource(reconfigurable, configSource);
            }
         } else if (this.watchManager.hasEventListeners() && configSource.getURL() != null && monitorIntervalSeconds >= 0) {
            this.monitorSource(reconfigurable, configSource);
         }
      }
   }

   private void monitorSource(final Reconfigurable reconfigurable, final ConfigurationSource configSource) {
      URI uri = configSource.getURI();
      if (uri != null && configSource.getLastModified() > 0L) {
         File file = configSource.getFile();
         Source cfgSource = file != null ? new Source(file) : new Source(uri);
         Watcher watcher = WatcherFactory.getInstance(this.pluginPackages)
            .newWatcher(cfgSource, this, reconfigurable, this.listeners, configSource.getLastModified());
         if (watcher != null) {
            this.watchManager.watch(cfgSource, watcher);
         }
      } else {
         LOGGER.info("{} does not support dynamic reconfiguration", configSource.getURI());
      }
   }

   @Override
   public void start() {
      if (this.getState() == LifeCycle.State.INITIALIZING) {
         this.initialize();
      }

      LOGGER.info("Starting configuration {}...", this);
      this.setStarting();
      if (this.isConfigurationMonitoringEnabled()) {
         LOGGER.info(
            "Start watching for changes to {} every {} seconds", this.watchManager.getConfigurationWatchers().keySet(), this.watchManager.getIntervalSeconds()
         );
         this.watchManager.start();
      }

      if (this.hasAsyncLoggers()) {
         this.asyncLoggerConfigDisruptor.start();
      }

      Set<LoggerConfig> alreadyStarted = new HashSet<>();

      for (LoggerConfig logger : this.loggerConfigs.values()) {
         logger.start();
         alreadyStarted.add(logger);
      }

      for (Appender appender : this.appenders.values()) {
         appender.start();
      }

      if (!alreadyStarted.contains(this.root)) {
         this.root.start();
      }

      super.start();
      LOGGER.info("Configuration {} started.", this);
   }

   private boolean isConfigurationMonitoringEnabled() {
      return this instanceof Reconfigurable && this.watchManager.getIntervalSeconds() > 0;
   }

   private void watchMonitorResources() {
      if (this.isConfigurationMonitoringEnabled()) {
         this.monitorResources.forEach(monitorResource -> {
            Source source = new Source(monitorResource.getUri());
            ConfigurationFileWatcher watcher = new ConfigurationFileWatcher(this, (Reconfigurable)this, this.listeners, source.getFile().lastModified());
            this.watchManager.watch(source, watcher);
         });
      }
   }

   private boolean hasAsyncLoggers() {
      if (this.root instanceof AsyncLoggerConfig) {
         return true;
      } else {
         for (LoggerConfig logger : this.loggerConfigs.values()) {
            if (logger instanceof AsyncLoggerConfig) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean stop(final long timeout, final TimeUnit timeUnit) {
      LOGGER.info("Stopping configuration {}...", this);
      this.setStopping();
      super.stop(timeout, timeUnit, false);

      for (LoggerConfig loggerConfig : this.loggerConfigs.values()) {
         loggerConfig.getReliabilityStrategy().beforeStopConfiguration(this);
      }

      this.root.getReliabilityStrategy().beforeStopConfiguration(this);
      String cls = this.getClass().getSimpleName();
      LOGGER.trace("{} notified {} ReliabilityStrategies that config will be stopped.", cls, this.loggerConfigs.size() + 1);
      if (!this.loggerConfigs.isEmpty()) {
         LOGGER.trace("{} stopping {} LoggerConfigs.", cls, this.loggerConfigs.size());

         for (LoggerConfig logger : this.loggerConfigs.values()) {
            logger.stop(timeout, timeUnit);
         }
      }

      LOGGER.trace("{} stopping root LoggerConfig.", cls);
      if (!this.root.isStopped()) {
         this.root.stop(timeout, timeUnit);
      }

      if (this.hasAsyncLoggers()) {
         LOGGER.trace("{} stopping AsyncLoggerConfigDisruptor.", cls);
         this.asyncLoggerConfigDisruptor.stop(timeout, timeUnit);
      }

      LOGGER.trace("{} notifying ReliabilityStrategies that appenders will be stopped.", cls);

      for (LoggerConfig loggerConfig : this.loggerConfigs.values()) {
         loggerConfig.getReliabilityStrategy().beforeStopAppenders();
      }

      this.root.getReliabilityStrategy().beforeStopAppenders();
      Appender[] array = this.appenders.values().toArray(Appender.EMPTY_ARRAY);
      List<Appender> async = this.getAsyncAppenders(array);
      if (!async.isEmpty()) {
         LOGGER.trace("{} stopping {} AsyncAppenders.", cls, async.size());

         for (Appender appender : async) {
            if (appender instanceof LifeCycle2) {
               ((LifeCycle2)appender).stop(timeout, timeUnit);
            } else {
               appender.stop();
            }
         }
      }

      LOGGER.trace("{} stopping remaining Appenders.", cls);
      int appenderCount = 0;

      for (int i = array.length - 1; i >= 0; i--) {
         if (array[i].isStarted()) {
            if (array[i] instanceof LifeCycle2) {
               ((LifeCycle2)array[i]).stop(timeout, timeUnit);
            } else {
               array[i].stop();
            }

            appenderCount++;
         }
      }

      LOGGER.trace("{} stopped {} remaining Appenders.", cls, appenderCount);
      LOGGER.trace("{} cleaning Appenders from {} LoggerConfigs.", cls, this.loggerConfigs.size() + 1);

      for (LoggerConfig loggerConfig : this.loggerConfigs.values()) {
         loggerConfig.clearAppenders();
      }

      this.root.clearAppenders();
      if (this.watchManager.isStarted()) {
         this.watchManager.stop(timeout, timeUnit);
      }

      this.configurationScheduler.stop(timeout, timeUnit);
      if (this.advertiser != null && this.advertisement != null) {
         this.advertiser.unadvertise(this.advertisement);
      }

      this.setStopped();
      LOGGER.info("Configuration {} stopped.", this);
      return true;
   }

   private List<Appender> getAsyncAppenders(final Appender[] all) {
      List<Appender> result = new ArrayList<>();

      for (int i = all.length - 1; i >= 0; i--) {
         if (all[i] instanceof AsyncAppender) {
            result.add(all[i]);
         }
      }

      return result;
   }

   @Override
   public boolean isShutdownHookEnabled() {
      return this.isShutdownHookEnabled;
   }

   @Override
   public long getShutdownTimeoutMillis() {
      return this.shutdownTimeoutMillis;
   }

   public void setup() {
   }

   protected Level getDefaultStatus() {
      PropertiesUtil properties = PropertiesUtil.getProperties();
      String statusLevel = properties.getStringProperty("log4j2.StatusLogger.level");
      if (statusLevel == null) {
         statusLevel = properties.getStringProperty("Log4jDefaultStatusLevel", Level.ERROR.name());
      }

      try {
         return Level.toLevel(statusLevel);
      } catch (Exception var4) {
         return Level.ERROR;
      }
   }

   protected void createAdvertiser(final String advertiserString, final ConfigurationSource configSource, final byte[] buffer, final String contentType) {
      if (advertiserString != null) {
         Node node = new Node(null, advertiserString, null);
         Map<String, String> attributes = node.getAttributes();
         attributes.put("content", new String(buffer));
         attributes.put("contentType", contentType);
         attributes.put("name", "configuration");
         if (configSource.getLocation() != null) {
            attributes.put("location", configSource.getLocation());
         }

         this.advertiserNode = node;
      }
   }

   private void setupAdvertisement() {
      if (this.advertiserNode != null) {
         String nodeName = this.advertiserNode.getName();
         PluginType<?> type = this.pluginManager.getPluginType(nodeName);
         if (type != null) {
            Class<? extends Advertiser> clazz = type.getPluginClass().asSubclass(Advertiser.class);

            try {
               this.advertiser = (Advertiser)LoaderUtil.newInstanceOf(clazz);
               this.advertisement = this.advertiser.advertise(this.advertiserNode.getAttributes());
            } catch (ReflectiveOperationException var5) {
               LOGGER.error("{} attempting to instantiate advertiser: {}", var5.getClass().getSimpleName(), nodeName, var5);
            }
         }
      }
   }

   @Override
   public <T> T getComponent(final String componentName) {
      return (T)this.componentMap.get(componentName);
   }

   @Override
   public void addComponent(final String componentName, final Object obj) {
      this.componentMap.putIfAbsent(componentName, obj);
   }

   protected void preConfigure(final Node node) {
      try {
         for (Node child : node.getChildren()) {
            if (child.getType() == null) {
               LOGGER.error("Unable to locate plugin type for " + child.getName());
            } else {
               Class<?> clazz = child.getType().getPluginClass();
               if (clazz.isAnnotationPresent(Scheduled.class)) {
                  this.configurationScheduler.incrementScheduledItems();
               }

               this.preConfigure(child);
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Error capturing node data for node " + node.getName(), var5);
      }
   }

   protected void processConditionals(final Node node) {
      try {
         List<Node> addList = new ArrayList<>();
         List<Node> removeList = new ArrayList<>();

         for (Node child : node.getChildren()) {
            PluginType<?> type = child.getType();
            if (type != null && "Arbiter".equals(type.getElementName())) {
               Class<?> clazz = type.getPluginClass();
               if (SelectArbiter.class.isAssignableFrom(clazz)) {
                  removeList.add(child);
                  addList.addAll(this.processSelect(child, type));
               } else if (Arbiter.class.isAssignableFrom(clazz)) {
                  removeList.add(child);

                  try {
                     Arbiter condition = (Arbiter)this.createPluginObject(type, child, null);
                     if (condition.isCondition()) {
                        addList.addAll(child.getChildren());
                        this.processConditionals(child);
                     }
                  } catch (Exception var9) {
                     LOGGER.error("Exception processing {}: Ignoring and including children", type.getPluginClass());
                     this.processConditionals(child);
                  }
               } else {
                  LOGGER.error("Encountered Condition Plugin that does not implement Condition: {}", child.getName());
                  this.processConditionals(child);
               }
            } else {
               this.processConditionals(child);
            }
         }

         if (!removeList.isEmpty()) {
            List<Node> children = node.getChildren();
            children.removeAll(removeList);
            children.addAll(addList);

            for (Node grandChild : addList) {
               grandChild.setParent(node);
            }
         }
      } catch (Exception var10) {
         LOGGER.error("Error capturing node data for node " + node.getName(), var10);
      }
   }

   protected List<Node> processSelect(final Node selectNode, final PluginType<?> type) {
      List<Node> addList = new ArrayList<>();
      SelectArbiter select = (SelectArbiter)this.createPluginObject(type, selectNode, null);
      List<Arbiter> conditions = new ArrayList<>();

      for (Node child : selectNode.getChildren()) {
         PluginType<?> nodeType = child.getType();
         if (nodeType != null) {
            if (Arbiter.class.isAssignableFrom(nodeType.getPluginClass())) {
               Arbiter condition = (Arbiter)this.createPluginObject(nodeType, child, null);
               conditions.add(condition);
               child.setObject(condition);
            } else {
               LOGGER.error("Invalid Node {} for Select. Must be a Condition", child.getName());
            }
         } else {
            LOGGER.error("No PluginType for node {}", child.getName());
         }
      }

      Arbiter condition = select.evaluateConditions(conditions);
      if (condition != null) {
         for (Node childx : selectNode.getChildren()) {
            if (condition == childx.getObject()) {
               addList.addAll(childx.getChildren());
               this.processConditionals(childx);
            }
         }
      }

      return addList;
   }

   protected void doConfigure() {
      this.processConditionals(this.rootNode);
      this.preConfigure(this.rootNode);
      this.configurationScheduler.start();
      List<Node> children = this.rootNode.getChildren();
      boolean hasProperties = false;

      for (Node child : children) {
         if ("Properties".equalsIgnoreCase(child.getName())) {
            hasProperties = true;
            this.createConfiguration(child, null);
            if (child.getObject() != null) {
               StrLookup lookup = child.getObject();
               this.runtimeStrSubstitutor.setVariableResolver(lookup);
               this.configurationStrSubstitutor.setVariableResolver(lookup);
            }
            break;
         }
      }

      if (!hasProperties) {
         Map<String, String> map = this.getComponent("ContextProperties");
         StrLookup lookup = map == null ? null : new PropertiesLookup(map);
         Interpolator interpolator = new Interpolator(lookup, this.pluginPackages);
         interpolator.setConfiguration(this);
         interpolator.setLoggerContext(this.loggerContext.get());
         this.runtimeStrSubstitutor.setVariableResolver(interpolator);
         this.configurationStrSubstitutor.setVariableResolver(interpolator);
      }

      for (Node childx : children) {
         if ("Scripts".equalsIgnoreCase(childx.getName())) {
            this.createConfiguration(childx, null);
            if (childx.getObject() != null) {
               for (AbstractScript script : childx.getObject(AbstractScript[].class)) {
                  if (script instanceof ScriptRef) {
                     LOGGER.error("Script reference to {} not added. Scripts definition cannot contain script references", script.getName());
                  } else if (this.scriptManager != null) {
                     this.scriptManager.addScript(script);
                  }
               }
            }
            break;
         }
      }

      boolean setLoggers = false;
      boolean setRoot = false;

      for (Node childxx : children) {
         if (!"Properties".equalsIgnoreCase(childxx.getName()) && !"Scripts".equalsIgnoreCase(childxx.getName())) {
            this.createConfiguration(childxx, null);
            if (childxx.getObject() != null) {
               if ("Appenders".equalsIgnoreCase(childxx.getName())) {
                  this.appenders = childxx.getObject();
               } else if (childxx.isInstanceOf(Filter.class)) {
                  this.addFilter(childxx.getObject(Filter.class));
               } else if (childxx.isInstanceOf(Loggers.class)) {
                  Loggers l = childxx.getObject();
                  this.loggerConfigs = l.getMap();
                  setLoggers = true;
                  if (l.getRoot() != null) {
                     this.root = l.getRoot();
                     setRoot = true;
                  }
               } else if (childxx.isInstanceOf(CustomLevels.class)) {
                  this.customLevels = childxx.getObject(CustomLevels.class).getCustomLevels();
               } else if (childxx.isInstanceOf(CustomLevelConfig.class)) {
                  List<CustomLevelConfig> copy = new ArrayList<>(this.customLevels);
                  copy.add(childxx.getObject(CustomLevelConfig.class));
                  this.customLevels = copy;
               } else if (childxx.isInstanceOf(AsyncWaitStrategyFactoryConfig.class)) {
                  AsyncWaitStrategyFactoryConfig awsfc = childxx.getObject(AsyncWaitStrategyFactoryConfig.class);
                  this.asyncWaitStrategyFactory = awsfc.createWaitStrategyFactory();
               } else if (childxx.isInstanceOf(MonitorResources.class)) {
                  this.monitorResources = childxx.getObject(MonitorResources.class).getResources();
               } else {
                  List<String> expected = Arrays.asList(
                     "\"Appenders\"", "\"Loggers\"", "\"Properties\"", "\"Scripts\"", "\"CustomLevels\"", "\"MonitorResources\""
                  );
                  LOGGER.error(
                     "Unknown object \"{}\" of type {} is ignored: try nesting it inside one of: {}.",
                     childxx.getName(),
                     childxx.getObject().getClass().getName(),
                     expected
                  );
               }
            }
         }
      }

      if (!setLoggers) {
         LOGGER.warn("No Loggers were configured, using default. Is the Loggers element missing?");
         this.setToDefault();
      } else {
         if (!setRoot) {
            LOGGER.warn("No Root logger was configured, creating default ERROR-level Root logger with Console appender");
            this.setToDefault();
         }

         for (Entry<String, LoggerConfig> entry : this.loggerConfigs.entrySet()) {
            LoggerConfig loggerConfig = entry.getValue();

            for (AppenderRef ref : loggerConfig.getAppenderRefs()) {
               Appender app = this.appenders.get(ref.getRef());
               if (app != null) {
                  loggerConfig.addAppender(app, ref.getLevel(), ref.getFilter());
               } else {
                  LOGGER.error("Unable to locate appender \"{}\" for logger config \"{}\"", ref.getRef(), loggerConfig);
               }
            }
         }

         this.setParents();
      }
   }

   public static Level getDefaultLevel() {
      String levelName = PropertiesUtil.getProperties().getStringProperty("org.apache.logging.log4j.level", Level.ERROR.name());
      return Level.valueOf(levelName);
   }

   protected void setToDefault() {
      if (Strings.isBlank(this.getName())) {
         this.setName("Default@" + Integer.toHexString(this.hashCode()));
      }

      Appender appender = ConsoleAppender.createDefaultAppenderForLayout(DefaultLayout.INSTANCE);
      appender.start();
      this.addAppender(appender);
      LoggerConfig rootLoggerConfig = this.getRootLogger();
      rootLoggerConfig.addAppender(appender, null, null);
      rootLoggerConfig.setLevel(getDefaultLevel());
   }

   public void setName(final String name) {
      this.name = name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public void addListener(final ConfigurationListener listener) {
      this.listeners.add(listener);
   }

   @Override
   public void removeListener(final ConfigurationListener listener) {
      this.listeners.remove(listener);
   }

   @Override
   public <T extends Appender> T getAppender(final String appenderName) {
      return (T)(appenderName != null ? this.appenders.get(appenderName) : null);
   }

   @Override
   public Map<String, Appender> getAppenders() {
      return this.appenders;
   }

   @Override
   public void addAppender(final Appender appender) {
      if (appender != null) {
         this.appenders.putIfAbsent(appender.getName(), appender);
      }
   }

   @Override
   public StrSubstitutor getStrSubstitutor() {
      return this.runtimeStrSubstitutor;
   }

   @Override
   public StrSubstitutor getConfigurationStrSubstitutor() {
      return this.configurationStrSubstitutor;
   }

   @Override
   public void setAdvertiser(final Advertiser advertiser) {
      this.advertiser = advertiser;
   }

   @Override
   public Advertiser getAdvertiser() {
      return this.advertiser;
   }

   @Override
   public ReliabilityStrategy getReliabilityStrategy(final LoggerConfig loggerConfig) {
      return ReliabilityStrategyFactory.getReliabilityStrategy(loggerConfig);
   }

   @Override
   public synchronized void addLoggerAppender(final Logger logger, final Appender appender) {
      if (appender != null && logger != null) {
         String loggerName = logger.getName();
         this.appenders.putIfAbsent(appender.getName(), appender);
         LoggerConfig lc = this.getLoggerConfig(loggerName);
         if (lc.getName().equals(loggerName)) {
            lc.addAppender(appender, null, null);
         } else {
            LoggerConfig nlc = new LoggerConfig(loggerName, lc.getLevel(), lc.isAdditive());
            nlc.addAppender(appender, null, null);
            nlc.setParent(lc);
            this.loggerConfigs.putIfAbsent(loggerName, nlc);
            this.setParents();
            logger.getContext().updateLoggers();
         }
      }
   }

   @Override
   public synchronized void addLoggerFilter(final Logger logger, final Filter filter) {
      String loggerName = logger.getName();
      LoggerConfig lc = this.getLoggerConfig(loggerName);
      if (lc.getName().equals(loggerName)) {
         lc.addFilter(filter);
      } else {
         LoggerConfig nlc = new LoggerConfig(loggerName, lc.getLevel(), lc.isAdditive());
         nlc.addFilter(filter);
         nlc.setParent(lc);
         this.loggerConfigs.putIfAbsent(loggerName, nlc);
         this.setParents();
         logger.getContext().updateLoggers();
      }
   }

   @Override
   public synchronized void setLoggerAdditive(final Logger logger, final boolean additive) {
      String loggerName = logger.getName();
      LoggerConfig lc = this.getLoggerConfig(loggerName);
      if (lc.getName().equals(loggerName)) {
         lc.setAdditive(additive);
      } else {
         LoggerConfig nlc = new LoggerConfig(loggerName, lc.getLevel(), additive);
         nlc.setParent(lc);
         this.loggerConfigs.putIfAbsent(loggerName, nlc);
         this.setParents();
         logger.getContext().updateLoggers();
      }
   }

   public synchronized void removeAppender(final String appenderName) {
      for (LoggerConfig logger : this.loggerConfigs.values()) {
         logger.removeAppender(appenderName);
      }

      Appender app = appenderName != null ? this.appenders.remove(appenderName) : null;
      if (app != null) {
         app.stop();
      }
   }

   @Override
   public List<CustomLevelConfig> getCustomLevels() {
      return Collections.unmodifiableList(this.customLevels);
   }

   @Override
   public LoggerConfig getLoggerConfig(final String loggerName) {
      LoggerConfig loggerConfig = this.loggerConfigs.get(loggerName);
      if (loggerConfig != null) {
         return loggerConfig;
      } else {
         String substr = loggerName;

         while ((substr = NameUtil.getSubName(substr)) != null) {
            loggerConfig = this.loggerConfigs.get(substr);
            if (loggerConfig != null) {
               return loggerConfig;
            }
         }

         return this.root;
      }
   }

   @Override
   public LoggerContext getLoggerContext() {
      return this.loggerContext.get();
   }

   @Override
   public LoggerConfig getRootLogger() {
      return this.root;
   }

   @Override
   public Map<String, LoggerConfig> getLoggers() {
      return Collections.unmodifiableMap(this.loggerConfigs);
   }

   public LoggerConfig getLogger(final String loggerName) {
      return this.loggerConfigs.get(loggerName);
   }

   @Override
   public synchronized void addLogger(final String loggerName, final LoggerConfig loggerConfig) {
      this.loggerConfigs.putIfAbsent(loggerName, loggerConfig);
      this.setParents();
   }

   @Override
   public synchronized void removeLogger(final String loggerName) {
      this.loggerConfigs.remove(loggerName);
      this.setParents();
   }

   @Override
   public void createConfiguration(final Node node, final LogEvent event) {
      PluginType<?> type = node.getType();
      if (type != null && type.isDeferChildren()) {
         node.setObject(this.createPluginObject(type, node, event));
      } else {
         for (Node child : node.getChildren()) {
            this.createConfiguration(child, event);
         }

         if (type == null) {
            if (node.getParent() != null) {
               LOGGER.error("Unable to locate plugin for {}", node.getName());
            }
         } else {
            node.setObject(this.createPluginObject(type, node, event));
         }
      }
   }

   public Object createPluginObject(final PluginType<?> type, final Node node) {
      if (this.getState() == LifeCycle.State.INITIALIZING) {
         return this.createPluginObject(type, node, null);
      } else {
         LOGGER.warn("Plugin Object creation is not allowed after initialization");
         return null;
      }
   }

   private Object createPluginObject(final PluginType<?> type, final Node node, final LogEvent event) {
      Class<?> clazz = type.getPluginClass();
      if (Map.class.isAssignableFrom(clazz)) {
         try {
            return createPluginMap(node);
         } catch (Exception var7) {
            LOGGER.warn("Unable to create Map for {} of class {}", type.getElementName(), clazz, var7);
         }
      }

      if (Collection.class.isAssignableFrom(clazz)) {
         try {
            return createPluginCollection(node);
         } catch (Exception var6) {
            LOGGER.warn("Unable to create List for {} of class {}", type.getElementName(), clazz, var6);
         }
      }

      return new PluginBuilder(type).withConfiguration(this).withConfigurationNode(node).forLogEvent(event).build();
   }

   private static Map<String, ?> createPluginMap(final Node node) {
      Map<String, Object> map = new LinkedHashMap<>();

      for (Node child : node.getChildren()) {
         Object object = child.getObject();
         map.put(child.getName(), object);
      }

      return map;
   }

   private static Collection<?> createPluginCollection(final Node node) {
      List<Node> children = node.getChildren();
      Collection<Object> list = new ArrayList<>(children.size());

      for (Node child : children) {
         Object object = child.getObject();
         list.add(object);
      }

      return list;
   }

   private void setParents() {
      for (Entry<String, LoggerConfig> entry : this.loggerConfigs.entrySet()) {
         LoggerConfig logger = entry.getValue();
         String key = entry.getKey();
         if (!key.isEmpty()) {
            int i = key.lastIndexOf(46);
            if (i > 0) {
               key = key.substring(0, i);
               LoggerConfig parent = this.getLoggerConfig(key);
               if (parent == null) {
                  parent = this.root;
               }

               logger.setParent(parent);
            } else {
               logger.setParent(this.root);
            }
         }
      }
   }

   protected static byte[] toByteArray(final InputStream is) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      byte[] data = new byte[16384];

      int nRead;
      while ((nRead = is.read(data, 0, data.length)) != -1) {
         buffer.write(data, 0, nRead);
      }

      return buffer.toByteArray();
   }

   @Override
   public NanoClock getNanoClock() {
      return this.nanoClock;
   }

   @Override
   public void setNanoClock(final NanoClock nanoClock) {
      this.nanoClock = Objects.requireNonNull(nanoClock, "nanoClock");
   }
}
