package org.eclipse.jetty.client;

public interface ProtocolHandler {
   String getName();

   boolean accept(Request var1, Response var2);

   Response.Listener getResponseListener();
}
