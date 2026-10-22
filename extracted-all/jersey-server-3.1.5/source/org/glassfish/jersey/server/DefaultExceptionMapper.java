package org.glassfish.jersey.server;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.server.internal.LocalizationMessages;

class DefaultExceptionMapper implements ExceptionMapper<Throwable> {
   public Response toResponse(Throwable exception) {
      return exception instanceof WebApplicationException
         ? processWebApplicationException((WebApplicationException)exception)
         : processDefaultException(exception);
   }

   private static Response processWebApplicationException(WebApplicationException exception) {
      return exception.getResponse() == null ? processDefaultException(exception) : exception.getResponse();
   }

   private static Response processDefaultException(Throwable exception) {
      return Response.status(Status.INTERNAL_SERVER_ERROR).entity(LocalizationMessages.ERROR_EXCEPTION_MAPPING_THROWN_TO_CONTAINER()).build();
   }
}
