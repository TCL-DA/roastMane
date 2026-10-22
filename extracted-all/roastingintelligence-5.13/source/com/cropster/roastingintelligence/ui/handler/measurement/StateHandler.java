package com.cropster.roastingintelligence.ui.handler.measurement;

import org.cropster.roastingintelligence.model.ControlState;

public interface StateHandler {
   void propagateValues(ControlState var1, Boolean var2, StateHandlerChain var3);
}
