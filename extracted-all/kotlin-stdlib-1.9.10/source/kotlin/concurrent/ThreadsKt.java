@file:JvmName(name = "ThreadsKt")

@file:SourceDebugExtension(["SMAP\nThread.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Thread.kt\nkotlin/concurrent/ThreadsKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,61:1\n1#2:62\n*E\n"])

package kotlin.concurrent

import kotlin.internal.InlineOnly
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension

public fun thread(start: Boolean, isDaemon: Boolean, contextClassLoader: ClassLoader?, name: String?, priority: Int, block: () -> Unit): Thread {
   var thread: <unrepresentable> = new Thread(block) {
      {
         this.$block = `$block`;
      }

      @Override
      public void run() {
         this.$block.invoke();
      }
   };
   if (isDaemon) {
      thread.setDaemon(true);
   }

   if (priority > 0) {
      thread.setPriority(priority);
   }

   if (name != null) {
      thread.setName(name);
   }

   if (contextClassLoader != null) {
      thread.setContextClassLoader(contextClassLoader);
   }

   if (start) {
      thread.start();
   }

   return thread;
}

@JvmSynthetic
fun `thread$default`(var0: Boolean, var1: Boolean, var2: ClassLoader, var3: java.lang.String, var4: Int, var5: Function0, var6: Int, var7: Any): Thread {
   if ((var6 and 1) != 0) {
      var0 = true;
   }

   if ((var6 and 2) != 0) {
      var1 = false;
   }

   if ((var6 and 4) != 0) {
      var2 = null;
   }

   if ((var6 and 8) != 0) {
      var3 = null;
   }

   if ((var6 and 16) != 0) {
      var4 = -1;
   }

   return thread(var0, var1, var2, var3, var4, var5);
}

@InlineOnly
public inline fun <T : Any> ThreadLocal<T>.getOrSet(default: () -> T): T {
   var var2: Any = `$this$getOrSet`.get();
   var var10000: Any;
   if (var2 == null) {
      var var3: Any = var1.invoke();
      `$this$getOrSet`.set(var3);
      var10000 = var3;
   } else {
      var10000 = var2;
   }

   return (T)var10000;
}

