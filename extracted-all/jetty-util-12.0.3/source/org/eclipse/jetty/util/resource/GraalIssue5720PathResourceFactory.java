package org.eclipse.jetty.util.resource;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

final class GraalIssue5720PathResourceFactory extends PathResourceFactory {
   @Override
   public Resource newResource(URI uri) {
      uri = GraalIssue5720PathResource.correctResourceURI(uri.normalize());
      Path path = Path.of(uri);
      return !Files.exists(path) ? null : new GraalIssue5720PathResource(path, uri, false);
   }
}
