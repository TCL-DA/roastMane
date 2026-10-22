package javafx.animation;

import com.sun.scenario.animation.shared.AnimationAccessor;

final class AnimationAccessorImpl extends AnimationAccessor {
   @Override
   public void setCurrentRate(Animation var1, double var2) {
      var1.setCurrentRate(var2);
   }

   @Override
   public void playTo(Animation var1, long var2, long var4) {
      var1.doPlayTo(var2, var4);
   }

   @Override
   public void jumpTo(Animation var1, long var2, long var4, boolean var6) {
      var1.doJumpTo(var2, var4, var6);
   }

   @Override
   public void finished(Animation var1) {
      var1.finished();
   }

   @Override
   public void setCurrentTicks(Animation var1, long var2) {
      var1.setCurrentTicks(var2);
   }
}
