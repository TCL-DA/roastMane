package nonapi.io.github.classgraph.classloaderhandler;

import java.net.URL;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class JPMSClassLoaderHandler implements ClassLoaderHandler {
   private JPMSClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "jdk.internal.loader.ClassLoaders$AppClassLoader".equals(classLoaderClass.getName())
         || "jdk.internal.loader.BuiltinClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      Object ucpVal = classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "ucp");
      if (ucpVal != null) {
         URL[] urls = (URL[])classpathOrder.reflectionUtils.invokeMethod(false, ucpVal, "getURLs");
         classpathOrder.addClasspathEntryObject(urls, classLoader, scanSpec, log);
      }
   }
}
