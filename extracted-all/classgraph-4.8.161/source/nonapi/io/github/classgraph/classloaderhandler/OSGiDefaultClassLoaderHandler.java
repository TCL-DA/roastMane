package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class OSGiDefaultClassLoaderHandler implements ClassLoaderHandler {
   private OSGiDefaultClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "org.eclipse.osgi.internal.baseadaptor.DefaultClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      Object classpathManager = classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getClasspathManager");
      Object[] entries = (Object[])classpathOrder.reflectionUtils.getFieldVal(false, classpathManager, "entries");
      if (entries != null) {
         for (Object entry : entries) {
            Object bundleFile = classpathOrder.reflectionUtils.invokeMethod(false, entry, "getBundleFile");
            File baseFile = (File)classpathOrder.reflectionUtils.invokeMethod(false, bundleFile, "getBaseFile");
            if (baseFile != null) {
               classpathOrder.addClasspathEntry(baseFile.getPath(), classLoader, scanSpec, log);
            }
         }
      }
   }
}
