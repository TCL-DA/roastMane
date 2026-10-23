package org.cropster.roastingintelligence;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.config.FileSystemConfiguration;
import com.cropster.roastingintelligence.config.PreferencesConfiguration;
import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.NotificationType;
import com.cropster.roastingintelligence.service.NotificationBusService;
import com.cropster.roastingintelligence.service.StorageService;
import com.cropster.roastingintelligence.service.impl.StorageServiceImpl;
import com.cropster.roastingintelligence.system.SystemSupport;
import com.cropster.roastingintelligence.ui.JavaFxController;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.Icon;
import com.cropster.roastingintelligence.ui.util.PlatformScheduler;
import com.cropster.roastingintelligence.ui.util.WindowUtil;
import com.sun.prism.GraphicsPipeline;
import com.sun.prism.impl.PrismSettings;
import java.awt.Desktop;
import java.awt.GraphicsEnvironment;
import java.awt.Desktop.Action;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.channels.FileLock;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TimeZone;
import java.util.Locale.Category;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import local.OfflineBootstrap;
import local.OfflineConnection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.measurement.impl.DemoSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.remote.ReadOnlyConnection;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.service.RoastPersister;
import org.cropster.roastingintelligence.service.impl.FilePersister;
import org.cropster.roastingintelligence.util.Measures;
import org.cropster.roastingintelligence.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoastingIntelligenceApp extends Application {
   private static final String LOG_LINE_START = "******************** START ********************";
   private static final int EXIT_CODE_OK = 0;
   private static final int EXIT_CODE_ERROR_LOCK = 1;
   private static final int EXIT_CODE_ERROR_GUI = 2;
   private static final String PROPERTIES_FILE = "/application.properties";
   private static final String JVM_MAXVRAM = "prism.maxvram";
   public static final String PREF_LOCALE = "locale";
   public static final String PREF_TEMPERATURE_SOURCE = "temperatureSource";
   private static final String[] JNLP_PROPERTIES = new String[]{"instance", "phidgets.ip", "phidgets.port"};
   private static final String LOG_CONFIGURATION = "log4j2.yaml";
   private static Logger logger;
   private static FileLock _lock;
   private static RandomAccessFile _lockFile;
   private static final ConcurrentHashMap<Class<?>, Set<Object>> _serviceRegistry = new ConcurrentHashMap<>();
   private static volatile HostServices hostServices;
   private final List<String> _setupWarnings = new ArrayList<>();
   private final List<MeasurementSourceFactory.ConfigurationWarning> _configurationWarnings = new ArrayList<>();
   private RoastingManager _roastingManager;
   private AppModel _appModel;
   private ApplicationController _controller;
   private Stage stage;

   public static <S, R extends S, T extends S> void registerService(Class<R> serviceInterface, T service) {
      if (!_serviceRegistry.containsKey(serviceInterface)) {
         _serviceRegistry.put(serviceInterface, new LinkedHashSet<>());
      }

      _serviceRegistry.get(serviceInterface).add(service);
   }

   public static <S, R extends S, T extends S> void unregisterService(Class<R> serviceInterface, T service) {
      if (_serviceRegistry.containsKey(serviceInterface)) {
         _serviceRegistry.get(serviceInterface).remove(service);
      }
   }

   public static <S> Set<S> retrieveService(Class<S> serviceinterface) {
      return (Set<S>)(_serviceRegistry.containsKey(serviceinterface) ? _serviceRegistry.get(serviceinterface) : Collections.emptySet());
   }

   public static <S> S retrieveSingleton(Class<S> serviceInterface) {
      Set<Object> services = _serviceRegistry.get(serviceInterface);
      if (services != null && !services.isEmpty()) {
         if (services.size() == 1) {
            return (S)services.iterator().next();
         } else {
            throw new IllegalStateException("No singelton: " + serviceInterface.getName());
         }
      } else {
         return null;
      }
   }

   public static void showDocument(String uri) {
      hostServices.showDocument(uri);
   }

   private static void aquireLock(String applicationDirectory) {
      try {
         File lockFile = new File(applicationDirectory, ".roastingintelligence.lock");
         _lockFile = new RandomAccessFile(lockFile, "rw");
         _lock = _lockFile.getChannel().tryLock();
         if (_lock != null) {
            lockFile.deleteOnExit();
         }
      } catch (Exception var2) {
         var2.printStackTrace(System.err);
      }
   }

   private static void releaseLock() {
      try {
         _lock.release();
         _lockFile.close();
      } catch (IOException var1) {
         logger.error("Error releasing lock", var1);
      }
   }

   private void checkKillSwitch(Date warnDate, Date killDate) {
      Date now = new Date();
      DateFormat dateFormat = DateFormat.getDateInstance(2);
      String killDateString = " " + dateFormat.format(killDate);
      Hyperlink hyperlink = new Hyperlink("https://download.cropster.com");
      hyperlink.setOnAction(e -> this.getHostServices().showDocument("https://download.cropster.com"));
      if (now.compareTo(killDate) > 0) {
         Alert alert = WindowUtil.createTextFlowAlert(
            AlertType.ERROR, org.cropster.roastingintelligence.util.Messages.m_("Version outdated") + killDateString, null, "button--error"
         );
         alert.getDialogPane()
            .setContent(
               new TextFlow(
                  new Node[]{
                     new Text(
                        org.cropster.roastingintelligence.util.Messages.m_(
                           "This is an outdated beta version and is no longer supported!\n\nRemove the software and install the current version from the following URL:"
                        )
                     ),
                     hyperlink,
                     new Text("\n"),
                     new Text("\n"),
                     new Text(
                        org.cropster.roastingintelligence.util.Messages.m_(
                           "Please contact us if you have problems to obtain an up-to-date version of the Roasting Intelligence software."
                        )
                     )
                  }
               )
            );
         alert.showAndWait();
         System.exit(4);
      } else if (now.compareTo(warnDate) > 0) {
         Alert alert = WindowUtil.createTextFlowAlert(
            AlertType.WARNING, org.cropster.roastingintelligence.util.Messages.m_("Version outdated") + killDateString, null, "button--warning"
         );
         alert.getDialogPane()
            .setContent(
               new TextFlow(
                  new Node[]{
                     new Text(
                        org.cropster.roastingintelligence.util.Messages.m_(
                           "This is an outdated beta version and is no longer supported!\n\nRemove the software and install the current version from the following URL:"
                        )
                     ),
                     hyperlink,
                     new Text("\n"),
                     new Text("\n"),
                     new Text(
                        org.cropster.roastingintelligence.util.Messages.m_(
                           "Please contact us if you have problems to obtain an up-to-date version of the Roasting Intelligence software."
                        )
                     ),
                     new Text("\n"),
                     new Text("\n"),
                     new Text(org.cropster.roastingintelligence.util.Messages.m_("With {0} this version will be disabled!", killDateString))
                  }
               )
            );
         alert.showAndWait();
      }
   }

   private static String getCurrentGraphicsPipeline() {
      return GraphicsPipeline.getPipeline().getClass().getName();
   }

   public static boolean isMaxVramSet(Preferences prefs) {
      return System.getProperty("prism.maxvram") != null ? true : prefs.get("prism.maxvram", null) != null;
   }

   public static void computeAndSetMaxVram() {
      if (System.getProperty("prism.maxvram") == null) {
         String instance = System.getProperty("org.cropster.roastingintelligence.instance");
         Preferences prefs = PreferencesConfiguration.createPreferences(instance);
         if (SystemUtils.IS_OS_MAC_OSX && isMaxVramSet(prefs)) {
            String maxVRam = prefs.get("prism.maxvram", null);
            if (maxVRam != null) {
               System.setProperty("prism.maxvram", maxVRam);
            }
         } else {
            int screenCount = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
            int maxVRam = 512 + 384 * screenCount;
            System.setProperty("prism.maxvram", Integer.toUnsignedString(maxVRam) + "m");
         }
      }
   }

   public static void updateMaxVram(Preferences prefs) {
      int screenCount = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length;
      int maxVRam = 512 + 384 * screenCount;
      prefs.put("prism.maxvram", Integer.toUnsignedString(maxVRam) + "m");
   }

   private static void logSystemInformation(File dataDirectory) {
      logger.info(
         "System: Java {} {} @ {} {} {}",
         new Object[]{
            System.getProperty("java.version"),
            System.getProperty("sun.arch.data.model") + "-bit",
            System.getProperty("os.name"),
            System.getProperty("os.arch"),
            System.getProperty("os.version")
         }
      );
      logger.info("java.library.path={}", System.getProperty("java.library.path"));

      try {
         logger.info("JavaFX graphics pipeline: {}", getCurrentGraphicsPipeline());
      } catch (Exception var2) {
         if (logger.isDebugEnabled()) {
            logger.debug("Error printing graphics pipeline", var2);
         } else {
            logger.warn("Error printing graphics pipeline: {}", var2.getClass().getSimpleName());
         }
      }

      logger.info("Java max memory: {}", formatMemory(Runtime.getRuntime().maxMemory()));
      logger.info("JavaFX max VRAM: {}", formatMemory(PrismSettings.maxVram));
      logger.info(
         "Data directory usable/free/total space: {} / {} / {}",
         new Object[]{formatMemory(dataDirectory.getUsableSpace()), formatMemory(dataDirectory.getFreeSpace()), formatMemory(dataDirectory.getTotalSpace())}
      );
      logger.info(
         "Using locale {} and timezone {} ({})",
         new Object[]{Locale.getDefault(), TimeZone.getDefault().getID(), new SimpleDateFormat("Z").format(new Date(NanoTime.currentTimeMillis()))}
      );
      if (!Locale.getDefault().equals(Locale.getDefault(Category.DISPLAY))) {
         logger.info("Using differing display locale {}", Locale.getDefault(Category.DISPLAY));
      }

      if (!Locale.getDefault().equals(Locale.getDefault(Category.FORMAT))) {
         logger.info("Using differing format locale {}", Locale.getDefault(Category.FORMAT));
      }

      logger.info("Using charset {}", Charset.defaultCharset());
      logger.info("Using file encoding {}", System.getProperty("file.encoding"));
   }

   private static String formatMemory(long memorySizeInBytes) {
      double unit = 1.0995116E12F;
      if ((double)memorySizeInBytes > unit) {
         return String.format(Locale.US, "%.1f TB", (double)memorySizeInBytes / unit);
      } else {
         unit = 1.0737418E9F;
         if ((double)memorySizeInBytes > unit) {
            return String.format(Locale.US, "%.1f GB", (double)memorySizeInBytes / unit);
         } else {
            unit = 1048576.0;
            if ((double)memorySizeInBytes > unit) {
               return String.format(Locale.US, "%.1f MB", (double)memorySizeInBytes / unit);
            } else {
               unit = 1024.0;
               return (double)memorySizeInBytes > unit
                  ? String.format(Locale.US, "%.1f KB", (double)memorySizeInBytes / unit)
                  : String.format(Locale.US, "%d B", memorySizeInBytes);
            }
         }
      }
   }

   private static void redirectStream(File dir, String redirectFileName, Consumer<PrintStream> streamConsumer) {
      if (!StringUtils.isEmpty(redirectFileName)) {
         try {
            File redirectFile = new File(redirectFileName);
            File redirectBakFile = new File(dir, redirectFileName + ".bak");
            if (!redirectFile.isAbsolute()) {
               redirectFile = new File(dir, redirectFileName);
            }

            if (redirectFile.exists()) {
               if (redirectBakFile.exists()) {
                  redirectBakFile.delete();
               }

               redirectFile.renameTo(redirectBakFile);
            }

            redirectFile.createNewFile();
            System.out.println(String.format("Redirecting stream to %s", redirectFile.getAbsolutePath()));
            streamConsumer.accept(new PrintStream(new FileOutputStream(redirectFile)));
         } catch (IOException var5) {
            var5.printStackTrace();
         }
      }
   }

   private void createSourceFactories() {
      Class<? extends MeasurementSourceFactory> sourceFactoryClass = null;

      for (Class<?> element : Defaults.SOURCE_FACTORIES) {
         try {
            sourceFactoryClass = (Class<? extends MeasurementSourceFactory>)element;
            MeasurementSourceFactory sourceFactory = (MeasurementSourceFactory)element.getConstructor().newInstance();
            registerService(MeasurementSourceFactory.class, sourceFactory);
            registerService(sourceFactory.getClass(), sourceFactory);
         } catch (Exception var7) {
            logger.error("Error creating measurement sources", var7);
            WindowUtil.showSetupAlert(null, sourceFactoryClass, var7);
         }
      }
   }

   private Map<String, Set<SourceParameter>> migrateSourceFactoryConfiguration(Preferences preferences) {
      Map<String, Set<SourceParameter>> sourceConfigurations = new LinkedHashMap<>();

      for (MeasurementSourceFactory sourceFactory : retrieveService(MeasurementSourceFactory.class)) {
         if (!this._appModel.getChannelsBySource((Class<? extends MeasurementSourceFactory>)sourceFactory.getClass()).isEmpty()) {
            logger.info("Migrating source factory configuration for {}", sourceFactory.getClass().getName());
            String prefix = sourceFactory.getClass().getSimpleName();
            Set<SourceParameter> parameters = sourceFactory.getParameters();

            for (SourceParameter parameter : parameters) {
               String parameterKey = String.format("%s.%s", prefix, parameter.getName());
               String parameterValue = preferences.get(parameterKey, null);
               if (StringUtils.isNotBlank(parameterValue)) {
                  parameter.setValue(parameterValue);
               }
            }

            sourceConfigurations.put(prefix, parameters);
         }
      }

      return sourceConfigurations;
   }

   private void setupSourceFactories(com.cropster.roastingintelligence.Preferences preferences) {
      OfflineBootstrap.configure(this._appModel);

      for (MeasurementSourceFactory sourceFactory : retrieveService(MeasurementSourceFactory.class)) {
         Set<Channel> channels = this._appModel.getChannelsBySource((Class<? extends MeasurementSourceFactory>)sourceFactory.getClass());
         Set<ControlState> states = this._appModel.getStatesBySource((Class<? extends MeasurementSourceFactory>)sourceFactory.getClass());
         if (!channels.isEmpty() || !states.isEmpty()) {
            boolean disable = false;

            try {
               logger.info("Setting up source factory {}", sourceFactory.getClass().getName());
               sourceFactory.setup(preferences.merge(sourceFactory));
               this._setupWarnings.addAll(sourceFactory.getSetupWarnings());
               sourceFactory.getConfigurationWarnings(channels)
                  .stream()
                  .filter(w -> w != null && !w.message().isBlank())
                  .forEach(this._configurationWarnings::add);
            } catch (LinkageError var10) {
               disable = true;
               logger.error("Error loading native library", var10);
               WindowUtil.showSetupAlert(null, var10);
            } catch (Exception var11) {
               disable = true;
               logger.error("Error setting up measurement sources", var11);
               WindowUtil.showSetupAlert(null, (Class<? extends MeasurementSourceFactory>)sourceFactory.getClass(), var11);
            }

            if (disable) {
               for (Channel channel : channels) {
                  channel.setSourceFactory(null);
               }
            }
         }
      }
   }

   private static void tearDownSourceFactories(AppModel appModel, Preferences preferences) {
      for (MeasurementSourceFactory sourceFactory : retrieveService(MeasurementSourceFactory.class)) {
         boolean used = !appModel.getChannelsBySource((Class<? extends MeasurementSourceFactory>)sourceFactory.getClass()).isEmpty();
         String prefix = sourceFactory.getClass().getSimpleName();

         for (SourceParameter parameter : sourceFactory.getParameters()) {
            String parameterName = String.format("%s.%s", prefix, parameter.getName());
            if (parameter.getValue() != null && used) {
               preferences.put(parameterName, parameter.getValue());
            } else {
               preferences.remove(parameterName);
            }
         }
      }
   }

   private void setupSegment() {
   }

   public static void main(String[] args) {
      computeAndSetMaxVram();
      if (args != null && args.length > 0 && args[0].equals("--console")) {
         RoastingIntelligenceApp application = new RoastingIntelligenceApp();
         application.run("console");
      } else {
         launch(RoastingIntelligenceApp.class, args);
      }
   }

   private void initializeApplication(FileSystemConfiguration.Directories directories, Preferences prefs) {
      File logDir = directories.logDirectory();
      logDir.mkdirs();
      redirectStream(logDir, System.getProperty("com.cropster.roastingintelligence.redirect.stderr"), System::setErr);
      redirectStream(logDir, System.getProperty("com.cropster.roastingintelligence.redirect.stdout"), System::setOut);
      String localeString = prefs.get("locale", null);
      if (localeString != null) {
         Locale.setDefault(LocaleUtils.toLocale(localeString));
      }

      aquireLock(directories.dataDirectory().getAbsolutePath());
      if (_lock == null) {
         System.err.println(org.cropster.roastingintelligence.util.Messages.m_("An instance of this program is already running. Please close that first."));
         WindowUtil.showAlert(
            AlertType.ERROR,
            null,
            org.cropster.roastingintelligence.util.Messages.m_("Error starting application"),
            org.cropster.roastingintelligence.util.Messages.m_("An instance of this program is already running. Please close that first.")
         );
         System.exit(1);
      }

      this.setupLoggingSystem(directories.dataDirectory(), logDir);
      logSystemInformation(directories.dataDirectory());
      JavaFxUtil.setIcons(this.stage);
      Runtime.getRuntime().addShutdownHook(new Thread("LockShutdownHook") {
         @Override
         public void run() {
            RoastingIntelligenceApp.releaseLock();
         }
      });
   }

   private void setupLoggingSystem(File dataDirectory, File logDirectory) {
      try {
         if (System.getProperty("log4j.configurationFile") == null) {
            File log4j2ConfFile = new File(dataDirectory, "log4j2.yaml");
            if (!log4j2ConfFile.exists()) {
               try (
                  InputStream templateStream = RoastingIntelligenceApp.class.getResourceAsStream("/log4j2.yaml");
                  FileOutputStream outputStream = new FileOutputStream(log4j2ConfFile);
               ) {
                  if (templateStream == null) {
                     throw new IOException(MessageFormat.format("Missing /{0} resource", "log4j2.yaml"));
                  }

                  IOUtils.copy(templateStream, outputStream);
               }
            }

            System.setProperty("org.cropster.roastingintelligence.logging.path", logDirectory.getAbsolutePath() + File.separator);
            System.setProperty("log4j.configurationFile", log4j2ConfFile.toURI().toString());
            LogManager.getContext(false);
         }

         logger = LoggerFactory.getLogger(RoastingIntelligenceApp.class);
         logger.info("******************** START ********************");
         logger.info("Initialized Log4j2 from {}", System.getProperty("log4j.configurationFile"));
      } catch (Exception var12) {
         logger = LoggerFactory.getLogger(RoastingIntelligenceApp.class);
         logger.info("******************** START ********************");
         logger.error("Failed to initialize Log4j2", var12);
      }

      Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
         @Override
         public void uncaughtException(Thread thread, Throwable throwable) {
            RoastingIntelligenceApp.logger.error("Uncaught exception in thread {}", thread.getName(), throwable);
         }
      });
   }

   public Stage getStage() {
      return this.stage;
   }

   public Scene switchScene(String title, Parent root) {
      Scene scene = new Scene(root);
      scene.getStylesheets().add(JavaFxController.STYLE_SHEET);
      String compositeTitle = org.cropster.roastingintelligence.util.Messages.m_("Cropster Roasting Intelligence");
      if (title != null) {
         compositeTitle = compositeTitle + " - " + title;
      }

      this.stage.setTitle(compositeTitle);
      this.stage.setScene(scene);
      return scene;
   }

   public void start(Stage stage) throws Exception {
      this.stage = stage;
      hostServices = this.getHostServices();
      this.run("javafx");
   }

   public void run(String controllerType) {
      for (String element : JNLP_PROPERTIES) {
         String propertyName = "org.cropster.roastingintelligence." + element;
         String propertyValue = System.getProperty("jnlp." + propertyName);
         if (propertyValue != null) {
            System.setProperty(propertyName, propertyValue);
         }
      }

      String instance = System.getProperty("org.cropster.roastingintelligence.instance");
      final Preferences prefs = new PreferencesConfiguration().preferences(instance);
      FileSystemConfiguration.Directories directories = new FileSystemConfiguration().directories(instance, prefs);
      this.initializeApplication(directories, prefs);

      try {
         Properties applicationProperties = new Properties();

         try {
            applicationProperties.load(RoastingIntelligenceApp.class.getResourceAsStream("/application.properties"));

            for (String propertyName : applicationProperties.stringPropertyNames()) {
               String propertyValue = applicationProperties.getProperty(propertyName);
               if (propertyValue != null) {
                  if (propertyValue.startsWith("@")) {
                     logger.warn("Property not filtered during build: {}", propertyName);
                  } else {
                     System.setProperty(propertyName, propertyValue);
                  }
               }
            }
         } catch (IOException var20) {
            throw new RuntimeException("Error loading version from /application.properties", var20);
         }

         String versionProperty = System.getProperty("org.cropster.roastingintelligence.version");
         if (versionProperty == null) {
            versionProperty = "99.0";
            System.setProperty("org.cropster.roastingintelligence.version", versionProperty);
         }

         Version version = new Version(versionProperty);
         logger.info("Running Roasting Intelligence {}", version);
         ConfigUpdater configUpdater = new ConfigUpdater(version, prefs, directories.dataDirectory());
         configUpdater.run();
         this._appModel = new AppModel(version);
         this._appModel.setDirectories(directories);
         final PreferencesStore preferencesStore = new PreferencesStore(prefs, this._appModel);
         preferencesStore.load();
         boolean autoUpdateSuspended = false;
         String installerNodeName = "com.cropster.roastingintelligence.install4j.ri5";
         Preferences preferencesRoot = Preferences.userRoot();
         if (preferencesRoot.nodeExists(installerNodeName)) {
            Preferences installerPreferences = preferencesRoot.node(installerNodeName);
            String autoUpdateSuspendedKey = "autoUpdateSuspended";
            if (version.getClassifier() != null) {
               autoUpdateSuspendedKey = version.getClassifier() + "." + autoUpdateSuspendedKey;
            }

            autoUpdateSuspended = installerPreferences.getBoolean(autoUpdateSuspendedKey, false);
            if (autoUpdateSuspended) {
               logger.warn("Install4j auto-update is disabled");
            }
         }

         this.createSourceFactories();
         final StorageService storageService = new StorageServiceImpl(this._appModel.getVersion(), directories.dataDirectory(), directories.outboxDirectory());
         registerService(StorageService.class, storageService);
         if (storageService.isPreferencesAvailable()) {
            this.loadPreferences(storageService);
         } else {
            this._appModel.getPreferences().setSourceConfigurations(this.migrateSourceFactoryConfiguration(prefs));
         }

         Measures.initDefaultMeasureUnits(this._appModel.getPreferences());
         this.loadMachinePreferences(storageService);
         this.setupSourceFactories(this._appModel.getPreferences());
         this._appModel.setDemoMode(!this._appModel.getChannelsBySource(DemoSourceFactory.class).isEmpty());
         CsarConnection connection = null;
         if (this._appModel.isReadOnlyConnection()) {
            connection = new ReadOnlyConnection(version, this._appModel.getUri());
         } else {
            connection = new OfflineConnection(version, this._appModel.getUri());
         }

         Data cachedData = storageService.loadDataCache();
         if (cachedData != null) {
            this._appModel.setData(cachedData);
         }

         this._roastingManager = new RoastingManager();
         registerService(RoastingManager.class, this._roastingManager);
         RoastPersister roastPersister = null;
         FilePersister var36;
         if (this._appModel.isReadOnlyConnection()) {
            File demoDir = new File(FileUtils.getTempDirectory(), "roastingintelligence-demo");
            File tempOutbox = new File(demoDir, "outbox");
            tempOutbox.mkdirs();
            File tempSent = new File(demoDir, "sent");
            tempSent.mkdirs();
            var36 = new FilePersister(version, tempOutbox, tempSent);
         } else {
            var36 = new FilePersister(version, directories.outboxDirectory(), directories.sentDirectory());
         }

         this._roastingManager.setRoastPersister(var36);
         this._roastingManager.setConnection(connection);
         this._appModel.setRoastPersister(var36);
         this.setupSegment();
         NotificationBusService notificationBusService = ServiceLoader.load(NotificationBusService.class)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Notification bus service not found"));
         registerService(NotificationBusService.class, notificationBusService);
         this.notifyCurrentVersion(notificationBusService, versionProperty, autoUpdateSuspended);
         publishConfigurationWarnings(notificationBusService, this._configurationWarnings);
         this._configurationWarnings.clear();
         if ("console".equals(controllerType)) {
            this._controller = new ConsoleController(this._appModel, connection);
         } else {
            JavaFxController javaFxController = new JavaFxController(this, preferencesStore, this._appModel, this._roastingManager, connection);
            this._controller = javaFxController;
         }

         this._roastingManager.listeners().add(this._controller);
         if (SystemUtils.IS_OS_MAC) {
            if (Desktop.isDesktopSupported()) {
               Desktop desktop = Desktop.getDesktop();
               if (desktop.isSupported(Action.APP_QUIT_HANDLER)) {
                  desktop.setQuitHandler((e, response) -> {
                     response.cancelQuit();
                     this._controller.close();
                  });
               } else {
                  logger.warn("Java desktop quit handler not supported.");
               }
            } else {
               logger.warn("Java desktop integration not supported.");
            }
         }

         this._controller.setShutdownHook(new Runnable() {
            @Override
            public void run() {
               RoastingIntelligenceApp.logger.info("Executing shutdown hook");

               try {
                  RoastingIntelligenceApp.tearDownSourceFactories(RoastingIntelligenceApp.this._appModel, prefs);
                  RoastingIntelligenceApp.logger.info("Saving legacy preferences");
                  preferencesStore.save();
               } catch (Throwable var4) {
                  RoastingIntelligenceApp.logger.error("Failed saving preferences", var4);
               }

               storageService.saveDataCache(RoastingIntelligenceApp.this._appModel.getData());
               if (SystemUtils.IS_OS_MAC_OSX) {
                  RoastingIntelligenceApp.updateMaxVram(prefs);
               }

               if (RoastingIntelligenceApp.this._appModel.isRestartRequired()) {
                  try {
                     ServiceLoader<SystemSupport> serviceLoader = ServiceLoader.load(SystemSupport.class);
                     SystemSupport systemSupport = serviceLoader.findFirst().orElse(null);
                     if (systemSupport != null) {
                        systemSupport.restart();
                     } else {
                        RoastingIntelligenceApp.logger.warn("Skipping restart - no provider for SystemSupport found");
                     }
                  } catch (Throwable var3) {
                     RoastingIntelligenceApp.logger.error("Error restarting application", var3);
                  }
               }

               System.exit(0);
            }
         });
         this._controller.start(this._setupWarnings);
      } catch (Throwable var21) {
         logger.error("Error starting application", var21);
         String errorMessage = org.cropster.roastingintelligence.util.Messages.m_("Error starting application") + ":";

         for (Throwable cause = var21; cause != null; cause = cause.getCause()) {
            errorMessage = errorMessage + "\n" + cause.getClass().getSimpleName();
            if (cause.getMessage() != null) {
               errorMessage = errorMessage + " - " + cause.getMessage();
            }
         }

         WindowUtil.showAlert(AlertType.ERROR, null, org.cropster.roastingintelligence.util.Messages.m_("Error starting application"), errorMessage);
         this.exit(2);
      }
   }

   public static void publishConfigurationWarnings(NotificationBusService notificationBusService, List<MeasurementSourceFactory.ConfigurationWarning> warnings) {
      for (MeasurementSourceFactory.ConfigurationWarning warning : warnings) {
         notificationBusService.publish(
            Notification.builder(NotificationType.WARNING).withTitle(warning.title()).withMessage(warning.message()).build(), "system-connector-configuration"
         );
      }
   }

   private void notifyCurrentVersion(NotificationBusService notificationBusService, String versionProperty, boolean autoUpdateSuspended) {
      ServiceLoader<SystemSupport> serviceLoader = ServiceLoader.load(SystemSupport.class);
      SystemSupport systemSupport = serviceLoader.findFirst().orElse(null);
      if (systemSupport == null) {
         notificationBusService.publish(
            Notification.builder(NotificationType.SUCCESS)
               .withTitle(org.cropster.roastingintelligence.util.Messages.m_("Latest version installed"))
               .withProperty("version-current", versionProperty)
               .withProperty("auto-update-suspended", autoUpdateSuspended)
               .build(),
            "system-update"
         );
      } else {
         AtomicReference<Runnable> actionEnablerCallback = new AtomicReference<>();
         Runnable callback = () -> PlatformScheduler.executeBackgroundTask(() -> {
               this.checkForUpdate();
               Platform.runLater(() -> actionEnablerCallback.get().run());
            });
         org.controlsfx.control.action.Action action = new ActionBuilder(org.cropster.roastingintelligence.util.Messages.m_("Check for updates"), callback)
            .disableOnCallback(true)
            .graphics(Icon.SYNC.load())
            .build();
         actionEnablerCallback.set(() -> action.setDisabled(false));
         notificationBusService.publish(
            Notification.builder(NotificationType.SUCCESS)
               .withTitle(org.cropster.roastingintelligence.util.Messages.m_("Latest version installed"))
               .withProperty("version-current", versionProperty)
               .withProperty("auto-update-suspended", autoUpdateSuspended)
               .withProperty("action", action)
               .build(),
            "system-update"
         );
      }
   }

   public synchronized void checkForUpdate() {
   }

   private void logAndNotify(NotificationBusService notificationBusService, String message) {
      this.logAndNotify(notificationBusService, message, null);
   }

   private void logAndNotify(NotificationBusService notificationBusService, String message, Throwable ex) {
      Notification.NotificationBuilder builder = Notification.builder(NotificationType.SUCCESS).withProperty("version-update-not-found", true);
      if (ex != null) {
         builder.withProperty("exception", ex);
         builder.withMessage(message);
         logger.error(message, ex);
      } else {
         message = "Skipping check for update: " + message;
         builder.withMessage(message);
         logger.warn(message);
      }

      notificationBusService.publish(builder.build(), "system-update");
   }

   private void loadMachinePreferences(StorageService storageManager) {
      try {
         com.cropster.roastingintelligence.Preferences jsonPreferences = storageManager.loadMachinePreferences();
         if (jsonPreferences != null) {
            this._appModel.getPreferences().setSourceConfigurations(jsonPreferences.getSourceConfigurations());
            jsonPreferences.applyChannelChecks();
            this._appModel.getPreferences().setChannels(jsonPreferences.getChannels());
            this._appModel.getPreferences().setStates(jsonPreferences.getStates());
         }
      } catch (IOException var3) {
         logger.error("Empty or unreadable machine preferences file", var3);
         WindowUtil.showAlert(
            AlertType.ERROR,
            null,
            org.cropster.roastingintelligence.util.Messages.m_("Error loading preferences"),
            org.cropster.roastingintelligence.util.Messages.m_(
               "The machine configuration file could not be read.\nPlease select your machine to re-load the preferences."
            )
         );
      }
   }

   private void loadPreferences(StorageService storageManager) {
      try {
         com.cropster.roastingintelligence.Preferences jsonPreferences = storageManager.loadPreferences();
         if (jsonPreferences != null) {
            this._appModel.setPreferences(jsonPreferences);
         }

         if (this._appModel.getPreferences().getLocale() != null) {
            Locale.setDefault(this._appModel.getPreferences().getLocale());
         }
      } catch (IOException var3) {
         logger.error("Empty or unreadable preferences file", var3);
         WindowUtil.showAlert(
            AlertType.ERROR,
            null,
            org.cropster.roastingintelligence.util.Messages.m_("Error loading preferences"),
            org.cropster.roastingintelligence.util.Messages.m_("The preferences file could not be read.\nThe default preferences have been restored.")
         );
      }
   }

   private void exit(int exitCode) {
      if (this._controller != null) {
         try {
            this._controller.close();
         } catch (Throwable var3) {
            logger.error("Error closing controller", var3);
         }
      }

      System.exit(exitCode);
   }
}
