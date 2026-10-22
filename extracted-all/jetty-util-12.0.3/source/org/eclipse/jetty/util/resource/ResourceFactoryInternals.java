package org.eclipse.jetty.util.resource;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.ProviderNotFoundException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.component.DumpableCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class ResourceFactoryInternals {
   private static final Logger LOG = LoggerFactory.getLogger(ResourceFactoryInternals.class);
   private static final Path CURRENT_WORKING_DIR = Path.of(System.getProperty("user.dir"));
   static final Index.Mutable<ResourceFactory> RESOURCE_FACTORIES = new Index.Builder<ResourceFactory>().caseSensitive(false).mutable().build();
   static ResourceFactory ROOT;

   static boolean isSupported(URI uri) {
      return uri != null && uri.getScheme() != null ? RESOURCE_FACTORIES.get(uri.getScheme()) != null : false;
   }

   static boolean isSupported(String str) {
      return StringUtil.isBlank(str) ? false : RESOURCE_FACTORIES.getBest(str) != null;
   }

   static {
      MountedPathResourceFactory mountedPathResourceFactory = new MountedPathResourceFactory();
      RESOURCE_FACTORIES.put("jar", mountedPathResourceFactory);
      PathResourceFactory pathResourceFactory = new PathResourceFactory();
      RESOURCE_FACTORIES.put("file", pathResourceFactory);
      RESOURCE_FACTORIES.put("jrt", pathResourceFactory);
      URL url = ResourceFactoryInternals.class.getResource("/org/eclipse/jetty/version/build.properties");
      if (url != null && !RESOURCE_FACTORIES.contains(url.getProtocol())) {
         ResourceFactory resourceFactory;
         if (GraalIssue5720PathResource.isAffectedURL(url)) {
            resourceFactory = new GraalIssue5720PathResourceFactory();
         } else {
            resourceFactory = (ResourceFactory)(url.toString().contains("!/") ? mountedPathResourceFactory : pathResourceFactory);
         }

         RESOURCE_FACTORIES.put(url.getProtocol(), resourceFactory);
      }

      ROOT = new ResourceFactoryInternals.CompositeResourceFactory() {
         @Override
         protected void onMounted(FileSystemPool.Mount mount, URI uri) {
            if (LOG.isDebugEnabled()) {
               LOG.warn("Leaked {} for {}", new Object[]{mount, uri, new Throwable()});
            } else {
               LOG.warn("Leaked {} for {}", mount, uri);
            }
         }
      };
   }

   static class Closeable implements ResourceFactory.Closeable {
      private final ResourceFactoryInternals.CompositeResourceFactory _compositeResourceFactory = new ResourceFactoryInternals.CompositeResourceFactory();

      @Override
      public Resource newResource(URI uri) {
         return this._compositeResourceFactory.newResource(uri);
      }

      @Override
      public void close() {
         for (FileSystemPool.Mount mount : this._compositeResourceFactory.getMounts()) {
            IO.close((java.io.Closeable)mount);
         }

         this._compositeResourceFactory.clearMounts();
      }
   }

   static class CompositeResourceFactory implements ResourceFactory {
      private final List<FileSystemPool.Mount> _mounts = new CopyOnWriteArrayList<>();

      @Override
      public Resource newResource(URI uri) {
         if (uri == null) {
            return null;
         } else {
            try {
               if (!uri.isAbsolute()) {
                  if (uri.toString().startsWith("/")) {
                     uri = new URI("file", uri.toString(), null);
                  } else {
                     uri = ResourceFactoryInternals.CURRENT_WORKING_DIR.toUri().resolve(uri);
                  }

                  uri = URIUtil.correctFileURI(uri);
               }

               ResourceFactory resourceFactory = ResourceFactoryInternals.RESOURCE_FACTORIES.get(uri.getScheme());
               if (resourceFactory == null) {
                  throw new IllegalArgumentException("URI scheme not supported: " + uri);
               } else {
                  if (resourceFactory instanceof MountedPathResourceFactory) {
                     FileSystemPool.Mount mount = this.mountIfNeeded(uri);
                     if (mount != null) {
                        this._mounts.add(mount);
                        this.onMounted(mount, uri);
                     }
                  }

                  return resourceFactory.newResource(uri);
               }
            } catch (ProviderNotFoundException | URISyntaxException var4) {
               throw new IllegalArgumentException("Unable to create resource from: " + uri, var4);
            }
         }
      }

      private FileSystemPool.Mount mountIfNeeded(URI uri) {
         String scheme = uri.getScheme();
         if (!"jar".equalsIgnoreCase(scheme)) {
            return null;
         } else {
            try {
               return FileSystemPool.INSTANCE.mount(uri);
            } catch (IOException var4) {
               throw new IllegalArgumentException("Unable to mount: " + uri, var4);
            }
         }
      }

      protected void onMounted(FileSystemPool.Mount mount, URI uri) {
      }

      public List<FileSystemPool.Mount> getMounts() {
         return this._mounts;
      }

      public void clearMounts() {
         this._mounts.clear();
      }
   }

   static class LifeCycle extends AbstractLifeCycle implements ResourceFactory.LifeCycle {
      private final ResourceFactoryInternals.CompositeResourceFactory _compositeResourceFactory = new ResourceFactoryInternals.CompositeResourceFactory();

      @Override
      public Resource newResource(URI uri) {
         return this._compositeResourceFactory.newResource(uri);
      }

      @Override
      protected void doStop() throws Exception {
         for (FileSystemPool.Mount mount : this._compositeResourceFactory.getMounts()) {
            IO.close((java.io.Closeable)mount);
         }

         this._compositeResourceFactory.clearMounts();
         super.doStop();
      }

      @Override
      public void dump(Appendable out, String indent) throws IOException {
         List<URI> referencedUris = this._compositeResourceFactory.getMounts().stream().map(mount -> mount.root().getURI()).toList();
         Dumpable.dumpObjects(out, indent, this, new DumpableCollection("newResourceReferences", referencedUris));
      }
   }
}
