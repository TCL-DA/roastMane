package nonapi.io.github.classgraph.classloaderhandler;

import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class CxfContainerClassLoaderHandler implements ClassLoaderHandler {
   private CxfContainerClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "org.apache.openejb.server.cxf.transport.util.CxfContainerClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      try {
         classLoaderOrder.delegateTo(Class.forName("org.apache.openejb.server.cxf.transport.util.CxfUtil").getClassLoader(), true, log);
      } catch (ClassNotFoundException | LinkageError var4) {
      }

      classLoaderOrder.delegateTo((ClassLoader)classLoaderOrder.reflectionUtils.invokeMethod(false, classLoader, "tccl"), false, log);
      classLoaderOrder.add(classLoader, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
   }
}
