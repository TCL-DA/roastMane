package javafx.animation;

import com.sun.javafx.animation.TickCalculation;
import com.sun.javafx.tk.Toolkit;
import com.sun.javafx.util.Utils;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.shared.ClipEnvelope;
import com.sun.scenario.animation.shared.PulseReceiver;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyDoublePropertyBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public abstract class Animation {
   public static final int INDEFINITE = -1;
   private static final double EPSILON = 1.0E-12;
   private long startTime;
   private long pauseTime;
   private boolean paused = false;
   private final AbstractPrimaryTimer timer;
   private AccessControlContext accessCtrlCtx = null;
   final PulseReceiver pulseReceiver = new PulseReceiver() {
      @Override
      public void timePulse(long var1) {
         long var3 = var1 - Animation.this.startTime;
         if (var3 >= 0L) {
            if (Animation.this.accessCtrlCtx == null) {
               throw new IllegalStateException("Error: AccessControlContext not captured");
            } else {
               AccessController.doPrivileged((PrivilegedAction<Void>)(() -> {
                  Animation.this.doTimePulse(var3);
                  return null;
               }), Animation.this.accessCtrlCtx);
            }
         }
      }
   };
   Animation parent = null;
   ClipEnvelope clipEnvelope;
   private boolean lastPlayedFinished = true;
   private boolean lastPlayedForward = true;
   private DoubleProperty rate;
   private static final double DEFAULT_RATE = 1.0;
   private double oldRate = 1.0;
   private ReadOnlyDoubleProperty currentRate;
   private static final double DEFAULT_CURRENT_RATE = 0.0;
   private ReadOnlyObjectProperty<Duration> cycleDuration;
   private static final Duration DEFAULT_CYCLE_DURATION = Duration.ZERO;
   private ReadOnlyObjectProperty<Duration> totalDuration;
   private static final Duration DEFAULT_TOTAL_DURATION = Duration.ZERO;
   private Animation.CurrentTimeProperty currentTime;
   private long currentTicks;
   private ObjectProperty<Duration> delay;
   private static final Duration DEFAULT_DELAY = Duration.ZERO;
   private IntegerProperty cycleCount;
   private static final int DEFAULT_CYCLE_COUNT = 1;
   private BooleanProperty autoReverse;
   private static final boolean DEFAULT_AUTO_REVERSE = false;
   private ReadOnlyObjectProperty<Animation.Status> status;
   private static final Animation.Status DEFAULT_STATUS = Animation.Status.STOPPED;
   private ObjectProperty<EventHandler<ActionEvent>> onFinished;
   private static final EventHandler<ActionEvent> DEFAULT_ON_FINISHED = null;
   private ObservableMap<String, Duration> cuePoints;
   private final double targetFramerate;
   private final int resolution;
   private long lastPulse;

   static final boolean isNearZero(double var0) {
      return Math.abs(var0) < 1.0E-12;
   }

   private static boolean areNearEqual(double var0, double var2) {
      return isNearZero(var2 - var0);
   }

   private long now() {
      return TickCalculation.fromNano(this.timer.nanos());
   }

   private void addPulseReceiver() {
      this.accessCtrlCtx = AccessController.getContext();
      this.timer.addPulseReceiver(this.pulseReceiver);
   }

   void startReceiver(long var1) {
      this.paused = false;
      this.startTime = this.now() + var1;
      this.addPulseReceiver();
   }

   void pauseReceiver() {
      if (!this.paused) {
         this.pauseTime = this.now();
         this.paused = true;
         this.timer.removePulseReceiver(this.pulseReceiver);
      }
   }

   void resumeReceiver() {
      if (this.paused) {
         long var1 = this.now() - this.pauseTime;
         this.startTime += var1;
         this.paused = false;
         this.addPulseReceiver();
      }
   }

   public final void setRate(double var1) {
      if (this.rate != null || !areNearEqual(var1, 1.0)) {
         this.rateProperty().set(var1);
      }
   }

   public final double getRate() {
      return this.rate == null ? 1.0 : this.rate.get();
   }

   public final DoubleProperty rateProperty() {
      if (this.rate == null) {
         this.rate = new DoublePropertyBase(1.0) {
            public void invalidated() {
               double var1 = Animation.this.getRate();
               if (Animation.this.isRunningEmbedded()) {
                  if (this.isBound()) {
                     this.unbind();
                  }

                  this.set(Animation.this.oldRate);
                  throw new IllegalArgumentException("Cannot set rate of embedded animation while running.");
               } else {
                  if (Animation.isNearZero(var1)) {
                     if (Animation.this.isRunning()) {
                        Animation.this.lastPlayedForward = Animation.areNearEqual(Animation.this.getCurrentRate(), Animation.this.oldRate);
                     }

                     Animation.this.doSetCurrentRate(0.0);
                     Animation.this.pauseReceiver();
                  } else {
                     if (Animation.this.isRunning()) {
                        double var3 = Animation.this.getCurrentRate();
                        if (Animation.isNearZero(var3)) {
                           Animation.this.doSetCurrentRate(Animation.this.lastPlayedForward ? var1 : -var1);
                           Animation.this.resumeReceiver();
                        } else {
                           boolean var5 = Animation.areNearEqual(var3, Animation.this.oldRate);
                           Animation.this.doSetCurrentRate(var5 ? var1 : -var1);
                        }
                     }

                     Animation.this.oldRate = var1;
                  }

                  Animation.this.clipEnvelope.setRate(var1);
               }
            }

            public Object getBean() {
               return Animation.this;
            }

            public String getName() {
               return "rate";
            }
         };
      }

      return this.rate;
   }

   private boolean isRunningEmbedded() {
      return this.parent == null ? false : !this.parent.isStopped() || this.parent.isRunningEmbedded();
   }

   public final double getCurrentRate() {
      return this.currentRate == null ? 0.0 : this.currentRate.get();
   }

   public final ReadOnlyDoubleProperty currentRateProperty() {
      if (this.currentRate == null) {
         this.currentRate = new Animation.CurrentRateProperty();
      }

      return this.currentRate;
   }

   void setCurrentRate(double var1) {
      this.doSetCurrentRate(var1);
   }

   private void doSetCurrentRate(double var1) {
      if (this.currentRate != null || !areNearEqual(var1, 0.0)) {
         ((Animation.CurrentRateProperty)this.currentRateProperty()).set(var1);
      }
   }

   protected final void setCycleDuration(Duration var1) {
      if (this.cycleDuration != null || !DEFAULT_CYCLE_DURATION.equals(var1)) {
         if (var1.lessThan(Duration.ZERO)) {
            throw new IllegalArgumentException("Cycle duration cannot be negative");
         }

         ((Animation.AnimationReadOnlyProperty)this.cycleDurationProperty()).set(var1);
         this.updateTotalDuration();
      }
   }

   public final Duration getCycleDuration() {
      return this.cycleDuration == null ? DEFAULT_CYCLE_DURATION : (Duration)this.cycleDuration.get();
   }

   public final ReadOnlyObjectProperty<Duration> cycleDurationProperty() {
      if (this.cycleDuration == null) {
         this.cycleDuration = new Animation.AnimationReadOnlyProperty<Duration>("cycleDuration", DEFAULT_CYCLE_DURATION);
      }

      return this.cycleDuration;
   }

   public final Duration getTotalDuration() {
      return this.totalDuration == null ? DEFAULT_TOTAL_DURATION : (Duration)this.totalDuration.get();
   }

   public final ReadOnlyObjectProperty<Duration> totalDurationProperty() {
      if (this.totalDuration == null) {
         this.totalDuration = new Animation.AnimationReadOnlyProperty<Duration>("totalDuration", DEFAULT_TOTAL_DURATION);
      }

      return this.totalDuration;
   }

   private void updateTotalDuration() {
      int var1 = this.getCycleCount();
      Duration var2 = this.getCycleDuration();
      Duration var3;
      if (Duration.ZERO.equals(var2)) {
         var3 = Duration.ZERO;
      } else if (var1 == -1) {
         var3 = Duration.INDEFINITE;
      } else if (var1 <= 1) {
         var3 = var2;
      } else {
         var3 = var2.multiply((double)var1);
      }

      if (this.totalDuration != null || !DEFAULT_TOTAL_DURATION.equals(var3)) {
         ((Animation.AnimationReadOnlyProperty)this.totalDurationProperty()).set(var3);
      }

      if (this.isStopped()) {
         this.syncClipEnvelope();
         if (var3.lessThan(this.getCurrentTime())) {
            this.clipEnvelope.jumpTo(TickCalculation.fromDuration(var3));
         }
      }
   }

   public final Duration getCurrentTime() {
      return TickCalculation.toDuration(this.currentTicks);
   }

   public final ReadOnlyObjectProperty<Duration> currentTimeProperty() {
      if (this.currentTime == null) {
         this.currentTime = new Animation.CurrentTimeProperty();
      }

      return this.currentTime;
   }

   public final void setDelay(Duration var1) {
      if (this.delay != null || !DEFAULT_DELAY.equals(var1)) {
         this.delayProperty().set(var1);
      }
   }

   public final Duration getDelay() {
      return this.delay == null ? DEFAULT_DELAY : (Duration)this.delay.get();
   }

   public final ObjectProperty<Duration> delayProperty() {
      if (this.delay == null) {
         this.delay = new ObjectPropertyBase<Duration>(DEFAULT_DELAY) {
            protected void invalidated() {
               Duration var1 = (Duration)this.get();
               if (var1.lessThan(Duration.ZERO)) {
                  if (this.isBound()) {
                     this.unbind();
                  }

                  this.set(Duration.ZERO);
                  throw new IllegalArgumentException("Cannot set delay to negative value. Setting to Duration.ZERO");
               }
            }

            public Object getBean() {
               return Animation.this;
            }

            public String getName() {
               return "delay";
            }
         };
      }

      return this.delay;
   }

   public final void setCycleCount(int var1) {
      if (this.cycleCount != null || var1 != 1) {
         this.cycleCountProperty().set(var1);
      }
   }

   public final int getCycleCount() {
      return this.cycleCount == null ? 1 : this.cycleCount.get();
   }

   public final IntegerProperty cycleCountProperty() {
      if (this.cycleCount == null) {
         this.cycleCount = new IntegerPropertyBase(1) {
            public void invalidated() {
               Animation.this.updateTotalDuration();
            }

            public Object getBean() {
               return Animation.this;
            }

            public String getName() {
               return "cycleCount";
            }
         };
      }

      return this.cycleCount;
   }

   public final void setAutoReverse(boolean var1) {
      if (this.autoReverse != null || var1) {
         this.autoReverseProperty().set(var1);
      }
   }

   public final boolean isAutoReverse() {
      return this.autoReverse == null ? false : this.autoReverse.get();
   }

   public final BooleanProperty autoReverseProperty() {
      if (this.autoReverse == null) {
         this.autoReverse = new BooleanPropertyBase(false) {
            public Object getBean() {
               return Animation.this;
            }

            public String getName() {
               return "autoReverse";
            }
         };
      }

      return this.autoReverse;
   }

   protected final void setStatus(Animation.Status var1) {
      if (this.status != null || !DEFAULT_STATUS.equals(var1)) {
         ((Animation.AnimationReadOnlyProperty)this.statusProperty()).set(var1);
      }
   }

   public final Animation.Status getStatus() {
      return this.status == null ? DEFAULT_STATUS : (Animation.Status)this.status.get();
   }

   public final ReadOnlyObjectProperty<Animation.Status> statusProperty() {
      if (this.status == null) {
         this.status = new Animation.AnimationReadOnlyProperty<Animation.Status>("status", Animation.Status.STOPPED);
      }

      return this.status;
   }

   boolean isStopped() {
      return this.getStatus() == Animation.Status.STOPPED;
   }

   boolean isPaused() {
      return this.getStatus() == Animation.Status.PAUSED;
   }

   boolean isRunning() {
      return this.getStatus() == Animation.Status.RUNNING;
   }

   public final void setOnFinished(EventHandler<ActionEvent> var1) {
      if (this.onFinished != null || var1 != DEFAULT_ON_FINISHED) {
         this.onFinishedProperty().set(var1);
      }
   }

   public final EventHandler<ActionEvent> getOnFinished() {
      return this.onFinished == null ? DEFAULT_ON_FINISHED : (EventHandler)this.onFinished.get();
   }

   public final ObjectProperty<EventHandler<ActionEvent>> onFinishedProperty() {
      if (this.onFinished == null) {
         this.onFinished = new ObjectPropertyBase<EventHandler<ActionEvent>>(DEFAULT_ON_FINISHED) {
            public Object getBean() {
               return Animation.this;
            }

            public String getName() {
               return "onFinished";
            }
         };
      }

      return this.onFinished;
   }

   public final ObservableMap<String, Duration> getCuePoints() {
      if (this.cuePoints == null) {
         this.cuePoints = FXCollections.observableHashMap();
      }

      return this.cuePoints;
   }

   public void jumpTo(Duration var1) {
      Objects.requireNonNull(var1, "Time needs to be specified");
      if (var1.isUnknown()) {
         throw new IllegalArgumentException("The time is invalid");
      } else if (this.parent != null) {
         throw new IllegalStateException("Cannot jump when embedded in another animation");
      } else {
         this.lastPlayedFinished = false;
         double var2 = var1.isIndefinite() ? this.getCycleDuration().toMillis() : Utils.clamp(0.0, var1.toMillis(), this.getTotalDuration().toMillis());
         long var4 = TickCalculation.fromMillis(var2);
         if (this.isStopped()) {
            this.syncClipEnvelope();
         }

         this.clipEnvelope.jumpTo(var4);
      }
   }

   public void jumpTo(String var1) {
      Objects.requireNonNull(var1, "CuePoint needs to be specified");
      if ("start".equalsIgnoreCase(var1)) {
         this.jumpTo(Duration.ZERO);
      } else if ("end".equalsIgnoreCase(var1)) {
         this.jumpTo(this.getTotalDuration());
      } else {
         Duration var2 = (Duration)this.getCuePoints().get(var1);
         if (var2 != null) {
            this.jumpTo(var2);
         }
      }
   }

   public void playFrom(String var1) {
      this.jumpTo(var1);
      this.play();
   }

   public void playFrom(Duration var1) {
      this.jumpTo(var1);
      this.play();
   }

   public void playFromStart() {
      this.stop();
      this.setRate(Math.abs(this.getRate()));
      this.jumpTo(Duration.ZERO);
      this.play();
   }

   public void play() {
      if (this.parent != null) {
         throw new IllegalStateException("Cannot start when embedded in another animation");
      } else {
         switch (this.getStatus()) {
            case STOPPED:
               if (this.startable(true)) {
                  double var1 = this.getRate();
                  if (this.lastPlayedFinished) {
                     this.jumpTo(var1 < 0.0 ? this.getTotalDuration() : Duration.ZERO);
                  }

                  this.doStart(true);
                  this.startReceiver(TickCalculation.fromDuration(this.getDelay()));
                  if (isNearZero(var1)) {
                     this.pauseReceiver();
                  }
               } else {
                  this.runHandler(this.getOnFinished());
               }
               break;
            case PAUSED:
               this.doResume();
               if (!isNearZero(this.getRate())) {
                  this.resumeReceiver();
               }
            case RUNNING:
         }
      }
   }

   void doStart(boolean var1) {
      this.sync(var1);
      this.setStatus(Animation.Status.RUNNING);
      this.clipEnvelope.start();
      this.doSetCurrentRate(this.clipEnvelope.getCurrentRate());
      this.lastPulse = 0L;
   }

   void doResume() {
      this.setStatus(Animation.Status.RUNNING);
      this.doSetCurrentRate(this.lastPlayedForward ? this.getRate() : -this.getRate());
   }

   public void stop() {
      if (this.parent != null) {
         throw new IllegalStateException("Cannot stop when embedded in another animation");
      } else {
         if (!this.isStopped()) {
            this.clipEnvelope.abortCurrentPulse();
            this.doStop();
            this.jumpTo(Duration.ZERO);
            this.lastPlayedFinished = true;
         }
      }
   }

   void doStop() {
      if (!this.paused) {
         this.timer.removePulseReceiver(this.pulseReceiver);
      }

      this.setStatus(Animation.Status.STOPPED);
      this.doSetCurrentRate(0.0);
   }

   public void pause() {
      if (this.parent != null) {
         throw new IllegalStateException("Cannot pause when embedded in another animation");
      } else {
         if (this.isRunning()) {
            this.clipEnvelope.abortCurrentPulse();
            this.pauseReceiver();
            this.doPause();
         }
      }
   }

   void doPause() {
      double var1 = this.getCurrentRate();
      if (!isNearZero(var1)) {
         this.lastPlayedForward = areNearEqual(this.getCurrentRate(), this.getRate());
      }

      this.doSetCurrentRate(0.0);
      this.setStatus(Animation.Status.PAUSED);
   }

   final void finished() {
      this.lastPlayedFinished = true;
      this.doStop();
      this.runHandler(this.getOnFinished());
   }

   void runHandler(EventHandler<ActionEvent> var1) {
      if (var1 != null) {
         try {
            var1.handle(new ActionEvent(this, null));
         } catch (Exception var3) {
            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), var3);
         }
      }
   }

   public final double getTargetFramerate() {
      return this.targetFramerate;
   }

   protected Animation(double var1) {
      this.targetFramerate = var1;
      this.resolution = (int)Math.max(1L, Math.round(6000.0 / var1));
      this.clipEnvelope = ClipEnvelope.create(this);
      this.timer = Toolkit.getToolkit().getPrimaryTimer();
   }

   protected Animation() {
      this.resolution = 1;
      this.targetFramerate = (double)(6000 / Toolkit.getToolkit().getPrimaryTimer().getDefaultResolution());
      this.clipEnvelope = ClipEnvelope.create(this);
      this.timer = Toolkit.getToolkit().getPrimaryTimer();
   }

   Animation(AbstractPrimaryTimer var1) {
      this.resolution = 1;
      this.targetFramerate = (double)(6000 / var1.getDefaultResolution());
      this.clipEnvelope = ClipEnvelope.create(this);
      this.timer = var1;
   }

   Animation(AbstractPrimaryTimer var1, ClipEnvelope var2, int var3) {
      this.resolution = var3;
      this.targetFramerate = (double)(6000 / var3);
      this.clipEnvelope = var2;
      this.timer = var1;
   }

   boolean startable(boolean var1) {
      return TickCalculation.fromDuration(this.getCycleDuration()) > 0L || !var1 && this.clipEnvelope.wasSynched();
   }

   void sync(boolean var1) {
      if (var1 || !this.clipEnvelope.wasSynched()) {
         this.syncClipEnvelope();
      }
   }

   private void syncClipEnvelope() {
      int var1 = this.getCycleCount();
      int var2 = var1 <= 0 && var1 != -1 ? 1 : var1;
      this.clipEnvelope = this.clipEnvelope.setCycleCount(var2);
      this.clipEnvelope.setCycleDuration(this.getCycleDuration());
      this.clipEnvelope.setAutoReverse(this.isAutoReverse());
   }

   void doTimePulse(long var1) {
      if (this.resolution == 1) {
         this.clipEnvelope.timePulse(var1);
      } else if (var1 - this.lastPulse >= (long)this.resolution) {
         this.lastPulse = var1 / (long)this.resolution * (long)this.resolution;
         this.clipEnvelope.timePulse(var1);
      }
   }

   abstract void doPlayTo(long var1, long var3);

   abstract void doJumpTo(long var1, long var3, boolean var5);

   void setCurrentTicks(long var1) {
      this.currentTicks = var1;
      if (this.currentTime != null) {
         this.currentTime.fireValueChangedEvent();
      }
   }

   static {
      AnimationAccessorImpl.DEFAULT = new AnimationAccessorImpl();
   }

   private class AnimationReadOnlyProperty<T> extends ReadOnlyObjectPropertyBase<T> {
      private final String name;
      private T value;

      private AnimationReadOnlyProperty(String var2, T var3) {
         this.name = var2;
         this.value = (T)var3;
      }

      public Object getBean() {
         return Animation.this;
      }

      public String getName() {
         return this.name;
      }

      public T get() {
         return this.value;
      }

      private void set(T var1) {
         this.value = (T)var1;
         this.fireValueChangedEvent();
      }
   }

   private class CurrentRateProperty extends ReadOnlyDoublePropertyBase {
      private double value;

      public Object getBean() {
         return Animation.this;
      }

      public String getName() {
         return "currentRate";
      }

      public double get() {
         return this.value;
      }

      private void set(double var1) {
         this.value = var1;
         this.fireValueChangedEvent();
      }
   }

   private class CurrentTimeProperty extends ReadOnlyObjectPropertyBase<Duration> {
      public Object getBean() {
         return Animation.this;
      }

      public String getName() {
         return "currentTime";
      }

      public Duration get() {
         return Animation.this.getCurrentTime();
      }

      public void fireValueChangedEvent() {
         super.fireValueChangedEvent();
      }
   }

   public static enum Status {
      PAUSED,
      RUNNING,
      STOPPED;
   }
}
