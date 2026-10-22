package org.glassfish.jersey.server.internal.monitoring;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.monitoring.RequestEvent;

public class EmptyRequestEventBuilder implements RequestEventBuilder {
   public static final EmptyRequestEventBuilder INSTANCE = new EmptyRequestEventBuilder();

   @Override
   public RequestEventBuilder setExceptionMapper(ExceptionMapper<?> exceptionMapper) {
      return this;
   }

   @Override
   public RequestEventBuilder setContainerRequest(ContainerRequest containerRequest) {
      return this;
   }

   @Override
   public RequestEventBuilder setContainerResponse(ContainerResponse containerResponse) {
      return this;
   }

   @Override
   public RequestEventBuilder setSuccess(boolean success) {
      return this;
   }

   @Override
   public RequestEventBuilder setResponseWritten(boolean responseWritten) {
      return this;
   }

   @Override
   public RequestEventBuilder setException(Throwable throwable, RequestEvent.ExceptionCause exceptionCause) {
      return this;
   }

   @Override
   public RequestEventBuilder setExtendedUriInfo(ExtendedUriInfo extendedUriInfo) {
      return this;
   }

   @Override
   public RequestEventBuilder setContainerResponseFilters(Iterable<ContainerResponseFilter> containerResponseFilters) {
      return this;
   }

   @Override
   public RequestEventBuilder setContainerRequestFilters(Iterable<ContainerRequestFilter> containerRequestFilters) {
      return this;
   }

   @Override
   public RequestEventBuilder setResponseSuccessfullyMapped(boolean responseSuccessfullyMapped) {
      return this;
   }

   @Override
   public RequestEvent build(RequestEvent.Type eventType) {
      return null;
   }
}
