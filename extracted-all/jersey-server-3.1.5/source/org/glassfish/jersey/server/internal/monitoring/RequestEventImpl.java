package org.glassfish.jersey.server.internal.monitoring;

import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ExtendedUriInfo;
import org.glassfish.jersey.server.monitoring.RequestEvent;

public class RequestEventImpl implements RequestEvent {
   private final RequestEvent.Type type;
   private final ContainerRequest containerRequest;
   private final ContainerResponse containerResponse;
   private final Throwable throwable;
   private final ExtendedUriInfo extendedUriInfo;
   private final Iterable<ContainerResponseFilter> containerResponseFilters;
   private final Iterable<ContainerRequestFilter> containerRequestFilters;
   private final ExceptionMapper<?> exceptionMapper;
   private final boolean success;
   private final boolean responseSuccessfullyMapped;
   private final RequestEvent.ExceptionCause exceptionCause;
   private final boolean responseWritten;

   private RequestEventImpl(
      RequestEvent.Type type,
      ContainerRequest containerRequest,
      ContainerResponse containerResponse,
      Throwable throwable,
      ExtendedUriInfo extendedUriInfo,
      Iterable<ContainerResponseFilter> containerResponseFilters,
      Iterable<ContainerRequestFilter> containerRequestFilters,
      ExceptionMapper<?> exceptionMapper,
      boolean success,
      boolean responseSuccessfullyMapped,
      RequestEvent.ExceptionCause exceptionCause,
      boolean responseWritten
   ) {
      this.type = type;
      this.containerRequest = containerRequest;
      this.containerResponse = containerResponse;
      this.throwable = throwable;
      this.extendedUriInfo = extendedUriInfo;
      this.containerResponseFilters = containerResponseFilters;
      this.containerRequestFilters = containerRequestFilters;
      this.exceptionMapper = exceptionMapper;
      this.success = success;
      this.responseSuccessfullyMapped = responseSuccessfullyMapped;
      this.exceptionCause = exceptionCause;
      this.responseWritten = responseWritten;
   }

   @Override
   public ContainerRequest getContainerRequest() {
      return this.containerRequest;
   }

   @Override
   public ContainerResponse getContainerResponse() {
      return this.containerResponse;
   }

   @Override
   public Throwable getException() {
      return this.throwable;
   }

   @Override
   public RequestEvent.Type getType() {
      return this.type;
   }

   @Override
   public ExtendedUriInfo getUriInfo() {
      return this.extendedUriInfo;
   }

   @Override
   public ExceptionMapper<?> getExceptionMapper() {
      return this.exceptionMapper;
   }

   @Override
   public Iterable<ContainerRequestFilter> getContainerRequestFilters() {
      return this.containerRequestFilters;
   }

   @Override
   public Iterable<ContainerResponseFilter> getContainerResponseFilters() {
      return this.containerResponseFilters;
   }

   @Override
   public boolean isSuccess() {
      return this.success;
   }

   @Override
   public boolean isResponseSuccessfullyMapped() {
      return this.responseSuccessfullyMapped;
   }

   @Override
   public RequestEvent.ExceptionCause getExceptionCause() {
      return this.exceptionCause;
   }

   @Override
   public boolean isResponseWritten() {
      return this.responseWritten;
   }

   public static class Builder implements RequestEventBuilder {
      private ContainerRequest containerRequest;
      private ContainerResponse containerResponse;
      private Throwable throwable;
      private ExtendedUriInfo extendedUriInfo;
      private Iterable<ContainerResponseFilter> containerResponseFilters;
      private Iterable<ContainerRequestFilter> containerRequestFilters;
      private ExceptionMapper<?> exceptionMapper;
      private boolean success;
      private boolean responseWritten;
      private boolean responseSuccessfullyMapped;
      private RequestEvent.ExceptionCause exceptionCause;

      public RequestEventImpl.Builder setExceptionMapper(ExceptionMapper<?> exceptionMapper) {
         this.exceptionMapper = exceptionMapper;
         return this;
      }

      public RequestEventImpl.Builder setContainerRequest(ContainerRequest containerRequest) {
         this.containerRequest = containerRequest;
         return this;
      }

      public RequestEventImpl.Builder setContainerResponse(ContainerResponse containerResponse) {
         this.containerResponse = containerResponse;
         return this;
      }

      public RequestEventImpl.Builder setResponseWritten(boolean responseWritten) {
         this.responseWritten = responseWritten;
         return this;
      }

      public RequestEventImpl.Builder setSuccess(boolean success) {
         this.success = success;
         return this;
      }

      public RequestEventImpl.Builder setException(Throwable throwable, RequestEvent.ExceptionCause exceptionCause) {
         this.throwable = throwable;
         this.exceptionCause = exceptionCause;
         return this;
      }

      public RequestEventImpl.Builder setExtendedUriInfo(ExtendedUriInfo extendedUriInfo) {
         this.extendedUriInfo = extendedUriInfo;
         return this;
      }

      public RequestEventImpl.Builder setContainerResponseFilters(Iterable<ContainerResponseFilter> containerResponseFilters) {
         this.containerResponseFilters = containerResponseFilters;
         return this;
      }

      public RequestEventImpl.Builder setContainerRequestFilters(Iterable<ContainerRequestFilter> containerRequestFilters) {
         this.containerRequestFilters = containerRequestFilters;
         return this;
      }

      public RequestEventImpl.Builder setResponseSuccessfullyMapped(boolean responseSuccessfullyMapped) {
         this.responseSuccessfullyMapped = responseSuccessfullyMapped;
         return this;
      }

      public RequestEventImpl build(RequestEvent.Type type) {
         return new RequestEventImpl(
            type,
            this.containerRequest,
            this.containerResponse,
            this.throwable,
            this.extendedUriInfo,
            this.containerResponseFilters,
            this.containerRequestFilters,
            this.exceptionMapper,
            this.success,
            this.responseSuccessfullyMapped,
            this.exceptionCause,
            this.responseWritten
         );
      }
   }
}
