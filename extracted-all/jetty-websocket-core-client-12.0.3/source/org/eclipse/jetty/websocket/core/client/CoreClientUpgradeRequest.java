package org.eclipse.jetty.websocket.core.client;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpUpgrader;
import org.eclipse.jetty.client.Request;
import org.eclipse.jetty.client.Response;
import org.eclipse.jetty.client.Result;
import org.eclipse.jetty.client.HttpUpgrader.Factory;
import org.eclipse.jetty.client.Request.Listener;
import org.eclipse.jetty.client.Response.CompleteListener;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpFields;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.http.HttpFields.Mutable;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.ExceptionUtil;
import org.eclipse.jetty.util.QuotedStringTokenizer;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.websocket.core.Behavior;
import org.eclipse.jetty.websocket.core.CoreSession;
import org.eclipse.jetty.websocket.core.ExtensionConfig;
import org.eclipse.jetty.websocket.core.ExtensionStack;
import org.eclipse.jetty.websocket.core.FrameHandler;
import org.eclipse.jetty.websocket.core.Negotiated;
import org.eclipse.jetty.websocket.core.WebSocketConnection;
import org.eclipse.jetty.websocket.core.WebSocketConstants;
import org.eclipse.jetty.websocket.core.WebSocketCoreSession;
import org.eclipse.jetty.websocket.core.Configuration.ConfigurationCustomizer;
import org.eclipse.jetty.websocket.core.client.internal.HttpUpgraderOverHTTP;
import org.eclipse.jetty.websocket.core.client.internal.HttpUpgraderOverHTTP2;
import org.eclipse.jetty.websocket.core.exception.UpgradeException;
import org.eclipse.jetty.websocket.core.exception.WebSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class CoreClientUpgradeRequest implements CompleteListener, Factory {
   private static final Logger LOG = LoggerFactory.getLogger(CoreClientUpgradeRequest.class);
   private final Request request;
   private final CompletableFuture<CoreSession> futureCoreSession;
   private final WebSocketCoreClient wsClient;
   private FrameHandler frameHandler;
   private final ConfigurationCustomizer customizer = new ConfigurationCustomizer();
   private final List<UpgradeListener> upgradeListeners = new ArrayList<>();
   private List<ExtensionConfig> requestedExtensions = new ArrayList<>();

   public static CoreClientUpgradeRequest from(WebSocketCoreClient webSocketClient, URI requestURI, final FrameHandler frameHandler) {
      return new CoreClientUpgradeRequest(webSocketClient, requestURI) {
         @Override
         public FrameHandler getFrameHandler() {
            return frameHandler;
         }
      };
   }

   public CoreClientUpgradeRequest(WebSocketCoreClient webSocketClient, URI requestURI) {
      this.request = webSocketClient.getHttpClient().newRequest(requestURI);
      this.request.attribute(Factory.class.getName(), this);
      if (!requestURI.isAbsolute()) {
         throw new IllegalArgumentException("WebSocket URI must be absolute");
      } else if (StringUtil.isBlank(requestURI.getScheme())) {
         throw new IllegalArgumentException("WebSocket URI must include a scheme");
      } else {
         String scheme = requestURI.getScheme();
         if (!HttpScheme.WS.is(scheme) && !HttpScheme.WSS.is(scheme)) {
            throw new IllegalArgumentException("WebSocket URI scheme only supports [ws] and [wss], not [" + scheme + "]");
         } else if (requestURI.getHost() == null) {
            throw new IllegalArgumentException("Invalid WebSocket URI: host not present");
         } else {
            this.wsClient = webSocketClient;
            this.futureCoreSession = new CompletableFuture<>();
            this.futureCoreSession.whenComplete((session, throwable) -> {
               if (throwable != null) {
                  this.request.abort(throwable);
               }
            });
         }
      }
   }

   public String getMethod() {
      return this.request.getMethod();
   }

   public URI getURI() {
      return this.request.getURI();
   }

   public HttpVersion getVersion() {
      return this.request.getVersion();
   }

   public void listener(Listener listener) {
      this.request.listener(listener);
   }

   public void headers(Consumer<Mutable> consumer) {
      this.request.headers(consumer);
   }

   public HttpFields getHeaders() {
      return this.request.getHeaders();
   }

   public List<HttpCookie> getCookies() {
      return this.request.getCookies();
   }

   public void timeout(long value, TimeUnit unit) {
      this.request.timeout(value, unit);
   }

   public void setConfiguration(ConfigurationCustomizer config) {
      config.customize(this.customizer);
   }

   public void addListener(UpgradeListener listener) {
      this.upgradeListeners.add(listener);
   }

   public void addExtensions(ExtensionConfig... configs) {
      this.requestedExtensions.addAll(Arrays.asList(configs));
   }

   public void addExtensions(String... configs) {
      for (String config : configs) {
         this.requestedExtensions.add(ExtensionConfig.parse(config));
      }
   }

   public List<ExtensionConfig> getExtensions() {
      return this.requestedExtensions;
   }

   public void setExtensions(List<ExtensionConfig> configs) {
      this.requestedExtensions = configs;
   }

   public List<String> getSubProtocols() {
      return this.request.getHeaders().getCSV(HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL, true);
   }

   public void setSubProtocols(String... protocols) {
      this.request.headers(headers -> {
         headers.remove(HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL);

         for (String protocol : protocols) {
            headers.add(HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL, protocol);
         }
      });
   }

   public void setSubProtocols(List<String> protocols) {
      this.request.headers(headers -> {
         headers.remove(HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL);

         for (String protocol : protocols) {
            headers.add(HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL, protocol);
         }
      });
   }

   public void send(CompleteListener listener) {
      try {
         this.frameHandler = this.getFrameHandler();
         if (this.frameHandler == null) {
            throw new IllegalArgumentException("FrameHandler could not be created");
         }
      } catch (Throwable var3) {
         throw new IllegalArgumentException("FrameHandler could not be created", var3);
      }

      this.request.send(listener);
   }

   public CompletableFuture<CoreSession> sendAsync() {
      this.send(this);
      return this.futureCoreSession;
   }

   public void onComplete(Result result) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("onComplete() - {}", result);
      }

      URI requestURI = result.getRequest().getURI();
      Response response = result.getResponse();
      int responseStatusCode = response.getStatus();
      String responseLine = responseStatusCode + " " + response.getReason();
      if (result.isFailed()) {
         if (LOG.isDebugEnabled()) {
            if (result.getFailure() != null) {
               LOG.debug("General Failure", result.getFailure());
            }

            if (result.getRequestFailure() != null) {
               LOG.debug("Request Failure", result.getRequestFailure());
            }

            if (result.getResponseFailure() != null) {
               LOG.debug("Response Failure", result.getResponseFailure());
            }
         }

         Throwable failure = result.getFailure();
         boolean wrapFailure = !(failure instanceof IOException) && !(failure instanceof UpgradeException);
         if (wrapFailure) {
            failure = new UpgradeException(requestURI, responseStatusCode, responseLine, failure);
         }

         this.handleException(failure);
      } else {
         if (responseStatusCode != 101) {
            this.handleException(
               new UpgradeException(requestURI, responseStatusCode, "Failed to upgrade to websocket: Unexpected HTTP Response Status Code: " + responseLine)
            );
         }
      }
   }

   protected void handleException(Throwable failure) {
      this.futureCoreSession.completeExceptionally(failure);
      if (this.frameHandler != null) {
         try {
            this.frameHandler.onError(failure, Callback.NOOP);
         } catch (Throwable var3) {
            LOG.warn("FrameHandler onError threw", var3);
         }
      }
   }

   public HttpUpgrader newHttpUpgrader(HttpVersion version) {
      if (version == HttpVersion.HTTP_1_1) {
         return new HttpUpgraderOverHTTP(this);
      } else if (version == HttpVersion.HTTP_2) {
         return new HttpUpgraderOverHTTP2(this);
      } else {
         throw new UnsupportedOperationException("Unsupported HTTP version for upgrade: " + version);
      }
   }

   protected void customize(EndPoint endPoint) {
   }

   public abstract FrameHandler getFrameHandler();

   public void requestComplete() {
      String extensionString = this.requestedExtensions
         .stream()
         .filter(ec -> !ec.getName().startsWith("@"))
         .<CharSequence>map(ExtensionConfig::getParameterizedNameWithoutInternalParams)
         .collect(Collectors.joining(","));
      if (!StringUtil.isEmpty(extensionString)) {
         this.request.headers(headers -> headers.add(HttpHeader.SEC_WEBSOCKET_EXTENSIONS, extensionString));
      }

      Throwable listenerError = this.notifyUpgradeListeners(listener -> listener.onHandshakeRequest(this.request));
      if (listenerError != null) {
         this.request.abort(listenerError);
      } else {
         String extsAfterListener = String.join(",", this.request.getHeaders().getCSV(HttpHeader.SEC_WEBSOCKET_EXTENSIONS, true));
         if (!extensionString.equals(extsAfterListener)) {
            if (!this.requestedExtensions.isEmpty()) {
               this.request.abort(new IllegalStateException("Extensions set in both the ClientUpgradeRequest and UpgradeListener"));
            }

            this.requestedExtensions = ExtensionConfig.parseList(new String[]{extsAfterListener});
         }
      }
   }

   private Throwable notifyUpgradeListeners(Consumer<UpgradeListener> action) {
      Throwable multiException = null;

      for (UpgradeListener listener : this.upgradeListeners) {
         try {
            action.accept(listener);
         } catch (Throwable var6) {
            LOG.info("Exception while invoking listener {}", listener, var6);
            multiException = ExceptionUtil.combine(multiException, var6);
         }
      }

      return multiException;
   }

   public void upgrade(Response response, EndPoint endPoint) {
      List<ExtensionConfig> negotiatedExtensions = new ArrayList<>();
      HttpField extField = response.getHeaders().getField(HttpHeader.SEC_WEBSOCKET_EXTENSIONS);
      if (extField != null) {
         String[] extValues = extField.getValues();
         if (extValues != null) {
            for (String extVal : extValues) {
               Iterator<String> i = QuotedStringTokenizer.CSV.tokenize(extVal);

               while (i.hasNext()) {
                  negotiatedExtensions.add(ExtensionConfig.parse(i.next()));
               }
            }
         }
      }

      List<ExtensionConfig> negotiatedWithInternal = new ArrayList<>(this.requestedExtensions);
      Iterator<ExtensionConfig> iterator = negotiatedWithInternal.iterator();

      while (iterator.hasNext()) {
         ExtensionConfig extConfig = iterator.next();
         if (!extConfig.isInternalExtension()) {
            long negExtsCount = negotiatedExtensions.stream().filter(ec -> extConfig.getName().equals(ec.getName())).count();
            if (negExtsCount < 1L) {
               iterator.remove();
            } else {
               long duplicateCount = negotiatedWithInternal.stream().filter(ec -> extConfig.getName().equals(ec.getName())).count();
               if (duplicateCount > 1L) {
                  iterator.remove();
               }
            }
         }
      }

      for (ExtensionConfig config : negotiatedExtensions) {
         if (!config.getName().startsWith("@")) {
            boolean wasRequested = false;

            for (ExtensionConfig requestedConfig : this.requestedExtensions) {
               if (config.getName().equalsIgnoreCase(requestedConfig.getName())) {
                  for (Entry<String, String> entry : requestedConfig.getInternalParameters()) {
                     config.setParameter(entry.getKey(), entry.getValue());
                  }

                  wasRequested = true;
                  break;
               }
            }

            if (!wasRequested) {
               throw new WebSocketException("Upgrade failed: Sec-WebSocket-Extensions contained extension not requested");
            }

            long numExtsWithSameName = negotiatedExtensions.stream().filter(c -> config.getName().equalsIgnoreCase(c.getName())).count();
            if (numExtsWithSameName > 1L) {
               throw new WebSocketException("Upgrade failed: Sec-WebSocket-Extensions contained more than one extension of the same name");
            }
         }
      }

      ExtensionStack extensionStack = new ExtensionStack(this.wsClient.getWebSocketComponents(), Behavior.CLIENT);
      extensionStack.negotiate(this.requestedExtensions, negotiatedWithInternal);
      String negotiatedSubProtocol = null;
      HttpField subProtocolField = response.getHeaders().getField(HttpHeader.SEC_WEBSOCKET_SUBPROTOCOL);
      if (subProtocolField != null) {
         String[] values = subProtocolField.getValues();
         if (values != null) {
            if (values.length > 1) {
               throw new WebSocketException("Upgrade failed: Too many WebSocket subprotocol's in response: " + Arrays.toString((Object[])values));
            }

            if (values.length == 1) {
               negotiatedSubProtocol = values[0];
            }
         }
      }

      List<String> offeredSubProtocols = this.getSubProtocols();
      if (negotiatedSubProtocol != null && !offeredSubProtocols.contains(negotiatedSubProtocol)) {
         throw new WebSocketException("Upgrade failed: subprotocol [" + negotiatedSubProtocol + "] not found in offered subprotocols " + offeredSubProtocols);
      } else {
         this.customize(endPoint);
         String scheme = this.request.getScheme();
         Negotiated negotiated = new Negotiated(
            this.request.getURI(), negotiatedSubProtocol, HttpScheme.isSecure(scheme), extensionStack, WebSocketConstants.SPEC_VERSION_STRING
         );
         WebSocketCoreSession coreSession = new WebSocketCoreSession(this.frameHandler, Behavior.CLIENT, negotiated, this.wsClient.getWebSocketComponents());
         coreSession.setClassLoader(this.wsClient.getClassLoader());
         this.customizer.customize(coreSession);
         HttpClient httpClient = this.wsClient.getHttpClient();
         ByteBufferPool bufferPool = this.wsClient.getWebSocketComponents().getByteBufferPool();
         WebSocketConnection wsConnection = new WebSocketConnection(endPoint, httpClient.getExecutor(), httpClient.getScheduler(), bufferPool, coreSession);
         coreSession.setWebSocketConnection(wsConnection);
         this.wsClient.getEventListeners().forEach(wsConnection::addEventListener);
         Throwable listenerError = this.notifyUpgradeListeners(listener -> listener.onHandshakeResponse(this.request, response));
         if (listenerError != null) {
            throw new WebSocketException("onHandshakeResponse error", listenerError);
         } else {
            try {
               endPoint.upgrade(wsConnection);
               if (!this.futureCoreSession.complete(coreSession)) {
                  this.futureCoreSession.exceptionally(t -> {
                     coreSession.processConnectionError(t, Callback.NOOP);
                     return null;
                  });
               }
            } catch (Throwable var18) {
               this.futureCoreSession.completeExceptionally(var18);
            }
         }
      }
   }
}
