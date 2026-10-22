package org.glassfish.jersey.server.spi;

import jakarta.validation.ValidationException;
import org.glassfish.jersey.server.model.Invocable;

public interface ValidationInterceptorContext {
   Object getResource();

   void setResource(Object var1);

   Invocable getInvocable();

   Object[] getArgs();

   void setArgs(Object[] var1);

   void proceed() throws ValidationException;
}
