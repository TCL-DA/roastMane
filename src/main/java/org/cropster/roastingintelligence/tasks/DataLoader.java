package org.cropster.roastingintelligence.tasks;

import com.cropster.roastingintelligence.Data;
import com.cropster.roastingintelligence.ui.PermissionNotificationController;
import com.cropster.util.Collect;
import com.cropster.util.model.Unit;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.listener.AbstractMessagePublisher;
import org.cropster.roastingintelligence.listener.MessageListener;
import org.cropster.roastingintelligence.model.Analysis;
import org.cropster.roastingintelligence.model.BetweenBatchLog;
import org.cropster.roastingintelligence.model.BlendComponent;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotConsistencyCheck;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.remote.CsarConnection;
import org.cropster.roastingintelligence.util.CollectionUtil;
import org.cropster.roastingintelligence.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataLoader extends AbstractMessagePublisher implements Runnable {
   private final Logger logger = LoggerFactory.getLogger(DataLoader.class);
   private final CsarConnection _connection;
   private final Long _machineId;
   private final AppModel _appModel;

   public DataLoader(CsarConnection connection, Long machineId, AppModel appModel) {
      this._connection = connection;
      this._machineId = machineId;
      this._appModel = appModel;
   }

   private void pimpReference(Roast roast) {
      roast.addComment(new RoastingComment(roast.getDuration(), "end", null));
      Curve gasCurve = roast.getCurves().get("gas");
      if (gasCurve != null) {
         NumberFormat percentFormat = NumberFormat.getPercentInstance();
         percentFormat.setRoundingMode(RoundingMode.HALF_EVEN);
         percentFormat.setMinimumFractionDigits(0);
         percentFormat.setMaximumFractionDigits(2);
         Double lastValue = null;
         Set<RoastingComment> comments = new LinkedHashSet<>();

         for (Entry<Long, Double> entry : gasCurve.entrySet()) {
            if (lastValue == null || Math.abs(entry.getValue() - lastValue) > 0.0) {
               Long time = entry.getKey();
               if (roast.getComment(time) != null) {
                  if (time >= 1000L && roast.getComment(time - 1000L) == null) {
                     time = time - 1000L;
                  } else {
                     if (roast.getComment(time + 1000L) != null) {
                        continue;
                     }

                     time = time + 1000L;
                  }
               }

               RoastingComment gasComment = new RoastingComment(time, "gas", percentFormat.format(entry.getValue() / 100.0));
               comments.add(gasComment);
               lastValue = entry.getValue();
            }
         }

         if (comments.size() <= this._appModel.getPreferences().getGasCurveAsCommentsThreshold()) {
            for (RoastingComment comment : comments) {
               roast.addComment(comment);
            }
         }
      }
   }

   @Override
   public void run() {
      long syncStartTime = NanoTime.currentTimeMillis();
      Machine machine = this._appModel.getData().getMachines().get(this._machineId);
      if (machine == null) {
         machine = this._connection.getMachine(this._machineId);
      }

      if (machine != null) {
         this.logger.info("Using machine {}{}", machine, machine.getType() != null ? " (" + machine.getType() + ")" : "");
      }

      String groupCode = machine.getGroup().getCode();
      if (this._connection.isGroupOnHold(groupCode)) {
         String message = org.cropster.roastingintelligence.util.Messages.m_("Your account has been put on hold!")
            + "\n\n"
            + org.cropster.roastingintelligence.util.Messages.m_(
               "We have recently attempted to contact your business regarding your Cropster account multiple times, but have yet to receive a reply. Please contact {0} for assistance in quickly regaining access to your account. If you are reading this message outside US business hours (7:00 am-5:00 pm PST) or EU business hours (8:00 am-6:00 pm CET) please copy {1} in your email to our accounting department.",
               "mailto:accounting@cropster.com?subject=Acct%20hold%20" + groupCode,
               "mailto:sales@cropster.com?cc=accounting@cropster.com&subject=Acct%20hold%20" + groupCode
            )
            + "\n\n"
            + org.cropster.roastingintelligence.util.Messages.m_(
               "Please note, all of your data is intact and will be instantly accessible once this issue is resolved. We value our relationship with you tremendously and apologize in advance for any interruption to your business activities."
            );
         this.publishMessage(MessageListener.MessageType.ERROR, message);
         this._appModel.setData(new Data());
         this._appModel.fireCsarDataReceived();
      } else {
         Data data = new Data(this._appModel.getData());
         data.setWorkers(this._connection.getRoastPersons(machine));
         data.setProfiles(CollectionUtil.toMap(this._connection.getProfiles(this._machineId)));
         data.setLots(CollectionUtil.toMap(this._connection.getLots(machine, machine.isForSamples())));
         PermissionNotificationController.updateSingleGroupMember(data, groupCode, this._connection);
         Collection<Long> syncedRoastIds = new ArrayList<>();
         Lot lot = null;

         for (Roast roast : this._appModel.getRecentRoasts()) {
            lot = roast.getLot();
            if (lot != null && lot.getId() != null) {
               syncedRoastIds.add(lot.getId());
            }
         }

         Set<Long> referenceIds = new LinkedHashSet<>();
         Map<Long, Roast> referencesByProfile = new LinkedHashMap<>();
         Map<Long, BetweenBatchLog> betweenBatchLogsByLot = new HashMap<>();
         Map<Long, Analysis> analysesByLot = new LinkedHashMap<>();
         Map<Long, Collection<Goal>> goalMap = new HashMap<>();

         for (Profile profile : data.getProfiles().values()) {
            goalMap.put(profile.getId(), new LinkedHashSet<>());
            if (profile.getReferenceId() != null) {
               referenceIds.add(profile.getReferenceId());
               Roast previousReference = this._appModel.getData().getReferences().get(profile.getId());
               if (previousReference != null
                  && profile.getReferenceId().equals(previousReference.getLot().getId())
                  && !profile.getId().equals(this._appModel.getSelectedProfileId())
                  && profile.getReferenceLastModified() != null
                  && profile.getReferenceLastModified().getTime() < this._appModel.getSyncTime()) {
                  referencesByProfile.put(profile.getId(), previousReference);
               } else {
                  this.logger.info("Fetching reference for profile {}", profile.getName());
                  Roast referenceRoast = this._connection
                     .getProcessingRecord(profile.getReferenceId(), this._appModel.getPreferences().getPreferredUnit(Unit.CELSIUS));
                  if (referenceRoast != null) {
                     this.pimpReference(referenceRoast);
                     referencesByProfile.put(profile.getId(), referenceRoast);
                     Analysis latestCuppingAnalysis = this._connection.getLatestCuppingAnalysis(referenceRoast.getLot().getId());
                     analysesByLot.put(referenceRoast.getLot().getId(), latestCuppingAnalysis);
                  }

                  for (BlendComponent component : profile.getComponents()) {
                     if (!component.getLotIds().isEmpty()) {
                        Long lotId = (Long)Collect.first(component.getLotIds());
                        Analysis latestPhysicalAnalysis = this._connection.getLatestPhysicalAnalysis(lotId);
                        analysesByLot.put(lotId, latestPhysicalAnalysis);
                     }
                  }
               }
            }
         }

         data.setReferences(referencesByProfile);
         data.setAnalyses(analysesByLot);
         List<Long> referenceIdsToUpdate = new LinkedList<>();

         for (Long referenceId : referenceIds) {
            if (this._appModel.getData().getBetweenBatchLogs().containsKey(referenceId)) {
               betweenBatchLogsByLot.put(referenceId, this._appModel.getData().getBetweenBatchLogs().get(referenceId));
            } else {
               referenceIdsToUpdate.add(referenceId);
            }
         }

         this.logger.info("Fetching between batch logs for {} references", referenceIdsToUpdate.size());

         while (!referenceIdsToUpdate.isEmpty()) {
            int splitIndex = Math.min(100, referenceIdsToUpdate.size());
            List<Long> referenceIdsToFetch = referenceIdsToUpdate.subList(0, splitIndex);
            referenceIdsToUpdate = referenceIdsToUpdate.subList(splitIndex, referenceIdsToUpdate.size());
            Collection<BetweenBatchLog> fetchedBetweenBatchLogs = this._connection.getBetweenBatchLogs(referenceIdsToFetch);

            for (BetweenBatchLog bbl : fetchedBetweenBatchLogs) {
               betweenBatchLogsByLot.put(bbl.getNextBatch(), bbl);
            }

            this.logger.info("Fetched {} between batch logs", fetchedBetweenBatchLogs.size());
         }

         data.setBetweenBatchLogs(betweenBatchLogsByLot);

         for (Goal goal : this._connection.getGoals(groupCode)) {
            if (goal.isGlobal()) {
               for (Entry<Long, Collection<Goal>> goalsOfProfile : goalMap.entrySet()) {
                  if (goal.getProfiles() == null || !goal.getProfiles().contains(goalsOfProfile.getKey())) {
                     goalsOfProfile.getValue().add(goal);
                  }
               }
            } else if (goal.getProfiles() != null) {
               for (Long profileId : goal.getProfiles()) {
                  if (goalMap.containsKey(profileId)) {
                     goalMap.get(profileId).add(goal);
                  }
               }
            }
         }

         data.setGoals(goalMap);
         Collection<LotConsistencyCheck> checks = this._connection.getLotConsistencyChecks(machine.getGroup().getCode(), syncedRoastIds);
         if (CollectionUtils.isNotEmpty(checks)) {
            Map<Long, LotConsistencyCheck> checksByLot = checks.stream().collect(Collectors.toMap(LotConsistencyCheck::getLot, Function.identity()));
            data.setChecks(checksByLot);
         }

         Collection<Scheduled> schedules = this._connection.getSchedules(this._machineId);
         this._appModel.setScheduledRoasts(schedules);
         if (this._appModel.getPreferences().isCurvePredictionEnabled()) {
            int curvePredictionInterval = this._connection.getCurvePredictionInterval();
            if (curvePredictionInterval >= 0) {
               this._appModel.getPreferences().setCurvePredictionRequestInterval(curvePredictionInterval);
            }
         }

         this._appModel.setSyncTime(syncStartTime);
         this._appModel.setData(data);
         this.logger.info("Data synced from server");
         this._appModel.fireCsarDataReceived();
      }
   }
}
