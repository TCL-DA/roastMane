package okhttp3.internal.ws

import java.io.Closeable
import java.io.IOException
import java.net.ProtocolException
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util
import okio.Buffer
import okio.BufferedSource
import okio.ByteString
import okio.Buffer.UnsafeCursor

@SourceDebugExtension(["SMAP\nWebSocketReader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WebSocketReader.kt\nokhttp3/internal/ws/WebSocketReader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,298:1\n1#2:299\n*E\n"])
class WebSocketReader(isClient: Boolean,
      source: BufferedSource,
      frameCallback: okhttp3.internal.ws.WebSocketReader.FrameCallback,
      perMessageDeflate: Boolean,
      noContextTakeover: Boolean
   ) :
   Closeable {
   private final var closed: Boolean
   private final val controlFrameBuffer: Buffer
   private final val frameCallback: okhttp3.internal.ws.WebSocketReader.FrameCallback
   private final var frameLength: Long
   private final val isClient: Boolean
   private final var isControlFrame: Boolean
   private final var isFinalFrame: Boolean
   private final val maskCursor: UnsafeCursor?
   private final val maskKey: ByteArray?
   private final val messageFrameBuffer: Buffer
   private final var messageInflater: MessageInflater?
   private final val noContextTakeover: Boolean
   private final var opcode: Int
   private final val perMessageDeflate: Boolean
   private final var readingCompressedMessage: Boolean
   public final val source: BufferedSource

   init {
      this.isClient = isClient;
      this.source = source;
      this.frameCallback = frameCallback;
      this.perMessageDeflate = perMessageDeflate;
      this.noContextTakeover = noContextTakeover;
      this.controlFrameBuffer = new Buffer();
      this.messageFrameBuffer = new Buffer();
      this.maskKey = if (this.isClient) null else new byte[4];
      this.maskCursor = if (this.isClient) null else new UnsafeCursor();
   }

   @Throws(java/io/IOException::class)
   public fun processNextFrame() {
      this.readHeader();
      if (this.isControlFrame) {
         this.readControlFrame();
      } else {
         this.readMessageFrame();
      }
   }

   @Throws(java/io/IOException::class, java/net/ProtocolException::class)
   private fun readHeader() {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IndexOutOfBoundsException: Index -1 out of bounds for length 0
      //   at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:100)
      //   at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:106)
      //   at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:302)
      //   at java.base/java.util.Objects.checkIndex(Objects.java:385)
      //   at java.base/java.util.ArrayList.remove(ArrayList.java:551)
      //   at org.jetbrains.java.decompiler.util.collections.ListStack.pop(ListStack.java:31)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processBlock(ExprProcessor.java:475)
      //   at org.jetbrains.java.decompiler.modules.decompiler.ExprProcessor.processStatement(ExprProcessor.java:134)
      //
      // Bytecode:
      // 000: aload 0
      // 001: getfield okhttp3/internal/ws/WebSocketReader.closed Z
      // 004: ifeq 011
      // 007: new java/io/IOException
      // 00a: dup
      // 00b: ldc "closed"
      // 00d: invokespecial java/io/IOException.<init> (Ljava/lang/String;)V
      // 010: athrow
      // 011: bipush 0
      // 012: istore 1
      // 013: aload 0
      // 014: getfield okhttp3/internal/ws/WebSocketReader.source Lokio/BufferedSource;
      // 017: invokeinterface okio/BufferedSource.timeout ()Lokio/Timeout; 1
      // 01c: invokevirtual okio/Timeout.timeoutNanos ()J
      // 01f: lstore 2
      // 020: aload 0
      // 021: getfield okhttp3/internal/ws/WebSocketReader.source Lokio/BufferedSource;
      // 024: invokeinterface okio/BufferedSource.timeout ()Lokio/Timeout; 1
      // 029: invokevirtual okio/Timeout.clearTimeout ()Lokio/Timeout;
      // 02c: pop
      // 02d: nop
      // 02e: aload 0
      // 02f: getfield okhttp3/internal/ws/WebSocketReader.source Lokio/BufferedSource;
      // 032: invokeinterface okio/BufferedSource.readByte ()B 1
      // 037: sipush 255
      // 03a: invokestatic okhttp3/internal/Util.and (BI)I
      // 03d: istore 1
      // 03e: aload 0
      // 03f: getfield okhttp3/internal/ws/WebSocketReader.source Lokio/BufferedSource;
      // 042: invokeinterface okio/BufferedSource.timeout ()Lokio/Timeout; 1
      // 047: lload 2
      // 048: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
      // 04b: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
      // 04e: pop
      // 04f: goto 068
      // 052: astore 4
      // 054: aload 0
      // 055: getfield okhttp3/internal/ws/WebSocketReader.source Lokio/BufferedSource;
      // 058: invokeinterface okio/BufferedSource.timeout ()Lokio/Timeout; 1
      // 05d: lload 2
      // 05e: getstatic java/util/concurrent/TimeUnit.NANOSECONDS Ljava/util/concurrent/TimeUnit;
      // 061: invokevirtual okio/Timeout.timeout (JLjava/util/concurrent/TimeUnit;)Lokio/Timeout;
      // 064: pop
      // 065: aload 4
      // 067: athrow
      // 068: aload 0
      // 069: iload 1
      // 06a: bipush 15
      // 06c: iand
      // 06d: putfield okhttp3/internal/ws/WebSocketReader.opcode I
      // 070: aload 0
      // 071: iload 1
      // 072: sipush 128
      // 075: iand
      // 076: ifeq 07d
      // 079: bipush 1
      // 07a: goto 07e
      // 07d: bipush 0
      // 07e: putfield okhttp3/internal/ws/WebSocketReader.isFinalFrame Z
      // 081: aload 0
      // 082: iload 1
      // 083: bipush 8
      // 085: iand
      // 086: ifeq 08d
      // 089: bipush 1
      // 08a: goto 08e
      // 08d: bipush 0
      // 08e: putfield okhttp3/internal/ws/WebSocketReader.isControlFrame Z
      // 091: aload 0
      // 092: getfield okhttp3/internal/ws/WebSocketReader.isControlFrame Z
      // 095: ifeq 0a9
      // 098: aload 0
      // 099: getfield okhttp3/internal/ws/WebSocketReader.isFinalFrame Z
      // 09c: ifne 0a9
      // 09f: new java/net/ProtocolException
      // 0a2: dup
      // 0a3: ldc "Control frames must be final."
      // 0a5: invokespecial java/net/ProtocolException.<init> (Ljava/lang/String;)V
      // 0a8: athrow
      // 0a9: iload 1
      // 0aa: bipush 64
      // 0ac: iand
      // 0ad: ifeq 0b4
      // 0b0: bipush 1
      // 0b1: goto 0b5
      // 0b4: bipush 0
      // 0b5: istore 4
      // 0b7: aload 0
      // 0b8: getfield okhttp3/internal/ws/WebSocketReader.opcode I
      // 0bb: tableswitch 55 1 2 21 21
      // 0d0: aload 0
      // 0d1: iload 4
      // 0d3: ifeq 0eb
      // 0d6: aload 0
      // 0d7: getfield okhttp3/internal/ws/WebSocketReader.perMessageDeflate Z
      // 0da: ifne 0e7
      // 0dd: new java/net/ProtocolException
      // 0e0: dup
      // 0e1: ldc "Unexpected rsv1 flag"
      // 0e3: invokespecial java/net/ProtocolException.<init> (Ljava/lang/String;)V
      // 0e6: athrow
      // 0e7: bipush 1
      // 0e8: goto 0ec
      // 0eb: bipush 0
      // 0ec: putfield okhttp3/internal/ws/WebSocketReader.readingCompressedMessage Z
      // 0ef: goto 101
      // 0f2: iload 4
      // 0f4: ifeq 101
      // 0f7: new java/net/ProtocolException
      // 0fa: dup
      // 0fb: ldc "Unexpected rsv1 flag"
      // 0fd: invokespecial java/net/ProtocolException.<init> (Ljava/lang/String;)V
      // 100: athrow
      // 101: iload 1
      // 102: bipush 32
      // 104: iand
      // 105: ifeq 10c
      // 108: bipush 1
      // 109: goto 10d
      // 10c: bipush 0
      // 10d: istore 5
      // 10f: iload 5
      // 111: ifeq 11e
      // 114: new java/net/ProtocolException
      // 117: dup
      // 118: ldc "Unexpected rsv2 flag"
      // 11a: invokespecial java/net/ProtocolException.<init> (Ljava/lang/String;)V
      // 11d: athrow
      // 11e: iload 1
      // 11f: bipush 16
      // 121: iand
      // 122: ifeq 129
      // 125: bipush 1
      // 126: goto 12a
      // 129: bipush 0
      // 12a: istore 6
      // 12c: iload 6
      // 12e: ifeq 13b
      // 131: new java/net/ProtocolException
      // 134: dup
      // 135: ldc "Unexpected rsv3 flag"
      // 137: invokespecial java/net/ProtocolException.<init> (Ljava/lang/String;)V
      // 13a: athrow
      // 13b: aload 0
      // 13c: getfield okhttp3/internal/ws/WebSocketReader.source Lokio/BufferedSource;
      // 13f: invokeinterface okio/BufferedSource.readByte ()B 1
      // 144: sipush 255
      // 147: invokestatic okhttp3/internal/Util.and (BI)I
      // 14a: istore 7
      // 14c: iload 7
      // 14e: sipush 128
      // 151: iand
      // 152: ifeq 159
      // 155: bipush 1
      // 156: goto 15a
      // 159: bipush 0
      // 15a: istore 8
      // 15c: iload 8
      // 15e: aload 0
      // 15f: getfield okhttp3/internal/ws/WebSocketReader.isClient Z
      // 162: if_icmpne 17b
      // 165: new java/net/ProtocolException
      // 168: dup
      // 169: aload 0
      // 16a: getfield okhttp3/internal/ws/WebSocketReader.isClient Z
      // 16d: ifeq 175
      // 170: ldc "Server-sent frames must not be masked."
      // 172: goto 177
      // 175: ldc "Client-sent frames must be masked."
      // 177: invokespecial java/net/ProtocolException.<init> (Ljava/lang/String;)V
      // 17a: athrow
      // 17b: aload 0
      // 17c: iload 7
      // 17e: bipush 127
      // 180: iand
      // 181: i2l
      // 182: putfield okhttp3/internal/ws/WebSocketReader.frameLength J
      // 185: aload 0
      // 186: getfield okhttp3/internal/ws/WebSocketReader.frameLength J
      // 189: ldc2_w 126
      // 18c: lcmp
      // 18d: ifne 1a6
      // 190: aload 0
      // 191: aload 0
      // 192: getfield okhttp3/internal/ws/WebSocketReader.source Lokio/BufferedSource;
      // 195: invokeinterface okio/BufferedSource.readShort ()S 1
      // 19a: ldc 65535
      // 19c: invokestatic okhttp3/internal/Util.and (SI)I
      // 19f: i2l
      // 1a0: putfield okhttp3/internal/ws/WebSocketReader.frameLength J
      // 1a3: goto 1ed
      // 1a6: aload 0
      // 1a7: getfield okhttp3/internal/ws/WebSocketReader.frameLength J
      // 1aa: ldc2_w 127
      // 1ad: lcmp
      // 1ae: ifne 1ed
      // 1b1: aload 0
      // 1b2: aload 0
      // 1b3: getfield okhttp3/internal/ws/WebSocketReader.source Lokio/BufferedSource;
      // 1b6: invokeinterface okio/BufferedSource.readLong ()J 1
      // 1bb: putfield okhttp3/internal/ws/WebSocketReader.frameLength J
      // 1be: aload 0
      // 1bf: getfield okhttp3/internal/ws/WebSocketReader.frameLength J
      // 1c2: lconst_0
      // 1c3: lcmp
      // 1c4: ifge 1ed
      // 1c7: new java/net/ProtocolException
      // 1ca: dup
      // 1cb: new java/lang/StringBuilder
      // 1ce: dup
      // 1cf: invokespecial java/lang/StringBuilder.<init> ()V
      // 1d2: ldc "Frame length 0x"
      // 1d4: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 1d7: aload 0
      // 1d8: getfield okhttp3/internal/ws/WebSocketReader.frameLength J
      // 1db: invokestatic okhttp3/internal/Util.toHexString (J)Ljava/lang/String;
      // 1de: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 1e1: ldc " > 0x7FFFFFFFFFFFFFFF"
      // 1e3: invokevirtual java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder;
      // 1e6: invokevirtual java/lang/StringBuilder.toString ()Ljava/lang/String;
      // 1e9: invokespecial java/net/ProtocolException.<init> (Ljava/lang/String;)V
      // 1ec: athrow
      // 1ed: aload 0
      // 1ee: getfield okhttp3/internal/ws/WebSocketReader.isControlFrame Z
      // 1f1: ifeq 209
      // 1f4: aload 0
      // 1f5: getfield okhttp3/internal/ws/WebSocketReader.frameLength J
      // 1f8: ldc2_w 125
      // 1fb: lcmp
      // 1fc: ifle 209
      // 1ff: new java/net/ProtocolException
      // 202: dup
      // 203: ldc "Control frame must be less than 125B."
      // 205: invokespecial java/net/ProtocolException.<init> (Ljava/lang/String;)V
      // 208: athrow
      // 209: iload 8
      // 20b: ifeq 21f
      // 20e: aload 0
      // 20f: getfield okhttp3/internal/ws/WebSocketReader.source Lokio/BufferedSource;
      // 212: aload 0
      // 213: getfield okhttp3/internal/ws/WebSocketReader.maskKey [B
      // 216: dup
      // 217: invokestatic kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
      // 21a: invokeinterface okio/BufferedSource.readFully ([B)V 2
      // 21f: return
   }

   @Throws(java/io/IOException::class)
   private fun readControlFrame() {
      if (this.frameLength > 0L) {
         this.source.readFully(this.controlFrameBuffer, this.frameLength);
         if (!this.isClient) {
            var var10000: Buffer = this.controlFrameBuffer;
            var var10001: UnsafeCursor = this.maskCursor;
            var10000.readAndWriteUnsafe(var10001);
            this.maskCursor.seek(0L);
            var var6: WebSocketProtocol = WebSocketProtocol.INSTANCE;
            var10001 = this.maskCursor;
            var var10002: ByteArray = this.maskKey;
            var6.toggleMask(var10001, var10002);
            this.maskCursor.close();
         }
      }

      switch (this.opcode) {
         case 8:
            var code: Int = 1005;
            var reason: java.lang.String = "";
            var bufferSize: Long = this.controlFrameBuffer.size();
            if (bufferSize == 1L) {
               throw new ProtocolException("Malformed close payload length of 1.");
            }

            if (bufferSize != 0L) {
               code = this.controlFrameBuffer.readShort();
               reason = this.controlFrameBuffer.readUtf8();
               var codeExceptionMessage: java.lang.String = WebSocketProtocol.INSTANCE.closeCodeExceptionMessage(code);
               if (codeExceptionMessage != null) {
                  throw new ProtocolException(codeExceptionMessage);
               }
            }

            this.frameCallback.onReadClose(code, reason);
            this.closed = true;
            break;
         case 9:
            this.frameCallback.onReadPing(this.controlFrameBuffer.readByteString());
            break;
         case 10:
            this.frameCallback.onReadPong(this.controlFrameBuffer.readByteString());
            break;
         default:
            throw new ProtocolException("Unknown control opcode: " + Util.toHexString(this.opcode));
      }
   }

   @Throws(java/io/IOException::class)
   private fun readMessageFrame() {
      var opcode: Int = this.opcode;
      if (this.opcode != 1 && this.opcode != 2) {
         throw new ProtocolException("Unknown opcode: " + Util.toHexString(opcode));
      } else {
         this.readMessage();
         if (this.readingCompressedMessage) {
            var var10000: MessageInflater = this.messageInflater;
            if (this.messageInflater == null) {
               var var3: MessageInflater = new MessageInflater(this.noContextTakeover);
               this.messageInflater = var3;
               var10000 = var3;
            }

            var10000.inflate(this.messageFrameBuffer);
         }

         if (opcode == 1) {
            this.frameCallback.onReadMessage(this.messageFrameBuffer.readUtf8());
         } else {
            this.frameCallback.onReadMessage(this.messageFrameBuffer.readByteString());
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun readUntilNonControlFrame() {
      while (!this.closed) {
         this.readHeader();
         if (this.isControlFrame) {
            this.readControlFrame();
            continue;
         }
         break;
      }
   }

   @Throws(java/io/IOException::class)
   private fun readMessage() {
      while (!this.closed) {
         if (this.frameLength > 0L) {
            this.source.readFully(this.messageFrameBuffer, this.frameLength);
            if (!this.isClient) {
               var var10000: Buffer = this.messageFrameBuffer;
               var var10001: UnsafeCursor = this.maskCursor;
               var10000.readAndWriteUnsafe(var10001);
               this.maskCursor.seek(this.messageFrameBuffer.size() - this.frameLength);
               var var1: WebSocketProtocol = WebSocketProtocol.INSTANCE;
               var10001 = this.maskCursor;
               var var10002: ByteArray = this.maskKey;
               var1.toggleMask(var10001, var10002);
               this.maskCursor.close();
            }
         }

         if (!this.isFinalFrame) {
            this.readUntilNonControlFrame();
            if (this.opcode == 0) {
               continue;
            }

            throw new ProtocolException("Expected continuation opcode. Got: " + Util.toHexString(this.opcode));
         }

         return;
      }

      throw new IOException("closed");
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      if (this.messageInflater != null) {
         this.messageInflater.close();
      }
   }

   interface FrameCallback {
      @Throws(java/io/IOException::class)
      public abstract fun onReadMessage(text: String) {
      }

      @Throws(java/io/IOException::class)
      public abstract fun onReadMessage(bytes: ByteString) {
      }

      public abstract fun onReadPing(payload: ByteString) {
      }

      public abstract fun onReadPong(payload: ByteString) {
      }

      public abstract fun onReadClose(code: Int, reason: String) {
      }
   }
}
