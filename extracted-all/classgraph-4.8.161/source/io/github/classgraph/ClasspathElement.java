package io.github.classgraph;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

abstract class ClasspathElement implements Comparable<ClasspathElement> {
   int classpathElementIdx;
   List<String> nestedClasspathRootPrefixes;
   boolean skipClasspathElement;
   boolean containsSpecificallyAcceptedClasspathElementResourcePath;
   final int classpathElementIdxWithinParent;
   Collection<ClasspathElement> childClasspathElements = new ConcurrentLinkedQueue<>();
   protected final List<Resource> acceptedResources = new ArrayList<>();
   protected List<Resource> acceptedClassfileResources = new ArrayList<>();
   protected final Map<File, Long> fileToLastModified = new ConcurrentHashMap<>();
   protected final AtomicBoolean scanned = new AtomicBoolean(false);
   protected ClassLoader classLoader;
   protected String packageRootPrefix;
   String moduleNameFromModuleDescriptor;
   final ScanSpec scanSpec;

   ClasspathElement(Scanner.ClasspathEntryWorkUnit workUnit, ScanSpec scanSpec) {
      this.packageRootPrefix = workUnit.packageRootPrefix;
      this.classpathElementIdxWithinParent = workUnit.classpathElementIdxWithinParent;
      this.classLoader = workUnit.classLoader;
      this.scanSpec = scanSpec;
   }

   public int compareTo(ClasspathElement other) {
      return this.classpathElementIdxWithinParent - other.classpathElementIdxWithinParent;
   }

   ClassLoader getClassLoader() {
      return this.classLoader;
   }

   int getNumClassfileMatches() {
      return this.acceptedClassfileResources == null ? 0 : this.acceptedClassfileResources.size();
   }

   protected boolean checkResourcePathAcceptReject(String relativePath, LogNode log) {
      if (!this.scanSpec.classpathElementResourcePathAcceptReject.acceptAndRejectAreEmpty()) {
         if (this.scanSpec.classpathElementResourcePathAcceptReject.isRejected(relativePath)) {
            if (log != null) {
               log.log("Reached rejected classpath element resource path, stopping scanning: " + relativePath);
            }

            return false;
         }

         if (this.scanSpec.classpathElementResourcePathAcceptReject.isSpecificallyAccepted(relativePath)) {
            if (log != null) {
               log.log("Reached specifically accepted classpath element resource path: " + relativePath);
            }

            this.containsSpecificallyAcceptedClasspathElementResourcePath = true;
         }
      }

      return true;
   }

   void maskClassfiles(int classpathIdx, Set<String> classpathRelativePathsFound, LogNode log) {
      List<Resource> acceptedClassfileResourcesFiltered = new ArrayList<>(this.acceptedClassfileResources.size());
      boolean foundMasked = false;

      for (Resource res : this.acceptedClassfileResources) {
         String pathRelativeToPackageRoot = res.getPath();
         if (!pathRelativeToPackageRoot.equals("module-info.class")
            && !pathRelativeToPackageRoot.equals("package-info.class")
            && !pathRelativeToPackageRoot.endsWith("/package-info.class")
            && !classpathRelativePathsFound.add(pathRelativeToPackageRoot)) {
            foundMasked = true;
            if (log != null) {
               log.log(
                  String.format("%06d-1", classpathIdx),
                  "Ignoring duplicate (masked) class " + JarUtils.classfilePathToClassName(pathRelativeToPackageRoot) + " found at " + res
               );
            }
         } else {
            acceptedClassfileResourcesFiltered.add(res);
         }
      }

      if (foundMasked) {
         this.acceptedClassfileResources = acceptedClassfileResourcesFiltered;
      }
   }

   protected void addAcceptedResource(Resource resource, ScanSpec.ScanSpecPathMatch parentMatchStatus, boolean isClassfileOnly, LogNode log) {
      String path = resource.getPath();
      boolean isClassFile = FileUtils.isClassfile(path);
      boolean isAccepted = false;
      if (isClassFile) {
         if (this.scanSpec.enableClassInfo && !this.scanSpec.classfilePathAcceptReject.isRejected(path)) {
            this.acceptedClassfileResources.add(resource);
            isAccepted = true;
         }
      } else {
         isAccepted = true;
      }

      if (!isClassfileOnly) {
         this.acceptedResources.add(resource);
      }

      if (log != null && isAccepted) {
         String type = isClassFile ? "classfile" : "resource";
         String logStr;
         switch (parentMatchStatus) {
            case HAS_ACCEPTED_PATH_PREFIX:
               logStr = "Found " + type + " within subpackage of accepted package: ";
               break;
            case AT_ACCEPTED_PATH:
               logStr = "Found " + type + " within accepted package: ";
               break;
            case AT_ACCEPTED_CLASS_PACKAGE:
               logStr = "Found specifically-accepted " + type + ": ";
               break;
            default:
               logStr = "Found accepted " + type + ": ";
         }

         resource.scanLog = log.log(
            "0:" + path,
            logStr + path + (path.equals(resource.getPathRelativeToClasspathElement()) ? "" : " ; full path: " + resource.getPathRelativeToClasspathElement())
         );
      }
   }

   protected void finishScanPaths(LogNode log) {
      if (log != null) {
         if (this.acceptedResources.isEmpty() && this.acceptedClassfileResources.isEmpty()) {
            log.log(
               this.scanSpec.enableClassInfo ? "No accepted classfiles or resources found" : "Classfile scanning is disabled, and no accepted resources found"
            );
         } else if (this.acceptedResources.isEmpty()) {
            log.log("No accepted resources found");
         } else if (this.acceptedClassfileResources.isEmpty()) {
            log.log(this.scanSpec.enableClassInfo ? "No accepted classfiles found" : "Classfile scanning is disabled");
         }
      }

      if (log != null) {
         log.addElapsedTime();
      }
   }

   protected LogNode log(int classpathElementIdx, String msg, LogNode log) {
      return log.log(String.format("%07d", classpathElementIdx), msg);
   }

   protected LogNode log(int classpathElementIdx, String msg, Throwable t, LogNode log) {
      return log.log(String.format("%07d", classpathElementIdx), msg, t);
   }

   abstract void open(WorkQueue<Scanner.ClasspathEntryWorkUnit> var1, LogNode var2) throws InterruptedException;

   abstract void scanPaths(LogNode var1);

   abstract Resource getResource(String var1);

   abstract URI getURI();

   abstract List<URI> getAllURIs();

   abstract File getFile();

   abstract String getModuleName();
}
