package nonapi.io.github.classgraph.reflection;

import io.github.classgraph.ClassGraph;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Callable;

public final class ReflectionUtils {
   public ReflectionDriver reflectionDriver;
   private Class<?> accessControllerClass;
   private Class<?> privilegedActionClass;
   private Method accessControllerDoPrivileged;

   public ReflectionUtils() {
      if (ClassGraph.CIRCUMVENT_ENCAPSULATION == ClassGraph.CircumventEncapsulationMethod.NARCISSUS) {
         try {
            this.reflectionDriver = new NarcissusReflectionDriver();
         } catch (Throwable var4) {
            System.err.println("Could not load Narcissus reflection driver: " + var4);
         }
      } else if (ClassGraph.CIRCUMVENT_ENCAPSULATION == ClassGraph.CircumventEncapsulationMethod.JVM_DRIVER) {
         try {
            this.reflectionDriver = new JVMDriverReflectionDriver();
         } catch (Throwable var3) {
            System.err.println("Could not load JVM-Driver reflection driver: " + var3);
         }
      }

      if (this.reflectionDriver == null) {
         this.reflectionDriver = new StandardReflectionDriver();
      }

      try {
         this.accessControllerClass = this.reflectionDriver.findClass("java.security.AccessController");
         this.privilegedActionClass = this.reflectionDriver.findClass("java.security.PrivilegedAction");
         this.accessControllerDoPrivileged = this.reflectionDriver.findMethod(this.accessControllerClass, null, "doPrivileged", this.privilegedActionClass);
      } catch (Throwable var2) {
      }
   }

   public Object getFieldVal(boolean throwException, Object obj, Field field) throws IllegalArgumentException {
      if (this.reflectionDriver == null) {
         throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
      } else if (obj != null && field != null) {
         try {
            return this.reflectionDriver.getField(obj, field);
         } catch (Throwable var5) {
            if (throwException) {
               throw new IllegalArgumentException("Can't read field " + obj.getClass().getName() + "." + field.getName() + ": " + var5);
            } else {
               return null;
            }
         }
      } else if (throwException) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   public Object getFieldVal(boolean throwException, Object obj, String fieldName) throws IllegalArgumentException {
      if (this.reflectionDriver == null) {
         throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
      } else if (obj != null && fieldName != null) {
         try {
            return this.reflectionDriver.getField(obj, this.reflectionDriver.findInstanceField(obj, fieldName));
         } catch (Throwable var5) {
            if (throwException) {
               throw new IllegalArgumentException("Can't read field " + obj.getClass().getName() + "." + fieldName + ": " + var5);
            } else {
               return null;
            }
         }
      } else if (throwException) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   public Object getStaticFieldVal(boolean throwException, Class<?> cls, String fieldName) throws IllegalArgumentException {
      if (this.reflectionDriver == null) {
         throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
      } else if (cls != null && fieldName != null) {
         try {
            return this.reflectionDriver.getStaticField(this.reflectionDriver.findStaticField(cls, fieldName));
         } catch (Throwable var5) {
            if (throwException) {
               throw new IllegalArgumentException("Can't read field " + cls.getName() + "." + fieldName + ": " + var5);
            } else {
               return null;
            }
         }
      } else if (throwException) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   public Object invokeMethod(boolean throwException, Object obj, String methodName) throws IllegalArgumentException {
      if (this.reflectionDriver == null) {
         throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
      } else if (obj != null && methodName != null) {
         try {
            return this.reflectionDriver.invokeMethod(obj, this.reflectionDriver.findInstanceMethod(obj, methodName));
         } catch (Throwable var5) {
            if (throwException) {
               throw new IllegalArgumentException("Method \"" + methodName + "\" could not be invoked: " + var5);
            } else {
               return null;
            }
         }
      } else if (throwException) {
         throw new IllegalArgumentException("Unexpected null argument");
      } else {
         return null;
      }
   }

   public Object invokeMethod(boolean throwException, Object obj, String methodName, Class<?> argType, Object param) throws IllegalArgumentException {
      if (this.reflectionDriver == null) {
         throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
      } else if (obj != null && methodName != null && argType != null) {
         try {
            return this.reflectionDriver.invokeMethod(obj, this.reflectionDriver.findInstanceMethod(obj, methodName, argType), param);
         } catch (Throwable var7) {
            if (throwException) {
               throw new IllegalArgumentException("Method \"" + methodName + "\" could not be invoked: " + var7);
            } else {
               return null;
            }
         }
      } else if (throwException) {
         throw new IllegalArgumentException("Unexpected null argument");
      } else {
         return null;
      }
   }

   public Object invokeStaticMethod(boolean throwException, Class<?> cls, String methodName) throws IllegalArgumentException {
      if (this.reflectionDriver == null) {
         throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
      } else if (cls != null && methodName != null) {
         try {
            return this.reflectionDriver.invokeStaticMethod(this.reflectionDriver.findStaticMethod(cls, methodName));
         } catch (Throwable var5) {
            if (throwException) {
               throw new IllegalArgumentException("method \"" + methodName + "\" could not be invoked: " + var5);
            } else {
               return null;
            }
         }
      } else if (throwException) {
         throw new IllegalArgumentException("Unexpected null argument");
      } else {
         return null;
      }
   }

   public Object invokeStaticMethod(boolean throwException, Class<?> cls, String methodName, Class<?> argType, Object param) throws IllegalArgumentException {
      if (this.reflectionDriver == null) {
         throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
      } else if (cls != null && methodName != null && argType != null) {
         try {
            return this.reflectionDriver.invokeStaticMethod(this.reflectionDriver.findStaticMethod(cls, methodName, argType), param);
         } catch (Throwable var7) {
            if (throwException) {
               throw new IllegalArgumentException("method \"" + methodName + "\" could not be invoked: " + var7);
            } else {
               return null;
            }
         }
      } else if (throwException) {
         throw new IllegalArgumentException("Unexpected null argument");
      } else {
         return null;
      }
   }

   public Class<?> classForNameOrNull(String className) {
      if (this.reflectionDriver == null) {
         throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
      } else {
         try {
            return this.reflectionDriver.findClass(className);
         } catch (Throwable var3) {
            return null;
         }
      }
   }

   public Method staticMethodForNameOrNull(String className, String staticMethodName) {
      if (this.reflectionDriver == null) {
         throw new RuntimeException("Cannot use reflection after ScanResult has been closed");
      } else {
         try {
            return this.reflectionDriver.findStaticMethod(this.reflectionDriver.findClass(className), staticMethodName);
         } catch (Throwable var4) {
            return null;
         }
      }
   }

   public <T> T doPrivileged(Callable<T> callable) throws Throwable {
      if (this.accessControllerDoPrivileged != null) {
         Object privilegedAction = Proxy.newProxyInstance(
            this.privilegedActionClass.getClassLoader(),
            new Class[]{this.privilegedActionClass},
            new ReflectionUtils.PrivilegedActionInvocationHandler<>(callable)
         );
         return (T)this.accessControllerDoPrivileged.invoke(null, privilegedAction);
      } else {
         return callable.call();
      }
   }

   private class PrivilegedActionInvocationHandler<T> implements InvocationHandler {
      private final Callable<T> callable;

      public PrivilegedActionInvocationHandler(Callable<T> callable) {
         this.callable = callable;
      }

      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         return this.callable.call();
      }
   }
}
