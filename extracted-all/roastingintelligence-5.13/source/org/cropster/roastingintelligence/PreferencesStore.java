package org.cropster.roastingintelligence;

import com.cropster.util.model.Unit;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.model.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PreferencesStore {
   private final Logger logger = LoggerFactory.getLogger(PreferencesStore.class);
   public static final String PREF_SELECTED_PROFILE = "selectedProfileId";
   static final String PREF_BASE_URL = "baseUrl";
   static final String PREF_SYNC_TIME = "syncTime";
   static final String PREF_LOGIN_THRESHOLD = "lthr";
   static final String PREF_ROASTING_PERSON = "selectedRoastingPerson";
   static final String PREF_MACHINE_ID = "roastingMachineId";
   static final String PREF_USER = "user";
   static final String PREF_USER_NAME = "userName";
   static final String PREF_REFRESH_TOKEN = "refreshToken";
   static final String PREF_CLIENT_ID = "clientId";
   static final String PREF_SCORE = "score";
   static final String PREF_COUNT = "count";
   static final String PREF_WEIGHT_UNIT = "weightUnit";
   static final String PREF_ROASTVALUE_UNIT = "roastValueUnit";
   static final String PREF_ROASTVALUE2_UNIT = "roastValue2Unit";
   static final String PREF_TEMP_UNIT = "temperatureUnit";
   static final String PREF_PRESSURE_UNIT = "pressureUnit";
   static final String PREF_REPLAY_ENABLED = "replayEnabled";
   static final String PREF_THRESHOLD_RESET_GAS = "resetGasThreshold";
   static final String PREF_THRESHOLD_START = "startDetectionThreshold";
   static final String PREF_THRESHOLD_END = "endDetectionThreshold";
   static final String PREF_MIN_TEMP = "minTemp";
   static final String PREF_MAX_TEMP = "maxTemp";
   static final String PREF_MIN_RISE_RATE = "minRiseRate";
   static final String PREF_MAX_RISE_RATE = "maxRiseRate";
   static final String PREF_TIME = "time";
   static final String PREF_REFERENCE_COMMENT_HISTORY_HIDDEN = "referenceCommentHistoryHidden";
   static final String PREF_GAS_CURVE_AS_COMMENTS_THRESHOLD = "gasCurveAsCommentsThreshold";
   static final String PREF_EVENT_AUTO_MARKING = "eventAutoMarking";
   static final String PREF_COMMENTING_BY_TEMP = "commentingByTemp";
   static final String PREF_SOUND_ALARM_ON_EVENTS = "soundAlarmOnEvents";
   static final String PREF_ALARM_SOUND_REPETITIONS = "alarmSoundRepetitions";
   static final String PREF_ALARM_SOUND_VOLUME = "alarmSoundVolume";
   static final String PREF_ALARM_TOAST_NOTIFICATION = "alarmToastNotification";
   static final String PREF_STOP_ROASTING_CONFIRMATION = "stopRoastingConfirmationEnabled";
   static final String PREF_CURVE_PREDICITON = "curvePredictionEnabled";
   static final String PREF_FIRSTCRACK_PREDICTION = "firstCrackPredictionEnabled";
   static final String PREF_FLICK_PREDICTION = "flickPredictionEnabled";
   static final String PREF_BETWEEN_BATCH_PROTOCOL = "betweenBatchProtocol";
   static final String PREF_DEVELOPMENT_TIME = "developmentTimeEnabled";
   static final String PREF_TIMER_TYPE = "timerType";
   static final String PREF_AUTO_DETECTION = "autoDetectionEnabled";
   static final String PREF_AUTO_START = "autoStartEnabled";
   static final String PREF_MAX_ROAST_COUNT = "maxRoastCount";
   static final String PREF_TIME_LAPSE = "timeLapse";
   static final String PREF_HINTS = "hints";
   static final String PREF_PREFILLS = "preFills";
   static final String PREF_ENABLED_EVENTS = "enabledEvents";
   static final String PREF_ENABLED_EVENTS_SIDEBAR = "enabledEventsSidebar";
   static final String PREF_ENABLED_EVENTS_CHART = "enabledEventsChart";
   static final String PREF_CHANNELS = "channels";
   static final String PREF_ROR_SMOOTHING = "rorSmoothing";
   static final String PREF_ROR = "ror";
   public static final String PREF_PRINTERJOB_PRINTER = "PrinterJob.printer";
   public static final String PREF_PRINTERJOB_ORIENTATION = "PrinterJob.orientation";
   public static final String PREF_PAGEFORMAT_WIDTH = "PrintDialog.pageFormat.width";
   public static final String PREF_PAGEFORMAT_HEIGHT = "PrintDialog.pageFormat.height";
   private static final String CHANNEL_PATTERN = "Channel.%s.";
   private static final String SUFFIX_FACTORY = "sourceFactory";
   private static final String SUFFIX_INDEX = "index";
   private static final String SUFFIX_TYPE = "type";
   private static final String SUFFIX_COLOR = "color";
   private static final String SUFFIX_VISIBILITY = "visibility";
   private static final String SUFFIX_OFFSET = "offset";
   private static final String SUFFIX_ROR = "ror";
   private static final String ENCRYPTION_ALGORITHM = "DES";
   private DESKeySpec _keySpec;
   private final Preferences _preferences;
   private final ObjectMapper _objectMapper = new ObjectMapper();
   private final AppModel _appModel;

   public PreferencesStore(Preferences preferences, AppModel appModel) {
      this._preferences = preferences;
      this._appModel = appModel;

      try {
         this._keySpec = new DESKeySpec("alks43253456sdg45683fjoiuyrw98729572425324532453452352sfhshsdfs".getBytes("UTF8"));
      } catch (Exception var4) {
         this.logger.warn("Error creating encryption key.", var4);
      }
   }

   public Preferences getPreferences() {
      return this._preferences;
   }

   public Long getId(String key) {
      long value = this._preferences.getLong(key, -1L);
      return value > 0L ? value : null;
   }

   public void setId(String key, Long value) {
      if (value != null) {
         this._preferences.putLong(key, value);
      } else {
         this._preferences.remove(key);
      }
   }

   public <T> T[] getArray(String key, Class<T[]> clazz, T[] defaultValue) {
      T[] array = this.getArray(key, clazz);
      return array != null ? array : defaultValue;
   }

   public <T> T[] getArray(String key, Class<T[]> clazz) {
      String string = this._preferences.get(key, null);
      if (string != null) {
         try {
            return (T[])((Object[])this._objectMapper.readValue(string, clazz));
         } catch (Exception var5) {
            this.logger.error("Error reading value {}", string, var5);
         }
      }

      return null;
   }

   public void setArray(String key, Object object) {
      try {
         if (object != null) {
            this._preferences.put(key, this._objectMapper.writeValueAsString(object));
         } else {
            this._preferences.remove(key);
         }
      } catch (Exception var4) {
         this.logger.error("Error writing value {}", object, var4);
      }
   }

   public <T extends Enum<T>> T getEnum(String key, T def) {
      String enumName = this._preferences.get(key, null);
      return enumName != null ? Enum.valueOf(def.getDeclaringClass(), enumName) : def;
   }

   public <T extends Enum<T>> T getEnum(String key, Class<T> enumClazz) {
      String enumName = this._preferences.get(key, null);
      return enumName != null ? Enum.valueOf(enumClazz, enumName) : null;
   }

   public <T extends Enum<T>> void setEnum(String key, T value) {
      if (value != null) {
         this._preferences.put(key, value.name());
      } else {
         this._preferences.remove(key);
      }
   }

   public Color getColor(String key, Color def) {
      String colorHexString = this._preferences.get(key, null);
      return colorHexString != null ? new Color(Integer.parseInt(colorHexString, 16)) : def;
   }

   public void setColor(String key, Color value) {
      if (value != null) {
         this._preferences.put(key, StringUtils.leftPad(Integer.toHexString(value.getRGB() & 16777215), 6, "0"));
      } else {
         this._preferences.remove(key);
      }
   }

   public <E extends Enum<E>> EnumSet<E> getEnumSet(String key, Class<E> enumClass) {
      int intValue = this._preferences.getInt(key, 3);
      EnumSet<E> enumSet = EnumSet.noneOf(enumClass);

      for (E e : (Enum[])enumClass.getEnumConstants()) {
         int mask = (int)Math.pow(2.0, (double)e.ordinal());
         if ((intValue & mask) > 0) {
            enumSet.add(e);
         }
      }

      return enumSet;
   }

   public <E extends Enum<E>> void setEnumSet(String key, EnumSet<E> enumSet) {
      if (enumSet != null) {
         int intValue = 0;

         for (E e : enumSet) {
            int mask = (int)Math.pow(2.0, (double)e.ordinal());
            intValue |= mask;
         }

         this._preferences.putInt(key, intValue);
      } else {
         this._preferences.remove(key);
      }
   }

   public void load() throws URISyntaxException {
      String url = this._preferences.get("baseUrl", "https://c-sar.cropster.com");
      if (System.getProperty("org.cropster.roastingintelligence.baseUrl") != null) {
         url = System.getProperty("org.cropster.roastingintelligence.baseUrl");
      } else {
         System.setProperty("org.cropster.roastingintelligence.baseUrl", url);
      }

      this._appModel.setUri(new URI(url.trim()));
      this._appModel.setSyncTime(this._preferences.getLong("syncTime", -1L));
      long loginThresholdDate = this._preferences.getLong("lthr", -1L);
      if (loginThresholdDate > 0L) {
         this._appModel.setLoginThresholdDate(new Date(loginThresholdDate));
      }

      this._appModel.setSelectedRoastingPerson(this._preferences.get("selectedRoastingPerson", null));
      long profileId = this._preferences.getLong("selectedProfileId", -1L);
      if (profileId > 0L) {
         this._appModel.setSelectedProfileId(profileId);
      }

      this._appModel.getHints().addAll(this.getEnumSet("hints", AppModel.Hint.class));
      String user = this._preferences.get("user", null);
      String username = this._preferences.get("userName", null);
      String refreshToken = this._preferences.get("refreshToken", null);
      String clientId = this._preferences.get("clientId", null);
      String score = this._preferences.get("score", null);
      String count = this._preferences.get("count", null);
      this._appModel.setUser(user);
      this._appModel.setUserName(username);
      if (refreshToken != null) {
         this._appModel.setRefreshToken(this.decrypt(refreshToken));
      }

      this._appModel.setRememberMe(username == null || this._appModel.isTokenSet());
      this._appModel.setClientId(clientId != null ? this.decrypt(clientId) : null);
      this._appModel.setScore(StringUtils.isNotBlank(score) ? Long.valueOf(this.decrypt(score)) : 0L);
      this._appModel.setCount(StringUtils.isNotBlank(count) ? Long.valueOf(this.decrypt(count)) : 0L);
      com.cropster.roastingintelligence.Preferences preferences = this._appModel.getPreferences();
      preferences.setPreferredUnit(Unit.valueOf(this._preferences.get("temperatureUnit", Unit.CELSIUS.name())));
      preferences.setPreferredUnit(Unit.valueOf(this._preferences.get("pressureUnit", Unit.PASCAL.name())));
      preferences.setWeightUnit(Unit.valueOf(this._preferences.get("weightUnit", Unit.KG.name())));
      preferences.setRoastValueUnit(Unit.valueOf(this._preferences.get("roastValueUnit", Defaults.ROAST_VALUE_UNIT.name())));
      preferences.setRoastValue2Unit(Unit.valueOf(this._preferences.get("roastValue2Unit", Defaults.ROAST_VALUE_UNIT.name())));
      preferences.setReplayEnabled(this._preferences.getBoolean("replayEnabled", true));
      preferences.setResetGasThreshold(this._preferences.getInt("resetGasThreshold", -1));
      preferences.setStartThreshold(this._preferences.getDouble("startDetectionThreshold", Defaults.TEMPERATURE_DROP_START.doubleValue()));
      preferences.setEndThreshold(this._preferences.getDouble("endDetectionThreshold", Defaults.TEMPERATURE_DROP_END.doubleValue()));
      preferences.setMinTemp(this._preferences.getDouble("minTemp", 45.0));
      preferences.setMaxTemp(this._preferences.getDouble("maxTemp", 275.0));
      preferences.setMinRiseRate(this._preferences.getDouble("minRiseRate", 0.0));
      preferences.setMaxRiseRate(this._preferences.getDouble("maxRiseRate", Defaults.MAX_RISE_RATE));
      preferences.setTime(this._preferences.getLong("time", 1020L));
      preferences.setReferenceCommentHistoryHidden(this._preferences.getBoolean("referenceCommentHistoryHidden", true));
      preferences.setGasCurveAsCommentsThreshold(this._preferences.getInt("gasCurveAsCommentsThreshold", -1));
      preferences.setCommentingByTemp(this._preferences.getBoolean("commentingByTemp", true));
      preferences.setEventAutoMarking(this._preferences.getBoolean("eventAutoMarking", false));
      preferences.setSoundAlarmOnEventsEnabled(this._preferences.getBoolean("soundAlarmOnEvents", false));
      preferences.setAlarmsSoundRepetitions(this._preferences.getInt("alarmSoundRepetitions", 1));
      preferences.setAlarmsSoundVolume(this._preferences.getDouble("alarmSoundVolume", 0.7));
      preferences.setAlarmsToastNotificationEnabled(this._preferences.getBoolean("alarmToastNotification", true));
      preferences.setStopRoastingConfirmationEnabled(this._preferences.getBoolean("stopRoastingConfirmationEnabled", true));
      preferences.setCurvePredictionEnabled(this._preferences.getBoolean("curvePredictionEnabled", true));
      preferences.setFirstCrackPredictionEnabled(this._preferences.getBoolean("firstCrackPredictionEnabled", true));
      preferences.setFlickPredictionEnabled(this._preferences.getBoolean("flickPredictionEnabled", true));
      preferences.setBetweenBatchProtocolEnabled(this._preferences.getBoolean("betweenBatchProtocol", true));
      preferences.setRorSmoothing(this.getEnum("rorSmoothing", Defaults.ROR_SMOOTHING));
      preferences.setRor(this._preferences.getInt("ror", 60));
      preferences.setTimerType(this.getEnum("timerType", Defaults.TIMER_TYPE));
      preferences.setAutoDetectionEnabled(this._preferences.getBoolean("autoDetectionEnabled", true));
      preferences.setAutoStartEnabled(this._preferences.getBoolean("autoStartEnabled", false));
      preferences.setMaxRoastCount(this._preferences.getInt("maxRoastCount", 20));
      preferences.setTimeLapseEnabled(this._preferences.getBoolean("timeLapse", false));
      long machineId = this._preferences.getLong("roastingMachineId", -1L);
      if (machineId > 0L) {
         preferences.setSelectedMachineId(machineId);
      }

      preferences.setEnabledEventsPopup(this.getArray("enabledEvents", String[].class, Defaults.ENABLED_EVENTS));
      preferences.setEnabledEventsSidebar(this.getArray("enabledEventsSidebar", String[].class, Defaults.ENABLED_EVENTS));
      preferences.setEnabledEventsChart(this.getArray("enabledEventsChart", String[].class, Defaults.ENABLED_EVENTS));
      String[] preFills = this.getArray("preFills", String[].class);
      if (preFills != null) {
         preferences.setPreFills(preFills);
      }

      String[] channelNames = this.getArray("channels", String[].class);
      if (channelNames != null) {
         int i = 0;
         List<Channel> channels = new ArrayList<>();

         for (String channelName : channelNames) {
            Color defaultColor = i >= Defaults.COLORS.length ? Color.GRAY : Defaults.COLORS[i++];
            String prefix = String.format("Channel.%s.", channelName);
            Channel channel = new Channel(channelName);
            String sourceFactoryClassName = this._preferences.get(prefix + "sourceFactory", null);
            if (sourceFactoryClassName != null) {
               try {
                  channel.setSourceFactory((Class<? extends MeasurementSourceFactory>)Class.forName(sourceFactoryClassName));
               } catch (ClassNotFoundException var28) {
                  this.logger.error("Error loading preferences", var28);
               }
            }

            channel.setIndex(this._preferences.getInt(prefix + "index", 0));
            channel.setProbeType(this._preferences.get(prefix + "type", null));
            if (!channel.isControlChannel()) {
               channel.setColor(this.getColor(prefix + "color", defaultColor));
            }

            channel.setVisibility(this.getEnumSet(prefix + "visibility", Channel.Visibility.class));
            channel.setOffset(this._preferences.getDouble(prefix + "offset", 0.0));
            boolean legacyRor = this._preferences.getInt(prefix + "ror", "beanTemperature".equals(channelName) ? 1 : -1) > 0;
            channel.setRorEnabled(this._preferences.getBoolean(prefix + "ror", legacyRor));
            channels.add(channel);
         }

         for (Channel channelx : channels) {
            if (channelx.isControlChannel()) {
               channels.stream()
                  .filter(c -> !c.isControlChannel())
                  .filter(c -> channel.getName().startsWith(c.getName()))
                  .findFirst()
                  .ifPresentOrElse(c -> channel.setColor(c.getColor()), () -> channel.setColor(Color.BLACK));
            }
         }

         preferences.setChannels(channels);
      }

      this._appModel.fireSettingsChanged();
   }

   public void save() {
      try {
         this._preferences.putLong("syncTime", this._appModel.getSyncTime());
         if (this._appModel.getLoginThresholdDate() != null) {
            this._preferences.putLong("lthr", this._appModel.getLoginThresholdDate().getTime());
         } else {
            this._preferences.remove("lthr");
         }

         if (this._appModel.getUserName() != null) {
            if (this._appModel.getUser() != null) {
               this._preferences.put("user", this._appModel.getUser());
            }

            this._preferences.put("userName", this._appModel.getUserName());
         } else {
            this._preferences.remove("user");
            this._preferences.remove("userName");
         }

         if (this._appModel.isTokenSet() && this._appModel.isRememberMe()) {
            this._preferences.put("refreshToken", this.encrypt(this._appModel.getRefreshToken()));
         } else {
            this._preferences.remove("refreshToken");
         }

         this._preferences.put("clientId", this.encrypt(String.valueOf(this._appModel.getClientId())));
         this._preferences.put("score", this.encrypt(String.valueOf(this._appModel.getScore())));
         this._preferences.put("count", this.encrypt(String.valueOf(this._appModel.getCount())));
         if (this._appModel.getSelectedRoastingPerson() != null) {
            this._preferences.put("selectedRoastingPerson", this._appModel.getSelectedRoastingPerson());
         } else {
            this._preferences.remove("selectedRoastingPerson");
         }

         this.setEnumSet("hints", this._appModel.getHints());
         this._preferences.flush();
         this._preferences.sync();
      } catch (BackingStoreException var2) {
         this.logger.error("Exception saving preferences", var2);
      }
   }

   private String encrypt(String clearText) {
      try {
         SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
         SecretKey key = keyFactory.generateSecret(this._keySpec);
         byte[] clearTextBytes = clearText.getBytes("UTF-8");
         Cipher cipher = Cipher.getInstance("DES");
         cipher.init(1, key);
         byte[] encrypedBytes = cipher.doFinal(clearTextBytes);
         return Base64.encodeBase64String(encrypedBytes);
      } catch (Exception var7) {
         this.logger.warn("Error encrypting password. Password not saved.", var7);
         return null;
      }
   }

   private String decrypt(String encrypted) {
      try {
         SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
         SecretKey key = keyFactory.generateSecret(this._keySpec);
         byte[] encrypedBytes = Base64.decodeBase64(encrypted);
         Cipher cipher = Cipher.getInstance("DES");
         cipher.init(2, key);
         byte[] clearTextBytes = cipher.doFinal(encrypedBytes);
         return new String(clearTextBytes, "UTF-8");
      } catch (Exception var7) {
         this.logger.warn("Error decrypting password.", var7);
         return null;
      }
   }
}
