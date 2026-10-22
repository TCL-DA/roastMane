package org.eclipse.jetty.util.resource;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathResourceFactory implements ResourceFactory {
   @Override
   public Resource newResource(URI uri) {
      Path path = Paths.get(uri.normalize());
      return !Files.exists(path) ? null : new PathResource(path, uri, false);
   }
}
