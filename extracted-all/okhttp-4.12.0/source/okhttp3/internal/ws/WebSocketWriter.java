package okhttp3.internal.ws

import java.io.Closeable
import java.io.IOException
import java.util.Random
import kotlin.jvm.internal.SourceDebugExtension
import okio.Buffer
import okio.BufferedSink
import okio.ByteString
import okio.Buffer.UnsafeCursor

@SourceDebugExtension(["SMAP\nWebSocketWriter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 WebSocketWriter.kt\nokhttp3/internal/ws/WebSocketWriter\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,202:1\n1#2:203\n*E\n"])
class WebSocketWriter(isClient: Boolean, sink: BufferedSink, random: Random, perMessageDeflate: Boolean, noContextTakeover: Boolean, minimumDeflateSize: Long) :
   Closeable {
   private final val isClient: Boolean
   private final val maskCursor: UnsafeCursor?
   private final val maskKey: ByteArray?
   private final val messageBuffer: Buffer
   private final var messageDeflater: MessageDeflater?
   private final val minimumDeflateSize: Long
   private final val noContextTakeover: Boolean
   private final val perMessageDeflate: Boolean
   public final val random: Random
   public final val sink: BufferedSink
   private final val sinkBuffer: Buffer
   private final var writerClosed: Boolean

   init {
      this.isClient = isClient;
      this.sink = sink;
      this.random = random;
      this.perMessageDeflate = perMessageDeflate;
      this.noContextTakeover = noContextTakeover;
      this.minimumDeflateSize = minimumDeflateSize;
      this.messageBuffer = new Buffer();
      this.sinkBuffer = this.sink.getBuffer();
      this.maskKey = if (this.isClient) new byte[4] else null;
      this.maskCursor = if (this.isClient) new UnsafeCursor() else null;
   }

   @Throws(java/io/IOException::class)
   public fun writePing(payload: ByteString) {
      this.writeControlFrame(9, payload);
   }

   @Throws(java/io/IOException::class)
   public fun writePong(payload: ByteString) {
      this.writeControlFrame(10, payload);
   }

   @Throws(java/io/IOException::class)
   public fun writeClose(code: Int, reason: ByteString?) {
      label48: {
         var payload: ByteString = ByteString.EMPTY;
         if (code != 0 || reason != null) {
            if (code != 0) {
               WebSocketProtocol.INSTANCE.validateCloseCode(code);
            }

            var `$this$writeClose_u24lambda_u240`: Buffer = new Buffer();
            `$this$writeClose_u24lambda_u240`.writeShort(code);
            if (reason != null) {
               `$this$writeClose_u24lambda_u240`.write(reason);
            }

            payload = `$this$writeClose_u24lambda_u240`.readByteString();
         }

         try {
            this.writeControlFrame(8, payload);
         } catch (java.lang.Throwable var7) {
            this.writerClosed = true;
         }

         this.writerClosed = true;
      }
   }

   @Throws(java/io/IOException::class)
   private fun writeControlFrame(opcode: Int, payload: ByteString) {
      if (this.writerClosed) {
         throw new IOException("closed");
      } else {
         var length: Int = payload.size();
         if ((long)length > 125L) {
            throw new IllegalArgumentException("Payload size must be less than or equal to 125".toString());
         } else {
            this.sinkBuffer.writeByte(128 or opcode);
            if (this.isClient) {
               this.sinkBuffer.writeByte(length or 128);
               var var10000: Random = this.random;
               var var10001: ByteArray = this.maskKey;
               var10000.nextBytes(var10001);
               this.sinkBuffer.write(this.maskKey);
               if (length > 0) {
                  var payloadStart: Long = this.sinkBuffer.size();
                  this.sinkBuffer.write(payload);
                  var var10: Buffer = this.sinkBuffer;
                  var var11: UnsafeCursor = this.maskCursor;
                  var10.readAndWriteUnsafe(var11);
                  this.maskCursor.seek(payloadStart);
                  WebSocketProtocol.INSTANCE.toggleMask(this.maskCursor, this.maskKey);
                  this.maskCursor.close();
               }
            } else {
               this.sinkBuffer.writeByte(length);
               this.sinkBuffer.write(payload);
            }

            this.sink.flush();
         }
      }
   }

   @Throws(java/io/IOException::class)
   public fun writeMessageFrame(formatOpcode: Int, data: ByteString) {
      if (this.writerClosed) {
         throw new IOException("closed");
      } else {
         this.messageBuffer.write(data);
         var b0: Int = formatOpcode or 128;
         if (this.perMessageDeflate && (long)data.size() >= this.minimumDeflateSize) {
            var var10000: MessageDeflater = this.messageDeflater;
            if (this.messageDeflater == null) {
               var b1: MessageDeflater = new MessageDeflater(this.noContextTakeover);
               this.messageDeflater = b1;
               var10000 = b1;
            }

            var10000.deflate(this.messageBuffer);
            b0 |= 64;
         }

         var var9: Long = this.messageBuffer.size();
         this.sinkBuffer.writeByte(b0);
         var var10: Int = 0;
         if (this.isClient) {
            var10 = 0 or 128;
         }

         if (var9 <= 125L) {
            this.sinkBuffer.writeByte(var10 or (int)var9);
         } else if (var9 <= 65535L) {
            this.sinkBuffer.writeByte(var10 or 126);
            this.sinkBuffer.writeShort((int)var9);
         } else {
            this.sinkBuffer.writeByte(var10 or 127);
            this.sinkBuffer.writeLong(var9);
         }

         if (this.isClient) {
            var var14: Random = this.random;
            var var10001: ByteArray = this.maskKey;
            var14.nextBytes(var10001);
            this.sinkBuffer.write(this.maskKey);
            if (var9 > 0L) {
               var var15: Buffer = this.messageBuffer;
               var var16: UnsafeCursor = this.maskCursor;
               var15.readAndWriteUnsafe(var16);
               this.maskCursor.seek(0L);
               WebSocketProtocol.INSTANCE.toggleMask(this.maskCursor, this.maskKey);
               this.maskCursor.close();
            }
         }

         this.sinkBuffer.write(this.messageBuffer, var9);
         this.sink.emit();
      }
   }

   public override fun close() {
      if (this.messageDeflater != null) {
         this.messageDeflater.close();
      }
   }
}
