package org.eclipse.jetty.client.transport;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.eclipse.jetty.client.Authentication;
import org.eclipse.jetty.client.AuthenticationStore;
import org.eclipse.jetty.client.BytesRequestContent;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.HttpRequestException;
import org.eclipse.jetty.client.ProxyConfiguration;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpCookieStore;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpCookieStore.Empty;
import org.eclipse.jetty.http.HttpField.LongValueHttpField;
import org.eclipse.jetty.io.CyclicTimeouts;
import org.eclipse.jetty.util.Attachable;
import org.eclipse.jetty.util.NanoTime;
import org.eclipse.jetty.util.thread.AutoLock;
import org.eclipse.jetty.util.thread.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class HttpConnection implements IConnection, Attachable {
   private static final Logger LOG = LoggerFactory.getLogger(HttpConnection.class);
   private final AutoLock lock = new AutoLock();
   private final HttpDestination destination;
   private final HttpConnection.RequestTimeouts requestTimeouts;
   private Object attachment;
   private int idleTimeoutGuard;
   private long idleTimeoutNanoTime;

   protected HttpConnection(HttpDestination destination) {
      this.destination = destination;
      this.requestTimeouts = new HttpConnection.RequestTimeouts(destination.getHttpClient().getScheduler());
      this.idleTimeoutNanoTime = NanoTime.now();
   }

   public HttpClient getHttpClient() {
      return this.destination.getHttpClient();
   }

   public HttpDestination getHttpDestination() {
      return this.destination;
   }

   protected abstract Iterator<HttpChannel> getHttpChannels();

   @Override
   public void send(Request request, Response.CompleteListener listener) {
      HttpRequest httpRequest = (HttpRequest)request;
      ResponseListeners responseListeners = httpRequest.getResponseListeners();
      httpRequest.sent();
      if (listener != null) {
         responseListeners.addCompleteListener(listener, true);
      }

      HttpExchange exchange = new HttpExchange(this.getHttpDestination(), httpRequest);
      SendFailure result = this.send(exchange);
      if (result != null) {
         httpRequest.abort(result.failure);
      }
   }

   protected SendFailure send(HttpChannel channel, HttpExchange exchange) {
      AutoLock ignored = this.lock.lock();

      boolean send;
      try {
         send = this.idleTimeoutGuard >= 0;
         if (send) {
            this.idleTimeoutGuard++;
         }
      } catch (Throwable var12) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var10) {
               var12.addSuppressed(var10);
            }
         }

         throw var12;
      }

      if (ignored != null) {
         ignored.close();
      }

      if (send) {
         HttpRequest request = exchange.getRequest();
         SendFailure result;
         if (channel.associate(exchange)) {
            request.sent();
            this.requestTimeouts.schedule(channel);
            channel.send();
            result = null;
         } else {
            channel.release();
            result = new SendFailure(new HttpRequestException("Could not associate request to connection", request), false);
         }

         AutoLock ignoredx = this.lock.lock();

         try {
            this.idleTimeoutGuard--;
            this.idleTimeoutNanoTime = NanoTime.now();
         } catch (Throwable var11) {
            if (ignoredx != null) {
               try {
                  ignoredx.close();
               } catch (Throwable var9) {
                  var11.addSuppressed(var9);
               }
            }

            throw var11;
         }

         if (ignoredx != null) {
            ignoredx.close();
         }

         return result;
      } else {
         return new SendFailure(new TimeoutException(), true);
      }
   }

   protected void normalizeRequest(HttpRequest request) {
      boolean normalized = request.normalized();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Normalizing {} {}", !normalized, request);
      }

      if (!normalized) {
         String path = request.getPath();
         if (path.trim().length() == 0) {
            path = "/";
            request.path(path);
         }

         boolean http1 = request.getVersion().getVersion() <= 11;
         boolean applyProxyAuthentication = false;
         ProxyConfiguration.Proxy proxy = this.destination.getProxy();
         if (proxy instanceof HttpProxy httpProxy) {
            boolean tunnelled = httpProxy.requiresTunnel(this.destination.getOrigin());
            if (http1 && !tunnelled) {
               URI uri = request.getURI();
               if (uri != null) {
                  request.path(uri.toString());
               }
            }

            applyProxyAuthentication = !tunnelled;
         }

         HttpFields headers = request.getHeaders();
         if (http1 && !headers.contains(HttpHeader.HOST.asString())) {
            URI uri = request.getURI();
            if (uri != null) {
               request.addHeader(new HttpField(HttpHeader.HOST, uri.getAuthority()));
            } else {
               request.addHeader(this.getHttpDestination().getHostField());
            }
         }

         Request.Content content = request.getBody();
         if (content == null) {
            request.body(new BytesRequestContent());
         } else {
            if (!headers.contains(HttpHeader.CONTENT_TYPE)) {
               String contentType = content.getContentType();
               if (contentType == null) {
                  contentType = this.getHttpClient().getDefaultRequestContentType();
               }

               if (contentType != null) {
                  HttpField field = new HttpField(HttpHeader.CONTENT_TYPE, contentType);
                  request.addHeader(field);
               }
            }

            long contentLength = content.getLength();
            if (contentLength >= 0L && !headers.contains(HttpHeader.CONTENT_LENGTH)) {
               request.addHeader(new LongValueHttpField(HttpHeader.CONTENT_LENGTH, contentLength));
            }
         }

         StringBuilder cookies = this.convertCookies(request.getCookies(), null);
         HttpCookieStore cookieStore = this.getHttpClient().getHttpCookieStore();
         if (cookieStore != null && cookieStore.getClass() != Empty.class) {
            URI uri = request.getURI();
            if (uri != null) {
               cookies = this.convertCookies(cookieStore.match(uri), cookies);
            }
         }

         if (cookies != null) {
            HttpField cookieField = new HttpField(HttpHeader.COOKIE, cookies.toString());
            request.addHeader(cookieField);
         }

         if (applyProxyAuthentication) {
            this.applyProxyAuthentication(request, proxy);
         }

         this.applyRequestAuthentication(request);
      }
   }

   private StringBuilder convertCookies(List<HttpCookie> cookies, StringBuilder builder) {
      for (HttpCookie cookie : cookies) {
         if (builder == null) {
            builder = new StringBuilder();
         }

         if (builder.length() > 0) {
            builder.append("; ");
         }

         builder.append(cookie.getName()).append("=").append(cookie.getValue());
      }

      return builder;
   }

   private void applyRequestAuthentication(Request request) {
      AuthenticationStore authenticationStore = this.getHttpClient().getAuthenticationStore();
      if (authenticationStore.hasAuthenticationResults()) {
         URI uri = request.getURI();
         if (uri != null) {
            Authentication.Result result = authenticationStore.findAuthenticationResult(uri);
            if (result != null) {
               result.apply(request);
            }
         }
      }
   }

   private void applyProxyAuthentication(Request request, ProxyConfiguration.Proxy proxy) {
      if (proxy != null) {
         Authentication.Result result = this.getHttpClient().getAuthenticationStore().findAuthenticationResult(proxy.getURI());
         if (result != null) {
            result.apply(request);
         }
      }
   }

   public boolean onIdleTimeout(long idleTimeout, Throwable failure) {
      AutoLock ignored = this.lock.lock();

      boolean var8;
      label60: {
         boolean elapsed;
         try {
            if (this.idleTimeoutGuard == 0) {
               long elapsedx = NanoTime.millisSince(this.idleTimeoutNanoTime);
               boolean idle = elapsedx > idleTimeout / 2L;
               if (idle) {
                  this.idleTimeoutGuard = -1;
               }

               if (LOG.isDebugEnabled()) {
                  LOG.debug("Idle timeout {}/{}ms - {}", new Object[]{elapsedx, idleTimeout, this});
               }

               var8 = idle;
               break label60;
            }

            if (LOG.isDebugEnabled()) {
               LOG.debug("Idle timeout skipped - {}", this);
            }

            elapsed = false;
         } catch (Throwable var10) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var9) {
                  var10.addSuppressed(var9);
               }
            }

            throw var10;
         }

         if (ignored != null) {
            ignored.close();
         }

         return elapsed;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var8;
   }

   public void setAttachment(Object obj) {
      this.attachment = obj;
   }

   public Object getAttachment() {
      return this.attachment;
   }

   public void destroy() {
      this.requestTimeouts.destroy();
   }

   @Override
   public String toString() {
      return String.format("%s@%h", this.getClass().getSimpleName(), this);
   }

   private class RequestTimeouts extends CyclicTimeouts<HttpChannel> {
      private RequestTimeouts(Scheduler scheduler) {
         super(scheduler);
      }

      protected Iterator<HttpChannel> iterator() {
         return HttpConnection.this.getHttpChannels();
      }

      protected boolean onExpired(HttpChannel channel) {
         HttpExchange exchange = channel.getHttpExchange();
         if (exchange == null) {
            return false;
         } else {
            HttpRequest request = exchange.getRequest();
            request.abort(new TimeoutException("Total timeout " + request.getConversation().getTimeout() + " ms elapsed"));
            return false;
         }
      }
   }
}
