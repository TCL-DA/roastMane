package org.cropster.roastingintelligence.measurement;

import java.util.Map;
import org.cropster.roastingintelligence.model.ControlState;

public interface ControlStateSource extends MeasurementSource {
   Map<ControlState, Boolean> getStates();

   Boolean getState(ControlState var1);

   void setState(ControlState var1, Boolean var2);
}
