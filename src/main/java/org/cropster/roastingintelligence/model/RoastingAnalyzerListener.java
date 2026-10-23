package org.cropster.roastingintelligence.model;

import java.util.Date;
import org.cropster.roastingintelligence.RoastingModel;

public interface RoastingAnalyzerListener {
   void startDetected(RoastingModel var1, Date var2);

   void truningPointDetected(RoastingModel var1, Date var2);

   void endDetected(RoastingModel var1, Date var2);
}
