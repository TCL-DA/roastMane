package org.cropster.roastingintelligence.measurement;

import java.io.IOException;
import org.cropster.roastingintelligence.model.MeasurementModel;

public interface MeasurementMonitor {
   MeasurementSource getSource();

   void addModel(MeasurementModel var1);

   void removeModel(MeasurementModel var1);

   void start() throws IOException;

   void stop() throws IOException;
}
