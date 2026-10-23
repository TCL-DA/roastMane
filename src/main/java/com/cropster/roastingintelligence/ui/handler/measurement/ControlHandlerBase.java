package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ControlSlider;
import java.math.BigDecimal;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Measure;

public class ControlHandlerBase extends AbstractControlHandler {
   public ControlHandlerBase(ControlSlider controlSlider) {
      super(controlSlider);
      this.getControlSlider().getAutoModeToggle().setVisible(false);
   }

   @Override
   public void process(Channel channel, CombinedMeasure combinedMeasure) {
      Measure measure = combinedMeasure.getMeasure();
      this.setDisableState(measure);
      if (measure != null) {
         if (!this.getControlSlider().getSlider().isValueChanging()) {
            double value = measure.getAmount();
            if (this.getControlSlider().getWriteToDeviceIsPending().get()) {
               BigDecimal receivedValue = ControlSlider.round(this.sourceUnit.from(BigDecimal.valueOf(value), this.unit));
               if (receivedValue.equals(this.getControlSlider().getPendingValueToWrite())) {
                  this.getControlSlider().getWriteToDeviceIsPending().set(false);
               }
            } else {
               this.getControlSlider().setSliderWithoutDeviceWrite(measure.getAmount());
            }
         }
      }
   }

   public void setDisableState(Measure measure) {
      if (measure == null) {
         this.getControlSlider().setDisable(true);
      } else if (this.getControlSlider().isDisable()) {
         this.getControlSlider().setDisable(false);
      }
   }
}
