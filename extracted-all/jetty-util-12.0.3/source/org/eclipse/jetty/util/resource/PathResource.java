package org.eclipse.jetty.util.resource;

import java.io.IOException;
import java.net.URI;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PathResource extends Resource {
   private static final Logger LOG = LoggerFactory.getLogger(PathResource.class);
   public static Index<String> SUPPORTED_SCHEMES = new Index.Builder<String>().caseSensitive(false).with("file").with("jrt").build();
   private final Path path;
   private final URI uri;
   private Boolean alias;
   private Path realPath;

   public static boolean isSameName(Path pathA, Path pathB) {
      int aCount = pathA.getNameCount();
      int bCount = pathB.getNameCount();
      if (aCount != bCount) {
         return false;
      } else {
         int i = bCount;

         while (i-- > 0) {
            if (!pathA.getName(i).toString().equals(pathB.getName(i).toString())) {
               return false;
            }
         }

         return true;
      }
   }

   PathResource(URI uri) {
      this(uri, false);
   }

   PathResource(URI uri, boolean bypassAllowedSchemeCheck) {
      this(Paths.get(uri), uri, bypassAllowedSchemeCheck);
   }

   PathResource(Path path) {
      this(path, path.toUri(), true);
   }

   PathResource(Path path, URI uri, boolean bypassAllowedSchemeCheck) {
      if (!uri.isAbsolute()) {
         throw new IllegalArgumentException("not an absolute uri: " + uri);
      } else if (!bypassAllowedSchemeCheck && !SUPPORTED_SCHEMES.contains(uri.getScheme())) {
         throw new IllegalArgumentException("not an allowed scheme: " + uri);
      } else {
         if (Files.isDirectory(path)) {
            String uriString = uri.toASCIIString();
            if (!uriString.endsWith("/")) {
               uri = URIUtil.correctFileURI(URI.create(uriString + "/"));
            }
         }

         this.path = path;
         this.uri = uri;
      }
   }

   @Override
   public boolean exists() {
      if (this.alias == null) {
         return Files.exists(this.path);
      } else {
         return this.realPath == null ? false : Files.exists(this.realPath);
      }
   }

   @Override
   public Path getPath() {
      return this.path;
   }

   @Override
   public boolean contains(Resource other) {
      if (other == null) {
         return false;
      } else {
         Path thisPath = this.getPath();
         if (thisPath == null) {
            throw new UnsupportedOperationException("Resources without a Path must implement contains");
         } else {
            Path otherPath = other.getPath();
            return otherPath != null && otherPath.getFileSystem().equals(thisPath.getFileSystem()) && otherPath.startsWith(thisPath);
         }
      }
   }

   public Path getRealPath() {
      this.resolveAlias();
      return this.realPath;
   }

   @Override
   public URI getRealURI() {
      Path realPath = this.getRealPath();
      return realPath == null ? null : realPath.toUri();
   }

   @Override
   public List<Resource> list() {
      if (!this.isDirectory()) {
         return List.of();
      } else {
         try {
            List var2;
            try (Stream<Path> dirStream = Files.list(this.getPath())) {
               var2 = dirStream.<PathResource>map(PathResource::new).collect(Collectors.toCollection(ArrayList::new));
            }

            return var2;
         } catch (DirectoryIteratorException var6) {
            LOG.debug("Directory list failure", var6);
         } catch (IOException var7) {
            LOG.debug("Directory list access failure", var7);
         }

         return List.of();
      }
   }

   @Override
   public boolean isAlias() {
      this.resolveAlias();
      return this.alias != null && this.alias;
   }

   @Override
   public String getName() {
      return this.path.toAbsolutePath().toString();
   }

   @Override
   public String getFileName() {
      Path fn = this.path.getFileName();
      return fn == null ? "" : fn.toString();
   }

   @Override
   public URI getURI() {
      return this.uri;
   }

   @Override
   public Resource resolve(String subUriPath) {
      if (URIUtil.isNotNormalWithinSelf(subUriPath)) {
         throw new IllegalArgumentException(subUriPath);
      } else if ("/".equals(subUriPath)) {
         return this;
      } else {
         URI uri = this.getURI();
         URI resolvedUri = URIUtil.addPath(uri, subUriPath);
         Path path = Paths.get(resolvedUri);
         return Files.exists(path) ? this.newResource(path, resolvedUri) : null;
      }
   }

   protected Resource newResource(Path path, URI uri) {
      return new PathResource(path, uri, true);
   }

   @Override
   public boolean isDirectory() {
      return Files.isDirectory(this.getPath(), LinkOption.NOFOLLOW_LINKS);
   }

   @Override
   public boolean isReadable() {
      return Files.isReadable(this.getPath());
   }

   @Override
   public Instant lastModified() {
      Path path = this.getPath();
      if (path == null) {
         return Instant.EPOCH;
      } else if (!Files.exists(path)) {
         return Instant.EPOCH;
      } else {
         try {
            FileTime ft = Files.getLastModifiedTime(path, LinkOption.NOFOLLOW_LINKS);
            return ft.toInstant();
         } catch (IOException var3) {
            LOG.trace("IGNORED", var3);
            return Instant.EPOCH;
         }
      }
   }

   @Override
   public long length() {
      try {
         return Files.size(this.getPath());
      } catch (IOException var2) {
         return 0L;
      }
   }

   private void resolveAlias() {
      if (this.alias == null) {
         try {
            this.realPath = this.path.toRealPath();
         } catch (Exception var2) {
            if (var2 instanceof IOException) {
               LOG.trace("IGNORED", var2);
            } else {
               LOG.warn("bad alias ({} {}) for {}", new Object[]{var2.getClass().getName(), var2.getMessage(), this.path});
            }

            return;
         }

         this.alias = !isSameName(this.path, this.realPath) || !Objects.equals(this.uri, this.toUri(this.realPath));
      }
   }

   protected URI toUri(Path path) {
      URI pathUri = path.toUri();
      String rawUri = pathUri.toASCIIString();
      return Files.isDirectory(path) && !rawUri.endsWith("/") ? URI.create(rawUri + "/") : pathUri;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         PathResource other = (PathResource)obj;
         return Objects.equals(this.path, other.path) && Objects.equals(this.uri, other.uri);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.path, this.uri);
   }

   @Override
   public String toString() {
      return this.uri.toASCIIString();
   }
}
