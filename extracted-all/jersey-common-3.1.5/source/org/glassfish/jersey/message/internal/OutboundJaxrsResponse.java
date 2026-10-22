package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.Response.StatusType;
import jakarta.ws.rs.core.Response.Status.Family;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.glassfish.jersey.internal.LocalizationMessages;

public class OutboundJaxrsResponse extends Response {
   private final OutboundMessageContext context;
   private final StatusType status;
   private boolean closed = false;
   private boolean buffered = false;

   public static OutboundJaxrsResponse from(Response response, Configuration configuration) {
      if (response instanceof OutboundJaxrsResponse) {
         ((OutboundJaxrsResponse)response).context.setConfiguration(configuration);
         return (OutboundJaxrsResponse)response;
      } else {
         StatusType status = response.getStatusInfo();
         OutboundMessageContext context = new OutboundMessageContext(configuration);
         context.getHeaders().putAll(response.getMetadata());
         context.setEntity(response.getEntity());
         return new OutboundJaxrsResponse(status, context);
      }
   }

   @Deprecated
   public static OutboundJaxrsResponse from(Response response) {
      return from(response, (Configuration)null);
   }

   public OutboundJaxrsResponse(StatusType status, OutboundMessageContext context) {
      this.status = status;
      this.context = context;
   }

   public OutboundMessageContext getContext() {
      return this.context;
   }

   public int getStatus() {
      return this.status.getStatusCode();
   }

   public StatusType getStatusInfo() {
      return this.status;
   }

   public Object getEntity() {
      if (this.closed) {
         throw new IllegalStateException(LocalizationMessages.RESPONSE_CLOSED());
      } else {
         return this.context.getEntity();
      }
   }

   public <T> T readEntity(Class<T> type) throws ProcessingException {
      throw new IllegalStateException(LocalizationMessages.NOT_SUPPORTED_ON_OUTBOUND_MESSAGE());
   }

   public <T> T readEntity(GenericType<T> entityType) throws ProcessingException {
      throw new IllegalStateException(LocalizationMessages.NOT_SUPPORTED_ON_OUTBOUND_MESSAGE());
   }

   public <T> T readEntity(Class<T> type, Annotation[] annotations) throws ProcessingException {
      throw new IllegalStateException(LocalizationMessages.NOT_SUPPORTED_ON_OUTBOUND_MESSAGE());
   }

   public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) throws ProcessingException {
      throw new IllegalStateException(LocalizationMessages.NOT_SUPPORTED_ON_OUTBOUND_MESSAGE());
   }

   public boolean hasEntity() {
      if (this.closed) {
         throw new IllegalStateException(LocalizationMessages.RESPONSE_CLOSED());
      } else {
         return this.context.hasEntity();
      }
   }

   public boolean bufferEntity() throws ProcessingException {
      if (this.closed) {
         throw new IllegalStateException(LocalizationMessages.RESPONSE_CLOSED());
      } else if (!this.context.hasEntity() || !InputStream.class.isAssignableFrom(this.context.getEntityClass())) {
         return false;
      } else if (this.buffered) {
         return true;
      } else {
         InputStream in = InputStream.class.cast(this.context.getEntity());
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         byte[] buffer = new byte[1024];

         try {
            int len;
            try {
               while ((len = in.read(buffer)) != -1) {
                  out.write(buffer, 0, len);
               }
            } catch (IOException var13) {
               throw new ProcessingException(var13);
            }
         } finally {
            try {
               in.close();
            } catch (IOException var12) {
               throw new ProcessingException(var12);
            }
         }

         this.context.setEntity(new ByteArrayInputStream(out.toByteArray()));
         this.buffered = true;
         return true;
      }
   }

   public void close() throws ProcessingException {
      this.closed = true;
      this.context.close();
      if (this.buffered) {
         this.context.setEntity(null);
      } else if (this.context.hasEntity() && InputStream.class.isAssignableFrom(this.context.getEntityClass())) {
         try {
            InputStream.class.cast(this.context.getEntity()).close();
         } catch (IOException var2) {
            throw new ProcessingException(var2);
         }
      }
   }

   public MultivaluedMap<String, String> getStringHeaders() {
      return this.context.getStringHeaders();
   }

   public String getHeaderString(String name) {
      return this.context.getHeaderString(name);
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

   public jakarta.ws.rs.core.Link.Builder getLinkBuilder(String relation) {
      return this.context.getLinkBuilder(relation);
   }

   public MultivaluedMap<String, Object> getMetadata() {
      return this.context.getHeaders();
   }

   public String toString() {
      return "OutboundJaxrsResponse{status="
         + this.status.getStatusCode()
         + ", reason="
         + this.status.getReasonPhrase()
         + ", hasEntity="
         + this.context.hasEntity()
         + ", closed="
         + this.closed
         + ", buffered="
         + this.buffered
         + "}";
   }

   public static class Builder extends ResponseBuilder {
      private StatusType status;
      private final OutboundMessageContext context;
      private static final InheritableThreadLocal<URI> baseUriThreadLocal = new InheritableThreadLocal<>();

      public static void setBaseUri(URI baseUri) {
         baseUriThreadLocal.set(baseUri);
      }

      private static URI getBaseUri() {
         return baseUriThreadLocal.get();
      }

      public static void clearBaseUri() {
         baseUriThreadLocal.remove();
      }

      public Builder(OutboundMessageContext context) {
         this.context = context;
      }

      public Response build() {
         StatusType st = this.status;
         if (st == null) {
            st = this.context.hasEntity() ? Status.OK : Status.NO_CONTENT;
         }

         return new OutboundJaxrsResponse(st, new OutboundMessageContext(this.context));
      }

      public ResponseBuilder clone() {
         return new OutboundJaxrsResponse.Builder(new OutboundMessageContext(this.context)).status(this.status);
      }

      public ResponseBuilder status(StatusType status) {
         if (status == null) {
            throw new IllegalArgumentException("Response status must not be 'null'");
         } else {
            this.status = status;
            return this;
         }
      }

      public ResponseBuilder status(final int status, final String reasonPhrase) {
         if (status >= 100 && status <= 599) {
            final Family family = Family.familyOf(status);
            this.status = new StatusType() {
               public int getStatusCode() {
                  return status;
               }

               public Family getFamily() {
                  return family;
               }

               public String getReasonPhrase() {
                  return reasonPhrase;
               }
            };
            return this;
         } else {
            throw new IllegalArgumentException("Response status must not be less than '100' or greater than '599'");
         }
      }

      public ResponseBuilder status(int code) {
         this.status = Statuses.from(code);
         return this;
      }

      public ResponseBuilder entity(Object entity) {
         this.context.setEntity(entity);
         return this;
      }

      public ResponseBuilder entity(Object entity, Annotation[] annotations) {
         this.context.setEntity(entity, annotations);
         return this;
      }

      public ResponseBuilder type(MediaType type) {
         this.context.setMediaType(type);
         return this;
      }

      public ResponseBuilder type(String type) {
         return this.type(type == null ? null : MediaType.valueOf(type));
      }

      public ResponseBuilder variant(Variant variant) {
         if (variant == null) {
            this.type((MediaType)null);
            this.language((String)null);
            this.encoding(null);
            return this;
         } else {
            this.type(variant.getMediaType());
            this.language(variant.getLanguage());
            this.encoding(variant.getEncoding());
            return this;
         }
      }

      public ResponseBuilder variants(List<Variant> variants) {
         if (variants == null) {
            this.header("Vary", null);
            return this;
         } else if (variants.isEmpty()) {
            return this;
         } else {
            MediaType accept = variants.get(0).getMediaType();
            boolean vAccept = false;
            Locale acceptLanguage = variants.get(0).getLanguage();
            boolean vAcceptLanguage = false;
            String acceptEncoding = variants.get(0).getEncoding();
            boolean vAcceptEncoding = false;

            for (Variant v : variants) {
               vAccept |= !vAccept && this.vary(v.getMediaType(), accept);
               vAcceptLanguage |= !vAcceptLanguage && this.vary(v.getLanguage(), acceptLanguage);
               vAcceptEncoding |= !vAcceptEncoding && this.vary(v.getEncoding(), acceptEncoding);
            }

            StringBuilder vary = new StringBuilder();
            this.append(vary, vAccept, "Accept");
            this.append(vary, vAcceptLanguage, "Accept-Language");
            this.append(vary, vAcceptEncoding, "Accept-Encoding");
            if (vary.length() > 0) {
               this.header("Vary", vary.toString());
            }

            return this;
         }
      }

      private boolean vary(MediaType v, MediaType vary) {
         return v != null && !v.equals(vary);
      }

      private boolean vary(Locale v, Locale vary) {
         return v != null && !v.equals(vary);
      }

      private boolean vary(String v, String vary) {
         return v != null && !v.equalsIgnoreCase(vary);
      }

      private void append(StringBuilder sb, boolean v, String s) {
         if (v) {
            if (sb.length() > 0) {
               sb.append(',');
            }

            sb.append(s);
         }
      }

      public ResponseBuilder language(String language) {
         this.headerSingle("Content-Language", language);
         return this;
      }

      public ResponseBuilder language(Locale language) {
         this.headerSingle("Content-Language", language);
         return this;
      }

      public ResponseBuilder location(URI location) {
         URI locationUri = location;
         if (location != null && !location.isAbsolute()) {
            URI baseUri = getBaseUri();
            if (baseUri != null) {
               locationUri = baseUri.resolve(location);
            }
         }

         this.headerSingle("Location", locationUri);
         return this;
      }

      public ResponseBuilder contentLocation(URI location) {
         this.headerSingle("Content-Location", location);
         return this;
      }

      public ResponseBuilder encoding(String encoding) {
         this.headerSingle("Content-Encoding", encoding);
         return this;
      }

      public ResponseBuilder tag(EntityTag tag) {
         this.headerSingle("ETag", tag);
         return this;
      }

      public ResponseBuilder tag(String tag) {
         return this.tag(tag == null ? null : new EntityTag(tag));
      }

      public ResponseBuilder lastModified(Date lastModified) {
         this.headerSingle("Last-Modified", lastModified);
         return this;
      }

      public ResponseBuilder cacheControl(CacheControl cacheControl) {
         this.headerSingle("Cache-Control", cacheControl);
         return this;
      }

      public ResponseBuilder expires(Date expires) {
         this.headerSingle("Expires", expires);
         return this;
      }

      public ResponseBuilder cookie(NewCookie... cookies) {
         if (cookies != null) {
            for (NewCookie cookie : cookies) {
               this.header("Set-Cookie", cookie);
            }
         } else {
            this.header("Set-Cookie", null);
         }

         return this;
      }

      public ResponseBuilder header(String name, Object value) {
         return this.header(name, value, false);
      }

      private ResponseBuilder headerSingle(String name, Object value) {
         return this.header(name, value, true);
      }

      private ResponseBuilder header(String name, Object value, boolean single) {
         if (value != null) {
            if (single) {
               this.context.getHeaders().putSingle(name, value);
            } else {
               this.context.getHeaders().add(name, value);
            }
         } else {
            this.context.getHeaders().remove(name);
         }

         return this;
      }

      public ResponseBuilder variants(Variant... variants) {
         return this.variants(Arrays.asList(variants));
      }

      public ResponseBuilder links(Link... links) {
         if (links != null) {
            for (Link link : links) {
               this.header("Link", link);
            }
         } else {
            this.header("Link", null);
         }

         return this;
      }

      public ResponseBuilder link(URI uri, String rel) {
         this.header("Link", Link.fromUri(uri).rel(rel).build(new Object[0]));
         return this;
      }

      public ResponseBuilder link(String uri, String rel) {
         this.header("Link", Link.fromUri(uri).rel(rel).build(new Object[0]));
         return this;
      }

      public ResponseBuilder allow(String... methods) {
         return methods != null && (methods.length != 1 || methods[0] != null)
            ? this.allow(new HashSet<>(Arrays.asList(methods)))
            : this.allow((Set<String>)null);
      }

      public ResponseBuilder allow(Set<String> methods) {
         if (methods == null) {
            return this.header("Allow", null, true);
         } else {
            StringBuilder allow = new StringBuilder();

            for (String m : methods) {
               this.append(allow, true, m);
            }

            return this.header("Allow", allow, true);
         }
      }

      public ResponseBuilder replaceAll(MultivaluedMap<String, Object> headers) {
         this.context.replaceHeaders(headers);
         return this;
      }
   }
}
