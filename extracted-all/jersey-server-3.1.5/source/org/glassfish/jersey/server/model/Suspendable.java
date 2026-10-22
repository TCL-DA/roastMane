package org.glassfish.jersey.server.model;

import java.util.concurrent.TimeUnit;

public interface Suspendable {
   boolean isSuspendDeclared();

   boolean isManagedAsyncDeclared();

   long getSuspendTimeout();

   TimeUnit getSuspendTimeoutUnit();
}
