package io.github.classgraph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fileslice.reader.ClassfileReader;
import nonapi.io.github.classgraph.recycler.RecycleOnClose;
import nonapi.io.github.classgraph.recycler.Recycler;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.ProxyingInputStream;
import nonapi.io.github.classgraph.utils.VersionFinder;

class ClasspathElementModule extends ClasspathElement {
   final ModuleRef moduleRef;
   SingletonMap<ModuleRef, Recycler<ModuleReaderProxy, IOException>, IOException> moduleRefToModuleReaderProxyRecyclerMap;
   private Recycler<ModuleReaderProxy, IOException> moduleReaderProxyRecycler;
   private final Set<String> allResourcePaths = new HashSet<>();

   ClasspathElementModule(
      ModuleRef moduleRef,
      SingletonMap<ModuleRef, Recycler<ModuleReaderProxy, IOException>, IOException> moduleRefToModuleReaderProxyRecyclerMap,
      Scanner.ClasspathEntryWorkUnit workUnit,
      ScanSpec scanSpec
   ) {
      super(workUnit, scanSpec);
      this.moduleRefToModuleReaderProxyRecyclerMap = moduleRefToModuleReaderProxyRecyclerMap;
      this.moduleRef = moduleRef;
   }

   @Override
   void open(WorkQueue<Scanner.ClasspathEntryWorkUnit> workQueueIgnored, LogNode log) throws InterruptedException {
      if (!this.scanSpec.scanModules) {
         if (log != null) {
            this.log(this.classpathElementIdx, "Skipping module, since module scanning is disabled: " + this.getModuleName(), log);
         }

         this.skipClasspathElement = true;
      } else {
         try {
            this.moduleReaderProxyRecycler = this.moduleRefToModuleReaderProxyRecyclerMap.get(this.moduleRef, log);
         } catch (SingletonMap.NullSingletonException | SingletonMap.NewInstanceException | IOException var4) {
            if (log != null) {
               this.log(
                  this.classpathElementIdx, "Skipping invalid module " + this.getModuleName() + " : " + (var4.getCause() == null ? var4 : var4.getCause()), log
               );
            }

            this.skipClasspathElement = true;
         }
      }
   }

   private Resource newResource(final String resourcePath) {
      return new Resource(this, -1L) {
         private ModuleReaderProxy moduleReaderProxy;
         private final AtomicBoolean isOpen = new AtomicBoolean();

         @Override
         public String getPath() {
            return resourcePath;
         }

         @Override
         public long getLastModified() {
            return 0L;
         }

         @Override
         public Set<PosixFilePermission> getPosixFilePermissions() {
            return null;
         }

         @Override
         public ByteBuffer read() throws IOException {
            if (ClasspathElementModule.this.skipClasspathElement) {
               throw new IOException("Module could not be opened");
            } else if (this.isOpen.getAndSet(true)) {
               throw new IOException("Resource is already open -- cannot open it again without first calling close()");
            } else {
               try {
                  this.moduleReaderProxy = ClasspathElementModule.this.moduleReaderProxyRecycler.acquire();
                  this.byteBuffer = this.moduleReaderProxy.read(resourcePath);
                  this.length = (long)this.byteBuffer.remaining();
                  return this.byteBuffer;
               } catch (OutOfMemoryError | SecurityException var2) {
                  this.close();
                  throw new IOException("Could not open " + this, var2);
               }
            }
         }

         @Override
         ClassfileReader openClassfile() throws IOException {
            return new ClassfileReader(this.open(), this);
         }

         @Override
         public InputStream open() throws IOException {
            if (ClasspathElementModule.this.skipClasspathElement) {
               throw new IOException("Module could not be opened");
            } else if (this.isOpen.getAndSet(true)) {
               throw new IOException("Resource is already open -- cannot open it again without first calling close()");
            } else {
               try {
                  this.moduleReaderProxy = ClasspathElementModule.this.moduleReaderProxyRecycler.acquire();
                  this.inputStream = new ProxyingInputStream(this.moduleReaderProxy.open(resourcePath)) {
                     @Override
                     public void close() throws IOException {
                        super.close();

                        try {
                           close();
                        } catch (Exception var2) {
                        }
                     }
                  };
                  this.length = -1L;
                  return this.inputStream;
               } catch (SecurityException var2) {
                  this.close();
                  throw new IOException("Could not open " + this, var2);
               }
            }
         }

         @Override
         public byte[] load() throws IOException {
            Resource res = this;
            Throwable var2 = null;

            byte[] var4;
            try {
               this.read();
               byte[] byteArray;
               if (res.byteBuffer.hasArray() && res.byteBuffer.position() == 0 && res.byteBuffer.limit() == res.byteBuffer.capacity()) {
                  byteArray = res.byteBuffer.array();
               } else {
                  byteArray = new byte[res.byteBuffer.remaining()];
                  res.byteBuffer.get(byteArray);
               }

               res.length = (long)byteArray.length;
               var4 = byteArray;
            } catch (Throwable var13) {
               var2 = var13;
               throw var13;
            } finally {
               if (this != null) {
                  if (var2 != null) {
                     try {
                        res.close();
                     } catch (Throwable var12) {
                        var2.addSuppressed(var12);
                     }
                  } else {
                     this.close();
                  }
               }
            }

            return var4;
         }

         @Override
         public void close() {
            if (this.isOpen.getAndSet(false)) {
               if (this.moduleReaderProxy != null) {
                  if (this.byteBuffer != null) {
                     this.moduleReaderProxy.release(this.byteBuffer);
                     this.byteBuffer = null;
                  }

                  ClasspathElementModule.this.moduleReaderProxyRecycler.recycle(this.moduleReaderProxy);
                  this.moduleReaderProxy = null;
               }

               super.close();
            }
         }
      };
   }

   @Override
   Resource getResource(String relativePath) {
      return this.allResourcePaths.contains(relativePath) ? this.newResource(relativePath) : null;
   }

   @Override
   void scanPaths(LogNode log) {
      if (!this.skipClasspathElement) {
         if (this.scanned.getAndSet(true)) {
            throw new IllegalArgumentException("Already scanned classpath element " + this);
         } else {
            LogNode subLog = log == null ? null : this.log(this.classpathElementIdx, "Scanning module " + this.moduleRef.getName(), log);
            boolean isModularJar = VersionFinder.JAVA_MAJOR_VERSION >= 9 && this.getModuleName() != null;

            try (RecycleOnClose<ModuleReaderProxy, IOException> moduleReaderProxyRecycleOnClose = this.moduleReaderProxyRecycler.acquireRecycleOnClose()) {
               List<String> resourceRelativePaths;
               try {
                  resourceRelativePaths = moduleReaderProxyRecycleOnClose.get().list();
               } catch (SecurityException var25) {
                  if (subLog != null) {
                     subLog.log("Could not get resource list for module " + this.moduleRef.getName(), var25);
                  }

                  return;
               }

               CollectionUtils.sortIfNotEmpty(resourceRelativePaths);
               String prevParentRelativePath = null;
               ScanSpec.ScanSpecPathMatch prevParentMatchStatus = null;

               for (String relativePath : resourceRelativePaths) {
                  if (!relativePath.endsWith("/")) {
                     if (relativePath.startsWith("META-INF/versions/")) {
                        if (subLog != null) {
                           subLog.log("Found unexpected nested versioned entry in module -- skipping: " + relativePath);
                        }
                     } else if ((!isModularJar || relativePath.indexOf(47) >= 0 || !relativePath.endsWith(".class") || relativePath.equals("module-info.class"))
                        && this.checkResourcePathAcceptReject(relativePath, log)) {
                        int lastSlashIdx = relativePath.lastIndexOf(47);
                        String parentRelativePath = lastSlashIdx < 0 ? "/" : relativePath.substring(0, lastSlashIdx + 1);
                        boolean parentRelativePathChanged = !parentRelativePath.equals(prevParentRelativePath);
                        ScanSpec.ScanSpecPathMatch parentMatchStatus = prevParentRelativePath != null && !parentRelativePathChanged
                           ? prevParentMatchStatus
                           : this.scanSpec.dirAcceptMatchStatus(parentRelativePath);
                        prevParentRelativePath = parentRelativePath;
                        prevParentMatchStatus = parentMatchStatus;
                        if (parentMatchStatus == ScanSpec.ScanSpecPathMatch.HAS_REJECTED_PATH_PREFIX) {
                           if (subLog != null) {
                              subLog.log("Skipping rejected path: " + relativePath);
                           }
                        } else if (this.allResourcePaths.add(relativePath)) {
                           if (parentMatchStatus != ScanSpec.ScanSpecPathMatch.HAS_ACCEPTED_PATH_PREFIX
                              && parentMatchStatus != ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_PATH
                              && (
                                 parentMatchStatus != ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_CLASS_PACKAGE
                                    || !this.scanSpec.classfileIsSpecificallyAccepted(relativePath)
                              )) {
                              if (this.scanSpec.enableClassInfo && relativePath.equals("module-info.class")) {
                                 this.addAcceptedResource(this.newResource(relativePath), parentMatchStatus, true, subLog);
                              }
                           } else {
                              this.addAcceptedResource(this.newResource(relativePath), parentMatchStatus, false, subLog);
                           }
                        }
                     }
                  }
               }

               File moduleFile = this.moduleRef.getLocationFile();
               if (moduleFile != null && moduleFile.exists()) {
                  this.fileToLastModified.put(moduleFile, moduleFile.lastModified());
               }
            } catch (IOException var28) {
               if (subLog != null) {
                  subLog.log("Exception opening module " + this.moduleRef.getName(), var28);
               }

               this.skipClasspathElement = true;
            }

            this.finishScanPaths(subLog);
         }
      }
   }

   ModuleRef getModuleRef() {
      return this.moduleRef;
   }

   @Override
   public String getModuleName() {
      String moduleName = this.moduleRef.getName();
      if (moduleName == null || moduleName.isEmpty()) {
         moduleName = this.moduleNameFromModuleDescriptor;
      }

      return moduleName != null && !moduleName.isEmpty() ? moduleName : null;
   }

   private String getModuleNameOrEmpty() {
      String moduleName = this.getModuleName();
      return moduleName == null ? "" : moduleName;
   }

   @Override
   URI getURI() {
      URI uri = this.moduleRef.getLocation();
      if (uri == null) {
         throw new IllegalArgumentException("Module " + this.getModuleName() + " has a null location");
      } else {
         return uri;
      }
   }

   @Override
   List<URI> getAllURIs() {
      return Collections.singletonList(this.getURI());
   }

   @Override
   File getFile() {
      try {
         URI uri = this.moduleRef.getLocation();
         if (uri != null && !uri.getScheme().equals("jrt")) {
            File file = new File(uri);
            if (file.exists()) {
               return file;
            }
         }
      } catch (Exception var3) {
      }

      return null;
   }

   @Override
   public String toString() {
      return this.moduleRef.toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ClasspathElementModule)) {
         return false;
      } else {
         ClasspathElementModule other = (ClasspathElementModule)obj;
         return this.getModuleNameOrEmpty().equals(other.getModuleNameOrEmpty());
      }
   }

   @Override
   public int hashCode() {
      return this.getModuleNameOrEmpty().hashCode();
   }
}
