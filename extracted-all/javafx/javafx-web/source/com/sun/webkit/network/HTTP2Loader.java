package com.sun.webkit.network;

import com.sun.javafx.logging.PlatformLogger;
import com.sun.javafx.logging.PlatformLogger.Level;
import com.sun.javafx.tk.Toolkit;
import com.sun.webkit.Invoker;
import com.sun.webkit.WebPage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.ConnectException;
import java.net.CookieHandler;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpTimeoutException;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.ByteBuffer;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import javax.net.ssl.SSLHandshakeException;

final class HTTP2Loader extends URLLoaderBase {
   private static final PlatformLogger logger = PlatformLogger.getLogger(URLLoader.class.getName());
   private final WebPage webPage;
   private final boolean asynchronous;
   private String url;
   private String method;
   private final String headers;
   private FormDataElement[] formDataElements;
   private final long data;
   private volatile boolean canceled = false;
   private final CompletableFuture<Void> response;
   private static final HttpClient HTTP_CLIENT = AccessController.doPrivileged(
      (PrivilegedAction<HttpClient>)(() -> HttpClient.newBuilder()
            .version(Version.HTTP_2)
            .followRedirects(Redirect.NEVER)
            .connectTimeout(Duration.ofSeconds(30L))
            .cookieHandler(CookieHandler.getDefault())
            .build())
   );
   private static final int DEFAULT_BUFSIZE = 40960;
   private static final ByteBuffer BUFFER;

   static HTTP2Loader create(WebPage var0, ByteBufferPool var1, boolean var2, String var3, String var4, String var5, FormDataElement[] var6, long var7) {
      return !var3.startsWith("http://") && !var3.startsWith("https://") ? null : new HTTP2Loader(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   private String[] getCustomHeaders() {
      Locale var1 = Locale.getDefault();
      String var2 = "";
      if (!var1.equals(Locale.US) && !var1.equals(Locale.ENGLISH)) {
         var2 = var1.getCountry().isEmpty() ? var1.getLanguage() + "," : var1.getLanguage() + "-" + var1.getCountry() + ",";
      }

      return new String[]{
         "Accept-Language", var2.toLowerCase() + "en-us;q=0.8,en;q=0.7", "Accept-Encoding", "gzip, inflate", "Accept-Charset", "ISO-8859-1,utf-8;q=0.7,*;q=0.7"
      };
   }

   private String[] getRequestHeaders() {
      return Arrays.stream(this.headers.split("\n")).flatMap(var0 -> Stream.of(var0.split(":", 2))).toArray(String[]::new);
   }

   private URI toURI() throws MalformedURLException {
      URI var1;
      try {
         var1 = new URI(this.url);
      } catch (IllegalArgumentException | URISyntaxException var5) {
         try {
            URL var3 = URLs.newURL(this.url);
            var1 = new URI(var3.getProtocol(), var3.getUserInfo(), var3.getHost(), var3.getPort(), var3.getPath(), var3.getQuery(), var3.getRef());
         } catch (MalformedURLException | IllegalArgumentException | URISyntaxException var4) {
            throw new MalformedURLException(this.url);
         }
      }

      return var1;
   }

   private BodyPublisher getFormDataPublisher() {
      if (this.formDataElements == null) {
         return BodyPublishers.noBody();
      } else {
         Vector var1 = new Vector();
         final AtomicLong var2 = new AtomicLong();

         for (FormDataElement var6 : this.formDataElements) {
            try {
               var6.open();
               var2.addAndGet(var6.getSize());
               var1.add(var6.getInputStream());
            } catch (IOException var8) {
               return null;
            }
         }

         SequenceInputStream var9 = new SequenceInputStream(var1.elements());
         final BodyPublisher var10 = BodyPublishers.ofInputStream(() -> var9);
         return new BodyPublisher() {
            @Override
            public long contentLength() {
               return var2.longValue() <= 2147483647L ? var2.longValue() : -1L;
            }

            @Override
            public void subscribe(final Subscriber<? super ByteBuffer> var1) {
               var10.subscribe(new Subscriber<ByteBuffer>() {
                  @Override
                  public void onComplete() {
                     var1.onComplete();
                  }

                  @Override
                  public void onError(Throwable var1x) {
                     var1.onError(var1x);
                  }

                  public void onNext(ByteBuffer var1x) {
                     var1.onNext(var1x);
                     HTTP2Loader.this.didSendData((long)var1x.limit(), var2.longValue());
                  }

                  @Override
                  public void onSubscribe(Subscription var1x) {
                     var1.onSubscribe(var1x);
                  }
               });
            }
         };
      }
   }

   private InputStream createZIPStream(String var1, InputStream var2) throws IOException {
      if ("gzip".equalsIgnoreCase(var1)) {
         return new GZIPInputStream(var2);
      } else {
         return (InputStream)("deflate".equalsIgnoreCase(var1) ? new InflaterInputStream(var2) : var2);
      }
   }

   private BodySubscriber<Void> createZIPEncodedBodySubscriber(String var1) {
      if (!"gzip".equalsIgnoreCase(var1) && !"inflate".equalsIgnoreCase(var1)) {
         logger.severe(String.format("Unknown encoding type '%s' found, discarding", var1));
         return BodySubscribers.discarding();
      } else {
         final BodySubscriber var2 = BodySubscribers.ofInputStream();
         final CompletionStage var3 = var2.getBody().thenAcceptAsync(var2x -> {
            try {
               InputStream var3x = var2x;

               try (InputStream var4 = this.createZIPStream(var1, var2x)) {
                  while (!this.canceled) {
                     byte[] var5 = new byte[8192];
                     int var6 = var4.read(var5);
                     if (var6 < 0) {
                        this.didFinishLoading();
                        break;
                     }

                     this.didReceiveData(var5, var6);
                  }
               } catch (Throwable var10) {
                  if (var2x != null) {
                     try {
                        var3x.close();
                     } catch (Throwable var7) {
                        var10.addSuppressed(var7);
                     }
                  }

                  throw var10;
               }

               if (var2x != null) {
                  var2x.close();
               }
            } catch (IOException var11) {
               this.didFail(var11);
            }
         });
         return new BodySubscriber<Void>() {
            @Override
            public void onComplete() {
               var2.onComplete();
            }

            @Override
            public void onError(Throwable var1) {
               var2.onError(var1);
            }

            public void onNext(List<ByteBuffer> var1) {
               var2.onNext(var1);
            }

            @Override
            public void onSubscribe(Subscription var1) {
               var2.onSubscribe(var1);
            }

            @Override
            public CompletionStage<Void> getBody() {
               return var2.getBody().thenCombine(var3, (var0, var1) -> null);
            }
         };
      }
   }

   private BodySubscriber<Void> createNormalBodySubscriber() {
      return BodySubscribers.fromSubscriber(new Subscriber<List<ByteBuffer>>() {
         private Subscription subscription;
         private final AtomicBoolean subscribed = new AtomicBoolean();

         @Override
         public void onComplete() {
            HTTP2Loader.this.didFinishLoading();
         }

         @Override
         public void onError(Throwable var1) {
         }

         public void onNext(List<ByteBuffer> var1) {
            HTTP2Loader.this.didReceiveData(var1);
            this.requestIfNotCancelled();
         }

         @Override
         public void onSubscribe(Subscription var1) {
            if (!this.subscribed.compareAndSet(false, true)) {
               var1.cancel();
            } else {
               this.subscription = var1;
               this.requestIfNotCancelled();
            }
         }

         private void requestIfNotCancelled() {
            if (HTTP2Loader.this.canceled) {
               this.subscription.cancel();
            } else {
               this.subscription.request(1L);
            }
         }
      });
   }

   private BodySubscriber<Void> getBodySubscriber(String var1) {
      return var1.isEmpty() ? this.createNormalBodySubscriber() : this.createZIPEncodedBodySubscriber(var1);
   }

   private HTTP2Loader(WebPage var1, ByteBufferPool var2, boolean var3, String var4, String var5, String var6, FormDataElement[] var7, long var8) {
      this.webPage = var1;
      this.asynchronous = var3;
      this.url = var4;
      this.method = var5;
      this.headers = var6;
      this.formDataElements = var7;
      this.data = var8;

      URI var10;
      try {
         var10 = this.toURI();
      } catch (MalformedURLException var14) {
         this.response = null;
         this.didFail(var14);
         return;
      }

      HttpRequest var11 = HttpRequest.newBuilder()
         .uri(var10)
         .headers(this.getRequestHeaders())
         .headers(this.getCustomHeaders())
         .version(Version.HTTP_2)
         .method(var5, this.getFormDataPublisher())
         .build();
      BodyHandler var12 = var1x -> {
         if (!this.handleRedirectionIfNeeded(var1x)) {
            this.didReceiveResponse(var1x);
         }

         return this.getBodySubscriber(getContentEncoding(var1x));
      };
      CompletableFuture var13 = AccessController.doPrivileged(
         (PrivilegedAction<CompletableFuture>)(() -> HTTP_CLIENT.sendAsync(var11, var12).thenAccept(var0 -> {
            }).exceptionally(var1xx -> this.didFail(var1xx.getCause()))), var1.getAccessControlContext()
      );
      this.response = var13;
      if (!var3) {
         this.waitForRequestToComplete();
      }
   }

   @Override
   public void fwkCancel() {
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest(String.format("data: [0x%016X]", this.data));
      }

      this.canceled = true;
   }

   private void callBackIfNotCanceled(Runnable var1) {
      Invoker.getInvoker().invokeOnEventThread(() -> {
         if (!this.canceled) {
            var1.run();
         }
      });
   }

   private void waitForRequestToComplete() {
      Object var1 = new Object();
      this.response.handle((var1x, var2) -> {
         Invoker.getInvoker().invokeOnEventThread(() -> Toolkit.getToolkit().exitNestedEventLoop(var1, null));
         return null;
      });
      Toolkit.getToolkit().enterNestedEventLoop(var1);
   }

   private boolean handleRedirectionIfNeeded(ResponseInfo var1) {
      switch (var1.statusCode()) {
         case 301:
         case 302:
         case 303:
         case 307:
            this.willSendRequest(var1);
            return true;
         case 304:
            this.didReceiveResponse(var1);
            this.didFinishLoading();
            return true;
         case 305:
         case 306:
         default:
            return false;
      }
   }

   private static long getContentLength(ResponseInfo var0) {
      return var0.headers().firstValueAsLong("content-length").orElse(-1L);
   }

   private static String getContentType(ResponseInfo var0) {
      return var0.headers().firstValue("content-type").orElse("application/octet-stream");
   }

   private static String getContentEncoding(ResponseInfo var0) {
      return var0.headers().firstValue("content-encoding").orElse("");
   }

   private static String getHeadersAsString(ResponseInfo var0) {
      return var0.headers()
            .map()
            .entrySet()
            .stream()
            .map(var0x -> String.format("%s:%s", var0x.getKey(), var0x.getValue().stream().collect(Collectors.joining(","))))
            .collect(Collectors.joining("\n"))
         + "\n";
   }

   private void willSendRequest(ResponseInfo var1) {
      this.callBackIfNotCanceled(
         () -> twkWillSendRequest(var1.statusCode(), getContentType(var1), "", getContentLength(var1), getHeadersAsString(var1), this.url, this.data)
      );
   }

   private void didReceiveResponse(ResponseInfo var1) {
      this.callBackIfNotCanceled(
         () -> twkDidReceiveResponse(var1.statusCode(), getContentType(var1), "", getContentLength(var1), getHeadersAsString(var1), this.url, this.data)
      );
   }

   private ByteBuffer getDirectBuffer(int var1) {
      ByteBuffer var2 = BUFFER;
      if (var1 > var2.capacity()) {
         var2 = ByteBuffer.allocateDirect(var1);
      }

      return var2.clear();
   }

   private ByteBuffer copyToDirectBuffer(ByteBuffer var1) {
      return this.getDirectBuffer(var1.limit()).put(var1).flip();
   }

   private void didReceiveData(byte[] var1, int var2) {
      this.callBackIfNotCanceled(() -> this.notifyDidReceiveData(this.getDirectBuffer(var2).put(var1, 0, var2).flip()));
   }

   private void didReceiveData(List<ByteBuffer> var1) {
      this.callBackIfNotCanceled(() -> var1.stream().map(this::copyToDirectBuffer).forEach(this::notifyDidReceiveData));
   }

   private void notifyDidReceiveData(ByteBuffer var1) {
      Invoker.getInvoker().checkEventThread();
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest(String.format("byteBuffer: [%s], position: [%s], remaining: [%s], data: [0x%016X]", var1, var1.position(), var1.remaining(), this.data));
      }

      twkDidReceiveData(var1, var1.position(), var1.remaining(), this.data);
   }

   private void didFinishLoading() {
      this.callBackIfNotCanceled(this::notifyDidFinishLoading);
   }

   private void notifyDidFinishLoading() {
      Invoker.getInvoker().checkEventThread();
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest(String.format("data: [0x%016X]", this.data));
      }

      twkDidFinishLoading(this.data);
   }

   private Void didFail(Throwable var1) {
      this.callBackIfNotCanceled(() -> {
         byte var2;
         try {
            throw var1;
         } catch (MalformedURLException var4) {
            var2 = 2;
         } catch (AccessControlException var5) {
            var2 = 8;
         } catch (UnknownHostException var6) {
            var2 = 1;
         } catch (NoRouteToHostException var7) {
            var2 = 6;
         } catch (ConnectException var8) {
            var2 = 4;
         } catch (SocketException var9) {
            var2 = 5;
         } catch (SSLHandshakeException var10) {
            var2 = 3;
         } catch (HttpTimeoutException | SocketTimeoutException var11) {
            var2 = 7;
         } catch (FileNotFoundException var12) {
            var2 = 11;
         } catch (Throwable var13) {
            var2 = 99;
         }

         this.notifyDidFail(var2, this.url, var1.getMessage());
      });
      return null;
   }

   private void notifyDidFail(int var1, String var2, String var3) {
      Invoker.getInvoker().checkEventThread();
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest(String.format("errorCode: [%d], url: [%s], message: [%s], data: [0x%016X]", var1, var2, var3, this.data));
      }

      twkDidFail(var1, var2, var3, this.data);
   }

   private void didSendData(long var1, long var3) {
      this.callBackIfNotCanceled(() -> this.notifyDidSendData(var1, var3));
   }

   private void notifyDidSendData(long var1, long var3) {
      Invoker.getInvoker().checkEventThread();
      if (logger.isLoggable(Level.FINEST)) {
         logger.finest(String.format("totalBytesSent: [%d], totalBytesToBeSent: [%d], data: [0x%016X]", var1, var3, this.data));
      }

      twkDidSendData(var1, var3, this.data);
   }

   static {
      int var0 = AccessController.doPrivileged(
         (PrivilegedAction<Integer>)(() -> Integer.valueOf(System.getProperty("jdk.httpclient.bufsize", Integer.toString(40960))))
      );
      BUFFER = ByteBuffer.allocateDirect(var0);
   }
}
