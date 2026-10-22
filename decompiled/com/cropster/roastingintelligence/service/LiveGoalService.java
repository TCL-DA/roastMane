package com.cropster.roastingintelligence.service;

import com.cropster.roastingintelligence.listener.ActiveGoalsChangeListener;
import com.cropster.roastingintelligence.listener.LiveGoalsChangeListener;
import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import com.cropster.roastingintelligence.model.livegoals.Checker;
import com.cropster.roastingintelligence.model.livegoals.LiveGoal;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalDisplayMode;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalType;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.cropster.roastingintelligence.AppModel;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;

public interface LiveGoalService {
   String OTHER_CHARGE_TEMPERATURES = "chargeTemperature-";
   String OTHER_END_TEMPERATURES = "endTemperature-";
   String DIRTY_PROPERTY = "dirty";
   String DIRTY_NOTIFICATION_ENABLED_PROPERTY = "dirtyNotificationEnabled";
   String SUSPENDED_PROPERTY = "suspended";

   default void addLiveGoals(LiveGoal... liveGoals) {
      this.addLiveGoals(List.of(liveGoals));
   }

   void addLiveGoals(Collection<LiveGoal> var1);

   default boolean removeLiveGoals(LiveGoal... liveGoals) {
      return this.removeLiveGoals(List.of(liveGoals));
   }

   boolean removeLiveGoals(Collection<LiveGoal> var1);

   void clearLiveGoals();

   List<LiveGoal> getLiveGoals();

   default void setLiveGoals(LiveGoal... liveGoals) {
      this.setLiveGoals(List.of(liveGoals));
   }

   void setLiveGoals(Collection<LiveGoal> var1);

   default boolean replaceLiveGoals(LiveGoal... liveGoals) {
      return this.replaceLiveGoals(List.of(liveGoals));
   }

   boolean replaceLiveGoals(Collection<LiveGoal> var1);

   boolean moveLiveGoalAfter(String var1, String var2);

   boolean moveLiveGoalBefore(String var1, String var2);

   default boolean containsLiveGoal(LiveGoal liveGoal) {
      return liveGoal != null && this.containsLiveGoal(liveGoal.getId());
   }

   boolean containsLiveGoal(String var1);

   List<LiveGoal> createDefaultLiveGoals();

   Map<String, Set<LiveGoalDisplayMode>> getSupportedDisplayModes();

   Map<String, String> getLocalizedNames();

   List<LiveGoalService.ActiveGoal> getActiveGoals();

   void clearActiveGoals();

   void forceEvaluation(Roast var1);

   boolean isDirty();

   boolean isDirtyNotificationEnabled();

   void setDirtyNotificationEnabled(boolean var1);

   void setDirtyNotificationEnabled(boolean var1, boolean var2);

   void importFrom(Collection<LiveGoal> var1);

   List<LiveGoal> exportTo();

   default void attach(MeasurementModel model, RoastingPhase phase) {
      this.attach(model, phase, false);
   }

   void attach(MeasurementModel var1, RoastingPhase var2, boolean var3);

   void detach();

   boolean isAttached();

   void refresh();

   void suspend();

   void resume();

   boolean isSuspended();

   double getWarningThreshold();

   void setWarningThreshold(double var1);

   default void setCurrentProfile(AppModel appModel) {
      Objects.requireNonNull(appModel, "Null 'appModel' parameter.");
      this.setCurrentProfile(appModel.getSelectedProfile(), appModel.getCurrentReference(), appModel.getData().getGoals());
   }

   void setCurrentProfile(Profile var1, Roast var2, Map<Long, Collection<Goal>> var3);

   void addLiveGoalsChangeListener(LiveGoalsChangeListener var1);

   void removeLiveGoalsChangeListener(LiveGoalsChangeListener var1);

   void addActiveGoalsChangeListener(ActiveGoalsChangeListener var1);

   void removeActiveGoalsChangeListener(ActiveGoalsChangeListener var1);

   void addPropertyChangeListener(PropertyChangeListener var1);

   void removePropertyChangeListener(PropertyChangeListener var1);

   void addPropertyChangeListener(String var1, PropertyChangeListener var2);

   void removePropertyChangeListener(String var1, PropertyChangeListener var2);

   public abstract static class ActiveGoal {
      private final Checker checker;
      private final Goal goal;
      private final LiveGoal liveGoal;
      private final Profile profile;
      private CheckResult lastCheckResult;

      protected ActiveGoal(Profile profile, LiveGoal liveGoal, Goal goal, Checker checker) {
         this.profile = profile;
         this.liveGoal = liveGoal;
         this.goal = goal;
         this.checker = checker;
         this.lastCheckResult = goal != null ? CheckResult.initialized(goal) : null;
      }

      public Checker getChecker() {
         return this.checker;
      }

      public Goal getGoal() {
         return this.goal;
      }

      public CheckResult getLastCheckResult() {
         return this.lastCheckResult;
      }

      public void setLastCheckResult(CheckResult lastCheckResult) {
         this.lastCheckResult = lastCheckResult;
      }

      public LiveGoal getLiveGoal() {
         return this.liveGoal;
      }

      public Profile getProfile() {
         return this.profile;
      }

      public Measure getReferenceValue() {
         return null;
      }

      public LiveGoalType getType() {
         return this.liveGoal.getType();
      }

      public void resetCheckResult() {
         this.lastCheckResult = CheckResult.initialized(this.goal);
      }

      public void updateCheckResult(Measure actualValue, Measure referenceValue) {
         if (this.lastCheckResult.getEvaluationStatus() != CheckResult.EvaluationStatus.COMPLETED) {
            this.lastCheckResult = this.checker.evaluate(this.goal, actualValue, referenceValue);
         }
      }

      @Override
      public String toString() {
         return this.liveGoal.getId();
      }
   }
}
