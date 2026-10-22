package org.glassfish.jersey.client;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Link.Builder;
import jakarta.ws.rs.core.Response.StatusType;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.glassfish.jersey.internal.util.Producer;
import org.glassfish.jersey.process.internal.RequestContext;
import org.glassfish.jersey.process.internal.RequestScope;

class InboundJaxrsResponse extends Response {
   private final ClientResponse context;
   private final RequestScope scope;
   private final RequestContext requestContext;
   private final AtomicBoolean isClosed = new AtomicBoolean(false);

   public InboundJaxrsResponse(ClientResponse context, RequestScope scope) {
      this.context = context;
      this.scope = scope;
      if (this.scope != null) {
         this.requestContext = scope.referenceCurrent();
      } else {
         this.requestContext = null;
      }
   }

   public int getStatus() {
      return this.context.getStatus();
   }

   public StatusType getStatusInfo() {
      return this.context.getStatusInfo();
   }

   public Object getEntity() throws IllegalStateException {
      return this.context.getEntity();
   }

   public <T> T readEntity(final Class<T> entityType) throws ProcessingException, IllegalStateException {
      return this.runInScopeIfPossible(new Producer<T>() {
         public T call() {
            return InboundJaxrsResponse.this.context.readEntity(entityType);
         }
      });
   }

   public <T> T readEntity(final GenericType<T> entityType) throws ProcessingException, IllegalStateException {
      return this.runInScopeIfPossible(new Producer<T>() {
         public T call() {
            return InboundJaxrsResponse.this.context.readEntity(entityType);
         }
      });
   }

   public <T> T readEntity(final Class<T> entityType, final Annotation[] annotations) throws ProcessingException, IllegalStateException {
      return this.runInScopeIfPossible(new Producer<T>() {
         public T call() {
            return InboundJaxrsResponse.this.context.readEntity(entityType, annotations);
         }
      });
   }

   public <T> T readEntity(final GenericType<T> entityType, final Annotation[] annotations) throws ProcessingException, IllegalStateException {
      return this.runInScopeIfPossible(new Producer<T>() {
         public T call() {
            return InboundJaxrsResponse.this.context.readEntity(entityType, annotations);
         }
      });
   }

   public boolean hasEntity() {
      return this.context.hasEntity();
   }

   public boolean bufferEntity() throws ProcessingException {
      return this.context.bufferEntity();
   }

   public void close() throws ProcessingException {
      if (this.isClosed.compareAndSet(false, true)) {
         try {
            this.context.close();
         } finally {
            if (this.requestContext != null) {
               this.requestContext.release();
            }
         }
      }
   }

   public String getHeaderString(String name) {
      return this.context.getHeaderString(name);
   }

   public MultivaluedMap<String, String> getStringHeaders() {
      return this.context.getHeaders();
   }

   public MediaType getMediaType() {
      return this.context.getMediaType();
   }

   public Locale getLanguage() {
      return this.context.getLanguage();
   }

   public int getLength() {
      return this.context.getLength();
   }

   public Map<String, NewCookie> getCookies() {
      return this.context.getResponseCookies();
   }

   public EntityTag getEntityTag() {
      return this.context.getEntityTag();
   }

   public Date getDate() {
      return this.context.getDate();
   }

   public Date getLastModified() {
      return this.context.getLastModified();
   }

   public Set<String> getAllowedMethods() {
      return this.context.getAllowedMethods();
   }

   public URI getLocation() {
      return this.context.getLocation();
   }

   public Set<Link> getLinks() {
      return this.context.getLinks();
   }

   public boolean hasLink(String relation) {
      return this.context.hasLink(relation);
   }

   public Link getLink(String relation) {
      return this.context.getLink(relation);
   }

   public Builder getLinkBuilder(String relation) {
      return this.context.getLinkBuilder(relation);
   }

   public MultivaluedMap<String, Object> getMetadata() {
      return this.context.getHeaders();
   }

   public String toString() {
      return "InboundJaxrsResponse{context=" + this.context + "}";
   }

   private <T> T runInScopeIfPossible(Producer<T> producer) {
      return (T)(this.scope != null && this.requestContext != null ? this.scope.runInScope(this.requestContext, producer) : producer.call());
   }
}
