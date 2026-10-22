package okhttp3.internal.connection

import java.lang.ref.Reference
import java.net.Socket
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.Address
import okhttp3.ConnectionPool
import okhttp3.Route
import okhttp3.internal.Util
import okhttp3.internal.concurrent.Task
import okhttp3.internal.concurrent.TaskQueue
import okhttp3.internal.concurrent.TaskRunner
import okhttp3.internal.platform.Platform

@SourceDebugExtension(["SMAP\nRealConnectionPool.kt\nKotlin\n*S Kotlin\n*F\n+ 1 RealConnectionPool.kt\nokhttp3/internal/connection/RealConnectionPool\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 Util.kt\nokhttp3/internal/Util\n*L\n1#1,250:1\n1#2:251\n1774#3,4:252\n608#4,4:256\n608#4,4:260\n608#4,4:264\n*S KotlinDebug\n*F\n+ 1 RealConnectionPool.kt\nokhttp3/internal/connection/RealConnectionPool\n*L\n60#1:252,4\n95#1:256,4\n106#1:260,4\n215#1:264,4\n*E\n"])
class RealConnectionPool(taskRunner: TaskRunner, maxIdleConnections: Int, keepAliveDuration: Long, timeUnit: TimeUnit) {
   @JvmStatic
   public RealConnectionPool.Companion Companion = new RealConnectionPool.Companion(null);

   private final val cleanupQueue: TaskQueue
   private final val cleanupTask: <unrepresentable>
   private final val connections: ConcurrentLinkedQueue<RealConnection>
   private final val keepAliveDurationNs: Long
   private final val maxIdleConnections: Int

   init {
      this.maxIdleConnections = maxIdleConnections;
      this.keepAliveDurationNs = timeUnit.toNanos(keepAliveDuration);
      this.cleanupQueue = taskRunner.newQueue();
      this.cleanupTask = new Task(this, Util.okHttpName + " ConnectionPool") {
         {
            super(`$super_call_param$1`, false, 2, null);
            this.this$0 = `$receiver`;
         }

         @Override
         public long runOnce() {
            return this.this$0.cleanup(System.nanoTime());
         }
      };
      this.connections = new ConcurrentLinkedQueue<>();
      if (keepAliveDuration <= 0L) {
         throw new IllegalArgumentException(("keepAliveDuration <= 0: " + keepAliveDuration).toString());
      }
   }

   public fun idleConnectionCount(): Int {
      var `$this$count$iv`: java.lang.Iterable = this.connections;
      var var12: Int;
      if (this.connections is java.util.Collection && this.connections.isEmpty()) {
         var12 = 0;
      } else {
         var `count$iv`: Int = 0;

         for (Object element$iv : $this$count$iv) {
            var it: RealConnection = `element$iv` as RealConnection;
            var var10000: Boolean;
            synchronized (it) {
               var10000 = it.getCalls().isEmpty();
            }

            if (var10000) {
               if (++`count$iv` < 0) {
                  CollectionsKt.throwCountOverflow();
               }
            }
         }

         var12 = `count$iv`;
      }

      return var12;
   }

   public fun connectionCount(): Int {
      return this.connections.size();
   }

   public fun callAcquirePooledConnection(address: Address, call: RealCall, routes: List<Route>?, requireMultiplexed: Boolean): Boolean {
      for (RealConnection connection : this.connections) {
         synchronized (connection){} // $VF: monitorenter 

         label30: {
            try {
               if ((!requireMultiplexed || connection.isMultiplexed$okhttp()) && connection.isEligible$okhttp(address, routes)) {
                  call.acquireConnectionNoEvents(connection);
                  break label30;
               }
            } catch (java.lang.Throwable var10) {
               // $VF: monitorexit
            }

            // $VF: monitorexit
            continue;
         }

         // $VF: monitorexit
      }

      return false;
   }

   public fun put(connection: RealConnection) {
      if (Util.assertionsEnabled && !Thread.holdsLock(connection)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + connection);
      } else {
         this.connections.add(connection);
         TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
      }
   }

   public fun connectionBecameIdle(connection: RealConnection): Boolean {
      if (Util.assertionsEnabled && !Thread.holdsLock(connection)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + connection);
      } else {
         var var10000: Boolean;
         if (!connection.getNoNewExchanges() && this.maxIdleConnections != 0) {
            TaskQueue.schedule$default(this.cleanupQueue, this.cleanupTask, 0L, 2, null);
            var10000 = false;
         } else {
            connection.setNoNewExchanges(true);
            this.connections.remove(connection);
            if (this.connections.isEmpty()) {
               this.cleanupQueue.cancelAll();
            }

            var10000 = true;
         }

         return var10000;
      }
   }

   public fun evictAll() {
      var var10000: java.util.Iterator = this.connections.iterator();
      var i: java.util.Iterator = var10000;

      while (i.hasNext()) {
         var connection: RealConnection = i.next() as RealConnection;
         var var9: Socket;
         synchronized (connection) {
            var var8: Socket;
            if (connection.getCalls().isEmpty()) {
               i.remove();
               connection.setNoNewExchanges(true);
               var8 = connection.socket();
            } else {
               var8 = null;
            }

            var9 = var8;
         }

         if (var9 != null) {
            Util.closeQuietly(var9);
         }
      }

      if (this.connections.isEmpty()) {
         this.cleanupQueue.cancelAll();
      }
   }

   public fun cleanup(now: Long): Long {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.MonitorExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.MonitorExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 000: bipush 0
      // 001: istore 3
      // 002: bipush 0
      // 003: istore 4
      // 005: aconst_null
      // 006: astore 5
      // 008: lconst_0
      // 009: lstore 14
      // 00b: ldc2_w -9223372036854775808
      // 00e: lstore 14
      // 010: aload 0
      // 011: getfield okhttp3/internal/connection/RealConnectionPool.connections Ljava/util/concurrent/ConcurrentLinkedQueue;
      // 014: invokevirtual java/util/concurrent/ConcurrentLinkedQueue.iterator ()Ljava/util/Iterator;
      // 017: astore 6
      // 019: aload 6
      // 01b: invokeinterface java/util/Iterator.hasNext ()Z 1
      // 020: ifeq 092
      // 023: aload 6
      // 025: invokeinterface java/util/Iterator.next ()Ljava/lang/Object; 1
      // 02a: checkcast okhttp3/internal/connection/RealConnection
      // 02d: astore 7
      // 02f: aload 7
      // 031: ldc "connection"
      // 033: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullExpressionValue (Ljava/lang/Object;Ljava/lang/String;)V
      // 036: aload 7
      // 038: astore 9
      // 03a: aload 9
      // 03c: monitorenter
      // 03d: nop
      // 03e: bipush 0
      // 03f: istore 10
      // 041: aload 0
      // 042: aload 7
      // 044: lload 1
      // 045: invokespecial okhttp3/internal/connection/RealConnectionPool.pruneAndGetAllocationCount (Lokhttp3/internal/connection/RealConnection;J)I
      // 048: ifle 05b
      // 04b: iload 3
      // 04c: istore 11
      // 04e: iload 11
      // 050: bipush 1
      // 051: iadd
      // 052: istore 3
      // 053: iload 11
      // 055: invokestatic java/lang/Integer.valueOf (I)Ljava/lang/Integer;
      // 058: goto 081
      // 05b: iload 4
      // 05d: bipush 1
      // 05e: iadd
      // 05f: istore 4
      // 061: lload 1
      // 062: aload 7
      // 064: invokevirtual okhttp3/internal/connection/RealConnection.getIdleAtNs$okhttp ()J
      // 067: lsub
      // 068: lstore 12
      // 06a: lload 12
      // 06c: lload 14
      // 06e: lcmp
      // 06f: ifle 07d
      // 072: lload 12
      // 074: lstore 14
      // 076: aload 7
      // 078: astore 5
      // 07a: goto 07e
      // 07d: nop
      // 07e: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 081: nop
      // 082: astore 10
      // 084: aload 9
      // 086: monitorexit
      // 087: goto 019
      // 08a: astore 10
      // 08c: aload 9
      // 08e: monitorexit
      // 08f: aload 10
      // 091: athrow
      // 092: nop
      // 093: lload 14
      // 095: aload 0
      // 096: getfield okhttp3/internal/connection/RealConnectionPool.keepAliveDurationNs J
      // 099: lcmp
      // 09a: ifge 0a6
      // 09d: iload 4
      // 09f: aload 0
      // 0a0: getfield okhttp3/internal/connection/RealConnectionPool.maxIdleConnections I
      // 0a3: if_icmple 12a
      // 0a6: aload 5
      // 0a8: dup
      // 0a9: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 0ac: astore 6
      // 0ae: aload 6
      // 0b0: astore 7
      // 0b2: aload 7
      // 0b4: monitorenter
      // 0b5: nop
      // 0b6: bipush 0
      // 0b7: istore 8
      // 0b9: aload 6
      // 0bb: invokevirtual okhttp3/internal/connection/RealConnection.getCalls ()Ljava/util/List;
      // 0be: checkcast java/util/Collection
      // 0c1: invokeinterface java/util/Collection.isEmpty ()Z 1
      // 0c6: ifne 0cd
      // 0c9: bipush 1
      // 0ca: goto 0ce
      // 0cd: bipush 0
      // 0ce: ifeq 0da
      // 0d1: lconst_0
      // 0d2: lstore 11
      // 0d4: aload 7
      // 0d6: monitorexit
      // 0d7: lload 11
      // 0d9: lreturn
      // 0da: aload 6
      // 0dc: invokevirtual okhttp3/internal/connection/RealConnection.getIdleAtNs$okhttp ()J
      // 0df: lload 14
      // 0e1: ladd
      // 0e2: lload 1
      // 0e3: lcmp
      // 0e4: ifeq 0f0
      // 0e7: lconst_0
      // 0e8: lstore 9
      // 0ea: aload 7
      // 0ec: monitorexit
      // 0ed: lload 9
      // 0ef: lreturn
      // 0f0: aload 6
      // 0f2: bipush 1
      // 0f3: invokevirtual okhttp3/internal/connection/RealConnection.setNoNewExchanges (Z)V
      // 0f6: aload 0
      // 0f7: getfield okhttp3/internal/connection/RealConnectionPool.connections Ljava/util/concurrent/ConcurrentLinkedQueue;
      // 0fa: aload 5
      // 0fc: invokevirtual java/util/concurrent/ConcurrentLinkedQueue.remove (Ljava/lang/Object;)Z
      // 0ff: istore 8
      // 101: aload 7
      // 103: monitorexit
      // 104: goto 10f
      // 107: astore 8
      // 109: aload 7
      // 10b: monitorexit
      // 10c: aload 8
      // 10e: athrow
      // 10f: aload 6
      // 111: invokevirtual okhttp3/internal/connection/RealConnection.socket ()Ljava/net/Socket;
      // 114: invokestatic okhttp3/internal/Util.closeQuietly (Ljava/net/Socket;)V
      // 117: aload 0
      // 118: getfield okhttp3/internal/connection/RealConnectionPool.connections Ljava/util/concurrent/ConcurrentLinkedQueue;
      // 11b: invokevirtual java/util/concurrent/ConcurrentLinkedQueue.isEmpty ()Z
      // 11e: ifeq 128
      // 121: aload 0
      // 122: getfield okhttp3/internal/connection/RealConnectionPool.cleanupQueue Lokhttp3/internal/concurrent/TaskQueue;
      // 125: invokevirtual okhttp3/internal/concurrent/TaskQueue.cancelAll ()V
      // 128: lconst_0
      // 129: lreturn
      // 12a: iload 4
      // 12c: ifle 137
      // 12f: aload 0
      // 130: getfield okhttp3/internal/connection/RealConnectionPool.keepAliveDurationNs J
      // 133: lload 14
      // 135: lsub
      // 136: lreturn
      // 137: iload 3
      // 138: ifle 140
      // 13b: aload 0
      // 13c: getfield okhttp3/internal/connection/RealConnectionPool.keepAliveDurationNs J
      // 13f: lreturn
      // 140: ldc2_w -1
      // 143: lreturn
   }

   private fun pruneAndGetAllocationCount(connection: RealConnection, now: Long): Int {
      if (Util.assertionsEnabled && !Thread.holdsLock(connection)) {
         throw new AssertionError("Thread " + Thread.currentThread().getName() + " MUST hold lock on " + connection);
      } else {
         var references: java.util.List = connection.getCalls();
         var var9: Int = 0;

         while (i < references.size()) {
            var reference: Reference = references.get(var9) as Reference;
            if (reference.get() != null) {
               var9++;
            } else {
               Platform.Companion
                  .get()
                  .logCloseableLeak(
                     "A connection to " + connection.route().address().url() + " was leaked. Did you forget to close a response body?",
                     (reference as RealCall.CallReference).getCallStackTrace()
                  );
               references.remove(var9);
               connection.setNoNewExchanges(true);
               if (references.isEmpty()) {
                  connection.setIdleAtNs$okhttp(now - this.keepAliveDurationNs);
                  return 0;
               }
            }
         }

         return references.size();
      }
   }

   companion object Companion private constructor() {
      public fun get(connectionPool: ConnectionPool): RealConnectionPool {
         return connectionPool.getDelegate$okhttp();
      }
   }
}
