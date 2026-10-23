package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ControlSlider;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Measure;

public class ControlHandlerGiesenGas extends AbstractControlHandler {
   public ControlHandlerGiesenGas(ControlSlider controlSlider) {
      super(controlSlider);
   }

   @Override
   public void propagateValues(Channel channel, CombinedMeasure combinedMeasure, HandlerChain chain) {
      if ("gas".equals(channel.getName())) {
         this.process(channel, combinedMeasure);
      }

      chain.propagateValues(channel, combinedMeasure);
   }

   @Override
   public void process(Channel channel, CombinedMeasure combinedMeasure) {
      Measure measure = combinedMeasure.getMeasure();
      if (this.getControlSlider().getAutoModeToggle().isOn()) {
         if (measure == null) {
            this.getControlSlider().setDisable(true);
         } else {
            if (this.getControlSlider().isDisable()) {
               this.getControlSlider().setDisable(false);
            }

            this.getControlSlider().setSliderWithoutDeviceWrite(measure.getAmount());
         }
      }
   }
}
