package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.PreEncodedHttpField;
import org.eclipse.jetty.http.HttpURI.Mutable;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.URIUtil;

public class MovedContextHandler extends ContextHandler {
   private int _statusCode = 303;
   private String _redirectURI;
   private boolean _discardPathInContext = true;
   private boolean _discardQuery = true;
   private HttpField _cacheControl;

   public MovedContextHandler() {
      this.setHandler(new MovedContextHandler.Redirector());
      this.setAllowNullPathInContext(true);
   }

   public int getStatusCode() {
      return this._statusCode;
   }

   public void setStatusCode(int statusCode) {
      if (!HttpStatus.isRedirection(statusCode)) {
         throw new IllegalArgumentException("Invalid HTTP redirection status code: " + statusCode);
      } else {
         this._statusCode = statusCode;
      }
   }

   public String getRedirectURI() {
      return this._redirectURI;
   }

   public void setRedirectURI(String redirectURI) {
      this._redirectURI = redirectURI;
   }

   public boolean isDiscardPathInContext() {
      return this._discardPathInContext;
   }

   public void setDiscardPathInContext(boolean discardPathInContext) {
      this._discardPathInContext = discardPathInContext;
   }

   public boolean isDiscardQuery() {
      return this._discardQuery;
   }

   public void setDiscardQuery(boolean discardQuery) {
      this._discardQuery = discardQuery;
   }

   public String getCacheControl() {
      return this._cacheControl == null ? null : this._cacheControl.getValue();
   }

   public void setCacheControl(String cacheControl) {
      this._cacheControl = cacheControl == null ? null : new PreEncodedHttpField(HttpHeader.CACHE_CONTROL, cacheControl);
   }

   private class Redirector extends Handler.Abstract {
      @Override
      public boolean handle(Request request, Response response, Callback callback) throws Exception {
         String redirectURI = MovedContextHandler.this.getRedirectURI();
         if (redirectURI == null) {
            redirectURI = "/";
         }

         Mutable redirectHttpURI = HttpURI.build(redirectURI);
         if (redirectHttpURI.getScheme() == null) {
            HttpURI httpURI = request.getHttpURI();
            redirectHttpURI = redirectHttpURI.scheme(httpURI.getScheme()).authority(httpURI.getAuthority());
         }

         if (!MovedContextHandler.this.isDiscardPathInContext()) {
            String pathInContext = Request.getPathInContext(request);
            String newPath = redirectHttpURI.getPath();
            redirectHttpURI.path(URIUtil.addPaths(newPath, pathInContext));
         }

         if (!MovedContextHandler.this.isDiscardQuery()) {
            String query = request.getHttpURI().getQuery();
            String newQuery = redirectHttpURI.getQuery();
            redirectHttpURI.query(URIUtil.addQueries(query, newQuery));
         }

         response.setStatus(MovedContextHandler.this.getStatusCode());
         response.getHeaders().put(HttpHeader.LOCATION, redirectHttpURI.asString());
         HttpField cacheControl = MovedContextHandler.this._cacheControl;
         if (cacheControl != null) {
            response.getHeaders().put(cacheControl);
         }

         callback.succeeded();
         return true;
      }
   }
}
