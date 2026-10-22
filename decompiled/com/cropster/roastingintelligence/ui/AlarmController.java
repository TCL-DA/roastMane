package com.cropster.roastingintelligence.ui;

import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.listener.AlarmsChangeEvent;
import com.cropster.roastingintelligence.listener.AlarmsChangeListener;
import com.cropster.roastingintelligence.listener.TriggeredAlarmEvent;
import com.cropster.roastingintelligence.listener.TriggeredAlarmListener;
import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.alarms.Alarm;
import com.cropster.roastingintelligence.model.alarms.AlarmType;
import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.NotificationType;
import com.cropster.roastingintelligence.service.AlarmService;
import com.cropster.roastingintelligence.service.NotificationBusService;
import com.cropster.roastingintelligence.service.StorageService;
import com.cropster.roastingintelligence.service.impl.AlarmServiceImpl;
import com.cropster.roastingintelligence.ui.audio.SoundManager;
import com.cropster.roastingintelligence.ui.layout.AlarmsSidebar;
import com.cropster.roastingintelligence.ui.list.EmptyStateList;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.WindowUtil;
import com.cropster.util.model.Unit;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Animation.Status;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import org.controlsfx.control.action.Action;
import org.cropster.roastingintelligence.RoastingIntelligenceApp;
import org.cropster.roastingintelligence.SyncService;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.service.impl.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlarmController implements AlarmsChangeListener, TriggeredAlarmListener {
   public static final String CREATED_ALARM_PROPERTY = "created-alarm";
   private static final String LOG_MESSAGE_FORMAT = "{0,number,#0} triggered alerts";
   private static final String MANAGE_MESSAGE_FORMAT = "{0,number,#0} of {1,number,#0} active alerts";
   private static final Duration VISUAL_NOTIFICATION_DURATION = Duration.millis(10000.0);
   private static final Logger LOGGER = LoggerFactory.getLogger(AlarmController.class);
   private final ObservableList<Alarm> alarms = FXCollections.observableArrayList();
   private final ObservableList<AlarmController.TriggeredAlarmHolder> triggeredAlarms = FXCollections.observableArrayList();
   private final IntegerProperty unacknowledgedTriggeredAlarms = new SimpleIntegerProperty(0);
   private final BooleanProperty visualAlarmEngaged = new SimpleBooleanProperty(false);
   private final AtomicInteger visualTriggeringAlarmsCount = new AtomicInteger(0);
   private final BooleanProperty toastNotificationSuspended = new SimpleBooleanProperty(false);
   private final AlarmService alarmService = new AlarmServiceImpl();
   private final AtomicReference<Action> toastNotificationAction = new AtomicReference<>();
   private final AtomicBoolean toastNotificationEnabled = new AtomicBoolean(true);

   private static List<Alarm> convertAlarmsUnit(List<Alarm> alarms, Preferences preferences) {
      List<Alarm> convertedAlarms = new ArrayList<>(alarms.size());

      for (Alarm alarm : alarms) {
         if (alarm.getType() == AlarmType.MEASURE_BASED) {
            Unit savedUnit = alarm.getTargetMeasure().getUnit();
            Unit userUnit = preferences.getUnit(savedUnit);
            if (userUnit != savedUnit) {
               convertedAlarms.add(Alarm.builder(alarm, true).withTargetMeasure(alarm.getTargetMeasure().toUnit(userUnit)).build());
               continue;
            }
         }

         convertedAlarms.add(alarm);
      }

      return convertedAlarms;
   }

   private static List<Alarm> loadAlarmsFromServer(CsarConnection connection) {
      List<Alarm> alarms = null;
      String json = null;

      try {
         json = connection.loadAlarmPreferences();
      } catch (Exception var6) {
         LOGGER.error("Error loading alarms from server", var6);
      }

      if (json != null) {
         JsonReader jsonReader = new JsonReader();

         try {
            alarms = jsonReader.readAlarms(new StringReader(json));
            LOGGER.info("Alarms loaded from server");
         } catch (Exception var5) {
            LOGGER.error("Error reading alarms from server", var5);
         }
      } else {
         LOGGER.info("No alarms found on server");
      }

      return alarms;
   }

   @Override
   public void alarmsChanged(AlarmsChangeEvent event) {
      Platform.runLater(() -> {
         List<Alarm> changedAlarms = event.getChangedAlarms();
         List<Integer> changedAlarmsIndexes = event.getChangedAlarmsIndexes();
         switch (event.getChangeType()) {
            case ADD:
               for (int i = 0; i < changedAlarms.size(); i++) {
                  this.alarms.add(changedAlarmsIndexes.get(i), changedAlarms.get(i));
               }
               break;
            case REMOVE:
               for (int i = changedAlarms.size() - 1; i >= 0; i--) {
                  this.alarms.remove(changedAlarmsIndexes.get(i));
               }
               break;
            case CLEAR:
               this.alarms.clear();
               break;
            default:
               throw new IllegalStateException("Unknown AlarmsChangeEvent.ChangeType: " + event.getChangeType());
         }
      });
   }

   @Override
   public void alarmTriggered(TriggeredAlarmEvent triggeredAlarmEvent) {
      AlarmController.TriggeredAlarmHolder holder = new AlarmController.TriggeredAlarmHolder(triggeredAlarmEvent);
      this.triggeredAlarms.add(holder);
      if (this.toastNotificationEnabled.get() && !this.isToastNotificationSuspended()) {
         RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class)
            .publish(
               Notification.builder(NotificationType.INFO)
                  .withTitle(org.cropster.roastingintelligence.util.Messages.m_("Alert \"{0}\" triggered", triggeredAlarmEvent.getAlarm().toString()))
                  .withProperty("action", this.toastNotificationAction.get())
                  .withProperty("action-close", new ActionBuilder(event -> holder.clearVisualFeedback()).build())
                  .withProperty("hide-after", VISUAL_NOTIFICATION_DURATION)
                  .withProperty("position", Pos.TOP_RIGHT)
                  .build(),
               "alarm"
            );
      }
   }

   public void initializeAlarmSystem(Preferences preferences, SyncService syncService, CsarConnection connection) {
      StorageService storageService = RoastingIntelligenceApp.retrieveSingleton(StorageService.class);
      this.alarmService
         .addPropertyChangeListener(
            "dirty",
            e -> {
               boolean saveNeeded = (Boolean)e.getNewValue();
               if (saveNeeded) {
                  try {
                     storageService.saveAlarms(this.alarmService.exportTo());
                  } catch (IOException var6x) {
                     LOGGER.error("Unable to write alarms file", var6x);
                     WindowUtil.showAlert(
                        AlertType.ERROR,
                        null,
                        org.cropster.roastingintelligence.util.Messages.m_("Error saving alarms"),
                        org.cropster.roastingintelligence.util.Messages.m_("The alarms file could not be written.")
                     );
                  }

                  syncService.uploadAlarms();
               }
            }
         );
      syncService.uploadAlarms();
      this.alarmService.setDirtyNotificationEnabled(false);
      boolean createDefaultAlarms = false;
      List<Alarm> alarms = storageService.loadAlarms();
      if (alarms == null) {
         List<Alarm> alarmsOnServer = loadAlarmsFromServer(connection);
         if (alarmsOnServer == null) {
            createDefaultAlarms = preferences.isSoundAlarmOnEventsEnabled();
            alarms = List.of();
         } else {
            alarms = alarmsOnServer;
         }
      }

      if (!alarms.isEmpty()) {
         List<Alarm> convertedAlarms = convertAlarmsUnit(alarms, preferences);
         this.alarmService.importFrom(convertedAlarms);
      }

      this.alarmService.setDirtyNotificationEnabled(true, alarms.isEmpty());
      if (createDefaultAlarms) {
         LOGGER.info("Creating default alarms");
         this.alarmService.createDefaultAlarms();
      }

      synchronized (this.alarmService) {
         this.synchronizeAlarms();
         this.alarmService.addAlarmsChangeListener(this);
         this.alarmService.addTriggeredAlarmListener(this);
      }

      if (preferences.isSoundAlarmOnEventsEnabled()) {
         this.alarmService.resume();
      }
   }

   public void initSidebar(AlarmsSidebar alarmsSidebar) {
      alarmsSidebar.setAlarmViewItems(this.alarms);
      ListView<Alarm> alarmsView = alarmsSidebar.getAlarmView();
      alarmsView.scrollTo(0);
      alarmsView.setOnEditCommit(e -> {
         alarmsView.getProperties().remove("created-alarm");
         int selectedIndex = alarmsView.getSelectionModel().getSelectedIndex();
         Alarm editedAlarm = (Alarm)e.getNewValue();
         if (this.alarmService.containsAlarm(editedAlarm)) {
            this.alarmService.replaceAlarms(editedAlarm);
         } else {
            this.alarms.remove(selectedIndex);
            this.alarmService.addAlarms(Alarm.builder(editedAlarm, true).withEnabled(true).build());
         }

         Platform.runLater(() -> alarmsView.getSelectionModel().select(selectedIndex));
      });
      alarmsView.setOnEditCancel(e -> {
         alarmsView.getProperties().remove("created-alarm");
         Alarm selectedAlarm = (Alarm)alarmsView.getSelectionModel().getSelectedItem();
         if (!this.alarmService.containsAlarm(selectedAlarm)) {
            Platform.runLater(() -> this.alarms.remove(selectedAlarm));
         }
      });
      alarmsSidebar.setTriggeredAlarmViewItems(this.triggeredAlarms);
      EmptyStateList<AlarmController.TriggeredAlarmHolder> triggeredAlarmEmptyStateList = alarmsSidebar.getTriggeredAlarmEmptyStateList();
      triggeredAlarmEmptyStateList.visibleProperty().addListener((v, o, n) -> {
         if (!n) {
            this.dismissTriggeredAlarms();
         }
      });
      alarmsSidebar.visibleProperty().addListener((v, o, n) -> {
         if (!n && triggeredAlarmEmptyStateList.isVisible()) {
            this.dismissTriggeredAlarms();
         }
      });
      FilteredList<Alarm> enabledAlarms = this.alarms.filtered(Alarm::isEnabled);
      alarmsSidebar.getFooterLabel()
         .textProperty()
         .bind(
            Bindings.createStringBinding(
               () -> alarmsSidebar.isManageSelected()
                     ? org.cropster.roastingintelligence.util.Messages.m_(
                        "{0,number,#0} of {1,number,#0} active alerts", enabledAlarms.size(), this.alarms.size()
                     )
                     : org.cropster.roastingintelligence.util.Messages.m_("{0,number,#0} triggered alerts", this.triggeredAlarms.size()),
               new Observable[]{alarmsSidebar.manageSelectedProperty(), this.alarms, enabledAlarms, this.triggeredAlarms}
            )
         );
      alarmsSidebar.getAddButton().setOnAction(e -> {
         if (!alarmsSidebar.getFilterButtonAll().isSelected()) {
            alarmsSidebar.getFilterButtonAll().fire();
         }

         this.alarms.add(Alarm.eventBasedBuilder(RoastingPhase.ROASTING, "time").withEnabled(false).build());
         Platform.runLater(() -> {
            int lastItemIndex = this.alarms.size() - 1;
            alarmsView.scrollTo(lastItemIndex);
            alarmsView.getProperties().put("created-alarm", true);
            Platform.runLater(() -> alarmsView.edit(lastItemIndex));
         });
      });
      alarmsSidebar.setTriggeredAlarmCleaner(this::clearTriggeredAlarm);
      alarmsSidebar.getClearButton().setOnAction(e -> this.clearTriggeredAlarms());
      alarmsSidebar.getClearButton()
         .disableProperty()
         .bind(Bindings.createBooleanBinding(this.triggeredAlarms::isEmpty, new Observable[]{this.triggeredAlarms}));
      alarmsSidebar.setAlarmRemover(xva$0 -> rec$.removeAlarms(xva$0));
      alarmsSidebar.setAlarmReplacer(xva$0 -> rec$.replaceAlarms(xva$0));
   }

   public AlarmService getAlarmService() {
      return this.alarmService;
   }

   public ObservableList<Alarm> getAlarms() {
      return this.alarms;
   }

   public void synchronizeAlarms() {
      this.alarms.setAll(this.alarmService.getAlarms());
   }

   public void clearTriggeredAlarms() {
      Platform.runLater(() -> {
         this.triggeredAlarms.clear();
         this.unacknowledgedTriggeredAlarms.set(0);
      });
   }

   public void clearTriggeredAlarm(AlarmController.TriggeredAlarmHolder triggeredAlarmHolder) {
      if (triggeredAlarmHolder != null) {
         Platform.runLater(() -> {
            triggeredAlarmHolder.clearVisualFeedback();
            this.triggeredAlarms.remove(triggeredAlarmHolder);
            if (!triggeredAlarmHolder.isDismissed()) {
               this.unacknowledgedTriggeredAlarms.set(this.unacknowledgedTriggeredAlarms.get() - 1);
            }
         });
      }
   }

   public ObservableList<AlarmController.TriggeredAlarmHolder> getTriggeredAlarms() {
      return FXCollections.unmodifiableObservableList(this.triggeredAlarms);
   }

   public ReadOnlyBooleanProperty visualAlarmEngagedProperty() {
      return this.visualAlarmEngaged;
   }

   public boolean isVisualAlarmEngaged() {
      return this.visualAlarmEngaged.get();
   }

   public synchronized boolean isToastNotificationEnabled() {
      return this.toastNotificationEnabled.get();
   }

   public synchronized void setToastNotificationEnabled(boolean toastNotificationEnabled) {
      this.toastNotificationEnabled.set(toastNotificationEnabled);
   }

   public BooleanProperty toastNotificationSuspendedProperty() {
      return this.toastNotificationSuspended;
   }

   public synchronized boolean isToastNotificationSuspended() {
      return this.toastNotificationSuspended.get();
   }

   public synchronized void resumeToastNotification() {
      this.toastNotificationSuspended.set(false);
   }

   public synchronized void suspendToastNotification() {
      this.toastNotificationSuspended.set(true);
   }

   public synchronized Action getToastNotificationAction() {
      return this.toastNotificationAction.get();
   }

   public synchronized void setToastNotificationAction(Action toastNotificationAction) {
      this.toastNotificationAction.set(toastNotificationAction);
   }

   public void dismissTriggeredAlarms() {
      Platform.runLater(() -> this.triggeredAlarms.stream().filter(tae -> !tae.isDismissed()).forEach(tah -> tah.dismiss()));
   }

   public IntegerProperty unacknowledgedTriggeredAlarmsProperty() {
      return this.unacknowledgedTriggeredAlarms;
   }

   public int getUnacknowledgedTriggeredAlarms() {
      return this.unacknowledgedTriggeredAlarms.get();
   }

   public class TriggeredAlarmHolder {
      private final TriggeredAlarmEvent triggeredAlarmEvent;
      private final BooleanProperty dismissed = new SimpleBooleanProperty(false) {
         protected void invalidated() {
            AlarmController.this.unacknowledgedTriggeredAlarms.set(AlarmController.this.unacknowledgedTriggeredAlarms.get() - 1);
         }
      };
      private final DoubleProperty progress = new SimpleDoubleProperty();
      private Timeline timeline = null;

      public TriggeredAlarmHolder(TriggeredAlarmEvent triggeredAlarmEvent) {
         this.triggeredAlarmEvent = triggeredAlarmEvent;
         if (triggeredAlarmEvent.getAlarm().isVisualFeedback()) {
            AlarmController.this.visualTriggeringAlarmsCount.incrementAndGet();
            AlarmController.this.visualAlarmEngaged.set(true);
            this.timeline = this.createProgressTimeline();
            this.timeline.setOnFinished(e -> this.doFinish());
            this.timeline.play();
         }

         AlarmController.this.unacknowledgedTriggeredAlarms.set(AlarmController.this.unacknowledgedTriggeredAlarms.get() + 1);
      }

      private Timeline createProgressTimeline() {
         KeyValue progressBegin = new KeyValue(this.progress, 1.0);
         KeyValue progressEnd = new KeyValue(this.progress, 0.0);
         KeyFrame kfBegin = new KeyFrame(Duration.ZERO, new KeyValue[]{progressBegin});
         KeyFrame kfEnd = new KeyFrame(AlarmController.VISUAL_NOTIFICATION_DURATION, new KeyValue[]{progressEnd});
         return new Timeline(new KeyFrame[]{kfBegin, kfEnd});
      }

      public void clearVisualFeedback() {
         if (this.timeline != null && this.timeline.getStatus() == Status.RUNNING) {
            this.timeline.stop();
            this.doFinish();
         }
      }

      private void doFinish() {
         this.progress.set(0.0);
         if (AlarmController.this.visualTriggeringAlarmsCount.decrementAndGet() == 0) {
            AlarmController.this.visualAlarmEngaged.set(false);
         }

         if (SoundManager.get().isPlaying(this.triggeredAlarmEvent.getIdentifier())) {
            SoundManager.get().stop();
         }
      }

      public Alarm getAlarm() {
         return this.triggeredAlarmEvent.getAlarm();
      }

      public String getIdentifier() {
         return this.triggeredAlarmEvent.getIdentifier();
      }

      public TriggeredAlarmEvent getTriggeredAlarmEvent() {
         return this.triggeredAlarmEvent;
      }

      public ReadOnlyBooleanProperty dismissedProperty() {
         return this.dismissed;
      }

      public boolean isDismissed() {
         return this.dismissed.get();
      }

      public void dismiss() {
         this.dismissed.set(true);
      }

      public ReadOnlyDoubleProperty progressProperty() {
         return this.progress;
      }

      public double getProgress() {
         return this.progress.get();
      }
   }
}
