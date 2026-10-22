package org.eclipse.jetty.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.jetty.client.internal.HttpContentResponse;
import org.eclipse.jetty.client.transport.HttpConversation;
import org.eclipse.jetty.client.transport.HttpRequest;
import org.eclipse.jetty.client.transport.ResponseListeners;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.QuotedCSV;
import org.eclipse.jetty.util.NanoTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AuthenticationProtocolHandler implements ProtocolHandler {
   public static final int DEFAULT_MAX_CONTENT_LENGTH = 16384;
   private static final Logger LOG = LoggerFactory.getLogger(AuthenticationProtocolHandler.class);
   private static final Pattern CHALLENGE_PATTERN = Pattern.compile(
      "(?<schemeOnly>[!#$%&'*+\\-.^_`|~0-9A-Za-z]+)|(?:(?<scheme>[!#$%&'*+\\-.^_`|~0-9A-Za-z]+)\\s+)?(?:(?<token68>[a-zA-Z0-9\\-._~+/]+=*)|(?<paramName>[!#$%&'*+\\-.^_`|~0-9A-Za-z]+)\\s*=\\s*(?:(?<paramValue>.*)))"
   );
   private final HttpClient client;
   private final int maxContentLength;

   protected AuthenticationProtocolHandler(HttpClient client, int maxContentLength) {
      this.client = client;
      this.maxContentLength = maxContentLength;
   }

   protected HttpClient getHttpClient() {
      return this.client;
   }

   protected abstract HttpHeader getAuthenticateHeader();

   protected abstract HttpHeader getAuthorizationHeader();

   protected abstract URI getAuthenticationURI(Request var1);

   protected abstract String getAuthenticationAttribute();

   @Override
   public Response.Listener getResponseListener() {
      return new AuthenticationProtocolHandler.AuthenticationListener();
   }

   protected List<Authentication.HeaderInfo> getHeaderInfo(String header) throws IllegalArgumentException {
      List<Authentication.HeaderInfo> headerInfos = new ArrayList<>();

      for (String value : new QuotedCSV(true, new String[]{header})) {
         Matcher m = CHALLENGE_PATTERN.matcher(value);
         if (m.matches()) {
            if (m.group("schemeOnly") != null) {
               headerInfos.add(new Authentication.HeaderInfo(this.getAuthorizationHeader(), m.group(1), new HashMap<>()));
            } else {
               if (m.group("scheme") != null) {
                  headerInfos.add(new Authentication.HeaderInfo(this.getAuthorizationHeader(), m.group("scheme"), new HashMap<>()));
               }

               if (headerInfos.isEmpty()) {
                  throw new IllegalArgumentException("Parameters without auth-scheme");
               }

               Map<String, String> authParams = headerInfos.get(headerInfos.size() - 1).getParameters();
               if (m.group("paramName") != null) {
                  String paramVal = QuotedCSV.unquote(m.group("paramValue"));
                  authParams.put(m.group("paramName"), paramVal);
               } else if (m.group("token68") != null) {
                  if (!authParams.isEmpty()) {
                     throw new IllegalArgumentException("token68 after auth-params");
                  }

                  authParams.put("base64", m.group("token68"));
               }
            }
         }
      }

      return headerInfos;
   }

   private class AfterAuthenticationListener implements Response.Listener {
      private final Authentication.Result authenticationResult;

      private AfterAuthenticationListener(Authentication.Result authenticationResult) {
         this.authenticationResult = authenticationResult;
      }

      @Override
      public void onSuccess(Response response) {
         int status = response.getStatus();
         if (HttpStatus.isSuccess(status) || HttpStatus.isRedirection(status)) {
            AuthenticationProtocolHandler.this.client.getAuthenticationStore().addAuthenticationResult(this.authenticationResult);
         }
      }
   }

   private class AuthenticationListener extends BufferingResponseListener {
      private AuthenticationListener() {
         super(AuthenticationProtocolHandler.this.maxContentLength);
      }

      @Override
      public void onComplete(Result result) {
         HttpRequest request = (HttpRequest)result.getRequest();
         ContentResponse response = new HttpContentResponse(result.getResponse(), this.getContent(), this.getMediaType(), this.getEncoding());
         if (result.getResponseFailure() != null) {
            if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
               AuthenticationProtocolHandler.LOG.debug("Authentication challenge failed", result.getFailure());
            }

            this.forwardFailureComplete(request, result.getRequestFailure(), response, result.getResponseFailure());
         } else {
            String authenticationAttribute = AuthenticationProtocolHandler.this.getAuthenticationAttribute();
            HttpConversation conversation = request.getConversation();
            if (conversation.getAttribute(authenticationAttribute) != null) {
               if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                  AuthenticationProtocolHandler.LOG.debug("Bad credentials for {}", request);
               }

               this.forwardSuccessComplete(request, response);
            } else {
               HttpHeader header = AuthenticationProtocolHandler.this.getAuthenticateHeader();
               List<Authentication.HeaderInfo> headerInfos = this.parseAuthenticateHeader(response, header);
               if (headerInfos.isEmpty()) {
                  if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                     AuthenticationProtocolHandler.LOG.debug("Authentication challenge without {} header", header);
                  }

                  this.forwardFailureComplete(
                     request,
                     result.getRequestFailure(),
                     response,
                     new HttpResponseException("HTTP protocol violation: Authentication challenge without " + header + " header", response)
                  );
               } else {
                  Authentication authentication = null;
                  Authentication.HeaderInfo headerInfo = null;
                  URI authURI = this.resolveURI(request, AuthenticationProtocolHandler.this.getAuthenticationURI(request));
                  if (authURI != null) {
                     for (Authentication.HeaderInfo element : headerInfos) {
                        authentication = AuthenticationProtocolHandler.this.client
                           .getAuthenticationStore()
                           .findAuthentication(element.getType(), authURI, element.getRealm());
                        if (authentication != null) {
                           headerInfo = element;
                           break;
                        }
                     }
                  }

                  if (authentication == null) {
                     if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                        AuthenticationProtocolHandler.LOG.debug("No authentication available for {}", request);
                     }

                     this.forwardSuccessComplete(request, response);
                  } else {
                     Request.Content content = request.getBody();
                     if (content != null && !content.rewind()) {
                        if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                           AuthenticationProtocolHandler.LOG.debug("Request content not reproducible for {}", request);
                        }

                        this.forwardSuccessComplete(request, response);
                     } else {
                        try {
                           Authentication.Result authnResult = authentication.authenticate(request, response, headerInfo, conversation);
                           if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                              AuthenticationProtocolHandler.LOG.debug("Authentication result {}", authnResult);
                           }

                           if (authnResult == null) {
                              this.forwardSuccessComplete(request, response);
                              return;
                           }

                           conversation.setAttribute(authenticationAttribute, true);
                           Request newRequest = AuthenticationProtocolHandler.this.client.copyRequest(request, request.getURI());
                           if (HttpMethod.CONNECT.is(newRequest.getMethod())) {
                              newRequest.path(request.getPath());
                           }

                           long timeoutNanoTime = request.getTimeoutNanoTime();
                           if (timeoutNanoTime < Long.MAX_VALUE) {
                              long newTimeout = NanoTime.until(timeoutNanoTime);
                              if (newTimeout <= 0L) {
                                 TimeoutException failure = new TimeoutException("Total timeout " + request.getConversation().getTimeout() + " ms elapsed");
                                 this.forwardFailureComplete(request, failure, response, failure);
                                 return;
                              }

                              newRequest.timeout(newTimeout, TimeUnit.NANOSECONDS);
                           }

                           authnResult.apply(newRequest);
                           this.copyIfAbsent(request, newRequest, HttpHeader.AUTHORIZATION);
                           this.copyIfAbsent(request, newRequest, HttpHeader.PROXY_AUTHORIZATION);
                           AuthenticationProtocolHandler.AfterAuthenticationListener listener = AuthenticationProtocolHandler.this.new AfterAuthenticationListener(
                              authnResult
                           );
                           Connection connection = (Connection)request.getAttributes().get(Connection.class.getName());
                           if (connection != null) {
                              connection.send(newRequest, listener);
                           } else {
                              newRequest.send(listener);
                           }
                        } catch (Throwable var19) {
                           if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                              AuthenticationProtocolHandler.LOG.debug("Authentication failed", var19);
                           }

                           this.forwardFailureComplete(request, null, response, var19);
                        }
                     }
                  }
               }
            }
         }
      }

      private URI resolveURI(HttpRequest request, URI uri) {
         if (uri != null) {
            return uri;
         } else {
            String target = request.getScheme() + "://" + request.getHost();
            int port = request.getPort();
            if (port > 0) {
               target = target + ":" + port;
            }

            return URI.create(target);
         }
      }

      private void copyIfAbsent(HttpRequest oldRequest, Request newRequest, HttpHeader header) {
         HttpField field = oldRequest.getHeaders().getField(header);
         if (field != null && !newRequest.getHeaders().contains(header)) {
            newRequest.headers(headers -> headers.put(field));
         }
      }

      private void forwardSuccessComplete(HttpRequest request, Response response) {
         HttpConversation conversation = request.getConversation();
         conversation.updateResponseListeners(null);
         ResponseListeners responseListeners = conversation.getResponseListeners();
         responseListeners.emitSuccessComplete(new Result(request, response));
      }

      private void forwardFailureComplete(HttpRequest request, Throwable requestFailure, Response response, Throwable responseFailure) {
         HttpConversation conversation = request.getConversation();
         conversation.updateResponseListeners(null);
         ResponseListeners responseListeners = conversation.getResponseListeners();
         if (responseFailure == null) {
            responseListeners.emitSuccess(response);
         } else {
            responseListeners.emitFailure(response, responseFailure);
         }

         responseListeners.notifyComplete(new Result(request, requestFailure, response, responseFailure));
      }

      private List<Authentication.HeaderInfo> parseAuthenticateHeader(Response response, HttpHeader header) {
         List<Authentication.HeaderInfo> result = new ArrayList<>();

         for (String value : response.getHeaders().getValuesList(header)) {
            try {
               result.addAll(AuthenticationProtocolHandler.this.getHeaderInfo(value));
            } catch (IllegalArgumentException var8) {
               if (AuthenticationProtocolHandler.LOG.isDebugEnabled()) {
                  AuthenticationProtocolHandler.LOG.debug("Failed to parse authentication header", var8);
               }
            }
         }

         return result;
      }
   }
}
