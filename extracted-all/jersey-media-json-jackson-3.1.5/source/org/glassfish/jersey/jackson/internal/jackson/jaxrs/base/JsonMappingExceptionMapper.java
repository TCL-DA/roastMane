package org.glassfish.jersey.jackson.internal.jackson.jaxrs.base;

import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;

public class JsonMappingExceptionMapper implements ExceptionMapper<JsonMappingException> {
   public Response toResponse(JsonMappingException exception) {
      return Response.status(Status.BAD_REQUEST).entity(exception.getMessage()).type("text/plain").build();
   }
}
