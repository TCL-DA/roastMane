package nonapi.io.github.classgraph.classloaderhandler;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

class FelixClassLoaderHandler implements ClassLoaderHandler {
   private FelixClassLoaderHandler() {
   }

   public static boolean canHandle(Class<?> classLoaderClass, LogNode log) {
      return "org.apache.felix.framework.BundleWiringImpl$BundleClassLoaderJava5".equals(classLoaderClass.getName())
         || "org.apache.felix.framework.BundleWiringImpl$BundleClassLoader".equals(classLoaderClass.getName());
   }

   public static void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
      classLoaderOrder.delegateTo(classLoader.getParent(), true, log);
      classLoaderOrder.add(classLoader, log);
   }

   private static File getContentLocation(Object content, ReflectionUtils reflectionUtils) {
      return (File)reflectionUtils.invokeMethod(false, content, "getFile");
   }

   private static void addBundle(
      Object bundleWiring, ClassLoader classLoader, ClasspathOrder classpathOrderOut, Set<Object> bundles, ScanSpec scanSpec, LogNode log
   ) {
      bundles.add(bundleWiring);
      Object revision = classpathOrderOut.reflectionUtils.invokeMethod(false, bundleWiring, "getRevision");
      Object content = classpathOrderOut.reflectionUtils.invokeMethod(false, revision, "getContent");
      File location = content != null ? getContentLocation(content, classpathOrderOut.reflectionUtils) : null;
      if (location != null) {
         classpathOrderOut.addClasspathEntry(location, classLoader, scanSpec, log);
         List<?> embeddedContent = (List<?>)classpathOrderOut.reflectionUtils.invokeMethod(false, revision, "getContentPath");
         if (embeddedContent != null) {
            for (Object embedded : embeddedContent) {
               if (embedded != content) {
                  File embeddedLocation = embedded != null ? getContentLocation(embedded, classpathOrderOut.reflectionUtils) : null;
                  if (embeddedLocation != null) {
                     classpathOrderOut.addClasspathEntry(embeddedLocation, classLoader, scanSpec, log);
                  }
               }
            }
         }
      }
   }

   public static void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
      Set<Object> bundles = new HashSet<>();
      Object bundleWiring = classpathOrder.reflectionUtils.getFieldVal(false, classLoader, "m_wiring");
      addBundle(bundleWiring, classLoader, classpathOrder, bundles, scanSpec, log);
      List<?> requiredWires = (List<?>)classpathOrder.reflectionUtils.invokeMethod(false, bundleWiring, "getRequiredWires", String.class, null);
      if (requiredWires != null) {
         for (Object wire : requiredWires) {
            Object provider = classpathOrder.reflectionUtils.invokeMethod(false, wire, "getProviderWiring");
            if (!bundles.contains(provider)) {
               addBundle(provider, classLoader, classpathOrder, bundles, scanSpec, log);
            }
         }
      }
   }
}
