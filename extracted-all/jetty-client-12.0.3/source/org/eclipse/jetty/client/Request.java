package org.eclipse.jetty.client;

import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.io.Content.Source;
import org.eclipse.jetty.util.Fields;

public interface Request {
   String getScheme();

   Request scheme(String var1);

   String getHost();

   default Request host(String host) {
      return this;
   }

   int getPort();

   default Request port(int port) {
      return this;
   }

   String getMethod();

   Request method(HttpMethod var1);

   Request method(String var1);

   String getPath();

   Request path(String var1);

   String getQuery();

   URI getURI();

   HttpVersion getVersion();

   Request version(HttpVersion var1);

   Fields getParams();

   Request param(String var1, String var2);

   HttpFields getHeaders();

   Request headers(Consumer<Mutable> var1);

   Supplier<HttpFields> getTrailersSupplier();

   Request trailersSupplier(Supplier<HttpFields> var1);

   List<HttpCookie> getCookies();

   Request cookie(HttpCookie var1);

   Request tag(Object var1);

   Object getTag();

   Request attribute(String var1, Object var2);

   Map<String, Object> getAttributes();

   Request.Content getBody();

   Request body(Request.Content var1);

   Request file(Path var1) throws IOException;

   Request file(Path var1, String var2) throws IOException;

   String getAgent();

   Request agent(String var1);

   Request accept(String... var1);

   long getIdleTimeout();

   Request idleTimeout(long var1, TimeUnit var3);

   long getTimeout();

   Request timeout(long var1, TimeUnit var3);

   boolean isFollowRedirects();

   Request followRedirects(boolean var1);

   Request listener(Request.Listener var1);

   Request onRequestQueued(Request.QueuedListener var1);

   Request onRequestBegin(Request.BeginListener var1);

   Request onRequestHeaders(Request.HeadersListener var1);

   Request onRequestCommit(Request.CommitListener var1);

   Request onRequestContent(Request.ContentListener var1);

   Request onRequestSuccess(Request.SuccessListener var1);

   Request onRequestFailure(Request.FailureListener var1);

   Request onResponseBegin(Response.BeginListener var1);

   Request onResponseHeader(Response.HeaderListener var1);

   Request onResponseHeaders(Response.HeadersListener var1);

   Request onResponseContent(Response.ContentListener var1);

   Request onResponseContentAsync(Response.AsyncContentListener var1);

   Request onResponseContentSource(Response.ContentSourceListener var1);

   Request onResponseSuccess(Response.SuccessListener var1);

   Request onResponseFailure(Response.FailureListener var1);

   Request onPush(BiFunction<Request, Request, Response.CompleteListener> var1);

   Request onComplete(Response.CompleteListener var1);

   ContentResponse send() throws InterruptedException, TimeoutException, ExecutionException;

   void send(Response.CompleteListener var1);

   CompletableFuture<Boolean> abort(Throwable var1);

   Throwable getAbortCause();

   public interface BeginListener extends Request.RequestListener {
      void onBegin(Request var1);
   }

   public interface CommitListener extends Request.RequestListener {
      void onCommit(Request var1);
   }

   public interface Content extends Source {
      default String getContentType() {
         return "application/octet-stream";
      }
   }

   public interface ContentListener extends Request.RequestListener {
      void onContent(Request var1, ByteBuffer var2);
   }

   public interface FailureListener extends Request.RequestListener {
      void onFailure(Request var1, Throwable var2);
   }

   public interface HeadersListener extends Request.RequestListener {
      void onHeaders(Request var1);
   }

   public interface Listener
      extends Request.QueuedListener,
      Request.BeginListener,
      Request.HeadersListener,
      Request.CommitListener,
      Request.ContentListener,
      Request.SuccessListener,
      Request.FailureListener {
      @Override
      default void onQueued(Request request) {
      }

      @Override
      default void onBegin(Request request) {
      }

      @Override
      default void onHeaders(Request request) {
      }

      @Override
      default void onCommit(Request request) {
      }

      @Override
      default void onContent(Request request, ByteBuffer content) {
      }

      @Override
      default void onSuccess(Request request) {
      }

      @Override
      default void onFailure(Request request, Throwable failure) {
      }
   }

   public interface QueuedListener extends Request.RequestListener {
      void onQueued(Request var1);
   }

   public interface RequestListener extends EventListener {
   }

   public interface SuccessListener extends Request.RequestListener {
      void onSuccess(Request var1);
   }
}
