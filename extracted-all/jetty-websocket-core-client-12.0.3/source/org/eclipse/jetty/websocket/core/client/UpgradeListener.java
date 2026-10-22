package org.eclipse.jetty.websocket.core.client;

import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;

public interface UpgradeListener {
   default void onHandshakeRequest(Request request) {
   }

   default void onHandshakeResponse(Request request, Response response) {
   }
}
