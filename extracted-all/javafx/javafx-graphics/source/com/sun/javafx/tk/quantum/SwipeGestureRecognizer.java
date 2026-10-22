package com.sun.javafx.tk.quantum;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import javafx.event.EventType;
import javafx.scene.input.SwipeEvent;

class SwipeGestureRecognizer implements GestureRecognizer {
   private static final double TANGENT_30_DEGREES = 0.577;
   private static final double TANGENT_45_DEGREES = 1.0;
   private static final boolean VERBOSE = false;
   private static final double DISTANCE_THRESHOLD = 10.0;
   private static final double BACKWARD_DISTANCE_THRASHOLD = 5.0;
   private SwipeGestureRecognizer.SwipeRecognitionState state = SwipeGestureRecognizer.SwipeRecognitionState.IDLE;
   SwipeGestureRecognizer.MultiTouchTracker tracker = new SwipeGestureRecognizer.MultiTouchTracker();
   private ViewScene scene;

   SwipeGestureRecognizer(ViewScene var1) {
      this.scene = var1;
   }

   @Override
   public void notifyBeginTouchEvent(long var1, int var3, boolean var4, int var5) {
      this.tracker.params(var3, var4);
   }

   @Override
   public void notifyNextTouchEvent(long var1, int var3, long var4, int var6, int var7, int var8, int var9) {
      switch (var3) {
         case 811:
            this.tracker.pressed(var4, var1, var6, var7, var8, var9);
            break;
         case 812:
         case 814:
            this.tracker.progress(var4, var1, var8, var9);
            break;
         case 813:
            this.tracker.released(var4, var1, var6, var7, var8, var9);
            break;
         default:
            throw new RuntimeException("Error in swipe gesture recognition: unknown touch state: " + this.state);
      }
   }

   @Override
   public void notifyEndTouchEvent(long var1) {
   }

   private EventType<SwipeEvent> calcSwipeType(SwipeGestureRecognizer.TouchPointTracker var1) {
      double var2 = var1.getDistanceX();
      double var4 = var1.getDistanceY();
      double var6 = Math.abs(var2);
      double var8 = Math.abs(var4);
      boolean var10 = var6 > var8;
      double var11 = var10 ? var2 : var4;
      double var13 = var10 ? var6 : var8;
      double var15 = var10 ? var8 : var6;
      double var17 = var10 ? var1.lengthX : var1.lengthY;
      double var19 = var10 ? var1.maxDeviationY : var1.maxDeviationX;
      double var21 = var10 ? var1.lastXMovement : var1.lastYMovement;
      if (var13 <= 10.0) {
         return null;
      } else if (var15 > var13 * 0.577) {
         return null;
      } else if (var19 > var13 * 1.0) {
         return null;
      } else {
         int var23 = Integer.getInteger("com.sun.javafx.gestures.swipe.maxduration", 300);
         if (var1.getDuration() > (long)var23) {
            return null;
         } else if (var17 > var13 * 1.5) {
            return null;
         } else if (Math.signum(var11) != Math.signum(var21) && Math.abs(var21) > 5.0) {
            return null;
         } else if (var10) {
            return var1.getDistanceX() < 0.0 ? SwipeEvent.SWIPE_LEFT : SwipeEvent.SWIPE_RIGHT;
         } else {
            return var1.getDistanceY() < 0.0 ? SwipeEvent.SWIPE_UP : SwipeEvent.SWIPE_DOWN;
         }
      }
   }

   private void handleSwipeType(EventType<SwipeEvent> var1, SwipeGestureRecognizer.CenterComputer var2, int var3, int var4, boolean var5) {
      if (var1 != null) {
         AccessController.doPrivileged(
            (PrivilegedAction<Void>)(() -> {
               if (this.scene.sceneListener != null) {
                  this.scene
                     .sceneListener
                     .swipeEvent(
                        var1,
                        var3,
                        var2.getX(),
                        var2.getY(),
                        var2.getAbsX(),
                        var2.getAbsY(),
                        (var4 & 1) != 0,
                        (var4 & 4) != 0,
                        (var4 & 8) != 0,
                        (var4 & 16) != 0,
                        var5
                     );
               }

               return null;
            }),
            this.scene.getAccessControlContext()
         );
      }
   }

   private static class CenterComputer {
      double totalAbsX = 0.0;
      double totalAbsY = 0.0;
      double totalX = 0.0;
      double totalY = 0.0;
      int count = 0;

      public void add(double var1, double var3, double var5, double var7) {
         this.totalAbsX += var5;
         this.totalAbsY += var7;
         this.totalX += var1;
         this.totalY += var3;
         this.count++;
      }

      public double getX() {
         return this.count == 0 ? 0.0 : this.totalX / (double)this.count;
      }

      public double getY() {
         return this.count == 0 ? 0.0 : this.totalY / (double)this.count;
      }

      public double getAbsX() {
         return this.count == 0 ? 0.0 : this.totalAbsX / (double)this.count;
      }

      public double getAbsY() {
         return this.count == 0 ? 0.0 : this.totalAbsY / (double)this.count;
      }

      public void reset() {
         this.totalX = 0.0;
         this.totalY = 0.0;
         this.totalAbsX = 0.0;
         this.totalAbsY = 0.0;
         this.count = 0;
      }
   }

   private class MultiTouchTracker {
      SwipeGestureRecognizer.SwipeRecognitionState state = SwipeGestureRecognizer.SwipeRecognitionState.IDLE;
      Map<Long, SwipeGestureRecognizer.TouchPointTracker> trackers = new HashMap<>();
      SwipeGestureRecognizer.CenterComputer cc = new SwipeGestureRecognizer.CenterComputer();
      int modifiers;
      boolean direct;
      private int touchCount;
      private int currentTouchCount;
      private EventType<SwipeEvent> type;

      public void params(int var1, boolean var2) {
         this.modifiers = var1;
         this.direct = var2;
      }

      public void pressed(long var1, long var3, int var5, int var6, int var7, int var8) {
         this.currentTouchCount++;
         switch (this.state) {
            case IDLE:
               this.currentTouchCount = 1;
               this.state = SwipeGestureRecognizer.SwipeRecognitionState.ADDING;
            case ADDING:
               SwipeGestureRecognizer.TouchPointTracker var9 = new SwipeGestureRecognizer.TouchPointTracker();
               var9.start(var3, (double)var5, (double)var6, (double)var7, (double)var8);
               this.trackers.put(var1, var9);
               break;
            case REMOVING:
               this.state = SwipeGestureRecognizer.SwipeRecognitionState.FAILURE;
         }
      }

      public void released(long var1, long var3, int var5, int var6, int var7, int var8) {
         if (this.state != SwipeGestureRecognizer.SwipeRecognitionState.FAILURE) {
            SwipeGestureRecognizer.TouchPointTracker var9 = this.trackers.get(var1);
            if (var9 == null) {
               this.state = SwipeGestureRecognizer.SwipeRecognitionState.FAILURE;
               throw new RuntimeException("Error in swipe gesture recognition: released unknown touch point");
            }

            var9.end(var3, (double)var5, (double)var6, (double)var7, (double)var8);
            this.cc.add(var9.beginX, var9.beginY, var9.beginAbsX, var9.beginAbsY);
            this.cc.add(var9.endX, var9.endY, var9.endAbsX, var9.endAbsY);
            EventType var10 = SwipeGestureRecognizer.this.calcSwipeType(var9);
            switch (this.state) {
               case IDLE:
                  this.reset();
                  throw new RuntimeException("Error in swipe gesture recognition: released touch point outside of gesture");
               case ADDING:
                  this.state = SwipeGestureRecognizer.SwipeRecognitionState.REMOVING;
                  this.touchCount = this.currentTouchCount;
                  this.type = var10;
                  break;
               case REMOVING:
                  if (this.type != var10) {
                     this.state = SwipeGestureRecognizer.SwipeRecognitionState.FAILURE;
                  }
            }

            this.trackers.remove(var1);
         }

         this.currentTouchCount--;
         if (this.currentTouchCount == 0) {
            if (this.state == SwipeGestureRecognizer.SwipeRecognitionState.REMOVING) {
               SwipeGestureRecognizer.this.handleSwipeType(this.type, this.cc, this.touchCount, this.modifiers, this.direct);
            }

            this.state = SwipeGestureRecognizer.SwipeRecognitionState.IDLE;
            this.reset();
         }
      }

      public void progress(long var1, long var3, int var5, int var6) {
         if (this.state != SwipeGestureRecognizer.SwipeRecognitionState.FAILURE) {
            SwipeGestureRecognizer.TouchPointTracker var7 = this.trackers.get(var1);
            if (var7 == null) {
               this.state = SwipeGestureRecognizer.SwipeRecognitionState.FAILURE;
               throw new RuntimeException("Error in swipe gesture recognition: reported unknown touch point");
            } else {
               var7.progress(var3, (double)var5, (double)var6);
            }
         }
      }

      void reset() {
         this.trackers.clear();
         this.cc.reset();
         this.state = SwipeGestureRecognizer.SwipeRecognitionState.IDLE;
      }
   }

   private static enum SwipeRecognitionState {
      IDLE,
      ADDING,
      REMOVING,
      FAILURE;
   }

   private static class TouchPointTracker {
      long beginTime;
      long endTime;
      double beginX;
      double beginY;
      double endX;
      double endY;
      double beginAbsX;
      double beginAbsY;
      double endAbsX;
      double endAbsY;
      double lengthX;
      double lengthY;
      double maxDeviationX;
      double maxDeviationY;
      double lastXMovement;
      double lastYMovement;
      double lastX;
      double lastY;

      public void start(long var1, double var3, double var5, double var7, double var9) {
         this.beginX = var3;
         this.beginY = var5;
         this.beginAbsX = var7;
         this.beginAbsY = var9;
         this.lastX = var7;
         this.lastY = var9;
         this.beginTime = var1 / 1000000L;
      }

      public void end(long var1, double var3, double var5, double var7, double var9) {
         this.progress(var1, var7, var9);
         this.endX = var3;
         this.endY = var5;
         this.endAbsX = var7;
         this.endAbsY = var9;
         this.endTime = var1 / 1000000L;
      }

      public void progress(long var1, double var3, double var5) {
         double var7 = var3 - this.lastX;
         double var9 = var5 - this.lastY;
         this.lengthX = this.lengthX + Math.abs(var7);
         this.lengthY = this.lengthY + Math.abs(var9);
         this.lastX = var3;
         this.lastY = var5;
         double var11 = Math.abs(var3 - this.beginAbsX);
         if (var11 > this.maxDeviationX) {
            this.maxDeviationX = var11;
         }

         double var13 = Math.abs(var5 - this.beginAbsY);
         if (var13 > this.maxDeviationY) {
            this.maxDeviationY = var13;
         }

         if (Math.signum(var7) == Math.signum(this.lastXMovement)) {
            this.lastXMovement += var7;
         } else {
            this.lastXMovement = var7;
         }

         if (Math.signum(var9) == Math.signum(this.lastYMovement)) {
            this.lastYMovement += var9;
         } else {
            this.lastYMovement = var9;
         }
      }

      public double getDistanceX() {
         return this.endX - this.beginX;
      }

      public double getDistanceY() {
         return this.endY - this.beginY;
      }

      public long getDuration() {
         return this.endTime - this.beginTime;
      }
   }
}
