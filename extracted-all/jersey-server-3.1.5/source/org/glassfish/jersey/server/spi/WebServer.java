package org.glassfish.jersey.server.spi;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import java.util.concurrent.CompletionStage;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.SERVER)
public interface WebServer {
   Container container();

   int port();

   CompletionStage<?> start();

   CompletionStage<?> stop();

   <T> T unwrap(Class<T> var1);
}
