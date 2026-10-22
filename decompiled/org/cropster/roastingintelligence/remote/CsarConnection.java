package org.cropster.roastingintelligence.remote;

import com.cropster.roastingintelligence.exception.BetaAccessDeniedException;
import com.cropster.roastingintelligence.remote.Authenticator;
import com.cropster.util.model.Unit;
import java.io.Closeable;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import org.cropster.roastingintelligence.model.Analysis;
import org.cropster.roastingintelligence.model.BetweenBatchLog;
import org.cropster.roastingintelligence.model.BlendProfile;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Group;
import org.cropster.roastingintelligence.model.GroupMember;
import org.cropster.roastingintelligence.model.Lot;
import org.cropster.roastingintelligence.model.LotConsistencyCheck;
import org.cropster.roastingintelligence.model.LotTransaction;
import org.cropster.roastingintelligence.model.Machine;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.Scheduled;
import org.cropster.roastingintelligence.model.UnsavedBetweenBatchLog;
import org.cropster.roastingintelligence.model.User;
import org.cropster.roastingintelligence.model.WorkerTask;

public interface CsarConnection extends Closeable {
   void init(Authenticator var1);

   void open();

   void invalidate();

   void log() throws BetaAccessDeniedException;

   URL createSupportTicket(String var1, String var2, File var3);

   User getUser(String var1);

   User getCurrentUser();

   GroupMember getCurrentUserGroupMember(String var1);

   Collection<Group> getGroups();

   String getGroupAccountManager(String var1);

   boolean isGroupOnHold(String var1);

   Machine getMachine(Long var1);

   Collection<Machine> getMachines();

   Collection<String> getRoastPersons(Machine var1);

   Collection<Profile> getProfiles(Long var1);

   Lot getLot(Long var1);

   Collection<Lot> getLots(Machine var1, boolean var2);

   void updateLot(Lot var1);

   Roast getProcessingRecord(Long var1, Unit var2);

   Collection<BetweenBatchLog> getBetweenBatchLogs(Collection<Long> var1);

   Analysis getLatestCuppingAnalysis(Long var1);

   Analysis getLatestPhysicalAnalysis(Long var1);

   Collection<Scheduled> getSchedules(Long var1);

   Collection<Goal> getGoals(String var1);

   Collection<LotConsistencyCheck> getLotConsistencyChecks(String var1, Collection<Long> var2);

   void markReference(Long var1, Long var2);

   boolean updateScheduled(Scheduled var1, Lot var2);

   boolean updateScheduled(Scheduled var1);

   Lot startRoast(Roast var1);

   void finishRoast(Roast var1);

   void update(Roast var1);

   void update(WorkerTask var1);

   void storeBetweenBatchLog(UnsavedBetweenBatchLog var1);

   Collection<WorkerTask> getTasks(Long var1);

   int getCurvePredictionInterval();

   double[] getCurvePrediction(Machine var1, Measure var2, Unit var3, SortedMap<Long, Double> var4, SortedMap<Long, Double> var5);

   long[] getFirstCrackPrediction(Machine var1, Measure var2, Unit var3, SortedMap<Long, Double> var4);

   Double getFlickPrediction(Machine var1, Measure var2, Unit var3, SortedMap<Long, Double> var4, SortedMap<Long, Double> var5);

   String loadPreferences(long var1);

   String loadAlarmPreferences();

   String loadLiveGoalPreferences();

   void storeUserPreferences(String var1);

   void storeMachinePreferences(long var1, String var3);

   void storeAlarmPreferences(String var1);

   void storeLiveGoalPreferences(String var1);

   Lot createManualBlend(String var1, String var2, List<LotTransaction> var3);

   Lot createBlendWithProfile(long var1, String var3, List<LotTransaction> var4);

   Collection<Lot> getBlends(String var1, String var2);

   Collection<BlendProfile> getBlendProfiles(String var1);
}
