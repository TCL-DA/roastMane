package org.glassfish.jersey.message.internal;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Link.Builder;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.RuntimeDelegate;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.xml.transform.Source;
import org.glassfish.jersey.internal.LocalizationMessages;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.internal.RuntimeDelegateDecorator;
import org.glassfish.jersey.internal.util.collection.GuardianStringKeyMultivaluedMap;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Value;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.message.MessageBodyWorkers;

public abstract class InboundMessageContext {
   private static final InputStream EMPTY = new InputStream() {
      @Override
      public int read() throws IOException {
         return -1;
      }

      @Override
      public void mark(int readlimit) {
      }

      @Override
      public void reset() throws IOException {
      }

      @Override
      public boolean markSupported() {
         return true;
      }
   };
   private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
   private static final List<AcceptableMediaType> WILDCARD_ACCEPTABLE_TYPE_SINGLETON_LIST = Collections.singletonList(MediaTypes.WILDCARD_ACCEPTABLE_TYPE);
   private final GuardianStringKeyMultivaluedMap<String> headers = new GuardianStringKeyMultivaluedMap<>(HeaderUtils.createInbound());
   private final InboundMessageContext.EntityContent entityContent = new InboundMessageContext.EntityContent();
   private final boolean translateNce;
   private MessageBodyWorkers workers;
   private final Configuration configuration;
   private final RuntimeDelegate runtimeDelegateDecorator;
   private LazyValue<MediaType> contentTypeCache;
   private LazyValue<List<AcceptableMediaType>> acceptTypeCache;

   public InboundMessageContext(Configuration configuration) {
      this(configuration, false);
   }

   public InboundMessageContext(Configuration configuration, boolean translateNce) {
      this.translateNce = translateNce;
      this.configuration = configuration;
      this.runtimeDelegateDecorator = RuntimeDelegateDecorator.configured(configuration);
      this.contentTypeCache = this.contentTypeCache();
      this.acceptTypeCache = this.acceptTypeCache();
      this.headers.setGuard("Content-Type");
      this.headers.setGuard("Accept");
   }

   @Deprecated
   public InboundMessageContext() {
      this((Configuration)null);
   }

   @Deprecated
   public InboundMessageContext(boolean translateNce) {
      this((Configuration)null, translateNce);
   }

   public InboundMessageContext header(String name, Object value) {
      this.getHeaders().add(name, HeaderUtils.asString(value, this.runtimeDelegateDecorator));
      return this;
   }

   public InboundMessageContext headers(String name, Object... values) {
      this.getHeaders().addAll(name, HeaderUtils.asStringList(Arrays.asList(values), this.runtimeDelegateDecorator));
      return this;
   }

   public InboundMessageContext headers(String name, Iterable<?> values) {
      this.getHeaders().addAll(name, this.iterableToList(values));
      return this;
   }

   public InboundMessageContext headers(MultivaluedMap<String, String> newHeaders) {
      for (Entry<String, List<String>> header : newHeaders.entrySet()) {
         this.headers.addAll(header.getKey(), header.getValue());
      }

      return this;
   }

   public InboundMessageContext headers(Map<String, List<String>> newHeaders) {
      for (Entry<String, List<String>> header : newHeaders.entrySet()) {
         this.headers.addAll(header.getKey(), header.getValue());
      }

      return this;
   }

   public InboundMessageContext remove(String name) {
      this.getHeaders().remove(name);
      return this;
   }

   private List<String> iterableToList(Iterable<?> values) {
      LinkedList<String> linkedList = new LinkedList<>();

      for (Object element : values) {
         linkedList.add(HeaderUtils.asString(element, this.runtimeDelegateDecorator));
      }

      return linkedList;
   }

   public String getHeaderString(String name) {
      List<String> values = this.headers.get(name);
      if (values == null) {
         return null;
      } else if (values.isEmpty()) {
         return "";
      } else {
         Iterator<String> valuesIterator = values.iterator();
         StringBuilder buffer = new StringBuilder(valuesIterator.next());

         while (valuesIterator.hasNext()) {
            buffer.append(',').append(valuesIterator.next());
         }

         return buffer.toString();
      }
   }

   private <T> T singleHeader(String name, Function<String, T> converter, boolean convertNull) {
      List<String> values = this.headers.get(name);
      if (values != null && !values.isEmpty()) {
         if (values.size() > 1) {
            throw new HeaderValueException(LocalizationMessages.TOO_MANY_HEADER_VALUES(name, values.toString()), HeaderValueException.Context.INBOUND);
         } else {
            Object value = values.get(0);
            if (value == null) {
               return convertNull ? converter.apply(null) : null;
            } else {
               try {
                  return converter.apply(HeaderUtils.asString(value, this.runtimeDelegateDecorator));
               } catch (ProcessingException var7) {
                  throw exception(name, value, var7);
               }
            }
         }
      } else {
         return convertNull ? converter.apply(null) : null;
      }
   }

   private static HeaderValueException exception(String headerName, Object headerValue, Exception e) {
      return new HeaderValueException(LocalizationMessages.UNABLE_TO_PARSE_HEADER_VALUE(headerName, headerValue), e, HeaderValueException.Context.INBOUND);
   }

   public MultivaluedMap<String, String> getHeaders() {
      return this.headers;
   }

   public Date getDate() {
      return this.singleHeader("Date", new Function<String, Date>() {
         public Date apply(String input) {
            try {
               return HttpHeaderReader.readDate(input);
            } catch (ParseException var3) {
               throw new ProcessingException(var3);
            }
         }
      }, false);
   }

   public Set<MatchingEntityTag> getIfMatch() {
      String ifMatch = this.getHeaderString("If-Match");
      if (ifMatch != null && !ifMatch.isEmpty()) {
         try {
            return HttpHeaderReader.readMatchingEntityTag(ifMatch);
         } catch (ParseException var3) {
            throw exception("If-Match", ifMatch, var3);
         }
      } else {
         return null;
      }
   }

   public Set<MatchingEntityTag> getIfNoneMatch() {
      String ifNoneMatch = this.getHeaderString("If-None-Match");
      if (ifNoneMatch != null && !ifNoneMatch.isEmpty()) {
         try {
            return HttpHeaderReader.readMatchingEntityTag(ifNoneMatch);
         } catch (ParseException var3) {
            throw exception("If-None-Match", ifNoneMatch, var3);
         }
      } else {
         return null;
      }
   }

   public Locale getLanguage() {
      return this.singleHeader("Content-Language", new Function<String, Locale>() {
         public Locale apply(String input) {
            try {
               return new LanguageTag(input).getAsLocale();
            } catch (ParseException var3) {
               throw new ProcessingException(var3);
            }
         }
      }, false);
   }

   public int getLength() {
      return this.singleHeader("Content-Length", new Function<String, Integer>() {
         public Integer apply(String input) {
            try {
               return input != null && !input.isEmpty() ? Integer.parseInt(input) : -1;
            } catch (NumberFormatException var3) {
               throw new ProcessingException(var3);
            }
         }
      }, true);
   }

   public MediaType getMediaType() {
      if (this.headers.isObservedAndReset("Content-Type") && this.contentTypeCache.isInitialized()) {
         this.contentTypeCache = this.contentTypeCache();
      }

      return this.contentTypeCache.get();
   }

   private LazyValue<MediaType> contentTypeCache() {
      return Values.lazy((Value<MediaType>)(() -> this.singleHeader("Content-Type", new Function<String, MediaType>() {
            public MediaType apply(String input) {
               try {
                  return (MediaType)InboundMessageContext.this.runtimeDelegateDecorator.createHeaderDelegate(MediaType.class).fromString(input);
               } catch (IllegalArgumentException var3) {
                  throw new ProcessingException(var3);
               }
            }
         }, false)));
   }

   public List<AcceptableMediaType> getQualifiedAcceptableMediaTypes() {
      if (this.headers.isObservedAndReset("Accept") && this.acceptTypeCache.isInitialized()) {
         this.acceptTypeCache = this.acceptTypeCache();
      }

      return this.acceptTypeCache.get();
   }

   private LazyValue<List<AcceptableMediaType>> acceptTypeCache() {
      return Values.lazy((Value<List<AcceptableMediaType>>)(() -> {
         String value = this.getHeaderString("Accept");
         if (value != null && !value.isEmpty()) {
            try {
               return Collections.unmodifiableList(HttpHeaderReader.readAcceptMediaType(value));
            } catch (ParseException var3) {
               throw exception("Accept", value, var3);
            }
         } else {
            return WILDCARD_ACCEPTABLE_TYPE_SINGLETON_LIST;
         }
      }));
   }

   public List<AcceptableLanguageTag> getQualifiedAcceptableLanguages() {
      String value = this.getHeaderString("Accept-Language");
      if (value != null && !value.isEmpty()) {
         try {
            return Collections.unmodifiableList(HttpHeaderReader.readAcceptLanguage(value));
         } catch (ParseException var3) {
            throw exception("Accept-Language", value, var3);
         }
      } else {
         return Collections.singletonList(new AcceptableLanguageTag("*", null));
      }
   }

   public List<AcceptableToken> getQualifiedAcceptCharset() {
      String acceptCharset = this.getHeaderString("Accept-Charset");

      try {
         return acceptCharset != null && !acceptCharset.isEmpty()
            ? HttpHeaderReader.readAcceptToken(acceptCharset)
            : Collections.singletonList(new AcceptableToken("*"));
      } catch (ParseException var3) {
         throw exception("Accept-Charset", acceptCharset, var3);
      }
   }

   public List<AcceptableToken> getQualifiedAcceptEncoding() {
      String acceptEncoding = this.getHeaderString("Accept-Encoding");

      try {
         return acceptEncoding != null && !acceptEncoding.isEmpty()
            ? HttpHeaderReader.readAcceptToken(acceptEncoding)
            : Collections.singletonList(new AcceptableToken("*"));
      } catch (ParseException var3) {
         throw exception("Accept-Encoding", acceptEncoding, var3);
      }
   }

   public Map<String, Cookie> getRequestCookies() {
      List<String> cookies = this.headers.get("Cookie");
      if (cookies != null && !cookies.isEmpty()) {
         Map<String, Cookie> result = new HashMap<>();

         for (String cookie : cookies) {
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
            return new HashSet<>(HttpHeaderReader.readStringList(allowed.toUpperCase(Locale.ROOT)));
         } catch (ParseException var3) {
            throw exception("Allow", allowed, var3);
         }
      } else {
         return Collections.emptySet();
      }
   }

   public Map<String, NewCookie> getResponseCookies() {
      List<String> cookies = this.headers.get("Set-Cookie");
      if (cookies != null && !cookies.isEmpty()) {
         Map<String, NewCookie> result = new HashMap<>();

         for (String cookie : cookies) {
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
      return this.singleHeader("ETag", new Function<String, EntityTag>() {
         public EntityTag apply(String value) {
            return EntityTag.valueOf(value);
         }
      }, false);
   }

   public Date getLastModified() {
      return this.singleHeader("Last-Modified", new Function<String, Date>() {
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
      return this.singleHeader("Location", new Function<String, URI>() {
         public URI apply(String value) {
            try {
               return URI.create(value);
            } catch (IllegalArgumentException var3) {
               throw new ProcessingException(var3);
            }
         }
      }, false);
   }

   public Set<Link> getLinks() {
      List<String> links = this.headers.get("Link");
      if (links != null && !links.isEmpty()) {
         try {
            Set<Link> result = new HashSet<>(links.size());

            for (String link : links) {
               StringBuilder linkString = new StringBuilder();
               StringTokenizer st = new StringTokenizer(link, "<>,", true);
               boolean linkOpen = false;

               while (st.hasMoreTokens()) {
                  String n = st.nextToken();
                  if (n.equals("<")) {
                     linkOpen = true;
                  } else if (n.equals(">")) {
                     linkOpen = false;
                  } else if (!linkOpen && n.equals(",")) {
                     result.add(Link.valueOf(linkString.toString().trim()));
                     linkString = new StringBuilder();
                     continue;
                  }

                  linkString.append(n);
               }

               if (linkString.length() > 0) {
                  result.add(Link.valueOf(linkString.toString().trim()));
               }
            }

            return result;
         } catch (IllegalArgumentException var9) {
            throw exception("Link", links, var9);
         }
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

   public MessageBodyWorkers getWorkers() {
      if (this.workers == null) {
         throw new ProcessingException(LocalizationMessages.RESPONSE_CLOSED());
      } else {
         return this.workers;
      }
   }

   public void setWorkers(MessageBodyWorkers workers) {
      this.workers = workers;
   }

   public boolean hasEntity() {
      this.entityContent.ensureNotClosed();

      try {
         return this.entityContent.isBuffered() || !this.entityContent.isEmpty();
      } catch (IllegalStateException var2) {
         return false;
      }
   }

   public InputStream getEntityStream() {
      this.entityContent.ensureNotClosed();
      return this.entityContent.getWrappedStream();
   }

   public void setEntityStream(InputStream input) {
      this.entityContent.setContent(input, false);
   }

   public <T> T readEntity(Class<T> rawType, PropertiesDelegate propertiesDelegate) {
      return this.readEntity(rawType, rawType, EMPTY_ANNOTATIONS, propertiesDelegate);
   }

   public <T> T readEntity(Class<T> rawType, Annotation[] annotations, PropertiesDelegate propertiesDelegate) {
      return this.readEntity(rawType, rawType, annotations, propertiesDelegate);
   }

   public <T> T readEntity(Class<T> rawType, Type type, PropertiesDelegate propertiesDelegate) {
      return this.readEntity(rawType, type, EMPTY_ANNOTATIONS, propertiesDelegate);
   }

   public <T> T readEntity(Class<T> rawType, Type type, Annotation[] annotations, PropertiesDelegate propertiesDelegate) {
      boolean buffered = this.entityContent.isBuffered();
      if (buffered) {
         this.entityContent.reset();
      }

      this.entityContent.ensureNotClosed();
      if (this.workers == null) {
         return null;
      } else {
         MediaType mediaType = this.getMediaType();
         mediaType = mediaType == null ? MediaType.APPLICATION_OCTET_STREAM_TYPE : mediaType;
         boolean shouldClose = !buffered;

         Object var9;
         try {
            T t = (T)this.workers
               .readFrom(
                  rawType,
                  type,
                  annotations,
                  mediaType,
                  this.headers,
                  propertiesDelegate,
                  this.entityContent.getWrappedStream(),
                  (Iterable<ReaderInterceptor>)(this.entityContent.hasContent() ? this.getReaderInterceptors() : Collections.emptyList()),
                  this.translateNce
               );
            shouldClose = shouldClose && !(t instanceof Closeable) && !(t instanceof Source);
            var9 = t;
         } catch (IOException var13) {
            throw new ProcessingException(LocalizationMessages.ERROR_READING_ENTITY_FROM_INPUT_STREAM(), var13);
         } finally {
            if (shouldClose) {
               ReaderWriter.safelyClose(this.entityContent);
            }
         }

         return (T)var9;
      }
   }

   public boolean bufferEntity() throws ProcessingException {
      this.entityContent.ensureNotClosed();

      try {
         if (!this.entityContent.isBuffered() && this.entityContent.hasContent()) {
            InputStream entityStream = this.entityContent.getWrappedStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
               ReaderWriter.writeTo(entityStream, baos);
            } finally {
               ReaderWriter.safelyClose(entityStream);
            }

            this.entityContent.setContent(new ByteArrayInputStream(baos.toByteArray()), true);
            return true;
         } else {
            return true;
         }
      } catch (IOException var7) {
         throw new ProcessingException(LocalizationMessages.MESSAGE_CONTENT_BUFFERING_FAILED(), var7);
      }
   }

   public void close() {
      this.entityContent.close(true);
      this.setWorkers(null);
   }

   protected abstract Iterable<ReaderInterceptor> getReaderInterceptors();

   public Configuration getConfiguration() {
      return this.configuration;
   }

   private static class EntityContent extends EntityInputStream {
      private boolean buffered;

      EntityContent() {
         super(InboundMessageContext.EMPTY);
      }

      void setContent(InputStream content, boolean buffered) {
         this.buffered = buffered;
         this.setWrappedStream(content);
      }

      boolean hasContent() {
         return this.getWrappedStream() != InboundMessageContext.EMPTY;
      }

      boolean isBuffered() {
         return this.buffered;
      }

      @Override
      public void close() {
         this.close(false);
      }

      void close(boolean force) {
         if (!this.buffered || force) {
            try {
               super.close();
            } finally {
               this.buffered = false;
               this.setWrappedStream(null);
            }
         }
      }
   }
}
