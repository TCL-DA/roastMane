package org.glassfish.jersey.logging;

import jakarta.annotation.Priority;
import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.PreMatching;
import java.io.IOException;
import java.io.OutputStream;
import org.glassfish.jersey.message.MessageUtils;

@ConstrainedTo(RuntimeType.SERVER)
@PreMatching
@Priority(Integer.MIN_VALUE)
final class ServerLoggingFilter extends LoggingInterceptor implements ContainerRequestFilter, ContainerResponseFilter {
   public ServerLoggingFilter(LoggingFeature.LoggingFeatureBuilder builder) {
      super(builder);
   }

   public void filter(ContainerRequestContext context) throws IOException {
      if (this.logger.isLoggable(this.level)) {
         long id = this._id.incrementAndGet();
         context.setProperty(LOGGING_ID_PROPERTY, id);
         StringBuilder b = new StringBuilder();
         this.printRequestLine(b, "Server has received a request", id, context.getMethod(), context.getUriInfo().getRequestUri());
         this.printPrefixedHeaders(b, id, "> ", context.getHeaders());
         if (printEntity(this.verbosity, context.getMediaType()) && context.hasEntity()) {
            context.setEntityStream(this.logInboundEntity(b, context.getEntityStream(), MessageUtils.getCharset(context.getMediaType())));
         }

         this.log(b);
      }
   }

   public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
      if (this.logger.isLoggable(this.level)) {
         Object requestId = requestContext.getProperty(LOGGING_ID_PROPERTY);
         long id = requestId != null ? (Long)requestId : this._id.incrementAndGet();
         StringBuilder b = new StringBuilder();
         this.printResponseLine(b, "Server responded with a response", id, responseContext.getStatus());
         this.printPrefixedHeaders(b, id, "< ", responseContext.getStringHeaders());
         if (printEntity(this.verbosity, responseContext.getMediaType()) && responseContext.hasEntity()) {
            OutputStream stream = new LoggingInterceptor.LoggingStream(b, responseContext.getEntityStream());
            responseContext.setEntityStream(stream);
            requestContext.setProperty(ENTITY_LOGGER_PROPERTY, stream);
         } else {
            this.log(b);
         }
      }
   }
}
