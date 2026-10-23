package org.cropster.roastingintelligence;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.config.FileSystemConfiguration;
import com.cropster.roastingintelligence.model.ControlStateConfiguration;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.listener.CsarDataListener;
import org.cropster.roastingintelligence.listener.SettingsListener;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;
import org.cropster.roastingintelligence.model.WorkerTask;
import org.cropster.roastingintelligence.remote.Version;
import org.cropster.roastingintelligence.service.RoastPersister;
import org.cropster.roastingintelligence.util.CollectionUtil;
import org.cropster.roastingintelligence.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppModel {
   public static final String PREFERENCES_FILE = "preferences.json";
   public static final String MACHINE_FILE = "machine.json";
   public static final String DATA_FILE = "cache.json";
   private final Logger logger = LoggerFactory.getLogger(AppModel.class);
   private final Version _version;
   private URI _uri;
   private boolean _demoMode;
   private String _user;
   private String _userName;
   private String _refreshToken;
   private boolean _rememberMe;
   private String _clientId;
   private long _score = 0L;
   private long _count = 0L;
   private Preferences _preferences = new Preferences();
   private volatile Data _data = new Data();
   private FileSystemConfiguration.Directories _directories;
   private volatile String _selectedRoastingPerson;
   private volatile Long _selectedProfileId;
   private volatile Long _currentScheduledId;
   private volatile List<Roast> _recentRoasts;
   private Map<Long, Scheduled> _scheduledRoasts;
   private volatile Collection<WorkerTask> _tasks;
   private volatile BetweenBatchModel _betweenBatchModel;
   private volatile UnsavedBetweenBatchLog _betweenBatchLog;
   private final EnumSet<AppModel.Hint> _hints = EnumSet.noneOf(AppModel.Hint.class);
   private volatile long _syncTime = -1L;
   private volatile Date _loginThresholdDate;
   private volatile boolean _restartRequired;
   private RoastPersister _roastPersister;
   private AppModel.AutoStartMode _autoStartMode = AppModel.AutoStartMode.NONE;
   private Class<? extends MeasurementSourceFactory> _autoStartSourceFactory = null;
   private List<MeasurementSourceFactory> unconnectedConnectors = new ArrayList<>();
   private final CopyOnWriteArraySet<CsarDataListener> _csarDataListeners = new CopyOnWriteArraySet<>();
   private final CopyOnWriteArraySet<SettingsListener> _settingsListeners = new CopyOnWriteArraySet<>();

   public AppModel(Version version) throws URISyntaxException {
      this._version = version;
      this._uri = new URI("https://c-sar.cropster.com");
      this._recentRoasts = new CopyOnWriteArrayList<>();
      this._scheduledRoasts = Collections.emptyMap();
      this._tasks = new CopyOnWriteArrayList<>();
   }

   public Version getVersion() {
      return this._version;
   }

   public void activate() {
      try {
         this.setRecentRoasts(this._roastPersister.loadRecentRoasts(this._preferences.getMaxRoastCount()));
      } catch (Exception var2) {
         this.logger.error("Error loading recent roasts", var2);
      }
   }

   public boolean isDemoMode() {
      return this._demoMode;
   }

   public void setDemoMode(boolean demoMode) {
      this._demoMode = demoMode;
   }

   public boolean isReadOnlyConnection() {
      return this._demoMode && this._uri.getHost().equalsIgnoreCase("c-sar.cropster.com");
   }

   public long getSyncTime() {
      return this._syncTime;
   }

   public void setSyncTime(long syncTime) {
      this._syncTime = syncTime;
   }

   public Date getLoginThresholdDate() {
      return this._loginThresholdDate;
   }

   public void setLoginThresholdDate(Date loginThresholdDate) {
      this._loginThresholdDate = loginThresholdDate;
   }

   public boolean isRestartRequired() {
      return this._restartRequired;
   }

   public void setRestartRequired(boolean restartRequired) {
      this._restartRequired = restartRequired;
   }

   public RoastPersister getRoastPersister() {
      return this._roastPersister;
   }

   public void setRoastPersister(RoastPersister roastPersister) {
      this._roastPersister = roastPersister;
   }

   public void setCurrentScheduledId(Long id) {
      this.logger.info("Selecting scheduled {}", id);
      this._currentScheduledId = id;
      Scheduled scheduled = this.getCurrentScheduled();
      if (scheduled != null && scheduled.getWeight() != null) {
         this._preferences.setWeightUnit(scheduled.getWeight().getUnit());
      }
   }

   public String getRoastInfo(Roast roast) {
      Profile profile = null;
      if (roast.getProfile() != null) {
         profile = this._data.getProfiles().get(roast.getProfile());
      }

      return String.format(
            "%s%d Lot(s) | %,.2f %s",
            profile != null ? profile + " | " : "",
            roast.getLot().getSources().size(),
            roast.getStartWeight().getAmount(),
            roast.getStartWeight().getUnit()
         )
         .trim();
   }

   public synchronized Roast getCurrentReference() {
      return this._selectedProfileId != null ? this._data.getReferences().get(this._selectedProfileId) : null;
   }

   public Map<Long, Scheduled> getScheduledRoasts() {
      return this._scheduledRoasts;
   }

   public synchronized void setScheduledRoasts(Collection<Scheduled> scheduledRoasts) {
      Map<Long, Scheduled> scheduledMap = CollectionUtil.toMap(scheduledRoasts);

      for (Roast roast : this._recentRoasts) {
         if (roast.getScheduledId() != null) {
            scheduledMap.remove(roast.getScheduledId());
         }
      }

      this._scheduledRoasts = Collections.unmodifiableMap(scheduledMap);
   }

   public List<Roast> getRecentRoasts() {
      return this._recentRoasts;
   }

   public void setRecentRoasts(List<Roast> roasts) {
      this._recentRoasts.clear();
      this._recentRoasts.addAll(roasts);
   }

   public void addRecentRoast(Roast currentRoast) {
      this._recentRoasts.add(0, currentRoast);
      if (this._recentRoasts.size() > 100) {
         this._recentRoasts.remove(this._recentRoasts.size() - 1);
      }
   }

   public Collection<WorkerTask> getTasks() {
      return this._tasks;
   }

   public void setTasks(Collection<WorkerTask> tasks) {
      this._tasks = new CopyOnWriteArrayList<>(tasks);
   }

   public BetweenBatchModel getBetweenBatchModel() {
      return this._betweenBatchModel;
   }

   public void setBetweenBatchModel(BetweenBatchModel betweenBatchModel) {
      this._betweenBatchModel = betweenBatchModel;
   }

   public UnsavedBetweenBatchLog getBetweenBatchLog() {
      return this._betweenBatchLog;
   }

   public void setBetweenBatchLog(UnsavedBetweenBatchLog betweenBatchLog) {
      this._betweenBatchLog = betweenBatchLog;
   }

   public EnumSet<AppModel.Hint> getHints() {
      return this._hints;
   }

   public Preferences getPreferences() {
      return this._preferences;
   }

   public Data getData() {
      return this._data;
   }

   public void setData(Data data) {
      if (data == null) {
         throw new IllegalArgumentException("Parameter data must not be null");
      } else {
         this._data = data;
      }
   }

   public void setPreferences(Preferences preferences) {
      if (preferences == null) {
         throw new IllegalArgumentException("Parameter preferences must not be null");
      } else {
         this._preferences = preferences;
      }
   }

   private boolean isCommentEnabled(String[] enabledEvents, RoastingComment comment) {
      return comment.getEvent() == null
         ? Arrays.binarySearch(enabledEvents, "TEXT_COMMENT") >= 0
         : "turningPoint".equals(comment.getEvent())
            || "end".equals(comment.getEvent())
            || comment.getEvent().matches(".+[cC]ontrol")
            || Arrays.binarySearch(enabledEvents, comment.getEvent()) >= 0;
   }

   public boolean isEventEnabledInPopup(RoastingComment comment) {
      return this.isCommentEnabled(this._preferences.getEnabledEventsPopup(), comment);
   }

   public boolean isCommentEnabledInChart(RoastingComment comment) {
      return this.isCommentEnabled(this._preferences.getEnabledEventsChart(), comment);
   }

   public boolean isCommentEnabledInSidebar(RoastingComment comment) {
      return this.isCommentEnabled(this._preferences.getEnabledEventsSidebar(), comment);
   }

   public double getStartThreshold(Unit unit) {
      Unit tempUnit = this._preferences.getPreferredUnit(Unit.CELSIUS);
      BigDecimal startThreshold = BigDecimal.valueOf(this._preferences.getStartThreshold());
      startThreshold = unit.fromRelative(startThreshold, tempUnit);
      return startThreshold.doubleValue();
   }

   public double getTurningPointThreshold(Unit unit) {
      return Unit.FAHRENHEIT.equals(unit)
         ? Unit.FAHRENHEIT.fromRelative(Defaults.TEMPERATURE_RISE_TURNINGPOINT, Unit.CELSIUS).doubleValue()
         : Defaults.TEMPERATURE_RISE_TURNINGPOINT.doubleValue();
   }

   public double getEndThreshold(Unit unit) {
      Unit tempUnit = this._preferences.getPreferredUnit(Unit.CELSIUS);
      BigDecimal endThreshold = BigDecimal.valueOf(this._preferences.getEndThreshold());
      endThreshold = unit.fromRelative(endThreshold, tempUnit);
      return endThreshold.doubleValue();
   }

   @Deprecated(
      forRemoval = true
   )
   public FileSystemConfiguration.Directories getDirectories() {
      return this._directories;
   }

   public void setDirectories(FileSystemConfiguration.Directories directories) {
      this._directories = directories;
   }

   public URI getUri() {
      return this._uri;
   }

   public void setUri(URI uri) {
      this._uri = uri;
   }

   public String getUser() {
      return this._user;
   }

   public void setUser(String user) {
      this._user = user;
   }

   public String getUserName() {
      return this._userName;
   }

   public void setUserName(String userName) {
      this._userName = userName;
   }

   public String getRefreshToken() {
      return this._refreshToken;
   }

   public void setRefreshToken(String refreshToken) {
      this._refreshToken = refreshToken;
   }

   public boolean isTokenSet() {
      return !StringUtil.isBlank(this._refreshToken);
   }

   public boolean isRememberMe() {
      return this._rememberMe;
   }

   public void setRememberMe(boolean rememberMe) {
      this._rememberMe = rememberMe;
   }

   public String getUserInfo() {
      String userInfo = this._selectedRoastingPerson;
      if (userInfo == null) {
         userInfo = this._user;
      }

      if (userInfo == null) {
         userInfo = this._userName;
      }

      return userInfo;
   }

   public String getClientId() {
      return this._clientId;
   }

   public void setClientId(String clientId) {
      this._clientId = clientId;
   }

   public String getUserId() {
      return this._userName != null ? Base64.encodeBase64String(DigestUtils.sha3_224(this._userName)) : null;
   }

   public long getScore() {
      return this._score;
   }

   public void setScore(long score) {
      this._score = score;
   }

   public long getCount() {
      return this._count;
   }

   public void setCount(long count) {
      this._count = count;
   }

   public void persistRoast(Roast roast) throws IOException {
      if (!roast.isSynced()) {
         throw new IllegalArgumentException("Roast not synced");
      } else {
         Iterator<Measure> it = roast.getMeasures().values().iterator();

         while (it.hasNext()) {
            if (it.next() == null) {
               it.remove();
            }
         }

         this._roastPersister.save(roast);
      }
   }

   public Long getCurrentScheduleId() {
      return this._currentScheduledId;
   }

   public Scheduled getCurrentScheduled() {
      return this._currentScheduledId != null ? this._scheduledRoasts.get(this._currentScheduledId) : null;
   }

   public Collection<Channel> getActiveChannels() {
      return this._preferences.getChannels().stream().filter(c -> c.isActive()).collect(Collectors.toList());
   }

   public Collection<Channel> getVisibleChannels(Channel.Visibility visibility) {
      return this._preferences.getChannels().stream().filter(c -> c.isActive() && c.isVisible(visibility)).collect(Collectors.toList());
   }

   public Channel getChannel(String channelName) {
      return this._preferences.getChannels().stream().filter(c -> c.getName().equals(channelName)).findFirst().orElse(null);
   }

   public Set<Channel> getChannelsBySource(Class<? extends MeasurementSourceFactory> clazz) {
      return this._preferences.getChannels().stream().filter(c -> clazz.equals(c.getSourceFactory())).collect(Collectors.toSet());
   }

   public boolean isChannelActive(String channelName) {
      Channel channel = this.getChannel(channelName);
      return channel != null && channel.isActive();
   }

   public boolean isControlActive() {
      return this.isChannelActive("gasControl")
         || this.isChannelActive("airflowControl")
         || this.isChannelActive("drumPressureControl")
         || this.isChannelActive("fanSpeedControl");
   }

   public void mergeChannelOrder(String... channelOrder) {
      List<String> channelOrderList = Arrays.asList(channelOrder);
      Queue<String> missingChannels = new LinkedList<>(CollectionUtils.subtract(Arrays.asList(this._preferences.getChannelOrder()), channelOrderList));
      List<String> currentOrder = Arrays.asList(this._preferences.getChannelOrder());
      LinkedList<String> mergedOrder = new LinkedList<>(new LinkedHashSet<>(channelOrderList));

      while (!missingChannels.isEmpty()) {
         boolean merged = false;
         String channel = missingChannels.poll();
         int index = currentOrder.indexOf(channel);

         for (int i = index; i >= 0; i--) {
            int predecessorIndex = mergedOrder.indexOf(currentOrder.get(i));
            if (predecessorIndex >= 0) {
               mergedOrder.add(predecessorIndex + 1, channel);
               merged = true;
               break;
            }
         }

         if (!merged) {
            mergedOrder.add(channel);
         }
      }

      this._preferences.setChannelOrder(mergedOrder.toArray(new String[mergedOrder.size()]));
   }

   public Collection<ControlState> getEnabledStates() {
      return this._preferences.getStates().stream().map(ControlStateConfiguration::getState).toList();
   }

   public Set<ControlState> getStatesBySource(Class<? extends MeasurementSourceFactory> clazz) {
      return this._preferences
         .getStates()
         .stream()
         .map(ControlStateConfiguration::getState)
         .filter(c -> clazz.equals(c.getSourceFactory()))
         .collect(Collectors.toSet());
   }

   public synchronized Machine getSelectedMachine() {
      return this._preferences.getSelectedMachineId() != null ? this._data.getMachines().get(this._preferences.getSelectedMachineId()) : null;
   }

   public void setSelectedMachine(Machine machine) {
      this._preferences.setSelectedMachineId(machine != null ? machine.getId() : null);
   }

   public Double getMaxStartWeight() {
      Machine machine = this.getSelectedMachine();
      return machine != null && machine.getCapacity() != null ? machine.getCapacity().toUnit(this._preferences.getWeightUnit()).getAmount() : null;
   }

   public String[] getAvailableChannels() {
      return this.getSelectedMachine() != null && this.getSelectedMachine().getChannels() != null ? this.getSelectedMachine().getChannels() : new String[0];
   }

   public String getWorker() {
      if (StringUtils.isNotBlank(this._selectedRoastingPerson)) {
         return this._selectedRoastingPerson.trim();
      } else {
         return StringUtils.isNotBlank(this._user) ? this._user.trim() : null;
      }
   }

   public String getSelectedRoastingPerson() {
      return this._selectedRoastingPerson;
   }

   public void setSelectedRoastingPerson(String selectedRoastingPerson) {
      this._selectedRoastingPerson = selectedRoastingPerson;
   }

   public Long getSelectedProfileId() {
      return this._selectedProfileId;
   }

   public void setSelectedProfileId(Long selectedProfileId) {
      this._selectedProfileId = selectedProfileId;
   }

   public Profile getSelectedProfile() {
      return this._data.getProfiles().get(this._selectedProfileId);
   }

   public void setSelectedProfile(Profile profile) {
      if (profile != null && !profile.getId().equals(this._selectedProfileId)) {
         this._selectedProfileId = profile.getId();
      } else if (profile == null) {
         this._selectedProfileId = null;
      }
   }

   public AppModel.AutoStartMode getAutoStartMode() {
      return this._autoStartMode;
   }

   public void setAutoStartMode(AppModel.AutoStartMode autoStartMode) {
      this._autoStartMode = autoStartMode;
   }

   public Class<? extends MeasurementSourceFactory> getAutoStartSourceFactory() {
      return this._autoStartSourceFactory;
   }

   public void addCsarDataListener(CsarDataListener listener) {
      this._csarDataListeners.add(listener);
   }

   public void removeCsarDataListener(CsarDataListener listener) {
      this._csarDataListeners.remove(listener);
   }

   public void fireCsarDataReceived() {
      this._csarDataListeners.forEach(l -> l.dataReceived(this._data));
   }

   public void fireCsarTasksReceived() {
      this._csarDataListeners.forEach(l -> l.tasksReceived(this._tasks));
   }

   public void fireRoastsUploaded(int numRoastsUploaded, Throwable exception) {
      this._csarDataListeners.forEach(l -> l.roastsUploaded(numRoastsUploaded, exception));
   }

   public void addSettingsListener(SettingsListener settingsListener) {
      this._settingsListeners.add(settingsListener);
   }

   public void removeSettingsListener(SettingsListener settingsListener) {
      this._settingsListeners.remove(settingsListener);
   }

   public void fireSettingsChanged() {
      for (SettingsListener settingsListener : this._settingsListeners) {
         settingsListener.settingsChanged(this);
      }
   }

   public void updateAutoStartMode() {
      Class<? extends MeasurementSourceFactory> autoStartSourceFactory = this.findAutoStartSourceFactory();
      AppModel.AutoStartMode mode = AppModel.AutoStartMode.NONE;
      if (autoStartSourceFactory != null && this.getPreferences().isAutoStartEnabled()) {
         mode = AppModel.AutoStartMode.MACHINE_EVENT;
      } else if (this.getPreferences().isAutoDetectionEnabled()) {
         mode = AppModel.AutoStartMode.AUTO_DETECTION;
      }

      this._autoStartMode = mode;
      this._autoStartSourceFactory = autoStartSourceFactory;
   }

   private Class<? extends MeasurementSourceFactory> findAutoStartSourceFactory() {
      List<Class<? extends MeasurementSourceFactory>> autoStartSourceFactories = new ArrayList<>();

      for (Channel channel : this.getActiveChannels()) {
         MeasurementSourceFactory sourceFactory = RoastingIntelligenceApp.retrieveSingleton(channel.getSourceFactory());
         Set<SourceParameter> parameters = sourceFactory.getParameters();
         if (parameters != null && sourceFactory.supportsAutoStart(parameters)) {
            autoStartSourceFactories.add(channel.getSourceFactory());
         }
      }

      return autoStartSourceFactories.size() > 0 ? autoStartSourceFactories.get(0) : null;
   }

   public boolean isReplayAvailable(Profile profile) {
      if (profile == null) {
         return false;
      } else {
         Roast reference = this.getData().getReferences().get(profile.getId());
         return reference == null
            ? false
            : reference.getCurves().containsKey("gasControl") && this.isChannelActive("gasControl")
               || reference.getCurves().containsKey("setTemperatureControl") && this.isChannelActive("setTemperatureControl")
               || reference.getCurves().containsKey("customControl") && this.isChannelActive("customControl");
      }
   }

   public List<MeasurementSourceFactory> getUnconnectedConnectors() {
      return this.unconnectedConnectors;
   }

   public static enum AutoStartMode {
      AUTO_DETECTION,
      MACHINE_EVENT,
      NONE;
   }

   public static enum Hint {
      COMMENTS,
      @Deprecated
      BETWEEN_BATCH_RESET;
   }

   public static enum TimerType {
      NONE,
      DEVELOPMENT,
      MODULATION;
   }
}
