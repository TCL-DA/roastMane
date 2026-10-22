package okhttp3.internal.cache2

import java.io.Closeable
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.channels.FileChannel
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util
import okio.Buffer
import okio.ByteString
import okio.Source
import okio.Timeout

class Relay private constructor(file: RandomAccessFile?, upstream: Source?, upstreamPos: Long, metadata: ByteString, bufferMaxSize: Long) {
   @JvmStatic
   public Relay.Companion Companion = new Relay.Companion(null);
   @JvmStatic
   private int SOURCE_UPSTREAM = 1;
   @JvmStatic
   private int SOURCE_FILE = 2;
   @JvmField
   @JvmStatic
   public ByteString PREFIX_CLEAN = ByteString.Companion.encodeUtf8("OkHttp cache v1\n");
   @JvmField
   @JvmStatic
   public ByteString PREFIX_DIRTY = ByteString.Companion.encodeUtf8("OkHttp DIRTY :(\n");
   @JvmStatic
   private long FILE_HEADER_SIZE = 32L;

   public final val buffer: Buffer
   public final val bufferMaxSize: Long
   public final var complete: Boolean
      internal set
   public final var file: RandomAccessFile?
      internal set
   public final val isClosed: Boolean
      public final get() {
         return this.file == null;
      }

   private final val metadata: ByteString
   public final var sourceCount: Int
      internal set
   public final var upstream: Source?
      internal set
   public final val upstreamBuffer: Buffer
   public final var upstreamPos: Long
      internal set
   public final var upstreamReader: Thread?
      internal set

   init {
      this.file = file;
      this.upstream = upstream;
      this.upstreamPos = upstreamPos;
      this.metadata = metadata;
      this.bufferMaxSize = bufferMaxSize;
      this.upstreamBuffer = new Buffer();
      this.complete = this.upstream == null;
      this.buffer = new Buffer();
   }

   @Throws(java/io/IOException::class)
   private fun writeHeader(prefix: ByteString, upstreamSize: Long, metadataSize: Long) {
      var fileOperator: Buffer = new Buffer();
      fileOperator.write(prefix);
      fileOperator.writeLong(upstreamSize);
      fileOperator.writeLong(metadataSize);
      if (fileOperator.size() != 32L) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         var var10002: RandomAccessFile = this.file;
         var var12: FileChannel = var10002.getChannel();
         new FileOperator(var12).write(0L, fileOperator, 32L);
      }
   }

   @Throws(java/io/IOException::class)
   private fun writeMetadata(upstreamSize: Long) {
      var metadataBuffer: Buffer = new Buffer();
      metadataBuffer.write(this.metadata);
      var var10002: RandomAccessFile = this.file;
      var var5: FileChannel = var10002.getChannel();
      new FileOperator(var5).write(32L + upstreamSize, metadataBuffer, (long)this.metadata.size());
   }

   @Throws(java/io/IOException::class)
   public fun commit(upstreamSize: Long) {
      this.writeMetadata(upstreamSize);
      var var10000: RandomAccessFile = this.file;
      var10000.getChannel().force(false);
      this.writeHeader(PREFIX_CLEAN, upstreamSize, (long)this.metadata.size());
      var10000 = this.file;
      var10000.getChannel().force(false);
      synchronized (this) {
         this.complete = true;
      }

      if (this.upstream != null) {
         Util.closeQuietly(this.upstream as Closeable);
      }

      this.upstream = null;
   }

   public fun metadata(): ByteString {
      return this.metadata;
   }

   public fun newSource(): Source? {
      label26: {
         synchronized (this){} // $VF: monitorenter 

         label23: {
            try {
               if (this.file == null) {
                  break label23;
               }

               var var3: Int = this.sourceCount++;
            } catch (java.lang.Throwable var5) {
               // $VF: monitorexit
            }

            // $VF: monitorexit
         }

         // $VF: monitorexit
      }
   }

   companion object Companion private constructor() {
      private const val FILE_HEADER_SIZE: Long
      public final val PREFIX_CLEAN: ByteString
      public final val PREFIX_DIRTY: ByteString
      private const val SOURCE_FILE: Int
      private const val SOURCE_UPSTREAM: Int


      @Throws(java/io/IOException::class)
      public fun edit(file: File, upstream: Source, metadata: ByteString, bufferMaxSize: Long): Relay {
         var randomAccessFile: RandomAccessFile = new RandomAccessFile(file, "rw");
         var result: Relay = new Relay(randomAccessFile, upstream, 0L, metadata, bufferMaxSize, null);
         randomAccessFile.setLength(0L);
         Relay.access$writeHeader(result, Relay.PREFIX_DIRTY, -1L, -1L);
         return result;
      }

      @Throws(java/io/IOException::class)
      public fun read(file: File): Relay {
         var randomAccessFile: RandomAccessFile = new RandomAccessFile(file, "rw");
         var var10002: FileChannel = randomAccessFile.getChannel();
         var fileOperator: FileOperator = new FileOperator(var10002);
         var header: Buffer = new Buffer();
         fileOperator.read(0L, header, 32L);
         if (!(header.readByteString((long)Relay.PREFIX_CLEAN.size()) == Relay.PREFIX_CLEAN)) {
            throw new IOException("unreadable cache file");
         } else {
            var upstreamSize: Long = header.readLong();
            var metadataSize: Long = header.readLong();
            var metadataBuffer: Buffer = new Buffer();
            fileOperator.read(32L + upstreamSize, metadataBuffer, metadataSize);
            return new Relay(randomAccessFile, null, upstreamSize, metadataBuffer.readByteString(), 0L, null);
         }
      }
   }

   @SourceDebugExtension(["SMAP\nRelay.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Relay.kt\nokhttp3/internal/cache2/Relay$RelaySource\n+ 2 Util.kt\nokhttp3/internal/Util\n*L\n1#1,356:1\n563#2:357\n*S KotlinDebug\n*F\n+ 1 Relay.kt\nokhttp3/internal/cache2/Relay$RelaySource\n*L\n267#1:357\n*E\n"])
   internal inner class RelaySource : Source {
      private final var fileOperator: FileOperator?
      private final var sourcePos: Long
      private final val timeout: Timeout


      @Throws(java/io/IOException::class)
      public open fun read(sink: Buffer, byteCount: Long): Long {
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
         // 000: aload 1
         // 001: ldc "sink"
         // 003: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
         // 006: aload 0
         // 007: getfield okhttp3/internal/cache2/Relay$RelaySource.fileOperator Lokhttp3/internal/cache2/FileOperator;
         // 00a: ifnull 011
         // 00d: bipush 1
         // 00e: goto 012
         // 011: bipush 0
         // 012: ifne 026
         // 015: ldc "Check failed."
         // 017: astore 5
         // 019: new java/lang/IllegalStateException
         // 01c: dup
         // 01d: aload 5
         // 01f: invokevirtual java/lang/Object.toString ()Ljava/lang/String;
         // 022: invokespecial java/lang/IllegalStateException.<init> (Ljava/lang/String;)V
         // 025: athrow
         // 026: aload 0
         // 027: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 02a: astore 5
         // 02c: aload 0
         // 02d: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 030: astore 6
         // 032: aload 5
         // 034: astore 7
         // 036: aload 7
         // 038: monitorenter
         // 039: nop
         // 03a: bipush 0
         // 03b: istore 8
         // 03d: nop
         // 03e: aload 6
         // 040: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamPos ()J
         // 043: lstore 9
         // 045: aload 0
         // 046: getfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 049: lload 9
         // 04b: lcmp
         // 04c: ifne 082
         // 04f: aload 6
         // 051: invokevirtual okhttp3/internal/cache2/Relay.getComplete ()Z
         // 054: ifeq 062
         // 057: ldc2_w -1
         // 05a: lstore 15
         // 05c: aload 7
         // 05e: monitorexit
         // 05f: lload 15
         // 061: lreturn
         // 062: aload 6
         // 064: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamReader ()Ljava/lang/Thread;
         // 067: ifnull 076
         // 06a: aload 0
         // 06b: getfield okhttp3/internal/cache2/Relay$RelaySource.timeout Lokio/Timeout;
         // 06e: aload 6
         // 070: invokevirtual okio/Timeout.waitUntilNotified (Ljava/lang/Object;)V
         // 073: goto 03d
         // 076: aload 6
         // 078: invokestatic java/lang/Thread.currentThread ()Ljava/lang/Thread;
         // 07b: invokevirtual okhttp3/internal/cache2/Relay.setUpstreamReader (Ljava/lang/Thread;)V
         // 07e: bipush 1
         // 07f: goto 0d8
         // 082: aload 6
         // 084: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamPos ()J
         // 087: aload 6
         // 089: invokevirtual okhttp3/internal/cache2/Relay.getBuffer ()Lokio/Buffer;
         // 08c: invokevirtual okio/Buffer.size ()J
         // 08f: lsub
         // 090: lstore 9
         // 092: aload 0
         // 093: getfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 096: lload 9
         // 098: lcmp
         // 099: ifge 0a0
         // 09c: bipush 2
         // 09d: goto 0d8
         // 0a0: lload 2
         // 0a1: aload 6
         // 0a3: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamPos ()J
         // 0a6: aload 0
         // 0a7: getfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 0aa: lsub
         // 0ab: invokestatic java/lang/Math.min (JJ)J
         // 0ae: lstore 11
         // 0b0: aload 6
         // 0b2: invokevirtual okhttp3/internal/cache2/Relay.getBuffer ()Lokio/Buffer;
         // 0b5: aload 1
         // 0b6: aload 0
         // 0b7: getfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 0ba: lload 9
         // 0bc: lsub
         // 0bd: lload 11
         // 0bf: invokevirtual okio/Buffer.copyTo (Lokio/Buffer;JJ)Lokio/Buffer;
         // 0c2: pop
         // 0c3: aload 0
         // 0c4: aload 0
         // 0c5: getfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 0c8: lload 11
         // 0ca: ladd
         // 0cb: putfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 0ce: lload 11
         // 0d0: lstore 13
         // 0d2: aload 7
         // 0d4: monitorexit
         // 0d5: lload 13
         // 0d7: lreturn
         // 0d8: istore 8
         // 0da: aload 7
         // 0dc: monitorexit
         // 0dd: iload 8
         // 0df: goto 0ea
         // 0e2: astore 8
         // 0e4: aload 7
         // 0e6: monitorexit
         // 0e7: aload 8
         // 0e9: athrow
         // 0ea: istore 4
         // 0ec: iload 4
         // 0ee: bipush 2
         // 0ef: if_icmpne 128
         // 0f2: lload 2
         // 0f3: aload 0
         // 0f4: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 0f7: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamPos ()J
         // 0fa: aload 0
         // 0fb: getfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 0fe: lsub
         // 0ff: invokestatic java/lang/Math.min (JJ)J
         // 102: lstore 5
         // 104: aload 0
         // 105: getfield okhttp3/internal/cache2/Relay$RelaySource.fileOperator Lokhttp3/internal/cache2/FileOperator;
         // 108: dup
         // 109: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
         // 10c: ldc2_w 32
         // 10f: aload 0
         // 110: getfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 113: ladd
         // 114: aload 1
         // 115: lload 5
         // 117: invokevirtual okhttp3/internal/cache2/FileOperator.read (JLokio/Buffer;J)V
         // 11a: aload 0
         // 11b: aload 0
         // 11c: getfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 11f: lload 5
         // 121: ladd
         // 122: putfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 125: lload 5
         // 127: lreturn
         // 128: nop
         // 129: aload 0
         // 12a: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 12d: invokevirtual okhttp3/internal/cache2/Relay.getUpstream ()Lokio/Source;
         // 130: dup
         // 131: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
         // 134: aload 0
         // 135: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 138: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamBuffer ()Lokio/Buffer;
         // 13b: aload 0
         // 13c: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 13f: invokevirtual okhttp3/internal/cache2/Relay.getBufferMaxSize ()J
         // 142: invokeinterface okio/Source.read (Lokio/Buffer;J)J 4
         // 147: lstore 5
         // 149: lload 5
         // 14b: ldc2_w -1
         // 14e: lcmp
         // 14f: ifne 1af
         // 152: aload 0
         // 153: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 156: aload 0
         // 157: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 15a: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamPos ()J
         // 15d: invokevirtual okhttp3/internal/cache2/Relay.commit (J)V
         // 160: ldc2_w -1
         // 163: lstore 7
         // 165: aload 0
         // 166: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 169: astore 9
         // 16b: aload 0
         // 16c: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 16f: astore 10
         // 171: aload 9
         // 173: astore 11
         // 175: aload 11
         // 177: monitorenter
         // 178: nop
         // 179: bipush 0
         // 17a: istore 12
         // 17c: aload 10
         // 17e: aconst_null
         // 17f: invokevirtual okhttp3/internal/cache2/Relay.setUpstreamReader (Ljava/lang/Thread;)V
         // 182: aload 10
         // 184: astore 13
         // 186: bipush 0
         // 187: istore 14
         // 189: aload 13
         // 18b: ldc "null cannot be cast to non-null type java.lang.Object"
         // 18d: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;Ljava/lang/String;)V
         // 190: aload 13
         // 192: checkcast java/lang/Object
         // 195: invokevirtual java/lang/Object.notifyAll ()V
         // 198: nop
         // 199: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // 19c: astore 12
         // 19e: aload 11
         // 1a0: monitorexit
         // 1a1: goto 1ac
         // 1a4: astore 12
         // 1a6: aload 11
         // 1a8: monitorexit
         // 1a9: aload 12
         // 1ab: athrow
         // 1ac: lload 7
         // 1ae: lreturn
         // 1af: nop
         // 1b0: lload 5
         // 1b2: lload 2
         // 1b3: invokestatic java/lang/Math.min (JJ)J
         // 1b6: lstore 7
         // 1b8: aload 0
         // 1b9: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 1bc: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamBuffer ()Lokio/Buffer;
         // 1bf: aload 1
         // 1c0: lconst_0
         // 1c1: lload 7
         // 1c3: invokevirtual okio/Buffer.copyTo (Lokio/Buffer;JJ)Lokio/Buffer;
         // 1c6: pop
         // 1c7: aload 0
         // 1c8: aload 0
         // 1c9: getfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 1cc: lload 7
         // 1ce: ladd
         // 1cf: putfield okhttp3/internal/cache2/Relay$RelaySource.sourcePos J
         // 1d2: aload 0
         // 1d3: getfield okhttp3/internal/cache2/Relay$RelaySource.fileOperator Lokhttp3/internal/cache2/FileOperator;
         // 1d6: dup
         // 1d7: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
         // 1da: ldc2_w 32
         // 1dd: aload 0
         // 1de: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 1e1: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamPos ()J
         // 1e4: ladd
         // 1e5: aload 0
         // 1e6: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 1e9: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamBuffer ()Lokio/Buffer;
         // 1ec: invokevirtual okio/Buffer.clone ()Lokio/Buffer;
         // 1ef: lload 5
         // 1f1: invokevirtual okhttp3/internal/cache2/FileOperator.write (JLokio/Buffer;J)V
         // 1f4: aload 0
         // 1f5: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 1f8: astore 9
         // 1fa: aload 0
         // 1fb: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 1fe: astore 10
         // 200: aload 9
         // 202: astore 11
         // 204: aload 11
         // 206: monitorenter
         // 207: nop
         // 208: bipush 0
         // 209: istore 12
         // 20b: aload 10
         // 20d: invokevirtual okhttp3/internal/cache2/Relay.getBuffer ()Lokio/Buffer;
         // 210: aload 10
         // 212: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamBuffer ()Lokio/Buffer;
         // 215: lload 5
         // 217: invokevirtual okio/Buffer.write (Lokio/Buffer;J)V
         // 21a: aload 10
         // 21c: invokevirtual okhttp3/internal/cache2/Relay.getBuffer ()Lokio/Buffer;
         // 21f: invokevirtual okio/Buffer.size ()J
         // 222: aload 10
         // 224: invokevirtual okhttp3/internal/cache2/Relay.getBufferMaxSize ()J
         // 227: lcmp
         // 228: ifle 241
         // 22b: aload 10
         // 22d: invokevirtual okhttp3/internal/cache2/Relay.getBuffer ()Lokio/Buffer;
         // 230: aload 10
         // 232: invokevirtual okhttp3/internal/cache2/Relay.getBuffer ()Lokio/Buffer;
         // 235: invokevirtual okio/Buffer.size ()J
         // 238: aload 10
         // 23a: invokevirtual okhttp3/internal/cache2/Relay.getBufferMaxSize ()J
         // 23d: lsub
         // 23e: invokevirtual okio/Buffer.skip (J)V
         // 241: aload 10
         // 243: aload 10
         // 245: invokevirtual okhttp3/internal/cache2/Relay.getUpstreamPos ()J
         // 248: lload 5
         // 24a: ladd
         // 24b: invokevirtual okhttp3/internal/cache2/Relay.setUpstreamPos (J)V
         // 24e: nop
         // 24f: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // 252: astore 12
         // 254: aload 11
         // 256: monitorexit
         // 257: goto 262
         // 25a: astore 12
         // 25c: aload 11
         // 25e: monitorexit
         // 25f: aload 12
         // 261: athrow
         // 262: lload 7
         // 264: lstore 9
         // 266: aload 0
         // 267: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 26a: astore 11
         // 26c: aload 0
         // 26d: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 270: astore 12
         // 272: aload 11
         // 274: astore 13
         // 276: aload 13
         // 278: monitorenter
         // 279: nop
         // 27a: bipush 0
         // 27b: istore 14
         // 27d: aload 12
         // 27f: aconst_null
         // 280: invokevirtual okhttp3/internal/cache2/Relay.setUpstreamReader (Ljava/lang/Thread;)V
         // 283: aload 12
         // 285: astore 15
         // 287: bipush 0
         // 288: istore 16
         // 28a: aload 15
         // 28c: ldc "null cannot be cast to non-null type java.lang.Object"
         // 28e: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;Ljava/lang/String;)V
         // 291: aload 15
         // 293: checkcast java/lang/Object
         // 296: invokevirtual java/lang/Object.notifyAll ()V
         // 299: nop
         // 29a: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // 29d: astore 14
         // 29f: aload 13
         // 2a1: monitorexit
         // 2a2: goto 2ad
         // 2a5: astore 14
         // 2a7: aload 13
         // 2a9: monitorexit
         // 2aa: aload 14
         // 2ac: athrow
         // 2ad: lload 9
         // 2af: lreturn
         // 2b0: astore 5
         // 2b2: aload 0
         // 2b3: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 2b6: astore 6
         // 2b8: aload 0
         // 2b9: getfield okhttp3/internal/cache2/Relay$RelaySource.this$0 Lokhttp3/internal/cache2/Relay;
         // 2bc: astore 7
         // 2be: aload 6
         // 2c0: astore 8
         // 2c2: aload 8
         // 2c4: monitorenter
         // 2c5: nop
         // 2c6: bipush 0
         // 2c7: istore 9
         // 2c9: aload 7
         // 2cb: aconst_null
         // 2cc: invokevirtual okhttp3/internal/cache2/Relay.setUpstreamReader (Ljava/lang/Thread;)V
         // 2cf: aload 7
         // 2d1: astore 10
         // 2d3: bipush 0
         // 2d4: istore 11
         // 2d6: aload 10
         // 2d8: ldc "null cannot be cast to non-null type java.lang.Object"
         // 2da: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;Ljava/lang/String;)V
         // 2dd: aload 10
         // 2df: invokevirtual java/lang/Object.notifyAll ()V
         // 2e2: nop
         // 2e3: getstatic kotlin/Unit.INSTANCE Lkotlin/Unit;
         // 2e6: astore 9
         // 2e8: aload 8
         // 2ea: monitorexit
         // 2eb: goto 2f6
         // 2ee: astore 9
         // 2f0: aload 8
         // 2f2: monitorexit
         // 2f3: aload 9
         // 2f5: athrow
         // 2f6: aload 5
         // 2f8: athrow
      }

      public open fun timeout(): Timeout {
         return this.timeout;
      }

      @Throws(java/io/IOException::class)
      public open fun close() {
         if (this.fileOperator != null) {
            this.fileOperator = null;
            var fileToClose: Any = null;
            var var3: Relay = this.this$0;
            synchronized (this.this$0) {
               var3.setSourceCount(var3.getSourceCount() + -1);
               if (var3.getSourceCount() == 0) {
                  fileToClose = var3.getFile();
                  var3.setFile(null);
               }
            }

            if (fileToClose != null) {
               Util.closeQuietly(fileToClose as Closeable);
            }
         }
      }
   }
}
