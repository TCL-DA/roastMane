package javassist.util.proxy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.List;
import javassist.CannotCompileException;
import javassist.bytecode.ClassFile;

public class DefineClassHelper {
   private static final DefineClassHelper.Helper privileged = (DefineClassHelper.Helper)(ClassFile.MAJOR_VERSION > 54
      ? new DefineClassHelper.Java11()
      : (
         ClassFile.MAJOR_VERSION >= 53
            ? new DefineClassHelper.Java9()
            : (ClassFile.MAJOR_VERSION >= 51 ? new DefineClassHelper.Java7() : new DefineClassHelper.JavaOther())
      ));

   public static Class<?> toClass(String className, Class<?> neighbor, ClassLoader loader, ProtectionDomain domain, byte[] bcode) throws CannotCompileException {
      try {
         return privileged.defineClass(className, bcode, 0, bcode.length, neighbor, loader, domain);
      } catch (RuntimeException var7) {
         throw var7;
      } catch (CannotCompileException var8) {
         throw var8;
      } catch (ClassFormatError var9) {
         Throwable t = var9.getCause();
         throw new CannotCompileException((Throwable)(t == null ? var9 : t));
      } catch (Exception var10) {
         throw new CannotCompileException(var10);
      }
   }

   public static Class<?> toClass(Class<?> neighbor, byte[] bcode) throws CannotCompileException {
      try {
         DefineClassHelper.class.getModule().addReads(neighbor.getModule());
         Lookup lookup = MethodHandles.lookup();
         Lookup prvlookup = MethodHandles.privateLookupIn(neighbor, lookup);
         return prvlookup.defineClass(bcode);
      } catch (IllegalArgumentException | IllegalAccessException var4) {
         throw new CannotCompileException(var4.getMessage() + ": " + neighbor.getName() + " has no permission to define the class");
      }
   }

   public static Class<?> toClass(Lookup lookup, byte[] bcode) throws CannotCompileException {
      try {
         return lookup.defineClass(bcode);
      } catch (IllegalArgumentException | IllegalAccessException var3) {
         throw new CannotCompileException(var3.getMessage());
      }
   }

   static Class<?> toPublicClass(String className, byte[] bcode) throws CannotCompileException {
      try {
         Lookup lookup = MethodHandles.lookup();
         lookup = lookup.dropLookupMode(2);
         return lookup.defineClass(bcode);
      } catch (Throwable var3) {
         throw new CannotCompileException(var3);
      }
   }

   private DefineClassHelper() {
   }

   private abstract static class Helper {
      private Helper() {
      }

      abstract Class<?> defineClass(String var1, byte[] var2, int var3, int var4, Class<?> var5, ClassLoader var6, ProtectionDomain var7) throws ClassFormatError, CannotCompileException;
   }

   private static class Java11 extends DefineClassHelper.JavaOther {
      private Java11() {
      }

      @Override
      Class<?> defineClass(String name, byte[] bcode, int off, int len, Class<?> neighbor, ClassLoader loader, ProtectionDomain protectionDomain) throws ClassFormatError, CannotCompileException {
         return neighbor != null ? DefineClassHelper.toClass(neighbor, bcode) : super.defineClass(name, bcode, off, len, neighbor, loader, protectionDomain);
      }
   }

   private static class Java7 extends DefineClassHelper.Helper {
      private final SecurityActions stack;
      private final MethodHandle defineClass;

      private Java7() {
         this.stack = SecurityActions.stack;
         this.defineClass = this.getDefineClassMethodHandle();
      }

      private final MethodHandle getDefineClassMethodHandle() {
         if (DefineClassHelper.privileged != null && this.stack.getCallerClass() != this.getClass()) {
            throw new IllegalAccessError("Access denied for caller.");
         } else {
            try {
               return SecurityActions.getMethodHandle(
                  ClassLoader.class, "defineClass", new Class[]{String.class, byte[].class, int.class, int.class, ProtectionDomain.class}
               );
            } catch (NoSuchMethodException var2) {
               throw new RuntimeException("cannot initialize", var2);
            }
         }
      }

      @Override
      Class<?> defineClass(String name, byte[] b, int off, int len, Class<?> neighbor, ClassLoader loader, ProtectionDomain protectionDomain) throws ClassFormatError {
         if (this.stack.getCallerClass() != DefineClassHelper.class) {
            throw new IllegalAccessError("Access denied for caller.");
         } else {
            try {
               return (Class<?>)this.defineClass.invokeWithArguments(loader, name, b, off, len, protectionDomain);
            } catch (Throwable var9) {
               if (var9 instanceof RuntimeException) {
                  throw (RuntimeException)var9;
               } else if (var9 instanceof ClassFormatError) {
                  throw (ClassFormatError)var9;
               } else {
                  throw new ClassFormatError(var9.getMessage());
               }
            }
         }
      }
   }

   private static class Java9 extends DefineClassHelper.Helper {
      private final Object stack;
      private final Method getCallerClass;
      private final DefineClassHelper.Java9.ReferencedUnsafe sunMiscUnsafe = this.getReferencedUnsafe();

      Java9() {
         // $VF: Couldn't be decompiled
         // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
         // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.gen.VarType.isGeneric()" because "newRet" is null
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInferredExprType(InvocationExprent.java:634)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ArrayExprent.getInferredExprType(ArrayExprent.java:45)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.NewExprent.getInferredExprType(NewExprent.java:119)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:966)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.appendParamList(InvocationExprent.java:1153)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.toJava(InvocationExprent.java:902)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:1018)
         //   at org.jetbrains.java.decompiler.modules.decompiler.exps.AssignmentExprent.toJava(AssignmentExprent.java:154)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:895)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:90)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.CatchStatement.toJava(CatchStatement.java:166)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.toJava(IfStatement.java:241)
         //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.jmpWrapper(ExprProcessor.java:833)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.SequenceStatement.toJava(SequenceStatement.java:107)
         //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
         //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1283)
         //
         // Bytecode:
         // 00: aload 0
         // 01: aconst_null
         // 02: invokespecial javassist/util/proxy/DefineClassHelper$Helper.<init> (Ljavassist/util/proxy/DefineClassHelper$1;)V
         // 05: aload 0
         // 06: aload 0
         // 07: invokespecial javassist/util/proxy/DefineClassHelper$Java9.getReferencedUnsafe ()Ljavassist/util/proxy/DefineClassHelper$Java9$ReferencedUnsafe;
         // 0a: putfield javassist/util/proxy/DefineClassHelper$Java9.sunMiscUnsafe Ljavassist/util/proxy/DefineClassHelper$Java9$ReferencedUnsafe;
         // 0d: aconst_null
         // 0e: astore 1
         // 0f: ldc "java.lang.StackWalker"
         // 11: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
         // 14: astore 1
         // 15: goto 19
         // 18: astore 2
         // 19: aload 1
         // 1a: ifnull 63
         // 1d: ldc "java.lang.StackWalker$Option"
         // 1f: invokestatic java/lang/Class.forName (Ljava/lang/String;)Ljava/lang/Class;
         // 22: astore 2
         // 23: aload 0
         // 24: aload 1
         // 25: ldc "getInstance"
         // 27: bipush 1
         // 28: anewarray 28
         // 2b: dup
         // 2c: bipush 0
         // 2d: aload 2
         // 2e: aastore
         // 2f: invokevirtual java/lang/Class.getMethod (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
         // 32: aconst_null
         // 33: bipush 1
         // 34: anewarray 43
         // 37: dup
         // 38: bipush 0
         // 39: aload 2
         // 3a: invokevirtual java/lang/Class.getEnumConstants ()[Ljava/lang/Object;
         // 3d: bipush 0
         // 3e: aaload
         // 3f: aastore
         // 40: invokevirtual java/lang/reflect/Method.invoke (Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
         // 43: putfield javassist/util/proxy/DefineClassHelper$Java9.stack Ljava/lang/Object;
         // 46: aload 0
         // 47: aload 1
         // 48: ldc "getCallerClass"
         // 4a: bipush 0
         // 4b: anewarray 28
         // 4e: invokevirtual java/lang/Class.getMethod (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
         // 51: putfield javassist/util/proxy/DefineClassHelper$Java9.getCallerClass Ljava/lang/reflect/Method;
         // 54: goto 6d
         // 57: astore 2
         // 58: new java/lang/RuntimeException
         // 5b: dup
         // 5c: ldc "cannot initialize"
         // 5e: aload 2
         // 5f: invokespecial java/lang/RuntimeException.<init> (Ljava/lang/String;Ljava/lang/Throwable;)V
         // 62: athrow
         // 63: aload 0
         // 64: aconst_null
         // 65: putfield javassist/util/proxy/DefineClassHelper$Java9.stack Ljava/lang/Object;
         // 68: aload 0
         // 69: aconst_null
         // 6a: putfield javassist/util/proxy/DefineClassHelper$Java9.getCallerClass Ljava/lang/reflect/Method;
         // 6d: return
      }

      private final DefineClassHelper.Java9.ReferencedUnsafe getReferencedUnsafe() {
         try {
            if (DefineClassHelper.privileged != null && this.getCallerClass.invoke(this.stack) != this.getClass()) {
               throw new IllegalAccessError("Access denied for caller.");
            }
         } catch (Exception var5) {
            throw new RuntimeException("cannot initialize", var5);
         }

         try {
            SecurityActions.TheUnsafe usf = SecurityActions.getSunMiscUnsafeAnonymously();
            List<Method> defineClassMethod = usf.methods.get("defineClass");
            if (null == defineClassMethod) {
               return null;
            } else {
               MethodHandle meth = MethodHandles.lookup().unreflect(defineClassMethod.get(0));
               return new DefineClassHelper.Java9.ReferencedUnsafe(usf, meth);
            }
         } catch (Throwable var4) {
            throw new RuntimeException("cannot initialize", var4);
         }
      }

      @Override
      Class<?> defineClass(String name, byte[] b, int off, int len, Class<?> neighbor, ClassLoader loader, ProtectionDomain protectionDomain) throws ClassFormatError {
         try {
            if (this.getCallerClass.invoke(this.stack) != DefineClassHelper.class) {
               throw new IllegalAccessError("Access denied for caller.");
            }
         } catch (Exception var9) {
            throw new RuntimeException("cannot initialize", var9);
         }

         return this.sunMiscUnsafe.defineClass(name, b, off, len, loader, protectionDomain);
      }

      final class ReferencedUnsafe {
         private final SecurityActions.TheUnsafe sunMiscUnsafeTheUnsafe;
         private final MethodHandle defineClass;

         ReferencedUnsafe(SecurityActions.TheUnsafe usf, MethodHandle meth) {
            this.sunMiscUnsafeTheUnsafe = usf;
            this.defineClass = meth;
         }

         Class<?> defineClass(String name, byte[] b, int off, int len, ClassLoader loader, ProtectionDomain protectionDomain) throws ClassFormatError {
            try {
               if (Java9.this.getCallerClass.invoke(Java9.this.stack) != DefineClassHelper.Java9.class) {
                  throw new IllegalAccessError("Access denied for caller.");
               }
            } catch (Exception var9) {
               throw new RuntimeException("cannot initialize", var9);
            }

            try {
               return (Class<?>)this.defineClass.invokeWithArguments(this.sunMiscUnsafeTheUnsafe.theUnsafe, name, b, off, len, loader, protectionDomain);
            } catch (Throwable var8) {
               if (var8 instanceof RuntimeException) {
                  throw (RuntimeException)var8;
               } else if (var8 instanceof ClassFormatError) {
                  throw (ClassFormatError)var8;
               } else {
                  throw new ClassFormatError(var8.getMessage());
               }
            }
         }
      }
   }

   private static class JavaOther extends DefineClassHelper.Helper {
      private final Method defineClass = this.getDefineClassMethod();
      private final SecurityActions stack;

      private JavaOther() {
         this.stack = SecurityActions.stack;
      }

      private final Method getDefineClassMethod() {
         if (DefineClassHelper.privileged != null && this.stack.getCallerClass() != this.getClass()) {
            throw new IllegalAccessError("Access denied for caller.");
         } else {
            try {
               return SecurityActions.getDeclaredMethod(
                  ClassLoader.class, "defineClass", new Class[]{String.class, byte[].class, int.class, int.class, ProtectionDomain.class}
               );
            } catch (NoSuchMethodException var2) {
               throw new RuntimeException("cannot initialize", var2);
            }
         }
      }

      @Override
      Class<?> defineClass(String name, byte[] b, int off, int len, Class<?> neighbor, ClassLoader loader, ProtectionDomain protectionDomain) throws ClassFormatError, CannotCompileException {
         Class<?> klass = this.stack.getCallerClass();
         if (klass != DefineClassHelper.class && klass != this.getClass()) {
            throw new IllegalAccessError("Access denied for caller.");
         } else {
            try {
               SecurityActions.setAccessible(this.defineClass, true);
               return (Class<?>)this.defineClass.invoke(loader, name, b, off, len, protectionDomain);
            } catch (Throwable var10) {
               if (var10 instanceof ClassFormatError) {
                  throw (ClassFormatError)var10;
               } else if (var10 instanceof RuntimeException) {
                  throw (RuntimeException)var10;
               } else {
                  throw new CannotCompileException(var10);
               }
            }
         }
      }
   }
}
