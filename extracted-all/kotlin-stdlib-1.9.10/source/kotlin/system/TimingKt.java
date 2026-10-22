@file:JvmName(name = "TimingKt")

package kotlin.system

import kotlin.contracts.InvocationKind

public inline fun measureTimeMillis(block: () -> Unit): Long {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   var start: Long = System.currentTimeMillis();
   block.invoke();
   return System.currentTimeMillis() - start;
}

public inline fun measureNanoTime(block: () -> Unit): Long {
   contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
   }

   var start: Long = System.nanoTime();
   block.invoke();
   return System.nanoTime() - start;
}

