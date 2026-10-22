package org.glassfish.hk2.api;

public interface ValidationInformation {
   Operation getOperation();

   ActiveDescriptor<?> getCandidate();

   Injectee getInjectee();

   Filter getFilter();

   StackTraceElement getCaller();
}
