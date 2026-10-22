package com.sun.prism.impl;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

public class BufferUtil {
   public static final int SIZEOF_BYTE = 1;
   public static final int SIZEOF_SHORT = 2;
   public static final int SIZEOF_INT = 4;
   public static final int SIZEOF_FLOAT = 4;
   public static final int SIZEOF_DOUBLE = 8;
   private static boolean isCDCFP;
   private static Class byteOrderClass;
   private static Object nativeOrderObject;
   private static Method orderMethod;

   private BufferUtil() {
   }

   public static void nativeOrder(ByteBuffer var0) {
      if (!isCDCFP) {
         try {
            if (byteOrderClass == null) {
               byteOrderClass = AccessController.doPrivileged((PrivilegedExceptionAction<Class>)(() -> Class.forName("java.nio.ByteOrder", true, null)));
               orderMethod = ByteBuffer.class.getMethod("order", byteOrderClass);
               Method var1 = byteOrderClass.getMethod("nativeOrder", (Class<?>[])null);
               nativeOrderObject = var1.invoke(null, (Object[])null);
            }
         } catch (Throwable var3) {
            isCDCFP = true;
         }

         if (!isCDCFP) {
            try {
               orderMethod.invoke(var0, nativeOrderObject);
            } catch (Throwable var2) {
            }
         }
      }
   }

   public static ByteBuffer newByteBuffer(int var0) {
      ByteBuffer var1 = ByteBuffer.allocateDirect(var0);
      nativeOrder(var1);
      return var1;
   }

   public static DoubleBuffer newDoubleBuffer(int var0) {
      ByteBuffer var1 = newByteBuffer(var0 * 8);
      return var1.asDoubleBuffer();
   }

   public static FloatBuffer newFloatBuffer(int var0) {
      ByteBuffer var1 = newByteBuffer(var0 * 4);
      return var1.asFloatBuffer();
   }

   public static IntBuffer newIntBuffer(int var0) {
      ByteBuffer var1 = newByteBuffer(var0 * 4);
      return var1.asIntBuffer();
   }

   public static ShortBuffer newShortBuffer(int var0) {
      ByteBuffer var1 = newByteBuffer(var0 * 2);
      return var1.asShortBuffer();
   }
}
