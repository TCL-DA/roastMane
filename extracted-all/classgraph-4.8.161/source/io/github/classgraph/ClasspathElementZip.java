package io.github.classgraph;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.classloaderhandler.ClassLoaderHandlerRegistry;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fastzipfilereader.FastZipEntry;
import nonapi.io.github.classgraph.fastzipfilereader.LogicalZipFile;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.fastzipfilereader.ZipFileSlice;
import nonapi.io.github.classgraph.fileslice.reader.ClassfileReader;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.URLPathEncoder;
import nonapi.io.github.classgraph.utils.VersionFinder;

class ClasspathElementZip extends ClasspathElement {
   private final String rawPath;
   LogicalZipFile logicalZipFile;
   private String zipFilePath;
   private final ConcurrentHashMap<String, Resource> relativePathToResource = new ConcurrentHashMap<>();
   private final Set<String> strippedAutomaticPackageRootPrefixes = new HashSet<>();
   private final NestedJarHandler nestedJarHandler;
   String moduleNameFromManifestFile;
   private String derivedAutomaticModuleName;

   ClasspathElementZip(Scanner.ClasspathEntryWorkUnit workUnit, NestedJarHandler nestedJarHandler, ScanSpec scanSpec) {
      super(workUnit, scanSpec);
      Object rawPathObj = workUnit.classpathEntryObj;
      String rawPath = null;
      if (rawPathObj instanceof Path) {
         try {
            rawPath = ((Path)rawPathObj).toUri().toString();
         } catch (SecurityException | IOError var7) {
         }
      }

      if (rawPath == null) {
         rawPath = rawPathObj.toString();
      }

      this.rawPath = rawPath;
      this.zipFilePath = rawPath;
      this.nestedJarHandler = nestedJarHandler;
   }

   @Override
   void open(WorkQueue<Scanner.ClasspathEntryWorkUnit> workQueue, LogNode log) throws InterruptedException {
      if (!this.scanSpec.scanJars) {
         if (log != null) {
            this.log(this.classpathElementIdx, "Skipping classpath element, since jar scanning is disabled: " + this.rawPath, log);
         }

         this.skipClasspathElement = true;
      } else {
         LogNode subLog = log == null ? null : this.log(this.classpathElementIdx, "Opening jar: " + this.rawPath, log);
         int plingIdx = this.rawPath.indexOf(33);
         String outermostZipFilePathResolved = FastPathResolver.resolve(
            FileUtils.currDirPath(), plingIdx < 0 ? this.rawPath : this.rawPath.substring(0, plingIdx)
         );
         if (!this.scanSpec.jarAcceptReject.isAcceptedAndNotRejected(outermostZipFilePathResolved)) {
            if (subLog != null) {
               subLog.log("Skipping jarfile that is rejected or not accepted: " + this.rawPath);
            }

            this.skipClasspathElement = true;
         } else {
            try {
               Entry<LogicalZipFile, String> logicalZipFileAndPackageRoot;
               try {
                  logicalZipFileAndPackageRoot = this.nestedJarHandler.nestedPathToLogicalZipFileAndPackageRootMap.get(this.rawPath, subLog);
               } catch (SingletonMap.NewInstanceException | SingletonMap.NullSingletonException var16) {
                  throw new IOException("Could not get logical zipfile " + this.rawPath + " : " + (var16.getCause() == null ? var16 : var16.getCause()));
               }

               this.logicalZipFile = logicalZipFileAndPackageRoot.getKey();
               if (this.logicalZipFile == null) {
                  throw new IOException("Logical zipfile was null");
               }

               this.zipFilePath = FastPathResolver.resolve(FileUtils.currDirPath(), this.logicalZipFile.getPath());
               String packageRoot = logicalZipFileAndPackageRoot.getValue();
               if (!packageRoot.isEmpty()) {
                  this.packageRootPrefix = packageRoot + "/";
               }
            } catch (IllegalArgumentException | IOException var17) {
               if (subLog != null) {
                  subLog.log("Could not open jarfile " + this.rawPath + " : " + var17);
               }

               this.skipClasspathElement = true;
               return;
            }

            if (!this.scanSpec.enableSystemJarsAndModules && this.logicalZipFile.isJREJar) {
               if (subLog != null) {
                  subLog.log("Ignoring JRE jar: " + this.rawPath);
               }

               this.skipClasspathElement = true;
            } else if (!this.logicalZipFile.isAcceptedAndNotRejected(this.scanSpec.jarAcceptReject)) {
               if (subLog != null) {
                  subLog.log("Skipping jarfile that is rejected or not accepted: " + this.rawPath);
               }

               this.skipClasspathElement = true;
            } else {
               int childClasspathEntryIdx = 0;
               if (this.scanSpec.scanNestedJars) {
                  for (FastZipEntry zipEntry : this.logicalZipFile.entries) {
                     for (String libDirPrefix : ClassLoaderHandlerRegistry.AUTOMATIC_LIB_DIR_PREFIXES) {
                        if (zipEntry.entryNameUnversioned.startsWith(libDirPrefix) && zipEntry.entryNameUnversioned.endsWith(".jar")) {
                           String entryPath = zipEntry.getPath();
                           if (subLog != null) {
                              subLog.log("Found nested lib jar: " + entryPath);
                           }

                           workQueue.addWorkUnit(new Scanner.ClasspathEntryWorkUnit(entryPath, this.getClassLoader(), this, childClasspathEntryIdx++, ""));
                           break;
                        }
                     }
                  }
               }

               Set<String> scheduledChildClasspathElements = new HashSet<>();
               scheduledChildClasspathElements.add(this.rawPath);
               if (this.logicalZipFile.classPathManifestEntryValue != null) {
                  String jarParentDir = FileUtils.getParentDirPath(this.logicalZipFile.getPathWithinParentZipFileSlice());

                  for (String childClassPathEltPathRelative : this.logicalZipFile.classPathManifestEntryValue.split(" ")) {
                     if (!childClassPathEltPathRelative.isEmpty()) {
                        String childClassPathEltPath = FastPathResolver.resolve(jarParentDir, childClassPathEltPathRelative);
                        ZipFileSlice parentZipFileSlice = this.logicalZipFile.getParentZipFileSlice();
                        String childClassPathEltPathWithPrefix = parentZipFileSlice == null
                           ? childClassPathEltPath
                           : parentZipFileSlice.getPath() + (childClassPathEltPath.startsWith("/") ? "!" : "!/") + childClassPathEltPath;
                        if (scheduledChildClasspathElements.add(childClassPathEltPathWithPrefix)) {
                           workQueue.addWorkUnit(
                              new Scanner.ClasspathEntryWorkUnit(childClassPathEltPathWithPrefix, this.getClassLoader(), this, childClasspathEntryIdx++, "")
                           );
                        }
                     }
                  }
               }

               if (this.logicalZipFile.bundleClassPathManifestEntryValue != null) {
                  String zipFilePathPrefix = this.zipFilePath + "!/";

                  for (String childBundlePath : this.logicalZipFile.bundleClassPathManifestEntryValue.split(",")) {
                     while (childBundlePath.startsWith("/")) {
                        childBundlePath = childBundlePath.substring(1);
                     }

                     if (!childBundlePath.isEmpty() && !childBundlePath.equals(".")) {
                        String childClassPathEltPath = zipFilePathPrefix + FileUtils.sanitizeEntryPath(childBundlePath, true, true);
                        if (scheduledChildClasspathElements.add(childClassPathEltPath)) {
                           workQueue.addWorkUnit(
                              new Scanner.ClasspathEntryWorkUnit(childClassPathEltPath, this.getClassLoader(), this, childClasspathEntryIdx++, "")
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private Resource newResource(final FastZipEntry zipEntry, final String pathRelativeToPackageRoot) {
      return new Resource(this, zipEntry.uncompressedSize) {
         private final AtomicBoolean isOpen = new AtomicBoolean();

         @Override
         public String getPath() {
            return pathRelativeToPackageRoot;
         }

         @Override
         public String getPathRelativeToClasspathElement() {
            return zipEntry.entryName.startsWith(ClasspathElementZip.this.packageRootPrefix)
               ? zipEntry.entryName.substring(ClasspathElementZip.this.packageRootPrefix.length())
               : zipEntry.entryName;
         }

         @Override
         public long getLastModified() {
            return zipEntry.getLastModifiedTimeMillis();
         }

         @Override
         public Set<PosixFilePermission> getPosixFilePermissions() {
            int fileAttributes = zipEntry.fileAttributes;
            Set<PosixFilePermission> perms;
            if (fileAttributes == 0) {
               perms = null;
            } else {
               perms = new HashSet<>();
               if ((fileAttributes & 256) > 0) {
                  perms.add(PosixFilePermission.OWNER_READ);
               }

               if ((fileAttributes & 128) > 0) {
                  perms.add(PosixFilePermission.OWNER_WRITE);
               }

               if ((fileAttributes & 64) > 0) {
                  perms.add(PosixFilePermission.OWNER_EXECUTE);
               }

               if ((fileAttributes & 32) > 0) {
                  perms.add(PosixFilePermission.GROUP_READ);
               }

               if ((fileAttributes & 16) > 0) {
                  perms.add(PosixFilePermission.GROUP_WRITE);
               }

               if ((fileAttributes & 8) > 0) {
                  perms.add(PosixFilePermission.GROUP_EXECUTE);
               }

               if ((fileAttributes & 4) > 0) {
                  perms.add(PosixFilePermission.OTHERS_READ);
               }

               if ((fileAttributes & 2) > 0) {
                  perms.add(PosixFilePermission.OTHERS_WRITE);
               }

               if ((fileAttributes & 1) > 0) {
                  perms.add(PosixFilePermission.OTHERS_EXECUTE);
               }
            }

            return perms;
         }

         @Override
         ClassfileReader openClassfile() throws IOException {
            return new ClassfileReader(this.open(), this);
         }

         @Override
         public InputStream open() throws IOException {
            if (ClasspathElementZip.this.skipClasspathElement) {
               throw new IOException("Jarfile could not be opened");
            } else if (this.isOpen.getAndSet(true)) {
               throw new IOException("Resource is already open -- cannot open it again without first calling close()");
            } else {
               try {
                  this.inputStream = zipEntry.getSlice().open(this);
                  this.length = zipEntry.uncompressedSize;
                  return this.inputStream;
               } catch (IOException var2) {
                  this.close();
                  throw var2;
               }
            }
         }

         @Override
         public ByteBuffer read() throws IOException {
            if (ClasspathElementZip.this.skipClasspathElement) {
               throw new IOException("Jarfile could not be opened");
            } else if (this.isOpen.getAndSet(true)) {
               throw new IOException("Resource is already open -- cannot open it again without first calling close()");
            } else {
               try {
                  this.byteBuffer = zipEntry.getSlice().read();
                  this.length = (long)this.byteBuffer.remaining();
                  return this.byteBuffer;
               } catch (IOException var2) {
                  this.close();
                  throw var2;
               }
            }
         }

         @Override
         public byte[] load() throws IOException {
            if (ClasspathElementZip.this.skipClasspathElement) {
               throw new IOException("Jarfile could not be opened");
            } else if (this.isOpen.getAndSet(true)) {
               throw new IOException("Resource is already open -- cannot open it again without first calling close()");
            } else {
               Resource res = this;
               Throwable var2 = null;

               byte[] var4;
               try {
                  byte[] byteArray = zipEntry.getSlice().load();
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
         }

         @Override
         public void close() {
            if (this.isOpen.getAndSet(false)) {
               if (this.byteBuffer != null) {
                  this.byteBuffer = null;
               }

               super.close();
            }
         }
      };
   }

   @Override
   Resource getResource(String relativePath) {
      return this.relativePathToResource.get(relativePath);
   }

   @Override
   void scanPaths(LogNode log) {
      if (this.logicalZipFile == null) {
         this.skipClasspathElement = true;
      }

      if (!this.checkResourcePathAcceptReject(this.getZipFilePath(), log)) {
         this.skipClasspathElement = true;
      }

      if (!this.skipClasspathElement) {
         if (this.scanned.getAndSet(true)) {
            throw new IllegalArgumentException("Already scanned classpath element " + this.getZipFilePath());
         } else {
            LogNode subLog = log == null ? null : this.log(this.classpathElementIdx, "Scanning jarfile classpath element " + this.getZipFilePath(), log);
            boolean isModularJar = false;
            if (VersionFinder.JAVA_MAJOR_VERSION >= 9) {
               String moduleName = this.moduleNameFromModuleDescriptor;
               if (moduleName == null || moduleName.isEmpty()) {
                  moduleName = this.moduleNameFromManifestFile;
               }

               if (moduleName != null && moduleName.isEmpty()) {
                  moduleName = null;
               }

               if (moduleName != null) {
                  isModularJar = true;
               }
            }

            Set<String> loggedNestedClasspathRootPrefixes = null;
            String prevParentRelativePath = null;
            ScanSpec.ScanSpecPathMatch prevParentMatchStatus = null;

            for (FastZipEntry zipEntry : this.logicalZipFile.entries) {
               String relativePath = zipEntry.entryNameUnversioned;
               if (relativePath.startsWith("META-INF/versions/")) {
                  if (subLog != null) {
                     if (VersionFinder.JAVA_MAJOR_VERSION < 9) {
                        subLog.log(
                           "Skipping versioned entry in jar, because JRE version "
                              + VersionFinder.JAVA_MAJOR_VERSION
                              + " does not support this: "
                              + relativePath
                        );
                     } else {
                        subLog.log(
                           "Found unexpected versioned entry in jar (the jar's manifest file may be missing the \"Multi-Release\" key) -- skipping: "
                              + relativePath
                        );
                     }
                  }
               } else if (!isModularJar || relativePath.indexOf(47) >= 0 || !relativePath.endsWith(".class") || relativePath.equals("module-info.class")) {
                  if (this.nestedClasspathRootPrefixes != null) {
                     boolean reachedNestedRoot = false;

                     for (String nestedClasspathRoot : this.nestedClasspathRootPrefixes) {
                        if (relativePath.startsWith(nestedClasspathRoot)) {
                           if (subLog != null) {
                              if (loggedNestedClasspathRootPrefixes == null) {
                                 loggedNestedClasspathRootPrefixes = new HashSet<>();
                              }

                              if (loggedNestedClasspathRootPrefixes.add(nestedClasspathRoot)) {
                                 subLog.log("Reached nested classpath root, stopping recursion to avoid duplicate scanning: " + nestedClasspathRoot);
                              }
                           }

                           reachedNestedRoot = true;
                           break;
                        }
                     }

                     if (reachedNestedRoot) {
                        continue;
                     }
                  }

                  if (this.packageRootPrefix.isEmpty() || relativePath.startsWith(this.packageRootPrefix)) {
                     if (!this.packageRootPrefix.isEmpty()) {
                        relativePath = relativePath.substring(this.packageRootPrefix.length());
                     } else {
                        for (int i = 0; i < ClassLoaderHandlerRegistry.AUTOMATIC_PACKAGE_ROOT_PREFIXES.length; i++) {
                           String packageRoot = ClassLoaderHandlerRegistry.AUTOMATIC_PACKAGE_ROOT_PREFIXES[i];
                           if (relativePath.startsWith(packageRoot)) {
                              relativePath = relativePath.substring(packageRoot.length());
                              String packageRootWithoutFinalSlash = packageRoot.endsWith("/")
                                 ? packageRoot.substring(0, packageRoot.length() - 1)
                                 : packageRoot;
                              this.strippedAutomaticPackageRootPrefixes.add(packageRootWithoutFinalSlash);
                           }
                        }
                     }

                     if (this.checkResourcePathAcceptReject(relativePath, log)) {
                        int lastSlashIdx = relativePath.lastIndexOf(47);
                        String parentRelativePath = lastSlashIdx < 0 ? "/" : relativePath.substring(0, lastSlashIdx + 1);
                        boolean parentRelativePathChanged = !parentRelativePath.equals(prevParentRelativePath);
                        ScanSpec.ScanSpecPathMatch parentMatchStatus = parentRelativePathChanged
                           ? this.scanSpec.dirAcceptMatchStatus(parentRelativePath)
                           : prevParentMatchStatus;
                        prevParentRelativePath = parentRelativePath;
                        prevParentMatchStatus = parentMatchStatus;
                        if (parentMatchStatus == ScanSpec.ScanSpecPathMatch.HAS_REJECTED_PATH_PREFIX) {
                           if (subLog != null) {
                              subLog.log("Skipping rejected path: " + relativePath);
                           }
                        } else {
                           Resource resource = this.newResource(zipEntry, relativePath);
                           if (this.relativePathToResource.putIfAbsent(relativePath, resource) == null) {
                              if (parentMatchStatus != ScanSpec.ScanSpecPathMatch.HAS_ACCEPTED_PATH_PREFIX
                                 && parentMatchStatus != ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_PATH
                                 && (
                                    parentMatchStatus != ScanSpec.ScanSpecPathMatch.AT_ACCEPTED_CLASS_PACKAGE
                                       || !this.scanSpec.classfileIsSpecificallyAccepted(relativePath)
                                 )) {
                                 if (this.scanSpec.enableClassInfo && relativePath.equals("module-info.class")) {
                                    this.addAcceptedResource(resource, parentMatchStatus, true, subLog);
                                 }
                              } else {
                                 this.addAcceptedResource(resource, parentMatchStatus, false, subLog);
                              }
                           }
                        }
                     }
                  }
               }
            }

            File zipfile = this.getFile();
            if (zipfile != null) {
               this.fileToLastModified.put(zipfile, zipfile.lastModified());
            }

            this.finishScanPaths(subLog);
         }
      }
   }

   @Override
   public String getModuleName() {
      String moduleName = this.moduleNameFromModuleDescriptor;
      if (moduleName == null || moduleName.isEmpty()) {
         moduleName = this.moduleNameFromManifestFile;
      }

      if (moduleName == null || moduleName.isEmpty()) {
         if (this.derivedAutomaticModuleName == null) {
            this.derivedAutomaticModuleName = JarUtils.derivedAutomaticModuleName(this.zipFilePath);
         }

         moduleName = this.derivedAutomaticModuleName;
      }

      return moduleName != null && !moduleName.isEmpty() ? moduleName : null;
   }

   String getZipFilePath() {
      return this.packageRootPrefix.isEmpty()
         ? this.zipFilePath
         : this.zipFilePath + "!/" + this.packageRootPrefix.substring(0, this.packageRootPrefix.length() - 1);
   }

   @Override
   URI getURI() {
      try {
         return new URI(URLPathEncoder.normalizeURLPath(this.getZipFilePath()));
      } catch (URISyntaxException var2) {
         throw new IllegalArgumentException("Could not form URI: " + var2);
      }
   }

   @Override
   List<URI> getAllURIs() {
      if (this.strippedAutomaticPackageRootPrefixes.isEmpty()) {
         return Collections.singletonList(this.getURI());
      } else {
         URI uri = this.getURI();
         List<URI> uris = new ArrayList<>();
         uris.add(uri);
         String uriStr = uri.toString();

         for (String prefix : this.strippedAutomaticPackageRootPrefixes) {
            try {
               uris.add(new URI(uriStr + "!/" + prefix));
            } catch (URISyntaxException var7) {
            }
         }

         return uris;
      }
   }

   @Override
   File getFile() {
      if (this.logicalZipFile != null) {
         return this.logicalZipFile.getPhysicalFile();
      } else {
         int plingIdx = this.rawPath.indexOf(33);
         String outermostZipFilePathResolved = FastPathResolver.resolve(
            FileUtils.currDirPath(), plingIdx < 0 ? this.rawPath : this.rawPath.substring(0, plingIdx)
         );
         return new File(outermostZipFilePathResolved);
      }
   }

   @Override
   public String toString() {
      return this.getZipFilePath();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      } else if (!(obj instanceof ClasspathElementZip)) {
         return false;
      } else {
         ClasspathElementZip other = (ClasspathElementZip)obj;
         return this.getZipFilePath().equals(other.getZipFilePath());
      }
   }

   @Override
   public int hashCode() {
      return this.getZipFilePath().hashCode();
   }
}
