package com.cropster.roastingintelligence.ui;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class JavaFxApplicationState {
   private final EnumSet<JavaFxApplicationState.Phase> roastInProgressStates = EnumSet.of(
      JavaFxApplicationState.Phase.DETECTING, JavaFxApplicationState.Phase.ROASTING, JavaFxApplicationState.Phase.STOPPED
   );
   private final ObjectProperty<JavaFxApplicationState.Phase> phase = new SimpleObjectProperty(JavaFxApplicationState.Phase.STARTUP);
   private final BooleanProperty replayAvailable = new SimpleBooleanProperty(false);
   private final BooleanProperty replayEnabled = new SimpleBooleanProperty(false);
   private final BooleanProperty replayStarted = new SimpleBooleanProperty(false);
   private final BooleanProperty replaying = new SimpleBooleanProperty(false);
   private final BooleanProperty roastInProgress = new SimpleBooleanProperty(false);
   private final BooleanProperty roastConsistent = new SimpleBooleanProperty(false);
   private final ObjectProperty<Long> scheduled = new SimpleObjectProperty();
   private final BooleanProperty betweenBatchStopped = new SimpleBooleanProperty(false);
   private final Set<String> autoMarkedEvents = new HashSet<>();

   public ObjectProperty<JavaFxApplicationState.Phase> phaseProperty() {
      return this.phase;
   }

   public JavaFxApplicationState.Phase getPhase() {
      return (JavaFxApplicationState.Phase)this.phase.get();
   }

   public void setPhase(JavaFxApplicationState.Phase phase) {
      assert phase != null;

      if (this.roastInProgressStates.contains(this.phase.get()) && !this.roastInProgressStates.contains(phase)) {
         this.autoMarkedEvents.clear();
      }

      this.phase.set(phase);
      this.roastInProgress.set(this.roastInProgressStates.contains(phase));
   }

   public BooleanProperty replayAvailableProperty() {
      return this.replayAvailable;
   }

   public boolean isReplayAvailable() {
      return this.replayAvailable.get();
   }

   public void setReplayAvailable(boolean replayAvailable) {
      this.replayAvailable.set(replayAvailable);
   }

   public BooleanProperty replayEnabledProperty() {
      return this.replayEnabled;
   }

   public boolean isReplayEnabled() {
      return this.replayEnabled.get();
   }

   public void setReplayEnabled(boolean replayEnabled) {
      this.replayEnabled.set(replayEnabled);
   }

   public BooleanProperty replayStartedProperty() {
      return this.replayStarted;
   }

   public boolean isReplayStarted() {
      return this.replayStarted.get();
   }

   public void setReplayStarted(boolean replayStarted) {
      this.replayStarted.set(replayStarted);
   }

   public BooleanProperty replayingProperty() {
      return this.replaying;
   }

   public boolean isReplaying() {
      return this.replaying.get();
   }

   public void setReplaying(boolean replaying) {
      this.replaying.set(replaying);
   }

   public BooleanProperty roastInProgressProperty() {
      return this.roastInProgress;
   }

   public boolean getRoastInProgress() {
      return this.roastInProgress.get();
   }

   public boolean getRoastConsistent() {
      return this.roastConsistent.get();
   }

   public void setRoastConsistent(boolean roastConsistent) {
      this.roastConsistent.set(roastConsistent);
   }

   public BooleanProperty roastConsistentProperty() {
      return this.roastConsistent;
   }

   public ObjectProperty<Long> scheduledProperty() {
      return this.scheduled;
   }

   public Long getScheduled() {
      return (Long)this.scheduled.get();
   }

   public void setScheduled(Long scheduled) {
      this.scheduled.set(scheduled);
   }

   public Set<String> getAutoMarkedEvents() {
      return this.autoMarkedEvents;
   }

   public BooleanProperty betweenBatchStoppedProperty() {
      return this.betweenBatchStopped;
   }

   public boolean isBetweenBatchStopped() {
      return this.betweenBatchStopped.get();
   }

   public void setBetweenBatchStopped(boolean stopped) {
      this.betweenBatchStopped.set(stopped);
   }

   public static enum Phase {
      STARTUP,
      SETUP,
      DETECTING,
      ROASTING,
      STOPPED;
   }
}
