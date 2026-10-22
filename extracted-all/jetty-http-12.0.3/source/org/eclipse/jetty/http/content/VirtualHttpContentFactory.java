package org.eclipse.jetty.http.content;

import java.io.IOException;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VirtualHttpContentFactory implements HttpContent.Factory {
   private static final Logger LOG = LoggerFactory.getLogger(VirtualHttpContentFactory.class);
   private final HttpContent.Factory _factory;
   private final Resource _resource;
   private final String _contentType;
   private final String _matchSuffix;

   public VirtualHttpContentFactory(HttpContent.Factory factory, Resource resource, String contentType) {
      this._factory = factory;
      this._resource = resource;
      this._matchSuffix = "/" + this._resource.getFileName();
      this._contentType = contentType;
      if (LOG.isDebugEnabled()) {
         LOG.debug(
            "resource=({}) {}, resource.getFileName()={}", new Object[]{this._resource.getClass().getName(), this._resource, this._resource.getFileName()}
         );
      }
   }

   public Resource getResource() {
      return this._resource;
   }

   @Override
   public HttpContent getContent(String path) throws IOException {
      HttpContent content = this._factory.getContent(path);
      if (content != null) {
         return content;
      } else {
         return this.matchResource(path) ? new ResourceHttpContent(this._resource, this._contentType) : null;
      }
   }

   protected boolean matchResource(String path) {
      return this._resource != null && path != null && path.endsWith(this._matchSuffix);
   }

   @Override
   public String toString() {
      return String.format(
         "%s@%x(factory=%s, resource=%s, matchSuffix=%s, contentType=%s)",
         this.getClass().getName(),
         this.hashCode(),
         this._factory,
         this._resource,
         this._matchSuffix,
         this._contentType
      );
   }
}
