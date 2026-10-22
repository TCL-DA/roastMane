package okhttp3.internal.http2

import java.io.Closeable
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util
import okhttp3.internal.http2.Hpack.Writer
import okio.Buffer
import okio.BufferedSink

@SourceDebugExtension(["SMAP\nHttp2Writer.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Http2Writer.kt\nokhttp3/internal/http2/Http2Writer\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,317:1\n1#2:318\n*E\n"])
class Http2Writer(sink: BufferedSink, client: Boolean) : Closeable {
   @JvmStatic
   public Http2Writer.Companion Companion = new Http2Writer.Companion(null);
   @JvmStatic
   private Logger logger = Logger.getLogger(Http2.class.getName());

   private final val client: Boolean
   private final var closed: Boolean
   private final val hpackBuffer: Buffer
   public final val hpackWriter: Writer
   private final var maxFrameSize: Int
   private final val sink: BufferedSink

   init {
      this.sink = sink;
      this.client = client;
      this.hpackBuffer = new Buffer();
      this.maxFrameSize = 16384;
      this.hpackWriter = new Hpack.Writer(0, false, this.hpackBuffer, 3, null);
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun connectionPreface() {
      if (this.closed) {
         throw new IOException("closed");
      } else if (this.client) {
         if (logger.isLoggable(Level.FINE)) {
            logger.fine(Util.format(">> CONNECTION " + Http2.CONNECTION_PREFACE.hex()));
         }

         this.sink.write(Http2.CONNECTION_PREFACE);
         this.sink.flush();
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun applyAndAckSettings(peerSettings: Settings) {
      if (this.closed) {
         throw new IOException("closed");
      } else {
         this.maxFrameSize = peerSettings.getMaxFrameSize(this.maxFrameSize);
         if (peerSettings.getHeaderTableSize() != -1) {
            this.hpackWriter.resizeHeaderTable(peerSettings.getHeaderTableSize());
         }

         this.frameHeader(0, 0, 4, 1);
         this.sink.flush();
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun pushPromise(streamId: Int, promisedStreamId: Int, requestHeaders: List<Header>) {
      if (this.closed) {
         throw new IOException("closed");
      } else {
         this.hpackWriter.writeHeaders(requestHeaders);
         var byteCount: Long = this.hpackBuffer.size();
         var length: Int = (int)Math.min((long)this.maxFrameSize - 4L, byteCount);
         this.frameHeader(streamId, length + 4, 5, if (byteCount == (long)length) 4 else 0);
         this.sink.writeInt(promisedStreamId and Integer.MAX_VALUE);
         this.sink.write(this.hpackBuffer, (long)length);
         if (byteCount > (long)length) {
            this.writeContinuationFrames(streamId, byteCount - (long)length);
         }
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun flush() {
      if (this.closed) {
         throw new IOException("closed");
      } else {
         this.sink.flush();
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun rstStream(streamId: Int, errorCode: ErrorCode) {
      if (this.closed) {
         throw new IOException("closed");
      } else if (errorCode.getHttpCode() == -1) {
         throw new IllegalArgumentException("Failed requirement.".toString());
      } else {
         this.frameHeader(streamId, 4, 3, 0);
         this.sink.writeInt(errorCode.getHttpCode());
         this.sink.flush();
      }
   }

   public fun maxDataLength(): Int {
      return this.maxFrameSize;
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun data(outFinished: Boolean, streamId: Int, source: Buffer?, byteCount: Int) {
      if (this.closed) {
         throw new IOException("closed");
      } else {
         var flags: Int = 0;
         if (outFinished) {
            flags = 0 or 1;
         }

         this.dataFrame(streamId, flags, source, byteCount);
      }
   }

   @Throws(java/io/IOException::class)
   public fun dataFrame(streamId: Int, flags: Int, buffer: Buffer?, byteCount: Int) {
      this.frameHeader(streamId, byteCount, 0, flags);
      if (byteCount > 0) {
         var var10000: BufferedSink = this.sink;
         var10000.write(buffer, (long)byteCount);
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun settings(settings: Settings) {
      if (this.closed) {
         throw new IOException("closed");
      } else {
         this.frameHeader(0, settings.size() * 6, 4, 0);

         for (int i = 0; i < 10; i++) {
            if (settings.isSet(i)) {
               var var10000: Int;
               switch (i) {
                  case 4:
                     var10000 = 3;
                     break;
                  case 5:
                  case 6:
                  default:
                     var10000 = i;
                     break;
                  case 7:
                     var10000 = 4;
               }

               this.sink.writeShort(var10000);
               this.sink.writeInt(settings.get(i));
            }
         }

         this.sink.flush();
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun ping(ack: Boolean, payload1: Int, payload2: Int) {
      if (this.closed) {
         throw new IOException("closed");
      } else {
         this.frameHeader(0, 8, 6, if (ack) 1 else 0);
         this.sink.writeInt(payload1);
         this.sink.writeInt(payload2);
         this.sink.flush();
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun goAway(lastGoodStreamId: Int, errorCode: ErrorCode, debugData: ByteArray) {
      if (this.closed) {
         throw new IOException("closed");
      } else if (errorCode.getHttpCode() == -1) {
         throw new IllegalArgumentException("errorCode.httpCode == -1".toString());
      } else {
         this.frameHeader(0, 8 + debugData.length, 7, 0);
         this.sink.writeInt(lastGoodStreamId);
         this.sink.writeInt(errorCode.getHttpCode());
         if (debugData.length != 0) {
            this.sink.write(debugData);
         }

         this.sink.flush();
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun windowUpdate(streamId: Int, windowSizeIncrement: Long) {
      if (this.closed) {
         throw new IOException("closed");
      } else if (windowSizeIncrement == 0L || windowSizeIncrement > 2147483647L) {
         throw new IllegalArgumentException(("windowSizeIncrement == 0 || windowSizeIncrement > 0x7fffffffL: " + windowSizeIncrement).toString());
      } else {
         this.frameHeader(streamId, 4, 8, 0);
         this.sink.writeInt((int)windowSizeIncrement);
         this.sink.flush();
      }
   }

   @Throws(java/io/IOException::class)
   public fun frameHeader(streamId: Int, length: Int, type: Int, flags: Int) {
      if (logger.isLoggable(Level.FINE)) {
         logger.fine(Http2.INSTANCE.frameLog(false, streamId, length, type, flags));
      }

      if (length > this.maxFrameSize) {
         throw new IllegalArgumentException(("FRAME_SIZE_ERROR length > " + this.maxFrameSize + ": " + length).toString());
      } else if ((streamId and Integer.MIN_VALUE) != 0) {
         throw new IllegalArgumentException(("reserved bit set: " + streamId).toString());
      } else {
         Util.writeMedium(this.sink, length);
         this.sink.writeByte(type and 255);
         this.sink.writeByte(flags and 255);
         this.sink.writeInt(streamId and Integer.MAX_VALUE);
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public override fun close() {
      this.closed = true;
      this.sink.close();
   }

   @Throws(java/io/IOException::class)
   private fun writeContinuationFrames(streamId: Int, byteCount: Long) {
      var byteCountx: Long = byteCount;

      while (byteCountx > 0L) {
         var length: Long = Math.min((long)this.maxFrameSize, byteCountx);
         byteCountx -= length;
         this.frameHeader(streamId, (int)length, 9, if (byteCountx == 0L) 4 else 0);
         this.sink.write(this.hpackBuffer, length);
      }
   }

   @Synchronized
   @Throws(java/io/IOException::class)
   public fun headers(outFinished: Boolean, streamId: Int, headerBlock: List<Header>) {
      if (this.closed) {
         throw new IOException("closed");
      } else {
         this.hpackWriter.writeHeaders(headerBlock);
         var byteCount: Long = this.hpackBuffer.size();
         var length: Long = Math.min((long)this.maxFrameSize, byteCount);
         var flags: Int = if (byteCount == length) 4 else 0;
         if (outFinished) {
            flags |= 1;
         }

         this.frameHeader(streamId, (int)length, 1, flags);
         this.sink.write(this.hpackBuffer, length);
         if (byteCount > length) {
            this.writeContinuationFrames(streamId, byteCount - length);
         }
      }
   }

   companion object Companion private constructor() {
      private final val logger: Logger

   }
}
