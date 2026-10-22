package org.aopalliance.instrument;

public class UndoNotSupportedException extends Exception {
   public UndoNotSupportedException(Instrumentation instrumentation) {
      super("Undo not supported for instrumentation: " + instrumentation);
   }
}
