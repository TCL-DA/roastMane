package org.glassfish.jersey.server.internal.inject;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.glassfish.jersey.server.model.Invocable;
import org.glassfish.jersey.spi.Contract;

@Contract
public interface ConfiguredValidator extends Validator {
   void validateResourceAndInputParams(Object var1, Invocable var2, Object[] var3) throws ConstraintViolationException;

   void validateResult(Object var1, Invocable var2, Object var3) throws ConstraintViolationException;
}
