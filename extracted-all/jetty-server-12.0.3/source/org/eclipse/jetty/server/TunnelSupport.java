package org.eclipse.jetty.server;

import org.eclipse.jetty.io.EndPoint;

public interface TunnelSupport {
   String getProtocol();

   EndPoint getEndPoint();
}
