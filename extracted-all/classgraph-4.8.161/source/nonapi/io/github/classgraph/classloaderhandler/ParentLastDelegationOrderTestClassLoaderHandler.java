package nonapi.io.github.classgraph.classloaderhandler;

import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class ParentLastDelegationOrderTestClassLoaderHandler implements ClassLoaderHandler {
   private ParentLastDelegationOrderTestClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "io.github.classgraph.issues.issue267.FakeRestartClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.add(classLoader, log);
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      String classpath = (String)classpathOrder.reflectionUtils.invokeMethod(true, classLoader, "getClasspath");
      classpathOrder.addClasspathEntry(classpath, classLoader, scanSpec, log);
   }
}
