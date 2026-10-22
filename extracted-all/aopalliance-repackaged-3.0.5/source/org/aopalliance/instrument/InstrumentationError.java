package org.aopalliance.instrument;

public class InstrumentationError extends Error {
   public InstrumentationError(Instrumentation instrumentation, Throwable cause) {
      super("Error while instrumenting " + instrumentation, cause);
   }
}
