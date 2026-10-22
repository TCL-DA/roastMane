package org.eclipse.jetty.client;

import java.util.concurrent.CompletableFuture;
import org.eclipse.jetty.util.Promise;
import org.eclipse.jetty.util.Promise.Completable;

public interface Destination {
   Origin getOrigin();

   boolean isSecure();

   ProxyConfiguration.Proxy getProxy();

   ConnectionPool getConnectionPool();

   HttpClient getHttpClient();

   void newConnection(Promise<Connection> var1);

   default CompletableFuture<Connection> newConnection() {
      Completable<Connection> promise = new Completable();
      this.newConnection(promise);
      return promise;
   }

   void send(Request var1, Response.CompleteListener var2);
}
