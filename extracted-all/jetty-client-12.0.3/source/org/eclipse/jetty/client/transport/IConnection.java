package org.eclipse.jetty.client.transport;

import org.eclipse.jetty.client.Connection;

public interface IConnection extends Connection {
   SendFailure send(HttpExchange var1);
}
