package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ControlSlider;
import com.cropster.util.model.Unit;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.model.Channel;

public abstract class AbstractControlHandler implements MeasurementHandler {
   protected ControlSlider controlSlider;
   protected String controlMeasurement;
   protected Unit unit;
   protected Unit sourceUnit;
   protected MeasurementReaderWriter measurementReaderWriter;

   protected AbstractControlHandler(ControlSlider controlSlider) {
      this.controlSlider = controlSlider;
      this.unit = controlSlider.getUnit();
      this.sourceUnit = controlSlider.getSourceUnit();
      this.measurementReaderWriter = controlSlider.getSource();
      this.controlMeasurement = controlSlider.getMeasurement();
      controlSlider.visibleProperty().bind(controlSlider.configuredProperty().and(controlSlider.displayableProperty()));
      controlSlider.getToggle().visibleProperty().bind(controlSlider.configuredProperty().and(controlSlider.getToggle().displayableProperty()));
   }

   public abstract void process(Channel var1, CombinedMeasure var2);

   @Override
   public void propagateValues(Channel channel, CombinedMeasure combinedMeasure, HandlerChain chain) {
      if (this.controlMeasurement.equals(channel.getName())) {
         this.process(channel, combinedMeasure);
      }

      chain.propagateValues(channel, combinedMeasure);
   }

   ControlSlider getControlSlider() {
      return this.controlSlider;
   }
}
