package org.eclipse.jetty.util;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiReleaseJarFile implements Closeable {
   private static final Logger LOG = LoggerFactory.getLogger(MultiReleaseJarFile.class);
   private final Path jarFile;
   private final Resource jarResource;
   private final ResourceFactory.Closeable resourceFactory;

   public MultiReleaseJarFile(Path jarFile) {
      Objects.requireNonNull(jarFile, "Jar File");
      if (!Files.exists(jarFile)) {
         throw new IllegalArgumentException("File does not exist: " + jarFile);
      } else if (!Files.isRegularFile(jarFile)) {
         throw new IllegalArgumentException("Not a file: " + jarFile);
      } else if (!FileID.isJavaArchive(jarFile)) {
         throw new IllegalArgumentException("Not a Jar: " + jarFile);
      } else if (!Files.isReadable(jarFile)) {
         throw new IllegalArgumentException("Unable to read Jar file: " + jarFile);
      } else {
         this.jarFile = jarFile;
         this.resourceFactory = ResourceFactory.closeable();
         this.jarResource = this.resourceFactory.newJarFileResource(jarFile.toUri());
         if (LOG.isDebugEnabled()) {
            LOG.debug("mounting {}", this.jarResource);
         }
      }
   }

   public Stream<Path> stream() throws IOException {
      Path rootPath = this.jarResource.getPath();
      return Files.walk(rootPath).filter(FileID::isNotMetaInfVersions);
   }

   @Override
   public void close() throws IOException {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Closing {}", this.jarResource);
      }

      this.resourceFactory.close();
   }

   @Override
   public String toString() {
      return this.jarFile.toString();
   }
}
