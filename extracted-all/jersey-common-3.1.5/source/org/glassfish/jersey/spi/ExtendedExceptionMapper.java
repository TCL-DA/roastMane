package org.glassfish.jersey.spi;

import jakarta.ws.rs.ext.ExceptionMapper;

public interface ExtendedExceptionMapper<T extends Throwable> extends ExceptionMapper<T> {
   boolean isMappable(T var1);
}
