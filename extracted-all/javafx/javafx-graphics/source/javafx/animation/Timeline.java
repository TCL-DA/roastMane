package javafx.animation;

import com.sun.javafx.collections.TrackableObservableList;
import com.sun.scenario.animation.AbstractPrimaryTimer;
import com.sun.scenario.animation.shared.TimelineClipCore;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener.Change;
import javafx.util.Duration;

public final class Timeline extends Animation {
   final TimelineClipCore clipCore;
   private final ObservableList<KeyFrame> keyFrames = new TrackableObservableList<KeyFrame>() {
      protected void onChanged(Change<KeyFrame> var1) {
         while (var1.next()) {
            if (!var1.wasPermutated()) {
               for (KeyFrame var3 : var1.getRemoved()) {
                  String var4 = var3.getName();
                  if (var4 != null) {
                     Timeline.this.getCuePoints().remove(var4);
                  }
               }

               for (KeyFrame var7 : var1.getAddedSubList()) {
                  String var8 = var7.getName();
                  if (var8 != null) {
                     Timeline.this.getCuePoints().put(var8, var7.getTime());
                  }
               }

               Duration var6 = Timeline.this.clipCore.setKeyFrames(Timeline.this.getKeyFrames());
               Timeline.this.setCycleDuration(var6);
            }
         }
      }
   };

   public final ObservableList<KeyFrame> getKeyFrames() {
      return this.keyFrames;
   }

   public Timeline(double var1, KeyFrame... var3) {
      super(var1);
      this.clipCore = new TimelineClipCore(this);
      this.getKeyFrames().setAll(var3);
   }

   public Timeline(KeyFrame... var1) {
      this.clipCore = new TimelineClipCore(this);
      this.getKeyFrames().setAll(var1);
   }

   public Timeline(double var1) {
      super(var1);
      this.clipCore = new TimelineClipCore(this);
   }

   public Timeline() {
      this.clipCore = new TimelineClipCore(this);
   }

   Timeline(AbstractPrimaryTimer var1) {
      super(var1);
      this.clipCore = new TimelineClipCore(this);
   }

   @Override
   void doPlayTo(long var1, long var3) {
      this.clipCore.playTo(var1);
   }

   @Override
   void doJumpTo(long var1, long var3, boolean var5) {
      this.sync(false);
      this.setCurrentTicks(var1);
      this.clipCore.jumpTo(var1, var5);
   }

   @Override
   void setCurrentRate(double var1) {
      super.setCurrentRate(var1);
      this.clipCore.notifyCurrentRateChanged();
   }

   @Override
   void doStart(boolean var1) {
      super.doStart(var1);
      this.clipCore.start(var1);
   }

   @Override
   public void stop() {
      if (this.parent != null) {
         throw new IllegalStateException("Cannot stop when embedded in another animation");
      } else {
         if (this.getStatus() == Animation.Status.RUNNING) {
            this.clipCore.abort();
         }

         super.stop();
      }
   }
}
