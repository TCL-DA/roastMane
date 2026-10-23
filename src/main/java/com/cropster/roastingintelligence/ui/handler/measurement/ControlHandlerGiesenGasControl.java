package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ControlSlider;
import java.math.BigDecimal;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Measure;

public class ControlHandlerGiesenGasControl extends AbstractControlHandler {
   public ControlHandlerGiesenGasControl(ControlSlider controlSlider) {
      super(controlSlider);
      controlSlider.getControlPanel().disableProperty().bind(controlSlider.getAutoModeToggle().onProperty());
      controlSlider.getAutoModeToggle().setVisible(true);
      controlSlider.getAutoModeToggle().onProperty().addListener((s, o, n) -> {
         if (controlSlider.getIsUserInput().getAndSet(true)) {
            if (n) {
               controlSlider.writeToDevice(-1);
            } else {
               controlSlider.writeToDevice(controlSlider.getSlider().getValue());
            }
         }
      });
   }

   public boolean isInAutoMode() {
      return this.controlSlider.getAutoModeToggle().isOn();
   }

   @Override
   public void process(Channel channel, CombinedMeasure combinedMeasure) {
      Measure measure = combinedMeasure.getMeasure();
      if (measure == null) {
         this.controlSlider.setDisable(true);
      } else {
         if (this.controlSlider.isDisable()) {
            this.controlSlider.setDisable(false);
         }

         if (!this.controlSlider.getSlider().isValueChanging()) {
            if (this.controlSlider.getWriteToDeviceIsPending().get()) {
               BigDecimal receivedValue = ControlSlider.toBigDecimal(measure.getAmount());
               if (receivedValue.equals(this.controlSlider.getPendingValueToWrite())) {
                  this.controlSlider.getWriteToDeviceIsPending().set(false);
               }
            } else {
               double value = measure.getAmount();
               if (this.isInAutoMode()) {
                  if (value != -1.0) {
                     this.controlSlider.getIsUserInput().set(false);
                     this.controlSlider.getAutoModeToggle().setOn(false);
                     this.controlSlider.getIsUserInput().set(true);
                  }
               } else if (ControlSlider.toBigDecimal(value).equals(ControlSlider.toBigDecimal(-1.0))) {
                  this.controlSlider.getIsUserInput().set(false);
                  this.controlSlider.getAutoModeToggle().setOn(true);
                  this.controlSlider.getIsUserInput().set(true);
               } else {
                  this.controlSlider.setSliderWithoutDeviceWrite(value);
               }
            }
         }
      }
   }
}
