package javafx.animation;

import com.sun.scenario.animation.AbstractPrimaryTimer;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

public abstract class Transition extends Animation {
   private ObjectProperty<Interpolator> interpolator;
   private static final Interpolator DEFAULT_INTERPOLATOR = Interpolator.EASE_BOTH;
   private Interpolator cachedInterpolator;

   public final void setInterpolator(Interpolator var1) {
      if (this.interpolator != null || !DEFAULT_INTERPOLATOR.equals(var1)) {
         this.interpolatorProperty().set(var1);
      }
   }

   public final Interpolator getInterpolator() {
      return this.interpolator == null ? DEFAULT_INTERPOLATOR : (Interpolator)this.interpolator.get();
   }

   public final ObjectProperty<Interpolator> interpolatorProperty() {
      if (this.interpolator == null) {
         this.interpolator = new SimpleObjectProperty(this, "interpolator", DEFAULT_INTERPOLATOR);
      }

      return this.interpolator;
   }

   protected Interpolator getCachedInterpolator() {
      return this.cachedInterpolator;
   }

   public Transition(double var1) {
      super(var1);
   }

   public Transition() {
   }

   Transition(AbstractPrimaryTimer var1) {
      super(var1);
   }

   protected Node getParentTargetNode() {
      return this.parent != null && this.parent instanceof Transition ? ((Transition)this.parent).getParentTargetNode() : null;
   }

   protected abstract void interpolate(double var1);

   private double calculateFraction(long var1, long var3) {
      double var5 = var3 <= 0L ? 1.0 : (double)var1 / (double)var3;
      return this.cachedInterpolator.interpolate(0.0, 1.0, var5);
   }

   @Override
   boolean startable(boolean var1) {
      return super.startable(var1) && (this.getInterpolator() != null || !var1 && this.cachedInterpolator != null);
   }

   @Override
   void sync(boolean var1) {
      super.sync(var1);
      if (var1 || this.cachedInterpolator == null) {
         this.cachedInterpolator = this.getInterpolator();
      }
   }

   @Override
   void doPlayTo(long var1, long var3) {
      this.setCurrentTicks(var1);
      this.interpolate(this.calculateFraction(var1, var3));
   }

   @Override
   void doJumpTo(long var1, long var3, boolean var5) {
      this.setCurrentTicks(var1);
      if (this.getStatus() != Animation.Status.STOPPED || var5) {
         this.sync(false);
         this.interpolate(this.calculateFraction(var1, var3));
      }
   }
}
