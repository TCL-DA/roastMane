package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Link.Builder;
import jakarta.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.RuntimeDelegateDecorator;
import org.glassfish.jersey.internal.util.ReflectionHelper;
import org.glassfish.jersey.internal.util.collection.GuardianStringKeyMultivaluedMap;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;

public class OutboundMessageContext {
   private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
   private static final List<MediaType> WILDCARD_ACCEPTABLE_TYPE_SINGLETON_LIST = Collections.singletonList(MediaTypes.WILDCARD_ACCEPTABLE_TYPE);
   private final GuardianStringKeyMultivaluedMap<Object> headers;
   private final CommittingOutputStream committingOutputStream;
   private Configuration configuration;
   private RuntimeDelegate runtimeDelegateDecorator;
   private LazyValue<MediaType> mediaTypeCache;
   private Object entity;
   private GenericType<?> entityType;
   private Annotation[] entityAnnotations = EMPTY_ANNOTATIONS;
   private OutputStream entityStream;

   public OutboundMessageContext(Configuration configuration) {
      this.configuration = configuration;
      this.headers = new GuardianStringKeyMultivaluedMap<>(HeaderUtils.createOutbound());
      this.committingOutputStream = new CommittingOutputStream();
      this.entityStream = this.committingOutputStream;
      this.runtimeDelegateDecorator = RuntimeDelegateDecorator.configured(configuration);
      this.mediaTypeCache = this.mediaTypeCache();
      this.headers.setGuard("Content-Type");
   }

   public OutboundMessageContext(OutboundMessageContext original) {
      this.headers = new GuardianStringKeyMultivaluedMap<>(HeaderUtils.createOutbound());
      this.headers.setGuard("Content-Type");
      this.headers.putAll(original.headers);
      this.committingOutputStream = new CommittingOutputStream();
      this.entityStream = this.committingOutputStream;
      this.entity = original.entity;
      this.entityType = original.entityType;
      this.entityAnnotations = original.entityAnnotations;
      this.configuration = original.configuration;
      this.runtimeDelegateDecorator = original.runtimeDelegateDecorator;
      this.mediaTypeCache = this.mediaTypeCache();
   }

   @Deprecated
   public OutboundMessageContext() {
      this((Configuration)null);
   }

   public void replaceHeaders(MultivaluedMap<String, Object> headers) {
      this.getHeaders().clear();
      if (headers != null) {
         this.getHeaders().putAll(headers);
      }
   }

   public MultivaluedMap<String, String> getStringHeaders() {
      return HeaderUtils.asStringHeaders(this.headers, this.runtimeDelegateDecorator);
   }

   public String getHeaderString(String name) {
      return HeaderUtils.asHeaderString(this.headers.get(name), this.runtimeDelegateDecorator);
   }

   private <T> T singleHeader(String name, Class<T> valueType, Function<String, T> converter, boolean convertNull) {
      List<Object> values = this.headers.get(name);
      if (values != null && !values.isEmpty()) {
         if (values.size() > 1) {
            throw new HeaderValueException(LocalizationMessages.TOO_MANY_HEADER_VALUES(name, values.toString()), HeaderValueException.Context.OUTBOUND);
         } else {
            Object value = values.get(0);
            if (value == null) {
               return convertNull ? converter.apply(null) : null;
            } else if (valueType.isInstance(value)) {
               return valueType.cast(value);
            } else {
               try {
                  return converter.apply(HeaderUtils.asString(value, this.runtimeDelegateDecorator));
               } catch (ProcessingException var8) {
                  throw exception(name, value, var8);
               }
            }
         }
      } else {
         return convertNull ? converter.apply(null) : null;
      }
   }

   private static HeaderValueException exception(String headerName, Object headerValue, Exception e) {
      return new HeaderValueException(LocalizationMessages.UNABLE_TO_PARSE_HEADER_VALUE(headerName, headerValue), e, HeaderValueException.Context.OUTBOUND);
   }

   public MultivaluedMap<String, Object> getHeaders() {
      return this.headers;
   }

   public Date getDate() {
      return this.singleHeader("Date", Date.class, input -> {
         try {
            return HttpHeaderReader.readDate(input);
         } catch (ParseException var2) {
            throw new ProcessingException(var2);
         }
      }, false);
   }

   public Locale getLanguage() {
      return this.singleHeader("Content-Language", Locale.class, input -> {
         try {
            return new LanguageTag(input).getAsLocale();
         } catch (ParseException var2) {
            throw new ProcessingException(var2);
         }
      }, false);
   }

   public MediaType getMediaType() {
      if (this.headers.isObservedAndReset("Content-Type") && this.mediaTypeCache.isInitialized()) {
         this.mediaTypeCache = this.mediaTypeCache();
      }

      return this.mediaTypeCache.get();
   }

   private LazyValue<MediaType> mediaTypeCache() {
      return Values.lazy(
         (Value<MediaType>)(() -> this.singleHeader(
               "Content-Type",
               MediaType.class,
               RuntimeDelegateDecorator.configured(this.configuration).createHeaderDelegate(MediaType.class)::fromString,
               false
            ))
      );
   }

   public List<MediaType> getAcceptableMediaTypes() {
      List<Object> values = this.headers.get("Accept");
      if (values != null && !values.isEmpty()) {
         List<MediaType> result = new ArrayList<>(values.size());
         boolean conversionApplied = false;

         for (Object value : values) {
            try {
               if (value instanceof MediaType) {
                  AcceptableMediaType _value = AcceptableMediaType.valueOf((MediaType)value);
                  conversionApplied = _value != value;
                  result.add(_value);
               } else {
                  conversionApplied = true;
                  result.addAll(HttpHeaderReader.readAcceptMediaType(HeaderUtils.asString(value, this.runtimeDelegateDecorator)));
               }
            } catch (ParseException var7) {
               throw exception("Accept", value, var7);
            }
         }

         if (conversionApplied) {
            this.headers.put("Accept", result.stream().map(mediaType -> (Object)mediaType).collect(Collectors.toList()));
         }

         return Collections.unmodifiableList(result);
      } else {
         return WILDCARD_ACCEPTABLE_TYPE_SINGLETON_LIST;
      }
   }

   public List<Locale> getAcceptableLanguages() {
      List<Object> values = this.headers.get("Accept-Language");
      if (values != null && !values.isEmpty()) {
         List<Locale> result = new ArrayList<>(values.size());
         boolean conversionApplied = false;

         for (Object value : values) {
            if (value instanceof Locale) {
               result.add((Locale)value);
            } else {
               conversionApplied = true;

               try {
                  result.addAll(
                     HttpHeaderReader.readAcceptLanguage(HeaderUtils.asString(value, this.runtimeDelegateDecorator))
                        .stream()
                        .map(LanguageTag::getAsLocale)
                        .collect(Collectors.toList())
                  );
               } catch (ParseException var7) {
                  throw exception("Accept-Language", value, var7);
               }
            }
         }

         if (conversionApplied) {
            this.headers.put("Accept-Language", result.stream().map(locale -> (Object)locale).collect(Collectors.toList()));
         }

         return Collections.unmodifiableList(result);
      } else {
         return Collections.singletonList(new AcceptableLanguageTag("*", null).getAsLocale());
      }
   }

   public Map<String, Cookie> getRequestCookies() {
      List<Object> cookies = this.headers.get("Cookie");
      if (cookies != null && !cookies.isEmpty()) {
         Map<String, Cookie> result = new HashMap<>();

         for (String cookie : HeaderUtils.asStringList(cookies, this.runtimeDelegateDecorator)) {
            if (cookie != null) {
               result.putAll(HttpHeaderReader.readCookies(cookie));
            }
         }

         return result;
      } else {
         return Collections.emptyMap();
      }
   }

   public Set<String> getAllowedMethods() {
      String allowed = this.getHeaderString("Allow");
      if (allowed != null && !allowed.isEmpty()) {
         try {
            return new HashSet<>(HttpHeaderReader.readStringList(allowed));
         } catch (ParseException var3) {
            throw exception("Allow", allowed, var3);
         }
      } else {
         return Collections.emptySet();
      }
   }

   public int getLength() {
      return this.singleHeader("Content-Length", Integer.class, input -> {
         try {
            if (input != null && !input.isEmpty()) {
               int i = Integer.parseInt(input);
               if (i >= 0) {
                  return i;
               }
            }

            return -1;
         } catch (NumberFormatException var2) {
            throw new ProcessingException(var2);
         }
      }, true);
   }

   public long getLengthLong() {
      return this.singleHeader("Content-Length", Long.class, input -> {
         try {
            if (input != null && !input.isEmpty()) {
               long l = Long.parseLong(input);
               if (l >= 0L) {
                  return l;
               }
            }

            return -1L;
         } catch (NumberFormatException var3) {
            throw new ProcessingException(var3);
         }
      }, true);
   }

   public Map<String, NewCookie> getResponseCookies() {
      List<Object> cookies = this.headers.get("Set-Cookie");
      if (cookies != null && !cookies.isEmpty()) {
         Map<String, NewCookie> result = new HashMap<>();

         for (String cookie : HeaderUtils.asStringList(cookies, this.runtimeDelegateDecorator)) {
            if (cookie != null) {
               NewCookie newCookie = HttpHeaderReader.readNewCookie(cookie);
               String cookieName = newCookie.getName();
               if (result.containsKey(cookieName)) {
                  result.put(cookieName, HeaderUtils.getPreferredCookie(result.get(cookieName), newCookie));
               } else {
                  result.put(cookieName, newCookie);
               }
            }
         }

         return result;
      } else {
         return Collections.emptyMap();
      }
   }

   public EntityTag getEntityTag() {
      return this.singleHeader("ETag", EntityTag.class, new Function<String, EntityTag>() {
         public EntityTag apply(String value) {
            try {
               return value == null ? null : EntityTag.valueOf(value);
            } catch (IllegalArgumentException var3) {
               throw new ProcessingException(var3);
            }
         }
      }, false);
   }

   public Date getLastModified() {
      return this.singleHeader("Last-Modified", Date.class, new Function<String, Date>() {
         public Date apply(String input) {
            try {
               return HttpHeaderReader.readDate(input);
            } catch (ParseException var3) {
               throw new ProcessingException(var3);
            }
         }
      }, false);
   }

   public URI getLocation() {
      return this.singleHeader("Location", URI.class, new Function<String, URI>() {
         public URI apply(String value) {
            try {
               return value == null ? null : URI.create(value);
            } catch (IllegalArgumentException var3) {
               throw new ProcessingException(var3);
            }
         }
      }, false);
   }

   public Set<Link> getLinks() {
      List<Object> values = this.headers.get("Link");
      if (values != null && !values.isEmpty()) {
         Set<Link> result = new HashSet<>(values.size());
         boolean conversionApplied = false;

         for (Object value : values) {
            if (value instanceof Link) {
               result.add((Link)value);
            } else {
               conversionApplied = true;

               try {
                  result.add(Link.valueOf(HeaderUtils.asString(value, this.runtimeDelegateDecorator)));
               } catch (IllegalArgumentException var7) {
                  throw exception("Link", value, var7);
               }
            }
         }

         if (conversionApplied) {
            this.headers.put("Link", result.stream().map(link -> (Object)link).collect(Collectors.toList()));
         }

         return Collections.unmodifiableSet(result);
      } else {
         return Collections.emptySet();
      }
   }

   public boolean hasLink(String relation) {
      for (Link link : this.getLinks()) {
         List<String> relations = LinkProvider.getLinkRelations(link.getRel());
         if (relations != null && relations.contains(relation)) {
            return true;
         }
      }

      return false;
   }

   public Link getLink(String relation) {
      for (Link link : this.getLinks()) {
         List<String> relations = LinkProvider.getLinkRelations(link.getRel());
         if (relations != null && relations.contains(relation)) {
            return link;
         }
      }

      return null;
   }

   public Builder getLinkBuilder(String relation) {
      Link link = this.getLink(relation);
      return link == null ? null : Link.fromLink(link);
   }

   public boolean hasEntity() {
      return this.entity != null;
   }

   public Object getEntity() {
      return this.entity;
   }

   public void setEntity(Object entity) {
      this.setEntity(entity, ReflectionHelper.genericTypeFor(entity));
   }

   public void setEntity(Object entity, Annotation[] annotations) {
      this.setEntity(entity, ReflectionHelper.genericTypeFor(entity));
      this.setEntityAnnotations(annotations);
   }

   private void setEntity(Object entity, GenericType<?> type) {
      if (entity instanceof GenericEntity) {
         this.entity = ((GenericEntity)entity).getEntity();
      } else {
         this.entity = entity;
      }

      this.entityType = type;
   }

   public void setEntity(Object entity, Type type, Annotation[] annotations) {
      this.setEntity(entity, new GenericType(type));
      this.setEntityAnnotations(annotations);
   }

   public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType) {
      this.setEntity(entity, annotations);
      this.setMediaType(mediaType);
   }

   public void setMediaType(MediaType mediaType) {
      this.headers.putSingle("Content-Type", mediaType);
   }

   public Class<?> getEntityClass() {
      return this.entityType == null ? null : this.entityType.getRawType();
   }

   public Type getEntityType() {
      return this.entityType == null ? null : this.entityType.getType();
   }

   public void setEntityType(Type type) {
      this.entityType = new GenericType(type);
   }

   public Annotation[] getEntityAnnotations() {
      return (Annotation[])this.entityAnnotations.clone();
   }

   public void setEntityAnnotations(Annotation[] annotations) {
      this.entityAnnotations = annotations == null ? EMPTY_ANNOTATIONS : annotations;
   }

   public OutputStream getEntityStream() {
      return this.entityStream;
   }

   public void setEntityStream(OutputStream outputStream) {
      this.entityStream = outputStream;
   }

   public void enableBuffering(Configuration configuration) {
      Integer bufferSize = CommonProperties.getValue(
         configuration.getProperties(), configuration.getRuntimeType(), "jersey.config.contentLength.buffer", Integer.class
      );
      if (bufferSize != null) {
         this.committingOutputStream.enableBuffering(bufferSize);
      } else {
         this.committingOutputStream.enableBuffering();
      }
   }

   public void setStreamProvider(OutboundMessageContext.StreamProvider streamProvider) {
      this.committingOutputStream.setStreamProvider(streamProvider);
   }

   public void commitStream() throws IOException {
      if (!this.committingOutputStream.isCommitted()) {
         this.entityStream.flush();
         if (!this.committingOutputStream.isCommitted()) {
            this.committingOutputStream.commit();
            this.committingOutputStream.flush();
         }
      }
   }

   public boolean isCommitted() {
      return this.committingOutputStream.isCommitted();
   }

   public void close() {
      if (this.hasEntity()) {
         try {
            OutputStream es = this.getEntityStream();
            es.flush();
            es.close();
         } catch (IOException var10) {
            Logger.getLogger(OutboundMessageContext.class.getName()).log(Level.FINE, var10.getMessage(), (Throwable)var10);
         } finally {
            if (!this.committingOutputStream.isClosed()) {
               try {
                  this.committingOutputStream.close();
               } catch (IOException var9) {
                  Logger.getLogger(OutboundMessageContext.class.getName()).log(Level.FINE, var9.getMessage(), (Throwable)var9);
               }
            }
         }
      }
   }

   void setConfiguration(Configuration configuration) {
      this.configuration = configuration;
      this.runtimeDelegateDecorator = RuntimeDelegateDecorator.configured(configuration);
   }

   public Configuration getConfiguration() {
      return this.configuration;
   }

   public interface StreamProvider {
      OutputStream getOutputStream(int var1) throws IOException;
   }
}
