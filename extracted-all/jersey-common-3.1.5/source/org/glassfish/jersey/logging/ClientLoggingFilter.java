package org.glassfish.jersey.logging;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import java.io.IOException;
import java.io.OutputStream;
import org.glassfish.jersey.message.MessageUtils;

@ConstrainedTo(RuntimeType.CLIENT)
@PreMatching
@Priority(Integer.MAX_VALUE)
final class ClientLoggingFilter extends LoggingInterceptor implements ClientRequestFilter, ClientResponseFilter {
   public ClientLoggingFilter(LoggingFeature.LoggingFeatureBuilder builder) {
      super(builder);
   }

   public void filter(ClientRequestContext context) throws IOException {
      if (this.logger.isLoggable(this.level)) {
         long id = this._id.incrementAndGet();
         context.setProperty(LOGGING_ID_PROPERTY, id);
         StringBuilder b = new StringBuilder();
         this.printRequestLine(b, "Sending client request", id, context.getMethod(), context.getUri());
         this.printPrefixedHeaders(b, id, "> ", context.getStringHeaders());
         if (printEntity(this.verbosity, context.getMediaType()) && context.hasEntity()) {
            OutputStream stream = new LoggingInterceptor.LoggingStream(b, context.getEntityStream());
            context.setEntityStream(stream);
            context.setProperty(ENTITY_LOGGER_PROPERTY, stream);
         } else {
            this.log(b);
         }
      }
   }

   public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
      if (this.logger.isLoggable(this.level)) {
         Object requestId = requestContext.getProperty(LOGGING_ID_PROPERTY);
         long id = requestId != null ? (Long)requestId : this._id.incrementAndGet();
         StringBuilder b = new StringBuilder();
         this.printResponseLine(b, "Client response received", id, responseContext.getStatus());
         this.printPrefixedHeaders(b, id, "< ", responseContext.getHeaders());
         if (printEntity(this.verbosity, responseContext.getMediaType()) && responseContext.hasEntity()) {
            responseContext.setEntityStream(
               this.logInboundEntity(b, responseContext.getEntityStream(), MessageUtils.getCharset(responseContext.getMediaType()))
            );
         }

         this.log(b);
      }
   }
}
