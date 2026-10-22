package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ChannelStatusLabel;
import java.util.Map;
import org.cropster.roastingintelligence.measurement.MeasurementReaderWriter;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Measure;

public class ChannelStatusHandlerAutoMode extends ChannelStatusHandler {
   protected String measurement;
   protected MeasurementReaderWriter measurementReaderWriter;

   public ChannelStatusHandlerAutoMode(String measurement, MeasurementReaderWriter measurementReaderWriter, Map<String, ChannelStatusLabel> channelStatusPanels) {
      super(channelStatusPanels);
      this.measurement = measurement;
      this.measurementReaderWriter = measurementReaderWriter;
   }

   @Override
   public void process(Channel channel, CombinedMeasure combinedMeasure, ChannelStatusLabel channelStatusPanel, HandlerChain chain) {
      if (!this.measurement.equals(channel.getName())) {
         chain.propagateValues(channel, combinedMeasure);
      } else {
         Boolean autoMode = this.measurementReaderWriter.getAutoMode(channel.getName());
         Measure measure = combinedMeasure.getMeasure();
         if (autoMode == null) {
            channelStatusPanel.setValue(null);
            channelStatusPanel.setAutoMode(false);
         } else if (!autoMode) {
            channelStatusPanel.setValue(measure);
            channelStatusPanel.setAutoMode(false);
         } else if (autoMode) {
            channelStatusPanel.setAutoMode(true);
         }
      }
   }
}
