package com.cropster.roastingintelligence.ui.handler.measurement;

import java.util.Collection;
import java.util.Iterator;
import org.cropster.roastingintelligence.model.ControlState;

public class StateHandlerChain {
   private final Iterator<StateHandler> handlerIt;

   public StateHandlerChain(Collection<StateHandler> handlers) {
      this.handlerIt = handlers.iterator();
   }

   public void propagateValues(ControlState state, Boolean value) {
      if (this.handlerIt.hasNext()) {
         StateHandler handler = this.handlerIt.next();
         handler.propagateValues(state, value, this);
      }
   }
}
