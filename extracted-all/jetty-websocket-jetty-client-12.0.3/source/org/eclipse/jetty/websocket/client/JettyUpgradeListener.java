package org.eclipse.jetty.websocket.client;

import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;

public interface JettyUpgradeListener {
   default void onHandshakeRequest(Request request) {
   }

   default void onHandshakeResponse(Request request, Response response) {
   }
}
