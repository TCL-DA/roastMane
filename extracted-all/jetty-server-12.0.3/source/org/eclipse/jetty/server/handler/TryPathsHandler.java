package org.eclipse.jetty.server.handler;

import java.util.List;
import org.eclipse.jetty.http.HttpURI;
import org.eclipse.jetty.http.HttpURI.Mutable;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TryPathsHandler extends Handler.Wrapper {
   private static final Logger LOG = LoggerFactory.getLogger(TryPathsHandler.class);
   private String originalPathAttribute;
   private String originalQueryAttribute;
   private List<String> paths;

   public TryPathsHandler() {
      this(null);
   }

   public TryPathsHandler(Handler handler) {
      super(handler);
   }

   public String getOriginalPathAttribute() {
      return this.originalPathAttribute;
   }

   public void setOriginalPathAttribute(String originalPathAttribute) {
      this.originalPathAttribute = originalPathAttribute;
   }

   public String getOriginalQueryAttribute() {
      return this.originalQueryAttribute;
   }

   public void setOriginalQueryAttribute(String originalQueryAttribute) {
      this.originalQueryAttribute = originalQueryAttribute;
   }

   public List<String> getPaths() {
      return this.paths;
   }

   public void setPaths(List<String> paths) {
      this.paths = paths;
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      Handler next = this.getHandler();
      if (next == null) {
         return false;
      } else {
         for (String path : this.paths) {
            String interpolated = this.interpolate(request, path);
            Request tryRequest = this.tryPath(request, interpolated);
            if (LOG.isDebugEnabled()) {
               LOG.debug("rewritten request URI {} -> {}", request.getHttpURI(), tryRequest.getHttpURI());
            }

            boolean handled = super.handle(tryRequest, response, callback);
            if (LOG.isDebugEnabled()) {
               LOG.debug("handled {} {}", handled, tryRequest.getHttpURI());
            }

            if (handled) {
               return true;
            }
         }

         return false;
      }
   }

   private String interpolate(Request request, String value) {
      String path = Request.getPathInContext(request);
      return value.replace("$path", path);
   }

   private Request tryPath(Request wrapped, String newPathQuery) {
      HttpURI originalURI = wrapped.getHttpURI();
      String originalPathAttribute = this.getOriginalPathAttribute();
      if (originalPathAttribute != null && wrapped.getAttribute(originalPathAttribute) == null) {
         wrapped.setAttribute(originalPathAttribute, Request.getPathInContext(wrapped));
      }

      String originalQueryAttribute = this.getOriginalQueryAttribute();
      if (originalQueryAttribute != null && wrapped.getAttribute(originalQueryAttribute) == null) {
         wrapped.setAttribute(originalQueryAttribute, originalURI.getQuery());
      }

      String originalContextPath = Request.getContextPath(wrapped);
      Mutable rewrittenURI = HttpURI.build(originalURI);
      int queryIdx = newPathQuery.indexOf(63);
      if (queryIdx >= 0) {
         String path = newPathQuery.substring(0, queryIdx);
         rewrittenURI.path(URIUtil.addPaths(originalContextPath, path));
         rewrittenURI.query(newPathQuery.substring(queryIdx + 1));
      } else {
         rewrittenURI.path(URIUtil.addPaths(originalContextPath, newPathQuery));
      }

      HttpURI uri = rewrittenURI.asImmutable();
      return Request.serveAs(wrapped, uri);
   }
}
