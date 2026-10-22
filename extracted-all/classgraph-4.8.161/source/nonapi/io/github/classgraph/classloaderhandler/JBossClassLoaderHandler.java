package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.FileUtils;
import nonapi.io.github.classgraph.utils.LogNode;

class JBossClassLoaderHandler implements ClassLoaderHandler {
   private JBossClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "org.jboss.modules.ModuleClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   private static void handleResourceLoader(Object resourceLoader, ClassLoader classLoader, ClasspathOrder classpathOrderOut, ScanSpec scanSpec, LogNode log) {
      if (resourceLoader != null) {
         Object root = classpathOrderOut.reflectionUtils.getFieldVal(false, resourceLoader, "root");
         File physicalFile = (File)classpathOrderOut.reflectionUtils.invokeMethod(false, root, "getPhysicalFile");
         String path = null;
         if (physicalFile != null) {
            String name = (String)classpathOrderOut.reflectionUtils.invokeMethod(false, root, "getName");
            if (name != null) {
               File file = new File(physicalFile.getParentFile(), name);
               if (FileUtils.canRead(file)) {
                  path = file.getAbsolutePath();
               } else {
                  path = physicalFile.getAbsolutePath();
               }
            } else {
               path = physicalFile.getAbsolutePath();
            }
         } else {
            path = (String)classpathOrderOut.reflectionUtils.invokeMethod(false, root, "getPathName");
            if (path == null) {
               File file = root instanceof Path ? ((Path)root).toFile() : (root instanceof File ? (File)root : null);
               if (file != null) {
                  path = file.getAbsolutePath();
               }
            }
         }

         if (path == null) {
            File file = (File)classpathOrderOut.reflectionUtils.getFieldVal(false, resourceLoader, "fileOfJar");
            if (file != null) {
               path = file.getAbsolutePath();
            }
         }

         if (path != null) {
            classpathOrderOut.addClasspathEntry(path, classLoader, scanSpec, log);
         } else if (log != null) {
            log.log("Could not determine classpath for ResourceLoader: " + resourceLoader);
         }
      }
   }

   private static void handleRealModule(
      Object module, Set<Object> visitedModules, ClassLoader classLoader, ClasspathOrder classpathOrderOut, ScanSpec scanSpec, LogNode log
   ) {
      if (visitedModules.add(module)) {
         ClassLoader moduleLoader = (ClassLoader)classpathOrderOut.reflectionUtils.invokeMethod(false, module, "getClassLoader");
         if (moduleLoader == null) {
            moduleLoader = classLoader;
         }

         Object vfsResourceLoaders = classpathOrderOut.reflectionUtils.invokeMethod(false, moduleLoader, "getResourceLoaders");
         if (vfsResourceLoaders != null) {
            int i = 0;

            for (int n = Array.getLength(vfsResourceLoaders); i < n; i++) {
               Object resourceLoader = Array.get(vfsResourceLoaders, i);
               handleResourceLoader(resourceLoader, moduleLoader, classpathOrderOut, scanSpec, log);
            }
         }
      }
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      Object module = classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getModule");
      Object callerModuleLoader = classpathOrder.reflectionUtils.invokeMethod(false, module, "getCallerModuleLoader");
      Set<Object> visitedModules = new HashSet<>();
      Map<Object, Object> moduleMap = (Map<Object, Object>)classpathOrder.reflectionUtils.getFieldVal(false, callerModuleLoader, "moduleMap");

      for (Entry<Object, Object> ent : moduleMap != null ? moduleMap.entrySet() : Collections.emptySet()) {
         Object val = ent.getValue();
         Object realModule = classpathOrder.reflectionUtils.invokeMethod(false, val, "getModule");
         handleRealModule(realModule, visitedModules, classLoader, classpathOrder, scanSpec, log);
      }

      Map<String, List<?>> pathsMap = (Map<String, List<?>>)classpathOrder.reflectionUtils.invokeMethod(false, module, "getPaths");

      for (Entry<String, List<?>> ent : pathsMap.entrySet()) {
         for (Object localLoader : ent.getValue()) {
            Object moduleClassLoader = classpathOrder.reflectionUtils.getFieldVal(false, localLoader, "this$0");
            Object realModule = classpathOrder.reflectionUtils.getFieldVal(false, moduleClassLoader, "module");
            handleRealModule(realModule, visitedModules, classLoader, classpathOrder, scanSpec, log);
         }
      }
   }
}
