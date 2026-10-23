package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ControlSlider;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Measure;

public class ControlHandlerManualMeasurement extends AbstractControlHandler {
   protected String measurement;

   public ControlHandlerManualMeasurement(String measurement, ControlSlider controlSlider) {
      super(controlSlider);
      this.measurement = measurement;
   }

   public boolean isInAutoMode() {
      return this.controlSlider.getAutoModeToggle().isOn();
   }

   public Boolean getAutoMode() {
      return this.measurementReaderWriter.getAutoMode(this.controlMeasurement);
   }

   @Override
   public void propagateValues(Channel channel, CombinedMeasure combinedMeasure, HandlerChain chain) {
      if (this.measurement.equals(channel.getName())) {
         this.process(channel, combinedMeasure);
      }

      chain.propagateValues(channel, combinedMeasure);
   }

   @Override
   public void process(Channel channel, CombinedMeasure combinedMeasure) {
      this.process(channel, combinedMeasure, this.getAutoMode());
   }

   public void process(Channel channel, CombinedMeasure combinedMeasure, Boolean autoMode) {
      if (autoMode != null) {
         if (autoMode) {
            Measure measure = combinedMeasure.getMeasure();
            if (measure == null) {
               return;
            }

            this.controlSlider.setSliderWithoutDeviceWrite(measure.getAmount());
         }
      }
   }
}
