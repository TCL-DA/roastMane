package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;

public class SpinnerBehavior<T> extends BehaviorBase<Spinner<T>> {
   private static final double INITIAL_DURATION_MS = 750.0;
   private final InputMap<Spinner<T>> spinnerInputMap;
   private static final int STEP_AMOUNT = 1;
   private boolean isIncrementing = false;
   private Timeline timeline;
   final EventHandler<ActionEvent> spinningKeyFrameEventHandler = var1x -> {
      SpinnerValueFactory var2 = this.getNode().getValueFactory();
      if (var2 != null) {
         if (this.isIncrementing) {
            this.increment(1);
         } else {
            this.decrement(1);
         }
      }
   };

   public SpinnerBehavior(Spinner<T> var1) {
      super(var1);
      this.spinnerInputMap = this.createInputMap();
      this.addDefaultMapping(this.spinnerInputMap, new InputMap.Mapping[]{new InputMap.KeyMapping(KeyCode.UP, KeyEvent.KEY_PRESSED, var1x -> {
         if (this.arrowsAreVertical()) {
            this.increment(1);
         } else {
            FocusTraversalInputMap.traverseUp(var1x);
         }
      }), new InputMap.KeyMapping(KeyCode.RIGHT, KeyEvent.KEY_PRESSED, var1x -> {
         if (!this.arrowsAreVertical()) {
            this.increment(1);
         } else {
            FocusTraversalInputMap.traverseRight(var1x);
         }
      }), new InputMap.KeyMapping(KeyCode.LEFT, KeyEvent.KEY_PRESSED, var1x -> {
         if (!this.arrowsAreVertical()) {
            this.decrement(1);
         } else {
            FocusTraversalInputMap.traverseLeft(var1x);
         }
      }), new InputMap.KeyMapping(KeyCode.DOWN, KeyEvent.KEY_PRESSED, var1x -> {
         if (this.arrowsAreVertical()) {
            this.decrement(1);
         } else {
            FocusTraversalInputMap.traverseDown(var1x);
         }
      })});
   }

   @Override
   public InputMap<Spinner<T>> getInputMap() {
      return this.spinnerInputMap;
   }

   public void increment(int var1) {
      this.getNode().increment(var1);
   }

   public void decrement(int var1) {
      this.getNode().decrement(var1);
   }

   public void startSpinning(boolean var1) {
      this.isIncrementing = var1;
      if (this.timeline != null) {
         this.timeline.stop();
      }

      this.timeline = new Timeline();
      this.timeline.setCycleCount(-1);
      this.timeline.setDelay(this.getNode().getInitialDelay());
      KeyFrame var2 = new KeyFrame(Duration.ZERO, this.spinningKeyFrameEventHandler, new KeyValue[0]);
      KeyFrame var3 = new KeyFrame(this.getNode().getRepeatDelay(), new KeyValue[0]);
      this.timeline.getKeyFrames().setAll(new KeyFrame[]{var2, var3});
      this.timeline.playFromStart();
      this.spinningKeyFrameEventHandler.handle(null);
   }

   public void stopSpinning() {
      if (this.timeline != null) {
         this.timeline.stop();
      }
   }

   public boolean arrowsAreVertical() {
      ObservableList var1 = this.getNode().getStyleClass();
      return !var1.contains("arrows-on-left-horizontal") && !var1.contains("arrows-on-right-horizontal") && !var1.contains("split-arrows-horizontal");
   }
}
