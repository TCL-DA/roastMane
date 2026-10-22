package javafx.animation;

import com.sun.javafx.animation.TickCalculation;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.collections.VetoableListDecorator;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

public final class ParallelTransition extends Transition {
   private static final Animation[] EMPTY_ANIMATION_ARRAY = new Animation[0];
   private static final double EPSILON = 1.0E-12;
   private Animation[] cachedChildren = EMPTY_ANIMATION_ARRAY;
   private long[] durations;
   private long[] delays;
   private double[] rates;
   private long[] offsetTicks;
   private boolean[] forceChildSync;
   private long oldTicks;
   private long cycleTime;
   private boolean childrenChanged = true;
   private boolean toggledRate;
   private final InvalidationListener childrenListener = var1x -> {
      this.childrenChanged = true;
      if (this.getStatus() == Animation.Status.STOPPED) {
         this.setCycleDuration(this.computeCycleDuration());
      }
   };
   private final ChangeListener<Number> rateListener = new ChangeListener<Number>() {
      public void changed(ObservableValue<? extends Number> var1, Number var2, Number var3) {
         if (var2.doubleValue() * var3.doubleValue() < 0.0) {
            for (int var4 = 0; var4 < ParallelTransition.this.cachedChildren.length; var4++) {
               Animation var5 = ParallelTransition.this.cachedChildren[var4];
               var5.clipEnvelope.setRate(ParallelTransition.this.rates[var4] * Math.signum(ParallelTransition.this.getCurrentRate()));
            }

            ParallelTransition.this.toggledRate = true;
         }
      }
   };
   private ObjectProperty<Node> node;
   private static final Node DEFAULT_NODE = null;
   private final Set<Animation> childrenSet = new HashSet<>();
   private final ObservableList<Animation> children = new VetoableListDecorator<Animation>(new TrackableObservableList<Animation>() {
      protected void onChanged(Change<Animation> var1) {
         while (var1.next()) {
            for (Animation var3 : var1.getRemoved()) {
               var3.parent = null;
               var3.rateProperty().removeListener(ParallelTransition.this.childrenListener);
               var3.totalDurationProperty().removeListener(ParallelTransition.this.childrenListener);
               var3.delayProperty().removeListener(ParallelTransition.this.childrenListener);
            }

            for (Animation var5 : var1.getAddedSubList()) {
               var5.parent = ParallelTransition.this;
               var5.rateProperty().addListener(ParallelTransition.this.childrenListener);
               var5.totalDurationProperty().addListener(ParallelTransition.this.childrenListener);
               var5.delayProperty().addListener(ParallelTransition.this.childrenListener);
            }
         }

         ParallelTransition.this.childrenListener.invalidated(ParallelTransition.this.children);
      }
   }) {
      protected void onProposedChange(List<Animation> var1, int... var2) {
         IllegalArgumentException var3 = null;

         for (byte var4 = 0; var4 < var2.length; var4 += 2) {
            for (int var5 = var2[var4]; var5 < var2[var4 + 1]; var5++) {
               ParallelTransition.this.childrenSet.remove(ParallelTransition.this.children.get(var5));
            }
         }

         for (Animation var7 : var1) {
            if (var7 == null) {
               var3 = new IllegalArgumentException("Child cannot be null");
               break;
            }

            if (!ParallelTransition.this.childrenSet.add(var7)) {
               var3 = new IllegalArgumentException("Attempting to add a duplicate to the list of children");
               break;
            }

            if (ParallelTransition.checkCycle(var7, ParallelTransition.this)) {
               var3 = new IllegalArgumentException("This change would create cycle");
               break;
            }
         }

         if (var3 != null) {
            ParallelTransition.this.childrenSet.clear();
            ParallelTransition.this.childrenSet.addAll(ParallelTransition.this.children);
            throw var3;
         }
      }
   };

   public final void setNode(Node var1) {
      if (this.node != null || var1 != null) {
         this.nodeProperty().set(var1);
      }
   }

   public final Node getNode() {
      return this.node == null ? DEFAULT_NODE : (Node)this.node.get();
   }

   public final ObjectProperty<Node> nodeProperty() {
      if (this.node == null) {
         this.node = new SimpleObjectProperty(this, "node", DEFAULT_NODE);
      }

      return this.node;
   }

   private static boolean checkCycle(Animation var0, Animation var1) {
      for (Animation var2 = var1; var2 != var0; var2 = var2.parent) {
         if (var2.parent == null) {
            return false;
         }
      }

      return true;
   }

   public final ObservableList<Animation> getChildren() {
      return this.children;
   }

   public ParallelTransition(Node var1, Animation... var2) {
      this.setInterpolator(Interpolator.LINEAR);
      this.setNode(var1);
      this.getChildren().setAll(var2);
   }

   public ParallelTransition(Animation... var1) {
      this(null, var1);
   }

   public ParallelTransition(Node var1) {
      this.setInterpolator(Interpolator.LINEAR);
      this.setNode(var1);
   }

   public ParallelTransition() {
      this((Node)null);
   }

   ParallelTransition(AbstractPrimaryTimer var1) {
      super(var1);
      this.setInterpolator(Interpolator.LINEAR);
   }

   @Override
   protected Node getParentTargetNode() {
      Node var1 = this.getNode();
      return var1 != null ? var1 : (this.parent != null && this.parent instanceof Transition ? ((Transition)this.parent).getParentTargetNode() : null);
   }

   private Duration computeCycleDuration() {
      Duration var1 = Duration.ZERO;

      for (Animation var3 : this.getChildren()) {
         double var4 = Math.abs(var3.getRate());
         Duration var6 = var4 < 1.0E-12 ? var3.getTotalDuration() : var3.getTotalDuration().divide(var4);
         Duration var7 = var6.add(var3.getDelay());
         if (var7.isIndefinite()) {
            return Duration.INDEFINITE;
         }

         if (var7.greaterThan(var1)) {
            var1 = var7;
         }
      }

      return var1;
   }

   private double calculateFraction(long var1, long var3) {
      double var5 = (double)var1 / (double)var3;
      return var5 <= 0.0 ? 0.0 : (var5 >= 1.0 ? 1.0 : var5);
   }

   private boolean startChild(Animation var1, int var2) {
      boolean var3 = this.forceChildSync[var2];
      if (var1.startable(var3)) {
         var1.clipEnvelope.setRate(this.rates[var2] * Math.signum(this.getCurrentRate()));
         var1.doStart(var3);
         this.forceChildSync[var2] = false;
         return true;
      } else {
         return false;
      }
   }

   @Override
   void sync(boolean var1) {
      super.sync(var1);
      if ((!var1 || !this.childrenChanged) && this.durations != null) {
         if (var1) {
            int var8 = this.forceChildSync.length;

            for (int var9 = 0; var9 < var8; var9++) {
               this.forceChildSync[var9] = true;
            }
         }
      } else {
         this.cachedChildren = (Animation[])this.getChildren().toArray(EMPTY_ANIMATION_ARRAY);
         int var2 = this.cachedChildren.length;
         this.durations = new long[var2];
         this.delays = new long[var2];
         this.rates = new double[var2];
         this.offsetTicks = new long[var2];
         this.forceChildSync = new boolean[var2];
         this.cycleTime = 0L;
         int var3 = 0;

         for (Animation var7 : this.cachedChildren) {
            this.rates[var3] = Math.abs(var7.getRate());
            if (this.rates[var3] < 1.0E-12) {
               this.rates[var3] = 1.0;
            }

            this.durations[var3] = TickCalculation.fromDuration(var7.getTotalDuration(), this.rates[var3]);
            this.delays[var3] = TickCalculation.fromDuration(var7.getDelay());
            this.cycleTime = Math.max(this.cycleTime, TickCalculation.add(this.durations[var3], this.delays[var3]));
            this.forceChildSync[var3] = true;
            var3++;
         }

         this.childrenChanged = false;
      }
   }

   @Override
   void doPause() {
      super.doPause();

      for (Animation var4 : this.cachedChildren) {
         if (var4.getStatus() == Animation.Status.RUNNING) {
            var4.doPause();
         }
      }
   }

   @Override
   void doResume() {
      super.doResume();
      int var1 = 0;

      for (Animation var5 : this.cachedChildren) {
         if (var5.getStatus() == Animation.Status.PAUSED) {
            var5.doResume();
            var5.clipEnvelope.setRate(this.rates[var1] * Math.signum(this.getCurrentRate()));
         }

         var1++;
      }
   }

   @Override
   void doStart(boolean var1) {
      super.doStart(var1);
      this.toggledRate = false;
      this.rateProperty().addListener(this.rateListener);
      double var2 = this.getCurrentRate();
      long var4 = TickCalculation.fromDuration(this.getCurrentTime());
      if (var2 < 0.0) {
         this.jumpToEnd();
         if (var4 < this.cycleTime) {
            this.doJumpTo(var4, this.cycleTime, false);
         }
      } else {
         this.jumpToStart();
         if (var4 > 0L) {
            this.doJumpTo(var4, this.cycleTime, false);
         }
      }
   }

   @Override
   void doStop() {
      super.doStop();

      for (Animation var4 : this.cachedChildren) {
         if (var4.getStatus() != Animation.Status.STOPPED) {
            var4.doStop();
         }
      }

      if (this.childrenChanged) {
         this.setCycleDuration(this.computeCycleDuration());
      }

      this.rateProperty().removeListener(this.rateListener);
   }

   @Override
   void doPlayTo(long var1, long var3) {
      this.setCurrentTicks(var1);
      double var5 = this.calculateFraction(var1, var3);
      long var7 = Math.max(0L, Math.min(this.getCachedInterpolator().interpolate(0L, var3, var5), var3));
      if (this.toggledRate) {
         for (int var9 = 0; var9 < this.cachedChildren.length; var9++) {
            if (this.cachedChildren[var9].getStatus() == Animation.Status.RUNNING) {
               this.offsetTicks[var9] = (long)(
                  (double)this.offsetTicks[var9]
                     - Math.signum(this.getCurrentRate()) * (double)(this.durations[var9] - 2L * (this.oldTicks - this.delays[var9]))
               );
            }
         }

         this.toggledRate = false;
      }

      if (this.getCurrentRate() > 0.0) {
         int var16 = 0;

         for (Animation var13 : this.cachedChildren) {
            if (var7 >= this.delays[var16]
               && (
                  this.oldTicks <= this.delays[var16]
                     || var7 < TickCalculation.add(this.delays[var16], this.durations[var16]) && var13.getStatus() == Animation.Status.STOPPED
               )) {
               boolean var14 = this.oldTicks <= this.delays[var16];
               if (!this.startChild(var13, var16)) {
                  if (var14) {
                     EventHandler var15 = var13.getOnFinished();
                     if (var15 != null) {
                        var15.handle(new ActionEvent(this, null));
                     }
                  }
                  continue;
               }

               var13.clipEnvelope.jumpTo(0L);
            }

            if (var7 >= TickCalculation.add(this.durations[var16], this.delays[var16])) {
               if (var13.getStatus() == Animation.Status.RUNNING) {
                  var13.doTimePulse(TickCalculation.sub(this.durations[var16], this.offsetTicks[var16]));
                  this.offsetTicks[var16] = 0L;
               }
            } else if (var7 > this.delays[var16]) {
               var13.doTimePulse(TickCalculation.sub(var7 - this.delays[var16], this.offsetTicks[var16]));
            }

            var16++;
         }
      } else {
         int var17 = 0;

         for (Animation var21 : this.cachedChildren) {
            if (var7 < TickCalculation.add(this.durations[var17], this.delays[var17])) {
               if (this.oldTicks >= TickCalculation.add(this.durations[var17], this.delays[var17])
                  || var7 >= this.delays[var17] && var21.getStatus() == Animation.Status.STOPPED) {
                  boolean var22 = this.oldTicks >= TickCalculation.add(this.durations[var17], this.delays[var17]);
                  if (!this.startChild(var21, var17)) {
                     if (var22) {
                        EventHandler var23 = var21.getOnFinished();
                        if (var23 != null) {
                           var23.handle(new ActionEvent(this, null));
                        }
                     }
                     continue;
                  }

                  var21.clipEnvelope.jumpTo(Math.round((double)this.durations[var17] * this.rates[var17]));
               }

               if (var7 <= this.delays[var17]) {
                  if (var21.getStatus() == Animation.Status.RUNNING) {
                     var21.doTimePulse(TickCalculation.sub(this.durations[var17], this.offsetTicks[var17]));
                     this.offsetTicks[var17] = 0L;
                  }
               } else {
                  var21.doTimePulse(TickCalculation.sub(TickCalculation.add(this.durations[var17], this.delays[var17]) - var7, this.offsetTicks[var17]));
               }
            }

            var17++;
         }
      }

      this.oldTicks = var7;
   }

   @Override
   void doJumpTo(long var1, long var3, boolean var5) {
      this.setCurrentTicks(var1);
      if (this.getStatus() != Animation.Status.STOPPED || var5) {
         this.sync(false);
         double var6 = this.calculateFraction(var1, var3);
         long var8 = Math.max(0L, Math.min(this.getCachedInterpolator().interpolate(0L, var3, var6), var3));
         int var10 = 0;

         for (Animation var14 : this.cachedChildren) {
            Animation.Status var15 = var14.getStatus();
            if (var8 <= this.delays[var10]) {
               this.offsetTicks[var10] = 0L;
               if (var15 != Animation.Status.STOPPED) {
                  var14.clipEnvelope.jumpTo(0L);
                  var14.doStop();
               } else if (TickCalculation.fromDuration(var14.getCurrentTime()) != 0L) {
                  var14.doJumpTo(0L, this.durations[var10], true);
               }
            } else if (var8 >= TickCalculation.add(this.durations[var10], this.delays[var10])) {
               this.offsetTicks[var10] = 0L;
               if (var15 != Animation.Status.STOPPED) {
                  var14.clipEnvelope.jumpTo(Math.round((double)this.durations[var10] * this.rates[var10]));
                  var14.doStop();
               } else if (TickCalculation.fromDuration(var14.getCurrentTime()) != this.durations[var10]) {
                  var14.doJumpTo(this.durations[var10], this.durations[var10], true);
               }
            } else {
               if (var15 == Animation.Status.STOPPED) {
                  this.startChild(var14, var10);
                  if (this.getStatus() == Animation.Status.PAUSED) {
                     var14.doPause();
                  }

                  this.offsetTicks[var10] = this.getCurrentRate() > 0.0
                     ? var8 - this.delays[var10]
                     : TickCalculation.add(this.durations[var10], this.delays[var10]) - var8;
               } else if (var15 == Animation.Status.PAUSED) {
                  this.offsetTicks[var10] = (long)(
                     (double)this.offsetTicks[var10] + (double)(var8 - this.oldTicks) * Math.signum(this.clipEnvelope.getCurrentRate())
                  );
               } else {
                  this.offsetTicks[var10] = this.offsetTicks[var10] + (this.getCurrentRate() > 0.0 ? var8 - this.oldTicks : this.oldTicks - var8);
               }

               var14.clipEnvelope.jumpTo(Math.round((double)TickCalculation.sub(var8, this.delays[var10]) * this.rates[var10]));
            }

            var10++;
         }

         this.oldTicks = var8;
      }
   }

   @Override
   protected void interpolate(double var1) {
   }

   private void jumpToEnd() {
      for (int var1 = 0; var1 < this.cachedChildren.length; var1++) {
         if (this.forceChildSync[var1]) {
            this.cachedChildren[var1].sync(true);
         }

         this.cachedChildren[var1].doJumpTo(this.durations[var1], this.durations[var1], true);
      }
   }

   private void jumpToStart() {
      for (int var1 = this.cachedChildren.length - 1; var1 >= 0; var1--) {
         if (this.forceChildSync[var1]) {
            this.cachedChildren[var1].sync(true);
         }

         this.cachedChildren[var1].doJumpTo(0L, this.durations[var1], true);
      }
   }
}
