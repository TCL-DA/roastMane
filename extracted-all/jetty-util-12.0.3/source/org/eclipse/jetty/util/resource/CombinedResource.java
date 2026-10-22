package org.eclipse.jetty.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.URIUtil;

public class CombinedResource extends Resource {
   private final List<Resource> _resources;

   static Resource combine(List<Resource> resources) {
      resources = gatherUniqueFlatResourceList(resources);
      if (resources != null && !resources.isEmpty()) {
         return (Resource)(resources.size() == 1 ? resources.get(0) : new CombinedResource(resources));
      } else {
         return null;
      }
   }

   static List<Resource> gatherUniqueFlatResourceList(List<Resource> resources) {
      if (resources != null && !resources.isEmpty()) {
         List<Resource> unique = new ArrayList<>(resources.size());

         for (Resource r : resources) {
            if (r == null) {
               throw new IllegalArgumentException("Null Resource entry encountered");
            }

            if (r instanceof CombinedResource resourceCollection) {
               unique.addAll(gatherUniqueFlatResourceList(resourceCollection.getResources()));
            } else if (!unique.contains(r)) {
               if (!r.exists()) {
                  throw new IllegalArgumentException("Does not exist: " + r);
               }

               if (!r.isDirectory()) {
                  throw new IllegalArgumentException("Non-Directory not allowed: " + r);
               }

               unique.add(r);
            }
         }

         return unique;
      } else {
         return null;
      }
   }

   CombinedResource(List<Resource> resources) {
      this._resources = Collections.unmodifiableList(resources);
   }

   public List<Resource> getResources() {
      return this._resources;
   }

   @Override
   public Resource resolve(String subUriPath) {
      if (URIUtil.isNotNormalWithinSelf(subUriPath)) {
         throw new IllegalArgumentException(subUriPath);
      } else if (subUriPath.length() != 0 && !"/".equals(subUriPath)) {
         ArrayList<Resource> resources = null;
         Resource resolved = null;

         for (Resource res : this._resources) {
            resolved = res.resolve(subUriPath);
            if (!Resources.missing(resolved)) {
               if (!resolved.isDirectory()) {
                  return resolved;
               }

               if (resources == null) {
                  resources = new ArrayList<>();
               }

               resources.add(resolved);
            }
         }

         if (resources == null) {
            return resolved;
         } else {
            return (Resource)(resources.size() == 1 ? resources.get(0) : new CombinedResource(resources));
         }
      } else {
         return this;
      }
   }

   @Override
   public boolean exists() {
      return this._resources.stream().anyMatch(Resource::exists);
   }

   @Override
   public Path getPath() {
      return null;
   }

   @Override
   public String getName() {
      return null;
   }

   @Override
   public String getFileName() {
      String filename = null;

      for (Resource r : this._resources) {
         String fn = r.getFileName();
         if (fn == null) {
            return null;
         }

         if (filename == null) {
            filename = fn;
         } else if (!filename.equals(fn)) {
            return null;
         }
      }

      return filename;
   }

   @Override
   public URI getURI() {
      return null;
   }

   @Override
   public boolean isDirectory() {
      return true;
   }

   @Override
   public boolean isReadable() {
      for (Resource r : this._resources) {
         if (r.isReadable()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Instant lastModified() {
      Instant instant = null;

      for (Resource r : this._resources) {
         Instant lm = r.lastModified();
         if (instant == null || lm.isAfter(instant)) {
            instant = lm;
         }
      }

      return instant;
   }

   @Override
   public long length() {
      return -1L;
   }

   @Override
   public Iterator<Resource> iterator() {
      return this._resources.iterator();
   }

   @Override
   public List<Resource> list() {
      Map<String, Resource> results = new TreeMap<>();

      for (Resource base : this._resources) {
         for (Resource r : base.list()) {
            if (r.isDirectory()) {
               results.computeIfAbsent(r.getFileName(), this::resolve);
            } else {
               results.putIfAbsent(r.getFileName(), r);
            }
         }
      }

      return new ArrayList<>(results.values());
   }

   @Override
   public void copyTo(Path destination) throws IOException {
      for (Resource r : this.getAllResources()) {
         Path relative = this.getPathTo(r);
         Path pathTo = Objects.equals(relative.getFileSystem(), destination.getFileSystem())
            ? destination.resolve(relative)
            : resolveDifferentFileSystem(destination, relative);
         if (r.isDirectory()) {
            this.ensureDirExists(pathTo);
         } else {
            this.ensureDirExists(pathTo.getParent());
            Path pathFrom = r.getPath();
            if (pathFrom != null) {
               Files.copy(pathFrom, pathTo, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
            } else {
               try (
                  InputStream in = r.newInputStream();
                  OutputStream out = Files.newOutputStream(pathTo);
               ) {
                  IO.copy(in, out);
               }
            }
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         CombinedResource other = (CombinedResource)o;
         return Objects.equals(this._resources, other._resources);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this._resources);
   }

   @Override
   public String toString() {
      return this._resources.stream().map(Object::toString).collect(Collectors.joining(", ", "[", "]"));
   }

   @Override
   public boolean contains(Resource other) {
      label21:
      for (Resource o : other) {
         for (Resource r : this._resources) {
            if (r.contains(o)) {
               continue label21;
            }
         }

         return false;
      }

      return true;
   }

   @Override
   public Path getPathTo(Resource other) {
      Path otherPath = other.getPath();
      if (otherPath != null) {
         for (Resource r : this._resources) {
            Path path = r.getPath();
            if (otherPath.startsWith(path)) {
               return path.relativize(otherPath);
            }
         }

         return null;
      } else {
         Path relative = null;

         label41:
         for (Resource o : other) {
            for (Resource rx : this._resources) {
               if (o.getPath().startsWith(rx.getPath())) {
                  Path rel = rx.getPath().relativize(o.getPath());
                  if (relative == null) {
                     relative = rel;
                  } else if (!relative.equals(rel)) {
                     return null;
                  }
                  continue label41;
               }
            }

            return null;
         }

         return relative;
      }
   }
}
