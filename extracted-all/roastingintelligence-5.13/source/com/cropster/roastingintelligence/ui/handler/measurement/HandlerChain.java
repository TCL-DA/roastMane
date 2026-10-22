package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import java.util.Collection;
import java.util.Iterator;
import org.cropster.roastingintelligence.model.Channel;

public class HandlerChain {
   private Iterator<MeasurementHandler> handlerIt;

   public HandlerChain(Collection<MeasurementHandler> handlers) {
      this.handlerIt = handlers.iterator();
   }

   public void propagateValues(Channel channel, CombinedMeasure combinedMeasure) {
      if (this.handlerIt.hasNext()) {
         MeasurementHandler handler = this.handlerIt.next();
         handler.propagateValues(channel, combinedMeasure, this);
      }
   }
}
