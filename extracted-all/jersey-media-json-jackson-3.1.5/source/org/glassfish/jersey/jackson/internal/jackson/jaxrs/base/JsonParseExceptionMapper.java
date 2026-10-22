package org.glassfish.jersey.jackson.internal.jackson.jaxrs.base;

import com.fasterxml.jackson.core.JsonParseException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;

public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {
   public Response toResponse(JsonParseException exception) {
      return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).type("text/plain").build();
   }
}
