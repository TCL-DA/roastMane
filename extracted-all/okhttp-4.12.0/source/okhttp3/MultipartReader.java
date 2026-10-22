package okhttp3

import java.io.Closeable
import java.net.ProtocolException
import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.http1.HeadersReader
import okio.Buffer
import okio.BufferedSource
import okio.ByteString
import okio.Okio
import okio.Options
import okio.Source
import okio.Timeout

@SourceDebugExtension(["SMAP\nMultipartReader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartReader.kt\nokhttp3/MultipartReader\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,210:1\n1#2:211\n*E\n"])
class MultipartReader(source: BufferedSource, boundary: String) : Closeable {
   @JvmStatic
   public MultipartReader.Companion Companion = new MultipartReader.Companion(null);
   @JvmStatic
   private Options afterBoundaryOptions = Options.Companion
      .of(
         new ByteString[]{
            ByteString.Companion.encodeUtf8("\r\n"),
            ByteString.Companion.encodeUtf8("--"),
            ByteString.Companion.encodeUtf8(" "),
            ByteString.Companion.encodeUtf8("\t")
         }
      );

   public final val boundary: String
   private final var closed: Boolean
   private final val crlfDashDashBoundary: ByteString
   private final var currentPart: okhttp3.MultipartReader.PartSource?
   private final val dashDashBoundary: ByteString
   private final var noMoreParts: Boolean
   private final var partCount: Int
   private final val source: BufferedSource

   init {
      this.source = source;
      this.boundary = boundary;
      this.dashDashBoundary = new Buffer().writeUtf8("--").writeUtf8(this.boundary).readByteString();
      this.crlfDashDashBoundary = new Buffer().writeUtf8("\r\n--").writeUtf8(this.boundary).readByteString();
   }

   public constructor(response: ResponseBody)  {
      var var10001: BufferedSource = response.source();
      var var10002: MediaType = response.contentType();
      if (var10002 != null) {
         var var2: java.lang.String = var10002.parameter("boundary");
         if (var2 != null) {
            this(var10001, var2);
            return;
         }
      }

      throw new ProtocolException("expected the Content-Type to have a boundary parameter");
   }

   @Throws(java/io/IOException::class)
   public fun nextPart(): okhttp3.MultipartReader.Part? {
      if (this.closed) {
         throw new IllegalStateException("closed".toString());
      } else if (this.noMoreParts) {
         return null;
      } else {
         if (this.partCount == 0 && this.source.rangeEquals(0L, this.dashDashBoundary)) {
            this.source.skip((long)this.dashDashBoundary.size());
         } else {
            while (true) {
               var whitespace: Long = this.currentPartBytesRemaining(8192L);
               if (whitespace == 0L) {
                  this.source.skip((long)this.crlfDashDashBoundary.size());
                  break;
               }

               this.source.skip(whitespace);
            }
         }

         var var4: Boolean = false;

         while (true) {
            switch (this.source.select(afterBoundaryOptions)) {
               case -1:
                  throw new ProtocolException("unexpected characters after boundary");
               case 0:
                  var partSource: Int = this.partCount++;
                  var headers: Headers = new HeadersReader(this.source).readHeaders();
                  var var7: MultipartReader.PartSource = new MultipartReader.PartSource(this);
                  this.currentPart = var7;
                  return new MultipartReader.Part(headers, Okio.buffer(var7));
               case 1:
                  if (var4) {
                     throw new ProtocolException("unexpected characters after boundary");
                  }

                  if (this.partCount == 0) {
                     throw new ProtocolException("expected at least 1 part");
                  }

                  this.noMoreParts = true;
                  return null;
               case 2:
               case 3:
                  var4 = true;
               default:
            }
         }
      }
   }

   private fun currentPartBytesRemaining(maxResult: Long): Long {
      this.source.require((long)this.crlfDashDashBoundary.size());
      var delimiterIndex: Long = this.source.getBuffer().indexOf(this.crlfDashDashBoundary);
      return if (delimiterIndex == -1L)
         Math.min(maxResult, this.source.getBuffer().size() - (long)this.crlfDashDashBoundary.size() + 1L)
         else
         Math.min(maxResult, delimiterIndex);
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      if (!this.closed) {
         this.closed = true;
         this.currentPart = null;
         this.source.close();
      }
   }

   internal companion object Companion private constructor() {
      public final val afterBoundaryOptions: Options

   }

   class Part(headers: Headers, body: BufferedSource) : Closeable {
      public final val body: BufferedSource
      public final val headers: Headers

      init {
         this.headers = headers;
         this.body = body;
      }

      public override fun close() {
         this.body.close();
      }
   }

   @SourceDebugExtension(["SMAP\nMultipartReader.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MultipartReader.kt\nokhttp3/MultipartReader$PartSource\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Timeout.kt\nokio/Timeout\n*L\n1#1,210:1\n1#2:211\n268#3,26:212\n*S KotlinDebug\n*F\n+ 1 MultipartReader.kt\nokhttp3/MultipartReader$PartSource\n*L\n159#1:212,26\n*E\n"])
   private inner class PartSource : Source {
      private final val timeout: Timeout

      init {
         this.this$0 = `this$0`;
         this.timeout = new Timeout();
      }

      public open fun close() {
         if (MultipartReader.access$getCurrentPart$p(this.this$0) == this) {
            MultipartReader.access$setCurrentPart$p(this.this$0, null);
         }
      }

      public open fun read(sink: Buffer, byteCount: Long): Long {
         if (byteCount < 0L) {
            throw new IllegalArgumentException(("byteCount < 0: " + byteCount).toString());
         } else if (!(MultipartReader.access$getCurrentPart$p(this.this$0) == this)) {
            throw new IllegalStateException("closed".toString());
         } else {
            var `this_$iv`: Timeout = MultipartReader.access$getSource$p(this.this$0).timeout();
            var `other$iv`: Timeout = this.timeout;
            var var6: MultipartReader = this.this$0;
            var `originalTimeout$iv`: Long = `this_$iv`.timeoutNanos();
            `this_$iv`.timeout(Timeout.Companion.minTimeout(`other$iv`.timeoutNanos(), `this_$iv`.timeoutNanos()), TimeUnit.NANOSECONDS);
            label59:
            if (!`this_$iv`.hasDeadline()) {
               if (`other$iv`.hasDeadline()) {
                  `this_$iv`.deadlineNanoTime(`other$iv`.deadlineNanoTime());
               }

               try {
                  var limit: Long = MultipartReader.access$currentPartBytesRemaining(var6, byteCount);
                  if (limit != 0L) {
                     MultipartReader.access$getSource$p(var6).read(sink, limit);
                  }
               } catch (java.lang.Throwable var23) {
                  `this_$iv`.timeout(`originalTimeout$iv`, TimeUnit.NANOSECONDS);
                  if (`other$iv`.hasDeadline()) {
                     `this_$iv`.clearDeadline();
                  }
               }

               `this_$iv`.timeout(`originalTimeout$iv`, TimeUnit.NANOSECONDS);
               if (`other$iv`.hasDeadline()) {
                  `this_$iv`.clearDeadline();
               }
            } else {
               label146: {
                  var `originalDeadline$iv`: Long = `this_$iv`.deadlineNanoTime();
                  if (`other$iv`.hasDeadline()) {
                     `this_$iv`.deadlineNanoTime(Math.min(`this_$iv`.deadlineNanoTime(), `other$iv`.deadlineNanoTime()));
                  }

                  try {
                     var limitx: Long = MultipartReader.access$currentPartBytesRemaining(var6, byteCount);
                     if (limitx != 0L) {
                        MultipartReader.access$getSource$p(var6).read(sink, limitx);
                     }
                  } catch (java.lang.Throwable var24) {
                     `this_$iv`.timeout(`originalTimeout$iv`, TimeUnit.NANOSECONDS);
                     if (`other$iv`.hasDeadline()) {
                        `this_$iv`.deadlineNanoTime(`originalDeadline$iv`);
                     }
                  }

                  `this_$iv`.timeout(`originalTimeout$iv`, TimeUnit.NANOSECONDS);
                  if (`other$iv`.hasDeadline()) {
                     `this_$iv`.deadlineNanoTime(`originalDeadline$iv`);
                  }
               }
            }
         }
      }

      public open fun timeout(): Timeout {
         return this.timeout;
      }
   }
}
