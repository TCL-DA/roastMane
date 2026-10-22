package org.eclipse.jetty.util.resource;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.ProviderNotFoundException;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.eclipse.jetty.util.thread.AutoLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ManagedObject("Pool of FileSystems used to mount Resources")
public class FileSystemPool implements Dumpable {
   private static final Logger LOG = LoggerFactory.getLogger(FileSystemPool.class);
   public static final FileSystemPool INSTANCE = new FileSystemPool();
   private static final Map<String, String> ENV_MULTIRELEASE_RUNTIME;
   private final Map<URI, FileSystemPool.Bucket> pool = new HashMap<>();
   private final AutoLock poolLock = new AutoLock();
   private FileSystemPool.Listener listener;

   private FileSystemPool() {
   }

   FileSystemPool.Mount mount(URI uri) throws IOException {
      if (!uri.isAbsolute()) {
         throw new IllegalArgumentException("not an absolute uri: " + uri);
      } else if (!uri.getScheme().equalsIgnoreCase("jar")) {
         throw new IllegalArgumentException("not an supported scheme: " + uri);
      } else {
         FileSystem fileSystem = null;

         try {
            FileSystemPool.Mount var6;
            try (AutoLock ignore = this.poolLock.lock()) {
               try {
                  fileSystem = FileSystems.newFileSystem(uri, ENV_MULTIRELEASE_RUNTIME);
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Mounted new FS {}", uri);
                  }
               } catch (FileSystemAlreadyExistsException var8) {
                  fileSystem = Paths.get(uri).getFileSystem();
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Using existing FS {}", uri);
                  }
               } catch (ProviderNotFoundException var9) {
                  throw new IllegalArgumentException("Unable to mount FileSystem from unsupported URI: " + uri, var9);
               }

               URI rootURI = fileSystem.getPath("/").toUri();
               FileSystemPool.Mount mount = new FileSystemPool.Mount(rootURI, new MountedPathResource(uri));
               this.retain(rootURI, fileSystem, mount);
               var6 = mount;
            }

            return var6;
         } catch (Exception var11) {
            IO.close((Closeable)fileSystem);
            throw var11;
         }
      }
   }

   private void unmount(URI fsUri) {
      try {
         try (AutoLock ignore = this.poolLock.lock()) {
            FileSystemPool.Bucket bucket = this.pool.get(fsUri);
            if (bucket != null) {
               int count = bucket.counter.decrementAndGet();
               if (count == 0) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Ref counter reached 0, closing pooled FS {}", bucket);
                  }

                  IO.close((Closeable)bucket.fileSystem);
                  this.pool.remove(fsUri);
                  if (this.listener != null) {
                     this.listener.onClose(fsUri);
                     return;
                  }
               } else {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Decremented ref counter to {} for FS {}", count, bucket);
                  }

                  if (this.listener != null) {
                     this.listener.onDecrement(fsUri);
                     return;
                  }
               }

               return;
            }

            LOG.warn("Unable to release Mount (not in pool): {}", fsUri);
         }
      } catch (FileSystemNotFoundException var7) {
      }
   }

   @ManagedAttribute("The mounted FileSystems")
   public Collection<FileSystemPool.Mount> mounts() {
      List var2;
      try (AutoLock ignore = this.poolLock.lock()) {
         var2 = this.pool.values().stream().map(m -> m.mount).toList();
      }

      return var2;
   }

   @Override
   public void dump(Appendable out, String indent) throws IOException {
      Collection<FileSystemPool.Bucket> values;
      try (AutoLock ignore = this.poolLock.lock()) {
         values = this.pool.values();
      }

      Dumpable.dumpObjects(out, indent, this, new DumpableCollection("buckets", values));
   }

   @ManagedOperation(
      value = "Sweep the pool for deleted mount points",
      impact = "ACTION"
   )
   public void sweep() {
      Set<Entry<URI, FileSystemPool.Bucket>> entries;
      try (AutoLock ignore = this.poolLock.lock()) {
         entries = this.pool.entrySet();
      }

      for (Entry<URI, FileSystemPool.Bucket> entry : entries) {
         URI fsUri = entry.getKey();
         FileSystemPool.Bucket bucket = entry.getValue();
         FileSystem fileSystem = bucket.fileSystem;

         try (AutoLock ignore = this.poolLock.lock()) {
            if (fileSystem.isOpen() && !Files.isReadable(bucket.path)
               || !Files.getLastModifiedTime(bucket.path).equals(bucket.lastModifiedTime)
               || Files.size(bucket.path) != bucket.size) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("File {} backing filesystem {} has been removed or changed, closing it", bucket.path, fileSystem);
               }

               IO.close((Closeable)fileSystem);
               this.pool.remove(fsUri);
            }
         } catch (IOException var14) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Cannot read last access time or size of file {} backing filesystem {}", bucket.path, fileSystem);
            }
         }
      }
   }

   private void retain(URI fsUri, FileSystem fileSystem, FileSystemPool.Mount mount) {
      assert this.poolLock.isHeldByCurrentThread();

      FileSystemPool.Bucket bucket = this.pool.get(fsUri);
      if (bucket == null) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Pooling new FS {}", fileSystem);
         }

         bucket = new FileSystemPool.Bucket(fsUri, fileSystem, mount);
         this.pool.put(fsUri, bucket);
         if (this.listener != null) {
            this.listener.onRetain(fsUri);
         }
      } else {
         int count = bucket.counter.incrementAndGet();
         if (LOG.isDebugEnabled()) {
            LOG.debug("Incremented ref counter to {} for FS {}", count, fileSystem);
         }

         if (this.listener != null) {
            this.listener.onIncrement(fsUri);
         }
      }
   }

   int getReferenceCount(URI fsUri) {
      FileSystemPool.Bucket bucket = this.pool.get(fsUri);
      return bucket == null ? 0 : bucket.counter.get();
   }

   public void setListener(FileSystemPool.Listener listener) {
      try (AutoLock ignore = this.poolLock.lock()) {
         this.listener = listener;
      }
   }

   static {
      Map<String, String> env = new HashMap<>();
      env.put("releaseVersion", "runtime");
      ENV_MULTIRELEASE_RUNTIME = env;
   }

   private static class Bucket {
      private final AtomicInteger counter;
      private final FileSystem fileSystem;
      private final FileTime lastModifiedTime;
      private final long size;
      private final Path path;
      private final FileSystemPool.Mount mount;

      private Bucket(URI fsUri, FileSystem fileSystem, FileSystemPool.Mount mount) {
         URI containerUri = URIUtil.unwrapContainer(fsUri);
         Path path = Paths.get(containerUri);
         long size = -1L;
         FileTime lastModifiedTime = null;

         try {
            size = Files.size(path);
            lastModifiedTime = Files.getLastModifiedTime(path);
         } catch (IOException var10) {
            if (FileSystemPool.LOG.isDebugEnabled()) {
               FileSystemPool.LOG.debug("Cannot read size or last modified time from {} backing filesystem at {}", path, fsUri);
            }
         }

         this.counter = new AtomicInteger(1);
         this.fileSystem = fileSystem;
         this.path = path;
         this.size = size;
         this.lastModifiedTime = lastModifiedTime;
         this.mount = mount;
      }

      @Override
      public String toString() {
         return this.fileSystem.toString() + "#" + this.counter;
      }
   }

   public interface Listener {
      void onRetain(URI var1);

      void onIncrement(URI var1);

      void onDecrement(URI var1);

      void onClose(URI var1);
   }

   public static class Mount implements Closeable {
      private final URI fsUri;
      private final Resource root;

      private Mount(URI fsUri, Resource resource) {
         this.fsUri = fsUri;
         this.root = resource;
      }

      public Resource root() {
         return this.root;
      }

      @Override
      public void close() {
         FileSystemPool.INSTANCE.unmount(this.fsUri);
      }

      @Override
      public String toString() {
         return String.format("%s[uri=%s,root=%s]", this.getClass().getSimpleName(), this.fsUri, this.root);
      }
   }

   public static class StackLoggingListener implements FileSystemPool.Listener {
      private static final Logger LOG = LoggerFactory.getLogger(FileSystemPool.StackLoggingListener.class);

      @Override
      public void onRetain(URI uri) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Retain {}", uri, new Throwable("Retain"));
         }
      }

      @Override
      public void onIncrement(URI uri) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Increment {}", uri, new Throwable("Increment"));
         }
      }

      @Override
      public void onDecrement(URI uri) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Decrement {}", uri, new Throwable("Decrement"));
         }
      }

      @Override
      public void onClose(URI uri) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Close {}", uri, new Throwable("Close"));
         }
      }
   }
}
