package io.github.classgraph;

import java.io.Closeable;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nonapi.io.github.classgraph.classpath.ClasspathFinder;
import nonapi.io.github.classgraph.concurrency.AutoCloseableExecutorService;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.json.JSONDeserializer;
import nonapi.io.github.classgraph.json.JSONSerializer;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.AcceptReject;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.Assert;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

public final class ScanResult implements Closeable, AutoCloseable {
   private List<String> rawClasspathEltOrderStrs;
   private List<ClasspathElement> classpathOrder;
   private ResourceList allAcceptedResourcesCached;
   private final AtomicInteger getResourcesWithPathCallCount = new AtomicInteger();
   private Map<String, ResourceList> pathToAcceptedResourcesCached;
   Map<String, ClassInfo> classNameToClassInfo;
   private Map<String, PackageInfo> packageNameToPackageInfo;
   private Map<String, ModuleInfo> moduleNameToModuleInfo;
   private Map<File, Long> fileToLastModified;
   private boolean isObtainedFromDeserialization;
   private ClassGraphClassLoader classGraphClassLoader;
   ClasspathFinder classpathFinder;
   private NestedJarHandler nestedJarHandler;
   ScanSpec scanSpec;
   private final AtomicBoolean closed = new AtomicBoolean(false);
   protected ReflectionUtils reflectionUtils;
   private final LogNode topLevelLog;
   private final WeakReference<ScanResult> weakReference;
   private static Set<WeakReference<ScanResult>> nonClosedWeakReferences = Collections.newSetFromMap(new ConcurrentHashMap<>());
   private static final AtomicBoolean initialized = new AtomicBoolean(false);
   private static final String CURRENT_SERIALIZATION_FORMAT = "10";

   static void init(ReflectionUtils reflectionUtils) {
      if (!initialized.getAndSet(true)) {
         FileUtils.closeDirectByteBuffer(ByteBuffer.allocateDirect(32), reflectionUtils, null);
      }
   }

   ScanResult(
      ScanSpec scanSpec,
      List<ClasspathElement> classpathOrder,
      List<String> rawClasspathEltOrderStrs,
      ClasspathFinder classpathFinder,
      Map<String, ClassInfo> classNameToClassInfo,
      Map<String, PackageInfo> packageNameToPackageInfo,
      Map<String, ModuleInfo> moduleNameToModuleInfo,
      Map<File, Long> fileToLastModified,
      NestedJarHandler nestedJarHandler,
      LogNode topLevelLog
   ) {
      this.scanSpec = scanSpec;
      this.rawClasspathEltOrderStrs = rawClasspathEltOrderStrs;
      this.classpathOrder = classpathOrder;
      this.classpathFinder = classpathFinder;
      this.fileToLastModified = fileToLastModified;
      this.classNameToClassInfo = classNameToClassInfo;
      this.packageNameToPackageInfo = packageNameToPackageInfo;
      this.moduleNameToModuleInfo = moduleNameToModuleInfo;
      this.nestedJarHandler = nestedJarHandler;
      this.reflectionUtils = nestedJarHandler.reflectionUtils;
      this.topLevelLog = topLevelLog;
      if (classNameToClassInfo != null) {
         this.indexResourcesAndClassInfo(topLevelLog);
      }

      if (classNameToClassInfo != null) {
         Set<String> allRepeatableAnnotationNames = new HashSet<>();

         for (ClassInfo classInfo : classNameToClassInfo.values()) {
            if (classInfo.isAnnotation() && classInfo.annotationInfo != null) {
               AnnotationInfo repeatableMetaAnnotation = classInfo.annotationInfo.get("java.lang.annotation.Repeatable");
               if (repeatableMetaAnnotation != null) {
                  AnnotationParameterValueList vals = repeatableMetaAnnotation.getParameterValues();
                  if (!vals.isEmpty()) {
                     Object val = vals.getValue("value");
                     if (val instanceof AnnotationClassRef) {
                        AnnotationClassRef classRef = (AnnotationClassRef)val;
                        String repeatableAnnotationName = classRef.getName();
                        if (repeatableAnnotationName != null) {
                           allRepeatableAnnotationNames.add(repeatableAnnotationName);
                        }
                     }
                  }
               }
            }
         }

         if (!allRepeatableAnnotationNames.isEmpty()) {
            for (ClassInfo classInfox : classNameToClassInfo.values()) {
               classInfox.handleRepeatableAnnotations(allRepeatableAnnotationNames);
            }
         }
      }

      this.classGraphClassLoader = new ClassGraphClassLoader(this);
      this.weakReference = new WeakReference<>(this);
      nonClosedWeakReferences.add(this.weakReference);
   }

   private void indexResourcesAndClassInfo(LogNode log) {
      for (ClassInfo classInfo : this.classNameToClassInfo.values()) {
         classInfo.setScanResult(this);
      }

      if (this.scanSpec.enableInterClassDependencies) {
         for (ClassInfo ci : new ArrayList<>(this.classNameToClassInfo.values())) {
            Set<ClassInfo> refdClassesFiltered = new HashSet<>();

            for (ClassInfo refdClassInfo : ci.findReferencedClassInfo(log)) {
               if (refdClassInfo != null
                  && !ci.equals(refdClassInfo)
                  && !refdClassInfo.getName().equals("java.lang.Object")
                  && (!refdClassInfo.isExternalClass() || this.scanSpec.enableExternalClasses)) {
                  refdClassInfo.setScanResult(this);
                  refdClassesFiltered.add(refdClassInfo);
               }
            }

            ci.setReferencedClasses(new ClassInfoList(refdClassesFiltered, true));
         }
      }
   }

   public List<File> getClasspathFiles() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         List<File> classpathElementOrderFiles = new ArrayList<>();

         for (ClasspathElement classpathElement : this.classpathOrder) {
            File file = classpathElement.getFile();
            if (file != null) {
               classpathElementOrderFiles.add(file);
            }
         }

         return classpathElementOrderFiles;
      }
   }

   public String getClasspath() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         return JarUtils.pathElementsToPathStr(this.getClasspathFiles());
      }
   }

   public List<URI> getClasspathURIs() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         List<URI> classpathElementOrderURIs = new ArrayList<>();

         for (ClasspathElement classpathElement : this.classpathOrder) {
            try {
               for (URI uri : classpathElement.getAllURIs()) {
                  if (uri != null) {
                     classpathElementOrderURIs.add(uri);
                  }
               }
            } catch (IllegalArgumentException var6) {
            }
         }

         return classpathElementOrderURIs;
      }
   }

   public List<URL> getClasspathURLs() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         List<URL> classpathElementOrderURLs = new ArrayList<>();

         for (URI uri : this.getClasspathURIs()) {
            try {
               classpathElementOrderURLs.add(uri.toURL());
            } catch (MalformedURLException | IllegalArgumentException var5) {
            }
         }

         return classpathElementOrderURLs;
      }
   }

   public List<ModuleRef> getModules() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         List<ModuleRef> moduleRefs = new ArrayList<>();

         for (ClasspathElement classpathElement : this.classpathOrder) {
            if (classpathElement instanceof ClasspathElementModule) {
               moduleRefs.add(((ClasspathElementModule)classpathElement).getModuleRef());
            }
         }

         return moduleRefs;
      }
   }

   public ModulePathInfo getModulePathInfo() {
      this.scanSpec.modulePathInfo.getRuntimeInfo(this.reflectionUtils);
      return this.scanSpec.modulePathInfo;
   }

   public ResourceList getAllResources() {
      if (this.allAcceptedResourcesCached == null) {
         ResourceList acceptedResourcesList = new ResourceList();

         for (ClasspathElement classpathElt : this.classpathOrder) {
            acceptedResourcesList.addAll(classpathElt.acceptedResources);
         }

         this.allAcceptedResourcesCached = acceptedResourcesList;
      }

      return this.allAcceptedResourcesCached;
   }

   public Map<String, ResourceList> getAllResourcesAsMap() {
      if (this.pathToAcceptedResourcesCached == null) {
         Map<String, ResourceList> pathToAcceptedResourceListMap = new HashMap<>();

         for (Resource res : this.getAllResources()) {
            ResourceList resList = pathToAcceptedResourceListMap.get(res.getPath());
            if (resList == null) {
               pathToAcceptedResourceListMap.put(res.getPath(), resList = new ResourceList());
            }

            resList.add(res);
         }

         this.pathToAcceptedResourcesCached = pathToAcceptedResourceListMap;
      }

      return this.pathToAcceptedResourcesCached;
   }

   public ResourceList getResourcesWithPath(String resourcePath) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         String path = FileUtils.sanitizeEntryPath(resourcePath, true, true);
         ResourceList matchingResources = null;
         if (this.getResourcesWithPathCallCount.incrementAndGet() > 3) {
            matchingResources = this.getAllResourcesAsMap().get(path);
         } else {
            for (ClasspathElement classpathElt : this.classpathOrder) {
               for (Resource res : classpathElt.acceptedResources) {
                  if (res.getPath().equals(path)) {
                     if (matchingResources == null) {
                        matchingResources = new ResourceList();
                     }

                     matchingResources.add(res);
                  }
               }
            }
         }

         return matchingResources == null ? ResourceList.EMPTY_LIST : matchingResources;
      }
   }

   public ResourceList getResourcesWithPathIgnoringAccept(String resourcePath) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         String path = FileUtils.sanitizeEntryPath(resourcePath, true, true);
         ResourceList matchingResources = new ResourceList();

         for (ClasspathElement classpathElt : this.classpathOrder) {
            Resource matchingResource = classpathElt.getResource(path);
            if (matchingResource != null) {
               matchingResources.add(matchingResource);
            }
         }

         return matchingResources;
      }
   }

   @Deprecated
   public ResourceList getResourcesWithPathIgnoringWhitelist(String resourcePath) {
      return this.getResourcesWithPathIgnoringAccept(resourcePath);
   }

   public ResourceList getResourcesWithLeafName(String leafName) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         ResourceList allAcceptedResources = this.getAllResources();
         if (allAcceptedResources.isEmpty()) {
            return ResourceList.EMPTY_LIST;
         } else {
            ResourceList filteredResources = new ResourceList();

            for (Resource classpathResource : allAcceptedResources) {
               String relativePath = classpathResource.getPath();
               int lastSlashIdx = relativePath.lastIndexOf(47);
               if (relativePath.substring(lastSlashIdx + 1).equals(leafName)) {
                  filteredResources.add(classpathResource);
               }
            }

            return filteredResources;
         }
      }
   }

   public ResourceList getResourcesWithExtension(String extension) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         ResourceList allAcceptedResources = this.getAllResources();
         if (allAcceptedResources.isEmpty()) {
            return ResourceList.EMPTY_LIST;
         } else {
            String bareExtension = extension;

            while (bareExtension.startsWith(".")) {
               bareExtension = bareExtension.substring(1);
            }

            ResourceList filteredResources = new ResourceList();

            for (Resource classpathResource : allAcceptedResources) {
               String relativePath = classpathResource.getPath();
               int lastSlashIdx = relativePath.lastIndexOf(47);
               int lastDotIdx = relativePath.lastIndexOf(46);
               if (lastDotIdx > lastSlashIdx && relativePath.substring(lastDotIdx + 1).equalsIgnoreCase(bareExtension)) {
                  filteredResources.add(classpathResource);
               }
            }

            return filteredResources;
         }
      }
   }

   public ResourceList getResourcesMatchingPattern(Pattern pattern) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         ResourceList allAcceptedResources = this.getAllResources();
         if (allAcceptedResources.isEmpty()) {
            return ResourceList.EMPTY_LIST;
         } else {
            ResourceList filteredResources = new ResourceList();

            for (Resource classpathResource : allAcceptedResources) {
               String relativePath = classpathResource.getPath();
               if (pattern.matcher(relativePath).matches()) {
                  filteredResources.add(classpathResource);
               }
            }

            return filteredResources;
         }
      }
   }

   public ResourceList getResourcesMatchingWildcard(String wildcardString) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         return this.getResourcesMatchingPattern(AcceptReject.globToPattern(wildcardString, false));
      }
   }

   public ModuleInfo getModuleInfo(String moduleName) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return this.moduleNameToModuleInfo.get(moduleName);
      }
   }

   public ModuleInfoList getModuleInfo() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return new ModuleInfoList(this.moduleNameToModuleInfo.values());
      }
   }

   public PackageInfo getPackageInfo(String packageName) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return this.packageNameToPackageInfo.get(packageName);
      }
   }

   public PackageInfoList getPackageInfo() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return new PackageInfoList(this.packageNameToPackageInfo.values());
      }
   }

   public Map<ClassInfo, ClassInfoList> getClassDependencyMap() {
      Map<ClassInfo, ClassInfoList> map = new HashMap<>();

      for (ClassInfo ci : this.getAllClasses()) {
         map.put(ci, ci.getClassDependencies());
      }

      return map;
   }

   public Map<ClassInfo, ClassInfoList> getReverseClassDependencyMap() {
      Map<ClassInfo, Set<ClassInfo>> revMapSet = new HashMap<>();

      for (ClassInfo ci : this.getAllClasses()) {
         for (ClassInfo dep : ci.getClassDependencies()) {
            Set<ClassInfo> set = revMapSet.get(dep);
            if (set == null) {
               revMapSet.put(dep, set = new HashSet<>());
            }

            set.add(ci);
         }
      }

      Map<ClassInfo, ClassInfoList> revMapList = new HashMap<>();

      for (Entry<ClassInfo, Set<ClassInfo>> ent : revMapSet.entrySet()) {
         revMapList.put(ent.getKey(), new ClassInfoList(ent.getValue(), true));
      }

      return revMapList;
   }

   public ClassInfo getClassInfo(String className) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return this.classNameToClassInfo.get(className);
      }
   }

   public ClassInfoList getAllClasses() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return ClassInfo.getAllClasses(this.classNameToClassInfo.values(), this.scanSpec);
      }
   }

   public ClassInfoList getAllEnums() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return ClassInfo.getAllEnums(this.classNameToClassInfo.values(), this.scanSpec);
      }
   }

   public ClassInfoList getAllRecords() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return ClassInfo.getAllRecords(this.classNameToClassInfo.values(), this.scanSpec);
      }
   }

   public Map<String, ClassInfo> getAllClassesAsMap() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return this.classNameToClassInfo;
      }
   }

   public ClassInfoList getAllStandardClasses() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return ClassInfo.getAllStandardClasses(this.classNameToClassInfo.values(), this.scanSpec);
      }
   }

   public ClassInfoList getSubclasses(Class<?> superclass) {
      return this.getSubclasses(superclass.getName());
   }

   public ClassInfoList getSubclasses(String superclassName) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else if (superclassName.equals("java.lang.Object")) {
         return this.getAllStandardClasses();
      } else {
         ClassInfo superclass = this.classNameToClassInfo.get(superclassName);
         return superclass == null ? ClassInfoList.EMPTY_LIST : superclass.getSubclasses();
      }
   }

   public ClassInfoList getSuperclasses(String subclassName) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         ClassInfo subclass = this.classNameToClassInfo.get(subclassName);
         return subclass == null ? ClassInfoList.EMPTY_LIST : subclass.getSuperclasses();
      }
   }

   public ClassInfoList getSuperclasses(Class<?> subclass) {
      return this.getSuperclasses(subclass.getName());
   }

   public ClassInfoList getClassesWithMethodAnnotation(Class<? extends Annotation> methodAnnotation) {
      Assert.isAnnotation(methodAnnotation);
      return this.getClassesWithMethodAnnotation(methodAnnotation.getName());
   }

   public ClassInfoList getClassesWithMethodAnnotation(String methodAnnotationName) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (this.scanSpec.enableClassInfo && this.scanSpec.enableMethodInfo && this.scanSpec.enableAnnotationInfo) {
         ClassInfo classInfo = this.classNameToClassInfo.get(methodAnnotationName);
         return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getClassesWithMethodAnnotation();
      } else {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo(), #enableMethodInfo(), and #enableAnnotationInfo() before #scan()");
      }
   }

   public ClassInfoList getClassesWithMethodParameterAnnotation(Class<? extends Annotation> methodParameterAnnotation) {
      Assert.isAnnotation(methodParameterAnnotation);
      return this.getClassesWithMethodParameterAnnotation(methodParameterAnnotation.getName());
   }

   public ClassInfoList getClassesWithMethodParameterAnnotation(String methodParameterAnnotationName) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (this.scanSpec.enableClassInfo && this.scanSpec.enableMethodInfo && this.scanSpec.enableAnnotationInfo) {
         ClassInfo classInfo = this.classNameToClassInfo.get(methodParameterAnnotationName);
         return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getClassesWithMethodParameterAnnotation();
      } else {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo(), #enableMethodInfo(), and #enableAnnotationInfo() before #scan()");
      }
   }

   public ClassInfoList getClassesWithFieldAnnotation(Class<? extends Annotation> fieldAnnotation) {
      Assert.isAnnotation(fieldAnnotation);
      return this.getClassesWithFieldAnnotation(fieldAnnotation.getName());
   }

   public ClassInfoList getClassesWithFieldAnnotation(String fieldAnnotationName) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (this.scanSpec.enableClassInfo && this.scanSpec.enableFieldInfo && this.scanSpec.enableAnnotationInfo) {
         ClassInfo classInfo = this.classNameToClassInfo.get(fieldAnnotationName);
         return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getClassesWithFieldAnnotation();
      } else {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo(), #enableFieldInfo(), and #enableAnnotationInfo() before #scan()");
      }
   }

   public ClassInfoList getAllInterfaces() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         return ClassInfo.getAllImplementedInterfaceClasses(this.classNameToClassInfo.values(), this.scanSpec);
      }
   }

   public ClassInfoList getInterfaces(String className) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         ClassInfo classInfo = this.classNameToClassInfo.get(className);
         return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getInterfaces();
      }
   }

   public ClassInfoList getInterfaces(Class<?> classRef) {
      return this.getInterfaces(classRef.getName());
   }

   public ClassInfoList getClassesImplementing(Class<?> interfaceClass) {
      Assert.isInterface(interfaceClass);
      return this.getClassesImplementing(interfaceClass.getName());
   }

   public ClassInfoList getClassesImplementing(String interfaceName) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         ClassInfo classInfo = this.classNameToClassInfo.get(interfaceName);
         return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getClassesImplementing();
      }
   }

   public ClassInfoList getAllAnnotations() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (this.scanSpec.enableClassInfo && this.scanSpec.enableAnnotationInfo) {
         return ClassInfo.getAllAnnotationClasses(this.classNameToClassInfo.values(), this.scanSpec);
      } else {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() and #enableAnnotationInfo() before #scan()");
      }
   }

   public ClassInfoList getAllInterfacesAndAnnotations() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (this.scanSpec.enableClassInfo && this.scanSpec.enableAnnotationInfo) {
         return ClassInfo.getAllInterfacesOrAnnotationClasses(this.classNameToClassInfo.values(), this.scanSpec);
      } else {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() and #enableAnnotationInfo() before #scan()");
      }
   }

   public ClassInfoList getClassesWithAnnotation(Class<? extends Annotation> annotation) {
      Assert.isAnnotation(annotation);
      return this.getClassesWithAnnotation(annotation.getName());
   }

   public ClassInfoList getClassesWithAnnotation(String annotationName) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (this.scanSpec.enableClassInfo && this.scanSpec.enableAnnotationInfo) {
         ClassInfo classInfo = this.classNameToClassInfo.get(annotationName);
         return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getClassesWithAnnotation();
      } else {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() and #enableAnnotationInfo() before #scan()");
      }
   }

   public ClassInfoList getAnnotationsOnClass(String className) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (this.scanSpec.enableClassInfo && this.scanSpec.enableAnnotationInfo) {
         ClassInfo classInfo = this.classNameToClassInfo.get(className);
         return classInfo == null ? ClassInfoList.EMPTY_LIST : classInfo.getAnnotations();
      } else {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() and #enableAnnotationInfo() before #scan()");
      }
   }

   public boolean classpathContentsModifiedSinceScan() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (this.fileToLastModified == null) {
         return true;
      } else {
         for (Entry<File, Long> ent : this.fileToLastModified.entrySet()) {
            if (ent.getKey().lastModified() != ent.getValue()) {
               return true;
            }
         }

         return false;
      }
   }

   public long classpathContentsLastModifiedTime() {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else {
         long maxLastModifiedTime = 0L;
         if (this.fileToLastModified != null) {
            long currTime = System.currentTimeMillis();

            for (long timestamp : this.fileToLastModified.values()) {
               if (timestamp > maxLastModifiedTime && timestamp < currTime) {
                  maxLastModifiedTime = timestamp;
               }
            }
         }

         return maxLastModifiedTime;
      }
   }

   ClassLoader[] getClassLoaderOrderRespectingParentDelegation() {
      return this.classpathFinder.getClassLoaderOrderRespectingParentDelegation();
   }

   public Class<?> loadClass(String className, boolean returnNullIfClassNotFound) throws IllegalArgumentException {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (className != null && !className.isEmpty()) {
         try {
            return Class.forName(className, this.scanSpec.initializeLoadedClasses, this.classGraphClassLoader);
         } catch (LinkageError | ClassNotFoundException var4) {
            if (returnNullIfClassNotFound) {
               return null;
            } else {
               throw new IllegalArgumentException("Could not load class " + className + " : " + var4, var4);
            }
         }
      } else {
         throw new NullPointerException("className cannot be null or empty");
      }
   }

   public <T> Class<T> loadClass(String className, Class<T> superclassOrInterfaceType, boolean returnNullIfClassNotFound) throws IllegalArgumentException {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (className == null || className.isEmpty()) {
         throw new NullPointerException("className cannot be null or empty");
      } else if (superclassOrInterfaceType == null) {
         throw new NullPointerException("superclassOrInterfaceType parameter cannot be null");
      } else {
         Class<?> loadedClass;
         try {
            loadedClass = Class.forName(className, this.scanSpec.initializeLoadedClasses, this.classGraphClassLoader);
         } catch (LinkageError | ClassNotFoundException var6) {
            if (returnNullIfClassNotFound) {
               return null;
            }

            throw new IllegalArgumentException("Could not load class " + className + " : " + var6);
         }

         if (loadedClass == null || superclassOrInterfaceType.isAssignableFrom(loadedClass)) {
            return (Class<T>)loadedClass;
         } else if (returnNullIfClassNotFound) {
            return null;
         } else {
            throw new IllegalArgumentException("Loaded class " + loadedClass.getName() + " cannot be cast to " + superclassOrInterfaceType.getName());
         }
      }
   }

   public static ScanResult fromJSON(String json) {
      Matcher matcher = Pattern.compile("\\{[\\n\\r ]*\"format\"[ ]?:[ ]?\"([^\"]+)\"").matcher(json);
      if (!matcher.find()) {
         throw new IllegalArgumentException("JSON is not in correct format");
      } else if (!"10".equals(matcher.group(1))) {
         throw new IllegalArgumentException(
            "JSON was serialized in a different format from the format used by the current version of ClassGraph -- please serialize and deserialize your ScanResult using the same version of ClassGraph"
         );
      } else {
         ScanResult.SerializationFormat deserialized = JSONDeserializer.deserializeObject(ScanResult.SerializationFormat.class, json);
         if (deserialized != null && deserialized.format.equals("10")) {
            ClassGraph classGraph = new ClassGraph();
            classGraph.scanSpec = deserialized.scanSpec;

            ScanResult scanResult;
            try (AutoCloseableExecutorService executorService = new AutoCloseableExecutorService(ClassGraph.DEFAULT_NUM_WORKER_THREADS)) {
               scanResult = classGraph.getClasspathScanResult(executorService);
            }

            scanResult.rawClasspathEltOrderStrs = deserialized.classpath;
            scanResult.scanSpec = deserialized.scanSpec;
            scanResult.classNameToClassInfo = new HashMap<>();
            if (deserialized.classInfo != null) {
               for (ClassInfo ci : deserialized.classInfo) {
                  scanResult.classNameToClassInfo.put(ci.getName(), ci);
                  ci.setScanResult(scanResult);
               }
            }

            scanResult.moduleNameToModuleInfo = new HashMap<>();
            if (deserialized.moduleInfo != null) {
               for (ModuleInfo mi : deserialized.moduleInfo) {
                  scanResult.moduleNameToModuleInfo.put(mi.getName(), mi);
               }
            }

            scanResult.packageNameToPackageInfo = new HashMap<>();
            if (deserialized.packageInfo != null) {
               for (PackageInfo pi : deserialized.packageInfo) {
                  scanResult.packageNameToPackageInfo.put(pi.getName(), pi);
               }
            }

            scanResult.indexResourcesAndClassInfo(null);
            scanResult.isObtainedFromDeserialization = true;
            return scanResult;
         } else {
            throw new IllegalArgumentException("JSON was serialized by newer version of ClassGraph");
         }
      }
   }

   public String toJSON(int indentWidth) {
      if (this.closed.get()) {
         throw new IllegalArgumentException("Cannot use a ScanResult after it has been closed");
      } else if (!this.scanSpec.enableClassInfo) {
         throw new IllegalArgumentException("Please call ClassGraph#enableClassInfo() before #scan()");
      } else {
         List<ClassInfo> allClassInfo = new ArrayList<>(this.classNameToClassInfo.values());
         CollectionUtils.sortIfNotEmpty(allClassInfo);
         List<PackageInfo> allPackageInfo = new ArrayList<>(this.packageNameToPackageInfo.values());
         CollectionUtils.sortIfNotEmpty(allPackageInfo);
         List<ModuleInfo> allModuleInfo = new ArrayList<>(this.moduleNameToModuleInfo.values());
         CollectionUtils.sortIfNotEmpty(allModuleInfo);
         return JSONSerializer.serializeObject(
            new ScanResult.SerializationFormat("10", this.scanSpec, allClassInfo, allPackageInfo, allModuleInfo, this.rawClasspathEltOrderStrs),
            indentWidth,
            false
         );
      }
   }

   public String toJSON() {
      return this.toJSON(0);
   }

   public boolean isObtainedFromDeserialization() {
      return this.isObtainedFromDeserialization;
   }

   @Override
   public void close() {
      if (!this.closed.getAndSet(true)) {
         nonClosedWeakReferences.remove(this.weakReference);
         if (this.classpathOrder != null) {
            this.classpathOrder.clear();
            this.classpathOrder = null;
         }

         if (this.allAcceptedResourcesCached != null) {
            for (Resource classpathResource : this.allAcceptedResourcesCached) {
               classpathResource.close();
            }

            this.allAcceptedResourcesCached.clear();
            this.allAcceptedResourcesCached = null;
         }

         if (this.pathToAcceptedResourcesCached != null) {
            this.pathToAcceptedResourcesCached.clear();
            this.pathToAcceptedResourcesCached = null;
         }

         this.classGraphClassLoader = null;
         if (this.classNameToClassInfo != null) {
         }

         if (this.packageNameToPackageInfo != null) {
            this.packageNameToPackageInfo.clear();
            this.packageNameToPackageInfo = null;
         }

         if (this.moduleNameToModuleInfo != null) {
            this.moduleNameToModuleInfo.clear();
            this.moduleNameToModuleInfo = null;
         }

         if (this.fileToLastModified != null) {
            this.fileToLastModified.clear();
            this.fileToLastModified = null;
         }

         if (this.nestedJarHandler != null) {
            this.nestedJarHandler.close(this.topLevelLog);
            this.nestedJarHandler = null;
         }

         this.classGraphClassLoader = null;
         this.classpathFinder = null;
         this.reflectionUtils = null;
         if (this.topLevelLog != null) {
            this.topLevelLog.flush();
         }
      }
   }

   public static void closeAll() {
      for (WeakReference<ScanResult> nonClosedWeakReference : new ArrayList<>(nonClosedWeakReferences)) {
         ScanResult scanResult = nonClosedWeakReference.get();
         if (scanResult != null) {
            scanResult.close();
         }
      }
   }

   private static class SerializationFormat {
      public String format;
      public ScanSpec scanSpec;
      public List<String> classpath;
      public List<ClassInfo> classInfo;
      public List<PackageInfo> packageInfo;
      public List<ModuleInfo> moduleInfo;

      public SerializationFormat() {
      }

      public SerializationFormat(
         String serializationFormatStr,
         ScanSpec scanSpec,
         List<ClassInfo> classInfo,
         List<PackageInfo> packageInfo,
         List<ModuleInfo> moduleInfo,
         List<String> classpath
      ) {
         this.format = serializationFormatStr;
         this.scanSpec = scanSpec;
         this.classpath = classpath;
         this.classInfo = classInfo;
         this.packageInfo = packageInfo;
         this.moduleInfo = moduleInfo;
      }
   }
}
