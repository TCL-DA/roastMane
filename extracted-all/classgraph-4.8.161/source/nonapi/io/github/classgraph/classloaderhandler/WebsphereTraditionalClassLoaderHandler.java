package nonapi.io.github.classgraph.classloaderhandler;

import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class WebsphereTraditionalClassLoaderHandler implements ClassLoaderHandler {
   private WebsphereTraditionalClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "com.ibm.ws.classloader.CompoundClassLoader".equals(classLoaderClass.getName())
         || "com.ibm.ws.classloader.ProtectionClassLoader".equals(classLoaderClass.getName())
         || "com.ibm.ws.bootstrap.ExtClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      String classpath = (String)classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getClassPath");
      classpathOrder.addClasspathPathStr(classpath, classLoader, scanSpec, log);
   }
}
