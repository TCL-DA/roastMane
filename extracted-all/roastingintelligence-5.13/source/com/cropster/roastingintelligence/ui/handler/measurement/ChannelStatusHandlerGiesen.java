package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ChannelStatusLabel;
import java.util.Map;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.Measure;

public class ChannelStatusHandlerGiesen extends ChannelStatusHandler {
   public ChannelStatusHandlerGiesen(Map<String, ChannelStatusLabel> channelStatusPanels) {
      super(channelStatusPanels);
   }

   @Override
   public void process(Channel channel, CombinedMeasure combinedMeasure, ChannelStatusLabel channelStatusPanel, HandlerChain chain) {
      if (!"gasControl".equals(channel.getName())) {
         chain.propagateValues(channel, combinedMeasure);
      } else {
         Measure measure = combinedMeasure.getMeasure();
         if (measure == null || measure.getAmount() != -1.0) {
            channelStatusPanel.setValue(measure);
            channelStatusPanel.setAutoMode(false);
         } else if (measure.getAmount() == -1.0) {
            channelStatusPanel.setAutoMode(true);
         }
      }
   }
}
