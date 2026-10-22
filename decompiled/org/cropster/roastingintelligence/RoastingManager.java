package org.cropster.roastingintelligence;

import com.cropster.roastingintelligence.ui.JavaFxUtil;
import com.cropster.roastingintelligence.util.RenamingThreadFactory;
import com.cropster.util.Collect;
import com.cropster.util.model.Unit;
import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import javafx.application.Platform;
import local.OfflinePersistence;
import org.apache.commons.lang3.StringUtils;
import org.cropster.roastingintelligence.listener.AbstractMessagePublisher;
import org.cropster.roastingintelligence.listener.MessageListener;
import org.cropster.roastingintelligence.listener.RoastingListener;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingAnalyzer;
import org.cropster.roastingintelligence.model.RoastingAnalyzerListener;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;
import org.cropster.roastingintelligence.model.impl.AbstractCurveModel;
import org.cropster.roastingintelligence.model.impl.LinearInterpolationCurveModel;
import org.cropster.roastingintelligence.model.impl.RelativeCurveModel;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.service.RoastPersister;
import org.cropster.roastingintelligence.service.impl.JsonWriter;
import org.cropster.roastingintelligence.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoastingManager extends AbstractMessagePublisher {
   private final Logger logger = LoggerFactory.getLogger(RoastingManager.class);
   private final ScheduledExecutorService _executor = Executors.newScheduledThreadPool(3, new RenamingThreadFactory("RoastingManager"));
   private final CopyOnWriteArraySet<RoastingListener> _roastingListeners = new CopyOnWriteArraySet<>();
   private RoastPersister _roastPersister;
   private CsarConnection _connection;

   public ScheduledExecutorService getExecutor() {
      return this._executor;
   }

   public void setRoastPersister(RoastPersister roastPersister) {
      this._roastPersister = roastPersister;
   }

   public void setConnection(CsarConnection connection) {
      this._connection = connection;
   }

   private void unregisterRoastingAnalyzers() {
      for (RoastingListener roastingListener : this._roastingListeners) {
         if (roastingListener instanceof RoastingAnalyzer) {
            this.removeRoastingListener(roastingListener);
         }
      }
   }

   private void updateScheduled(Scheduled scheduled) {
      if (scheduled == null) {
         throw new IllegalArgumentException("scheduled must not be null");
      } else {
         this._executor.submit(() -> {
            try {
               this._connection.updateScheduled(scheduled);
            } catch (Throwable var3) {
               if (this.logger.isInfoEnabled()) {
                  this.logger.info("Error marking scheduled done", var3);
               } else if (this.logger.isDebugEnabled()) {
                  this.logger.debug("Error marking scheduled done", var3);
               }
            }
         });
      }
   }

   public RoastingModel startRoasting(AppModel appModel, MeasurementModel baseModel, Number startWeight, RoastingAnalyzerListener analyzerListener) {
      this.logger.info("Roasting started");
      RoastingModel roastingModel = new RoastingModel(
         appModel.getPreferences(), appModel.getData().clone(), appModel.getActiveChannels(), appModel.getEnabledStates()
      );
      Channel channel = appModel.getChannel("beanTemperature");
      if (channel != null && channel.isActive()) {
         CurveModel curveModel = roastingModel.getCurveModel(channel).getRootModel();
         double startThreshold = appModel.getStartThreshold(curveModel.getUnit());
         double turningPointThreshold = appModel.getTurningPointThreshold(curveModel.getUnit());
         double endThreshold = appModel.getEndThreshold(curveModel.getUnit());
         RoastingAnalyzer roastingAnalyzer = new RoastingAnalyzer(roastingModel, analyzerListener);
         roastingAnalyzer.setTurningPointOnly(!AppModel.AutoStartMode.AUTO_DETECTION.equals(appModel.getAutoStartMode()));
         roastingAnalyzer.setStartThreshold(startThreshold);
         roastingAnalyzer.setTurningPointThreshold(turningPointThreshold);
         roastingAnalyzer.setEndThreshold(endThreshold);
         this.logger
            .info("Analyzing roast with thresholds: start = {}, turningPoint = {}, end = {}", new Object[]{startThreshold, turningPointThreshold, endThreshold});
         this.addRoastingListener(roastingAnalyzer);
         curveModel.addListener(roastingAnalyzer);
      }

      if (!AppModel.AutoStartMode.AUTO_DETECTION.equals(appModel.getAutoStartMode())) {
         this.forceStart(roastingModel, roastingModel.getInitialDate());
      }

      Roast roast = roastingModel.getRoast();
      roast.setMachine(appModel.getSelectedMachine().getId());
      roast.setWorker(appModel.getWorker());
      roast.setProfile(appModel.getSelectedProfileId());
      roast.setStartWeight(new Measure(startWeight.doubleValue(), appModel.getPreferences().getWeightUnit()));
      Scheduled scheduled = appModel.getCurrentScheduled();
      if (scheduled != null) {
         scheduled.setDone(true);
         this.updateScheduled(scheduled);
         roastingModel.setScheduled(scheduled);
         roast.setScheduledId(scheduled.getId());
         if (StringUtils.isNotBlank(scheduled.getRoastingPerson())) {
            roast.setWorker(scheduled.getRoastingPerson());
         }
      }

      this.fireRoastStarted(roastingModel);
      return roastingModel;
   }

   public Date getRoundedStartDate(RoastingModel roastingModel, Date startDate) {
      long startMillis = startDate.getTime();
      long startRounded = startMillis;

      for (Channel channel : roastingModel.getChannels()) {
         SortedMap<Long, ?> values = roastingModel.getCurveModel(channel).getRootModel().getValues();
         Long nearestKey = Collect.proximate(values, startMillis, 1500L);
         if (nearestKey != null) {
            startRounded = Math.min(startRounded, nearestKey);
         }
      }

      return new Date(startRounded);
   }

   public void updateStartDateOfCurveModels(RoastingModel roastingModel, Date startDateRounded) {
      roastingModel.getRoast().setStartDate(startDateRounded);
      roastingModel.setStartDate(startDateRounded);

      for (Channel channel : roastingModel.getChannels()) {
         CurveModel model = roastingModel.getCurveModel(channel);
         RelativeCurveModel relativeModel = model.getModel(RelativeCurveModel.class);
         if (relativeModel != null && roastingModel.getStartDate() != null && !relativeModel.getZeroPoint().equals(roastingModel.getStartDate())) {
            LoggerFactory.getLogger(RelativeCurveModel.class).info("ROAST START");
            relativeModel.setZeroPoint(roastingModel.getStartDate());
         }

         model.getRootModel().fireCurveChanged();
      }

      roastingModel.updateStartTimeOfCurveModels();
      this.fireRoastForced(roastingModel);
   }

   public void forceStart(RoastingModel roastingModel, Date startDate) {
      Date startDateRounded = this.getRoundedStartDate(roastingModel, startDate);
      Platform.runLater(() -> this.updateStartDateOfCurveModels(roastingModel, startDateRounded));
   }

   public Date getCurrentEndDate(RoastingModel roastingModel) {
      boolean hasInterpolatedCurves = roastingModel.getCurves()
         .values()
         .stream()
         .map(model -> model.getModel(LinearInterpolationCurveModel.class))
         .anyMatch(Objects::nonNull);
      return hasInterpolatedCurves ? DateUtil.newDateTruncated(3L) : roastingModel.getCurrentEndDate();
   }

   public void stopRoasting(RoastingModel roastingModel) {
      this.logger.info("Roasting stopped");
      this.unregisterRoastingAnalyzers();
      Roast roast = roastingModel.getRoast();
      if (roastingModel.getStartDate() == null) {
         roastingModel.resetStartDate();
      }

      if (roastingModel.getEndDate() == null) {
         roastingModel.setEndDate(this.getCurrentEndDate(roastingModel));
      }

      roast.setEndWeight(roast.getStartWeight());
      if (roast.getProfile() != null) {
         Profile profile = roastingModel.getData().getProfiles().get(roast.getProfile());
         if (profile != null && profile.getWeightChange() != null) {
            BigDecimal endWeight = BigDecimal.valueOf(roast.getStartWeight().getAmount());
            endWeight = endWeight.add(endWeight.multiply(profile.getWeightChange()));
            roast.setEndWeight(new Measure(endWeight.doubleValue(), roast.getStartWeight().getUnit()));
         }
      }

      if (roastingModel.getScheduled() != null) {
         roast.setNotes(roastingModel.getScheduled().getNotes());
      }

      this.fireRoastStopped(roastingModel);
   }

   public void abortRoasting(RoastingModel roastingModel) {
      this.logger.info("Roasting aborted");
      this.unregisterRoastingAnalyzers();
      if (roastingModel.getRoast().getLot().getId() != null) {
      }

      Scheduled scheduled = roastingModel.getScheduled();
      if (scheduled != null) {
         scheduled.setDone(false);
         this.updateScheduled(scheduled);
      }

      this.fireRoastAborted(roastingModel);
   }

   public Roast finalizeRoasting(AppModel appModel, RoastingModel roastingModel, Double endWeight, String notes) {
      this.logger.info("Roasting finished");
      Roast roast = roastingModel.getRoast();
      roast.getLot().getSources().clear();
      roast.getLot().getSources().addAll(roastingModel.createTransactions());
      roast.setEndWeight(new Measure(endWeight, roast.getStartWeight().getUnit()));
      roast.setNotes(notes);
      roast.setStartDate(roastingModel.getEffectiveStartDate());
      roast.setEndDate(roastingModel.getEffectiveEndDate());
      if (roastingModel.getDevelopmentTime() != null) {
         double seconds = (double)roastingModel.getDevelopmentTime().longValue() / 1000.0;
         Measure developmentTime = new Measure(seconds, Unit.SECOND);
         roast.getMeasures().put("developmentTime", developmentTime);
         Measure developmentTimeRatio = roastingModel.getDevelopmentTimeRatio();
         roast.getMeasures().put("developmentTimeRatio", developmentTimeRatio);
      }

      long startTime = roastingModel.getEffectiveStartDate().getTime() + 1000L;
      UnsavedBetweenBatchLog betweenBatchLog = appModel.getBetweenBatchLog();
      if (betweenBatchLog != null && betweenBatchLog.getBetweenBatchModel() != null) {
         BetweenBatchModel betweenBatchModel = betweenBatchLog.getBetweenBatchModel();

         for (Channel channel : betweenBatchModel.getChannels()) {
            CurveModel bbmCurveModel = betweenBatchModel.getCurveModel(channel);
            CurveModel roastingCurveModel = roastingModel.getCurveModel(channel);
            if (bbmCurveModel != null && roastingCurveModel != null) {
               AbstractCurveModel.addLowerEntry(startTime, bbmCurveModel.getRootModel().getValues(), roastingCurveModel.getRootModel().getValues());
            }
         }
      }

      for (Channel channelx : roastingModel.getChannels()) {
         Curve croppedCurve = roastingModel.getCroppedCurve(channelx);
         Curve curve = new Curve(croppedCurve.getUnit());

         for (Entry<Long, Double> entry : croppedCurve.entrySet()) {
            long time = entry.getKey() - roastingModel.getEffectiveStartDate().getTime();
            curve.put(Long.valueOf(time), entry.getValue());
         }

         roast.getCurves().put(channelx.getName(), curve);
      }

      for (RoastingComment comment : roastingModel.getCroppedComments()) {
         long time = roastingModel.getEffectiveTime(comment.getTime());
         roast.addComment(new RoastingComment(time, comment.getEvent(), comment.getText()));
      }

      try {
         File file = this._roastPersister.saveNew(roastingModel.getRoast());
         this.logger.info("Saved roast to file {}", file.getName());
         this.fireRoastFinalized(roastingModel);
      } catch (Exception var22) {
         this.logger.error("Error persisting finished roast", var22);
         String clipboardMessage = "";

         try {
            StringWriter writer = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(appModel.getVersion());
            jsonWriter.write(writer, roast);
            JavaFxUtil.copyToClipboard(writer.toString());
            clipboardMessage = "\n"
               + org.cropster.roastingintelligence.util.Messages.m_(
                  "The roasting data has been copied to the system clipboard! Save it to a file to preserve it."
               );
         } catch (Throwable var21) {
            this.logger.error("Error copying roast to clipboard", var21);
         }

         this.publishMessage(
            MessageListener.MessageType.CRITICAL,
            org.cropster.roastingintelligence.util.Messages.m_(
                  "Sorry, could not save roasting data. You may not have write access to the data directory. Please check."
               )
               + clipboardMessage
         );
      } finally {
         ;
      }

      OfflinePersistence.verifySaved(roast);
      return roast;
   }

   public double[] predictCurve(Machine machine, Measure startWeight, Unit temperatureUnit, SortedMap<Long, Double> beanCurve, SortedMap<Long, Double> gasCurve) {
      return this._connection.getCurvePrediction(machine, startWeight, temperatureUnit, beanCurve, gasCurve);
   }

   public long[] predictFirstCrack(Machine machine, Measure startWeight, Unit temperatureUnit, SortedMap<Long, Double> beanCurve) {
      return this._connection.getFirstCrackPrediction(machine, startWeight, temperatureUnit, beanCurve);
   }

   public Double predictFlick(Machine machine, Measure startWeight, Unit temperatureUnit, SortedMap<Long, Double> beanCurve, SortedMap<Long, Double> gasCurve) {
      return this._connection.getFlickPrediction(machine, startWeight, temperatureUnit, beanCurve, gasCurve);
   }

   public void addRoastingListener(RoastingListener listener) {
      this._roastingListeners.add(listener);
   }

   public void removeRoastingListener(RoastingListener listener) {
      this._roastingListeners.remove(listener);
   }

   protected void fireRoastStarted(RoastingModel roastingModel) {
      for (RoastingListener listener : this._roastingListeners) {
         listener.roastingStarted(roastingModel);
      }
   }

   protected void fireRoastForced(RoastingModel roastingModel) {
      for (RoastingListener listener : this._roastingListeners) {
         listener.roastingForced(roastingModel);
      }
   }

   protected void fireRoastAborted(RoastingModel roastingModel) {
      for (RoastingListener listener : this._roastingListeners) {
         listener.roastingAborted(roastingModel);
      }
   }

   protected void fireRoastStopped(RoastingModel roastingModel) {
      for (RoastingListener listener : this._roastingListeners) {
         listener.roastingStopped(roastingModel);
      }
   }

   protected void fireRoastFinalized(RoastingModel roastingModel) {
      for (RoastingListener listener : this._roastingListeners) {
         listener.roastingFinalized(roastingModel);
      }
   }
}
