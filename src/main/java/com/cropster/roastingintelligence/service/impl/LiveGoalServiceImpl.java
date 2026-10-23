package com.cropster.roastingintelligence.service.impl;

import com.cropster.roastingintelligence.listener.ActiveGoalsChangeEvent;
import com.cropster.roastingintelligence.listener.ActiveGoalsChangeListener;
import com.cropster.roastingintelligence.listener.LiveGoalsChangeEvent;
import com.cropster.roastingintelligence.listener.LiveGoalsChangeListener;
import com.cropster.roastingintelligence.model.RoastingPhase;
import com.cropster.roastingintelligence.model.livegoals.CheckResult;
import com.cropster.roastingintelligence.model.livegoals.Checker;
import com.cropster.roastingintelligence.model.livegoals.Checkers;
import com.cropster.roastingintelligence.model.livegoals.LiveGoal;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalDisplayMode;
import com.cropster.roastingintelligence.model.livegoals.LiveGoalType;
import com.cropster.roastingintelligence.model.livegoals.checkers.AbstractCurveChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.DurationChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.FirstCrackChecker;
import com.cropster.roastingintelligence.model.livegoals.checkers.WeightLossChecker;
import com.cropster.roastingintelligence.service.LiveGoalService;
import com.cropster.util.model.Unit;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.event.EventListenerSupport;
import org.apache.commons.lang3.tuple.Pair;
import org.cropster.roastingintelligence.listener.CurveModelListener;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.CommentListener;
import org.cropster.roastingintelligence.model.Commentable;
import org.cropster.roastingintelligence.model.Curve;
import org.cropster.roastingintelligence.model.CurveModel;
import org.cropster.roastingintelligence.model.Goal;
import org.cropster.roastingintelligence.model.Measure;
import org.cropster.roastingintelligence.model.MeasurementModel;
import org.cropster.roastingintelligence.model.Profile;
import org.cropster.roastingintelligence.model.Roast;
import org.cropster.roastingintelligence.model.RoastingComment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LiveGoalServiceImpl implements LiveGoalService {
   private final Logger logger = LoggerFactory.getLogger(LiveGoalServiceImpl.class);
   private static final Map<String, Set<LiveGoalDisplayMode>> SUPPORTED_DISPLAY_MODES_CACHE;
   private static final Map<String, String> LOCALIZED_NAMES_CACHE;
   private final List<LiveGoal> liveGoals = new ArrayList<>();
   private final List<LiveGoalService.ActiveGoal> activeGoals = new ArrayList<>();
   private final EventListenerSupport<LiveGoalsChangeListener> liveGoalsChangeListeners = EventListenerSupport.create(LiveGoalsChangeListener.class);
   private final EventListenerSupport<ActiveGoalsChangeListener> activeGoalsChangeListeners = EventListenerSupport.create(ActiveGoalsChangeListener.class);
   private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
   private final List<Goal> currentProfileGoals = new LinkedList<>();
   private Profile currentProfile;
   private Roast currentProfileReference;
   private final AtomicBoolean dirty = new AtomicBoolean(false);
   private final AtomicBoolean dirtyNotificationEnabled = new AtomicBoolean(true);
   private final AtomicBoolean postRoast = new AtomicBoolean(false);
   private final AtomicBoolean suspended = new AtomicBoolean(true);
   private final AtomicReference<Double> warningThreshold = new AtomicReference<>(1.0);
   private final AtomicReference<RoastingPhase> currentPhase = new AtomicReference<>();
   private final AtomicReference<MeasurementModel> currentModel = new AtomicReference<>();
   private final CommentListener eventBasedListener = new CommentListener() {
      @Override
      public void commentAdded(RoastingComment comment) {
         List<LiveGoalService.ActiveGoal> currentActiveGoals = null;
         List<LiveGoalServiceImpl.EventActiveGoal> matchingGoals;
         synchronized (LiveGoalServiceImpl.this) {
            matchingGoals = LiveGoalServiceImpl.this.getEventActiveGoals()
               .stream()
               .filter(ag -> ag.getLiveGoal().getId().equals(comment.getEvent()))
               .filter(ag -> ag.getLastCheckResult().getEvaluationStatus() != CheckResult.EvaluationStatus.COMPLETED)
               .toList();
            if (!matchingGoals.isEmpty()) {
               currentActiveGoals = new ArrayList<>(LiveGoalServiceImpl.this.activeGoals);
            }
         }

         if (!matchingGoals.isEmpty()) {
            List<LiveGoalService.ActiveGoal> updatedGoals = new ArrayList<>();
            Measure actualValue = new Measure((double)comment.getTime(), Unit.MILLISECOND).toUnit(Unit.DURATION);
            matchingGoals.forEach(ag -> {
               CheckResult ocr = ag.getLastCheckResult();
               ag.updateCheckResult(actualValue, ag.getReferenceValue());
               CheckResult ncr = ag.getLastCheckResult();
               if (!ncr.equals(ocr)) {
                  updatedGoals.add(ag);
               }
            });
            if (!updatedGoals.isEmpty()) {
               ActiveGoalsChangeEvent changeEvent = new ActiveGoalsChangeEvent(
                  currentActiveGoals, matchingGoals.stream().map(LiveGoalService.ActiveGoal.class::cast).toList(), ActiveGoalsChangeEvent.ChangeType.UPDATE
               );
               ((ActiveGoalsChangeListener)LiveGoalServiceImpl.this.activeGoalsChangeListeners.fire()).activeGoalsChanged(changeEvent);
            }
         }
      }

      @Override
      public void commentRemoved(RoastingComment comment) {
         List<LiveGoalService.ActiveGoal> currentActiveGoals = null;
         List<LiveGoalServiceImpl.EventActiveGoal> matchingGoals;
         synchronized (LiveGoalServiceImpl.this) {
            matchingGoals = LiveGoalServiceImpl.this.getEventActiveGoals()
               .stream()
               .filter(ag -> ag.getLiveGoal().getId().equals(comment.getEvent()))
               .filter(ag -> ag.getLastCheckResult().getEvaluationStatus() != CheckResult.EvaluationStatus.COMPLETED)
               .toList();
            if (!matchingGoals.isEmpty()) {
               matchingGoals.forEach(LiveGoalService.ActiveGoal::resetCheckResult);
               currentActiveGoals = new ArrayList<>(LiveGoalServiceImpl.this.activeGoals);
            }
         }

         if (currentActiveGoals != null && !currentActiveGoals.isEmpty()) {
            ActiveGoalsChangeEvent changeEvent = new ActiveGoalsChangeEvent(
               currentActiveGoals, matchingGoals.stream().map(LiveGoalService.ActiveGoal.class::cast).toList(), ActiveGoalsChangeEvent.ChangeType.UPDATE
            );
            ((ActiveGoalsChangeListener)LiveGoalServiceImpl.this.activeGoalsChangeListeners.fire()).activeGoalsChanged(changeEvent);
         }
      }

      @Override
      public void commentUpdated(RoastingComment comment) {
      }
   };
   private final CurveModelListener valueBasedListener = new CurveModelListener() {
      @Override
      public void notifyCurveChanged(CurveModel source) {
      }

      @Override
      public void notifyCurveChanged(CurveModel source, long time) {
         LiveGoalServiceImpl.this.evaluateValueActiveGoals(LiveGoalServiceImpl.this.currentModel.get().getRoast());
      }
   };

   public LiveGoalServiceImpl() {
      this.addLiveGoalsChangeListener(e -> {
         if (this.dirtyNotificationEnabled.get()) {
            synchronized (this) {
               this.setDirty(true);
            }
         }
      });
   }

   @Override
   public void addLiveGoals(Collection<LiveGoal> liveGoals) {
      if (liveGoals != null && !liveGoals.isEmpty()) {
         LiveGoalsChangeEvent changeEvent;
         synchronized (this) {
            this.liveGoals.addAll(liveGoals);
            changeEvent = new LiveGoalsChangeEvent(this.liveGoals, liveGoals, LiveGoalsChangeEvent.ChangeType.ADD);
            this.refresh();
         }

         ((LiveGoalsChangeListener)this.liveGoalsChangeListeners.fire()).liveGoalsChanged(changeEvent);
      }
   }

   @Override
   public boolean removeLiveGoals(Collection<LiveGoal> liveGoals) {
      boolean removed = false;
      if (liveGoals != null && !liveGoals.isEmpty()) {
         Collection<LiveGoal> passedLiveGoals = liveGoals.stream().filter(this.liveGoals::contains).toList();
         LiveGoalsChangeEvent changeEvent;
         synchronized (this) {
            changeEvent = new LiveGoalsChangeEvent(this.liveGoals, passedLiveGoals, LiveGoalsChangeEvent.ChangeType.REMOVE);
            removed = this.liveGoals.removeAll(passedLiveGoals);
            if (removed) {
               this.refresh();
            }
         }

         if (removed) {
            ((LiveGoalsChangeListener)this.liveGoalsChangeListeners.fire()).liveGoalsChanged(changeEvent);
         }
      }

      return removed;
   }

   @Override
   public void clearLiveGoals() {
      LiveGoalsChangeEvent changeEvent;
      synchronized (this) {
         changeEvent = new LiveGoalsChangeEvent(this.liveGoals, null, LiveGoalsChangeEvent.ChangeType.CLEAR);
         this.liveGoals.clear();
         this.refresh();
      }

      ((LiveGoalsChangeListener)this.liveGoalsChangeListeners.fire()).liveGoalsChanged(changeEvent);
   }

   @Override
   public List<LiveGoal> getLiveGoals() {
      return Collections.unmodifiableList(this.liveGoals);
   }

   @Override
   public void setLiveGoals(Collection<LiveGoal> liveGoals) {
      this.clearLiveGoals();
      this.addLiveGoals(liveGoals);
   }

   @Override
   public boolean replaceLiveGoals(Collection<LiveGoal> liveGoals) {
      boolean replaced = false;
      if (liveGoals != null && !liveGoals.isEmpty()) {
         LiveGoalsChangeEvent addEvent;
         LiveGoalsChangeEvent removeEvent;
         synchronized (this) {
            Map<LiveGoal, Integer> idToIndexMap = new HashMap<>();
            liveGoals.forEach(
               replacement -> this.liveGoals
                     .stream()
                     .filter(lg -> lg.getId().equals(replacement.getId()))
                     .mapToInt(this.liveGoals::indexOf)
                     .findFirst()
                     .ifPresent(i -> idToIndexMap.put(replacement, i))
            );
            removeEvent = new LiveGoalsChangeEvent(
               this.liveGoals, idToIndexMap.values().stream().map(this.liveGoals::get).toList(), LiveGoalsChangeEvent.ChangeType.REMOVE
            );
            idToIndexMap.forEach((goal, index) -> this.liveGoals.set(index, goal));
            addEvent = new LiveGoalsChangeEvent(
               this.liveGoals, idToIndexMap.values().stream().map(this.liveGoals::get).toList(), LiveGoalsChangeEvent.ChangeType.ADD
            );
            replaced = !idToIndexMap.isEmpty();
            if (replaced) {
               this.refresh();
            }
         }

         if (replaced) {
            this.setDirtyNotificationEnabled(false);
            ((LiveGoalsChangeListener)this.liveGoalsChangeListeners.fire()).liveGoalsChanged(removeEvent);
            ((LiveGoalsChangeListener)this.liveGoalsChangeListeners.fire()).liveGoalsChanged(addEvent);
            this.setDirtyNotificationEnabled(true, true);
         }
      }

      return replaced;
   }

   @Override
   public boolean moveLiveGoalAfter(String liveGoalIdToMove, String liveGoalIdTarget) {
      if (!StringUtils.isBlank(liveGoalIdToMove) && !StringUtils.isBlank(liveGoalIdTarget) && !liveGoalIdToMove.equals(liveGoalIdTarget)) {
         LiveGoalsChangeEvent addEvent;
         LiveGoalsChangeEvent removeEvent;
         synchronized (this) {
            LiveGoal liveGoalToMove = this.liveGoals.stream().filter(lg -> lg.getId().equals(liveGoalIdToMove)).findFirst().orElse(null);
            if (liveGoalToMove == null) {
               return false;
            }

            LiveGoal liveGoalTarget = this.liveGoals.stream().filter(lg -> lg.getId().equals(liveGoalIdTarget)).findFirst().orElse(null);
            if (liveGoalTarget == null) {
               return false;
            }

            removeEvent = new LiveGoalsChangeEvent(this.liveGoals, List.of(liveGoalToMove), LiveGoalsChangeEvent.ChangeType.REMOVE);
            this.liveGoals.remove(liveGoalToMove);
            int targetIndex = this.liveGoals.indexOf(liveGoalTarget);
            this.liveGoals.add(targetIndex + 1, liveGoalToMove);
            addEvent = new LiveGoalsChangeEvent(this.liveGoals, List.of(liveGoalToMove), LiveGoalsChangeEvent.ChangeType.ADD);
            this.refresh();
         }

         this.setDirtyNotificationEnabled(false);
         ((LiveGoalsChangeListener)this.liveGoalsChangeListeners.fire()).liveGoalsChanged(removeEvent);
         ((LiveGoalsChangeListener)this.liveGoalsChangeListeners.fire()).liveGoalsChanged(addEvent);
         this.setDirtyNotificationEnabled(true, true);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean moveLiveGoalBefore(String liveGoalIdToMove, String liveGoalIdTarget) {
      if (!StringUtils.isBlank(liveGoalIdToMove) && !StringUtils.isBlank(liveGoalIdTarget) && !liveGoalIdToMove.equals(liveGoalIdTarget)) {
         LiveGoalsChangeEvent addEvent;
         LiveGoalsChangeEvent removeEvent;
         synchronized (this) {
            LiveGoal liveGoalToMove = this.liveGoals.stream().filter(lg -> lg.getId().equals(liveGoalIdToMove)).findFirst().orElse(null);
            if (liveGoalToMove == null) {
               return false;
            }

            LiveGoal liveGoalTarget = this.liveGoals.stream().filter(lg -> lg.getId().equals(liveGoalIdTarget)).findFirst().orElse(null);
            if (liveGoalTarget == null) {
               return false;
            }

            removeEvent = new LiveGoalsChangeEvent(this.liveGoals, List.of(liveGoalToMove), LiveGoalsChangeEvent.ChangeType.REMOVE);
            this.liveGoals.remove(liveGoalToMove);
            int targetIndex = this.liveGoals.indexOf(liveGoalTarget);
            this.liveGoals.add(targetIndex, liveGoalToMove);
            addEvent = new LiveGoalsChangeEvent(this.liveGoals, List.of(liveGoalToMove), LiveGoalsChangeEvent.ChangeType.ADD);
            this.refresh();
         }

         this.setDirtyNotificationEnabled(false);
         ((LiveGoalsChangeListener)this.liveGoalsChangeListeners.fire()).liveGoalsChanged(removeEvent);
         ((LiveGoalsChangeListener)this.liveGoalsChangeListeners.fire()).liveGoalsChanged(addEvent);
         this.setDirtyNotificationEnabled(true, true);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean containsLiveGoal(String liveGoalId) {
      return this.liveGoals.stream().anyMatch(lg -> lg.getId().equals(liveGoalId));
   }

   @Override
   public boolean isDirty() {
      return this.dirty.get();
   }

   private void setDirty(boolean dirty) {
      boolean wasDirty = this.dirty.get();
      this.dirty.set(dirty);
      this.propertyChangeSupport.firePropertyChange("dirty", wasDirty, dirty);
   }

   @Override
   public boolean isDirtyNotificationEnabled() {
      return this.dirtyNotificationEnabled.get();
   }

   @Override
   public void setDirtyNotificationEnabled(boolean dirtyNotificationEnabled) {
      boolean wasDirtyNotificationEnabled = this.dirtyNotificationEnabled.get();
      this.dirtyNotificationEnabled.set(dirtyNotificationEnabled);
      this.propertyChangeSupport.firePropertyChange("dirtyNotificationEnabled", wasDirtyNotificationEnabled, dirtyNotificationEnabled);
   }

   @Override
   public void setDirtyNotificationEnabled(boolean dirtyNotificationEnabled, boolean newDirtyValue) {
      this.setDirtyNotificationEnabled(dirtyNotificationEnabled);
      this.setDirty(newDirtyValue);
   }

   @Override
   public void importFrom(Collection<LiveGoal> liveGoals) {
      synchronized (this) {
         this.replaceLiveGoals(liveGoals);
         this.setDirty(false);
      }
   }

   @Override
   public List<LiveGoal> exportTo() {
      synchronized (this) {
         List<LiveGoal> exports = new ArrayList<>(this.liveGoals);
         this.setDirty(false);
         return exports;
      }
   }

   @Override
   public void attach(MeasurementModel model, RoastingPhase phase, boolean postRoast) {
      Objects.requireNonNull(phase, "Null 'phase' parameter.");
      if (phase == RoastingPhase.ALWAYS) {
         throw new IllegalArgumentException("Phase ALWAYS is not allowed.");
      } else {
         this.currentPhase.set(phase);
         this.postRoast.set(postRoast);
         synchronized (this) {
            if (this.currentModel.get() != null) {
               this.detach();
            }

            if (this.logger.isDebugEnabled()) {
               this.logger.debug("Attaching model {} at phase {}", model.getClass().getSimpleName(), phase);
            }

            this.currentModel.set(model);
            if (model != null) {
               if (!this.suspended.get()) {
                  this.performRefresh();
                  this.attachInnerListeners();
               }
            }
         }
      }
   }

   @Override
   public void detach() {
      synchronized (this) {
         MeasurementModel model = this.currentModel.get();
         if (model != null) {
            if (this.logger.isDebugEnabled()) {
               this.logger.debug("Detaching model {} at phase {}", model.getClass().getSimpleName(), this.currentPhase.get());
            }

            this.detachInnerListeners();
            this.currentModel.set(null);
            if (!this.suspended.get()) {
               this.performRefresh();
            }
         }
      }
   }

   @Override
   public boolean isAttached() {
      synchronized (this) {
         return this.currentModel.get() != null;
      }
   }

   @Override
   public void refresh() {
      if (!this.suspended.get()) {
         this.detachInnerListeners();
         this.performRefresh();
         this.attachInnerListeners();
      }
   }

   @Override
   public void suspend() {
      synchronized (this) {
         if (this.suspended.compareAndSet(false, true)) {
            this.detachInnerListeners();
            this.performRefresh();
            this.propertyChangeSupport.firePropertyChange("suspended", false, true);
         }
      }
   }

   @Override
   public void resume() {
      synchronized (this) {
         if (this.suspended.compareAndSet(true, false)) {
            this.performRefresh();
            this.attachInnerListeners();
            this.propertyChangeSupport.firePropertyChange("suspended", true, false);
         }
      }
   }

   @Override
   public boolean isSuspended() {
      synchronized (this) {
         return this.suspended.get();
      }
   }

   @Override
   public double getWarningThreshold() {
      return this.warningThreshold.get();
   }

   @Override
   public void setWarningThreshold(double threshold) {
      Validate.inclusiveBetween(0.0, 1.0, threshold, "Warning threshold must be between 0.0 and 1.0");
      this.warningThreshold.set(threshold);
   }

   @Override
   public void setCurrentProfile(Profile profile, Roast reference, Map<Long, Collection<Goal>> goals) {
      synchronized (this) {
         if (profile != null && this.currentProfile != profile) {
            this.currentProfile = profile;
            this.currentProfileReference = reference;
            this.currentProfileGoals.clear();
            Long profileId = this.currentProfile.getId();
            if (goals != null && goals.containsKey(profileId)) {
               this.currentProfileGoals.addAll(goals.get(profileId));
            }

            this.refresh();
         }
      }
   }

   @Override
   public void addLiveGoalsChangeListener(LiveGoalsChangeListener listener) {
      synchronized (this) {
         this.liveGoalsChangeListeners.addListener(listener, false);
      }
   }

   @Override
   public void removeLiveGoalsChangeListener(LiveGoalsChangeListener listener) {
      synchronized (this) {
         this.liveGoalsChangeListeners.removeListener(listener);
      }
   }

   @Override
   public void addActiveGoalsChangeListener(ActiveGoalsChangeListener listener) {
      synchronized (this) {
         this.activeGoalsChangeListeners.addListener(listener, false);
      }
   }

   @Override
   public void removeActiveGoalsChangeListener(ActiveGoalsChangeListener listener) {
      synchronized (this) {
         this.activeGoalsChangeListeners.removeListener(listener);
      }
   }

   @Override
   public void addPropertyChangeListener(PropertyChangeListener listener) {
      this.propertyChangeSupport.addPropertyChangeListener(listener);
   }

   @Override
   public void removePropertyChangeListener(PropertyChangeListener listener) {
      this.propertyChangeSupport.removePropertyChangeListener(listener);
   }

   @Override
   public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
      this.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
   }

   @Override
   public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
      this.propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
   }

   @Override
   public List<LiveGoalService.ActiveGoal> getActiveGoals() {
      return Collections.unmodifiableList(this.activeGoals);
   }

   @Override
   public void forceEvaluation(Roast roast) {
      Objects.requireNonNull(roast, "Null 'roast' parameter.");
      this.evaluateCurveActiveGoals(roast);
      this.evaluateValueActiveGoals(roast);
   }

   @Override
   public void clearActiveGoals() {
      ActiveGoalsChangeEvent changeEvent;
      synchronized (this) {
         changeEvent = new ActiveGoalsChangeEvent(this.activeGoals, null, ActiveGoalsChangeEvent.ChangeType.CLEAR);
         this.activeGoals.clear();
      }

      ((ActiveGoalsChangeListener)this.activeGoalsChangeListeners.fire()).activeGoalsChanged(changeEvent);
   }

   @Override
   public List<LiveGoal> createDefaultLiveGoals() {
      return List.of(
         LiveGoal.valueBasedBuilder("weightLoss").withTargetDisplayMode().build(),
         LiveGoal.valueBasedBuilder("developmentTime").withTargetDisplayMode().build(),
         LiveGoal.valueBasedBuilder("developmentTimeRatio").withTargetDisplayMode().build(),
         LiveGoal.valueBasedBuilder("duration").withTargetDisplayMode().build(),
         LiveGoal.curveBasedBuilder("endTemperature").withTargetDisplayMode().build(),
         LiveGoal.valueBasedBuilder("roastValue").withTargetDisplayMode().build(),
         LiveGoal.valueBasedBuilder("roastValue2").withTargetDisplayMode().build()
      );
   }

   @Override
   public Map<String, Set<LiveGoalDisplayMode>> getSupportedDisplayModes() {
      return SUPPORTED_DISPLAY_MODES_CACHE;
   }

   @Override
   public Map<String, String> getLocalizedNames() {
      return LOCALIZED_NAMES_CACHE;
   }

   private void evaluateCurveActiveGoals(Roast roast) {
      if (roast != null) {
         synchronized (this) {
            List<LiveGoalServiceImpl.CurveActiveGoal> curveActiveGoals = this.getCurveActiveGoals();
            if (!curveActiveGoals.isEmpty()) {
               for (LiveGoalServiceImpl.CurveActiveGoal cag : curveActiveGoals) {
                  cag.handleCurveValueChanged();
               }
            }
         }
      }
   }

   private void evaluateValueActiveGoals(Roast roast) {
      if (roast != null) {
         synchronized (this) {
            List<LiveGoalServiceImpl.ValueActiveGoal> valueActiveGoals = this.getValueActiveGoals();
            if (!valueActiveGoals.isEmpty()) {
               this.handleValueChanged(roast, valueActiveGoals);
            }
         }
      }
   }

   private List<LiveGoalServiceImpl.CurveActiveGoal> getCurveActiveGoals() {
      return this.activeGoals
         .stream()
         .filter(ag -> ag.getLiveGoal().getType() == LiveGoalType.CURVE_BASED)
         .filter(LiveGoalServiceImpl.CurveActiveGoal.class::isInstance)
         .map(LiveGoalServiceImpl.CurveActiveGoal.class::cast)
         .toList();
   }

   private List<LiveGoalServiceImpl.ValueActiveGoal> getValueActiveGoals() {
      return this.activeGoals
         .stream()
         .filter(ag -> ag.getLiveGoal().getType() == LiveGoalType.VALUE_BASED)
         .filter(LiveGoalServiceImpl.ValueActiveGoal.class::isInstance)
         .map(LiveGoalServiceImpl.ValueActiveGoal.class::cast)
         .toList();
   }

   private void attachInnerListeners() {
      MeasurementModel cm = this.currentModel.get();
      if (cm != null) {
         if (this.isEventBasedGoalAvailable() && cm instanceof Commentable commentable) {
            commentable.addCommentListener(this.eventBasedListener);
         }

         if (this.isValueBasedGoalAvailable()) {
            CurveModel beanTemperatureCurveModel = this.getBeanTemperatureCurveModel(cm);
            if (beanTemperatureCurveModel != null) {
               beanTemperatureCurveModel.addListener(this.valueBasedListener);
            }
         }
      }
   }

   private void detachInnerListeners() {
      MeasurementModel cm = this.currentModel.get();
      if (cm != null) {
         if (cm instanceof Commentable commentable) {
            commentable.removeCommentListener(this.eventBasedListener);
         }

         CurveModel beanTemperatureCurveModel = this.getBeanTemperatureCurveModel(cm);
         if (beanTemperatureCurveModel != null) {
            beanTemperatureCurveModel.removeListener(this.valueBasedListener);
         }
      }

      this.getCurveActiveGoals().stream().filter(cag -> cag.getCurve() != null).forEach(cag -> cag.getCurve().removeListener(cag));
   }

   private CurveModel getBeanTemperatureCurveModel(MeasurementModel measurementModel) {
      Channel beanTemperatureChannel = measurementModel.getChannel("beanTemperature");
      if (beanTemperatureChannel == null) {
         this.logger.warn("Cannot attach value-based live goals: missing BEAN_TEMPERATURE channel");
         return null;
      } else {
         CurveModel beanTemperatureCurveModel = measurementModel.getCurveModel(beanTemperatureChannel);
         if (beanTemperatureCurveModel == null) {
            this.logger.warn("Cannot attach value-based live goals: missing BEAN_TEMPERATURE curve model");
         }

         return beanTemperatureCurveModel;
      }
   }

   private boolean isCurveBasedGoalAvailable() {
      return this.liveGoals.stream().filter(LiveGoal::isVisible).anyMatch(lg -> lg.getType() == LiveGoalType.CURVE_BASED);
   }

   private boolean isEventBasedGoalAvailable() {
      return this.liveGoals.stream().filter(LiveGoal::isVisible).anyMatch(lg -> lg.getType() == LiveGoalType.EVENT_BASED);
   }

   private boolean isValueBasedGoalAvailable() {
      return this.liveGoals.stream().filter(LiveGoal::isVisible).anyMatch(lg -> lg.getType() == LiveGoalType.VALUE_BASED);
   }

   private void performRefresh() {
      if (!this.suspended.get()) {
         List<LiveGoalService.ActiveGoal> currentActiveGoals = null;
         ActiveGoalsChangeEvent changeEvent = null;
         List<LiveGoalServiceImpl.EventActiveGoal> eventActiveGoals;
         synchronized (this) {
            this.clearActiveGoals();
            List<Goal> filteredGoals = this.createAdditionalGoals(this.filterGoals(this.currentProfile, this.currentProfileGoals));
            if (filteredGoals.isEmpty()) {
               return;
            }

            List<LiveGoalService.ActiveGoal> newActiveGoals = new ArrayList<>();

            for (Goal g : filteredGoals) {
               LiveGoalService.ActiveGoal activeGoal = this.createActiveGoal(g, this.currentProfileReference);
               if (activeGoal != null) {
                  newActiveGoals.add(activeGoal);
               }
            }

            this.addMissingGoals(newActiveGoals);
            if (!newActiveGoals.isEmpty()) {
               Map<String, Integer> idToIndexMap = this.getIdToIndexMap(this.liveGoals);
               newActiveGoals.sort(Comparator.comparingInt(ag -> idToIndexMap.getOrDefault(ag.getLiveGoal().getId(), Integer.MAX_VALUE)));
               this.activeGoals.addAll(newActiveGoals);
               changeEvent = new ActiveGoalsChangeEvent(this.activeGoals, newActiveGoals, ActiveGoalsChangeEvent.ChangeType.ADD);
            }

            eventActiveGoals = this.getEventActiveGoals();
            if (!eventActiveGoals.isEmpty()) {
               currentActiveGoals = new ArrayList<>(this.activeGoals);
               eventActiveGoals = new ArrayList<>(eventActiveGoals);
            }
         }

         if (changeEvent != null) {
            ((ActiveGoalsChangeListener)this.activeGoalsChangeListeners.fire()).activeGoalsChanged(changeEvent);
         }

         this.forceEventActiveGoalsUpdate(currentActiveGoals, eventActiveGoals);
      }
   }

   private void addMissingGoals(List<LiveGoalService.ActiveGoal> newActiveGoals) {
      this.liveGoals
         .stream()
         .filter(lg -> lg.isVisible() && newActiveGoals.stream().noneMatch(ag -> ag.getLiveGoal().getId().equals(lg.getId())))
         .map(this::createMissingActiveGoal)
         .forEach(newActiveGoals::add);
   }

   private void forceEventActiveGoalsUpdate(List<LiveGoalService.ActiveGoal> currentActiveGoals, List<LiveGoalServiceImpl.EventActiveGoal> eventActiveGoals) {
      if (currentActiveGoals != null && !currentActiveGoals.isEmpty()) {
         if (this.currentModel.get() instanceof Commentable commentable) {
            eventActiveGoals.forEach(
               ag -> commentable.getComments().stream().filter(c -> ag.getLiveGoal().getId().equals(c.getEvent())).findFirst().ifPresent(c -> {
                     Measure actualValue = new Measure((double)c.getTime(), Unit.MILLISECOND).toUnit(Unit.DURATION);
                     ag.updateCheckResult(actualValue, ag.getReferenceValue());
                  })
            );
         }

         ActiveGoalsChangeEvent changeEvent = new ActiveGoalsChangeEvent(
            currentActiveGoals, eventActiveGoals.stream().map(LiveGoalService.ActiveGoal.class::cast).toList(), ActiveGoalsChangeEvent.ChangeType.UPDATE
         );
         ((ActiveGoalsChangeListener)this.activeGoalsChangeListeners.fire()).activeGoalsChanged(changeEvent);
      }
   }

   private Map<String, Integer> getIdToIndexMap(List<LiveGoal> liveGoalsList) {
      int size = liveGoalsList.size();
      Map<String, Integer> idToIndexMap = new HashMap<>(size);

      for (int i = 0; i < size; i++) {
         idToIndexMap.put(liveGoalsList.get(i).getId(), i);
      }

      return idToIndexMap;
   }

   private List<Goal> createAdditionalGoals(List<Goal> goals) {
      if (this.currentProfileReference == null) {
         return goals;
      } else {
         String charge = org.cropster.roastingintelligence.util.Messages.m_("chargeState");
         List<Goal> updatedGoals = new ArrayList<>(goals);
         this.createReferenceGoal("chargeTemperature", this.getLocalizedNames().get("chargeTemperature"), goals, updatedGoals);
         this.currentProfileReference
            .getCurves()
            .keySet()
            .forEach(
               curveName -> this.createReferenceGoal(
                     "chargeTemperature-" + curveName, charge + " " + org.cropster.roastingintelligence.util.Messages.m_(curveName), goals, updatedGoals
                  )
            );
         this.createReferenceGoal("gas", this.getLocalizedNames().get("gas"), goals, updatedGoals);
         this.createReferenceGoal("airflow", this.getLocalizedNames().get("airflow"), goals, updatedGoals);
         this.createReferenceGoal("ambientTemperature", this.getLocalizedNames().get("ambientTemperature"), goals, updatedGoals);
         this.createReferenceGoal("firstCrack", this.getLocalizedNames().get("firstCrack"), goals, updatedGoals);
         this.createReferenceGoal("developmentTime", this.getLocalizedNames().get("developmentTime"), goals, updatedGoals);
         this.createReferenceGoal("developmentTimeRatio", this.getLocalizedNames().get("developmentTimeRatio"), goals, updatedGoals);
         this.createReferenceGoal("duration", this.getLocalizedNames().get("duration"), goals, updatedGoals);
         this.createReferenceGoal("endTemperature", this.getLocalizedNames().get("endTemperature"), goals, updatedGoals);
         this.currentProfileReference
            .getCurves()
            .keySet()
            .forEach(
               curveName -> this.createReferenceGoal(
                     "endTemperature-" + curveName,
                     org.cropster.roastingintelligence.util.Messages.m_("end") + " " + org.cropster.roastingintelligence.util.Messages.m_(curveName),
                     goals,
                     updatedGoals
                  )
            );
         this.createReferenceGoal("startWeight", this.getLocalizedNames().get("startWeight"), goals, updatedGoals);
         this.createReferenceGoal("endWeight", this.getLocalizedNames().get("endWeight"), goals, updatedGoals);
         this.createReferenceGoal("weightLoss", this.getLocalizedNames().get("weightLoss"), goals, updatedGoals);
         this.createReferenceGoal("roastValue", this.getLocalizedNames().get("roastValue"), goals, updatedGoals);
         this.createReferenceGoal("roastValue2", this.getLocalizedNames().get("roastValue2"), goals, updatedGoals);
         this.createReferenceGoal("constantBeanTemp", this.getLocalizedNames().get("constantBeanTemp"), goals, updatedGoals);
         this.createReferenceGoal("constantRoR", this.getLocalizedNames().get("constantRoR"), goals, updatedGoals);
         return updatedGoals;
      }
   }

   private void createReferenceGoal(String name, String localizedMessage, List<Goal> goals, List<Goal> updatedGoals) {
      if (goals.stream().map(Goal::getName).noneMatch(name::equals)) {
         Goal goal = new Goal();
         goal.setName(name);
         goal.setLocalizedMessage(localizedMessage);
         updatedGoals.add(goal);
      }
   }

   private LiveGoalService.ActiveGoal createActiveGoal(Goal g, Roast reference) {
      LiveGoal liveGoal = this.liveGoals.stream().filter(lg -> this.liveGoalMatchesGoal(lg, g)).filter(LiveGoal::isVisible).findFirst().orElse(null);
      if (liveGoal == null) {
         return null;
      } else if (this.activeGoals.stream().anyMatch(ag -> ag.getGoal() == g)) {
         return null;
      } else {
         LiveGoalType type = liveGoal.getType();

         return switch (type) {
            case CURVE_BASED -> this.createCurveActiveGoal(g, liveGoal, reference);
            case EVENT_BASED -> this.createEventActiveGoal(g, liveGoal, reference);
            case VALUE_BASED -> this.createValueActiveGoal(g, liveGoal, reference);
         };
      }
   }

   private boolean liveGoalMatchesGoal(LiveGoal lg, Goal g) {
      if (lg != null && g != null) {
         String i = lg.getId();
         String n = g.getName();
         return i.equals(n)
            || "chargeTemperature-".equals(i) && n.startsWith("chargeTemperature-")
            || "endTemperature-".equals(i) && n.startsWith("endTemperature-");
      } else {
         return false;
      }
   }

   private LiveGoalService.ActiveGoal createReferenceActiveGoal(Goal g, LiveGoal liveGoal, Roast reference) {
      if (!this.isValidReferenceActiveGoal(g, liveGoal, reference)) {
         return null;
      } else {
         String goalName = g.getName();
         if ("chargeTemperature".equals(goalName) || "endTemperature".equals(goalName)) {
            goalName = "beanTemperature";
         }

         Measure refValue = getReferenceValue(g, liveGoal, reference, goalName);
         return refValue == null ? null : new LiveGoalServiceImpl.ReferenceActiveGoal(this.currentProfile, liveGoal, g, refValue, this.isAttached());
      }
   }

   private boolean isValidReferenceActiveGoal(Goal g, LiveGoal liveGoal, Roast reference) {
      if (liveGoal == null || g == null) {
         return false;
      } else if (reference != null && !g.isAbsolute()) {
         boolean isTarget = liveGoal.isTarget();
         boolean isMeetTheReference = liveGoal.isMeetTheReference() && !g.isAbsolute();
         boolean isRange = liveGoal.isRange() && g.isAbsolute();
         return isTarget || isMeetTheReference || isRange;
      } else {
         return false;
      }
   }

   private static Measure getReferenceValue(Goal g, LiveGoal liveGoal, Roast reference, String goalName) {
      Measure refValue = getTypedReferenceValue(liveGoal, reference);
      if (refValue == null) {
         refValue = getMeasureReferenceValue(reference, goalName);
      }

      if (refValue == null) {
         refValue = getCurveReferenceValue(g, reference, goalName);
      }

      return refValue;
   }

   private static Measure getTypedReferenceValue(LiveGoal liveGoal, Roast reference) {
      if (liveGoal.getType() == LiveGoalType.EVENT_BASED) {
         return FirstCrackChecker.getFirstCrackMeasure(reference);
      } else if (liveGoal.getType() != LiveGoalType.VALUE_BASED) {
         return null;
      } else {
         String var2 = liveGoal.getId();

         return switch (var2) {
            case "duration" -> DurationChecker.getDuration(reference);
            case "endWeight" -> reference.getEndWeight();
            case "startWeight" -> reference.getStartWeight();
            case "weightLoss" -> WeightLossChecker.getWeightLoss(reference);
            default -> null;
         };
      }
   }

   private static Measure getMeasureReferenceValue(Roast reference, String goalName) {
      Map<String, Measure> measures = reference.getMeasures();
      return measures != null && !measures.isEmpty() ? measures.get(goalName) : null;
   }

   private static Measure getCurveReferenceValue(Goal g, Roast reference, String goalName) {
      Curve curve = getReferenceCurve(reference, goalName);
      if (curve == null) {
         return null;
      } else {
         Entry<Long, Double> entry = getReferenceCurveEntry(g, curve);
         return entry == null ? null : new Measure(entry.getValue(), curve.getUnit());
      }
   }

   private static Curve getReferenceCurve(Roast reference, String goalName) {
      Map<String, Curve> curves = reference.getCurves();
      return curves != null && !curves.isEmpty() ? curves.get(goalName) : null;
   }

   private static Entry<Long, Double> getReferenceCurveEntry(Goal g, Curve curve) {
      return "endTemperature".equals(g.getName()) ? curve.lastEntry() : curve.firstEntry();
   }

   private LiveGoalService.ActiveGoal createCurveActiveGoal(Goal g, LiveGoal liveGoal, Roast reference) {
      if (this.hasInvalidCurveActiveGoalInputs(g, liveGoal, reference)) {
         return null;
      } else {
         AbstractCurveChecker curveChecker = this.getCurveChecker(g);
         if (curveChecker == null) {
            return this.createReferenceActiveGoalIfTarget(g, liveGoal, reference);
         } else {
            CurveModel curve = curveChecker.getCurveModel(this.currentModel.get());
            return this.shouldCreateRangeCurveActiveGoal(g, liveGoal)
               ? this.createRangeCurveActiveGoal(g, liveGoal, curveChecker, curve)
               : this.createCurveActiveGoalWithReference(g, liveGoal, reference, curveChecker, curve);
         }
      }
   }

   private boolean hasInvalidCurveActiveGoalInputs(Goal g, LiveGoal liveGoal, Roast reference) {
      return liveGoal == null || !this.isCurveBasedGoalAvailable() || reference == null && g != null && !g.isAbsolute();
   }

   private AbstractCurveChecker getCurveChecker(Goal g) {
      Checker checker = Checkers.getChecker(g.getName(), this.currentPhase.get(), this.postRoast.get());
      return checker instanceof AbstractCurveChecker ? (AbstractCurveChecker)checker : null;
   }

   private AbstractCurveChecker getCurveChecker(LiveGoal lg) {
      Checker checker = Checkers.getChecker(lg.getId(), this.currentPhase.get(), this.postRoast.get());
      return checker instanceof AbstractCurveChecker ? (AbstractCurveChecker)checker : null;
   }

   private LiveGoalService.ActiveGoal createReferenceActiveGoalIfTarget(Goal g, LiveGoal liveGoal, Roast reference) {
      return liveGoal.isTarget() ? this.createReferenceActiveGoal(g, liveGoal, reference) : null;
   }

   private boolean shouldCreateRangeCurveActiveGoal(Goal g, LiveGoal liveGoal) {
      return g != null && g.isAbsolute() && g.hasBoundsDefined() && liveGoal.isRange();
   }

   private LiveGoalService.ActiveGoal createRangeCurveActiveGoal(Goal g, LiveGoal liveGoal, AbstractCurveChecker curveChecker, CurveModel curve) {
      LiveGoalServiceImpl.CurveActiveGoal activeGoal = new LiveGoalServiceImpl.CurveActiveGoal(
         this.currentProfile, liveGoal, g, curveChecker, curve, this.isAttached()
      );
      if (curve != null) {
         curve.addListener(activeGoal);
      }

      return activeGoal;
   }

   private LiveGoalService.ActiveGoal createCurveActiveGoalWithReference(
      Goal g, LiveGoal liveGoal, Roast reference, AbstractCurveChecker curveChecker, CurveModel curve
   ) {
      boolean useReference = reference != null && liveGoal.isMeetTheReference() || liveGoal.isTarget();
      if (!useReference) {
         return null;
      } else {
         Measure refValue = curveChecker.getReferenceValue(reference);
         if (refValue == null || !liveGoal.isTarget() && !g.hasBoundsDefined()) {
            return liveGoal.isTarget() ? this.createReferenceActiveGoal(g, liveGoal, reference) : null;
         } else {
            LiveGoalServiceImpl.CurveActiveGoal activeGoal = new LiveGoalServiceImpl.CurveActiveGoal(
               this.currentProfile, liveGoal, g, curveChecker, curve, curveChecker.getCurve(reference), refValue, this.isAttached()
            );
            if (curve != null) {
               curve.addListener(activeGoal);
            }

            return activeGoal;
         }
      }
   }

   private LiveGoalService.ActiveGoal createEventActiveGoal(Goal g, LiveGoal liveGoal, Roast reference) {
      if (this.hasInvalidEventActiveGoal(g, liveGoal, reference)) {
         return null;
      } else {
         Checker checker = Checkers.getChecker(g.getName(), this.currentPhase.get(), this.postRoast.get());
         if (checker == null) {
            return liveGoal.isTarget() ? this.createReferenceActiveGoal(g, liveGoal, reference) : null;
         } else {
            return (LiveGoalService.ActiveGoal)(this.shouldCreateRangeEventActiveGoal(g, liveGoal)
               ? new LiveGoalServiceImpl.EventActiveGoal(this.currentProfile, liveGoal, g, checker, this.isAttached())
               : this.createEventActiveGoalWithReference(g, liveGoal, reference, checker));
         }
      }
   }

   private boolean hasInvalidEventActiveGoal(Goal g, LiveGoal liveGoal, Roast reference) {
      return liveGoal == null || !this.isEventBasedGoalAvailable() || reference == null && g != null && !g.isAbsolute();
   }

   private boolean shouldCreateRangeEventActiveGoal(Goal g, LiveGoal liveGoal) {
      return g != null && g.isAbsolute() && g.hasBoundsDefined() && liveGoal.isRange();
   }

   private LiveGoalService.ActiveGoal createEventActiveGoalWithReference(Goal g, LiveGoal liveGoal, Roast reference, Checker checker) {
      boolean useReference = reference != null && liveGoal.isMeetTheReference() || liveGoal.isTarget();
      if (!useReference) {
         return null;
      } else {
         Measure refValue = checker.getReferenceValue(reference);
         if (refValue == null || !liveGoal.isTarget() && !g.hasBoundsDefined()) {
            return liveGoal.isTarget() ? this.createReferenceActiveGoal(g, liveGoal, reference) : null;
         } else {
            return new LiveGoalServiceImpl.EventActiveGoal(this.currentProfile, liveGoal, g, checker, refValue, this.isAttached());
         }
      }
   }

   private LiveGoalService.ActiveGoal createValueActiveGoal(Goal g, LiveGoal liveGoal, Roast reference) {
      if (this.hasInvalidValueActiveGoal(g, liveGoal, reference)) {
         return null;
      } else {
         Checker checker = Checkers.getChecker(g.getName(), this.currentPhase.get(), this.postRoast.get());
         if (checker == null) {
            return liveGoal.isTarget() ? this.createReferenceActiveGoal(g, liveGoal, reference) : null;
         } else {
            return (LiveGoalService.ActiveGoal)(this.shouldCreateRangeValueActiveGoal(g, liveGoal)
               ? new LiveGoalServiceImpl.ValueActiveGoal(this.currentProfile, liveGoal, g, checker, this.isAttached())
               : this.createValueActiveGoalWithReference(g, liveGoal, reference, checker));
         }
      }
   }

   private boolean hasInvalidValueActiveGoal(Goal g, LiveGoal liveGoal, Roast reference) {
      return liveGoal == null || !this.isValueBasedGoalAvailable() || reference == null && g != null && !g.isAbsolute();
   }

   private boolean shouldCreateRangeValueActiveGoal(Goal g, LiveGoal liveGoal) {
      return g != null && g.isAbsolute() && g.hasBoundsDefined() && liveGoal.isRange();
   }

   private LiveGoalService.ActiveGoal createValueActiveGoalWithReference(Goal g, LiveGoal liveGoal, Roast reference, Checker checker) {
      boolean useReference = reference != null && liveGoal.isMeetTheReference() || liveGoal.isTarget();
      if (!useReference) {
         return null;
      } else {
         Measure refValue = checker.getReferenceValue(reference);
         if (refValue == null || !liveGoal.isTarget() && !g.hasBoundsDefined()) {
            return liveGoal.isTarget() ? this.createReferenceActiveGoal(g, liveGoal, reference) : null;
         } else {
            return new LiveGoalServiceImpl.ValueActiveGoal(this.currentProfile, liveGoal, g, checker, refValue, this.isAttached());
         }
      }
   }

   private LiveGoalService.ActiveGoal createMissingActiveGoal(LiveGoal liveGoal) {
      LiveGoalType type = liveGoal.getType();

      return switch (type) {
         case CURVE_BASED -> this.createMissingCurveActiveGoal(liveGoal);
         case EVENT_BASED -> this.createMissingEventActiveGoal(liveGoal);
         case VALUE_BASED -> this.createMissingValueActiveGoal(liveGoal);
      };
   }

   private LiveGoalService.ActiveGoal createMissingCurveActiveGoal(LiveGoal lg) {
      AbstractCurveChecker curveChecker = this.getCurveChecker(lg);
      if (curveChecker == null) {
         return new LiveGoalServiceImpl.MissingActiveGoal(this.currentProfile, lg, this.isAttached());
      } else {
         CurveModel curve = curveChecker.getCurveModel(this.currentModel.get());
         LiveGoalServiceImpl.CurveActiveGoal activeGoal = new LiveGoalServiceImpl.CurveActiveGoal(
            this.currentProfile, lg, curveChecker, curve, this.isAttached()
         );
         if (curve != null) {
            curve.addListener(activeGoal);
         }

         return activeGoal;
      }
   }

   private LiveGoalService.ActiveGoal createMissingEventActiveGoal(LiveGoal lg) {
      Checker checker = Checkers.getChecker(lg.getId(), this.currentPhase.get(), this.postRoast.get());
      return (LiveGoalService.ActiveGoal)(checker == null
         ? new LiveGoalServiceImpl.MissingActiveGoal(this.currentProfile, lg, this.isAttached())
         : new LiveGoalServiceImpl.EventActiveGoal(this.currentProfile, lg, checker, this.isAttached()));
   }

   private LiveGoalService.ActiveGoal createMissingValueActiveGoal(LiveGoal lg) {
      Checker checker = Checkers.getChecker(lg.getId(), this.currentPhase.get(), this.postRoast.get());
      return (LiveGoalService.ActiveGoal)(checker == null
         ? new LiveGoalServiceImpl.MissingActiveGoal(this.currentProfile, lg, this.isAttached())
         : new LiveGoalServiceImpl.ValueActiveGoal(this.currentProfile, lg, checker, this.isAttached()));
   }

   private List<Goal> filterGoals(Profile profile, List<Goal> goals) {
      List<Goal> filteredGoals = filterNotMatchingGoals(profile, goals);
      if (filteredGoals.isEmpty()) {
         return filteredGoals;
      } else {
         List<LiveGoal> lgs;
         synchronized (this) {
            lgs = this.liveGoals;
         }

         List<Goal> toBeRemoved = new ArrayList<>();
         lgs.stream()
            .map(lg -> Pair.of(lg, filteredGoals.stream().filter(g -> g.getName().equals(lg.getId())).toList()))
            .collect(Collectors.toMap(Pair::getLeft, Pair::getRight))
            .forEach((lg, fgl) -> fgl.stream().filter(g -> this.shouldBeRemoved(lg, g)).forEach(toBeRemoved::add));
         if (!toBeRemoved.isEmpty()) {
            filteredGoals.removeAll(toBeRemoved);
         }

         return filteredGoals;
      }
   }

   public static List<Goal> filterNotMatchingGoals(Profile profile, Collection<Goal> goals) {
      if (profile != null && !goals.isEmpty()) {
         Long pid = profile.getId();
         Map<String, Goal> absoluteGoalMap = new HashMap<>();
         Map<String, Goal> relativeGoalMap = new HashMap<>();
         Comparator<Goal> newestFirstComparator = Comparator.<Goal, Long>comparing(g -> g.getLastModified()).reversed();
         goals.stream()
            .filter(g -> isValidSpecificGoal(g, pid))
            .filter(g -> g.getLastModified() != null)
            .sorted(newestFirstComparator)
            .forEach(g -> putInMap(g, absoluteGoalMap, relativeGoalMap));
         goals.stream()
            .filter(g -> isValidGlobalGoal(g, pid))
            .filter(g -> g.getLastModified() != null)
            .sorted(newestFirstComparator)
            .forEach(g -> putInMap(g, absoluteGoalMap, relativeGoalMap));
         List<Goal> filteredGoals = new LinkedList<>();
         filteredGoals.addAll(absoluteGoalMap.values());
         filteredGoals.addAll(relativeGoalMap.values());
         return filteredGoals;
      } else {
         return List.of();
      }
   }

   private static void putInMap(Goal g, Map<String, Goal> absoluteGoalMap, Map<String, Goal> relativeGoalMap) {
      if (g.isAbsolute()) {
         absoluteGoalMap.putIfAbsent(g.getName(), g);
      } else {
         relativeGoalMap.putIfAbsent(g.getName(), g);
      }
   }

   private static boolean isValidSpecificGoal(Goal g, Long profileId) {
      Set<Long> profiles = g.getProfiles();
      return !g.isGlobal() && profiles != null && profiles.contains(profileId);
   }

   private static boolean isValidGlobalGoal(Goal g, Long profileId) {
      Set<Long> profiles = g.getProfiles();
      return g.isGlobal() && (profiles == null || !profiles.contains(profileId));
   }

   private boolean shouldBeRemoved(LiveGoal lg, Goal g) {
      return !lg.isVisible() ? true : g.isAbsolute() && (!g.hasBoundsDefined() || !g.isSatisfiable() || lg.getDisplayMode() != LiveGoalDisplayMode.RANGE);
   }

   private void handleValueChanged(Roast roast, List<LiveGoalServiceImpl.ValueActiveGoal> valueActiveGoals) {
      List<LiveGoalService.ActiveGoal> currentActiveGoals = null;
      List<LiveGoalServiceImpl.ValueActiveGoal> matchingGoals;
      synchronized (this) {
         matchingGoals = valueActiveGoals.stream()
            .filter(ag -> ag.getLastCheckResult().getEvaluationStatus() != CheckResult.EvaluationStatus.COMPLETED)
            .toList();
         if (!matchingGoals.isEmpty()) {
            matchingGoals.forEach(ag -> ag.updateCheckResult(ag.getChecker().getActualValue(roast, this.currentModel.get()), ag.getReferenceValue()));
            currentActiveGoals = new ArrayList<>(this.activeGoals);
         }
      }

      if (currentActiveGoals != null && !currentActiveGoals.isEmpty()) {
         ActiveGoalsChangeEvent changeEvent = new ActiveGoalsChangeEvent(
            currentActiveGoals, matchingGoals.stream().map(LiveGoalService.ActiveGoal.class::cast).toList(), ActiveGoalsChangeEvent.ChangeType.UPDATE
         );
         ((ActiveGoalsChangeListener)this.activeGoalsChangeListeners.fire()).activeGoalsChanged(changeEvent);
      }
   }

   private List<LiveGoalServiceImpl.EventActiveGoal> getEventActiveGoals() {
      return this.activeGoals
         .stream()
         .filter(ag -> ag.getLiveGoal().getType() == LiveGoalType.EVENT_BASED)
         .filter(LiveGoalServiceImpl.EventActiveGoal.class::isInstance)
         .map(LiveGoalServiceImpl.EventActiveGoal.class::cast)
         .toList();
   }

   static {
      Set<LiveGoalDisplayMode> all = Set.of(LiveGoalDisplayMode.TARGET, LiveGoalDisplayMode.RANGE, LiveGoalDisplayMode.MEET_THE_REFERENCE);
      Set<LiveGoalDisplayMode> range = Set.of(LiveGoalDisplayMode.RANGE);
      Set<LiveGoalDisplayMode> target = Set.of(LiveGoalDisplayMode.TARGET);
      SUPPORTED_DISPLAY_MODES_CACHE = Map.ofEntries(
         Map.entry("airflow", target),
         Map.entry("ambientTemperature", target),
         Map.entry("beanRoR", range),
         Map.entry("chargeTemperature", all),
         Map.entry("constantBeanTemp", target),
         Map.entry("constantRoR", target),
         Map.entry("developmentTime", all),
         Map.entry("developmentTimeRatio", all),
         Map.entry("duration", all),
         Map.entry("endTemperature", all),
         Map.entry("endWeight", all),
         Map.entry("firstCrack", all),
         Map.entry("gas", target),
         Map.entry("chargeTemperature-", target),
         Map.entry("endTemperature-", target),
         Map.entry("roastValue", all),
         Map.entry("roastValue2", all),
         Map.entry("simpleRor10", range),
         Map.entry("simpleRor15", range),
         Map.entry("simpleRor30", range),
         Map.entry("simpleRor60", range),
         Map.entry("startWeight", all),
         Map.entry("weightLoss", all)
      );
      String charge = org.cropster.roastingintelligence.util.Messages.m_("chargeState");
      LOCALIZED_NAMES_CACHE = Map.ofEntries(
         Map.entry("airflow", org.cropster.roastingintelligence.util.Messages.m_(charge + " " + org.cropster.roastingintelligence.util.Messages.m_("airflow"))),
         Map.entry("ambientTemperature", org.cropster.roastingintelligence.util.Messages.m_("ambientTemperature")),
         Map.entry("beanRoR", org.cropster.roastingintelligence.util.Messages.m_("beanRoR")),
         Map.entry("chargeTemperature", charge + " " + org.cropster.roastingintelligence.util.Messages.m_("beanTemperature")),
         Map.entry("constantBeanTemp", org.cropster.roastingintelligence.util.Messages.m_("constantBeanTemp")),
         Map.entry("constantRoR", org.cropster.roastingintelligence.util.Messages.m_("constantRoR")),
         Map.entry(
            "endTemperature",
            org.cropster.roastingintelligence.util.Messages.m_(
               org.cropster.roastingintelligence.util.Messages.m_("End") + " " + org.cropster.roastingintelligence.util.Messages.m_("beanTemperature")
            )
         ),
         Map.entry("endWeight", org.cropster.roastingintelligence.util.Messages.m_("endWeight")),
         Map.entry("firstCrack", org.cropster.roastingintelligence.util.Messages.m_("firstCrack")),
         Map.entry("gas", org.cropster.roastingintelligence.util.Messages.m_(charge + " " + org.cropster.roastingintelligence.util.Messages.m_("gas"))),
         Map.entry("developmentTime", org.cropster.roastingintelligence.util.Messages.m_("developmentTime")),
         Map.entry("developmentTimeRatio", org.cropster.roastingintelligence.util.Messages.m_("developmentTimeRatio")),
         Map.entry("duration", org.cropster.roastingintelligence.util.Messages.m_("duration")),
         Map.entry("chargeTemperature-", org.cropster.roastingintelligence.util.Messages.m_("Other charge temp.")),
         Map.entry("endTemperature-", org.cropster.roastingintelligence.util.Messages.m_("Other end temp.")),
         Map.entry("roastValue", org.cropster.roastingintelligence.util.Messages.m_("roastValue")),
         Map.entry("roastValue2", org.cropster.roastingintelligence.util.Messages.m_("roastValue2")),
         Map.entry("simpleRor10", org.cropster.roastingintelligence.util.Messages.m_("simpleRor10")),
         Map.entry("simpleRor15", org.cropster.roastingintelligence.util.Messages.m_("simpleRor15")),
         Map.entry("simpleRor30", org.cropster.roastingintelligence.util.Messages.m_("simpleRor30")),
         Map.entry("simpleRor60", org.cropster.roastingintelligence.util.Messages.m_("simpleRor60")),
         Map.entry("startWeight", org.cropster.roastingintelligence.util.Messages.m_("startWeight")),
         Map.entry("weightLoss", org.cropster.roastingintelligence.util.Messages.m_("weightLoss"))
      );
   }

   private static class BaseActiveGoal extends LiveGoalService.ActiveGoal {
      private final Measure referenceValue;

      BaseActiveGoal(Profile profile, LiveGoal liveGoal, Goal goal, Checker checker, Measure referenceValue, boolean attached) {
         super(profile, liveGoal, goal, checker);
         this.referenceValue = referenceValue;
         if (attached) {
            this.setLastCheckResult(CheckResult.waiting(goal));
         }
      }

      @Override
      public Measure getReferenceValue() {
         return this.referenceValue;
      }
   }

   private class CurveActiveGoal extends LiveGoalServiceImpl.BaseActiveGoal implements CurveModelListener {
      private final CurveModel curve;
      private final Curve referenceCurve;

      CurveActiveGoal(Profile profile, LiveGoal liveGoal, AbstractCurveChecker checker, CurveModel curve, boolean attached) {
         this(profile, liveGoal, null, checker, curve, null, null, attached);
      }

      CurveActiveGoal(Profile profile, LiveGoal liveGoal, Goal goal, AbstractCurveChecker checker, CurveModel curve, boolean attached) {
         this(profile, liveGoal, goal, checker, curve, null, null, attached);
      }

      CurveActiveGoal(
         Profile profile,
         LiveGoal liveGoal,
         Goal goal,
         AbstractCurveChecker checker,
         CurveModel curve,
         Curve referenceCurve,
         Measure referenceValue,
         boolean attached
      ) {
         super(profile, liveGoal, goal, checker, referenceValue, attached);
         this.curve = curve;
         this.referenceCurve = referenceCurve;
      }

      public CurveModel getCurve() {
         return this.curve;
      }

      @Override
      public void notifyCurveChanged(CurveModel source) {
      }

      @Override
      public void notifyCurveChanged(CurveModel source, long time) {
         this.handleCurveValueChanged();
      }

      public void handleCurveValueChanged() {
         CheckResult lcr = this.getLastCheckResult();
         if (lcr.getEvaluationStatus() != CheckResult.EvaluationStatus.COMPLETED) {
            CheckResult ncr;
            synchronized (LiveGoalServiceImpl.this) {
               ncr = this.getChecker().evaluate(this.getGoal(), this.curve, this.referenceCurve);
            }

            if (!ncr.equals(lcr)) {
               this.setLastCheckResult(ncr);
               ActiveGoalsChangeEvent changeEvent = new ActiveGoalsChangeEvent(
                  LiveGoalServiceImpl.this.activeGoals, List.of(this), ActiveGoalsChangeEvent.ChangeType.UPDATE
               );
               ((ActiveGoalsChangeListener)LiveGoalServiceImpl.this.activeGoalsChangeListeners.fire()).activeGoalsChanged(changeEvent);
            }
         }
      }
   }

   private static class EventActiveGoal extends LiveGoalServiceImpl.BaseActiveGoal {
      EventActiveGoal(Profile profile, LiveGoal liveGoal, Checker checker, boolean attached) {
         this(profile, liveGoal, null, checker, null, attached);
      }

      EventActiveGoal(Profile profile, LiveGoal liveGoal, Goal goal, Checker checker, boolean attached) {
         this(profile, liveGoal, goal, checker, null, attached);
      }

      EventActiveGoal(Profile profile, LiveGoal liveGoal, Goal goal, Checker checker, Measure referenceValue, boolean attached) {
         super(profile, liveGoal, goal, checker, referenceValue, attached);
      }
   }

   private static class MissingActiveGoal extends LiveGoalServiceImpl.BaseActiveGoal {
      MissingActiveGoal(Profile profile, LiveGoal liveGoal, boolean attached) {
         super(profile, liveGoal, null, null, null, attached);
      }
   }

   private static class ReferenceActiveGoal extends LiveGoalServiceImpl.BaseActiveGoal {
      ReferenceActiveGoal(Profile profile, LiveGoal liveGoal, Goal goal, Measure referenceValue, boolean attached) {
         super(profile, liveGoal, goal, null, referenceValue, attached);
      }
   }

   private static class ValueActiveGoal extends LiveGoalServiceImpl.BaseActiveGoal {
      ValueActiveGoal(Profile profile, LiveGoal liveGoal, Checker checker, boolean attached) {
         this(profile, liveGoal, null, checker, null, attached);
      }

      ValueActiveGoal(Profile profile, LiveGoal liveGoal, Goal goal, Checker checker, boolean attached) {
         this(profile, liveGoal, goal, checker, null, attached);
      }

      ValueActiveGoal(Profile profile, LiveGoal liveGoal, Goal goal, Checker checker, Measure referenceValue, boolean attached) {
         super(profile, liveGoal, goal, checker, referenceValue, attached);
      }
   }
}
