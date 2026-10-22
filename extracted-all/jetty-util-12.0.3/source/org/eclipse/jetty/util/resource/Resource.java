package org.eclipse.jetty.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.URIUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Resource implements Iterable<Resource> {
   private static final Logger LOG = LoggerFactory.getLogger(Resource.class);
   private static final LinkOption[] NO_FOLLOW_LINKS = new LinkOption[]{LinkOption.NOFOLLOW_LINKS};

   public static String dump(Resource resource) {
      return resource == null
         ? "null exists=false directory=false lm=-1"
         : "%s exists=%b directory=%b lm=%s".formatted(resource.toString(), resource.exists(), resource.isDirectory(), resource.lastModified());
   }

   public abstract Path getPath();

   public boolean isContainedIn(Resource container) {
      return container != null && container.contains(this);
   }

   public boolean contains(Resource other) {
      if (other == null) {
         return false;
      } else {
         URI thisURI = this.getURI();
         if (thisURI == null) {
            throw new UnsupportedOperationException("Resources without a URI must implement contains");
         } else {
            URI otherURI = other.getURI();
            if (otherURI == null) {
               return false;
            } else if (!StringUtil.asciiEqualsIgnoreCase(thisURI.getScheme(), otherURI.getScheme())) {
               return false;
            } else if (!Objects.equals(thisURI.getAuthority(), otherURI.getAuthority())) {
               return false;
            } else {
               String thisURIString = URIUtil.correctFileURI(thisURI).toASCIIString();
               String otherURIString = URIUtil.correctFileURI(otherURI).toASCIIString();
               return otherURIString.startsWith(thisURIString)
                  && (thisURIString.length() == otherURIString.length() || otherURIString.charAt(thisURIString.length()) == '/');
            }
         }
      }
   }

   public Path getPathTo(Resource other) {
      Path thisPath = this.getPath();
      if (thisPath == null) {
         throw new UnsupportedOperationException("Resources without a Path must implement getPathTo");
      } else if (!this.contains(other)) {
         return null;
      } else {
         Path otherPath = other.getPath();
         return otherPath == null ? null : thisPath.relativize(otherPath);
      }
   }

   @Override
   public Iterator<Resource> iterator() {
      return List.of(this).iterator();
   }

   public boolean exists() {
      return Files.exists(this.getPath(), NO_FOLLOW_LINKS);
   }

   public abstract boolean isDirectory();

   public abstract boolean isReadable();

   public Instant lastModified() {
      return Instant.EPOCH;
   }

   public long length() {
      return -1L;
   }

   public abstract URI getURI();

   public abstract String getName();

   public abstract String getFileName();

   public InputStream newInputStream() throws IOException {
      Path path = this.getPath();
      return path == null ? null : Files.newInputStream(path, StandardOpenOption.READ);
   }

   public ReadableByteChannel newReadableByteChannel() throws IOException {
      Path path = this.getPath();
      return path == null ? null : Files.newByteChannel(this.getPath(), StandardOpenOption.READ);
   }

   public List<Resource> list() {
      return List.of();
   }

   public abstract Resource resolve(String var1);

   public boolean isAlias() {
      return false;
   }

   public URI getRealURI() {
      return this.getURI();
   }

   public void copyTo(Path destination) throws IOException {
      Path src = this.getPath();
      if (src == null) {
         if (!this.isDirectory()) {
            try (
               InputStream in = this.newInputStream();
               OutputStream out = Files.newOutputStream(destination);
            ) {
               IO.copy(in, out);
            }
         } else {
            throw new UnsupportedOperationException("Directory Resources without a Path must implement copyTo");
         }
      } else if (Files.isRegularFile(src)) {
         if (Files.isDirectory(destination)) {
            Path destPath = destination.resolve(src.getFileName().toString());
            Files.copy(src, destPath, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
         } else {
            Files.copy(src, destination, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
         }
      } else {
         assert this.isDirectory();

         BiFunction<Path, Path, Path> resolver = src.getFileSystem().equals(destination.getFileSystem()) ? Path::resolve : Resource::resolveDifferentFileSystem;

         try (Stream<Path> entriesStream = Files.walk(src)) {
            Iterator<Path> pathIterator = entriesStream.iterator();

            while (pathIterator.hasNext()) {
               Path path = pathIterator.next();
               if (!src.equals(path)) {
                  Path relative = src.relativize(path);
                  Path destPath = resolver.apply(destination, relative);
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("CopyTo: {} > {}", path, destPath);
                  }

                  if (Files.isDirectory(path)) {
                     this.ensureDirExists(destPath);
                  } else {
                     this.ensureDirExists(destPath.getParent());
                     Files.copy(path, destPath, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
                  }
               }
            }
         }
      }
   }

   static Path resolveDifferentFileSystem(Path path, Path relative) {
      for (Path segment : relative) {
         path = path.resolve(segment.toString());
      }

      return path;
   }

   void ensureDirExists(Path dir) throws IOException {
      if (Files.exists(dir)) {
         if (!Files.isDirectory(dir)) {
            throw new IOException("Conflict, unable to create directory where file exists: " + dir);
         }
      } else {
         Files.createDirectories(dir);
      }
   }

   public Collection<Resource> getAllResources() {
      try {
         List<Resource> children = this.list();
         if (children != null && !children.isEmpty()) {
            boolean noDepth = true;
            Iterator<Resource> i = children.iterator();

            while (noDepth && i.hasNext()) {
               noDepth = !i.next().isDirectory();
            }

            if (noDepth) {
               return children;
            } else {
               ArrayList<Resource> deep = new ArrayList<>();

               for (Resource r : children) {
                  deep.add(r);
                  if (r.isDirectory()) {
                     deep.addAll(r.getAllResources());
                  }
               }

               return deep;
            }
         } else {
            return List.of();
         }
      } catch (Exception var6) {
         throw new IllegalStateException(var6);
      }
   }
}
