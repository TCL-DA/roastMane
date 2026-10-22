package org.glassfish.jersey.client.spi;

import javax.net.ssl.SSLContext;

public interface DefaultSslContextProvider {
   SSLContext getDefaultSslContext();
}
