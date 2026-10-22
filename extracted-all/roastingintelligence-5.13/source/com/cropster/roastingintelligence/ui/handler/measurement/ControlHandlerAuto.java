package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ControlSlider;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Measure;

public class ControlHandlerAuto extends ControlHandlerMode {
   public ControlHandlerAuto(ControlSlider controlSlider) {
      super(controlSlider);
   }

   @Override
   public void process(Channel channel, CombinedMeasure combinedMeasure) {
      Measure measure = combinedMeasure.getMeasure();
      this.processAuto(channel, measure, this.getAutoMode());
   }

   public void processAuto(Channel channel, Measure measure, Boolean autoMode) {
      if (super.process(channel, measure, autoMode)) {
         if (autoMode != null) {
            if (measure != null) {
               this.controlSlider.setSliderWithoutDeviceWrite(measure.getAmount());
            }
         }
      }
   }
}
