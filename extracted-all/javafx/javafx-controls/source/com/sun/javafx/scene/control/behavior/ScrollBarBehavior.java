package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.util.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.control.ScrollBar;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

public class ScrollBarBehavior extends BehaviorBase<ScrollBar> {
   private final InputMap<ScrollBar> inputMap = this.createInputMap();
   Timeline timeline;

   public ScrollBarBehavior(ScrollBar var1) {
      super(var1);
      this.addDefaultMapping(
         this.inputMap,
         new InputMap.Mapping[]{
            new InputMap.KeyMapping(KeyCode.HOME, KeyEvent.KEY_RELEASED, var1x -> this.home()),
            new InputMap.KeyMapping(KeyCode.END, KeyEvent.KEY_RELEASED, var1x -> this.end())
         }
      );
      InputMap var2 = new InputMap(var1);
      var2.setInterceptor(var1x -> var1.getOrientation() != Orientation.HORIZONTAL);
      var2.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               new InputMap.KeyMapping(KeyCode.LEFT, var2x -> this.rtl(var1, this::incrementValue, this::decrementValue)),
               new InputMap.KeyMapping(KeyCode.KP_LEFT, var2x -> this.rtl(var1, this::incrementValue, this::decrementValue)),
               new InputMap.KeyMapping(KeyCode.RIGHT, var2x -> this.rtl(var1, this::decrementValue, this::incrementValue)),
               new InputMap.KeyMapping(KeyCode.KP_RIGHT, var2x -> this.rtl(var1, this::decrementValue, this::incrementValue))
            }
         );
      this.addDefaultChildMap(this.inputMap, var2);
      InputMap var3 = new InputMap(var1);
      var3.setInterceptor(var1x -> var1.getOrientation() != Orientation.VERTICAL);
      var3.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               new InputMap.KeyMapping(KeyCode.UP, var1x -> this.decrementValue()),
               new InputMap.KeyMapping(KeyCode.KP_UP, var1x -> this.decrementValue()),
               new InputMap.KeyMapping(KeyCode.DOWN, var1x -> this.incrementValue()),
               new InputMap.KeyMapping(KeyCode.KP_DOWN, var1x -> this.incrementValue())
            }
         );
      this.addDefaultChildMap(this.inputMap, var3);
   }

   @Override
   public InputMap<ScrollBar> getInputMap() {
      return this.inputMap;
   }

   private void home() {
      this.getNode().setValue(this.getNode().getMin());
   }

   private void decrementValue() {
      this.getNode().adjustValue(0.0);
   }

   private void end() {
      this.getNode().setValue(this.getNode().getMax());
   }

   private void incrementValue() {
      this.getNode().adjustValue(1.0);
   }

   public void trackPress(double var1) {
      if (this.timeline == null) {
         ScrollBar var3 = this.getNode();
         if (!var3.isFocused() && var3.isFocusTraversable()) {
            var3.requestFocus();
         }

         boolean var6 = var1 > (var3.getValue() - var3.getMin()) / (var3.getMax() - var3.getMin());
         this.timeline = new Timeline();
         this.timeline.setCycleCount(-1);
         EventHandler var7 = var5 -> {
            boolean var6x = var1 > (var3.getValue() - var3.getMin()) / (var3.getMax() - var3.getMin());
            if (var6 == var6x) {
               var3.adjustValue(var1);
            } else {
               this.stopTimeline();
            }
         };
         KeyFrame var8 = new KeyFrame(Duration.millis(200.0), var7, new KeyValue[0]);
         this.timeline.getKeyFrames().add(var8);
         this.timeline.play();
         var7.handle(null);
      }
   }

   public void trackRelease() {
      this.stopTimeline();
   }

   public void decButtonPressed() {
      ScrollBar var1 = this.getNode();
      if (!var1.isFocused() && var1.isFocusTraversable()) {
         var1.requestFocus();
      }

      this.stopTimeline();
      this.timeline = new Timeline();
      this.timeline.setCycleCount(-1);
      EventHandler var2 = var2x -> {
         if (var1.getValue() > var1.getMin()) {
            var1.decrement();
         } else {
            this.stopTimeline();
         }
      };
      KeyFrame var3 = new KeyFrame(Duration.millis(200.0), var2, new KeyValue[0]);
      this.timeline.getKeyFrames().add(var3);
      this.timeline.play();
      var2.handle(null);
   }

   public void decButtonReleased() {
      this.stopTimeline();
   }

   public void incButtonPressed() {
      ScrollBar var1 = this.getNode();
      if (!var1.isFocused() && var1.isFocusTraversable()) {
         var1.requestFocus();
      }

      this.stopTimeline();
      this.timeline = new Timeline();
      this.timeline.setCycleCount(-1);
      EventHandler var2 = var2x -> {
         if (var1.getValue() < var1.getMax()) {
            var1.increment();
         } else {
            this.stopTimeline();
         }
      };
      KeyFrame var3 = new KeyFrame(Duration.millis(200.0), var2, new KeyValue[0]);
      this.timeline.getKeyFrames().add(var3);
      this.timeline.play();
      var2.handle(null);
   }

   public void incButtonReleased() {
      this.stopTimeline();
   }

   public void thumbDragged(double var1) {
      ScrollBar var3 = this.getNode();
      this.stopTimeline();
      if (!var3.isFocused() && var3.isFocusTraversable()) {
         var3.requestFocus();
      }

      double var4 = var1 * (var3.getMax() - var3.getMin()) + var3.getMin();
      if (!Double.isNaN(var4)) {
         var3.setValue(Utils.clamp(var3.getMin(), var4, var3.getMax()));
      }
   }

   private void stopTimeline() {
      if (this.timeline != null) {
         this.timeline.stop();
         this.timeline = null;
      }
   }
}
