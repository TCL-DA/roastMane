package org.eclipse.jetty.util.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class GraalIssue5720PathResource extends PathResource {
   private static final Logger LOG = LoggerFactory.getLogger(GraalIssue5720PathResource.class);
   private static final String URI_BAD_RESOURCE_PREFIX = "file:///resources!";

   GraalIssue5720PathResource(Path path, URI uri, boolean bypassAllowedSchemeCheck) {
      super(path, correctResourceURI(uri), bypassAllowedSchemeCheck || isResourceScheme(uri));
   }

   private static final boolean isResourceScheme(URI uri) {
      return "resource".equals(uri.getScheme());
   }

   static boolean isAffectedURL(URL url) {
      if (url != null && "resource".equals(url.getProtocol())) {
         URI uri;
         try {
            uri = url.toURI();
         } catch (URISyntaxException var7) {
            return false;
         }

         try {
            try {
               uri = Path.of(uri).toUri();
            } catch (FileSystemNotFoundException var5) {
               try {
                  FileSystems.newFileSystem(uri, Collections.emptyMap());
               } catch (FileSystemAlreadyExistsException var4) {
                  LOG.debug("Race condition upon calling FileSystems.newFileSystem for: {}", uri, var5);
               }

               uri = Path.of(uri).toUri();
            }
         } catch (RuntimeException | IOException var6) {
            LOG.warn("Could not check URL: {}", url, var6);
         }

         return uri.getSchemeSpecificPart().startsWith("file:///resources!");
      } else {
         return false;
      }
   }

   static URI correctResourceURI(URI uri) {
      if (uri != null && isResourceScheme(uri)) {
         String ssp = uri.getSchemeSpecificPart();
         return ssp.startsWith("file:///resources!") ? URI.create("resource:" + ssp.substring("file:///resources!".length())) : uri;
      } else {
         return uri;
      }
   }

   @Override
   public URI getRealURI() {
      Path realPath = this.getRealPath();
      return realPath == null ? null : correctResourceURI(realPath.toUri());
   }

   @Override
   protected URI toUri(Path path) {
      URI pathUri = correctResourceURI(path.toUri());
      String rawUri = pathUri.toASCIIString();
      return Files.isDirectory(path) && !rawUri.endsWith("/") ? URI.create(rawUri + "/") : pathUri;
   }
}
