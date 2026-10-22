package org.glassfish.jersey.server.spi;

import jakarta.validation.ValidationException;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface ValidationInterceptor {
   void onValidate(ValidationInterceptorContext var1) throws ValidationException;
}
