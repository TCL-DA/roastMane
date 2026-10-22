package org.glassfish.jersey.server.spi;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.SeBootstrap.Configuration;
import jakarta.ws.rs.core.Application;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface WebServerProvider {
   <T extends WebServer> T createServer(Class<T> var1, Application var2, Configuration var3) throws ProcessingException;

   <T extends WebServer> T createServer(Class<T> var1, Class<? extends Application> var2, Configuration var3) throws ProcessingException;

   static <T extends WebServer> boolean isSupportedWebServer(Class<? extends WebServer> supportedType, Class<T> userType, Configuration configuration) {
      Object webServerObj = configuration.property("jersey.config.server.bootstrap.webserver.class");
      Class<? extends WebServer> webServerCls = webServerObj != null && !WebServer.class.equals(webServerObj) ? (Class)webServerObj : null;
      return webServerCls != null && webServerCls.isAssignableFrom(supportedType) || webServerCls == null && userType.isAssignableFrom(supportedType);
   }
}
