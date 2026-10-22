package nonapi.io.github.classgraph.classloaderhandler;

import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class WeblogicClassLoaderHandler implements ClassLoaderHandler {
   private WeblogicClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "weblogic.utils.classloaders.ChangeAwareClassLoader".equals(classLoaderClass.getName())
         || "weblogic.utils.classloaders.GenericClassLoader".equals(classLoaderClass.getName())
         || "weblogic.utils.classloaders.FilteringClassLoader".equals(classLoaderClass.getName())
         || "weblogic.servlet.jsp.JspClassLoader".equals(classLoaderClass.getName())
         || "weblogic.servlet.jsp.TagFileClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      classpathOrder.addClasspathPathStr(
         (String)classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getFinderClassPath"), classLoader, scanSpec, log
      );
      classpathOrder.addClasspathPathStr((String)classpathOrder.reflectionUtils.invokeMethod(false, classLoader, "getClassPath"), classLoader, scanSpec, log);
   }
}
