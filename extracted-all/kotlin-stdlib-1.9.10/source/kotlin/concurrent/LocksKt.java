@file:JvmName(name = "LocksKt")

@file:SourceDebugExtension(["SMAP\nLocks.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Locks.kt\nkotlin/concurrent/LocksKt\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,74:1\n1#2:75\n*E\n"])

package kotlin.concurrent

import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock
import kotlin.contracts.InvocationKind
import kotlin.internal.InlineOnly
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension

@InlineOnly
public inline fun <T> Lock.withLock(action: () -> T): T {
   contract {
      callsInPlace(action, InvocationKind.EXACTLY_ONCE)
   }

   label14: {
      `$this$withLock`.lock();

      try {
         var var2: Any = action.invoke();
      } catch (java.lang.Throwable var3) {
         InlineMarker.finallyStart(1);
         `$this$withLock`.unlock();
         InlineMarker.finallyEnd(1);
      }

      InlineMarker.finallyStart(1);
      `$this$withLock`.unlock();
      InlineMarker.finallyEnd(1);
   }
}

@InlineOnly
public inline fun <T> ReentrantReadWriteLock.read(action: () -> T): T {
   contract {
      callsInPlace(action, InvocationKind.EXACTLY_ONCE)
   }

   label14: {
      var rl: ReadLock = `$this$read`.readLock();
      rl.lock();

      try {
         var var3: Any = action.invoke();
      } catch (java.lang.Throwable var4) {
         InlineMarker.finallyStart(1);
         rl.unlock();
         InlineMarker.finallyEnd(1);
      }

      InlineMarker.finallyStart(1);
      rl.unlock();
      InlineMarker.finallyEnd(1);
   }
}

@InlineOnly
public inline fun <T> ReentrantReadWriteLock.write(action: () -> T): T {
   contract {
      callsInPlace(action, InvocationKind.EXACTLY_ONCE)
   }

   label46: {
      var rl: ReadLock = `$this$write`.readLock();
      var readCount: Int = if (`$this$write`.getWriteHoldCount() == 0) `$this$write`.getReadHoldCount() else 0;

      for (int wl = 0; wl < readCount; wl++) {
         rl.unlock();
      }

      var var11: WriteLock = `$this$write`.writeLock();
      var11.lock();

      try {
         var it: Any = action.invoke();
      } catch (java.lang.Throwable var9) {
         InlineMarker.finallyStart(1);

         for (int var12 = 0; var12 < readCount; var12++) {
            rl.lock();
         }

         var11.unlock();
         InlineMarker.finallyEnd(1);
      }

      InlineMarker.finallyStart(1);

      for (int var13 = 0; var13 < readCount; var13++) {
         rl.lock();
      }

      var11.unlock();
      InlineMarker.finallyEnd(1);
   }
}

