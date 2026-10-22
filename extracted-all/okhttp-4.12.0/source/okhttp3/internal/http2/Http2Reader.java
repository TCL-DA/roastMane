package okhttp3.internal.http2

import java.io.Closeable
import java.io.EOFException
import java.io.IOException
import java.util.logging.Level
import java.util.logging.Logger
import okhttp3.internal.Util
import okhttp3.internal.http2.Hpack.Reader
import okio.Buffer
import okio.BufferedSource
import okio.ByteString
import okio.Source
import okio.Timeout

class Http2Reader(source: BufferedSource, client: Boolean) : Closeable {
   @JvmStatic
   public Http2Reader.Companion Companion = new Http2Reader.Companion(null);
   @JvmStatic
   private Logger logger;

   private final val client: Boolean
   private final val continuation: okhttp3.internal.http2.Http2Reader.ContinuationSource
   private final val hpackReader: Reader
   private final val source: BufferedSource

   init {
      this.source = source;
      this.client = client;
      this.continuation = new Http2Reader.ContinuationSource(this.source);
      this.hpackReader = new Hpack.Reader(this.continuation, 4096, 0, 4, null);
   }

   @Throws(java/io/IOException::class)
   public fun readConnectionPreface(handler: okhttp3.internal.http2.Http2Reader.Handler) {
      if (this.client) {
         if (!this.nextFrame(true, handler)) {
            throw new IOException("Required SETTINGS preface not received");
         }
      } else {
         var connectionPreface: ByteString = this.source.readByteString((long)Http2.CONNECTION_PREFACE.size());
         if (logger.isLoggable(Level.FINE)) {
            logger.fine(Util.format("<< CONNECTION " + connectionPreface.hex()));
         }

         if (!(Http2.CONNECTION_PREFACE == connectionPreface)) {
            throw new IOException("Expected a connection header but was " + connectionPreface.utf8());
         }
      }
   }

   @Throws(java/io/IOException::class)
   public fun nextFrame(requireSettings: Boolean, handler: okhttp3.internal.http2.Http2Reader.Handler): Boolean {
      try {
         this.source.require(9L);
      } catch (EOFException var7) {
         return false;
      }

      var length: Int = Util.readMedium(this.source);
      if (length > 16384) {
         throw new IOException("FRAME_SIZE_ERROR: " + length);
      } else {
         var type: Int = Util.and(this.source.readByte(), 255);
         var flags: Int = Util.and(this.source.readByte(), 255);
         var streamId: Int = this.source.readInt() and Integer.MAX_VALUE;
         if (logger.isLoggable(Level.FINE)) {
            logger.fine(Http2.INSTANCE.frameLog(true, streamId, length, type, flags));
         }

         if (requireSettings && type != 4) {
            throw new IOException("Expected a SETTINGS frame but was " + Http2.INSTANCE.formattedType$okhttp(type));
         } else {
            switch (type) {
               case 0:
                  this.readData(handler, length, flags, streamId);
                  break;
               case 1:
                  this.readHeaders(handler, length, flags, streamId);
                  break;
               case 2:
                  this.readPriority(handler, length, flags, streamId);
                  break;
               case 3:
                  this.readRstStream(handler, length, flags, streamId);
                  break;
               case 4:
                  this.readSettings(handler, length, flags, streamId);
                  break;
               case 5:
                  this.readPushPromise(handler, length, flags, streamId);
                  break;
               case 6:
                  this.readPing(handler, length, flags, streamId);
                  break;
               case 7:
                  this.readGoAway(handler, length, flags, streamId);
                  break;
               case 8:
                  this.readWindowUpdate(handler, length, flags, streamId);
                  break;
               default:
                  this.source.skip((long)length);
            }

            return true;
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun readHeaders(handler: okhttp3.internal.http2.Http2Reader.Handler, length: Int, flags: Int, streamId: Int) {
      if (streamId == 0) {
         throw new IOException("PROTOCOL_ERROR: TYPE_HEADERS streamId == 0");
      } else {
         var endStream: Boolean = (flags and 1) != 0;
         var padding: Int = if ((flags and 8) != 0) Util.and(this.source.readByte(), 255) else 0;
         var headerBlockLength: Int = length;
         if ((flags and 32) != 0) {
            this.readPriority(handler, streamId);
            headerBlockLength = length - 5;
         }

         handler.headers(
            endStream, streamId, -1, this.readHeaderBlock(Companion.lengthWithoutPadding(headerBlockLength, flags, padding), padding, flags, streamId)
         );
      }
   }

   @Throws(java/io/IOException::class)
   private fun readHeaderBlock(length: Int, padding: Int, flags: Int, streamId: Int): List<Header> {
      this.continuation.setLeft(length);
      this.continuation.setLength(this.continuation.getLeft());
      this.continuation.setPadding(padding);
      this.continuation.setFlags(flags);
      this.continuation.setStreamId(streamId);
      this.hpackReader.readHeaders();
      return this.hpackReader.getAndResetHeaderList();
   }

   @Throws(java/io/IOException::class)
   private fun readData(handler: okhttp3.internal.http2.Http2Reader.Handler, length: Int, flags: Int, streamId: Int) {
      if (streamId == 0) {
         throw new IOException("PROTOCOL_ERROR: TYPE_DATA streamId == 0");
      } else {
         var inFinished: Boolean = (flags and 1) != 0;
         if ((flags and 32) != 0) {
            throw new IOException("PROTOCOL_ERROR: FLAG_COMPRESSED without SETTINGS_COMPRESS_DATA");
         } else {
            var padding: Int = if ((flags and 8) != 0) Util.and(this.source.readByte(), 255) else 0;
            handler.data(inFinished, streamId, this.source, Companion.lengthWithoutPadding(length, flags, padding));
            this.source.skip((long)padding);
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun readPriority(handler: okhttp3.internal.http2.Http2Reader.Handler, length: Int, flags: Int, streamId: Int) {
      if (length != 5) {
         throw new IOException("TYPE_PRIORITY length: " + length + " != 5");
      } else if (streamId == 0) {
         throw new IOException("TYPE_PRIORITY streamId == 0");
      } else {
         this.readPriority(handler, streamId);
      }
   }

   @Throws(java/io/IOException::class)
   private fun readPriority(handler: okhttp3.internal.http2.Http2Reader.Handler, streamId: Int) {
      var w1: Int = this.source.readInt();
      handler.priority(streamId, w1 and Integer.MAX_VALUE, Util.and(this.source.readByte(), 255) + 1, (w1 and Integer.MIN_VALUE) != 0);
   }

   @Throws(java/io/IOException::class)
   private fun readRstStream(handler: okhttp3.internal.http2.Http2Reader.Handler, length: Int, flags: Int, streamId: Int) {
      if (length != 4) {
         throw new IOException("TYPE_RST_STREAM length: " + length + " != 4");
      } else if (streamId == 0) {
         throw new IOException("TYPE_RST_STREAM streamId == 0");
      } else {
         var errorCodeInt: Int = this.source.readInt();
         var var10000: ErrorCode = ErrorCode.Companion.fromHttp2(errorCodeInt);
         if (var10000 == null) {
            throw new IOException("TYPE_RST_STREAM unexpected error code: " + errorCodeInt);
         } else {
            handler.rstStream(streamId, var10000);
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun readSettings(handler: okhttp3.internal.http2.Http2Reader.Handler, length: Int, flags: Int, streamId: Int) {
      if (streamId != 0) {
         throw new IOException("TYPE_SETTINGS streamId != 0");
      } else if ((flags and 1) != 0) {
         if (length != 0) {
            throw new IOException("FRAME_SIZE_ERROR ack frame should be empty!");
         } else {
            handler.ackSettings();
         }
      } else if (length % 6 != 0) {
         throw new IOException("TYPE_SETTINGS length % 6 != 0: " + length);
      } else {
         var settings: Settings = new Settings();
         var var6: IntProgression = RangesKt.step(RangesKt.until(0, length) as IntProgression, 6);
         var i: Int = var6.getFirst();
         var var8: Int = var6.getLast();
         var var9: Int = var6.getStep();
         if (var9 > 0 && i <= var8 || var9 < 0 && var8 <= i) {
            while (true) {
               var id: Int = Util.and(this.source.readShort(), 65535);
               var value: Int = this.source.readInt();
               switch (id) {
                  case 1:
                  case 6:
                  default:
                     break;
                  case 2:
                     if (value != 0 && value != 1) {
                        throw new IOException("PROTOCOL_ERROR SETTINGS_ENABLE_PUSH != 0 or 1");
                     }
                     break;
                  case 3:
                     id = 4;
                     break;
                  case 4:
                     id = 7;
                     if (value < 0) {
                        throw new IOException("PROTOCOL_ERROR SETTINGS_INITIAL_WINDOW_SIZE > 2^31 - 1");
                     }
                     break;
                  case 5:
                     if (value < 16384 || value > 16777215) {
                        throw new IOException("PROTOCOL_ERROR SETTINGS_MAX_FRAME_SIZE: " + value);
                     }
               }

               settings.set(id, value);
               if (i == var8) {
                  break;
               }

               i += var9;
            }
         }

         handler.settings(false, settings);
      }
   }

   @Throws(java/io/IOException::class)
   private fun readPushPromise(handler: okhttp3.internal.http2.Http2Reader.Handler, length: Int, flags: Int, streamId: Int) {
      if (streamId == 0) {
         throw new IOException("PROTOCOL_ERROR: TYPE_PUSH_PROMISE streamId == 0");
      } else {
         var padding: Int = if ((flags and 8) != 0) Util.and(this.source.readByte(), 255) else 0;
         handler.pushPromise(
            streamId,
            this.source.readInt() and Integer.MAX_VALUE,
            this.readHeaderBlock(Companion.lengthWithoutPadding(length - 4, flags, padding), padding, flags, streamId)
         );
      }
   }

   @Throws(java/io/IOException::class)
   private fun readPing(handler: okhttp3.internal.http2.Http2Reader.Handler, length: Int, flags: Int, streamId: Int) {
      if (length != 8) {
         throw new IOException("TYPE_PING length != 8: " + length);
      } else if (streamId != 0) {
         throw new IOException("TYPE_PING streamId != 0");
      } else {
         handler.ping((flags and 1) != 0, this.source.readInt(), this.source.readInt());
      }
   }

   @Throws(java/io/IOException::class)
   private fun readGoAway(handler: okhttp3.internal.http2.Http2Reader.Handler, length: Int, flags: Int, streamId: Int) {
      if (length < 8) {
         throw new IOException("TYPE_GOAWAY length < 8: " + length);
      } else if (streamId != 0) {
         throw new IOException("TYPE_GOAWAY streamId != 0");
      } else {
         var lastStreamId: Int = this.source.readInt();
         var errorCodeInt: Int = this.source.readInt();
         var opaqueDataLength: Int = length - 8;
         var var10000: ErrorCode = ErrorCode.Companion.fromHttp2(errorCodeInt);
         if (var10000 == null) {
            throw new IOException("TYPE_GOAWAY unexpected error code: " + errorCodeInt);
         } else {
            var debugData: ByteString = ByteString.EMPTY;
            if (opaqueDataLength > 0) {
               debugData = this.source.readByteString((long)opaqueDataLength);
            }

            handler.goAway(lastStreamId, var10000, debugData);
         }
      }
   }

   @Throws(java/io/IOException::class)
   private fun readWindowUpdate(handler: okhttp3.internal.http2.Http2Reader.Handler, length: Int, flags: Int, streamId: Int) {
      if (length != 4) {
         throw new IOException("TYPE_WINDOW_UPDATE length !=4: " + length);
      } else {
         var increment: Long = Util.and(this.source.readInt(), 2147483647L);
         if (increment == 0L) {
            throw new IOException("windowSizeIncrement was 0");
         } else {
            handler.windowUpdate(streamId, increment);
         }
      }
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      this.source.close();
   }

   @JvmStatic
   fun {
      var var10000: Logger = Logger.getLogger(Http2.class.getName());
      logger = var10000;
   }

   companion object Companion private constructor() {
      public final val logger: Logger


      @Throws(java/io/IOException::class)
      public fun lengthWithoutPadding(length: Int, flags: Int, padding: Int): Int {
         var result: Int = length;
         if ((flags and 8) != 0) {
            result = length - 1;
         }

         if (padding > result) {
            throw new IOException("PROTOCOL_ERROR padding " + padding + " > remaining length " + result);
         } else {
            return result - padding;
         }
      }
   }

   internal class ContinuationSource(source: BufferedSource) : Source {
      public final var flags: Int
         internal set
      public final var left: Int
         internal set
      public final var length: Int
         internal set
      public final var padding: Int
         internal set
      private final val source: BufferedSource
      public final var streamId: Int
         internal set

      init {
         this.source = source;
      }

      @Throws(java/io/IOException::class)
      public open fun read(sink: Buffer, byteCount: Long): Long {
         while (this.left == 0) {
            this.source.skip((long)this.padding);
            this.padding = 0;
            if ((this.flags and 4) != 0) {
               return -1L;
            }

            this.readContinuationHeader();
         }

         var read: Long = this.source.read(sink, Math.min(byteCount, (long)this.left));
         if (read == -1L) {
            return -1L;
         } else {
            this.left -= (int)read;
            return read;
         }
      }

      public open fun timeout(): Timeout {
         return this.source.timeout();
      }

      @Throws(java/io/IOException::class)
      public open fun close() {
      }

      @Throws(java/io/IOException::class)
      private fun readContinuationHeader() {
         var previousStreamId: Int = this.streamId;
         this.left = Util.readMedium(this.source);
         this.length = this.left;
         var type: Int = Util.and(this.source.readByte(), 255);
         this.flags = Util.and(this.source.readByte(), 255);
         if (Http2Reader.Companion.getLogger().isLoggable(Level.FINE)) {
            Http2Reader.Companion.getLogger().fine(Http2.INSTANCE.frameLog(true, this.streamId, this.length, type, this.flags));
         }

         this.streamId = this.source.readInt() and Integer.MAX_VALUE;
         if (type != 9) {
            throw new IOException(type + " != TYPE_CONTINUATION");
         } else if (this.streamId != previousStreamId) {
            throw new IOException("TYPE_CONTINUATION streamId changed");
         }
      }
   }

   interface Handler {
      @Throws(java/io/IOException::class)
      public abstract fun data(inFinished: Boolean, streamId: Int, source: BufferedSource, length: Int) {
      }

      public abstract fun headers(inFinished: Boolean, streamId: Int, associatedStreamId: Int, headerBlock: List<Header>) {
      }

      public abstract fun rstStream(streamId: Int, errorCode: ErrorCode) {
      }

      public abstract fun settings(clearPrevious: Boolean, settings: Settings) {
      }

      public abstract fun ackSettings() {
      }

      public abstract fun ping(ack: Boolean, payload1: Int, payload2: Int) {
      }

      public abstract fun goAway(lastGoodStreamId: Int, errorCode: ErrorCode, debugData: ByteString) {
      }

      public abstract fun windowUpdate(streamId: Int, windowSizeIncrement: Long) {
      }

      public abstract fun priority(streamId: Int, streamDependency: Int, weight: Int, exclusive: Boolean) {
      }

      @Throws(java/io/IOException::class)
      public abstract fun pushPromise(streamId: Int, promisedStreamId: Int, requestHeaders: List<Header>) {
      }

      public abstract fun alternateService(streamId: Int, origin: String, protocol: ByteString, host: String, port: Int, maxAge: Long) {
      }
   }
}
