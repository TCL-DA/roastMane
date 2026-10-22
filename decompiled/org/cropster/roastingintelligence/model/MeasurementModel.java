package org.cropster.roastingintelligence.model;

import java.util.Collection;
import java.util.Date;

public interface MeasurementModel {
   Date getStartDate();

   Collection<Channel> getChannels();

   CurveModel getCurveModel(Channel var1);

   CurveModel getRorModel(Channel var1);

   void put(Channel var1, Date var2, Double var3);

   default Channel getChannel(String channelName) {
      return this.getChannels().stream().filter(c -> c.getName().equals(channelName)).findFirst().orElse(null);
   }

   default Roast getCurrentReference() {
      return null;
   }

   Collection<ControlState> getControlStates();

   void putState(ControlState var1, Boolean var2);

   Boolean getState(ControlState var1);

   default Roast getRoast() {
      return null;
   }
}
