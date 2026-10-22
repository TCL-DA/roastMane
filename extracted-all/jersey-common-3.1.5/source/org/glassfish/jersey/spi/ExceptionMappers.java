package org.glassfish.jersey.spi;

import jakarta.ws.rs.ext.ExceptionMapper;

public interface ExceptionMappers {
   <T extends Throwable> ExceptionMapper<T> find(Class<T> var1);

   <T extends Throwable> ExceptionMapper<T> findMapping(T var1);
}
