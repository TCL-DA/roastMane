package okio

import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock
import kotlin.jvm.internal.InlineMarker
import kotlin.jvm.internal.SourceDebugExtension
import org.jetbrains.annotations.NotNull

@SourceDebugExtension(["SMAP\nPipe.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Pipe.kt\nokio/Pipe\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Timeout.kt\nokio/Timeout\n*L\n1#1,257:1\n1#2:258\n268#3,26:259\n*S KotlinDebug\n*F\n+ 1 Pipe.kt\nokio/Pipe\n*L\n217#1:259,26\n*E\n"])
class Pipe(maxBufferSize: Long) {
   internal final val buffer: Buffer
   internal final var canceled: Boolean
   public final val condition: Condition
   internal final var foldedSink: Sink?
   public final val lock: ReentrantLock
   internal final val maxBufferSize: Long
   public final val sink: Sink
      public final get() {
         return this.sink;
      }

   internal final var sinkClosed: Boolean
   public final val source: Source
      public final get() {
         return this.source;
      }

   internal final var sourceClosed: Boolean

   init {
      this.maxBufferSize = maxBufferSize;
      this.buffer = new Buffer();
      this.lock = new ReentrantLock();
      var var10001: Condition = this.lock.newCondition();
      this.condition = var10001;
      if (this.maxBufferSize < 1L) {
         throw new IllegalArgumentException(("maxBufferSize < 1: " + this.maxBufferSize).toString());
      } else {
         this.sink = new Sink(this) {
            @NotNull
            private final Timeout timeout;

            {
               this.this$0 = `$receiver`;
               this.timeout = new Timeout();
            }

            @Override
            public void write(@NotNull Buffer param1, long param2) {
               // $VF: Couldn't be decompiled
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
               //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
               //
               // Bytecode:
               // 000: aload 1
               // 001: ldc "source"
               // 003: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
               // 006: lconst_0
               // 007: lstore 21
               // 009: lload 2
               // 00a: lstore 21
               // 00c: aconst_null
               // 00d: astore 4
               // 00f: aload 0
               // 010: getfield okio/Pipe$sink$1.this$0 Lokio/Pipe;
               // 013: invokevirtual okio/Pipe.getLock ()Ljava/util/concurrent/locks/ReentrantLock;
               // 016: checkcast java/util/concurrent/locks/Lock
               // 019: astore 5
               // 01b: aload 0
               // 01c: getfield okio/Pipe$sink$1.this$0 Lokio/Pipe;
               // 01f: astore 6
               // 021: aload 5
               // 023: invokeinterface java/util/concurrent/locks/Lock.lock ()V 1
               // 028: nop
               // 029: bipush 0
               // 02a: istore 7
               // 02c: aload 6
               // 02e: invokevirtual okio/Pipe.getSinkClosed$okio ()Z
               // 031: ifne 038
               // 034: bipush 1
               // 035: goto 039
               // 038: bipush 0
               // 039: ifne 050
               // 03c: bipush 0
               // 03d: istore 8
               // 03f: ldc "closed"
               // 041: astore 8
               // 043: new java/lang/IllegalStateException
               // 046: dup
               // 047: aload 8
               // 049: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
               // 04c: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // 04f: athrow
               // 050: aload 6
               // 052: invokevirtual okio/Pipe.getCanceled$okio ()Z
               // 055: ifeq 062
               // 058: new java/io/IOException
               // 05b: dup
               // 05c: ldc "canceled"
               // 05e: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
               // 061: athrow
               // 062: lload 21
               // 064: lconst_0
               // 065: lcmp
               // 066: ifle 0f1
               // 069: aload 6
               // 06b: invokevirtual okio/Pipe.getFoldedSink$okio ()Lokio/Sink;
               // 06e: dup
               // 06f: ifnull 07e
               // 072: astore 9
               // 074: bipush 0
               // 075: istore 10
               // 077: aload 9
               // 079: astore 4
               // 07b: goto 0f2
               // 07e: pop
               // 07f: aload 6
               // 081: invokevirtual okio/Pipe.getSourceClosed$okio ()Z
               // 084: ifeq 091
               // 087: new java/io/IOException
               // 08a: dup
               // 08b: ldc "source is closed"
               // 08d: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
               // 090: athrow
               // 091: aload 6
               // 093: invokevirtual okio/Pipe.getMaxBufferSize$okio ()J
               // 096: aload 6
               // 098: invokevirtual okio/Pipe.getBuffer$okio ()Lokio/Buffer;
               // 09b: invokevirtual okio/Buffer.size ()J
               // 09e: lsub
               // 09f: lstore 11
               // 0a1: lload 11
               // 0a3: lconst_0
               // 0a4: lcmp
               // 0a5: ifne 0c9
               // 0a8: aload 0
               // 0a9: getfield okio/Pipe$sink$1.timeout Lokio/Timeout;
               // 0ac: aload 6
               // 0ae: invokevirtual okio/Pipe.getCondition ()Ljava/util/concurrent/locks/Condition;
               // 0b1: invokevirtual okio/Timeout.awaitSignal (Ljava/util/concurrent/locks/Condition;)V
               // 0b4: aload 6
               // 0b6: invokevirtual okio/Pipe.getCanceled$okio ()Z
               // 0b9: ifeq 0c6
               // 0bc: new java/io/IOException
               // 0bf: dup
               // 0c0: ldc "canceled"
               // 0c2: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
               // 0c5: athrow
               // 0c6: goto 062
               // 0c9: lload 11
               // 0cb: lload 21
               // 0cd: invokestatic java/lang/Math.min (JJ)J
               // 0d0: lstore 13
               // 0d2: aload 6
               // 0d4: invokevirtual okio/Pipe.getBuffer$okio ()Lokio/Buffer;
               // 0d7: aload 1
               // 0d8: lload 13
               // 0da: invokevirtual okio/Buffer.write (Lokio/Buffer;J)V
               // 0dd: lload 21
               // 0df: lload 13
               // 0e1: lsub
               // 0e2: lstore 21
               // 0e4: aload 6
               // 0e6: invokevirtual okio/Pipe.getCondition ()Ljava/util/concurrent/locks/Condition;
               // 0e9: invokeinterface java/util/concurrent/locks/Condition.signalAll ()V 1
               // 0ee: goto 062
               // 0f1: nop
               // 0f2: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 0f5: astore 7
               // 0f7: aload 5
               // 0f9: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
               // 0fe: goto 10d
               // 101: astore 7
               // 103: aload 5
               // 105: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
               // 10a: aload 7
               // 10c: athrow
               // 10d: aload 4
               // 10f: astore 5
               // 111: aload 5
               // 113: ifnull 24a
               // 116: aload 0
               // 117: getfield okio/Pipe$sink$1.this$0 Lokio/Pipe;
               // 11a: astore 6
               // 11c: aload 5
               // 11e: astore 7
               // 120: bipush 0
               // 121: istore 8
               // 123: aload 7
               // 125: invokeinterface okio/Sink.timeout ()Lokio/Timeout; 1
               // 12a: astore 9
               // 12c: aload 6
               // 12e: invokevirtual okio/Pipe.sink ()Lokio/Sink;
               // 131: invokeinterface okio/Sink.timeout ()Lokio/Timeout; 1
               // 136: astore 10
               // 138: bipush 0
               // 139: istore 11
               // 13b: aload 9
               // 13d: invokevirtual okio/Timeout.timeoutNanos ()J
               // 140: lstore 12
               // 142: aload 9
               // 144: getstatic okio/Timeout.Companion Lokio/Timeout$Companion;
               // 147: aload 10
               // 149: invokevirtual okio/Timeout.timeoutNanos ()J
               // 14c: aload 9
               // 14e: invokevirtual okio/Timeout.timeoutNanos ()J
               // 151: invokevirtual okio/Timeout$Companion.minTimeout (JJ)J
               // 154: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 157: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 15a: pop
               // 15b: aload 9
               // 15d: invokevirtual okio/Timeout.hasDeadline ()Z
               // 160: ifeq 1de
               // 163: aload 9
               // 165: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 168: lstore 14
               // 16a: aload 10
               // 16c: invokevirtual okio/Timeout.hasDeadline ()Z
               // 16f: ifeq 185
               // 172: aload 9
               // 174: aload 9
               // 176: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 179: aload 10
               // 17b: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 17e: invokestatic java/lang/Math.min (JJ)J
               // 181: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 184: pop
               // 185: nop
               // 186: bipush 0
               // 187: istore 16
               // 189: aload 7
               // 18b: astore 17
               // 18d: bipush 0
               // 18e: istore 18
               // 190: aload 17
               // 192: aload 1
               // 193: lload 21
               // 195: invokeinterface okio/Sink.write (Lokio/Buffer;J)V 4
               // 19a: nop
               // 19b: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 19e: astore 16
               // 1a0: aload 9
               // 1a2: lload 12
               // 1a4: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 1a7: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 1aa: pop
               // 1ab: aload 10
               // 1ad: invokevirtual okio/Timeout.hasDeadline ()Z
               // 1b0: ifeq 1bb
               // 1b3: aload 9
               // 1b5: lload 14
               // 1b7: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 1ba: pop
               // 1bb: goto 246
               // 1be: astore 16
               // 1c0: aload 9
               // 1c2: lload 12
               // 1c4: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 1c7: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 1ca: pop
               // 1cb: aload 10
               // 1cd: invokevirtual okio/Timeout.hasDeadline ()Z
               // 1d0: ifeq 1db
               // 1d3: aload 9
               // 1d5: lload 14
               // 1d7: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 1da: pop
               // 1db: aload 16
               // 1dd: athrow
               // 1de: aload 10
               // 1e0: invokevirtual okio/Timeout.hasDeadline ()Z
               // 1e3: ifeq 1f1
               // 1e6: aload 9
               // 1e8: aload 10
               // 1ea: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 1ed: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 1f0: pop
               // 1f1: nop
               // 1f2: bipush 0
               // 1f3: istore 19
               // 1f5: aload 7
               // 1f7: astore 17
               // 1f9: bipush 0
               // 1fa: istore 18
               // 1fc: aload 17
               // 1fe: aload 1
               // 1ff: lload 21
               // 201: invokeinterface okio/Sink.write (Lokio/Buffer;J)V 4
               // 206: nop
               // 207: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 20a: astore 20
               // 20c: aload 9
               // 20e: lload 12
               // 210: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 213: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 216: pop
               // 217: aload 10
               // 219: invokevirtual okio/Timeout.hasDeadline ()Z
               // 21c: ifeq 225
               // 21f: aload 9
               // 221: invokevirtual okio/Timeout.clearDeadline ()Lokio/Timeout;
               // 224: pop
               // 225: goto 246
               // 228: astore 20
               // 22a: aload 9
               // 22c: lload 12
               // 22e: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 231: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 234: pop
               // 235: aload 10
               // 237: invokevirtual okio/Timeout.hasDeadline ()Z
               // 23a: ifeq 243
               // 23d: aload 9
               // 23f: invokevirtual okio/Timeout.clearDeadline ()Lokio/Timeout;
               // 242: pop
               // 243: aload 20
               // 245: athrow
               // 246: nop
               // 247: goto 24b
               // 24a: nop
               // 24b: return
            }

            @Override
            public void flush() {
               // $VF: Couldn't be decompiled
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
               //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
               //
               // Bytecode:
               // 000: aconst_null
               // 001: astore 1
               // 002: aload 0
               // 003: getfield okio/Pipe$sink$1.this$0 Lokio/Pipe;
               // 006: invokevirtual okio/Pipe.getLock ()Ljava/util/concurrent/locks/ReentrantLock;
               // 009: checkcast java/util/concurrent/locks/Lock
               // 00c: astore 2
               // 00d: aload 0
               // 00e: getfield okio/Pipe$sink$1.this$0 Lokio/Pipe;
               // 011: astore 3
               // 012: aload 2
               // 013: invokeinterface java/util/concurrent/locks/Lock.lock ()V 1
               // 018: nop
               // 019: bipush 0
               // 01a: istore 4
               // 01c: aload 3
               // 01d: invokevirtual okio/Pipe.getSinkClosed$okio ()Z
               // 020: ifne 027
               // 023: bipush 1
               // 024: goto 028
               // 027: bipush 0
               // 028: ifne 03f
               // 02b: bipush 0
               // 02c: istore 5
               // 02e: ldc "closed"
               // 030: astore 5
               // 032: new java/lang/IllegalStateException
               // 035: dup
               // 036: aload 5
               // 038: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
               // 03b: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
               // 03e: athrow
               // 03f: aload 3
               // 040: invokevirtual okio/Pipe.getCanceled$okio ()Z
               // 043: ifeq 050
               // 046: new java/io/IOException
               // 049: dup
               // 04a: ldc "canceled"
               // 04c: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
               // 04f: athrow
               // 050: aload 3
               // 051: invokevirtual okio/Pipe.getFoldedSink$okio ()Lokio/Sink;
               // 054: dup
               // 055: ifnull 063
               // 058: astore 6
               // 05a: bipush 0
               // 05b: istore 7
               // 05d: aload 6
               // 05f: astore 1
               // 060: goto 082
               // 063: pop
               // 064: aload 3
               // 065: invokevirtual okio/Pipe.getSourceClosed$okio ()Z
               // 068: ifeq 081
               // 06b: aload 3
               // 06c: invokevirtual okio/Pipe.getBuffer$okio ()Lokio/Buffer;
               // 06f: invokevirtual okio/Buffer.size ()J
               // 072: lconst_0
               // 073: lcmp
               // 074: ifle 081
               // 077: new java/io/IOException
               // 07a: dup
               // 07b: ldc "source is closed"
               // 07d: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
               // 080: athrow
               // 081: nop
               // 082: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 085: astore 4
               // 087: aload 2
               // 088: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
               // 08d: goto 09b
               // 090: astore 4
               // 092: aload 2
               // 093: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
               // 098: aload 4
               // 09a: athrow
               // 09b: aload 1
               // 09c: astore 2
               // 09d: aload 2
               // 09e: ifnull 1cc
               // 0a1: aload 0
               // 0a2: getfield okio/Pipe$sink$1.this$0 Lokio/Pipe;
               // 0a5: astore 3
               // 0a6: aload 2
               // 0a7: astore 4
               // 0a9: bipush 0
               // 0aa: istore 5
               // 0ac: aload 4
               // 0ae: invokeinterface okio/Sink.timeout ()Lokio/Timeout; 1
               // 0b3: astore 6
               // 0b5: aload 3
               // 0b6: invokevirtual okio/Pipe.sink ()Lokio/Sink;
               // 0b9: invokeinterface okio/Sink.timeout ()Lokio/Timeout; 1
               // 0be: astore 7
               // 0c0: bipush 0
               // 0c1: istore 8
               // 0c3: aload 6
               // 0c5: invokevirtual okio/Timeout.timeoutNanos ()J
               // 0c8: lstore 9
               // 0ca: aload 6
               // 0cc: getstatic okio/Timeout.Companion Lokio/Timeout$Companion;
               // 0cf: aload 7
               // 0d1: invokevirtual okio/Timeout.timeoutNanos ()J
               // 0d4: aload 6
               // 0d6: invokevirtual okio/Timeout.timeoutNanos ()J
               // 0d9: invokevirtual okio/Timeout$Companion.minTimeout (JJ)J
               // 0dc: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 0df: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 0e2: pop
               // 0e3: aload 6
               // 0e5: invokevirtual okio/Timeout.hasDeadline ()Z
               // 0e8: ifeq 163
               // 0eb: aload 6
               // 0ed: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 0f0: lstore 11
               // 0f2: aload 7
               // 0f4: invokevirtual okio/Timeout.hasDeadline ()Z
               // 0f7: ifeq 10d
               // 0fa: aload 6
               // 0fc: aload 6
               // 0fe: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 101: aload 7
               // 103: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 106: invokestatic java/lang/Math.min (JJ)J
               // 109: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 10c: pop
               // 10d: nop
               // 10e: bipush 0
               // 10f: istore 13
               // 111: aload 4
               // 113: astore 14
               // 115: bipush 0
               // 116: istore 15
               // 118: aload 14
               // 11a: invokeinterface okio/Sink.flush ()V 1
               // 11f: nop
               // 120: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 123: astore 13
               // 125: aload 6
               // 127: lload 9
               // 129: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 12c: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 12f: pop
               // 130: aload 7
               // 132: invokevirtual okio/Timeout.hasDeadline ()Z
               // 135: ifeq 140
               // 138: aload 6
               // 13a: lload 11
               // 13c: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 13f: pop
               // 140: goto 1c8
               // 143: astore 13
               // 145: aload 6
               // 147: lload 9
               // 149: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 14c: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 14f: pop
               // 150: aload 7
               // 152: invokevirtual okio/Timeout.hasDeadline ()Z
               // 155: ifeq 160
               // 158: aload 6
               // 15a: lload 11
               // 15c: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 15f: pop
               // 160: aload 13
               // 162: athrow
               // 163: aload 7
               // 165: invokevirtual okio/Timeout.hasDeadline ()Z
               // 168: ifeq 176
               // 16b: aload 6
               // 16d: aload 7
               // 16f: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 172: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 175: pop
               // 176: nop
               // 177: bipush 0
               // 178: istore 16
               // 17a: aload 4
               // 17c: astore 14
               // 17e: bipush 0
               // 17f: istore 15
               // 181: aload 14
               // 183: invokeinterface okio/Sink.flush ()V 1
               // 188: nop
               // 189: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 18c: astore 14
               // 18e: aload 6
               // 190: lload 9
               // 192: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 195: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 198: pop
               // 199: aload 7
               // 19b: invokevirtual okio/Timeout.hasDeadline ()Z
               // 19e: ifeq 1a7
               // 1a1: aload 6
               // 1a3: invokevirtual okio/Timeout.clearDeadline ()Lokio/Timeout;
               // 1a6: pop
               // 1a7: goto 1c8
               // 1aa: astore 14
               // 1ac: aload 6
               // 1ae: lload 9
               // 1b0: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 1b3: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 1b6: pop
               // 1b7: aload 7
               // 1b9: invokevirtual okio/Timeout.hasDeadline ()Z
               // 1bc: ifeq 1c5
               // 1bf: aload 6
               // 1c1: invokevirtual okio/Timeout.clearDeadline ()Lokio/Timeout;
               // 1c4: pop
               // 1c5: aload 14
               // 1c7: athrow
               // 1c8: nop
               // 1c9: goto 1cd
               // 1cc: nop
               // 1cd: return
            }

            @Override
            public void close() {
               // $VF: Couldn't be decompiled
               // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
               // java.lang.ClassCastException: class org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent cannot be cast to class org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent (org.jetbrains.java.decompiler.modules.decompiler.exps.InvocationExprent and org.jetbrains.java.decompiler.modules.decompiler.exps.IfExprent are in unnamed module of loader 'app')
               //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
               //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
               //
               // Bytecode:
               // 000: aconst_null
               // 001: astore 1
               // 002: aload 0
               // 003: getfield okio/Pipe$sink$1.this$0 Lokio/Pipe;
               // 006: invokevirtual okio/Pipe.getLock ()Ljava/util/concurrent/locks/ReentrantLock;
               // 009: checkcast java/util/concurrent/locks/Lock
               // 00c: astore 2
               // 00d: aload 0
               // 00e: getfield okio/Pipe$sink$1.this$0 Lokio/Pipe;
               // 011: astore 3
               // 012: aload 2
               // 013: invokeinterface java/util/concurrent/locks/Lock.lock ()V 1
               // 018: nop
               // 019: bipush 0
               // 01a: istore 4
               // 01c: aload 3
               // 01d: invokevirtual okio/Pipe.getSinkClosed$okio ()Z
               // 020: ifeq 02a
               // 023: aload 2
               // 024: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
               // 029: return
               // 02a: aload 3
               // 02b: invokevirtual okio/Pipe.getFoldedSink$okio ()Lokio/Sink;
               // 02e: dup
               // 02f: ifnull 03d
               // 032: astore 5
               // 034: bipush 0
               // 035: istore 6
               // 037: aload 5
               // 039: astore 1
               // 03a: goto 06a
               // 03d: pop
               // 03e: aload 3
               // 03f: invokevirtual okio/Pipe.getSourceClosed$okio ()Z
               // 042: ifeq 05b
               // 045: aload 3
               // 046: invokevirtual okio/Pipe.getBuffer$okio ()Lokio/Buffer;
               // 049: invokevirtual okio/Buffer.size ()J
               // 04c: lconst_0
               // 04d: lcmp
               // 04e: ifle 05b
               // 051: new java/io/IOException
               // 054: dup
               // 055: ldc "source is closed"
               // 057: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
               // 05a: athrow
               // 05b: aload 3
               // 05c: bipush 1
               // 05d: invokevirtual okio/Pipe.setSinkClosed$okio (Z)V
               // 060: aload 3
               // 061: invokevirtual okio/Pipe.getCondition ()Ljava/util/concurrent/locks/Condition;
               // 064: invokeinterface java/util/concurrent/locks/Condition.signalAll ()V 1
               // 069: nop
               // 06a: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 06d: astore 4
               // 06f: aload 2
               // 070: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
               // 075: goto 083
               // 078: astore 4
               // 07a: aload 2
               // 07b: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
               // 080: aload 4
               // 082: athrow
               // 083: aload 1
               // 084: astore 2
               // 085: aload 2
               // 086: ifnull 1b4
               // 089: aload 0
               // 08a: getfield okio/Pipe$sink$1.this$0 Lokio/Pipe;
               // 08d: astore 3
               // 08e: aload 2
               // 08f: astore 4
               // 091: bipush 0
               // 092: istore 5
               // 094: aload 4
               // 096: invokeinterface okio/Sink.timeout ()Lokio/Timeout; 1
               // 09b: astore 6
               // 09d: aload 3
               // 09e: invokevirtual okio/Pipe.sink ()Lokio/Sink;
               // 0a1: invokeinterface okio/Sink.timeout ()Lokio/Timeout; 1
               // 0a6: astore 7
               // 0a8: bipush 0
               // 0a9: istore 8
               // 0ab: aload 6
               // 0ad: invokevirtual okio/Timeout.timeoutNanos ()J
               // 0b0: lstore 9
               // 0b2: aload 6
               // 0b4: getstatic okio/Timeout.Companion Lokio/Timeout$Companion;
               // 0b7: aload 7
               // 0b9: invokevirtual okio/Timeout.timeoutNanos ()J
               // 0bc: aload 6
               // 0be: invokevirtual okio/Timeout.timeoutNanos ()J
               // 0c1: invokevirtual okio/Timeout$Companion.minTimeout (JJ)J
               // 0c4: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 0c7: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 0ca: pop
               // 0cb: aload 6
               // 0cd: invokevirtual okio/Timeout.hasDeadline ()Z
               // 0d0: ifeq 14b
               // 0d3: aload 6
               // 0d5: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 0d8: lstore 11
               // 0da: aload 7
               // 0dc: invokevirtual okio/Timeout.hasDeadline ()Z
               // 0df: ifeq 0f5
               // 0e2: aload 6
               // 0e4: aload 6
               // 0e6: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 0e9: aload 7
               // 0eb: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 0ee: invokestatic java/lang/Math.min (JJ)J
               // 0f1: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 0f4: pop
               // 0f5: nop
               // 0f6: bipush 0
               // 0f7: istore 13
               // 0f9: aload 4
               // 0fb: astore 14
               // 0fd: bipush 0
               // 0fe: istore 15
               // 100: aload 14
               // 102: invokeinterface okio/Sink.close ()V 1
               // 107: nop
               // 108: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 10b: astore 13
               // 10d: aload 6
               // 10f: lload 9
               // 111: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 114: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 117: pop
               // 118: aload 7
               // 11a: invokevirtual okio/Timeout.hasDeadline ()Z
               // 11d: ifeq 128
               // 120: aload 6
               // 122: lload 11
               // 124: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 127: pop
               // 128: goto 1b0
               // 12b: astore 13
               // 12d: aload 6
               // 12f: lload 9
               // 131: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 134: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 137: pop
               // 138: aload 7
               // 13a: invokevirtual okio/Timeout.hasDeadline ()Z
               // 13d: ifeq 148
               // 140: aload 6
               // 142: lload 11
               // 144: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 147: pop
               // 148: aload 13
               // 14a: athrow
               // 14b: aload 7
               // 14d: invokevirtual okio/Timeout.hasDeadline ()Z
               // 150: ifeq 15e
               // 153: aload 6
               // 155: aload 7
               // 157: invokevirtual okio/Timeout.deadlineNanoTime ()J
               // 15a: invokevirtual okio/Timeout.deadlineNanoTime (J)Lokio/Timeout;
               // 15d: pop
               // 15e: nop
               // 15f: bipush 0
               // 160: istore 16
               // 162: aload 4
               // 164: astore 14
               // 166: bipush 0
               // 167: istore 15
               // 169: aload 14
               // 16b: invokeinterface okio/Sink.close ()V 1
               // 170: nop
               // 171: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
               // 174: astore 14
               // 176: aload 6
               // 178: lload 9
               // 17a: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 17d: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 180: pop
               // 181: aload 7
               // 183: invokevirtual okio/Timeout.hasDeadline ()Z
               // 186: ifeq 18f
               // 189: aload 6
               // 18b: invokevirtual okio/Timeout.clearDeadline ()Lokio/Timeout;
               // 18e: pop
               // 18f: goto 1b0
               // 192: astore 14
               // 194: aload 6
               // 196: lload 9
               // 198: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
               // 19b: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
               // 19e: pop
               // 19f: aload 7
               // 1a1: invokevirtual okio/Timeout.hasDeadline ()Z
               // 1a4: ifeq 1ad
               // 1a7: aload 6
               // 1a9: invokevirtual okio/Timeout.clearDeadline ()Lokio/Timeout;
               // 1ac: pop
               // 1ad: aload 14
               // 1af: athrow
               // 1b0: nop
               // 1b1: goto 1b5
               // 1b4: nop
               // 1b5: return
            }

            @NotNull
            @Override
            public Timeout timeout() {
               return this.timeout;
            }
         };
         this.source = new Source(this) {
            @NotNull
            private final Timeout timeout;

            {
               this.this$0 = `$receiver`;
               this.timeout = new Timeout();
            }

            @Override
            public long read(@NotNull Buffer sink, long byteCount) {
               label45: {
                  var var4: Lock = this.this$0.getLock();
                  var var5: Pipe = this.this$0;
                  var4.lock();

                  label42: {
                     try {
                        if (var5.getSourceClosed$okio()) {
                           throw new IllegalStateException("closed".toString());
                        }

                        if (var5.getCanceled$okio()) {
                           throw new IOException("canceled");
                        }

                        while (var5.getBuffer$okio().size() == 0L) {
                           if (var5.getSinkClosed$okio()) {
                              break label42;
                           }

                           this.timeout.awaitSignal(var5.getCondition());
                           if (var5.getCanceled$okio()) {
                              throw new IOException("canceled");
                           }
                        }

                        var result: Long = var5.getBuffer$okio().read(sink, byteCount);
                        var5.getCondition().signalAll();
                     } catch (java.lang.Throwable var14) {
                        var4.unlock();
                     }

                     var4.unlock();
                  }

                  var4.unlock();
               }
            }

            @Override
            public void close() {
               label15: {
                  var var1: Lock = this.this$0.getLock();
                  var var2: Pipe = this.this$0;
                  var1.lock();

                  try {
                     var2.setSourceClosed$okio(true);
                     var2.getCondition().signalAll();
                  } catch (java.lang.Throwable var4) {
                     var1.unlock();
                  }

                  var1.unlock();
               }
            }

            @NotNull
            @Override
            public Timeout timeout() {
               return this.timeout;
            }
         };
      }
   }

   @Throws(java/io/IOException::class)
   public fun fold(sink: Sink) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.modules.decompiler.stats.IfStatement.initExprents(IfStatement.java:279)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:189)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.initStatementExprents(ExprProcessor.java:192)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:148)
      //
      // Bytecode:
      // 000: aload 1
      // 001: ldc "sink"
      // 003: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
      // 006: nop
      // 007: bipush 0
      // 008: istore 2
      // 009: aconst_null
      // 00a: astore 3
      // 00b: aload 0
      // 00c: getfield okio/Pipe.lock Ljava/util/concurrent/locks/ReentrantLock;
      // 00f: checkcast java/util/concurrent/locks/Lock
      // 012: astore 4
      // 014: aload 4
      // 016: invokeinterface java/util/concurrent/locks/Lock.lock ()V 1
      // 01b: nop
      // 01c: bipush 0
      // 01d: istore 5
      // 01f: aload 0
      // 020: getfield okio/Pipe.foldedSink Lokio/Sink;
      // 023: ifnonnull 02a
      // 026: bipush 1
      // 027: goto 02b
      // 02a: bipush 0
      // 02b: ifne 042
      // 02e: bipush 0
      // 02f: istore 6
      // 031: ldc "sink already folded"
      // 033: astore 6
      // 035: new java/lang/IllegalStateException
      // 038: dup
      // 039: aload 6
      // 03b: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
      // 03e: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
      // 041: athrow
      // 042: aload 0
      // 043: getfield okio/Pipe.canceled Z
      // 046: ifeq 058
      // 049: aload 0
      // 04a: aload 1
      // 04b: putfield okio/Pipe.foldedSink Lokio/Sink;
      // 04e: new java/io/IOException
      // 051: dup
      // 052: ldc "canceled"
      // 054: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 057: athrow
      // 058: aload 0
      // 059: getfield okio/Pipe.buffer Lokio/Buffer;
      // 05c: invokevirtual okio/Buffer.exhausted ()Z
      // 05f: ifeq 075
      // 062: aload 0
      // 063: bipush 1
      // 064: putfield okio/Pipe.sourceClosed Z
      // 067: aload 0
      // 068: aload 1
      // 069: putfield okio/Pipe.foldedSink Lokio/Sink;
      // 06c: nop
      // 06d: aload 4
      // 06f: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
      // 074: return
      // 075: aload 0
      // 076: getfield okio/Pipe.sinkClosed Z
      // 079: istore 2
      // 07a: new okio/Buffer
      // 07d: dup
      // 07e: invokespecial okio/Buffer.<init> ()V
      // 081: astore 3
      // 082: aload 3
      // 083: aload 0
      // 084: getfield okio/Pipe.buffer Lokio/Buffer;
      // 087: aload 0
      // 088: getfield okio/Pipe.buffer Lokio/Buffer;
      // 08b: invokevirtual okio/Buffer.size ()J
      // 08e: invokevirtual okio/Buffer.write (Lokio/Buffer;J)V
      // 091: aload 0
      // 092: getfield okio/Pipe.condition Ljava/util/concurrent/locks/Condition;
      // 095: invokeinterface java/util/concurrent/locks/Condition.signalAll ()V 1
      // 09a: nop
      // 09b: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 09e: astore 5
      // 0a0: aload 4
      // 0a2: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
      // 0a7: goto 0b6
      // 0aa: astore 5
      // 0ac: aload 4
      // 0ae: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
      // 0b3: aload 5
      // 0b5: athrow
      // 0b6: bipush 0
      // 0b7: istore 4
      // 0b9: nop
      // 0ba: aload 1
      // 0bb: aload 3
      // 0bc: aload 3
      // 0bd: invokevirtual okio/Buffer.size ()J
      // 0c0: invokeinterface okio/Sink.write (Lokio/Buffer;J)V 4
      // 0c5: iload 2
      // 0c6: ifeq 0d2
      // 0c9: aload 1
      // 0ca: invokeinterface okio/Sink.close ()V 1
      // 0cf: goto 0d8
      // 0d2: aload 1
      // 0d3: invokeinterface okio/Sink.flush ()V 1
      // 0d8: bipush 1
      // 0d9: istore 4
      // 0db: nop
      // 0dc: goto 006
      // 0df: astore 5
      // 0e1: aload 0
      // 0e2: getfield okio/Pipe.lock Ljava/util/concurrent/locks/ReentrantLock;
      // 0e5: checkcast java/util/concurrent/locks/Lock
      // 0e8: astore 6
      // 0ea: aload 6
      // 0ec: invokeinterface java/util/concurrent/locks/Lock.lock ()V 1
      // 0f1: nop
      // 0f2: bipush 0
      // 0f3: istore 7
      // 0f5: aload 0
      // 0f6: bipush 1
      // 0f7: putfield okio/Pipe.sourceClosed Z
      // 0fa: aload 0
      // 0fb: getfield okio/Pipe.condition Ljava/util/concurrent/locks/Condition;
      // 0fe: invokeinterface java/util/concurrent/locks/Condition.signalAll ()V 1
      // 103: nop
      // 104: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
      // 107: astore 7
      // 109: aload 6
      // 10b: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
      // 110: goto 11f
      // 113: astore 7
      // 115: aload 6
      // 117: invokeinterface java/util/concurrent/locks/Lock.unlock ()V 1
      // 11c: aload 7
      // 11e: athrow
      // 11f: aload 5
      // 121: athrow
   }

   private inline fun Sink.forward(block: (Sink) -> Unit) {
      label50: {
         var `this_$iv`: Timeout = `$this$forward`.timeout();
         var `other$iv`: Timeout = this.sink().timeout();
         var `originalTimeout$iv`: Long = `this_$iv`.timeoutNanos();
         `this_$iv`.timeout(Timeout.Companion.minTimeout(`other$iv`.timeoutNanos(), `this_$iv`.timeoutNanos()), TimeUnit.NANOSECONDS);
         if (`this_$iv`.hasDeadline()) {
            var `originalDeadline$iv`: Long = `this_$iv`.deadlineNanoTime();
            if (`other$iv`.hasDeadline()) {
               `this_$iv`.deadlineNanoTime(Math.min(`this_$iv`.deadlineNanoTime(), `other$iv`.deadlineNanoTime()));
            }

            try {
               block.invoke(`$this$forward`);
            } catch (java.lang.Throwable var15) {
               InlineMarker.finallyStart(1);
               `this_$iv`.timeout(`originalTimeout$iv`, TimeUnit.NANOSECONDS);
               if (`other$iv`.hasDeadline()) {
                  `this_$iv`.deadlineNanoTime(`originalDeadline$iv`);
               }

               InlineMarker.finallyEnd(1);
            }

            InlineMarker.finallyStart(1);
            `this_$iv`.timeout(`originalTimeout$iv`, TimeUnit.NANOSECONDS);
            if (`other$iv`.hasDeadline()) {
               `this_$iv`.deadlineNanoTime(`originalDeadline$iv`);
            }

            InlineMarker.finallyEnd(1);
         } else {
            if (`other$iv`.hasDeadline()) {
               `this_$iv`.deadlineNanoTime(`other$iv`.deadlineNanoTime());
            }

            try {
               block.invoke(`$this$forward`);
            } catch (java.lang.Throwable var14) {
               InlineMarker.finallyStart(1);
               `this_$iv`.timeout(`originalTimeout$iv`, TimeUnit.NANOSECONDS);
               if (`other$iv`.hasDeadline()) {
                  `this_$iv`.clearDeadline();
               }

               InlineMarker.finallyEnd(1);
            }

            InlineMarker.finallyStart(1);
            `this_$iv`.timeout(`originalTimeout$iv`, TimeUnit.NANOSECONDS);
            if (`other$iv`.hasDeadline()) {
               `this_$iv`.clearDeadline();
            }

            InlineMarker.finallyEnd(1);
         }
      }
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "sink", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_sink")
   public fun sink(): Sink {
      return this.sink;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "source", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_source")
   public fun source(): Source {
      return this.source;
   }

   public fun cancel() {
      label15: {
         var var1: Lock = this.lock;
         this.lock.lock();

         try {
            this.canceled = true;
            this.buffer.clear();
            this.condition.signalAll();
         } catch (java.lang.Throwable var3) {
            var1.unlock();
         }

         var1.unlock();
      }
   }
}
