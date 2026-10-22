package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import java.util.Collection;
import java.util.LinkedList;
import org.cropster.roastingintelligence.model.Channel;
import org.cropster.roastingintelligence.model.ControlState;

public class HandlerChainManager {
   private final LinkedList<MeasurementHandler> handlers = new LinkedList<>();
   private final LinkedList<StateHandler> stateHandlers = new LinkedList<>();

   public HandlerChainManager(
      MeasurementHandler defaultHandler,
      Collection<MeasurementHandler> controlHandlers,
      Collection<MeasurementHandler> channelStatusHandlers,
      Collection<StateHandler> controStateHandlers
   ) {
      this.handlers.addAll(controlHandlers);
      this.handlers.addAll(channelStatusHandlers);
      this.handlers.add(defaultHandler);
      this.stateHandlers.addAll(controStateHandlers);
   }

   public void propagateValues(Channel channel, CombinedMeasure combinedMeasure) {
      new HandlerChain(this.handlers).propagateValues(channel, combinedMeasure);
   }

   public void propagateValues(ControlState state, Boolean value) {
      new StateHandlerChain(this.stateHandlers).propagateValues(state, value);
   }
}
