package com.sun.jna;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.WeakHashMap;

public class NativeMappedConverter implements TypeConverter {
   private static final Map<Class<?>, Reference<NativeMappedConverter>> converters = new WeakHashMap<>();
   private final Class<?> type;
   private final Class<?> nativeType;
   private final NativeMapped instance;

   public static NativeMappedConverter getInstance(Class<?> cls) {
      synchronized (converters) {
         Reference<NativeMappedConverter> r = converters.get(cls);
         NativeMappedConverter nmc = r != null ? r.get() : null;
         if (nmc == null) {
            nmc = new NativeMappedConverter(cls);
            converters.put(cls, new SoftReference<>(nmc));
         }

         return nmc;
      }
   }

   public NativeMappedConverter(Class<?> type) {
      if (!NativeMapped.class.isAssignableFrom(type)) {
         throw new IllegalArgumentException("Type must derive from " + NativeMapped.class);
      } else {
         this.type = type;
         this.instance = this.defaultValue();
         this.nativeType = this.instance.nativeType();
      }
   }

   public NativeMapped defaultValue() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.NullPointerException: Cannot invoke "org.jetbrains.java.decompiler.struct.gen.VarType.isGeneric()" because "newRet" is null
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent.getInferredExprType(InvocationExprent.java:634)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ArrayExprent.getInferredExprType(ArrayExprent.java:45)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getInferredExprType(FunctionExprent.java:243)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.FunctionExprent.getInferredExprType(FunctionExprent.java:287)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.getCastedExprent(ExprProcessor.java:966)
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.ExitExprent.toJava(ExitExprent.java:86)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.listToJava(ExprProcessor.java:895)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.BasicBlockStatement.toJava(BasicBlockStatement.java:90)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.RootStatement.toJava(RootStatement.java:36)
      //   at org.jetbrains.java.decompiler.main.ClassWriter.writeMethod(ClassWriter.java:1283)
      //
      // Bytecode:
      // 00: aload 0
      // 01: getfield com/sun/jna/NativeMappedConverter.type Ljava/lang/Class;
      // 04: invokevirtual java/lang/Class.isEnum ()Z
      // 07: ifeq 17
      // 0a: aload 0
      // 0b: getfield com/sun/jna/NativeMappedConverter.type Ljava/lang/Class;
      // 0e: invokevirtual java/lang/Class.getEnumConstants ()[Ljava/lang/Object;
      // 11: bipush 0
      // 12: aaload
      // 13: checkcast com/sun/jna/NativeMapped
      // 16: areturn
      // 17: aload 0
      // 18: getfield com/sun/jna/NativeMappedConverter.type Ljava/lang/Class;
      // 1b: invokestatic com/sun/jna/Klass.newInstance (Ljava/lang/Class;)Ljava/lang/Object;
      // 1e: checkcast com/sun/jna/NativeMapped
      // 21: areturn
   }

   @Override
   public Object fromNative(Object nativeValue, FromNativeContext context) {
      return this.instance.fromNative(nativeValue, context);
   }

   @Override
   public Class<?> nativeType() {
      return this.nativeType;
   }

   @Override
   public Object toNative(Object value, ToNativeContext context) {
      if (value == null) {
         if (Pointer.class.isAssignableFrom(this.nativeType)) {
            return null;
         }

         value = this.defaultValue();
      }

      return ((NativeMapped)value).toNative();
   }
}
