package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.ui.components.ControlStateBar;
import com.cropster.roastingintelligence.ui.components.ControlSwitch;
import java.util.Map;
import org.cropster.roastingintelligence.model.ControlState;

public class ControlStateBarHandler extends AbstractControlSwitchStateHandler {
   private final ControlStateBar controlStateBar;

   public ControlStateBarHandler(ControlStateBar controlStateBar) {
      super(controlStateBar.getControlSwitches());
      this.controlStateBar = controlStateBar;
   }

   @Override
   public void process(ControlState state, Boolean value, ControlSwitch controlSwitch) {
      if (state != null) {
         if (value == null) {
            controlSwitch.setShowNoConnection(true);
         } else {
            if (controlSwitch.isShowNoConnection()) {
               controlSwitch.setShowNoConnection(false);
            }

            controlSwitch.setStateWithoutDeviceWrite(value);
         }
      }
   }

   @Override
   public Map<ControlState, ControlSwitch> getControlSwitches() {
      return this.controlStateBar.getControlSwitches();
   }
}
