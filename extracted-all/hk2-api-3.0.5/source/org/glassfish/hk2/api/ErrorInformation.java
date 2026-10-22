package org.glassfish.hk2.api;

public interface ErrorInformation {
   ErrorType getErrorType();

   Descriptor getDescriptor();

   Injectee getInjectee();

   MultiException getAssociatedException();
}
