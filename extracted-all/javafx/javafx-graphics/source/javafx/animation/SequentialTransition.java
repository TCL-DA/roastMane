package javafx.animation;

import com.sun.javafx.animation.TickCalculation;
import com.sun.javafx.collections.TrackableObservableList;
import com.sun.javafx.collections.VetoableListDecorator;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import java.util.Arrays;
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

public final class SequentialTransition extends Transition {
   private static final Animation[] EMPTY_ANIMATION_ARRAY = new Animation[0];
   private static final int BEFORE = -1;
   private static final double EPSILON = 1.0E-12;
   private Animation[] cachedChildren = EMPTY_ANIMATION_ARRAY;
   private long[] startTimes;
   private long[] durations;
   private long[] delays;
   private double[] rates;
   private boolean[] forceChildSync;
   private int end;
   private int curIndex = -1;
   private long oldTicks = 0L;
   private long offsetTicks;
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
            for (int var4 = 0; var4 < SequentialTransition.this.cachedChildren.length; var4++) {
               Animation var5 = SequentialTransition.this.cachedChildren[var4];
               var5.clipEnvelope.setRate(SequentialTransition.this.rates[var4] * Math.signum(SequentialTransition.this.getCurrentRate()));
            }

            SequentialTransition.this.toggledRate = true;
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
               var3.rateProperty().removeListener(SequentialTransition.this.childrenListener);
               var3.totalDurationProperty().removeListener(SequentialTransition.this.childrenListener);
               var3.delayProperty().removeListener(SequentialTransition.this.childrenListener);
            }

            for (Animation var5 : var1.getAddedSubList()) {
               var5.parent = SequentialTransition.this;
               var5.rateProperty().addListener(SequentialTransition.this.childrenListener);
               var5.totalDurationProperty().addListener(SequentialTransition.this.childrenListener);
               var5.delayProperty().addListener(SequentialTransition.this.childrenListener);
            }
         }

         SequentialTransition.this.childrenListener.invalidated(SequentialTransition.this.children);
      }
   }) {
      protected void onProposedChange(List<Animation> var1, int... var2) {
         IllegalArgumentException var3 = null;

         for (byte var4 = 0; var4 < var2.length; var4 += 2) {
            for (int var5 = var2[var4]; var5 < var2[var4 + 1]; var5++) {
               SequentialTransition.this.childrenSet.remove(SequentialTransition.this.children.get(var5));
            }
         }

         for (Animation var7 : var1) {
            if (var7 == null) {
               var3 = new IllegalArgumentException("Child cannot be null");
               break;
            }

            if (!SequentialTransition.this.childrenSet.add(var7)) {
               var3 = new IllegalArgumentException("Attempting to add a duplicate to the list of children");
               break;
            }

            if (SequentialTransition.checkCycle(var7, SequentialTransition.this)) {
               var3 = new IllegalArgumentException("This change would create cycle");
               break;
            }
         }

         if (var3 != null) {
            SequentialTransition.this.childrenSet.clear();
            SequentialTransition.this.childrenSet.addAll(SequentialTransition.this.children);
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

   public SequentialTransition(Node var1, Animation... var2) {
      this.setInterpolator(Interpolator.LINEAR);
      this.setNode(var1);
      this.getChildren().setAll(var2);
   }

   public SequentialTransition(Animation... var1) {
      this(null, var1);
   }

   public SequentialTransition(Node var1) {
      this.setInterpolator(Interpolator.LINEAR);
      this.setNode(var1);
   }

   public SequentialTransition() {
      this((Node)null);
   }

   SequentialTransition(AbstractPrimaryTimer var1) {
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
         var1 = var1.add(var3.getDelay());
         double var4 = Math.abs(var3.getRate());
         var1 = var1.add(var4 < 1.0E-12 ? var3.getTotalDuration() : var3.getTotalDuration().divide(var4));
         if (var1.isIndefinite()) {
            break;
         }
      }

      return var1;
   }

   private double calculateFraction(long var1, long var3) {
      double var5 = (double)var1 / (double)var3;
      return var5 <= 0.0 ? 0.0 : (var5 >= 1.0 ? 1.0 : var5);
   }

   private int findNewIndex(long var1) {
      if (this.curIndex != -1 && this.curIndex != this.end && this.startTimes[this.curIndex] <= var1 && var1 <= this.startTimes[this.curIndex + 1]) {
         return this.curIndex;
      } else {
         boolean var3 = this.curIndex == -1 || this.curIndex == this.end;
         int var4 = !var3 && var1 >= this.oldTicks ? this.curIndex + 1 : 0;
         int var5 = !var3 && this.oldTicks >= var1 ? this.curIndex : this.end;
         int var6 = Arrays.binarySearch(this.startTimes, var4, var5, var1);
         return var6 < 0 ? -var6 - 2 : (var6 > 0 ? var6 - 1 : 0);
      }
   }

   @Override
   void sync(boolean var1) {
      super.sync(var1);
      if ((!var1 || !this.childrenChanged) && this.startTimes != null) {
         if (var1) {
            int var9 = this.forceChildSync.length;

            for (int var3 = 0; var3 < var9; var3++) {
               this.forceChildSync[var3] = true;
            }
         }
      } else {
         this.cachedChildren = (Animation[])this.getChildren().toArray(EMPTY_ANIMATION_ARRAY);
         this.end = this.cachedChildren.length;
         this.startTimes = new long[this.end + 1];
         this.durations = new long[this.end];
         this.delays = new long[this.end];
         this.rates = new double[this.end];
         this.forceChildSync = new boolean[this.end];
         long var2 = 0L;
         int var4 = 0;

         for (Animation var8 : this.cachedChildren) {
            this.startTimes[var4] = var2;
            this.rates[var4] = Math.abs(var8.getRate());
            if (this.rates[var4] < 1.0E-12) {
               this.rates[var4] = 1.0;
            }

            this.durations[var4] = TickCalculation.fromDuration(var8.getTotalDuration(), this.rates[var4]);
            this.delays[var4] = TickCalculation.fromDuration(var8.getDelay());
            if (this.durations[var4] != Long.MAX_VALUE && this.delays[var4] != Long.MAX_VALUE && var2 != Long.MAX_VALUE) {
               var2 = TickCalculation.add(var2, TickCalculation.add(this.durations[var4], this.delays[var4]));
            } else {
               var2 = Long.MAX_VALUE;
            }

            this.forceChildSync[var4] = true;
            var4++;
         }

         this.startTimes[this.end] = var2;
         this.childrenChanged = false;
      }
   }

   @Override
   void doStart(boolean var1) {
      super.doStart(var1);
      this.toggledRate = false;
      this.rateProperty().addListener(this.rateListener);
      this.offsetTicks = 0L;
      double var2 = this.getCurrentRate();
      long var4 = TickCalculation.fromDuration(this.getCurrentTime());
      if (var2 < 0.0) {
         this.jumpToEnd();
         this.curIndex = this.end;
         if (var4 < this.startTimes[this.end]) {
            this.doJumpTo(var4, this.startTimes[this.end], false);
         }
      } else {
         this.jumpToBefore();
         this.curIndex = -1;
         if (var4 > 0L) {
            this.doJumpTo(var4, this.startTimes[this.end], false);
         }
      }
   }

   @Override
   void doPause() {
      super.doPause();
      if (this.curIndex != -1 && this.curIndex != this.end) {
         Animation var1 = this.cachedChildren[this.curIndex];
         if (var1.getStatus() == Animation.Status.RUNNING) {
            var1.doPause();
         }
      }
   }

   @Override
   void doResume() {
      super.doResume();
      if (this.curIndex != -1 && this.curIndex != this.end) {
         Animation var1 = this.cachedChildren[this.curIndex];
         if (var1.getStatus() == Animation.Status.PAUSED) {
            var1.doResume();
            var1.clipEnvelope.setRate(this.rates[this.curIndex] * Math.signum(this.getCurrentRate()));
         }
      }
   }

   @Override
   void doStop() {
      super.doStop();
      if (this.curIndex != -1 && this.curIndex != this.end) {
         Animation var1 = this.cachedChildren[this.curIndex];
         if (var1.getStatus() != Animation.Status.STOPPED) {
            var1.doStop();
         }
      }

      if (this.childrenChanged) {
         this.setCycleDuration(this.computeCycleDuration());
      }

      this.rateProperty().removeListener(this.rateListener);
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
   void doPlayTo(long var1, long var3) {
      this.setCurrentTicks(var1);
      double var5 = this.calculateFraction(var1, var3);
      long var7 = Math.max(0L, Math.min(this.getCachedInterpolator().interpolate(0L, var3, var5), var3));
      int var9 = this.findNewIndex(var7);
      Animation var10 = this.curIndex != -1 && this.curIndex != this.end ? this.cachedChildren[this.curIndex] : null;
      if (this.toggledRate) {
         if (var10 != null && var10.getStatus() == Animation.Status.RUNNING) {
            this.offsetTicks = (long)(
               (double)this.offsetTicks
                  - Math.signum(this.getCurrentRate())
                     * (double)(this.durations[this.curIndex] - 2L * (this.oldTicks - this.delays[this.curIndex] - this.startTimes[this.curIndex]))
            );
         }

         this.toggledRate = false;
      }

      if (this.curIndex == var9) {
         if (this.getCurrentRate() > 0.0) {
            long var11 = TickCalculation.add(this.startTimes[this.curIndex], this.delays[this.curIndex]);
            if (var7 >= var11) {
               if (this.oldTicks <= var11 || var10.getStatus() == Animation.Status.STOPPED) {
                  boolean var13 = this.oldTicks <= var11;
                  if (var13) {
                     var10.clipEnvelope.jumpTo(0L);
                  }

                  if (!this.startChild(var10, this.curIndex)) {
                     if (var13) {
                        EventHandler var14 = var10.getOnFinished();
                        if (var14 != null) {
                           var14.handle(new ActionEvent(this, null));
                        }
                     }

                     this.oldTicks = var7;
                     return;
                  }
               }

               if (var7 >= this.startTimes[this.curIndex + 1]) {
                  var10.doTimePulse(TickCalculation.sub(this.durations[this.curIndex], this.offsetTicks));
                  if (var7 == var3) {
                     this.curIndex = this.end;
                  }
               } else {
                  long var27 = TickCalculation.sub(var7 - var11, this.offsetTicks);
                  var10.doTimePulse(var27);
               }
            }
         } else {
            long var15 = TickCalculation.add(this.startTimes[this.curIndex], this.delays[this.curIndex]);
            if (this.oldTicks >= this.startTimes[this.curIndex + 1] || this.oldTicks >= var15 && var10.getStatus() == Animation.Status.STOPPED) {
               boolean var28 = this.oldTicks >= this.startTimes[this.curIndex + 1];
               if (var28) {
                  var10.clipEnvelope.jumpTo(Math.round((double)this.durations[this.curIndex] * this.rates[this.curIndex]));
               }

               if (!this.startChild(var10, this.curIndex)) {
                  if (var28) {
                     EventHandler var32 = var10.getOnFinished();
                     if (var32 != null) {
                        var32.handle(new ActionEvent(this, null));
                     }
                  }

                  this.oldTicks = var7;
                  return;
               }
            }

            if (var7 <= var15) {
               var10.doTimePulse(TickCalculation.sub(this.durations[this.curIndex], this.offsetTicks));
               if (var7 == 0L) {
                  this.curIndex = -1;
               }
            } else {
               long var29 = TickCalculation.sub(this.startTimes[this.curIndex + 1] - var7, this.offsetTicks);
               var10.doTimePulse(var29);
            }
         }
      } else if (this.curIndex < var9) {
         if (var10 != null) {
            long var16 = TickCalculation.add(this.startTimes[this.curIndex], this.delays[this.curIndex]);
            if (this.oldTicks <= var16 || var10.getStatus() == Animation.Status.STOPPED && this.oldTicks != this.startTimes[this.curIndex + 1]) {
               boolean var30 = this.oldTicks <= var16;
               if (var30) {
                  var10.clipEnvelope.jumpTo(0L);
               }

               if (!this.startChild(var10, this.curIndex) && var30) {
                  EventHandler var33 = var10.getOnFinished();
                  if (var33 != null) {
                     var33.handle(new ActionEvent(this, null));
                  }
               }
            }

            if (var10.getStatus() == Animation.Status.RUNNING) {
               var10.doTimePulse(TickCalculation.sub(this.durations[this.curIndex], this.offsetTicks));
            }

            this.oldTicks = this.startTimes[this.curIndex + 1];
         }

         this.offsetTicks = 0L;
         this.curIndex++;

         while (this.curIndex < var9) {
            Animation var17 = this.cachedChildren[this.curIndex];
            var17.clipEnvelope.jumpTo(0L);
            if (this.startChild(var17, this.curIndex)) {
               var17.doTimePulse(this.durations[this.curIndex]);
            } else {
               EventHandler var12 = var17.getOnFinished();
               if (var12 != null) {
                  var12.handle(new ActionEvent(this, null));
               }
            }

            this.oldTicks = this.startTimes[this.curIndex + 1];
            this.curIndex++;
         }

         Animation var18 = this.cachedChildren[this.curIndex];
         var18.clipEnvelope.jumpTo(0L);
         if (this.startChild(var18, this.curIndex)) {
            if (var7 >= this.startTimes[this.curIndex + 1]) {
               var18.doTimePulse(this.durations[this.curIndex]);
               if (var7 == var3) {
                  this.curIndex = this.end;
               }
            } else {
               long var22 = TickCalculation.sub(var7, TickCalculation.add(this.startTimes[this.curIndex], this.delays[this.curIndex]));
               var18.doTimePulse(var22);
            }
         } else {
            EventHandler var23 = var18.getOnFinished();
            if (var23 != null) {
               var23.handle(new ActionEvent(this, null));
            }
         }
      } else {
         if (var10 != null) {
            long var19 = TickCalculation.add(this.startTimes[this.curIndex], this.delays[this.curIndex]);
            if (this.oldTicks >= this.startTimes[this.curIndex + 1] || this.oldTicks > var19 && var10.getStatus() == Animation.Status.STOPPED) {
               boolean var31 = this.oldTicks >= this.startTimes[this.curIndex + 1];
               if (var31) {
                  var10.clipEnvelope.jumpTo(Math.round((double)this.durations[this.curIndex] * this.rates[this.curIndex]));
               }

               if (!this.startChild(var10, this.curIndex) && var31) {
                  EventHandler var34 = var10.getOnFinished();
                  if (var34 != null) {
                     var34.handle(new ActionEvent(this, null));
                  }
               }
            }

            if (var10.getStatus() == Animation.Status.RUNNING) {
               var10.doTimePulse(TickCalculation.sub(this.durations[this.curIndex], this.offsetTicks));
            }

            this.oldTicks = this.startTimes[this.curIndex];
         }

         this.offsetTicks = 0L;
         this.curIndex--;

         while (this.curIndex > var9) {
            Animation var20 = this.cachedChildren[this.curIndex];
            var20.clipEnvelope.jumpTo(Math.round((double)this.durations[this.curIndex] * this.rates[this.curIndex]));
            if (this.startChild(var20, this.curIndex)) {
               var20.doTimePulse(this.durations[this.curIndex]);
            } else {
               EventHandler var24 = var20.getOnFinished();
               if (var24 != null) {
                  var24.handle(new ActionEvent(this, null));
               }
            }

            this.oldTicks = this.startTimes[this.curIndex];
            this.curIndex--;
         }

         Animation var21 = this.cachedChildren[this.curIndex];
         var21.clipEnvelope.jumpTo(Math.round((double)this.durations[this.curIndex] * this.rates[this.curIndex]));
         if (this.startChild(var21, this.curIndex)) {
            if (var7 <= TickCalculation.add(this.startTimes[this.curIndex], this.delays[this.curIndex])) {
               var21.doTimePulse(this.durations[this.curIndex]);
               if (var7 == 0L) {
                  this.curIndex = -1;
               }
            } else {
               long var25 = TickCalculation.sub(this.startTimes[this.curIndex + 1], var7);
               var21.doTimePulse(var25);
            }
         } else {
            EventHandler var26 = var21.getOnFinished();
            if (var26 != null) {
               var26.handle(new ActionEvent(this, null));
            }
         }
      }

      this.oldTicks = var7;
   }

   @Override
   void doJumpTo(long var1, long var3, boolean var5) {
      this.setCurrentTicks(var1);
      Animation.Status var6 = this.getStatus();
      if (var6 != Animation.Status.STOPPED || var5) {
         this.sync(false);
         double var7 = this.calculateFraction(var1, var3);
         long var9 = Math.max(0L, Math.min(this.getCachedInterpolator().interpolate(0L, var3, var7), var3));
         int var11 = this.curIndex;
         this.curIndex = this.findNewIndex(var9);
         Animation var12 = this.cachedChildren[this.curIndex];
         double var13 = this.getCurrentRate();
         long var15 = TickCalculation.add(this.startTimes[this.curIndex], this.delays[this.curIndex]);
         if (this.curIndex != var11 && var6 != Animation.Status.STOPPED) {
            if (var11 != -1 && var11 != this.end) {
               Animation var17 = this.cachedChildren[var11];
               if (var17.getStatus() != Animation.Status.STOPPED) {
                  this.cachedChildren[var11].doStop();
               }
            }

            if (this.curIndex < var11) {
               for (int var19 = var11 == this.end ? this.end - 1 : var11; var19 > this.curIndex; var19--) {
                  this.cachedChildren[var19].doJumpTo(0L, this.durations[var19], true);
               }
            } else {
               for (int var18 = var11 == -1 ? 0 : var11; var18 < this.curIndex; var18++) {
                  this.cachedChildren[var18].doJumpTo(this.durations[var18], this.durations[var18], true);
               }
            }

            if (var9 >= var15) {
               this.startChild(var12, this.curIndex);
               if (var6 == Animation.Status.PAUSED) {
                  var12.doPause();
               }
            }
         }

         if (var11 == this.curIndex) {
            if (var13 == 0.0) {
               this.offsetTicks = (long)((double)this.offsetTicks + (double)(var9 - this.oldTicks) * Math.signum(this.clipEnvelope.getCurrentRate()));
            } else {
               this.offsetTicks = this.offsetTicks + (var13 > 0.0 ? var9 - this.oldTicks : this.oldTicks - var9);
            }
         } else if (var13 == 0.0) {
            if (this.clipEnvelope.getCurrentRate() > 0.0) {
               this.offsetTicks = Math.max(0L, var9 - var15);
            } else {
               this.offsetTicks = this.startTimes[this.curIndex] + this.durations[this.curIndex] - var9;
            }
         } else {
            this.offsetTicks = var13 > 0.0 ? Math.max(0L, var9 - var15) : this.startTimes[this.curIndex + 1] - var9;
         }

         var12.clipEnvelope.jumpTo(Math.round((double)TickCalculation.sub(var9, var15) * this.rates[this.curIndex]));
         this.oldTicks = var9;
      }
   }

   private void jumpToEnd() {
      for (int var1 = 0; var1 < this.end; var1++) {
         if (this.forceChildSync[var1]) {
            this.cachedChildren[var1].sync(true);
         }

         this.cachedChildren[var1].doJumpTo(this.durations[var1], this.durations[var1], true);
      }
   }

   private void jumpToBefore() {
      for (int var1 = this.end - 1; var1 >= 0; var1--) {
         if (this.forceChildSync[var1]) {
            this.cachedChildren[var1].sync(true);
         }

         this.cachedChildren[var1].doJumpTo(0L, this.durations[var1], true);
      }
   }

   @Override
   protected void interpolate(double var1) {
   }
}
