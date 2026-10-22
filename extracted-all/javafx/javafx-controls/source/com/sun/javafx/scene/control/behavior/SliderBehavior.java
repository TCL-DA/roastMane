package com.sun.javafx.scene.control.behavior;

import com.sun.javafx.scene.control.inputmap.InputMap;
import com.sun.javafx.scene.control.skin.Utils;
import javafx.geometry.Orientation;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class SliderBehavior extends BehaviorBase<Slider> {
   private final InputMap<Slider> sliderInputMap = this.createInputMap();
   private TwoLevelFocusBehavior tlFocus;

   public SliderBehavior(Slider var1) {
      super(var1);
      this.addDefaultMapping(
         this.sliderInputMap,
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
      this.addDefaultChildMap(this.sliderInputMap, var2);
      InputMap var3 = new InputMap(var1);
      var3.setInterceptor(var1x -> var1.getOrientation() != Orientation.VERTICAL);
      var3.getMappings()
         .addAll(
            new InputMap.Mapping[]{
               new InputMap.KeyMapping(KeyCode.DOWN, var1x -> this.decrementValue()),
               new InputMap.KeyMapping(KeyCode.KP_DOWN, var1x -> this.decrementValue()),
               new InputMap.KeyMapping(KeyCode.UP, var1x -> this.incrementValue()),
               new InputMap.KeyMapping(KeyCode.KP_UP, var1x -> this.incrementValue())
            }
         );
      this.addDefaultChildMap(this.sliderInputMap, var3);
      if (Utils.isTwoLevelFocus()) {
         this.tlFocus = new TwoLevelFocusBehavior(var1);
      }
   }

   @Override
   public void dispose() {
      if (this.tlFocus != null) {
         this.tlFocus.dispose();
      }

      super.dispose();
   }

   @Override
   public InputMap<Slider> getInputMap() {
      return this.sliderInputMap;
   }

   public void trackPress(MouseEvent var1, double var2) {
      Slider var4 = this.getNode();
      if (!var4.isFocused()) {
         var4.requestFocus();
      }

      if (var4.getOrientation().equals(Orientation.HORIZONTAL)) {
         var4.adjustValue(var2 * (var4.getMax() - var4.getMin()) + var4.getMin());
      } else {
         var4.adjustValue((1.0 - var2) * (var4.getMax() - var4.getMin()) + var4.getMin());
      }
   }

   public void thumbPressed(MouseEvent var1, double var2) {
      Slider var4 = this.getNode();
      if (!var4.isFocused()) {
         var4.requestFocus();
      }

      var4.setValueChanging(true);
   }

   public void thumbDragged(MouseEvent var1, double var2) {
      Slider var4 = this.getNode();
      var4.setValue(com.sun.javafx.util.Utils.clamp(var4.getMin(), var2 * (var4.getMax() - var4.getMin()) + var4.getMin(), var4.getMax()));
   }

   public void thumbReleased(MouseEvent var1) {
      Slider var2 = this.getNode();
      var2.setValueChanging(false);
      var2.adjustValue(var2.getValue());
   }

   void home() {
      Slider var1 = this.getNode();
      var1.adjustValue(var1.getMin());
   }

   void decrementValue() {
      Slider var1 = this.getNode();
      if (var1.isSnapToTicks()) {
         var1.adjustValue(var1.getValue() - this.computeIncrement());
      } else {
         var1.decrement();
      }
   }

   void end() {
      Slider var1 = this.getNode();
      var1.adjustValue(var1.getMax());
   }

   void incrementValue() {
      Slider var1 = this.getNode();
      if (var1.isSnapToTicks()) {
         var1.adjustValue(var1.getValue() + this.computeIncrement());
      } else {
         var1.increment();
      }
   }

   double computeIncrement() {
      Slider var1 = this.getNode();
      double var2 = 0.0;
      if (var1.getMinorTickCount() != 0) {
         var2 = var1.getMajorTickUnit() / (double)(Math.max(var1.getMinorTickCount(), 0) + 1);
      } else {
         var2 = var1.getMajorTickUnit();
      }

      return var1.getBlockIncrement() > 0.0 && var1.getBlockIncrement() < var2 ? var2 : var1.getBlockIncrement();
   }
}
