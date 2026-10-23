package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ChannelStatusLabel;
import java.util.Map;
import org.cropster.roastingintelligence.model.Channel;

public class ChannelStatusHandler extends AbstractChannelStatusHandler {
   protected Map<String, ChannelStatusLabel> channelStatusPanels;

   public ChannelStatusHandler(Map<String, ChannelStatusLabel> channelStatusPanels) {
      super(channelStatusPanels);
   }

   @Override
   public void process(Channel channel, CombinedMeasure combinedMeasure, ChannelStatusLabel channelStatusPanel, HandlerChain chain) {
      channelStatusPanel.setValue(combinedMeasure.getMeasure());
      if (channel.isRorEnabled() && channel.isRorSupported()) {
         channelStatusPanel.setRorValue(combinedMeasure.getRorMeasure());
      }
   }
}
