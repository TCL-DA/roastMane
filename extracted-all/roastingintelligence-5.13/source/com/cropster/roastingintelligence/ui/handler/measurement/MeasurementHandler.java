package com.cropster.roastingintelligence.ui.handler.measurement;

import com.cropster.roastingintelligence.model.CombinedMeasure;
import org.cropster.roastingintelligence.model.Channel;

public interface MeasurementHandler {
   void propagateValues(Channel var1, CombinedMeasure var2, HandlerChain var3);
}
