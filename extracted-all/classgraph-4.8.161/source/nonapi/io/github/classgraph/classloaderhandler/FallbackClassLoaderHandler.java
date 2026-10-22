package nonapi.io.github.classgraph.classloaderhandler;

import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class FallbackClassLoaderHandler implements ClassLoaderHandler {
   private FallbackClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return true;
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      boolean valid = false;
      valid |= classpathOrder.addClasspathEntryObject(
         classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getClassPath"), classLoader, scanSpec, log
      );
      valid |= classpathOrder.addClasspathEntryObject(
         classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getClasspath"), classLoader, scanSpec, log
      );
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "classpath"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "classPath"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "cp"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "classpath"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "classPath"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "cp"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getPath"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getPaths"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "path"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "paths"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "paths"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "paths"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getDir"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getDirs"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "dir"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "dirs"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "dir"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "dirs"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getFile"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getFiles"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "file"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "files"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "file"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "files"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getJar"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getJars"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "jar"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "jars"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "jar"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "jars"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getURL"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getURLs"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getUrl"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getUrls"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "url"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "urls"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "url"), classLoader, scanSpec, log);
      valid |= classpathOrder.addClasspathEntryObject(classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "urls"), classLoader, scanSpec, log);
      if (log != null) {
         log.log("FallbackClassLoaderHandler " + (valid ? "found" : "did not find") + " classpath entries in unknown ClassLoader " + classLoader);
      }
   }
}
