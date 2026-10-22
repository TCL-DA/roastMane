package org.glassfish.jersey.server.monitoring;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ExtendedUriInfo;

public interface RequestEvent {
   RequestEvent.Type getType();

   ContainerRequest getContainerRequest();

   ContainerResponse getContainerResponse();

   Throwable getException();

   ExtendedUriInfo getUriInfo();

   ExceptionMapper<?> getExceptionMapper();

   Iterable<ContainerRequestFilter> getContainerRequestFilters();

   Iterable<ContainerResponseFilter> getContainerResponseFilters();

   boolean isSuccess();

   boolean isResponseSuccessfullyMapped();

   RequestEvent.ExceptionCause getExceptionCause();

   boolean isResponseWritten();

   public static enum ExceptionCause {
      ORIGINAL,
      MAPPED_RESPONSE;
   }

   public static enum Type {
      START,
      MATCHING_START,
      LOCATOR_MATCHED,
      SUBRESOURCE_LOCATED,
      REQUEST_MATCHED,
      REQUEST_FILTERED,
      RESOURCE_METHOD_START,
      RESOURCE_METHOD_FINISHED,
      RESP_FILTERS_START,
      RESP_FILTERS_FINISHED,
      ON_EXCEPTION,
      EXCEPTION_MAPPER_FOUND,
      EXCEPTION_MAPPING_FINISHED,
      FINISHED;
   }
}
