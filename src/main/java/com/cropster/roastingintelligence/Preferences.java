package com.cropster.roastingintelligence;

import com.cropster.roastingintelligence.model.ControlStateConfiguration;
import com.cropster.roastingintelligence.ui.components.RoastLabel;
import com.cropster.util.model.Unit;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.Defaults;
import org.cropster.roastingintelligence.measurement.MeasurementSourceFactory;
import org.cropster.roastingintelligence.measurement.SourceParameter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.util.Smoothing;
import org.cropster.roastingintelligence.util.json.SourceParameterDeserializer;
import org.cropster.roastingintelligence.util.json.SourceParameterSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JsonFilter("preferences")
@JsonPropertyOrder({"locale", "weightUnit", "temperatureUnit", "pressureUnit", "roastValueUnit", "roastValue2Unit", "timerType", "soundAlarmOnEventsEnabled", "stopRoastingConfirmationEnabled", "curvePredictionEnabled", "curvePredictionRequestInterval", "firstCrackPredictionEnabled", "flickPredictionEnabled", "rorSmoothing", "ror", "betweenBatchProtocolEnabled", "automaticallyReplaceLotEnabled", "replayEnabled", "resetGasThreshold", "autoDetectionEnabled", "autoStartEnabled", "startThreshold", "endThreshold", "time", "minTemp", "maxTemp", "minRiseRate", "maxRiseRate", "commentingByTemp", "eventAutoMarking", "referenceCommentHistoryHidden", "gasCurveAsCommentsThreshold", "enabledEventsChart", "enabledEventsSidebar", "enabledEventsPopup", "preFills", "selectedMachineId", "sources", "channels", "channelOrder", "states", "maxRoastCount", "timeLapseEnabled", "alarmsSoundRepetitions", "alarmsSoundVolume", "alarmsToastNotificationEnabled", "condensedChannelStatus", "sliderControlButtonsOnTop", "smartGoalsAndReferencesEnabled", "hideLiveGoalCurrentValueEnabled", "hideGoalStatisticsEnabled"})
public class Preferences {
   private final Logger logger = LoggerFactory.getLogger(Preferences.class);
   private Locale locale;
   private boolean sliderControlButtonsOnTop = true;
   private final Map<Unit, Unit> preferredUnits = new HashMap<>();
   private Unit roastValueUnit = Defaults.ROAST_VALUE_UNIT;
   private Unit roastValue2Unit = Defaults.ROAST_VALUE_UNIT;
   private AppModel.TimerType timerType = Defaults.TIMER_TYPE;
   private boolean soundAlarmOnEventsEnabled = false;
   private boolean alarmsToastNotificationEnabled = true;
   private int alarmsSoundRepetitions = 1;
   private double alarmsSoundVolume = 0.7;
   private boolean stopRoastingConfirmationEnabled = true;
   private boolean condensedChannelStatus = false;
   private boolean curvePredictionEnabled;
   private int curvePredictionRequestInterval = 1;
   private boolean firstCrackPredictionEnabled;
   private boolean flickPredictionEnabled;
   private Smoothing rorSmoothing = Smoothing.STANDARD;
   private int ror = 60;
   private boolean betweenBatchProtocolEnabled;
   private boolean automaticallyReplaceLotEnabled;
   private boolean smartGoalsAndReferencesEnabled = true;
   private boolean hideLiveGoalCurrentValueEnabled = false;
   private boolean hideGoalStatisticsEnabled = false;
   private boolean replayEnabled = true;
   private int resetGasThreshold = -1;
   private boolean autoDetectionEnabled;
   private boolean autoStartEnabled;
   private double startThreshold = Defaults.TEMPERATURE_DROP_START.doubleValue();
   private double endThreshold = Defaults.TEMPERATURE_DROP_END.doubleValue();
   private long time = 1020L;
   private double minTemp = 45.0;
   private double maxTemp = 275.0;
   private double minRiseRate = 0.0;
   private double maxRiseRate = Defaults.MAX_RISE_RATE;
   private boolean commentingByTemp;
   private boolean eventAutoMarking;
   private boolean referenceCommentHistoryHidden = true;
   private int gasCurveAsCommentsThreshold = -1;
   private String[] enabledEventsChart = Defaults.ENABLED_EVENTS;
   private String[] enabledEventsSidebar = Defaults.ENABLED_EVENTS;
   private String[] enabledEventsPopup = Defaults.ENABLED_EVENTS;
   private final Set<String> preFills = new HashSet<>(Arrays.asList("gas", "airflow"));
   private Long selectedMachineId;
   private String[] channelOrder = new String[0];
   private final List<Channel> channels = new ArrayList<>();
   private final List<ControlStateConfiguration> states = new ArrayList<>();
   private final Map<String, Set<SourceParameter>> sourceConfigurations = new LinkedHashMap<>();
   private Integer printFontSize = Defaults.PRINT_FONT_SIZE;
   private RoastLabel.QrCodePosition printQrCodePosition = Defaults.PRINT_QR_CODE_POSITION;
   private String printLabelStyle;
   private boolean printTitleVisible = true;
   private RoastLabel.DateFormatter printDateFormatter = Defaults.PRINT_DATE_FORMATTER;
   private final Map<String, String> printLabelMapping = new LinkedHashMap<>(Defaults.PRINT_LABEL_MAPPING);
   private int maxRoastCount = 20;
   private boolean timeLapseEnabled;

   public Preferences() {
      this.preferredUnits.put(Unit.KG, Unit.KG);
      this.preferredUnits.put(Unit.CELSIUS, Unit.CELSIUS);
      this.preferredUnits.put(Unit.PASCAL, Unit.PASCAL);
   }

   @JsonInclude(Include.NON_ABSENT)
   public Locale getLocale() {
      return this.locale;
   }

   public void setLocale(Locale locale) {
      this.locale = locale;
   }

   @JsonIgnore
   public Unit getPreferredUnit(Unit unit) {
      return this.preferredUnits.get(unit.getSiUnit());
   }

   public void setPreferredUnit(Unit unit) {
      this.preferredUnits.put(unit.getSiUnit(), unit);
   }

   @JsonIgnore
   public Unit getUnit(Unit unit) {
      return this.preferredUnits.containsKey(unit.getSiUnit()) ? this.preferredUnits.get(unit.getSiUnit()) : unit;
   }

   public Unit getWeightUnit() {
      return this.getPreferredUnit(Unit.KG);
   }

   public void setWeightUnit(Unit unit) {
      this.setPreferredUnit(unit);
   }

   public Unit getTemperatureUnit() {
      return this.getPreferredUnit(Unit.CELSIUS);
   }

   public void setTemperatureUnit(Unit unit) {
      this.setPreferredUnit(unit);
   }

   public Unit getPressureUnit() {
      return this.getPreferredUnit(Unit.PASCAL);
   }

   public void setPressureUnit(Unit unit) {
      this.setPreferredUnit(unit);
   }

   public Unit getRoastValueUnit() {
      return this.roastValueUnit;
   }

   public void setRoastValueUnit(Unit roastValueUnit) {
      this.roastValueUnit = roastValueUnit;
   }

   public Unit getRoastValue2Unit() {
      return this.roastValue2Unit;
   }

   public void setRoastValue2Unit(Unit roastValue2Unit) {
      this.roastValue2Unit = roastValue2Unit;
   }

   public AppModel.TimerType getTimerType() {
      return this.timerType;
   }

   public void setTimerType(AppModel.TimerType timerType) {
      this.timerType = timerType;
   }

   public boolean isSoundAlarmOnEventsEnabled() {
      return this.soundAlarmOnEventsEnabled;
   }

   public void setSoundAlarmOnEventsEnabled(boolean soundAlarmOnEventsEnabled) {
      this.soundAlarmOnEventsEnabled = soundAlarmOnEventsEnabled;
   }

   public int getAlarmsSoundRepetitions() {
      return this.alarmsSoundRepetitions;
   }

   public void setAlarmsSoundRepetitions(int alarmsSoundRepetitions) {
      this.alarmsSoundRepetitions = Math.max(1, alarmsSoundRepetitions);
   }

   public double getAlarmsSoundVolume() {
      return this.alarmsSoundVolume;
   }

   public void setAlarmsSoundVolume(double alarmsSoundVolume) {
      this.alarmsSoundVolume = Math.min(1.0, Math.max(0.0, alarmsSoundVolume));
   }

   public boolean isAlarmsToastNotificationEnabled() {
      return this.alarmsToastNotificationEnabled;
   }

   public void setAlarmsToastNotificationEnabled(boolean alarmsToastNotificationEnabled) {
      this.alarmsToastNotificationEnabled = alarmsToastNotificationEnabled;
   }

   public boolean isStopRoastingConfirmationEnabled() {
      return this.stopRoastingConfirmationEnabled;
   }

   public void setStopRoastingConfirmationEnabled(boolean stopRoastingConfirmationEnabled) {
      this.stopRoastingConfirmationEnabled = stopRoastingConfirmationEnabled;
   }

   public boolean isSliderControlButtonsOnTop() {
      return this.sliderControlButtonsOnTop;
   }

   public void setSliderControlButtonsOnTop(boolean sliderControlButtonsOnTop) {
      this.sliderControlButtonsOnTop = sliderControlButtonsOnTop;
   }

   public boolean isCondensedChannelStatus() {
      return this.condensedChannelStatus;
   }

   public void setCondensedChannelStatus(boolean condensedChannelStatus) {
      this.condensedChannelStatus = condensedChannelStatus;
   }

   public boolean isCurvePredictionEnabled() {
      return this.curvePredictionEnabled;
   }

   public void setCurvePredictionEnabled(boolean curvePredictionEnabled) {
      this.curvePredictionEnabled = curvePredictionEnabled;
   }

   public int getCurvePredictionRequestInterval() {
      return this.curvePredictionRequestInterval;
   }

   public void setCurvePredictionRequestInterval(int curvePredictionRequestInterval) {
      this.curvePredictionRequestInterval = curvePredictionRequestInterval;
   }

   public boolean isFirstCrackPredictionEnabled() {
      return this.firstCrackPredictionEnabled;
   }

   public void setFirstCrackPredictionEnabled(boolean firstCrackPredictionEnabled) {
      this.firstCrackPredictionEnabled = firstCrackPredictionEnabled;
   }

   public boolean isFlickPredictionEnabled() {
      return this.flickPredictionEnabled;
   }

   public void setFlickPredictionEnabled(boolean flickPredictionEnabled) {
      this.flickPredictionEnabled = flickPredictionEnabled;
   }

   public Smoothing getRorSmoothing() {
      return this.rorSmoothing;
   }

   public void setRorSmoothing(Smoothing rorSmoothing) {
      this.rorSmoothing = rorSmoothing;
   }

   public int getRor() {
      return this.ror;
   }

   public void setRor(int ror) {
      this.ror = ror;
   }

   public boolean isBetweenBatchProtocolEnabled() {
      return this.betweenBatchProtocolEnabled;
   }

   public void setBetweenBatchProtocolEnabled(boolean betweenBatchProtocolEnabled) {
      this.betweenBatchProtocolEnabled = betweenBatchProtocolEnabled;
   }

   public boolean isAutomaticallyReplaceLotEnabled() {
      return this.automaticallyReplaceLotEnabled;
   }

   public void setAutomaticallyReplaceLotEnabled(boolean automaticallyReplaceLotEnabled) {
      this.automaticallyReplaceLotEnabled = automaticallyReplaceLotEnabled;
   }

   public boolean isReplayEnabled() {
      return this.replayEnabled;
   }

   public void setReplayEnabled(boolean replayEnabled) {
      this.replayEnabled = replayEnabled;
   }

   public int getResetGasThreshold() {
      return this.resetGasThreshold;
   }

   public void setResetGasThreshold(int resetGasThreshold) {
      this.resetGasThreshold = resetGasThreshold;
   }

   public boolean isAutoDetectionEnabled() {
      return this.autoDetectionEnabled;
   }

   public void setAutoDetectionEnabled(boolean autoDetectionEnabled) {
      this.autoDetectionEnabled = autoDetectionEnabled;
   }

   public boolean isAutoStartEnabled() {
      return this.autoStartEnabled;
   }

   public void setAutoStartEnabled(boolean autoStartEnabled) {
      this.autoStartEnabled = autoStartEnabled;
   }

   public double getStartThreshold() {
      return this.startThreshold;
   }

   public void setStartThreshold(double startThreshold) {
      this.startThreshold = startThreshold;
   }

   public double getEndThreshold() {
      return this.endThreshold;
   }

   public void setEndThreshold(double endThreshold) {
      this.endThreshold = endThreshold;
   }

   public long getTime() {
      return this.time;
   }

   public void setTime(long time) {
      this.time = time;
   }

   @JsonIgnore
   public int getMaxRoastingTime() {
      return (int)(this.time * 1000L);
   }

   public double getMinTemp() {
      return this.minTemp;
   }

   public void setMinTemp(double minTemp) {
      this.minTemp = minTemp;
   }

   public double getMaxTemp() {
      return this.maxTemp;
   }

   public void setMaxTemp(double maxTemp) {
      this.maxTemp = maxTemp;
   }

   public double getMinRiseRate() {
      return this.minRiseRate;
   }

   public void setMinRiseRate(double minRiseRate) {
      this.minRiseRate = minRiseRate;
   }

   public double getMaxRiseRate() {
      return this.maxRiseRate;
   }

   public void setMaxRiseRate(double maxRiseRate) {
      this.maxRiseRate = maxRiseRate;
   }

   public boolean isCommentingByTemp() {
      return this.commentingByTemp;
   }

   public void setCommentingByTemp(boolean commentsByTemp) {
      this.commentingByTemp = commentsByTemp;
   }

   public boolean isEventAutoMarking() {
      return this.eventAutoMarking;
   }

   public void setEventAutoMarking(boolean eventAutoMarking) {
      this.eventAutoMarking = eventAutoMarking;
   }

   public boolean isReferenceCommentHistoryHidden() {
      return this.referenceCommentHistoryHidden;
   }

   public void setReferenceCommentHistoryHidden(boolean referenceCommentHistoryHidden) {
      this.referenceCommentHistoryHidden = referenceCommentHistoryHidden;
   }

   public int getGasCurveAsCommentsThreshold() {
      return this.gasCurveAsCommentsThreshold;
   }

   public void setGasCurveAsCommentsThreshold(int gasCurveAsCommentsThreshold) {
      this.gasCurveAsCommentsThreshold = gasCurveAsCommentsThreshold;
   }

   public String[] getEnabledEventsChart() {
      return this.enabledEventsChart;
   }

   public void setEnabledEventsChart(String[] enabledEventsChart) {
      this.enabledEventsChart = enabledEventsChart;
   }

   public String[] getEnabledEventsSidebar() {
      return this.enabledEventsSidebar;
   }

   public void setEnabledEventsSidebar(String[] enabledEventsSidebar) {
      this.enabledEventsSidebar = enabledEventsSidebar;
   }

   public String[] getEnabledEventsPopup() {
      return this.enabledEventsPopup;
   }

   public void setEnabledEventsPopup(String[] enabledEventsPopup) {
      this.enabledEventsPopup = enabledEventsPopup;
   }

   public Set<String> getPreFills() {
      return Collections.unmodifiableSet(this.preFills);
   }

   public boolean isPreFillEnabled(String preFill) {
      return this.preFills.contains(preFill);
   }

   public void setPreFills(String... preFills) {
      this.preFills.clear();
      Collections.addAll(this.preFills, preFills);
   }

   public void setPreFill(String preFill, boolean enabled) {
      if (enabled) {
         this.preFills.add(preFill);
      } else {
         this.preFills.remove(preFill);
      }
   }

   public Long getSelectedMachineId() {
      return this.selectedMachineId;
   }

   public void setSelectedMachineId(Long machineId) {
      this.selectedMachineId = machineId;
   }

   public String[] getChannelOrder() {
      return this.channelOrder;
   }

   public void setChannelOrder(String[] channelOrder) {
      this.channelOrder = channelOrder;
   }

   public List<Channel> getChannels() {
      return Collections.unmodifiableList(this.channels);
   }

   public void setChannels(Collection<Channel> channels) {
      this.channels.clear();
      this.channels.addAll(channels);
   }

   public List<ControlStateConfiguration> getStates() {
      return Collections.unmodifiableList(this.states);
   }

   public void setStates(Collection<ControlStateConfiguration> states) {
      this.states.clear();
      this.states.addAll(states);
   }

   @JsonProperty("sources")
   @JsonSerialize(
      using = SourceParameterSerializer.class
   )
   public Map<String, Set<SourceParameter>> getSourceConfigurations() {
      return Collections.unmodifiableMap(this.sourceConfigurations);
   }

   @JsonDeserialize(
      using = SourceParameterDeserializer.class
   )
   public void setSourceConfigurations(Map<String, Set<SourceParameter>> sourceConfigurations) {
      this.sourceConfigurations.clear();
      this.sourceConfigurations.putAll(sourceConfigurations);
   }

   public Set<SourceParameter> merge(MeasurementSourceFactory sourceFactory) {
      Set<SourceParameter> storedParameters = this.sourceConfigurations.get(sourceFactory.getClass().getSimpleName());
      Set<SourceParameter> mergedParameters = new LinkedHashSet<>();

      for (SourceParameter parameter : sourceFactory.getParameters()) {
         if (storedParameters != null) {
            storedParameters.stream().filter(p -> p.getName().equals(parameter.getName())).findFirst().ifPresent(p -> parameter.setValue(p.getValue()));
         }

         mergedParameters.add(parameter);
      }

      return mergedParameters;
   }

   public Integer getPrintFontSize() {
      return this.printFontSize;
   }

   public void setPrintFontSize(Integer printFontSize) {
      this.printFontSize = printFontSize;
   }

   public RoastLabel.QrCodePosition getPrintQrCodePosition() {
      return this.printQrCodePosition;
   }

   public void setPrintQrCodePosition(RoastLabel.QrCodePosition printQrCodePosition) {
      this.printQrCodePosition = printQrCodePosition;
   }

   public String getPrintLabelStyle() {
      return this.printLabelStyle;
   }

   public void setPrintLabelStyle(String printLabelStyle) {
      this.printLabelStyle = printLabelStyle;
   }

   public boolean isPrintTitleVisible() {
      return this.printTitleVisible;
   }

   public void setPrintTitleVisible(boolean printTitleVisible) {
      this.printTitleVisible = printTitleVisible;
   }

   public RoastLabel.DateFormatter getPrintDateFormatter() {
      return this.printDateFormatter;
   }

   public void setPrintDateFormatter(RoastLabel.DateFormatter printDateFormatter) {
      this.printDateFormatter = printDateFormatter;
   }

   @JsonProperty("printLabel")
   public Map<String, String> getPrintLabelMapping() {
      return this.printLabelMapping;
   }

   public void setPrintLabelMapping(Map<String, String> printLabelMapping) {
      this.printLabelMapping.clear();
      this.printLabelMapping.putAll(printLabelMapping);
   }

   public int getMaxRoastCount() {
      return this.maxRoastCount;
   }

   public void setMaxRoastCount(int maxRoastCount) {
      if (maxRoastCount <= 100) {
         this.maxRoastCount = maxRoastCount;
      }
   }

   public boolean isTimeLapseEnabled() {
      return this.timeLapseEnabled;
   }

   public void setTimeLapseEnabled(boolean timeLapseEnabled) {
      this.timeLapseEnabled = timeLapseEnabled;
   }

   public boolean isSmartGoalsAndReferencesEnabled() {
      return this.smartGoalsAndReferencesEnabled;
   }

   public void setSmartGoalsAndReferencesEnabled(boolean smartGoalsAndReferencesEnabled) {
      this.smartGoalsAndReferencesEnabled = smartGoalsAndReferencesEnabled;
   }

   public boolean isHideGoalStatisticsEnabled() {
      return this.hideGoalStatisticsEnabled;
   }

   public void setHideGoalStatisticsEnabled(boolean hideGoalStatisticsEnabled) {
      this.hideGoalStatisticsEnabled = hideGoalStatisticsEnabled;
   }

   public boolean isHideLiveGoalCurrentValueEnabled() {
      return this.hideLiveGoalCurrentValueEnabled;
   }

   public void setHideLiveGoalCurrentValueEnabled(boolean hideLiveGoalCurrentValueEnabled) {
      this.hideLiveGoalCurrentValueEnabled = hideLiveGoalCurrentValueEnabled;
   }

   public void applyChannelChecks() {
      this.channels.forEach(channel -> {
         if (channel.isRorEnabled() && !channel.isRorSupported()) {
            this.logger.warn("Channel {} does not support RoR. Disabling RoR setting.", channel.getName());
            channel.setRorEnabled(false);
         }
      });
   }
}
