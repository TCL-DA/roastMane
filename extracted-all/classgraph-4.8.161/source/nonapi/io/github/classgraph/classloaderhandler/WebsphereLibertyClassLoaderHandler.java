package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class WebsphereLibertyClassLoaderHandler implements ClassLoaderHandler {
   private static final String PKG_PREFIX = "com.ibm.ws.classloading.internal.";
   private static final String IBM_APP_CLASS_LOADER = "com.ibm.ws.classloading.internal.AppClassLoader";
   private static final String IBM_THREAD_CONTEXT_CLASS_LOADER = "com.ibm.ws.classloading.internal.ThreadContextClassLoader";

   private WebsphereLibertyClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "com.ibm.ws.classloading.internal.AppClassLoader".equals(classLoaderClass.getName())
         || "com.ibm.ws.classloading.internal.ThreadContextClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   private static Collection<Object> getPaths(Object containerClassLoader, ReflectionUtils reflectionUtils) {
      if (containerClassLoader == null) {
         return Collections.emptyList();
      } else {
         Collection<Object> urls = callGetUrls(containerClassLoader, "getContainerURLs", reflectionUtils);
         if (urls != null && !urls.isEmpty()) {
            return urls;
         } else {
            Object container = reflectionUtils.getFieldVal(false, containerClassLoader, "container");
            if (container == null) {
               return Collections.emptyList();
            } else {
               urls = callGetUrls(container, "getURLs", reflectionUtils);
               if (urls != null && !urls.isEmpty()) {
                  return urls;
               } else {
                  Object delegate = reflectionUtils.getFieldVal(false, container, "delegate");
                  if (delegate == null) {
                     return Collections.emptyList();
                  } else {
                     String path = (String)reflectionUtils.getFieldVal(false, delegate, "path");
                     if (path != null && path.length() > 0) {
                        return Collections.singletonList(path);
                     } else {
                        Object base = reflectionUtils.getFieldVal(false, delegate, "base");
                        if (base == null) {
                           return Collections.emptyList();
                        } else {
                           Object archiveFile = reflectionUtils.getFieldVal(false, base, "archiveFile");
                           if (archiveFile != null) {
                              File file = (File)archiveFile;
                              return Collections.singletonList(file.getAbsolutePath());
                           } else {
                              return Collections.emptyList();
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static Collection<Object> callGetUrls(Object container, String methodName, ReflectionUtils reflectionUtils) {
      if (container != null) {
         try {
            Collection<Object> results = (Collection<Object>)reflectionUtils.invokeMethod(false, container, methodName);
            if (results != null && !results.isEmpty()) {
               Collection<Object> allUrls = new HashSet<>();

               for (Object result : results) {
                  if (result instanceof Collection) {
                     for (Object url : (Collection)result) {
                        if (url != null) {
                           allUrls.add(url);
                        }
                     }
                  } else if (result != null) {
                     allUrls.add(result);
                  }
               }

               return allUrls;
            }
         } catch (UnsupportedOperationException var9) {
         }
      }

      return Collections.emptyList();
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      Object appLoader = classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "appLoader");
      Object smartClassPath;
      if (appLoader != null) {
         smartClassPath = classpathOrder.reflectionUtils.getFieldVal(false, appLoader, "smartClassPath");
      } else {
         smartClassPath = classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "smartClassPath");
      }

      if (smartClassPath != null) {
         Collection<Object> paths = callGetUrls(smartClassPath, "getClassPath", classpathOrder.reflectionUtils);
         if (!paths.isEmpty()) {
            for (Object path : paths) {
               classpathOrder.addClasspathEntry(path, classLoader, scanSpec, log);
            }
         } else {
            List<Object> classPathElements = (List<Object>)classpathOrder.reflectionUtils.getFieldVal(false, smartClassPath, "classPath");
            if (classPathElements != null && !classPathElements.isEmpty()) {
               for (Object classPathElement : classPathElements) {
                  for (Object path : getPaths(classPathElement, classpathOrder.reflectionUtils)) {
                     classpathOrder.addClasspathEntry(path, classLoader, scanSpec, log);
                  }
               }
            }
         }
      }
   }
}
