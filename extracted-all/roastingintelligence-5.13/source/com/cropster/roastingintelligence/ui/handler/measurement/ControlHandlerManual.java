package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ControlSlider;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Measure;

public class ControlHandlerManual extends ControlHandlerMode {
   private boolean sliderFollowsControlMeasurement = true;

   public ControlHandlerManual(ControlSlider controlSlider) {
      super(controlSlider);
      controlSlider.getAutoModeToggle().onProperty().addListener((s, o, n) -> {
         if (this.autoModeIsUserInput.get()) {
            if (!this.isAutoModeMeasurement && o && !n) {
               controlSlider.writeToDevice(controlSlider.getSlider().getValue());
            }
         }
      });
   }

   public boolean isSliderFollowsControlMeasurement() {
      return this.sliderFollowsControlMeasurement;
   }

   public void setSliderFollowsControlMeasurement(boolean sliderFollowsControlMeasurement) {
      this.sliderFollowsControlMeasurement = sliderFollowsControlMeasurement;
   }

   @Override
   public void process(Channel channel, CombinedMeasure combinedMeasure) {
      Measure measure = combinedMeasure.getMeasure();
      this.processManual(channel, measure, this.getAutoMode());
   }

   public void processManual(Channel channel, Measure measure, Boolean autoMode) {
      if (super.process(channel, measure, autoMode)) {
         if (autoMode != null) {
            if (measure != null) {
               if (!autoMode || autoMode && this.sliderFollowsControlMeasurement) {
                  this.controlSlider.setSliderWithoutDeviceWrite(measure.getAmount());
               }
            }
         }
      }
   }
}
