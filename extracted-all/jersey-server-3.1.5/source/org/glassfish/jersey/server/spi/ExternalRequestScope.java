package org.glassfish.jersey.server.spi;

import org.glassfish.jersey.internal.inject.InjectionManager;

public interface ExternalRequestScope<T> extends AutoCloseable {
   ExternalRequestContext<T> open(InjectionManager var1);

   void suspend(ExternalRequestContext<T> var1, InjectionManager var2);

   void resume(ExternalRequestContext<T> var1, InjectionManager var2);

   @Override
   void close();
}
