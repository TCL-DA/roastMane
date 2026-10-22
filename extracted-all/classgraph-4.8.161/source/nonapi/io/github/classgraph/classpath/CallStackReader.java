package nonapi.io.github.classgraph.classpath;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import nonapi.io.github.classgraph.reflection.ReflectionUtils;
import nonapi.io.github.classgraph.utils.LogNode;
import nonapi.io.github.classgraph.utils.VersionFinder;

class CallStackReader {
   ReflectionUtils reflectionUtils;

   public CallStackReader(ReflectionUtils reflectionUtils) {
      this.reflectionUtils = reflectionUtils;
   }

   private static Class<?>[] getCallStackViaStackWalker() {
      try {
         Class<?> consumerClass = Class.forName("java.util.function.Consumer");
         final List<Class<?>> stackFrameClasses = new ArrayList<>();
         Class<?> stackWalkerOptionClass = Class.forName("java.lang.StackWalker$Option");
         Object retainClassReference = Class.forName("java.lang.Enum")
            .getMethod("valueOf", Class.class, String.class)
            .invoke(null, stackWalkerOptionClass, "RETAIN_CLASS_REFERENCE");
         Class<?> stackWalkerClass = Class.forName("java.lang.StackWalker");
         Object stackWalkerInstance = stackWalkerClass.getMethod("getInstance", stackWalkerOptionClass).invoke(null, retainClassReference);
         final Method stackFrameGetDeclaringClassMethod = Class.forName("java.lang.StackWalker$StackFrame").getMethod("getDeclaringClass");
         stackWalkerClass.getMethod("forEach", consumerClass)
            .invoke(stackWalkerInstance, Proxy.newProxyInstance(consumerClass.getClassLoader(), new Class[]{consumerClass}, new InvocationHandler() {
               @Override
               public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                  Class<?> declaringClass = (Class<?>)stackFrameGetDeclaringClassMethod.invoke(args[0]);
                  stackFrameClasses.add(declaringClass);
                  return null;
               }
            }));
         return stackFrameClasses.toArray(new Class[0]);
      } catch (LinkageError | Exception var7) {
         return null;
      }
   }

   private static Class<?>[] getCallStackViaSecurityManager(LogNode log) {
      try {
         Class<?> securityManagerClass = Class.forName("java.lang.SecurityManager");
         Object securityManager = null;

         for (Constructor<?> constructor : securityManagerClass.getDeclaredConstructors()) {
            if (constructor.getParameterTypes().length == 0) {
               securityManager = constructor.newInstance();
               break;
            }
         }

         if (securityManager != null) {
            Method getClassContext = securityManager.getClass().getDeclaredMethod("getClassContext");
            getClassContext.setAccessible(true);
            return (Class<?>[])getClassContext.invoke(securityManager);
         } else {
            return null;
         }
      } catch (Throwable var7) {
         if (log != null) {
            log.log("Exception while trying to obtain call stack via SecurityManager", var7);
         }

         return null;
      }
   }

   Class<?>[] getClassContext(final LogNode log) {
      Class<?>[] callStack = null;
      if (VersionFinder.JAVA_MAJOR_VERSION != 9
         && VersionFinder.JAVA_MAJOR_VERSION != 10
         && (
            VersionFinder.JAVA_MAJOR_VERSION != 11
               || VersionFinder.JAVA_MINOR_VERSION != 0
               || VersionFinder.JAVA_SUB_VERSION >= 4 && (VersionFinder.JAVA_SUB_VERSION != 4 || !VersionFinder.JAVA_IS_EA_VERSION)
         )
         && (
            VersionFinder.JAVA_MAJOR_VERSION != 12
               || VersionFinder.JAVA_MINOR_VERSION != 0
               || VersionFinder.JAVA_SUB_VERSION >= 2 && (VersionFinder.JAVA_SUB_VERSION != 2 || !VersionFinder.JAVA_IS_EA_VERSION)
         )) {
         try {
            callStack = this.reflectionUtils.doPrivileged(new Callable<Class<?>[]>() {
               public Class<?>[] call() throws Exception {
                  return CallStackReader.getCallStackViaStackWalker();
               }
            });
         } catch (Throwable var13) {
         }
      }

      if (VersionFinder.JAVA_MAJOR_VERSION < 9 && (callStack == null || callStack.length == 0)) {
         try {
            callStack = this.reflectionUtils.doPrivileged(new Callable<Class<?>[]>() {
               public Class<?>[] call() throws Exception {
                  return CallStackReader.getCallStackViaSecurityManager(log);
               }
            });
         } catch (Throwable var12) {
         }
      }

      if (callStack == null || callStack.length == 0) {
         StackTraceElement[] stackTrace = null;

         try {
            stackTrace = Thread.currentThread().getStackTrace();
         } catch (SecurityException var11) {
         }

         if (stackTrace == null || stackTrace.length == 0) {
            try {
               throw new Exception();
            } catch (Exception var14) {
               stackTrace = var14.getStackTrace();
            }
         }

         List<Class<?>> stackClassesList = new ArrayList<>();

         for (StackTraceElement elt : stackTrace) {
            try {
               stackClassesList.add(Class.forName(elt.getClassName()));
            } catch (LinkageError | ClassNotFoundException var10) {
            }
         }

         if (!stackClassesList.isEmpty()) {
            callStack = stackClassesList.toArray(new Class[0]);
         }
      }

      if (callStack == null || callStack.length == 0) {
         callStack = new Class[]{CallStackReader.class};
      }

      return callStack;
   }
}
