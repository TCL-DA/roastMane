package org.aopalliance.instrument;

import org.aopalliance.reflect.Locator;

public interface Instrumentation {
   int ADD_INTERFACE = 0;
   int SET_SUPERCLASS = 1;
   int ADD_CLASS = 2;
   int ADD_BEFORE_CODE = 3;
   int ADD_AFTER_CODE = 4;
   int ADD_METADATA = 5;

   Locator getLocation();

   int getType();
}
