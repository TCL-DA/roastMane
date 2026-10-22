package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.ui.components.ControlSwitch;
import java.util.Map;
import org.cropster.roastingintelligence.model.ControlState;

public abstract class AbstractControlSwitchStateHandler implements StateHandler {
   protected final Map<ControlState, ControlSwitch> controlSwitches;

   public AbstractControlSwitchStateHandler(Map<ControlState, ControlSwitch> controlSwitches) {
      this.controlSwitches = controlSwitches;
   }

   public abstract void process(ControlState var1, Boolean var2, ControlSwitch var3);

   @Override
   public void propagateValues(ControlState state, Boolean value, StateHandlerChain chain) {
      ControlSwitch controlSwitch = this.getControlSwitches().get(state);
      if (controlSwitch != null) {
         this.process(state, value, controlSwitch);
      }

      chain.propagateValues(state, value);
   }

   public Map<ControlState, ControlSwitch> getControlSwitches() {
      return this.controlSwitches;
   }
}
