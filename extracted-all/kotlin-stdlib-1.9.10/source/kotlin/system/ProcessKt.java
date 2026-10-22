@file:JvmName(name = "ProcessKt")

package kotlin.system

import kotlin.internal.InlineOnly

@InlineOnly
public inline fun exitProcess(status: Int): Nothing {
   System.exit(status);
   throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
}

