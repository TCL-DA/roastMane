package nonapi.io.github.classgraph.classpath;

import java.util.LinkedHashSet;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.scanspec.ScanSpec;
import nonapi.io.github.classgraph.utils.LogNode;

public class ClassLoaderFinder {
   private final ClassLoader[] contextClassLoaders;

   public ClassLoader[] getContextClassLoaders() {
      return this.contextClassLoaders;
   }

   ClassLoaderFinder(ScanSpec scanSpec, ReflectionUtils reflectionUtils, LogNode log) {
      LinkedHashSet<ClassLoader> classLoadersUnique;
      LogNode classLoadersFoundLog;
      if (scanSpec.overrideClassLoaders == null) {
         classLoadersUnique = new LinkedHashSet<>();
         ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
         if (threadClassLoader != null) {
            classLoadersUnique.add(threadClassLoader);
         }

         ClassLoader currClassClassLoader = this.getClass().getClassLoader();
         if (currClassClassLoader != null) {
            classLoadersUnique.add(currClassClassLoader);
         }

         ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
         if (systemClassLoader != null) {
            classLoadersUnique.add(systemClassLoader);
         }

         try {
            Class<?>[] callStack = new CallStackReader(reflectionUtils).getClassContext(log);

            for (int i = callStack.length - 1; i >= 0; i--) {
               ClassLoader callerClassLoader = callStack[i].getClassLoader();
               if (callerClassLoader != null) {
                  classLoadersUnique.add(callerClassLoader);
               }
            }
         } catch (IllegalArgumentException var12) {
            if (log != null) {
               log.log("Could not get call stack", var12);
            }
         }

         if (scanSpec.addedClassLoaders != null) {
            classLoadersUnique.addAll(scanSpec.addedClassLoaders);
         }

         classLoadersFoundLog = log == null ? null : log.log("Found ClassLoaders:");
      } else {
         classLoadersUnique = new LinkedHashSet<>(scanSpec.overrideClassLoaders);
         classLoadersFoundLog = log == null ? null : log.log("Override ClassLoaders:");
      }

      if (classLoadersFoundLog != null) {
         for (ClassLoader classLoader : classLoadersUnique) {
            classLoadersFoundLog.log(classLoader.getClass().getName());
         }
      }

      this.contextClassLoaders = classLoadersUnique.toArray(new ClassLoader[0]);
   }
}
