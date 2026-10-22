package org.eclipse.jetty.server;

import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.server.internal.HttpChannelState;
import org.eclipse.jetty.util.thread.Invocable;

public interface HttpChannel extends Invocable {
   ConnectionMetaData getConnectionMetaData();

   void setHttpStream(HttpStream var1);

   boolean isRequestHandled();

   Runnable onRequest(org.eclipse.jetty.http.MetaData.Request var1);

   Request getRequest();

   Runnable onContentAvailable();

   Runnable onIdleTimeout(TimeoutException var1);

   Runnable onFailure(Throwable var1);

   void recycle();

   public static class DefaultFactory implements HttpChannel.Factory {
      @Override
      public HttpChannel newHttpChannel(ConnectionMetaData connectionMetaData) {
         return new HttpChannelState(connectionMetaData);
      }
   }

   public interface Factory {
      HttpChannel newHttpChannel(ConnectionMetaData var1);
   }
}
