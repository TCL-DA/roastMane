package org.eclipse.jetty.http.content;

import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Objects;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.eclipse.jetty.util.resource.Resources;

public class ResourceHttpContentFactory implements HttpContent.Factory {
   private final ResourceFactory _factory;
   private final MimeTypes _mimeTypes;

   public ResourceHttpContentFactory(ResourceFactory factory, MimeTypes mimeTypes) {
      Objects.requireNonNull(mimeTypes, "MimeTypes cannot be null");
      this._factory = factory;
      this._mimeTypes = mimeTypes;
   }

   @Override
   public HttpContent getContent(String pathInContext) throws IOException {
      try {
         Resource resource = this._factory.newResource(pathInContext);
         return Resources.missing(resource) ? null : this.load(pathInContext, resource);
      } catch (Throwable var4) {
         InvalidPathException saferException = new InvalidPathException(pathInContext, "Invalid PathInContext");
         saferException.initCause(var4);
         throw saferException;
      }
   }

   private HttpContent load(String pathInContext, Resource resource) {
      return resource != null && resource.exists() ? new ResourceHttpContent(resource, this._mimeTypes.getMimeByExtension(pathInContext)) : null;
   }

   @Override
   public String toString() {
      return "ResourceContentFactory[" + this._factory + "]@" + this.hashCode();
   }
}
