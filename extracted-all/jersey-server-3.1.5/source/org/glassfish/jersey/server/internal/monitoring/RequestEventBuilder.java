package org.glassfish.jersey.server.internal.monitoring;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.monitoring.RequestEvent;

public interface RequestEventBuilder {
   RequestEventBuilder setExceptionMapper(ExceptionMapper<?> var1);

   RequestEventBuilder setContainerRequest(ContainerRequest var1);

   RequestEventBuilder setContainerResponse(ContainerResponse var1);

   RequestEventBuilder setSuccess(boolean var1);

   RequestEventBuilder setResponseWritten(boolean var1);

   RequestEventBuilder setException(Throwable var1, RequestEvent.ExceptionCause var2);

   RequestEventBuilder setExtendedUriInfo(ExtendedUriInfo var1);

   RequestEventBuilder setContainerResponseFilters(Iterable<ContainerResponseFilter> var1);

   RequestEventBuilder setContainerRequestFilters(Iterable<ContainerRequestFilter> var1);

   RequestEventBuilder setResponseSuccessfullyMapped(boolean var1);

   RequestEvent build(RequestEvent.Type var1);
}
