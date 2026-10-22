package org.glassfish.jersey.server;

import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Link.Builder;
import jakarta.ws.rs.core.Response.StatusType;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse;
import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.glassfish.jersey.message.internal.Statuses;
import org.glassfish.jersey.message.internal.OutboundMessageContext.StreamProvider;

public class ContainerResponse implements ContainerResponseContext {
   private StatusType status;
   private final ContainerRequest requestContext;
   private final OutboundMessageContext messageContext;
   private boolean mappedFromException;
   private boolean closed;

   public ContainerResponse(ContainerRequest requestContext, Response response) {
      this(requestContext, OutboundJaxrsResponse.from(response, requestContext.getConfiguration()));
   }

   ContainerResponse(ContainerRequest requestContext, OutboundJaxrsResponse response) {
      this.requestContext = requestContext;
      this.requestContext.inResponseProcessing();
      this.status = response.getStatusInfo();
      this.messageContext = response.getContext();
      String varyValue = requestContext.getVaryValue();
      if (varyValue != null && !this.messageContext.getHeaders().containsKey("Vary")) {
         this.messageContext.getHeaders().add("Vary", varyValue);
      }
   }

   public boolean isMappedFromException() {
      return this.mappedFromException;
   }

   public void setMappedFromException(boolean mappedFromException) {
      this.mappedFromException = mappedFromException;
   }

   public int getStatus() {
      return this.status.getStatusCode();
   }

   public void setStatus(int code) {
      this.status = Statuses.from(code);
   }

   public void setStatusInfo(StatusType status) {
      if (status == null) {
         throw new NullPointerException("Response status must not be 'null'");
      } else {
         this.status = status;
      }
   }

   public StatusType getStatusInfo() {
      return this.status;
   }

   public ContainerRequest getRequestContext() {
      return this.requestContext;
   }

   public Map<String, NewCookie> getCookies() {
      return this.messageContext.getResponseCookies();
   }

   public OutboundMessageContext getWrappedMessageContext() {
      return this.messageContext;
   }

   public String getHeaderString(String name) {
      return this.messageContext.getHeaderString(name);
   }

   public MultivaluedMap<String, Object> getHeaders() {
      return this.messageContext.getHeaders();
   }

   public MultivaluedMap<String, String> getStringHeaders() {
      return this.messageContext.getStringHeaders();
   }

   public Date getDate() {
      return this.messageContext.getDate();
   }

   public Locale getLanguage() {
      return this.messageContext.getLanguage();
   }

   public MediaType getMediaType() {
      return this.messageContext.getMediaType();
   }

   public Set<String> getAllowedMethods() {
      return this.messageContext.getAllowedMethods();
   }

   public int getLength() {
      return this.messageContext.getLength();
   }

   public EntityTag getEntityTag() {
      return this.messageContext.getEntityTag();
   }

   public Date getLastModified() {
      return this.messageContext.getLastModified();
   }

   public URI getLocation() {
      return this.messageContext.getLocation();
   }

   public Set<Link> getLinks() {
      return this.messageContext.getLinks();
   }

   public boolean hasLink(String relation) {
      return this.messageContext.hasLink(relation);
   }

   public Link getLink(String relation) {
      return this.messageContext.getLink(relation);
   }

   public Builder getLinkBuilder(String relation) {
      return this.messageContext.getLinkBuilder(relation);
   }

   public boolean hasEntity() {
      return this.messageContext.hasEntity();
   }

   public Object getEntity() {
      return this.messageContext.getEntity();
   }

   public void setEntity(Object entity) {
      this.messageContext.setEntity(entity);
   }

   public void setEntity(Object entity, Annotation[] annotations) {
      this.messageContext.setEntity(entity, annotations);
   }

   public void setEntity(Object entity, Type type, Annotation[] annotations) {
      this.messageContext.setEntity(entity, type, annotations);
   }

   public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType) {
      this.messageContext.setEntity(entity, annotations, mediaType);
   }

   public void setMediaType(MediaType mediaType) {
      this.messageContext.setMediaType(mediaType);
   }

   public Class<?> getEntityClass() {
      return this.messageContext.getEntityClass();
   }

   public Type getEntityType() {
      return this.messageContext.getEntityType();
   }

   public void setEntityType(Type type) {
      Type t = type;
      if (type instanceof ParameterizedType) {
         ParameterizedType parameterizedType = (ParameterizedType)type;
         if (parameterizedType.getRawType().equals(GenericEntity.class)) {
            t = parameterizedType.getActualTypeArguments()[0];
         }
      } else if (type instanceof TypeVariable) {
         TypeVariable typeVariable = (TypeVariable)type;
         Type[] bounds = typeVariable.getBounds();
         if (bounds.length == 1) {
            t = bounds[0];
         }
      } else if (type instanceof WildcardType) {
         WildcardType wildcardType = (WildcardType)type;
         Type[] bounds = wildcardType.getUpperBounds();
         if (bounds.length == 1) {
            t = bounds[0];
         }
      }

      this.messageContext.setEntityType(t);
   }

   public Annotation[] getEntityAnnotations() {
      return this.messageContext.getEntityAnnotations();
   }

   public void setEntityAnnotations(Annotation[] annotations) {
      this.messageContext.setEntityAnnotations(annotations);
   }

   public OutputStream getEntityStream() {
      return this.messageContext.getEntityStream();
   }

   public void setEntityStream(OutputStream outputStream) {
      this.messageContext.setEntityStream(outputStream);
   }

   public void setStreamProvider(StreamProvider streamProvider) {
      this.messageContext.setStreamProvider(streamProvider);
   }

   public void enableBuffering(Configuration configuration) {
      this.messageContext.enableBuffering(configuration);
   }

   public void commitStream() throws IOException {
      this.messageContext.commitStream();
   }

   public boolean isCommitted() {
      return this.messageContext.isCommitted();
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.messageContext.close();
         this.requestContext.getResponseWriter().commit();
         this.requestContext.setWorkers(null);
      }
   }

   public boolean isChunked() {
      return this.hasEntity() && ChunkedOutput.class.isAssignableFrom(this.getEntity().getClass());
   }
}
