package org.eclipse.jetty.util.resource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import org.eclipse.jetty.util.URIUtil;

public class MountedPathResource extends PathResource {
   private final URI containerUri = URIUtil.unwrapContainer(this.getURI());

   MountedPathResource(URI uri) throws IOException {
      super(uri, true);
   }

   MountedPathResource(Path path, URI uri) {
      super(path, uri, true);
   }

   @Override
   protected Resource newResource(Path path, URI uri) {
      return new MountedPathResource(path, uri);
   }

   @Override
   public boolean isContainedIn(Resource container) {
      return URIUtil.unwrapContainer(container.getURI()).equals(this.containerUri);
   }

   public Path getContainerPath() {
      return this.containerUri == null ? null : Path.of(this.containerUri);
   }

   @Override
   public String getName() {
      Path abs = this.getPath();
      if ("jar".equalsIgnoreCase(abs.toUri().getScheme())) {
         abs = abs.normalize();
      }

      abs = abs.toAbsolutePath();
      return abs.toString();
   }
}
