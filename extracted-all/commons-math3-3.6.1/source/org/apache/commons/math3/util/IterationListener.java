package org.apache.commons.math3.util;

import java.util.EventListener;

public interface IterationListener extends EventListener {
   void initializationPerformed(IterationEvent var1);

   void iterationPerformed(IterationEvent var1);

   void iterationStarted(IterationEvent var1);

   void terminationPerformed(IterationEvent var1);
}
