package org.cropster.roastingintelligence;

import com.cropster.roastingintelligence.Preferences;
import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalBetweenBatchModel;
import com.cropster.roastingintelligence.model.notification.Notification;
import com.cropster.roastingintelligence.model.notification.NotificationType;
import com.cropster.roastingintelligence.service.AlarmService;
import com.cropster.roastingintelligence.service.LiveGoalService;
import com.cropster.roastingintelligence.service.NotificationBusService;
import com.cropster.roastingintelligence.ui.AlarmController;
import com.cropster.roastingintelligence.ui.JavaFxApplicationState;
import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.ui.LiveGoalController;
import com.cropster.roastingintelligence.ui.ToastNotificationController;
import com.cropster.roastingintelligence.ui.chart.BetweenBatchChartWrapper;
import com.cropster.roastingintelligence.ui.chart.RoastChartWrapper;
import com.cropster.roastingintelligence.ui.components.CommentPopupContent;
import com.cropster.roastingintelligence.ui.components.CommentPopupHeader;
import com.cropster.roastingintelligence.ui.layout.MainPanelController;
import com.cropster.roastingintelligence.ui.layout.PreparationPanel;
import com.cropster.roastingintelligence.ui.util.ActionBuilder;
import com.cropster.roastingintelligence.ui.util.AlarmsUtil;
import com.cropster.roastingintelligence.ui.util.ChartUtils;
import com.cropster.roastingintelligence.ui.util.PopOverHandler;
import com.cropster.roastingintelligence.ui.util.WindowUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.CommentListener;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;
import org.cropster.roastingintelligence.model.impl.AbstractCurveModel;
import org.cropster.roastingintelligence.model.impl.StartEndPointCurveModel;
import org.cropster.roastingintelligence.util.MathUtil;
import org.cropster.roastingintelligence.util.NanoTime;
import org.jfree.chart.fx.ChartViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetweenBatchManager {
   private final Logger logger = LoggerFactory.getLogger(BetweenBatchManager.class);
   private final RoastingIntelligenceApp application;
   private final AlarmController alarmController;
   private final LiveGoalController liveGoalController;
   private final MeasurementHub measurementHub;
   private final MainPanelController mainPanelController;
   private final JavaFxApplicationState state;
   private final AppModel appModel;
   private final ToastNotificationController toastNotificationController;
   private final NotificationBusService notificationBusService = RoastingIntelligenceApp.retrieveSingleton(NotificationBusService.class);

   public BetweenBatchManager(
      RoastingIntelligenceApp application,
      AlarmController alarmController,
      LiveGoalController liveGoalController,
      MeasurementHub measurementHub,
      MainPanelController mainPanelController,
      JavaFxApplicationState state,
      AppModel appModel,
      ToastNotificationController toastNotificationController
   ) {
      this.application = application;
      this.alarmController = alarmController;
      this.liveGoalController = liveGoalController;
      this.measurementHub = measurementHub;
      this.mainPanelController = mainPanelController;
      this.state = state;
      this.appModel = appModel;
      this.toastNotificationController = toastNotificationController;
   }

   public void syncBetweenBatchModel(AppModel appModel, RoastingModel roastingModel) {
      BetweenBatchModel betweenBatchModel = appModel.getBetweenBatchModel();
      betweenBatchModel.setStartDate(new Date(roastingModel.getEffectiveEndDate().getTime() + 1000L));

      for (Channel channel : roastingModel.getChannels()) {
         CurveModel roastCurve = roastingModel.getCurveModel(channel).getRootModel();
         CurveModel betweenBatchCurveModel = betweenBatchModel.getCurveModel(channel);
         CurveModel betweenBatchRootCurveModel = betweenBatchCurveModel.getRootModel();
         Long startTime = betweenBatchModel.getStartDate().getTime();
         if (!betweenBatchRootCurveModel.getValues().isEmpty()) {
            if (startTime < betweenBatchRootCurveModel.getValues().firstKey()) {
               betweenBatchRootCurveModel.getValues().putAll(roastCurve.tailCurve(startTime));
               betweenBatchRootCurveModel.fireCurveChanged();
            } else if (roastingModel.getEffectiveEndDate().getTime() >= betweenBatchRootCurveModel.getValues().firstKey()) {
               Iterator<Long> it = betweenBatchRootCurveModel.getValues().keySet().iterator();

               while (it.hasNext()) {
                  if (it.next() <= roastingModel.getEffectiveEndDate().getTime()) {
                     it.remove();
                  }
               }

               betweenBatchRootCurveModel.fireCurveChanged();
            }
         }

         Optional.ofNullable(betweenBatchCurveModel.getModel(StartEndPointCurveModel.class)).ifPresent(model -> {
            AbstractCurveModel.addLowerEntry(startTime, roastCurve.getValues(), betweenBatchRootCurveModel.getValues());
            model.setStartTime(roastingModel.getEffectiveEndDate().getTime());
         });
         betweenBatchRootCurveModel.fireCurveChanged();
         if (!betweenBatchCurveModel.getValues().containsKey(startTime)) {
            Optional.ofNullable(AbstractCurveModel.lowerKey(roastCurve.getValues(), startTime))
               .ifPresent(lowerKey -> Optional.ofNullable(roastCurve.getValue(lowerKey)).ifPresent(value -> {
                     betweenBatchRootCurveModel.getValues().put(startTime, value);
                     betweenBatchRootCurveModel.fireCurveChanged();
                  }));
         }
      }
   }

   public void switchBetweenBatchLog(AppModel appModel, RoastingModel roastingModel) {
      UnsavedBetweenBatchLog currentBetweenBatchLog = appModel.getBetweenBatchLog();
      if (currentBetweenBatchLog != null) {
         currentBetweenBatchLog.setNextBatchDate(roastingModel.getEffectiveStartDate());
         if (!currentBetweenBatchLog.getPreviousBatchDate().equals(currentBetweenBatchLog.getNextBatchDate())) {
            BetweenBatchModel betweenBatchModel = currentBetweenBatchLog.getBetweenBatchModel();
            if (!betweenBatchModel.isStopped()) {
               long startTime = roastingModel.getEffectiveStartDate().getTime();
               betweenBatchModel.getChannels().forEach(channel -> {
                  CurveModel betweenBatchCurveModel = Optional.ofNullable(betweenBatchModel.getCurveModel(channel)).map(CurveModel::getRootModel).orElse(null);
                  CurveModel roastingCurveModel = Optional.ofNullable(roastingModel.getCurveModel(channel)).map(CurveModel::getRootModel).orElse(null);
                  if (betweenBatchCurveModel != null && roastingCurveModel != null) {
                     if (!roastingCurveModel.getValues().isEmpty()) {
                        SortedMap<Long, Double> roastingCurveAfterStart = roastingCurveModel.getValues().tailMap(startTime);
                        if (!roastingCurveAfterStart.isEmpty()) {
                           Long nextKeyAfterStart = roastingCurveAfterStart.firstKey();
                           betweenBatchCurveModel.getValues().putAll(roastingCurveModel.getValues().headMap(nextKeyAfterStart + 1000L));
                           betweenBatchCurveModel.fireCurveChanged();
                        }
                     }
                  }
               });
            }

            currentBetweenBatchLog.normalizeCurves(appModel);
            if (!currentBetweenBatchLog.isEmpty() && currentBetweenBatchLog.getDuration() <= 900000L) {
               try {
                  appModel.getRoastPersister().saveBetweenBatchLog(currentBetweenBatchLog);
               } catch (Exception var7) {
                  this.logger.error("Error saving between batch log", var7);
               }
            } else {
               this.logger.info("Discarding between batch log with duration {}", currentBetweenBatchLog.getDuration());
            }
         } else {
            this.logger.info("Discarding empty between batch log");
         }
      }

      Long betweenBatchLogStart = roastingModel.getEffectiveEndDate().getTime() + 1000L;
      currentBetweenBatchLog = new UnsavedBetweenBatchLog(roastingModel.getEffectiveStartDate());
      currentBetweenBatchLog.setStartDate(new Date(betweenBatchLogStart));
      appModel.setBetweenBatchLog(currentBetweenBatchLog);
   }

   public void updateBetweenBatchLog(UnsavedBetweenBatchLog betweenBatchLog, BetweenBatchModel betweenBatchModel) {
      betweenBatchLog.setStartDate(betweenBatchModel.getStartDate());
      betweenBatchLog.setBetweenBatchModel(betweenBatchModel);
      betweenBatchLog.getComments().addAll(betweenBatchModel.getComments());
   }

   public void initBetweenBatchModel(Date startDate) {
      Preferences prefs = this.appModel.getPreferences();
      BetweenBatchModel betweenBatchModel = new BetweenBatchModel(prefs, startDate, this.appModel.getActiveChannels(), this.appModel.getEnabledStates());
      this.appModel.setBetweenBatchModel(betweenBatchModel);
      this.measurementHub.addModel(betweenBatchModel);
      betweenBatchModel.addCommentListener(new CommentListener() {
         @Override
         public void commentAdded(RoastingComment comment) {
            BetweenBatchManager.this.mainPanelController.getBetweenBatchChart().addComment(comment, "beanTemperature");
         }

         @Override
         public void commentUpdated(RoastingComment comment) {
            BetweenBatchManager.this.mainPanelController.getBetweenBatchChart().updateComment(comment);
         }

         @Override
         public void commentRemoved(RoastingComment comment) {
            BetweenBatchManager.this.mainPanelController.getBetweenBatchChart().removeComment(comment, "beanTemperature");
         }
      });
      this.state.setBetweenBatchStopped(false);
   }

   public void finishBetweenBatchModel() {
      BetweenBatchModel betweenBatchModel = this.appModel.getBetweenBatchModel();
      if (betweenBatchModel != null) {
         this.measurementHub.removeModel(betweenBatchModel);
         if (this.appModel.getBetweenBatchLog() != null) {
            this.updateBetweenBatchLog(this.appModel.getBetweenBatchLog(), betweenBatchModel);
         }

         this.appModel.setBetweenBatchModel(null);
      }
   }

   public void resetBetweenBatchLog(ActionEvent ae) {
      if (!this.state.isBetweenBatchStopped()) {
         Alert alert = WindowUtil.createAlert(
            AlertType.WARNING,
            org.cropster.roastingintelligence.util.Messages.m_("Reset between batch timer"),
            org.cropster.roastingintelligence.util.Messages.m_(
               "Are you sure you want to continue?\n\nThe timer will start again from 00:00 and the between batch data recorded so far will be lost."
            ),
            "button--warning",
            ButtonType.CANCEL,
            ButtonType.OK
         );
         alert.getDialogPane().setMaxWidth(400.0);
         ((Button)alert.getDialogPane().lookupButton(ButtonType.OK)).setText(org.cropster.roastingintelligence.util.Messages.m_("Reset timer"));
         alert.resultProperty().addListener(e -> {
            if (ButtonType.OK.equals(alert.getResult())) {
               this.resetBetweenBatchLog();
            }
         });
         JavaFxUtil.applyButtonStyles(alert, "button--warning");
         WindowUtil.center(this.application.getStage(), alert).showAndWait();
      } else {
         this.resetBetweenBatchLog();
      }
   }

   private void resetBetweenBatchLog() {
      BetweenBatchModel bbModel = this.appModel.getBetweenBatchModel();
      bbModel.reset();
      this.state.setBetweenBatchStopped(false);
      this.toastNotificationController.hide("between-batch-tag");
      Preferences prefs = this.appModel.getPreferences();
      if (prefs.isSoundAlarmOnEventsEnabled()) {
         AlarmService alarmService = this.alarmController.getAlarmService();
         if (alarmService != null) {
            alarmService.attach(bbModel, RoastingPhase.BETWEEN_BATCHES);
         }
      }

      if (prefs.isSmartGoalsAndReferencesEnabled()) {
         LiveGoalService liveGoalService = this.liveGoalController.getLiveGoalService();
         if (liveGoalService != null) {
            PreparationPanel pPanel = this.mainPanelController.getPreparationPanel();
            liveGoalService.attach(new LiveGoalBetweenBatchModel(bbModel, pPanel.getStartWeightEditor()), RoastingPhase.BETWEEN_BATCHES);
         }
      }
   }

   public void stopBetweenBatchLog(ActionEvent ae) {
      this.stopBetweenBatchLog();
   }

   private void stopBetweenBatchLog() {
      this.state.setBetweenBatchStopped(true);
      this.appModel.getBetweenBatchModel().setStopped(true);
   }

   private void stopBetweenBatchLogAndNotify() {
      this.stopBetweenBatchLog();
      if (this.appModel.getPreferences().isBetweenBatchProtocolEnabled()) {
         this.notificationBusService
            .publish(
               Notification.builder(NotificationType.WARNING)
                  .withTitle(org.cropster.roastingintelligence.util.Messages.m_("Between batch time exceeded"))
                  .withMessage(
                     org.cropster.roastingintelligence.util.Messages.m_(
                        "The recording limit of {0} minutes was exceeded and the between batch protocol is now stopped.", 15L
                     )
                  )
                  .withProperty("tag", "between-batch-tag")
                  .withProperty(
                     "action",
                     new ActionBuilder(
                           org.cropster.roastingintelligence.util.Messages.m_("Restart between batch protocol"),
                           (Consumer<ActionEvent>)this::resetBetweenBatchLog
                        )
                        .build()
                  )
                  .build(),
               "between-batch"
            );
      }
   }

   public void switchToProgressPanel() {
      if (this.appModel.getBetweenBatchModel() != null) {
         this.toastNotificationController.hide("between-batch-tag");
         this.appModel.getBetweenBatchModel().removeCommentListener(this.mainPanelController.getCommentsSlidingPanel());
      }
   }

   public void switchToStartPanel() {
      Preferences prefs = this.appModel.getPreferences();
      BetweenBatchModel bbModel = this.appModel.getBetweenBatchModel();
      if (bbModel != null) {
         CommentPopupHeader header = new CommentPopupHeader();
         CommentPopupContent commentContent = new CommentPopupContent(prefs, bbModel, header);
         PopOverHandler<CommentPopupContent> betweenBatchEventHandler = new PopOverHandler<>(header, commentContent, this::showBetweenBatchCommentPopup);
         this.mainPanelController.setBetweenBatchChartHandler(betweenBatchEventHandler);
         this.mainPanelController.getCommentsSlidingPanel().init(bbModel);
      }

      this.mainPanelController.getCommentsSidebarButton().setVisible(prefs.isBetweenBatchProtocolEnabled());
      this.mainPanelController.getCommentsSidebarButton().setDisable(bbModel == null);
   }

   public void showBetweenBatchCommentPopup(PopOverHandler<CommentPopupContent> popup, Point2D position) {
      long time = this.appModel.getBetweenBatchModel().getDuration();
      List<String> events = new ArrayList<>(Arrays.asList("gas", "airflow"));
      events.retainAll(Arrays.asList(this.appModel.getPreferences().getEnabledEventsPopup()));
      CommentPopupContent commentPopupContent = popup.getContentNode();
      commentPopupContent.getTimeInput().setValue(time > 0L ? time : 0L);
      commentPopupContent.updateEventButtons(events.toArray(new String[0]));
      commentPopupContent.toFront();
      popup.show(this.mainPanelController.getBetweenBatchPanel(), position);
   }

   public void updateBetweenBatchTime() {
      if (this.appModel.getBetweenBatchModel() != null && !this.state.isBetweenBatchStopped()) {
         long betweenBatchTime = this.measurementHub.getTime() - this.appModel.getBetweenBatchModel().getStartDate().getTime();
         this.mainPanelController.getBetweenBatchTimerLabel().setTime(betweenBatchTime > 0L ? betweenBatchTime : 0L);
         if (betweenBatchTime > 900000L && !this.state.isBetweenBatchStopped()) {
            this.stopBetweenBatchLogAndNotify();
         }
      }
   }

   public void roastingStopped(RoastChartWrapper roastChartWrapper, RoastingModel roastingModel) {
      if (this.appModel.getPreferences().isBetweenBatchProtocolEnabled()) {
         long minBetweenBatchUpperBound = roastingModel.getDuration() / 1000L + 240L;
         if (roastChartWrapper.getChart().getTimeRangeUpperBound() < minBetweenBatchUpperBound) {
            roastChartWrapper.getChart().setTimeRangeUpperBound(minBetweenBatchUpperBound);
         }

         roastChartWrapper.getChart().setBetweenBatchAnnotation(roastingModel.getDuration(), roastingModel.getDuration() + 900000L);
      }
   }

   public void abortRoasting() {
      UnsavedBetweenBatchLog bbLog = this.appModel.getBetweenBatchLog();
      BetweenBatchModel bbModel = this.appModel.getBetweenBatchModel();
      Preferences prefs = this.appModel.getPreferences();
      if (bbLog == null) {
         if (bbModel != null) {
            this.finishBetweenBatchModel();
            this.mainPanelController.hideBetweenBatchTimer();
         }

         if (prefs.isSmartGoalsAndReferencesEnabled()) {
            PreparationPanel pPanel = this.mainPanelController.getPreparationPanel();
            this.liveGoalController
               .getLiveGoalService()
               .attach(new LiveGoalBetweenBatchModel(this.measurementHub, pPanel.getStartWeightEditor()), RoastingPhase.BETWEEN_BATCHES);
         }
      } else {
         if (bbModel == null) {
            Date startDate = new Date(MathUtil.floorLong(NanoTime.currentTimeMillis() + 1000L, 1000L));
            this.initBetweenBatchModel(startDate);
            bbModel = this.appModel.getBetweenBatchModel();
            if (prefs.isBetweenBatchProtocolEnabled()) {
               this.mainPanelController.showBetweenBatchTimer(0L);
            } else {
               this.mainPanelController.hideBetweenBatchTimer();
            }
         }

         if (prefs.isSoundAlarmOnEventsEnabled()) {
            AlarmService alarmService = this.alarmController.getAlarmService();
            if (alarmService != null) {
               alarmService.attach(bbModel, RoastingPhase.BETWEEN_BATCHES);
            }
         }

         if (prefs.isSmartGoalsAndReferencesEnabled()) {
            LiveGoalService liveGoalService = this.liveGoalController.getLiveGoalService();
            if (liveGoalService != null) {
               PreparationPanel pPanel = this.mainPanelController.getPreparationPanel();
               liveGoalService.attach(new LiveGoalBetweenBatchModel(bbModel, pPanel.getStartWeightEditor()), RoastingPhase.BETWEEN_BATCHES);
            }
         }
      }
   }

   public void resetChartAxes() {
      BetweenBatchChartWrapper wrapper = this.mainPanelController.getBetweenBatchPanel().getChartWrapper();
      if (wrapper != null) {
         ChartViewer viewer = wrapper.getChartViewer();
         if (viewer != null) {
            ChartUtils.restoreZoom(viewer);
         }
      }
   }

   public void settingsChanged(AppModel appModel) {
      if (appModel.getBetweenBatchModel() != null && appModel.getPreferences().isBetweenBatchProtocolEnabled()) {
         AlarmsUtil.addAlarmsBorderHandler(this.mainPanelController.getBetweenBatchPanel().getChartWrapper(), this.alarmController);
      }
   }
}
