package org.eclipse.jetty.client.transport;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.eclipse.jetty.client.CompletableResponseListener;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.Destination;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.client.PathRequestContent;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.RequestListeners;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.client.internal.NotifyingRequestListeners;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.util.Fields;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.Fields.Field;
import org.eclipse.jetty.util.Promise.Completable;

public class HttpRequest implements Request {
   private static final URI NULL_URI = URI.create("null:0");
   private final Mutable headers = HttpFields.build();
   private final Fields params = new Fields(true);
   private final ResponseListeners responseListeners = new ResponseListeners();
   private final AtomicReference<Throwable> aborted = new AtomicReference<>();
   private final HttpClient client;
   private final HttpConversation conversation;
   private String scheme;
   private String host;
   private int port;
   private String path;
   private String query;
   private URI uri;
   private String method = HttpMethod.GET.asString();
   private HttpVersion version = HttpVersion.HTTP_1_1;
   private boolean versionExplicit;
   private long idleTimeout = -1L;
   private long timeout;
   private long timeoutNanoTime = Long.MAX_VALUE;
   private Request.Content content;
   private boolean followRedirects;
   private List<HttpCookie> cookies;
   private Map<String, Object> attributes;
   private NotifyingRequestListeners requestListeners;
   private BiFunction<Request, Request, Response.CompleteListener> pushHandler;
   private Supplier<HttpFields> trailers;
   private Object tag;
   private boolean normalized;

   public HttpRequest(HttpClient client, HttpConversation conversation, URI uri) {
      if (uri.getHost() == null) {
         throw new IllegalArgumentException(String.format("Invalid URI host: null (authority: %s)", uri.getRawAuthority()));
      } else {
         this.client = client;
         this.conversation = conversation;
         this.scheme = uri.getScheme();
         this.host = uri.getHost();
         this.port = HttpClient.normalizePort(this.scheme, uri.getPort());
         this.path = uri.getRawPath();
         this.query = uri.getRawQuery();
         this.extractParams(this.query);
         this.followRedirects(client.isFollowRedirects());
         HttpField acceptEncodingField = client.getContentDecoderFactories().getAcceptEncodingField();
         if (acceptEncodingField != null) {
            this.headers.put(acceptEncodingField);
         }

         HttpField userAgentField = client.getUserAgentField();
         if (userAgentField != null) {
            this.headers.put(userAgentField);
         }
      }
   }

   public HttpRequest copy(URI newURI) {
      if (newURI == null) {
         StringBuilder builder = new StringBuilder(64);
         URIUtil.appendSchemeHostPort(builder, this.getScheme(), this.getHost(), this.getPort());
         newURI = URI.create(builder.toString());
      }

      HttpRequest newRequest = this.copyInstance(newURI);
      newRequest.method(this.getMethod())
         .version(this.getVersion())
         .body(this.getBody())
         .idleTimeout(this.getIdleTimeout(), TimeUnit.MILLISECONDS)
         .timeout(this.getTimeout(), TimeUnit.MILLISECONDS)
         .followRedirects(this.isFollowRedirects())
         .tag(this.getTag())
         .headers(
            h -> h.clear()
                  .add(this.getHeaders())
                  .remove(EnumSet.of(HttpHeader.HOST, HttpHeader.EXPECT, HttpHeader.COOKIE, HttpHeader.AUTHORIZATION, HttpHeader.PROXY_AUTHORIZATION))
         );
      return newRequest;
   }

   protected HttpRequest copyInstance(URI newURI) {
      return new HttpRequest(this.getHttpClient(), this.getConversation(), newURI);
   }

   protected HttpClient getHttpClient() {
      return this.client;
   }

   public HttpConversation getConversation() {
      return this.conversation;
   }

   @Override
   public String getScheme() {
      return this.scheme;
   }

   @Override
   public Request scheme(String scheme) {
      this.scheme = scheme;
      this.uri = null;
      return this;
   }

   @Override
   public String getHost() {
      return this.host;
   }

   @Override
   public Request host(String host) {
      this.host = host;
      this.uri = null;
      return this;
   }

   @Override
   public int getPort() {
      return this.port;
   }

   @Override
   public Request port(int port) {
      this.port = port;
      this.uri = null;
      return this;
   }

   @Override
   public String getMethod() {
      return this.method;
   }

   @Override
   public Request method(HttpMethod method) {
      return this.method(method.asString());
   }

   @Override
   public Request method(String method) {
      this.method = Objects.requireNonNull(method).toUpperCase(Locale.ENGLISH);
      return this;
   }

   @Override
   public String getPath() {
      return this.path;
   }

   @Override
   public Request path(String path) {
      URI uri = this.newURI(path);
      if (uri == null) {
         this.path = path;
         this.query = null;
      } else {
         String rawPath = uri.getRawPath();
         if (rawPath == null) {
            rawPath = "";
         }

         if (!rawPath.startsWith("/")) {
            rawPath = "/" + rawPath;
         }

         this.path = rawPath;
         String query = uri.getRawQuery();
         if (query != null) {
            this.query = query;
            this.params.clear();
            this.extractParams(query);
         }

         if (uri.isAbsolute()) {
            this.path = this.buildURI(false).toString();
         }
      }

      this.uri = null;
      return this;
   }

   @Override
   public String getQuery() {
      return this.query;
   }

   @Override
   public URI getURI() {
      if (this.uri == null) {
         this.uri = this.buildURI(true);
      }

      boolean isNullURI = this.uri == NULL_URI;
      return isNullURI ? null : this.uri;
   }

   @Override
   public HttpVersion getVersion() {
      return this.version;
   }

   public boolean isVersionExplicit() {
      return this.versionExplicit;
   }

   @Override
   public Request version(HttpVersion version) {
      this.version = Objects.requireNonNull(version);
      this.versionExplicit = true;
      return this;
   }

   @Override
   public Request param(String name, String value) {
      return this.param(name, value, false);
   }

   private Request param(String name, String value, boolean fromQuery) {
      this.params.add(name, value);
      if (!fromQuery) {
         if (this.query != null) {
            this.query = this.query + "&" + this.urlEncode(name) + "=" + this.urlEncode(value);
         } else {
            this.query = this.buildQuery();
         }

         this.uri = null;
      }

      return this;
   }

   @Override
   public Fields getParams() {
      return this.params.asImmutable();
   }

   @Override
   public String getAgent() {
      return this.headers.get(HttpHeader.USER_AGENT);
   }

   @Override
   public Request agent(String agent) {
      this.headers.put(HttpHeader.USER_AGENT, agent);
      return this;
   }

   @Override
   public Request accept(String... accepts) {
      StringBuilder result = new StringBuilder();

      for (String accept : accepts) {
         if (result.length() > 0) {
            result.append(", ");
         }

         result.append(accept);
      }

      if (result.length() > 0) {
         this.headers.put(HttpHeader.ACCEPT, result.toString());
      }

      return this;
   }

   @Override
   public List<HttpCookie> getCookies() {
      return this.cookies != null ? this.cookies : Collections.emptyList();
   }

   @Override
   public Request cookie(HttpCookie cookie) {
      if (this.cookies == null) {
         this.cookies = new ArrayList<>();
      }

      this.cookies.add(cookie);
      return this;
   }

   @Override
   public Request tag(Object tag) {
      this.tag = tag;
      return this;
   }

   @Override
   public Object getTag() {
      return this.tag;
   }

   @Override
   public Request attribute(String name, Object value) {
      if (this.attributes == null) {
         this.attributes = new HashMap<>(4);
      }

      this.attributes.put(name, value);
      return this;
   }

   @Override
   public Map<String, Object> getAttributes() {
      return this.attributes != null ? this.attributes : Collections.emptyMap();
   }

   @Override
   public HttpFields getHeaders() {
      return this.headers;
   }

   @Override
   public Request headers(Consumer<Mutable> consumer) {
      consumer.accept(this.headers);
      return this;
   }

   public HttpRequest addHeader(HttpField header) {
      this.headers.add(header);
      return this;
   }

   private RequestListeners requestListeners() {
      if (this.requestListeners == null) {
         this.requestListeners = new NotifyingRequestListeners();
      }

      return this.requestListeners;
   }

   private NotifyingRequestListeners getHttpClientRequestListeners() {
      return (NotifyingRequestListeners)this.client.getRequestListeners();
   }

   @Override
   public Request listener(Request.Listener listener) {
      this.requestListeners().addListener(listener);
      return this;
   }

   @Override
   public Request onRequestQueued(Request.QueuedListener listener) {
      this.requestListeners().addQueuedListener(listener);
      return this;
   }

   public void notifyQueued() {
      if (this.requestListeners != null) {
         this.requestListeners.notifyQueued(this);
      }

      this.getHttpClientRequestListeners().notifyQueued(this);
   }

   @Override
   public Request onRequestBegin(Request.BeginListener listener) {
      this.requestListeners().addBeginListener(listener);
      return this;
   }

   public void notifyBegin() {
      if (this.requestListeners != null) {
         this.requestListeners.notifyBegin(this);
      }

      this.getHttpClientRequestListeners().notifyBegin(this);
   }

   @Override
   public Request onRequestHeaders(Request.HeadersListener listener) {
      this.requestListeners().addHeadersListener(listener);
      return this;
   }

   public void notifyHeaders() {
      if (this.requestListeners != null) {
         this.requestListeners.notifyHeaders(this);
      }

      this.getHttpClientRequestListeners().notifyHeaders(this);
   }

   @Override
   public Request onRequestCommit(Request.CommitListener listener) {
      this.requestListeners().addCommitListener(listener);
      return this;
   }

   public void notifyCommit() {
      if (this.requestListeners != null) {
         this.requestListeners.notifyCommit(this);
      }

      this.getHttpClientRequestListeners().notifyCommit(this);
   }

   @Override
   public Request onRequestContent(Request.ContentListener listener) {
      this.requestListeners().addContentListener(listener);
      return this;
   }

   public void notifyContent(ByteBuffer byteBuffer) {
      if (this.requestListeners != null) {
         this.requestListeners.notifyContent(this, byteBuffer);
      }

      this.getHttpClientRequestListeners().notifyContent(this, byteBuffer);
   }

   @Override
   public Request onRequestSuccess(Request.SuccessListener listener) {
      this.requestListeners().addSuccessListener(listener);
      return this;
   }

   public void notifySuccess() {
      if (this.requestListeners != null) {
         this.requestListeners.notifySuccess(this);
      }

      this.getHttpClientRequestListeners().notifySuccess(this);
   }

   @Override
   public Request onRequestFailure(Request.FailureListener listener) {
      this.requestListeners().addFailureListener(listener);
      return this;
   }

   public void notifyFailure(Throwable failure) {
      if (this.requestListeners != null) {
         this.requestListeners.notifyFailure(this, failure);
      }

      this.getHttpClientRequestListeners().notifyFailure(this, failure);
   }

   @Override
   public Request onResponseBegin(Response.BeginListener listener) {
      this.responseListeners.addBeginListener(listener);
      return this;
   }

   @Override
   public Request onResponseHeader(Response.HeaderListener listener) {
      this.responseListeners.addHeaderListener(listener);
      return this;
   }

   @Override
   public Request onResponseHeaders(Response.HeadersListener listener) {
      this.responseListeners.addHeadersListener(listener);
      return this;
   }

   @Override
   public Request onResponseContent(Response.ContentListener listener) {
      this.responseListeners.addContentSourceListener(listener);
      return this;
   }

   @Override
   public Request onResponseContentAsync(Response.AsyncContentListener listener) {
      this.responseListeners.addContentSourceListener(listener);
      return this;
   }

   @Override
   public Request onResponseContentSource(Response.ContentSourceListener listener) {
      this.responseListeners.addContentSourceListener(listener);
      return this;
   }

   @Override
   public Request onResponseSuccess(Response.SuccessListener listener) {
      this.responseListeners.addSuccessListener(listener);
      return this;
   }

   @Override
   public Request onResponseFailure(Response.FailureListener listener) {
      this.responseListeners.addFailureListener(listener);
      return this;
   }

   @Override
   public Request onPush(BiFunction<Request, Request, Response.CompleteListener> pushHandler) {
      this.pushHandler = pushHandler;
      return this;
   }

   @Override
   public Request onComplete(Response.CompleteListener listener) {
      this.responseListeners.addCompleteListener(listener, false);
      return this;
   }

   @Override
   public Request trailersSupplier(Supplier<HttpFields> trailers) {
      this.trailers = trailers;
      return this;
   }

   @Override
   public Request.Content getBody() {
      return this.content;
   }

   @Override
   public Request body(Request.Content content) {
      this.content = content;
      return this;
   }

   @Override
   public Request file(Path file) throws IOException {
      return this.file(file, "application/octet-stream");
   }

   @Override
   public Request file(Path file, String contentType) throws IOException {
      return this.body(new PathRequestContent(contentType, file));
   }

   @Override
   public boolean isFollowRedirects() {
      return this.followRedirects;
   }

   @Override
   public Request followRedirects(boolean follow) {
      this.followRedirects = follow;
      return this;
   }

   @Override
   public long getIdleTimeout() {
      return this.idleTimeout;
   }

   @Override
   public Request idleTimeout(long timeout, TimeUnit unit) {
      this.idleTimeout = unit.toMillis(timeout);
      return this;
   }

   @Override
   public long getTimeout() {
      return this.timeout;
   }

   @Override
   public Request timeout(long timeout, TimeUnit unit) {
      this.timeout = unit.toMillis(timeout);
      return this;
   }

   @Override
   public ContentResponse send() throws InterruptedException, TimeoutException, ExecutionException {
      try {
         CompletableResponseListener listener = new CompletableResponseListener(this);
         return listener.send().get();
      } catch (ExecutionException var4) {
         if (var4.getCause() instanceof TimeoutException t) {
            this.abort(t);
            throw t;
         } else {
            this.abort(var4);
            throw var4;
         }
      } catch (Throwable var5) {
         this.abort(var5);
         throw var5;
      }
   }

   @Override
   public void send(Response.CompleteListener listener) {
      Destination destination = this.client.resolveDestination(this);
      destination.send(this, listener);
   }

   void sendAsync(HttpDestination destination, Response.CompleteListener listener) {
      if (listener != null) {
         this.responseListeners.addCompleteListener(listener, true);
      }

      destination.send(this);
   }

   void sent() {
      if (this.timeoutNanoTime == Long.MAX_VALUE) {
         long timeout = this.getTimeout();
         if (timeout > 0L) {
            this.timeoutNanoTime = NanoTime.now() + TimeUnit.MILLISECONDS.toNanos(timeout);
         }
      }
   }

   public long getTimeoutNanoTime() {
      return this.timeoutNanoTime;
   }

   public ResponseListeners getResponseListeners() {
      return this.responseListeners;
   }

   public BiFunction<Request, Request, Response.CompleteListener> getPushHandler() {
      return this.pushHandler;
   }

   @Override
   public Supplier<HttpFields> getTrailersSupplier() {
      return this.trailers;
   }

   @Override
   public CompletableFuture<Boolean> abort(Throwable cause) {
      if (this.aborted.compareAndSet(null, Objects.requireNonNull(cause))) {
         Completable<Boolean> promise = new Completable();
         this.conversation.abort(cause, promise);
         return promise;
      } else {
         return CompletableFuture.completedFuture(false);
      }
   }

   @Override
   public Throwable getAbortCause() {
      return this.aborted.get();
   }

   boolean normalized() {
      boolean result = this.normalized;
      this.normalized = true;
      return result;
   }

   private String buildQuery() {
      StringBuilder result = new StringBuilder();
      Iterator<Field> iterator = this.params.iterator();

      while (iterator.hasNext()) {
         Field field = iterator.next();
         List<String> values = field.getValues();

         for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
               result.append("&");
            }

            result.append(field.getName()).append("=");
            result.append(this.urlEncode(values.get(i)));
         }

         if (iterator.hasNext()) {
            result.append("&");
         }
      }

      return result.toString();
   }

   private String urlEncode(String value) {
      return value == null ? "" : URLEncoder.encode(value, StandardCharsets.UTF_8);
   }

   private void extractParams(String query) {
      if (query != null) {
         for (String nameValue : query.split("&")) {
            String[] parts = nameValue.split("=");
            if (parts.length > 0) {
               String name = this.urlDecode(parts[0]);
               if (name.trim().length() != 0) {
                  this.param(name, parts.length < 2 ? "" : this.urlDecode(parts[1]), true);
               }
            }
         }
      }
   }

   private String urlDecode(String value) {
      return URLDecoder.decode(value, StandardCharsets.UTF_8);
   }

   private URI buildURI(boolean withQuery) {
      String path = this.getPath();
      String query = this.getQuery();
      if (query != null && withQuery) {
         path = path + "?" + query;
      }

      URI result = this.newURI(path);
      if (result == null) {
         return NULL_URI;
      } else {
         if (!result.isAbsolute()) {
            result = URI.create(new Origin(this.getScheme(), this.getHost(), this.getPort()).asString() + path);
         }

         return result;
      }
   }

   private URI newURI(String path) {
      try {
         if ("*".equals(path)) {
            return null;
         } else {
            URI result = new URI(path);
            return result.isOpaque() ? null : result;
         }
      } catch (URISyntaxException var3) {
         return null;
      }
   }

   @Override
   public String toString() {
      return String.format("%s[%s %s %s]@%x", this.getClass().getSimpleName(), this.getMethod(), this.getPath(), this.getVersion(), this.hashCode());
   }
}
