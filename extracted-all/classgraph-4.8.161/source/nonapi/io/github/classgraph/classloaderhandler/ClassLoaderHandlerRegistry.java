package nonapi.io.github.classgraph.classloaderhandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import nonapi.io.github.classgraph.classpath.ClassLoaderOrder;
import nonapi.io.github.classgraph.classpath.ClasspathOrder;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClassLoaderHandlerRegistry {
   public static final List<ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry> CLASS_LOADER_HANDLERS = Collections.unmodifiableList(
      Arrays.asList(
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(AntClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(EquinoxClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(EquinoxContextFinderClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(FelixClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(JBossClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(WeblogicClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(WebsphereLibertyClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(WebsphereTraditionalClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(OSGiDefaultClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(SpringBootRestartClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(TomcatWebappClassLoaderBaseHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(CxfContainerClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(PlexusClassWorldsClassRealmClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(QuarkusClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(UnoOneJarClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(ParentLastDelegationOrderTestClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(JPMSClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(URLClassLoaderHandler.class),
         new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(ClassGraphClassLoaderHandler.class)
      )
   );
   public static final ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry FALLBACK_HANDLER = new ClassLoaderHandlerRegistry.ClassLoaderHandlerRegistryEntry(
      FallbackClassLoaderHandler.class
   );
   public static final String[] AUTOMATIC_LIB_DIR_PREFIXES = new String[]{
      "BOOT-INF/lib/", "WEB-INF/lib/", "WEB-INF/lib-provided/", "META-INF/lib/", "lib/", "lib/ext/", "main/"
   };
   public static final String[] AUTOMATIC_PACKAGE_ROOT_PREFIXES = new String[]{"classes/", "test-classes/", "BOOT-INF/classes/", "WEB-INF/classes/"};

   private ClassLoaderHandlerRegistry() {
   }

   public static class ClassLoaderHandlerRegistryEntry {
      private final Method canHandleMethod;
      private final Method findClassLoaderOrderMethod;
      private final Method findClasspathOrderMethod;
      public final Class<? extends ClassLoaderHandler> classLoaderHandlerClass;

      private ClassLoaderHandlerRegistryEntry(Class<? extends ClassLoaderHandler> classLoaderHandlerClass) {
         this.classLoaderHandlerClass = classLoaderHandlerClass;

         try {
            this.canHandleMethod = classLoaderHandlerClass.getDeclaredMethod("canHandle", Class.class, LogNode.class);
         } catch (Exception var5) {
            throw new RuntimeException("Could not find canHandle method for " + classLoaderHandlerClass.getName(), var5);
         }

         try {
            this.findClassLoaderOrderMethod = classLoaderHandlerClass.getDeclaredMethod(
               "findClassLoaderOrder", ClassLoader.class, ClassLoaderOrder.class, LogNode.class
            );
         } catch (Exception var4) {
            throw new RuntimeException("Could not find findClassLoaderOrder method for " + classLoaderHandlerClass.getName(), var4);
         }

         try {
            this.findClasspathOrderMethod = classLoaderHandlerClass.getDeclaredMethod(
               "findClasspathOrder", ClassLoader.class, ClasspathOrder.class, ScanSpec.class, LogNode.class
            );
         } catch (Exception var3) {
            throw new RuntimeException("Could not find findClasspathOrder method for " + classLoaderHandlerClass.getName(), var3);
         }
      }

      public boolean canHandle(Class<?> classLoader, LogNode log) {
         try {
            return (Boolean)this.canHandleMethod.invoke(null, classLoader, log);
         } catch (Throwable var4) {
            throw new RuntimeException("Exception while calling canHandle for " + this.classLoaderHandlerClass.getName(), var4);
         }
      }

      public void findClassLoaderOrder(ClassLoader classLoader, ClassLoaderOrder classLoaderOrder, LogNode log) {
         try {
            this.findClassLoaderOrderMethod.invoke(null, classLoader, classLoaderOrder, log);
         } catch (Throwable var5) {
            throw new RuntimeException("Exception while calling findClassLoaderOrder for " + this.classLoaderHandlerClass.getName(), var5);
         }
      }

      public void findClasspathOrder(ClassLoader classLoader, ClasspathOrder classpathOrder, ScanSpec scanSpec, LogNode log) {
         try {
            this.findClasspathOrderMethod.invoke(null, classLoader, classpathOrder, scanSpec, log);
         } catch (Throwable var6) {
            throw new RuntimeException("Exception while calling findClassLoaderOrder for " + this.classLoaderHandlerClass.getName(), var6);
         }
      }
   }
}
