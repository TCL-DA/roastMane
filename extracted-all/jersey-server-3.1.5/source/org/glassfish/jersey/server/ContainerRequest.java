package org.glassfish.jersey.server;

import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.EntityTag;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.Variant;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.WriterInterceptor;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.glassfish.jersey.http.HttpHeaders;
import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.internal.PropertiesResolver;
import org.glassfish.jersey.internal.guava.Preconditions;
import org.glassfish.jersey.internal.util.collection.LazyValue;
import org.glassfish.jersey.internal.util.collection.Ref;
import org.glassfish.jersey.internal.util.collection.Refs;
import org.glassfish.jersey.internal.util.collection.Values;
import org.glassfish.jersey.message.internal.HttpHeaderReader;
import org.glassfish.jersey.message.internal.InboundMessageContext;
import org.glassfish.jersey.message.internal.LanguageTag;
import org.glassfish.jersey.message.internal.MatchingEntityTag;
import org.glassfish.jersey.message.internal.TracingAwarePropertiesDelegate;
import org.glassfish.jersey.message.internal.VariantSelector;
import org.glassfish.jersey.message.internal.OutboundJaxrsResponse.Builder;
import org.glassfish.jersey.model.ContractProvider;
import org.glassfish.jersey.model.internal.CommonConfig;
import org.glassfish.jersey.model.internal.ComponentBag;
import org.glassfish.jersey.model.internal.RankedProvider;
import org.glassfish.jersey.process.Inflector;
import org.glassfish.jersey.server.internal.LocalizationMessages;
import org.glassfish.jersey.server.internal.ProcessingProviders;
import org.glassfish.jersey.server.internal.process.RequestProcessingContext;
import org.glassfish.jersey.server.internal.routing.UriRoutingContext;
import org.glassfish.jersey.server.model.ResourceMethodInvoker;
import org.glassfish.jersey.server.spi.ContainerResponseWriter;
import org.glassfish.jersey.server.spi.RequestScopedInitializer;
import org.glassfish.jersey.uri.UriComponent;
import org.glassfish.jersey.uri.internal.JerseyUriBuilder;

public class ContainerRequest extends InboundMessageContext implements ContainerRequestContext, Request, HttpHeaders, PropertiesDelegate, PropertiesResolver {
   private static final URI DEFAULT_BASE_URI = URI.create("/");
   private final PropertiesDelegate propertiesDelegate;
   private final UriRoutingContext uriRoutingContext;
   private URI baseUri;
   private URI requestUri;
   private String encodedRelativePath = null;
   private String decodedRelativePath = null;
   private URI absolutePathUri = null;
   private String httpMethod;
   private SecurityContext securityContext;
   private Response abortResponse;
   private String varyValue;
   private ProcessingProviders processingProviders;
   private RequestScopedInitializer requestScopedInitializer;
   private ContainerResponseWriter responseWriter;
   private boolean inResponseProcessingPhase;
   private final LazyValue<PropertiesResolver> propertiesResolver = Values.lazy(
      () -> PropertiesResolver.create(this.getConfiguration(), this.getPropertiesDelegate())
   );
   private static final String ERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE = LocalizationMessages.ERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE();
   private static final String ERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE = LocalizationMessages.ERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE();
   private static final String ERROR_REQUEST_ABORT_IN_RESPONSE_PHASE = LocalizationMessages.ERROR_REQUEST_ABORT_IN_RESPONSE_PHASE();
   private static final String METHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY = LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY("variants");
   private static final String METHOD_PARAMETER_CANNOT_BE_NULL_ETAG = LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("eTag");
   private static final String METHOD_PARAMETER_CANNOT_BE_NULL_LAST_MODIFIED = LocalizationMessages.METHOD_PARAMETER_CANNOT_BE_NULL("lastModified");

   public ContainerRequest(
      URI baseUri, URI requestUri, String httpMethod, SecurityContext securityContext, PropertiesDelegate propertiesDelegate, Configuration configuration
   ) {
      super(configuration, true);
      this.baseUri = baseUri == null ? DEFAULT_BASE_URI : baseUri.normalize();
      this.requestUri = requestUri;
      this.httpMethod = httpMethod;
      this.securityContext = securityContext;
      this.propertiesDelegate = new TracingAwarePropertiesDelegate(propertiesDelegate);
      this.uriRoutingContext = new UriRoutingContext(this);
   }

   @Deprecated
   public ContainerRequest(URI baseUri, URI requestUri, String httpMethod, SecurityContext securityContext, PropertiesDelegate propertiesDelegate) {
      this(baseUri, requestUri, httpMethod, securityContext, propertiesDelegate, new CommonConfig(RuntimeType.SERVER, ComponentBag.EXCLUDE_EMPTY) {
         {
            this.property(ContainerRequest.class.getName(), Deprecated.class.getSimpleName());
         }
      });
   }

   public RequestScopedInitializer getRequestScopedInitializer() {
      return this.requestScopedInitializer;
   }

   public void setRequestScopedInitializer(RequestScopedInitializer requestScopedInitializer) {
      this.requestScopedInitializer = requestScopedInitializer;
   }

   public ContainerResponseWriter getResponseWriter() {
      return this.responseWriter;
   }

   public void setWriter(ContainerResponseWriter responseWriter) {
      this.responseWriter = responseWriter;
   }

   public <T> T readEntity(Class<T> rawType) {
      return (T)this.readEntity(rawType, this.propertiesDelegate);
   }

   public <T> T readEntity(Class<T> rawType, Annotation[] annotations) {
      return (T)super.readEntity(rawType, annotations, this.propertiesDelegate);
   }

   public <T> T readEntity(Class<T> rawType, Type type) {
      return (T)super.readEntity(rawType, type, this.propertiesDelegate);
   }

   public <T> T readEntity(Class<T> rawType, Type type, Annotation[] annotations) {
      return (T)super.readEntity(rawType, type, annotations, this.propertiesDelegate);
   }

   public <T> T resolveProperty(String name, Class<T> type) {
      return (T)((PropertiesResolver)this.propertiesResolver.get()).resolveProperty(name, type);
   }

   public <T> T resolveProperty(String name, T defaultValue) {
      return (T)((PropertiesResolver)this.propertiesResolver.get()).resolveProperty(name, defaultValue);
   }

   public boolean hasProperty(String name) {
      return this.propertiesDelegate.hasProperty(name);
   }

   public Object getProperty(String name) {
      return this.propertiesDelegate.getProperty(name);
   }

   public Collection<String> getPropertyNames() {
      return this.propertiesDelegate.getPropertyNames();
   }

   public void setProperty(String name, Object object) {
      this.propertiesDelegate.setProperty(name, object);
   }

   public void removeProperty(String name) {
      this.propertiesDelegate.removeProperty(name);
   }

   public PropertiesDelegate getPropertiesDelegate() {
      return this.propertiesDelegate;
   }

   public ExtendedUriInfo getUriInfo() {
      return this.uriRoutingContext;
   }

   void setProcessingProviders(ProcessingProviders providers) {
      this.processingProviders = providers;
   }

   UriRoutingContext getUriRoutingContext() {
      return this.uriRoutingContext;
   }

   Iterable<RankedProvider<ContainerRequestFilter>> getRequestFilters() {
      Inflector<RequestProcessingContext, ContainerResponse> inflector = this.getInflector();
      return emptyIfNull(inflector instanceof ResourceMethodInvoker ? ((ResourceMethodInvoker)inflector).getRequestFilters() : null);
   }

   Iterable<RankedProvider<ContainerResponseFilter>> getResponseFilters() {
      Inflector<RequestProcessingContext, ContainerResponse> inflector = this.getInflector();
      return emptyIfNull(inflector instanceof ResourceMethodInvoker ? ((ResourceMethodInvoker)inflector).getResponseFilters() : null);
   }

   protected Iterable<ReaderInterceptor> getReaderInterceptors() {
      Inflector<RequestProcessingContext, ContainerResponse> inflector = this.getInflector();
      return inflector instanceof ResourceMethodInvoker
         ? ((ResourceMethodInvoker)inflector).getReaderInterceptors()
         : this.processingProviders.getSortedGlobalReaderInterceptors();
   }

   Iterable<WriterInterceptor> getWriterInterceptors() {
      Inflector<RequestProcessingContext, ContainerResponse> inflector = this.getInflector();
      return inflector instanceof ResourceMethodInvoker
         ? ((ResourceMethodInvoker)inflector).getWriterInterceptors()
         : this.processingProviders.getSortedGlobalWriterInterceptors();
   }

   private Inflector<RequestProcessingContext, ContainerResponse> getInflector() {
      return this.uriRoutingContext.getEndpoint();
   }

   private static <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
      return (Iterable<T>)(iterable == null ? Collections.emptyList() : iterable);
   }

   public URI getBaseUri() {
      return this.baseUri;
   }

   public URI getRequestUri() {
      return this.requestUri;
   }

   public URI getAbsolutePath() {
      return this.absolutePathUri != null
         ? this.absolutePathUri
         : (this.absolutePathUri = new JerseyUriBuilder().uri(this.requestUri).replaceQuery("").fragment("").build(new Object[0]));
   }

   public void setRequestUri(URI requestUri) throws IllegalStateException {
      if (!this.uriRoutingContext.getMatchedURIs().isEmpty()) {
         throw new IllegalStateException("Method could be called only in pre-matching request filter.");
      } else {
         this.encodedRelativePath = null;
         this.decodedRelativePath = null;
         this.absolutePathUri = null;
         this.uriRoutingContext.invalidateUriComponentViews();
         this.requestUri = requestUri;
      }
   }

   public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException {
      if (!this.uriRoutingContext.getMatchedURIs().isEmpty()) {
         throw new IllegalStateException("Method could be called only in pre-matching request filter.");
      } else {
         this.encodedRelativePath = null;
         this.decodedRelativePath = null;
         this.absolutePathUri = null;
         this.uriRoutingContext.invalidateUriComponentViews();
         this.baseUri = baseUri;
         this.requestUri = requestUri;
         Builder.setBaseUri(baseUri);
      }
   }

   public String getPath(boolean decode) {
      if (decode) {
         return this.decodedRelativePath != null
            ? this.decodedRelativePath
            : (this.decodedRelativePath = UriComponent.decode(this.encodedRelativePath(), org.glassfish.jersey.uri.UriComponent.Type.PATH));
      } else {
         return this.encodedRelativePath();
      }
   }

   private String encodedRelativePath() {
      if (this.encodedRelativePath != null) {
         return this.encodedRelativePath;
      } else {
         String requestUriRawPath = this.requestUri.getRawPath();
         if (this.baseUri == null) {
            return this.encodedRelativePath = requestUriRawPath;
         } else {
            int baseUriRawPathLength = this.baseUri.getRawPath().length();
            return this.encodedRelativePath = baseUriRawPathLength < requestUriRawPath.length() ? requestUriRawPath.substring(baseUriRawPathLength) : "";
         }
      }
   }

   public String getMethod() {
      return this.httpMethod;
   }

   public void setMethod(String method) throws IllegalStateException {
      if (!this.uriRoutingContext.getMatchedURIs().isEmpty()) {
         throw new IllegalStateException("Method could be called only in pre-matching request filter.");
      } else {
         this.httpMethod = method;
      }
   }

   public void setMethodWithoutException(String method) {
      this.httpMethod = method;
   }

   public SecurityContext getSecurityContext() {
      return this.securityContext;
   }

   public void setSecurityContext(SecurityContext context) {
      Preconditions.checkState(!this.inResponseProcessingPhase, ERROR_REQUEST_SET_SECURITY_CONTEXT_IN_RESPONSE_PHASE);
      this.securityContext = context;
   }

   public void setEntityStream(InputStream input) {
      Preconditions.checkState(!this.inResponseProcessingPhase, ERROR_REQUEST_SET_ENTITY_STREAM_IN_RESPONSE_PHASE);
      super.setEntityStream(input);
   }

   public Request getRequest() {
      return this;
   }

   public void abortWith(Response response) {
      Preconditions.checkState(!this.inResponseProcessingPhase, ERROR_REQUEST_ABORT_IN_RESPONSE_PHASE);
      this.abortResponse = response;
   }

   public void inResponseProcessing() {
      this.inResponseProcessingPhase = true;
   }

   public Response getAbortResponse() {
      return this.abortResponse;
   }

   public Map<String, Cookie> getCookies() {
      return super.getRequestCookies();
   }

   public List<MediaType> getAcceptableMediaTypes() {
      return this.getQualifiedAcceptableMediaTypes().stream().map(input -> (MediaType)input).collect(Collectors.toList());
   }

   public List<Locale> getAcceptableLanguages() {
      return this.getQualifiedAcceptableLanguages().stream().<Locale>map(LanguageTag::getAsLocale).collect(Collectors.toList());
   }

   public Variant selectVariant(List<Variant> variants) throws IllegalArgumentException {
      if (variants != null && !variants.isEmpty()) {
         Ref<String> varyValueRef = Refs.emptyRef();
         Variant variant = VariantSelector.selectVariant(this, variants, varyValueRef);
         this.varyValue = (String)varyValueRef.get();
         return variant;
      } else {
         throw new IllegalArgumentException(METHOD_PARAMETER_CANNOT_BE_NULL_OR_EMPTY);
      }
   }

   public String getVaryValue() {
      return this.varyValue;
   }

   public ResponseBuilder evaluatePreconditions(EntityTag eTag) {
      if (eTag == null) {
         throw new IllegalArgumentException(METHOD_PARAMETER_CANNOT_BE_NULL_ETAG);
      } else {
         ResponseBuilder r = this.evaluateIfMatch(eTag);
         return r != null ? r : this.evaluateIfNoneMatch(eTag);
      }
   }

   public ResponseBuilder evaluatePreconditions(Date lastModified) {
      if (lastModified == null) {
         throw new IllegalArgumentException(METHOD_PARAMETER_CANNOT_BE_NULL_LAST_MODIFIED);
      } else {
         long lastModifiedTime = lastModified.getTime();
         ResponseBuilder r = this.evaluateIfUnmodifiedSince(lastModifiedTime);
         return r != null ? r : this.evaluateIfModifiedSince(lastModifiedTime);
      }
   }

   public ResponseBuilder evaluatePreconditions(Date lastModified, EntityTag eTag) {
      if (lastModified == null) {
         throw new IllegalArgumentException(METHOD_PARAMETER_CANNOT_BE_NULL_LAST_MODIFIED);
      } else if (eTag == null) {
         throw new IllegalArgumentException(METHOD_PARAMETER_CANNOT_BE_NULL_ETAG);
      } else {
         ResponseBuilder r = this.evaluateIfMatch(eTag);
         if (r != null) {
            return r;
         } else {
            long lastModifiedTime = lastModified.getTime();
            r = this.evaluateIfUnmodifiedSince(lastModifiedTime);
            if (r != null) {
               return r;
            } else {
               boolean isGetOrHead = "GET".equals(this.getMethod()) || "HEAD".equals(this.getMethod());
               Set<MatchingEntityTag> matchingTags = this.getIfNoneMatch();
               if (matchingTags != null) {
                  r = this.evaluateIfNoneMatch(eTag, matchingTags, isGetOrHead);
                  if (r == null) {
                     return null;
                  }
               }

               String ifModifiedSinceHeader = this.getHeaderString("If-Modified-Since");
               if (ifModifiedSinceHeader != null && !ifModifiedSinceHeader.isEmpty() && isGetOrHead) {
                  r = this.evaluateIfModifiedSince(lastModifiedTime, ifModifiedSinceHeader);
                  if (r != null) {
                     r.tag(eTag);
                  }
               }

               return r;
            }
         }
      }
   }

   public ResponseBuilder evaluatePreconditions() {
      Set<MatchingEntityTag> matchingTags = this.getIfMatch();
      return matchingTags == null ? null : Response.status(Status.PRECONDITION_FAILED);
   }

   private ResponseBuilder evaluateIfMatch(EntityTag eTag) {
      Set<? extends EntityTag> matchingTags = this.getIfMatch();
      if (matchingTags == null) {
         return null;
      } else if (eTag.isWeak()) {
         return Response.status(Status.PRECONDITION_FAILED);
      } else {
         return matchingTags != MatchingEntityTag.ANY_MATCH && !matchingTags.contains(eTag) ? Response.status(Status.PRECONDITION_FAILED) : null;
      }
   }

   private ResponseBuilder evaluateIfNoneMatch(EntityTag eTag) {
      Set<MatchingEntityTag> matchingTags = this.getIfNoneMatch();
      if (matchingTags == null) {
         return null;
      } else {
         String httpMethod = this.getMethod();
         return this.evaluateIfNoneMatch(eTag, matchingTags, "GET".equals(httpMethod) || "HEAD".equals(httpMethod));
      }
   }

   private ResponseBuilder evaluateIfNoneMatch(EntityTag eTag, Set<? extends EntityTag> matchingTags, boolean isGetOrHead) {
      if (isGetOrHead) {
         if (matchingTags == MatchingEntityTag.ANY_MATCH) {
            return Response.notModified(eTag);
         }

         if (matchingTags.contains(eTag) || matchingTags.contains(new EntityTag(eTag.getValue(), !eTag.isWeak()))) {
            return Response.notModified(eTag);
         }
      } else {
         if (eTag.isWeak()) {
            return null;
         }

         if (matchingTags == MatchingEntityTag.ANY_MATCH || matchingTags.contains(eTag)) {
            return Response.status(Status.PRECONDITION_FAILED);
         }
      }

      return null;
   }

   private ResponseBuilder evaluateIfUnmodifiedSince(long lastModified) {
      String ifUnmodifiedSinceHeader = this.getHeaderString("If-Unmodified-Since");
      if (ifUnmodifiedSinceHeader != null && !ifUnmodifiedSinceHeader.isEmpty()) {
         try {
            long ifUnmodifiedSince = HttpHeaderReader.readDate(ifUnmodifiedSinceHeader).getTime();
            if (roundDown(lastModified) > ifUnmodifiedSince) {
               return Response.status(Status.PRECONDITION_FAILED);
            }
         } catch (ParseException var6) {
         }
      }

      return null;
   }

   private ResponseBuilder evaluateIfModifiedSince(long lastModified) {
      String ifModifiedSinceHeader = this.getHeaderString("If-Modified-Since");
      if (ifModifiedSinceHeader != null && !ifModifiedSinceHeader.isEmpty()) {
         String httpMethod = this.getMethod();
         return !"GET".equals(httpMethod) && !"HEAD".equals(httpMethod) ? null : this.evaluateIfModifiedSince(lastModified, ifModifiedSinceHeader);
      } else {
         return null;
      }
   }

   private ResponseBuilder evaluateIfModifiedSince(long lastModified, String ifModifiedSinceHeader) {
      try {
         long ifModifiedSince = HttpHeaderReader.readDate(ifModifiedSinceHeader).getTime();
         if (roundDown(lastModified) <= ifModifiedSince) {
            return Response.notModified();
         }
      } catch (ParseException var6) {
      }

      return null;
   }

   private static long roundDown(long time) {
      return time - time % 1000L;
   }

   public List<String> getRequestHeader(String name) {
      return (List<String>)this.getHeaders().get(name);
   }

   public MultivaluedMap<String, String> getRequestHeaders() {
      return this.getHeaders();
   }

   void checkState() throws IllegalStateException {
      if (this.securityContext == null) {
         throw new IllegalStateException("SecurityContext set in the ContainerRequestContext must not be null.");
      } else if (this.responseWriter == null) {
         throw new IllegalStateException("ResponseWriter set in the ContainerRequestContext must not be null.");
      }
   }
}
