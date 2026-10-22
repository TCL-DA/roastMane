package org.eclipse.yasson.internal.model;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;

class ModulesUtil {
   private ModulesUtil() {
   }

   static Lookup lookup() {
      return MethodHandles.publicLookup();
   }
}
