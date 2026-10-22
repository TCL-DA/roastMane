package org.eclipse.jetty.util.resource;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.component.Container;
import org.eclipse.jetty.util.component.Dumpable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ResourceFactory {
   Logger LOG = LoggerFactory.getLogger(ResourceFactory.class);

   static Resource combine(List<Resource> resources) {
      return CombinedResource.combine(resources);
   }

   static Resource combine(Resource... resources) {
      return CombinedResource.combine(List.of(resources));
   }

   Resource newResource(URI var1);

   @Deprecated(
      since = "12.0.2",
      forRemoval = true
   )
   default Resource newSystemResource(String resource) {
      return this.newClassLoaderResource(resource);
   }

   default Resource newClassLoaderResource(String resource, boolean searchSystemClassLoader) {
      if (StringUtil.isBlank(resource)) {
         throw new IllegalArgumentException("Resource String is invalid: " + resource);
      } else {
         URL url = null;
         List<Function<String, URL>> loaders = new ArrayList<>();
         loaders.add(Thread.currentThread().getContextClassLoader()::getResource);
         loaders.add(ResourceFactory.class.getClassLoader()::getResource);
         if (searchSystemClassLoader) {
            loaders.add(ClassLoader::getSystemResource);
         }

         for (Function<String, URL> loader : loaders) {
            if (url != null) {
               break;
            }

            try {
               url = loader.apply(resource);
               if (url == null && resource.startsWith("/")) {
                  url = loader.apply(resource.substring(1));
               }
            } catch (IllegalArgumentException var9) {
               if (LOG.isTraceEnabled()) {
                  LOG.trace("Ignoring bad getResource(): {}", resource, var9);
               }
            }
         }

         if (url == null) {
            return null;
         } else {
            try {
               URI uri = url.toURI();
               return this.newResource(uri);
            } catch (URISyntaxException var8) {
               throw new IllegalArgumentException("Error creating resource from URL: " + url, var8);
            }
         }
      }
   }

   default Resource newClassLoaderResource(String resource) {
      return this.newClassLoaderResource(resource, true);
   }

   @Deprecated(
      since = "12.0.2",
      forRemoval = true
   )
   default Resource newClassPathResource(String resource) {
      return this.newClassLoaderResource(resource, false);
   }

   default Resource newMemoryResource(URL url) {
      if (url == null) {
         throw new IllegalArgumentException("URL is null");
      } else {
         return new MemoryResource(url);
      }
   }

   default Resource newResource(String resource) {
      if (StringUtil.isBlank(resource)) {
         throw new IllegalArgumentException("Resource String is invalid: " + resource);
      } else {
         return this.newResource(URIUtil.toURI(resource));
      }
   }

   default Resource newResource(Path path) {
      if (path == null) {
         throw new IllegalArgumentException("Path is null");
      } else {
         return this.newResource(path.toUri());
      }
   }

   default Resource newResource(List<URI> uris) {
      if (uris != null && !uris.isEmpty()) {
         return combine(uris.stream().map(this::newResource).toList());
      } else {
         throw new IllegalArgumentException("List of URIs is invalid");
      }
   }

   default Resource newResource(URL url) {
      if (url == null) {
         throw new IllegalArgumentException("URL is null");
      } else {
         try {
            return this.newResource(url.toURI());
         } catch (URISyntaxException var3) {
            throw new IllegalArgumentException("Error creating resource from URL: " + url, var3);
         }
      }
   }

   default Resource newJarFileResource(URI uri) {
      if (!uri.getScheme().equalsIgnoreCase("file")) {
         throw new IllegalArgumentException("Not an allowed path: " + uri);
      } else {
         return this.newResource(URIUtil.toJarFileUri(uri));
      }
   }

   static boolean isSupported(String str) {
      return ResourceFactoryInternals.isSupported(str);
   }

   static boolean isSupported(URI uri) {
      return ResourceFactoryInternals.isSupported(uri);
   }

   static void registerResourceFactory(String scheme, ResourceFactory resourceFactory) {
      if (StringUtil.isBlank(scheme)) {
         throw new IllegalArgumentException("Scheme is blank");
      } else {
         ResourceFactoryInternals.RESOURCE_FACTORIES.put(scheme, resourceFactory);
      }
   }

   static ResourceFactory unregisterResourceFactory(String scheme) {
      if (StringUtil.isBlank(scheme)) {
         throw new IllegalArgumentException("Scheme is blank");
      } else {
         return ResourceFactoryInternals.RESOURCE_FACTORIES.remove(scheme);
      }
   }

   static ResourceFactory root() {
      return ResourceFactoryInternals.ROOT;
   }

   static ResourceFactory.Closeable closeable() {
      return new ResourceFactoryInternals.Closeable();
   }

   static ResourceFactory.LifeCycle lifecycle() {
      ResourceFactory.LifeCycle factory = new ResourceFactoryInternals.LifeCycle();
      org.eclipse.jetty.util.component.LifeCycle.start(factory);
      return factory;
   }

   static ResourceFactory of(final Resource baseResource) {
      return baseResource instanceof ResourceFactory ? (ResourceFactory)baseResource : new ResourceFactory() {
         @Override
         public Resource newResource(URI resource) {
            return this.newResource(resource.toString());
         }

         @Override
         public Resource newResource(String resource) {
            return baseResource.resolve(resource);
         }
      };
   }

   static ResourceFactory of(final Container container) {
      Objects.requireNonNull(container);
      ResourceFactory.LifeCycle factory = container.getBean(ResourceFactory.LifeCycle.class);
      if (factory == null) {
         factory = lifecycle();
         container.addBean(factory, true);
         final ResourceFactory.LifeCycle finalFactory = factory;
         container.addEventListener(new org.eclipse.jetty.util.component.LifeCycle.Listener() {
            @Override
            public void lifeCycleStopped(org.eclipse.jetty.util.component.LifeCycle event) {
               container.removeBean(this);
               container.removeBean(finalFactory);
            }
         });
      }

      return factory;
   }

   public interface Closeable extends ResourceFactory, java.io.Closeable {
      @Override
      void close();
   }

   public interface LifeCycle extends org.eclipse.jetty.util.component.LifeCycle, ResourceFactory, Dumpable {
   }
}
