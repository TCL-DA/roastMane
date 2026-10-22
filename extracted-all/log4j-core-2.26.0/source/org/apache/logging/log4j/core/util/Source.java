package org.apache.logging.log4j.core.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.internal.annotation.SuppressFBWarnings;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class Source {
   private static final Logger LOGGER = StatusLogger.getLogger();
   @Nullable
   private final File file;
   private final URI uri;
   private final String location;

   private static String normalize(final File file) {
      try {
         return file.getCanonicalFile().getAbsolutePath();
      } catch (IOException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   @Nullable
   private static File toFile(Path path) {
      try {
         return Objects.requireNonNull(path, "path").toFile();
      } catch (UnsupportedOperationException var2) {
         return null;
      }
   }

   @SuppressFBWarnings(
      value = {"PATH_TRAVERSAL_IN"},
      justification = "The URI should be specified in a configuration file."
   )
   @Nullable
   private static File toFile(URI uri) {
      try {
         String scheme = Objects.requireNonNull(uri, "uri").getScheme();
         if (!Strings.isBlank(scheme) && !scheme.equals("file")) {
            LOGGER.debug("uri does not represent a local file: " + uri);
            return null;
         } else {
            return new File(uri.getPath());
         }
      } catch (Exception var2) {
         LOGGER.debug("uri is malformed: " + uri);
         return null;
      }
   }

   private static URI toURI(final URL url) {
      try {
         return Objects.requireNonNull(url, "url").toURI();
      } catch (URISyntaxException var2) {
         throw new IllegalArgumentException(var2);
      }
   }

   public Source(final ConfigurationSource source) {
      this.file = source.getFile();
      this.uri = Objects.requireNonNull(source.getURI());
      this.location = Objects.requireNonNull(source.getLocation());
   }

   public Source(final File file) {
      this.file = Objects.requireNonNull(file, "file");
      this.location = normalize(file);
      this.uri = file.toURI();
   }

   public Source(final Path path) {
      Path normPath = Objects.requireNonNull(path, "path").normalize();
      this.file = toFile(normPath);
      this.uri = normPath.toUri();
      this.location = normPath.toString();
   }

   public Source(final URI uri) {
      URI normUri = Objects.requireNonNull(uri, "uri").normalize();
      this.uri = normUri;
      this.location = normUri.toString();
      this.file = toFile(normUri);
   }

   @Deprecated
   public Source(URI uri, long ignored) {
      this(uri);
   }

   public Source(final URL url) {
      this.uri = toURI(url);
      this.location = this.uri.toString();
      this.file = toFile(this.uri);
   }

   @Override
   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof Source)) {
         return false;
      } else {
         Source other = (Source)obj;
         return Objects.equals(this.location, other.location);
      }
   }

   @Nullable
   public File getFile() {
      return this.file;
   }

   public String getLocation() {
      return this.location;
   }

   @SuppressFBWarnings(
      value = {"PATH_TRAVERSAL_IN"},
      justification = "The `file`, `uri` and `location` fields come from Log4j properties."
   )
   public Path getPath() {
      return this.file != null ? this.file.toPath() : Paths.get(this.uri);
   }

   public URI getURI() {
      return this.uri;
   }

   public URL getURL() {
      try {
         return this.uri.toURL();
      } catch (MalformedURLException var2) {
         throw new IllegalStateException(var2);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.location);
   }

   @Override
   public String toString() {
      return this.location;
   }
}
