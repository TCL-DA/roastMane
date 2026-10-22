package io.github.classgraph;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import nonapi.io.github.classgraph.utils.CollectionUtils;

public class ResourceList extends PotentiallyUnmodifiableList<Resource> implements AutoCloseable {
   static final long serialVersionUID = 1L;
   static final ResourceList EMPTY_LIST = new ResourceList();
   private static final ResourceList.ResourceFilter CLASSFILE_FILTER = new ResourceList.ResourceFilter() {
      @Override
      public boolean accept(Resource resource) {
         String path = resource.getPath();
         if (path.endsWith(".class") && path.length() >= 7) {
            char c = path.charAt(path.length() - 7);
            return c != '/' && c != '.';
         } else {
            return false;
         }
      }
   };

   public static ResourceList emptyList() {
      return EMPTY_LIST;
   }

   public ResourceList() {
   }

   public ResourceList(int sizeHint) {
      super(sizeHint);
   }

   public ResourceList(Collection<Resource> resourceCollection) {
      super(resourceCollection);
   }

   public ResourceList get(String resourcePath) {
      boolean hasResourceWithPath = false;

      for (Resource res : this) {
         if (res.getPath().equals(resourcePath)) {
            hasResourceWithPath = true;
            break;
         }
      }

      if (!hasResourceWithPath) {
         return EMPTY_LIST;
      } else {
         ResourceList matchingResources = new ResourceList(2);

         for (Resource resx : this) {
            if (resx.getPath().equals(resourcePath)) {
               matchingResources.add(resx);
            }
         }

         return matchingResources;
      }
   }

   public List<String> getPaths() {
      List<String> resourcePaths = new ArrayList<>(this.size());

      for (Resource resource : this) {
         resourcePaths.add(resource.getPath());
      }

      return resourcePaths;
   }

   public List<String> getPathsRelativeToClasspathElement() {
      List<String> resourcePaths = new ArrayList<>(this.size());

      for (Resource resource : this) {
         resourcePaths.add(resource.getPath());
      }

      return resourcePaths;
   }

   public List<URL> getURLs() {
      List<URL> resourceURLs = new ArrayList<>(this.size());

      for (Resource resource : this) {
         resourceURLs.add(resource.getURL());
      }

      return resourceURLs;
   }

   public List<URI> getURIs() {
      List<URI> resourceURLs = new ArrayList<>(this.size());

      for (Resource resource : this) {
         resourceURLs.add(resource.getURI());
      }

      return resourceURLs;
   }

   public ResourceList classFilesOnly() {
      return this.filter(CLASSFILE_FILTER);
   }

   public ResourceList nonClassFilesOnly() {
      return this.filter(new ResourceList.ResourceFilter() {
         @Override
         public boolean accept(Resource resource) {
            return !ResourceList.CLASSFILE_FILTER.accept(resource);
         }
      });
   }

   public Map<String, ResourceList> asMap() {
      Map<String, ResourceList> pathToResourceList = new HashMap<>();

      for (Resource resource : this) {
         String path = resource.getPath();
         ResourceList resourceList = pathToResourceList.get(path);
         if (resourceList == null) {
            resourceList = new ResourceList(1);
            pathToResourceList.put(path, resourceList);
         }

         resourceList.add(resource);
      }

      return pathToResourceList;
   }

   public List<Entry<String, ResourceList>> findDuplicatePaths() {
      List<Entry<String, ResourceList>> duplicatePaths = new ArrayList<>();

      for (Entry<String, ResourceList> pathAndResourceList : this.asMap().entrySet()) {
         if (pathAndResourceList.getValue().size() > 1) {
            duplicatePaths.add(new SimpleEntry<>(pathAndResourceList.getKey(), pathAndResourceList.getValue()));
         }
      }

      CollectionUtils.sortIfNotEmpty(duplicatePaths, new Comparator<Entry<String, ResourceList>>() {
         public int compare(Entry<String, ResourceList> o1, Entry<String, ResourceList> o2) {
            return o1.getKey().compareTo(o2.getKey());
         }
      });
      return duplicatePaths;
   }

   public ResourceList filter(ResourceList.ResourceFilter filter) {
      ResourceList resourcesFiltered = new ResourceList();

      for (Resource resource : this) {
         if (filter.accept(resource)) {
            resourcesFiltered.add(resource);
         }
      }

      return resourcesFiltered;
   }

   @Deprecated
   public void forEachByteArray(ResourceList.ByteArrayConsumer byteArrayConsumer, boolean ignoreIOExceptions) {
      for (Resource resource : this) {
         try {
            Resource resourceToClose = resource;
            Throwable var6 = null;

            try {
               byteArrayConsumer.accept(resourceToClose, resourceToClose.load());
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (resource != null) {
                  if (var6 != null) {
                     try {
                        resourceToClose.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     resource.close();
                  }
               }
            }
         } catch (IOException var18) {
            if (!ignoreIOExceptions) {
               throw new IllegalArgumentException("Could not load resource " + resource, var18);
            }
         }
      }
   }

   @Deprecated
   public void forEachByteArray(ResourceList.ByteArrayConsumer byteArrayConsumer) {
      this.forEachByteArray(byteArrayConsumer, false);
   }

   public void forEachByteArrayIgnoringIOException(ResourceList.ByteArrayConsumer byteArrayConsumer) {
      for (Resource resource : this) {
         try {
            Resource resourceToClose = resource;
            Throwable var5 = null;

            try {
               byteArrayConsumer.accept(resourceToClose, resourceToClose.load());
            } catch (Throwable var15) {
               var5 = var15;
               throw var15;
            } finally {
               if (resource != null) {
                  if (var5 != null) {
                     try {
                        resourceToClose.close();
                     } catch (Throwable var14) {
                        var5.addSuppressed(var14);
                     }
                  } else {
                     resource.close();
                  }
               }
            }
         } catch (IOException var17) {
         }
      }
   }

   public void forEachByteArrayThrowingIOException(ResourceList.ByteArrayConsumerThrowsIOException byteArrayConsumerThrowsIOException) throws IOException {
      for (Resource resource : this) {
         Resource resourceToClose = resource;
         Throwable var5 = null;

         try {
            byteArrayConsumerThrowsIOException.accept(resourceToClose, resourceToClose.load());
         } catch (Throwable var14) {
            var5 = var14;
            throw var14;
         } finally {
            if (resource != null) {
               if (var5 != null) {
                  try {
                     resourceToClose.close();
                  } catch (Throwable var13) {
                     var5.addSuppressed(var13);
                  }
               } else {
                  resource.close();
               }
            }
         }
      }
   }

   @Deprecated
   public void forEachInputStream(ResourceList.InputStreamConsumer inputStreamConsumer, boolean ignoreIOExceptions) {
      for (Resource resource : this) {
         try {
            Resource resourceToClose = resource;
            Throwable var6 = null;

            try {
               inputStreamConsumer.accept(resourceToClose, resourceToClose.open());
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (resource != null) {
                  if (var6 != null) {
                     try {
                        resourceToClose.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     resource.close();
                  }
               }
            }
         } catch (IOException var18) {
            if (!ignoreIOExceptions) {
               throw new IllegalArgumentException("Could not load resource " + resource, var18);
            }
         }
      }
   }

   @Deprecated
   public void forEachInputStream(ResourceList.InputStreamConsumer inputStreamConsumer) {
      this.forEachInputStream(inputStreamConsumer, false);
   }

   public void forEachInputStreamIgnoringIOException(ResourceList.InputStreamConsumer inputStreamConsumer) {
      for (Resource resource : this) {
         try {
            Resource resourceToClose = resource;
            Throwable var5 = null;

            try {
               inputStreamConsumer.accept(resourceToClose, resourceToClose.open());
            } catch (Throwable var15) {
               var5 = var15;
               throw var15;
            } finally {
               if (resource != null) {
                  if (var5 != null) {
                     try {
                        resourceToClose.close();
                     } catch (Throwable var14) {
                        var5.addSuppressed(var14);
                     }
                  } else {
                     resource.close();
                  }
               }
            }
         } catch (IOException var17) {
         }
      }
   }

   public void forEachInputStreamThrowingIOException(ResourceList.InputStreamConsumerThrowsIOException inputStreamConsumerThrowsIOException) throws IOException {
      for (Resource resource : this) {
         Resource resourceToClose = resource;
         Throwable var5 = null;

         try {
            inputStreamConsumerThrowsIOException.accept(resourceToClose, resourceToClose.open());
         } catch (Throwable var14) {
            var5 = var14;
            throw var14;
         } finally {
            if (resource != null) {
               if (var5 != null) {
                  try {
                     resourceToClose.close();
                  } catch (Throwable var13) {
                     var5.addSuppressed(var13);
                  }
               } else {
                  resource.close();
               }
            }
         }
      }
   }

   @Deprecated
   public void forEachByteBuffer(ResourceList.ByteBufferConsumer byteBufferConsumer, boolean ignoreIOExceptions) {
      for (Resource resource : this) {
         try {
            Resource resourceToClose = resource;
            Throwable var6 = null;

            try {
               byteBufferConsumer.accept(resourceToClose, resourceToClose.read());
            } catch (Throwable var16) {
               var6 = var16;
               throw var16;
            } finally {
               if (resource != null) {
                  if (var6 != null) {
                     try {
                        resourceToClose.close();
                     } catch (Throwable var15) {
                        var6.addSuppressed(var15);
                     }
                  } else {
                     resource.close();
                  }
               }
            }
         } catch (IOException var18) {
            if (!ignoreIOExceptions) {
               throw new IllegalArgumentException("Could not load resource " + resource, var18);
            }
         }
      }
   }

   @Deprecated
   public void forEachByteBuffer(ResourceList.ByteBufferConsumer byteBufferConsumer) {
      this.forEachByteBuffer(byteBufferConsumer, false);
   }

   public void forEachByteBufferIgnoringIOException(ResourceList.ByteBufferConsumer byteBufferConsumer) {
      for (Resource resource : this) {
         try {
            Resource resourceToClose = resource;
            Throwable var5 = null;

            try {
               byteBufferConsumer.accept(resourceToClose, resourceToClose.read());
            } catch (Throwable var15) {
               var5 = var15;
               throw var15;
            } finally {
               if (resource != null) {
                  if (var5 != null) {
                     try {
                        resourceToClose.close();
                     } catch (Throwable var14) {
                        var5.addSuppressed(var14);
                     }
                  } else {
                     resource.close();
                  }
               }
            }
         } catch (IOException var17) {
         }
      }
   }

   public void forEachByteBufferThrowingIOException(ResourceList.ByteBufferConsumerThrowsIOException byteBufferConsumerThrowsIOException) throws IOException {
      for (Resource resource : this) {
         Resource resourceToClose = resource;
         Throwable var5 = null;

         try {
            byteBufferConsumerThrowsIOException.accept(resourceToClose, resourceToClose.read());
         } catch (Throwable var14) {
            var5 = var14;
            throw var14;
         } finally {
            if (resource != null) {
               if (var5 != null) {
                  try {
                     resourceToClose.close();
                  } catch (Throwable var13) {
                     var5.addSuppressed(var13);
                  }
               } else {
                  resource.close();
               }
            }
         }
      }
   }

   @Override
   public void close() {
      for (Resource resource : this) {
         resource.close();
      }
   }

   static {
      EMPTY_LIST.makeUnmodifiable();
   }

   @FunctionalInterface
   public interface ByteArrayConsumer {
      void accept(Resource var1, byte[] var2);
   }

   @FunctionalInterface
   public interface ByteArrayConsumerThrowsIOException {
      void accept(Resource var1, byte[] var2) throws IOException;
   }

   @FunctionalInterface
   public interface ByteBufferConsumer {
      void accept(Resource var1, ByteBuffer var2);
   }

   @FunctionalInterface
   public interface ByteBufferConsumerThrowsIOException {
      void accept(Resource var1, ByteBuffer var2) throws IOException;
   }

   @FunctionalInterface
   public interface InputStreamConsumer {
      void accept(Resource var1, InputStream var2);
   }

   @FunctionalInterface
   public interface InputStreamConsumerThrowsIOException {
      void accept(Resource var1, InputStream var2) throws IOException;
   }

   @FunctionalInterface
   public interface ResourceFilter {
      boolean accept(Resource var1);
   }
}
