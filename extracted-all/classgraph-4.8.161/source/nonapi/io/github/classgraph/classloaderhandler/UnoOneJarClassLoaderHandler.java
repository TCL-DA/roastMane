package nonapi.io.github.classgraph.classloaderhandler;

import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class UnoOneJarClassLoaderHandler implements ClassLoaderHandler {
   private UnoOneJarClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "com.needhamsoftware.unojar.JarClassLoader".equals(classLoaderClass.getName())
         || "com.simontuffs.onejar.JarClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      String unoJarOneJarPath = (String)classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getOneJarPath");
      classpathOrder.addClasspathEntry(unoJarOneJarPath, classLoader, scanSpec, log);
      String unoJarJarPath = System.getProperty("uno-jar.jar.path");
      classpathOrder.addClasspathEntry(unoJarJarPath, classLoader, scanSpec, log);
      String oneJarJarPath = System.getProperty("one-jar.jar.path");
      classpathOrder.addClasspathEntry(oneJarJarPath, classLoader, scanSpec, log);
      String oneJarClassPath = System.getProperty("one-jar.class.path");
      if (oneJarClassPath != null) {
         classpathOrder.addClasspathEntryObject(oneJarClassPath.split("\\|"), classLoader, scanSpec, log);
      }
   }
}
