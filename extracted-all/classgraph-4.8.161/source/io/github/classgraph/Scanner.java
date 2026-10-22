package io.github.classgraph;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import nonapi.io.github.classgraph.classpath.ClasspathFinder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.classpath.ModuleFinder;
import nonapi.io.github.classgraph.concurrency.AutoCloseableExecutorService;
import nonapi.io.github.classgraph.concurrency.InterruptionChecker;
import nonapi.io.github.classgraph.concurrency.SingletonMap;
import nonapi.io.github.classgraph.concurrency.WorkQueue;
import nonapi.io.github.classgraph.fastzipfilereader.NestedJarHandler;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.CollectionUtils;
import nonapi.io.github.classgraph.utils.FastPathResolver;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.JarUtils;
import nonapi.io.github.classgraph.utils.LogNode;

class Scanner implements Callable<ScanResult> {
   private final ScanSpec scanSpec;
   public boolean performScan;
   private final NestedJarHandler nestedJarHandler;
   private final ExecutorService executorService;
   private final InterruptionChecker interruptionChecker;
   private final int numParallelTasks;
   private final ClassGraph.ScanResultProcessor scanResultProcessor;
   private final ClassGraph.FailureHandler failureHandler;
   private final LogNode topLevelLog;
   private final ClasspathFinder classpathFinder;
   private final List<ClasspathElementModule> moduleOrder;
   private final SingletonMap<Object, ClasspathElement, IOException> classpathEntryObjToClasspathEntrySingletonMap = new SingletonMap<Object, ClasspathElement, IOException>(
      
   ) {
      public ClasspathElement newInstance(Object classpathEntryObj, LogNode log) throws IOException, InterruptedException {
         throw new IOException("Should not reach here");
      }
   };

   Scanner(
      boolean performScan,
      ScanSpec scanSpec,
      ExecutorService executorService,
      int numParallelTasks,
      ClassGraph.ScanResultProcessor scanResultProcessor,
      ClassGraph.FailureHandler failureHandler,
      ReflectionUtils reflectionUtils,
      LogNode topLevelLog
   ) throws InterruptedException {
      this.scanSpec = scanSpec;
      this.performScan = performScan;
      scanSpec.sortPrefixes();
      scanSpec.log(topLevelLog);
      if (topLevelLog != null) {
         if (scanSpec.pathAcceptReject != null && scanSpec.packagePrefixAcceptReject.isSpecificallyAccepted("")) {
            topLevelLog.log(
               "Note: There is no need to accept the root package (\"\") -- not accepting anything will have the same effect of causing all packages to be scanned"
            );
         }

         topLevelLog.log("Number of worker threads: " + numParallelTasks);
      }

      this.executorService = executorService;
      this.interruptionChecker = executorService instanceof AutoCloseableExecutorService
         ? ((AutoCloseableExecutorService)executorService).interruptionChecker
         : new InterruptionChecker();
      this.nestedJarHandler = new NestedJarHandler(scanSpec, this.interruptionChecker, reflectionUtils);
      this.numParallelTasks = numParallelTasks;
      this.scanResultProcessor = scanResultProcessor;
      this.failureHandler = failureHandler;
      this.topLevelLog = topLevelLog;
      LogNode classpathFinderLog = topLevelLog == null ? null : topLevelLog.log("Finding classpath");
      this.classpathFinder = new ClasspathFinder(scanSpec, reflectionUtils, classpathFinderLog);

      try {
         this.moduleOrder = new ArrayList<>();
         ModuleFinder moduleFinder = this.classpathFinder.getModuleFinder();
         if (moduleFinder != null) {
            List<ModuleRef> systemModuleRefs = moduleFinder.getSystemModuleRefs();
            ClassLoader[] classLoaderOrderRespectingParentDelegation = this.classpathFinder.getClassLoaderOrderRespectingParentDelegation();
            ClassLoader defaultClassLoader = classLoaderOrderRespectingParentDelegation != null && classLoaderOrderRespectingParentDelegation.length != 0
               ? classLoaderOrderRespectingParentDelegation[0]
               : null;
            if (systemModuleRefs != null) {
               for (ModuleRef systemModuleRef : systemModuleRefs) {
                  String moduleName = systemModuleRef.getName();
                  if ((!scanSpec.enableSystemJarsAndModules || !scanSpec.moduleAcceptReject.acceptAndRejectAreEmpty())
                     && !scanSpec.moduleAcceptReject.isSpecificallyAcceptedAndNotRejected(moduleName)) {
                     if (classpathFinderLog != null) {
                        classpathFinderLog.log("Skipping non-accepted or rejected system module: " + moduleName);
                     }
                  } else {
                     ClasspathElementModule classpathElementModule = new ClasspathElementModule(
                        systemModuleRef,
                        this.nestedJarHandler.moduleRefToModuleReaderProxyRecyclerMap,
                        new Scanner.ClasspathEntryWorkUnit(null, defaultClassLoader, null, this.moduleOrder.size(), ""),
                        scanSpec
                     );
                     this.moduleOrder.add(classpathElementModule);
                     classpathElementModule.open(null, classpathFinderLog);
                  }
               }
            }

            List<ModuleRef> nonSystemModuleRefs = moduleFinder.getNonSystemModuleRefs();
            if (nonSystemModuleRefs != null) {
               for (ModuleRef nonSystemModuleRef : nonSystemModuleRefs) {
                  String moduleName = nonSystemModuleRef.getName();
                  if (moduleName == null) {
                     moduleName = "";
                  }

                  if (scanSpec.moduleAcceptReject.isAcceptedAndNotRejected(moduleName)) {
                     ClasspathElementModule classpathElementModule = new ClasspathElementModule(
                        nonSystemModuleRef,
                        this.nestedJarHandler.moduleRefToModuleReaderProxyRecyclerMap,
                        new Scanner.ClasspathEntryWorkUnit(null, defaultClassLoader, null, this.moduleOrder.size(), ""),
                        scanSpec
                     );
                     this.moduleOrder.add(classpathElementModule);
                     classpathElementModule.open(null, classpathFinderLog);
                  } else if (classpathFinderLog != null) {
                     classpathFinderLog.log("Skipping non-accepted or rejected module: " + moduleName);
                  }
               }
            }
         }
      } catch (InterruptedException var19) {
         this.nestedJarHandler.close(null);
         throw var19;
      }
   }

   private static void findClasspathOrderRec(ClasspathElement currClasspathElement, Set<ClasspathElement> visitedClasspathElts, List<ClasspathElement> order) {
      if (visitedClasspathElts.add(currClasspathElement)) {
         if (!currClasspathElement.skipClasspathElement) {
            order.add(currClasspathElement);
         }

         for (ClasspathElement childClasspathElt : CollectionUtils.sortCopy(currClasspathElement.childClasspathElements)) {
            findClasspathOrderRec(childClasspathElt, visitedClasspathElts, order);
         }
      }
   }

   private List<ClasspathElement> findClasspathOrder(Set<ClasspathElement> toplevelClasspathElts) {
      List<ClasspathElement> toplevelClasspathEltsSorted = CollectionUtils.sortCopy(toplevelClasspathElts);
      Set<ClasspathElement> visitedClasspathElts = new HashSet<>();
      List<ClasspathElement> order = new ArrayList<>();

      for (ClasspathElement elt : toplevelClasspathEltsSorted) {
         findClasspathOrderRec(elt, visitedClasspathElts, order);
      }

      return order;
   }

   private <W> void processWorkUnits(Collection<W> workUnits, LogNode log, WorkQueue.WorkUnitProcessor<W> workUnitProcessor) throws InterruptedException, ExecutionException {
      WorkQueue.runWorkQueue(workUnits, this.executorService, this.interruptionChecker, this.numParallelTasks, log, workUnitProcessor);
      if (log != null) {
         log.addElapsedTime();
      }

      this.interruptionChecker.check();
   }

   private static Object normalizeClasspathEntry(Object classpathEntryObj) throws IOException {
      if (classpathEntryObj == null) {
         throw new IOException("Got null classpath entry object");
      } else {
         Object classpathEntryObjNormalized = classpathEntryObj;
         if (!(classpathEntryObj instanceof Path)) {
            classpathEntryObjNormalized = FastPathResolver.resolve(FileUtils.currDirPath(), classpathEntryObj.toString());
         }

         if (classpathEntryObjNormalized instanceof String) {
            String classpathEntStr = (String)classpathEntryObjNormalized;
            boolean isURL = JarUtils.URL_SCHEME_PATTERN.matcher(classpathEntStr).matches();
            boolean isMultiSection = classpathEntStr.contains("!");
            if (isURL || isMultiSection) {
               if (!isURL) {
                  classpathEntStr = "file:" + classpathEntStr;
               }

               if (isMultiSection) {
                  classpathEntStr = "jar:" + classpathEntStr;
                  classpathEntStr = classpathEntStr.replaceAll("!([^/])", "!/$1");
               }

               try {
                  URL classpathEntryURL = new URL(classpathEntStr);
                  classpathEntryObjNormalized = classpathEntryURL;
                  if (!isMultiSection) {
                     try {
                        String scheme = classpathEntryURL.getProtocol();
                        if (!"http".equals(scheme) && !"https".equals(scheme)) {
                           URI classpathEntryURI = classpathEntryURL.toURI();
                           classpathEntryObjNormalized = Paths.get(classpathEntryURI);
                        }
                     } catch (IllegalArgumentException | SecurityException | URISyntaxException var14) {
                     } catch (FileSystemNotFoundException var15) {
                     }
                  }
               } catch (MalformedURLException var16) {
                  try {
                     URI classpathEntryURI = new URI(classpathEntStr);
                     classpathEntryObjNormalized = classpathEntryURI;
                     String scheme = classpathEntryURI.getScheme();
                     if (!"http".equals(scheme) && !"https".equals(scheme)) {
                        classpathEntryObjNormalized = Paths.get(classpathEntryURI);
                     }
                  } catch (URISyntaxException var11) {
                     throw new IOException("Malformed URI: " + classpathEntryObjNormalized + " : " + var11);
                  } catch (SecurityException | IllegalArgumentException var12) {
                  } catch (FileSystemNotFoundException var13) {
                  }
               }
            }

            if (classpathEntryObjNormalized instanceof String) {
               try {
                  classpathEntryObjNormalized = new File((String)classpathEntryObjNormalized).toPath();
               } catch (Exception var10) {
                  try {
                     classpathEntryObjNormalized = Paths.get((String)classpathEntryObjNormalized);
                  } catch (InvalidPathException var9) {
                     throw new IOException("Malformed path: " + classpathEntryObj + " : " + var9);
                  }
               }
            }
         }

         if (classpathEntryObjNormalized instanceof Path) {
            try {
               classpathEntryObjNormalized = ((Path)classpathEntryObjNormalized).toRealPath();
            } catch (SecurityException | IOException var8) {
            }
         }

         return classpathEntryObjNormalized;
      }
   }

   private WorkQueue.WorkUnitProcessor<Scanner.ClasspathEntryWorkUnit> newClasspathEntryWorkUnitProcessor(
      final Set<ClasspathElement> allClasspathEltsOut, final Set<ClasspathElement> toplevelClasspathEltsOut
   ) {
      return new WorkQueue.WorkUnitProcessor<Scanner.ClasspathEntryWorkUnit>() {
         public void processWorkUnit(
            final Scanner.ClasspathEntryWorkUnit workUnit, final WorkQueue<Scanner.ClasspathEntryWorkUnit> workQueue, final LogNode log
         ) throws InterruptedException {
            try {
               workUnit.classpathEntryObj = Scanner.normalizeClasspathEntry(workUnit.classpathEntryObj);
               final boolean isJar;
               if (!(workUnit.classpathEntryObj instanceof URL) && !(workUnit.classpathEntryObj instanceof URI)) {
                  if (!(workUnit.classpathEntryObj instanceof Path)) {
                     throw new IOException(
                        "Got unexpected classpath entry object type " + workUnit.classpathEntryObj.getClass().getName() + " : " + workUnit.classpathEntryObj
                     );
                  }

                  Path path = (Path)workUnit.classpathEntryObj;
                  if ("JrtFileSystem".equals(path.getFileSystem().getClass().getSimpleName())) {
                     throw new IOException("Ignoring JrtFS filesystem path (modules are scanned using the JPMS API): " + path);
                  }

                  if (FileUtils.canReadAndIsFile(path)) {
                     isJar = true;
                  } else {
                     if (!FileUtils.canReadAndIsDir(path)) {
                        if (!FileUtils.canRead(path)) {
                           throw new IOException("Cannot read path: " + path);
                        }

                        throw new IOException("Not a file or directory: " + path);
                     }

                     isJar = false;
                  }
               } else {
                  isJar = true;
               }

               Scanner.this.classpathEntryObjToClasspathEntrySingletonMap
                  .get(
                     workUnit.classpathEntryObj,
                     log,
                     new SingletonMap.NewInstanceFactory<ClasspathElement, IOException>() {
                        public ClasspathElement newInstance() throws IOException, InterruptedException {
                           ClasspathElement classpathElement = (ClasspathElement)(isJar
                              ? new ClasspathElementZip(workUnit, Scanner.this.nestedJarHandler, Scanner.this.scanSpec)
                              : new ClasspathElementDir(workUnit, Scanner.this.nestedJarHandler, Scanner.this.scanSpec));
                           allClasspathEltsOut.add(classpathElement);
                           LogNode subLog = log == null ? null : log.log("Opening classpath element " + classpathElement);
                           classpathElement.open(workQueue, subLog);
                           if (workUnit.parentClasspathElement != null) {
                              workUnit.parentClasspathElement.childClasspathElements.add(classpathElement);
                           } else {
                              toplevelClasspathEltsOut.add(classpathElement);
                           }

                           return classpathElement;
                        }
                     }
                  );
            } catch (Exception var6) {
               if (log != null) {
                  log.log("Skipping invalid classpath entry " + workUnit.classpathEntryObj + " : " + (var6.getCause() == null ? var6 : var6.getCause()));
               }
            }
         }
      };
   }

   private void findNestedClasspathElements(List<SimpleEntry<String, ClasspathElement>> classpathElts, LogNode log) {
      CollectionUtils.sortIfNotEmpty(classpathElts, new Comparator<SimpleEntry<String, ClasspathElement>>() {
         public int compare(SimpleEntry<String, ClasspathElement> o1, SimpleEntry<String, ClasspathElement> o2) {
            return o1.getKey().compareTo(o2.getKey());
         }
      });

      for (int i = 0; i < classpathElts.size(); i++) {
         SimpleEntry<String, ClasspathElement> ei = classpathElts.get(i);
         String basePath = ei.getKey();
         int basePathLen = basePath.length();

         for (int j = i + 1; j < classpathElts.size(); j++) {
            SimpleEntry<String, ClasspathElement> ej = classpathElts.get(j);
            String comparePath = ej.getKey();
            int comparePathLen = comparePath.length();
            boolean foundNestedClasspathRoot = false;
            if (comparePath.startsWith(basePath) && comparePathLen > basePathLen) {
               char nextChar = comparePath.charAt(basePathLen);
               if (nextChar == '/' || nextChar == '!') {
                  String nestedClasspathRelativePath = comparePath.substring(basePathLen + 1);
                  if (nestedClasspathRelativePath.indexOf(33) < 0) {
                     foundNestedClasspathRoot = true;
                     ClasspathElement baseElement = ei.getValue();
                     if (baseElement.nestedClasspathRootPrefixes == null) {
                        baseElement.nestedClasspathRootPrefixes = new ArrayList<>();
                     }

                     baseElement.nestedClasspathRootPrefixes.add(nestedClasspathRelativePath + "/");
                     if (log != null) {
                        log.log(basePath + " is a prefix of the nested element " + comparePath);
                     }
                  }
               }
            }

            if (!foundNestedClasspathRoot) {
               break;
            }
         }
      }
   }

   private void preprocessClasspathElementsByType(List<ClasspathElement> finalTraditionalClasspathEltOrder, LogNode classpathFinderLog) {
      List<SimpleEntry<String, ClasspathElement>> classpathEltDirs = new ArrayList<>();
      List<SimpleEntry<String, ClasspathElement>> classpathEltZips = new ArrayList<>();

      for (ClasspathElement classpathElt : finalTraditionalClasspathEltOrder) {
         if (classpathElt instanceof ClasspathElementDir) {
            File file = classpathElt.getFile();
            String path = file == null ? classpathElt.toString() : file.getPath();
            classpathEltDirs.add(new SimpleEntry<>(path, classpathElt));
         } else if (classpathElt instanceof ClasspathElementZip) {
            ClasspathElementZip classpathEltZip = (ClasspathElementZip)classpathElt;
            classpathEltZips.add(new SimpleEntry<>(classpathEltZip.getZipFilePath(), classpathElt));
            if (classpathEltZip.logicalZipFile != null) {
               if (classpathEltZip.logicalZipFile.addExportsManifestEntryValue != null) {
                  for (String addExports : JarUtils.smartPathSplit(classpathEltZip.logicalZipFile.addExportsManifestEntryValue, ' ', this.scanSpec)) {
                     this.scanSpec.modulePathInfo.addExports.add(addExports + "=ALL-UNNAMED");
                  }
               }

               if (classpathEltZip.logicalZipFile.addOpensManifestEntryValue != null) {
                  for (String addOpens : JarUtils.smartPathSplit(classpathEltZip.logicalZipFile.addOpensManifestEntryValue, ' ', this.scanSpec)) {
                     this.scanSpec.modulePathInfo.addOpens.add(addOpens + "=ALL-UNNAMED");
                  }
               }

               if (classpathEltZip.logicalZipFile.automaticModuleNameManifestEntryValue != null) {
                  classpathEltZip.moduleNameFromManifestFile = classpathEltZip.logicalZipFile.automaticModuleNameManifestEntryValue;
               }
            }
         }
      }

      this.findNestedClasspathElements(classpathEltDirs, classpathFinderLog);
      this.findNestedClasspathElements(classpathEltZips, classpathFinderLog);
   }

   private void maskClassfiles(List<ClasspathElement> classpathElementOrder, LogNode maskLog) {
      Set<String> acceptedClasspathRelativePathsFound = new HashSet<>();

      for (int classpathIdx = 0; classpathIdx < classpathElementOrder.size(); classpathIdx++) {
         ClasspathElement classpathElement = classpathElementOrder.get(classpathIdx);
         classpathElement.maskClassfiles(classpathIdx, acceptedClasspathRelativePathsFound, maskLog);
      }

      if (maskLog != null) {
         maskLog.addElapsedTime();
      }
   }

   private ScanResult performScan(List<ClasspathElement> finalClasspathEltOrder, List<String> finalClasspathEltOrderStrs, ClasspathFinder classpathFinder) throws InterruptedException, ExecutionException {
      if (this.scanSpec.enableClassInfo) {
         this.maskClassfiles(finalClasspathEltOrder, this.topLevelLog == null ? null : this.topLevelLog.log("Masking classfiles"));
      }

      Map<File, Long> fileToLastModified = new HashMap<>();

      for (ClasspathElement classpathElement : finalClasspathEltOrder) {
         fileToLastModified.putAll(classpathElement.fileToLastModified);
      }

      Map<String, ClassInfo> classNameToClassInfo = new ConcurrentHashMap<>();
      Map<String, PackageInfo> packageNameToPackageInfo = new HashMap<>();
      Map<String, ModuleInfo> moduleNameToModuleInfo = new HashMap<>();
      if (this.scanSpec.enableClassInfo) {
         List<Scanner.ClassfileScanWorkUnit> classfileScanWorkItems = new ArrayList<>();
         Set<String> acceptedClassNamesFound = new HashSet<>();

         for (ClasspathElement classpathElement : finalClasspathEltOrder) {
            for (Resource resource : classpathElement.acceptedClassfileResources) {
               String className = JarUtils.classfilePathToClassName(resource.getPath());
               if (!acceptedClassNamesFound.add(className)
                  && !className.equals("module-info")
                  && !className.equals("package-info")
                  && !className.endsWith(".package-info")) {
                  throw new IllegalArgumentException(
                     "Class "
                        + className
                        + " should not have been scheduled more than once for scanning due to classpath masking -- please report this bug at: https://github.com/classgraph/classgraph/issues"
                  );
               }

               classfileScanWorkItems.add(new Scanner.ClassfileScanWorkUnit(classpathElement, resource, false));
            }
         }

         Queue<Classfile> scannedClassfiles = new ConcurrentLinkedQueue<>();
         Scanner.ClassfileScannerWorkUnitProcessor classfileWorkUnitProcessor = new Scanner.ClassfileScannerWorkUnitProcessor(
            this.scanSpec, finalClasspathEltOrder, Collections.unmodifiableSet(acceptedClassNamesFound), scannedClassfiles
         );
         this.processWorkUnits(
            classfileScanWorkItems, this.topLevelLog == null ? null : this.topLevelLog.log("Scanning classfiles"), classfileWorkUnitProcessor
         );
         LogNode linkLog = this.topLevelLog == null ? null : this.topLevelLog.log("Linking related classfiles");

         while (!scannedClassfiles.isEmpty()) {
            Classfile c = scannedClassfiles.remove();
            c.link(classNameToClassInfo, packageNameToPackageInfo, moduleNameToModuleInfo);
         }

         if (linkLog != null) {
            linkLog.addElapsedTime();
         }
      } else if (this.topLevelLog != null) {
         this.topLevelLog.log("Classfile scanning is disabled");
      }

      return new ScanResult(
         this.scanSpec,
         finalClasspathEltOrder,
         finalClasspathEltOrderStrs,
         classpathFinder,
         classNameToClassInfo,
         packageNameToPackageInfo,
         moduleNameToModuleInfo,
         fileToLastModified,
         this.nestedJarHandler,
         this.topLevelLog
      );
   }

   private ScanResult openClasspathElementsThenScan() throws InterruptedException, ExecutionException {
      List<Scanner.ClasspathEntryWorkUnit> rawClasspathEntryWorkUnits = new ArrayList<>();

      for (ClasspathOrder.ClasspathEntry rawClasspathEntry : this.classpathFinder.getClasspathOrder().getOrder()) {
         rawClasspathEntryWorkUnits.add(
            new Scanner.ClasspathEntryWorkUnit(rawClasspathEntry.classpathEntryObj, rawClasspathEntry.classLoader, null, rawClasspathEntryWorkUnits.size(), "")
         );
      }

      Set<ClasspathElement> allClasspathElts = Collections.newSetFromMap(new ConcurrentHashMap<>());
      Set<ClasspathElement> toplevelClasspathElts = Collections.newSetFromMap(new ConcurrentHashMap<>());
      this.processWorkUnits(
         rawClasspathEntryWorkUnits,
         this.topLevelLog == null ? null : this.topLevelLog.log("Opening classpath elements"),
         this.newClasspathEntryWorkUnitProcessor(allClasspathElts, toplevelClasspathElts)
      );
      List<ClasspathElement> classpathEltOrder = this.findClasspathOrder(toplevelClasspathElts);
      this.preprocessClasspathElementsByType(classpathEltOrder, this.topLevelLog == null ? null : this.topLevelLog.log("Finding nested classpath elements"));
      LogNode classpathOrderLog = this.topLevelLog == null ? null : this.topLevelLog.log("Final classpath element order:");
      int numElts = this.moduleOrder.size() + classpathEltOrder.size();
      List<ClasspathElement> finalClasspathEltOrder = new ArrayList<>(numElts);
      List<String> finalClasspathEltOrderStrs = new ArrayList<>(numElts);
      int classpathOrderIdx = 0;

      for (ClasspathElementModule classpathElt : this.moduleOrder) {
         classpathElt.classpathElementIdx = classpathOrderIdx++;
         finalClasspathEltOrder.add(classpathElt);
         finalClasspathEltOrderStrs.add(classpathElt.toString());
         if (classpathOrderLog != null) {
            ModuleRef moduleRef = classpathElt.getModuleRef();
            classpathOrderLog.log(moduleRef.toString());
         }
      }

      for (ClasspathElement classpathEltx : classpathEltOrder) {
         classpathEltx.classpathElementIdx = classpathOrderIdx++;
         finalClasspathEltOrder.add(classpathEltx);
         finalClasspathEltOrderStrs.add(classpathEltx.toString());
         if (classpathOrderLog != null) {
            classpathOrderLog.log(classpathEltx.toString());
         }
      }

      this.processWorkUnits(
         finalClasspathEltOrder,
         this.topLevelLog == null ? null : this.topLevelLog.log("Scanning classpath elements"),
         new WorkQueue.WorkUnitProcessor<ClasspathElement>() {
            public void processWorkUnit(ClasspathElement classpathElement, WorkQueue<ClasspathElement> workQueueIgnored, LogNode pathScanLog) throws InterruptedException {
               classpathElement.scanPaths(pathScanLog);
            }
         }
      );
      List<ClasspathElement> finalClasspathEltOrderFiltered = finalClasspathEltOrder;
      if (!this.scanSpec.classpathElementResourcePathAcceptReject.acceptIsEmpty()) {
         finalClasspathEltOrderFiltered = new ArrayList<>(finalClasspathEltOrder.size());

         for (ClasspathElement classpathElement : finalClasspathEltOrder) {
            if (classpathElement.containsSpecificallyAcceptedClasspathElementResourcePath) {
               finalClasspathEltOrderFiltered.add(classpathElement);
            }
         }
      }

      if (this.performScan) {
         return this.performScan(finalClasspathEltOrderFiltered, finalClasspathEltOrderStrs, this.classpathFinder);
      } else {
         if (this.topLevelLog != null) {
            this.topLevelLog.log("Only returning classpath elements (not performing a scan)");
         }

         return new ScanResult(
            this.scanSpec,
            finalClasspathEltOrderFiltered,
            finalClasspathEltOrderStrs,
            this.classpathFinder,
            null,
            null,
            null,
            null,
            this.nestedJarHandler,
            this.topLevelLog
         );
      }
   }

   public ScanResult call() throws InterruptedException, CancellationException, ExecutionException {
      ScanResult scanResult = null;
      long scanStart = System.currentTimeMillis();
      boolean removeTemporaryFilesAfterScan = this.scanSpec.removeTemporaryFilesAfterScan;

      try {
         scanResult = this.openClasspathElementsThenScan();
         if (this.topLevelLog != null) {
            this.topLevelLog.log("~", String.format("Total time: %.3f sec", (double)(System.currentTimeMillis() - scanStart) * 0.001));
            this.topLevelLog.flush();
         }

         if (this.scanResultProcessor != null) {
            try {
               this.scanResultProcessor.processScanResult(scanResult);
            } catch (Exception var8) {
               scanResult.close();
               throw new ExecutionException(var8);
            }

            scanResult.close();
         }
      } catch (Throwable var10) {
         Throwable e = var10;
         if (this.topLevelLog != null) {
            this.topLevelLog
               .log(
                  "~",
                  !(var10 instanceof InterruptedException) && !(var10 instanceof CancellationException)
                     ? (!(var10 instanceof ExecutionException) && !(var10 instanceof RuntimeException) ? var10.getMessage() : "Uncaught exception during scan")
                     : "Scan interrupted or canceled",
                  InterruptionChecker.getCause(var10)
               );
            this.topLevelLog.flush();
         }

         removeTemporaryFilesAfterScan = true;
         this.interruptionChecker.interrupt();
         if (this.failureHandler == null) {
            if (removeTemporaryFilesAfterScan) {
               this.nestedJarHandler.close(this.topLevelLog);
            }

            throw var10;
         }

         try {
            this.failureHandler.onFailure(e);
         } catch (Exception var9) {
            if (this.topLevelLog != null) {
               this.topLevelLog.log("~", "The failure handler threw an exception:", var9);
               this.topLevelLog.flush();
            }

            ExecutionException failureHandlerException = new ExecutionException("Exception while calling failure handler", var9);
            failureHandlerException.addSuppressed(var10);
            if (removeTemporaryFilesAfterScan) {
               this.nestedJarHandler.close(this.topLevelLog);
            }

            throw failureHandlerException;
         }
      }

      if (removeTemporaryFilesAfterScan) {
         this.nestedJarHandler.close(this.topLevelLog);
      }

      return scanResult;
   }

   static class ClassfileScanWorkUnit {
      private final ClasspathElement classpathElement;
      private final Resource classfileResource;
      private final boolean isExternalClass;

      ClassfileScanWorkUnit(ClasspathElement classpathElement, Resource classfileResource, boolean isExternalClass) {
         this.classpathElement = classpathElement;
         this.classfileResource = classfileResource;
         this.isExternalClass = isExternalClass;
      }
   }

   private static class ClassfileScannerWorkUnitProcessor implements WorkQueue.WorkUnitProcessor<Scanner.ClassfileScanWorkUnit> {
      private final ScanSpec scanSpec;
      private final List<ClasspathElement> classpathOrder;
      private final Set<String> acceptedClassNamesFound;
      private final Set<String> classNamesScheduledForExtendedScanning = Collections.newSetFromMap(new ConcurrentHashMap<>());
      private final Queue<Classfile> scannedClassfiles;
      private final ConcurrentHashMap<String, String> stringInternMap = new ConcurrentHashMap<>();

      public ClassfileScannerWorkUnitProcessor(
         ScanSpec scanSpec, List<ClasspathElement> classpathOrder, Set<String> acceptedClassNamesFound, Queue<Classfile> scannedClassfiles
      ) {
         this.scanSpec = scanSpec;
         this.classpathOrder = classpathOrder;
         this.acceptedClassNamesFound = acceptedClassNamesFound;
         this.scannedClassfiles = scannedClassfiles;
      }

      public void processWorkUnit(Scanner.ClassfileScanWorkUnit workUnit, WorkQueue<Scanner.ClassfileScanWorkUnit> workQueue, LogNode log) throws InterruptedException {
         LogNode subLog = workUnit.classfileResource.scanLog == null
            ? null
            : workUnit.classfileResource.scanLog.log(workUnit.classfileResource.getPath(), "Parsing classfile");

         try {
            Classfile classfile = new Classfile(
               workUnit.classpathElement,
               this.classpathOrder,
               this.acceptedClassNamesFound,
               this.classNamesScheduledForExtendedScanning,
               workUnit.classfileResource.getPath(),
               workUnit.classfileResource,
               workUnit.isExternalClass,
               this.stringInternMap,
               workQueue,
               this.scanSpec,
               subLog
            );
            this.scannedClassfiles.add(classfile);
            if (subLog != null) {
               subLog.addElapsedTime();
            }
         } catch (Classfile.SkipClassException var6) {
            if (subLog != null) {
               subLog.log(workUnit.classfileResource.getPath(), "Skipping classfile: " + var6.getMessage());
               subLog.addElapsedTime();
            }
         } catch (Classfile.ClassfileFormatException var7) {
            if (subLog != null) {
               subLog.log(workUnit.classfileResource.getPath(), "Invalid classfile: " + var7.getMessage());
               subLog.addElapsedTime();
            }
         } catch (IOException var8) {
            if (subLog != null) {
               subLog.log(workUnit.classfileResource.getPath(), "Could not read classfile: " + var8);
               subLog.addElapsedTime();
            }
         } catch (Exception var9) {
            if (subLog != null) {
               subLog.log(workUnit.classfileResource.getPath(), "Could not read classfile", var9);
               subLog.addElapsedTime();
            }
         }
      }
   }

   static class ClasspathEntryWorkUnit {
      Object classpathEntryObj;
      final ClassLoader classLoader;
      final ClasspathElement parentClasspathElement;
      final int classpathElementIdxWithinParent;
      final String packageRootPrefix;

      public ClasspathEntryWorkUnit(
         Object classpathEntryObj,
         ClassLoader classLoader,
         ClasspathElement parentClasspathElement,
         int classpathElementIdxWithinParent,
         String packageRootPrefix
      ) {
         this.classpathEntryObj = classpathEntryObj;
         this.classLoader = classLoader;
         this.parentClasspathElement = parentClasspathElement;
         this.classpathElementIdxWithinParent = classpathElementIdxWithinParent;
         this.packageRootPrefix = packageRootPrefix;
      }
   }
}
