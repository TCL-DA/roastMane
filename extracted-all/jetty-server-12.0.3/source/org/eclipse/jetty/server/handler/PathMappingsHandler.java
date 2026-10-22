package org.eclipse.jetty.server.handler;

import java.io.IOException;
import java.util.List;
import org.eclipse.jetty.http.pathmap.MappedResource;
import org.eclipse.jetty.http.pathmap.MatchedResource;
import org.eclipse.jetty.http.pathmap.PathMappings;
import org.eclipse.jetty.http.pathmap.PathSpec;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.component.Dumpable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathMappingsHandler extends Handler.AbstractContainer {
   private static final Logger LOG = LoggerFactory.getLogger(PathMappingsHandler.class);
   private final PathMappings<Handler> mappings = new PathMappings();

   public PathMappingsHandler() {
      this(true);
   }

   public PathMappingsHandler(boolean dynamic) {
      super(dynamic);
   }

   @Override
   public List<Handler> getHandlers() {
      return this.mappings.streamResources().<Handler>map(MappedResource::getResource).toList();
   }

   public void addMapping(PathSpec pathSpec, Handler handler) {
      if (this.isStarted()) {
         throw new IllegalStateException("Cannot add mapping: " + this);
      } else if (handler == this) {
         throw new IllegalStateException("Unable to addHandler of self: " + handler);
      } else {
         if (handler instanceof Handler.Container container && container.getDescendants().contains(this)) {
            throw new IllegalStateException("loop detected: " + handler);
         }

         Handler old = (Handler)this.mappings.get(pathSpec);
         this.mappings.put(pathSpec, handler);
         this.updateBean(old, handler);
      }
   }

   public void dump(Appendable out, String indent) throws IOException {
      Dumpable.dumpObjects(out, indent, this, new Object[]{this.mappings});
   }

   @Override
   public boolean handle(Request request, Response response, Callback callback) throws Exception {
      String pathInContext = Request.getPathInContext(request);
      MatchedResource<Handler> matchedResource = this.mappings.getMatched(pathInContext);
      if (matchedResource == null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("No mappings matched {}", pathInContext);
         }

         return false;
      } else {
         Handler handler = (Handler)matchedResource.getResource();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Matched {} to {} -> {}", new Object[]{pathInContext, matchedResource.getPathSpec(), handler});
         }

         boolean handled = handler.handle(request, response, callback);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Handled {} {} by {}", new Object[]{handled, pathInContext, handler});
         }

         return handled;
      }
   }
}
