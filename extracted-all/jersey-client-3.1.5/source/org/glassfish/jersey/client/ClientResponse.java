package org.glassfish.jersey.client;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientResponseContext;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.ext.ReaderInterceptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.glassfish.jersey.client.internal.LocalizationMessages;
import org.glassfish.jersey.internal.inject.InjectionManager;
import org.glassfish.jersey.internal.inject.InjectionManagerSupplier;
import org.glassfish.jersey.message.internal.InboundMessageContext;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.Statuses;

public class ClientResponse extends InboundMessageContext implements ClientResponseContext, InjectionManagerSupplier {
   private StatusType status;
   private final ClientRequest requestContext;
   private URI resolvedUri;

   public ClientResponse(final ClientRequest requestContext, final Response response) {
      this(response.getStatusInfo(), requestContext);
      this.headers(OutboundJaxrsResponse.from(response, requestContext.getConfiguration()).getContext().getStringHeaders());
      final Object entity = response.getEntity();
      if (entity != null) {
         InputStream entityStream = new InputStream() {
            private ByteArrayInputStream byteArrayInputStream = null;

            @Override
            public int read() throws IOException {
               if (this.byteArrayInputStream == null) {
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  OutputStream stream = null;

                  try {
                     try {
                        stream = requestContext.getWorkers()
                           .writeTo(
                              entity,
                              entity.getClass(),
                              null,
                              null,
                              response.getMediaType(),
                              response.getMetadata(),
                              requestContext.getPropertiesDelegate(),
                              baos,
                              Collections.emptyList()
                           );
                     } finally {
                        if (stream != null) {
                           stream.close();
                        }
                     }
                  } catch (IOException var7) {
                  }

                  this.byteArrayInputStream = new ByteArrayInputStream(baos.toByteArray());
               }

               return this.byteArrayInputStream.read();
            }
         };
         this.setEntityStream(entityStream);
      }
   }

   public ClientResponse(StatusType status, ClientRequest requestContext) {
      this(status, requestContext, requestContext.getUri());
   }

   public ClientResponse(StatusType status, ClientRequest requestContext, URI resolvedRequestUri) {
      super(requestContext.getConfiguration());
      this.status = status;
      this.resolvedUri = resolvedRequestUri;
      this.requestContext = requestContext;
      this.setWorkers(requestContext.getWorkers());
   }

   public int getStatus() {
      return this.status.getStatusCode();
   }

   public void setStatus(int code) {
      this.status = Statuses.from(code);
   }

   public void setStatusInfo(StatusType status) {
      if (status == null) {
         throw new NullPointerException(LocalizationMessages.CLIENT_RESPONSE_STATUS_NULL());
      } else {
         this.status = status;
      }
   }

   public StatusType getStatusInfo() {
      return this.status;
   }

   public URI getResolvedRequestUri() {
      return this.resolvedUri;
   }

   public void setResolvedRequestUri(URI uri) {
      if (uri == null) {
         throw new NullPointerException(LocalizationMessages.CLIENT_RESPONSE_RESOLVED_URI_NULL());
      } else if (!uri.isAbsolute()) {
         throw new IllegalArgumentException(LocalizationMessages.CLIENT_RESPONSE_RESOLVED_URI_NOT_ABSOLUTE());
      } else {
         this.resolvedUri = uri;
      }
   }

   public ClientRequest getRequestContext() {
      return this.requestContext;
   }

   public Map<String, NewCookie> getCookies() {
      return super.getResponseCookies();
   }

   public Set<Link> getLinks() {
      return super.getLinks()
         .stream()
         .map(link -> (Link)(link.getUri().isAbsolute() ? link : Link.fromLink(link).baseUri(this.getResolvedRequestUri()).build(new Object[0])))
         .collect(Collectors.toSet());
   }

   public String toString() {
      return "ClientResponse{method="
         + this.requestContext.getMethod()
         + ", uri="
         + this.requestContext.getUri()
         + ", status="
         + this.status.getStatusCode()
         + ", reason="
         + this.status.getReasonPhrase()
         + "}";
   }

   public Object getEntity() throws IllegalStateException {
      return this.getEntityStream();
   }

   public <T> T readEntity(Class<T> entityType) throws ProcessingException, IllegalStateException {
      return (T)this.readEntity(entityType, this.requestContext.getPropertiesDelegate());
   }

   public <T> T readEntity(GenericType<T> entityType) throws ProcessingException, IllegalStateException {
      return (T)this.readEntity(entityType.getRawType(), entityType.getType(), this.requestContext.getPropertiesDelegate());
   }

   public <T> T readEntity(Class<T> entityType, Annotation[] annotations) throws ProcessingException, IllegalStateException {
      return (T)this.readEntity(entityType, annotations, this.requestContext.getPropertiesDelegate());
   }

   public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) throws ProcessingException, IllegalStateException {
      return (T)this.readEntity(entityType.getRawType(), entityType.getType(), annotations, this.requestContext.getPropertiesDelegate());
   }

   public InjectionManager getInjectionManager() {
      return this.getRequestContext().getInjectionManager();
   }

   protected Iterable<ReaderInterceptor> getReaderInterceptors() {
      return this.requestContext.getReaderInterceptors();
   }
}
