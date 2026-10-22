package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import com.cropster.roastingintelligence.ui.components.ChannelStatusLabel;
import java.util.Map;
import org.cropster.roastingintelligence.model.Channel;

public abstract class AbstractChannelStatusHandler implements MeasurementHandler {
   protected Map<String, ChannelStatusLabel> channelStatusPanels;

   public AbstractChannelStatusHandler(Map<String, ChannelStatusLabel> channelStatusPanels) {
      this.channelStatusPanels = channelStatusPanels;
   }

   public abstract void process(Channel var1, CombinedMeasure var2, ChannelStatusLabel var3, HandlerChain var4);

   @Override
   public void propagateValues(Channel channel, CombinedMeasure combinedMeasure, HandlerChain chain) {
      ChannelStatusLabel channelStatusPanel = this.channelStatusPanels.get(channel.getName());
      if (channelStatusPanel != null) {
         this.process(channel, combinedMeasure, channelStatusPanel, chain);
      }
   }

   public Map<String, ChannelStatusLabel> getChannelStatusPanel() {
      return this.channelStatusPanels;
   }

   public void setChannelStatusPanel(Map<String, ChannelStatusLabel> channelStatusPanel) {
      this.channelStatusPanels = channelStatusPanel;
   }
}
