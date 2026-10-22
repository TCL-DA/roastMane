package org.glassfish.jersey.client.spi;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.core.Response;
import java.util.Deque;
import java.util.Optional;
import org.glassfish.jersey.Beta;
import org.glassfish.jersey.spi.Contract;

@Contract
@ConstrainedTo(RuntimeType.CLIENT)
@Beta
public interface PostInvocationInterceptor {
   void afterRequest(ClientRequestContext var1, ClientResponseContext var2);

   void onException(ClientRequestContext var1, PostInvocationInterceptor.ExceptionContext var2);

   public interface ExceptionContext {
      Optional<ClientResponseContext> getResponseContext();

      Deque<Throwable> getThrowables();

      void resolve(Response var1);
   }
}
