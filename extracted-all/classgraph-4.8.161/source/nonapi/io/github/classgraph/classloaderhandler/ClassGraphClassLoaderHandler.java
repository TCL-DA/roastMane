package nonapi.io.github.classgraph.classloaderhandler;

import io.github.classgraph.ClassGraphClassLoader;
import java.net.URL;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class ClassGraphClassLoaderHandler implements ClassLoaderHandler {
   private ClassGraphClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      boolean matches = "io.github.classgraph.ClassGraphClassLoader".equals(classLoaderClass.getName());
      if (matches && log != null) {
         log.log(
            "Sharing a `ClassGraphClassLoader` between multiple nested scans is not advisable, because scan criteria may differ between scans. See: https://github.com/classgraph/classgraph/issues/485"
         );
      }

      return matches;
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      for (URL url : ((ClassGraphClassLoader)classLoader).getURLs()) {
         if (url != null) {
            classpathOrder.addClasspathEntry(url, classLoader, scanSpec, log);
         }
      }
   }
}
