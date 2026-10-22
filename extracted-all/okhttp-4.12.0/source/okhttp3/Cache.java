package okhttp3

import java.io.Closeable
import java.io.File
import java.io.Flushable
import java.io.IOException
import java.security.cert.Certificate
import java.security.cert.CertificateEncodingException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.util.ArrayList
import java.util.NoSuchElementException
import java.util.TreeSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.StringCompanionObject
import okhttp3.internal.Util
import okhttp3.internal.cache.CacheRequest
import okhttp3.internal.cache.CacheStrategy
import okhttp3.internal.cache.DiskLruCache
import okhttp3.internal.cache.DiskLruCache.Editor
import okhttp3.internal.cache.DiskLruCache.Snapshot
import okhttp3.internal.concurrent.TaskRunner
import okhttp3.internal.http.HttpMethod
import okhttp3.internal.http.StatusLine
import okhttp3.internal.io.FileSystem
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.ByteString
import okio.ForwardingSink
import okio.ForwardingSource
import okio.Okio
import okio.Sink
import okio.Source
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class Cache internal constructor(directory: File, maxSize: Long, fileSystem: FileSystem) : Closeable, Flushable {
   @JvmStatic
   public Cache.Companion Companion = new Cache.Companion(null);
   @JvmStatic
   private int VERSION = 201105;
   @JvmStatic
   private int ENTRY_METADATA = 0;
   @JvmStatic
   private int ENTRY_BODY = 1;
   @JvmStatic
   private int ENTRY_COUNT = 2;

   internal final val cache: DiskLruCache
   public final val directory: File
      public final get() {
         return this.cache.getDirectory();
      }

   private final var hitCount: Int
   public final val isClosed: Boolean
      public final get() {
         return this.cache.isClosed();
      }

   private final var networkCount: Int
   private final var requestCount: Int
   internal final var writeAbortCount: Int
   internal final var writeSuccessCount: Int

   init {
      this.cache = new DiskLruCache(fileSystem, directory, 201105, 2, maxSize, TaskRunner.INSTANCE);
   }

   public constructor(directory: File, maxSize: Long) : this(directory, maxSize, FileSystem.SYSTEM)
   internal fun get(request: Request): Response? {
      var key: java.lang.String = Companion.key(request.url());

      var entry: DiskLruCache.Snapshot;
      try {
         var var10000: DiskLruCache.Snapshot = this.cache.get(key);
         if (var10000 == null) {
            return null;
         }

         entry = var10000;
      } catch (IOException var8) {
         return null;
      }

      var snapshot: DiskLruCache.Snapshot = entry;

      var response: Cache.Entry;
      try {
         response = new Cache.Entry(snapshot.getSource(0));
      } catch (IOException var7) {
         Util.closeQuietly(entry);
         return null;
      }

      var var10: Response = response.response(entry);
      if (!response.matches(request, var10)) {
         var var11: ResponseBody = var10.body();
         if (var11 != null) {
            Util.closeQuietly(var11);
         }

         return null;
      } else {
         return var10;
      }
   }

   internal fun put(response: Response): CacheRequest? {
      var requestMethod: java.lang.String = response.request().method();
      if (HttpMethod.INSTANCE.invalidatesCache(response.request().method())) {
         try {
            this.remove$okhttp(response.request());
         } catch (IOException var6) {
         }

         return null;
      } else if (!(requestMethod == "GET")) {
         return null;
      } else if (Companion.hasVaryAll(response)) {
         return null;
      } else {
         var entry: Cache.Entry = new Cache.Entry(response);
         var editor: DiskLruCache.Editor = null;

         try {
            var var10000: DiskLruCache.Editor = DiskLruCache.edit$default(this.cache, Companion.key(response.request().url()), 0L, 2, null);
            if (var10000 == null) {
               return null;
            } else {
               editor = var10000;
               entry.writeTo(var10000);
               return new Cache.RealCacheRequest(this, var10000);
            }
         } catch (IOException var7) {
            this.abortQuietly(editor);
            return null;
         }
      }
   }

   @Throws(java/io/IOException::class)
   internal fun remove(request: Request) {
      this.cache.remove(Companion.key(request.url()));
   }

   internal fun update(cached: Response, network: Response) {
      var entry: Cache.Entry = new Cache.Entry(network);
      var var10000: ResponseBody = cached.body();
      var snapshot: DiskLruCache.Snapshot = (var10000 as Cache.CacheResponseBody).getSnapshot();
      var editor: DiskLruCache.Editor = null;

      try {
         var var8: DiskLruCache.Editor = snapshot.edit();
         if (var8 == null) {
            return;
         }

         editor = var8;
         entry.writeTo(var8);
         var8.commit();
      } catch (IOException var7) {
         this.abortQuietly(editor);
      }
   }

   private fun abortQuietly(editor: Editor?) {
      try {
         if (editor != null) {
            editor.abort();
         }
      } catch (IOException var3) {
      }
   }

   @Throws(java/io/IOException::class)
   public fun initialize() {
      this.cache.initialize();
   }

   @Throws(java/io/IOException::class)
   public fun delete() {
      this.cache.delete();
   }

   @Throws(java/io/IOException::class)
   public fun evictAll() {
      this.cache.evictAll();
   }

   @Throws(java/io/IOException::class)
   public fun urls(): MutableIterator<String> {
      return new java.util.Iterator<java.lang.String>(this) {
         @NotNull
         private final java.util.Iterator<DiskLruCache.Snapshot> delegate;
         @Nullable
         private java.lang.String nextUrl;
         private boolean canRemove;

         {
            this.delegate = `$receiver`.getCache$okhttp().snapshots();
         }

         @Override
         public boolean hasNext() {
            if (this.nextUrl != null) {
               return true;
            } else {
               label54: {
                  this.canRemove = false;

                  while (this.delegate.hasNext()) {
                     try {
                        var var1: Closeable = this.delegate.next();
                        var var2: java.lang.Throwable = null;

                        try {
                           try {
                              this.nextUrl = Okio.buffer((var1 as DiskLruCache.Snapshot).getSource(0)).readUtf8LineStrict();
                           } catch (java.lang.Throwable var7) {
                              var2 = var7;
                              throw var7;
                           }
                        } catch (java.lang.Throwable var8) {
                           CloseableKt.closeFinally(var1, var2);
                        }

                        CloseableKt.closeFinally(var1, null);
                     } catch (IOException var9) {
                     }
                  }

                  return false;
               }
            }
         }

         @NotNull
         public java.lang.String next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               var var10000: java.lang.String = this.nextUrl;
               this.nextUrl = null;
               this.canRemove = true;
               return var10000;
            }
         }

         @Override
         public void remove() {
            if (!this.canRemove) {
               throw new IllegalStateException("remove() before next()".toString());
            } else {
               this.delegate.remove();
            }
         }
      };
   }

   @Synchronized
   public fun writeAbortCount(): Int {
      return this.writeAbortCount;
   }

   @Synchronized
   public fun writeSuccessCount(): Int {
      return this.writeSuccessCount;
   }

   @Throws(java/io/IOException::class)
   public fun size(): Long {
      return this.cache.size();
   }

   public fun maxSize(): Long {
      return this.cache.getMaxSize();
   }

   @Throws(java/io/IOException::class)
   public override fun flush() {
      this.cache.flush();
   }

   @Throws(java/io/IOException::class)
   public override fun close() {
      this.cache.close();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "directory", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_directory")
   public fun directory(): File {
      return this.cache.getDirectory();
   }

   @Synchronized
   internal fun trackResponse(cacheStrategy: CacheStrategy) {
      var var2: Int = this.requestCount++;
      if (cacheStrategy.getNetworkRequest() != null) {
         var2 = this.networkCount++;
      } else if (cacheStrategy.getCacheResponse() != null) {
         var2 = this.hitCount++;
      }
   }

   @Synchronized
   internal fun trackConditionalCacheHit() {
      var var1: Int = this.hitCount++;
   }

   @Synchronized
   public fun networkCount(): Int {
      return this.networkCount;
   }

   @Synchronized
   public fun hitCount(): Int {
      return this.hitCount;
   }

   @Synchronized
   public fun requestCount(): Int {
      return this.requestCount;
   }

   @JvmStatic
   fun key(url: HttpUrl): java.lang.String {
      return Companion.key(url);
   }

   private class CacheResponseBody(snapshot: Snapshot, contentType: String?, contentLength: String?) : ResponseBody {
      private final val bodySource: BufferedSource
      private final val contentLength: String?
      private final val contentType: String?
      public final val snapshot: Snapshot

      init {
         this.snapshot = snapshot;
         this.contentType = contentType;
         this.contentLength = contentLength;
         var source: Source = this.snapshot.getSource(1);
         this.bodySource = Okio.buffer((new ForwardingSource(source, this) {
            {
               super(`$source`);
               this.this$0 = `$receiver`;
            }

            public void close() throws IOException {
               this.this$0.getSnapshot().close();
               super.close();
            }
         }) as Source);
      }

      public override fun contentType(): MediaType? {
         return if (this.contentType != null) MediaType.Companion.parse(this.contentType) else null;
      }

      public override fun contentLength(): Long {
         return if (this.contentLength != null) Util.toLongOrDefault(this.contentLength, -1L) else -1L;
      }

      public override fun source(): BufferedSource {
         return this.bodySource;
      }
   }

   @SourceDebugExtension(["SMAP\nCache.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Cache.kt\nokhttp3/Cache$Companion\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,788:1\n2624#2,3:789\n*S KotlinDebug\n*F\n+ 1 Cache.kt\nokhttp3/Cache$Companion\n*L\n729#1:789,3\n*E\n"])
   companion object Companion private constructor() {
      private const val ENTRY_BODY: Int
      private const val ENTRY_COUNT: Int
      private const val ENTRY_METADATA: Int
      private const val VERSION: Int


      public fun key(url: HttpUrl): String {
         return ByteString.Companion.encodeUtf8(url.toString()).md5().hex();
      }

      @Throws(java/io/IOException::class)
      internal fun readInt(source: BufferedSource): Int {
         try {
            var e: Long = source.readDecimalLong();
            var line: java.lang.String = source.readUtf8LineStrict();
            if (e >= 0L && e <= 2147483647L && line.length() <= 0) {
               return (int)e;
            } else {
               throw new IOException("expected an int but was \"" + e + line + 34);
            }
         } catch (NumberFormatException var5) {
            throw new IOException(var5.getMessage());
         }
      }

      public fun varyMatches(cachedResponse: Response, cachedRequest: Headers, newRequest: Request): Boolean {
         var `$this$none$iv`: java.lang.Iterable = this.varyFields(cachedResponse.headers());
         var var10000: Boolean;
         if (`$this$none$iv` is java.util.Collection && (`$this$none$iv` as java.util.Collection).isEmpty()) {
            var10000 = true;
         } else {
            for (Object element$iv : $this$none$iv) {
               if (!(cachedRequest.values(`element$iv` as java.lang.String) == newRequest.headers(`element$iv` as java.lang.String))) {
                  return false;
               }
            }

            var10000 = true;
         }

         return var10000;
      }

      public fun Response.hasVaryAll(): Boolean {
         return this.varyFields(`$this$hasVaryAll`.headers()).contains("*");
      }

      private fun Headers.varyFields(): Set<String> {
         var result: java.util.Set = null;
         var i: Int = 0;

         for (int var4 = $this$varyFields.size(); i < var4; i++) {
            if (StringsKt.equals("Vary", `$this$varyFields`.name(i), true)) {
               var value: java.lang.String = `$this$varyFields`.value(i);
               if (result == null) {
                  result = new TreeSet(StringsKt.getCASE_INSENSITIVE_ORDER(StringCompanionObject.INSTANCE));
               }

               for (java.lang.String varyField : StringsKt.split$default(value, new char[]{','}, false, 0, 6, null)) {
                  result.add(StringsKt.trim(var8).toString());
               }
            }
         }

         var var9: java.util.Set = result;
         if (result == null) {
            var9 = SetsKt.emptySet();
         }

         return var9;
      }

      public fun Response.varyHeaders(): Headers {
         var var10000: Response = `$this$varyHeaders`.networkResponse();
         return this.varyHeaders(var10000.request().headers(), `$this$varyHeaders`.headers());
      }

      private fun varyHeaders(requestHeaders: Headers, responseHeaders: Headers): Headers {
         var varyFields: java.util.Set = this.varyFields(responseHeaders);
         if (varyFields.isEmpty()) {
            return Util.EMPTY_HEADERS;
         } else {
            var result: Headers.Builder = new Headers.Builder();
            var i: Int = 0;

            for (int var6 = requestHeaders.size(); i < var6; i++) {
               var fieldName: java.lang.String = requestHeaders.name(i);
               if (varyFields.contains(fieldName)) {
                  result.add(fieldName, requestHeaders.value(i));
               }
            }

            return result.build();
         }
      }
   }

   private class Entry {
      @JvmStatic
      public Cache.Entry.Companion Companion = new Cache.Entry.Companion(null);
      @JvmStatic
      private java.lang.String SENT_MILLIS = Platform.Companion.get().getPrefix() + "-Sent-Millis";
      @JvmStatic
      private java.lang.String RECEIVED_MILLIS = Platform.Companion.get().getPrefix() + "-Received-Millis";

      private final val code: Int
      private final val handshake: Handshake?
      private final val isHttps: Boolean
         private final get() {
            return this.url.scheme() == "https";
         }

      private final val message: String
      private final val protocol: Protocol
      private final val receivedResponseMillis: Long
      private final val requestMethod: String
      private final val responseHeaders: Headers
      private final val sentRequestMillis: Long
      private final val url: HttpUrl
      private final val varyHeaders: Headers

      public constructor(rawSource: Source) : super() {
         label70: {
            var var2: Closeable = rawSource as Closeable;
            var var3: java.lang.Throwable = null;

            try {
               try {
                  var it: Source = var2 as Source;
                  var source: BufferedSource = Okio.buffer(rawSource);
                  var urlLine: java.lang.String = source.readUtf8LineStrict();
                  var var10001: HttpUrl = HttpUrl.Companion.parse(urlLine);
                  if (var10001 == null) {
                     var var27: IOException = new IOException("Cache corruption for " + urlLine);
                     Platform.Companion.get().log("cache corruption", 5, var27);
                     throw var27 as java.lang.Throwable;
                  }

                  this.url = var10001;
                  this.requestMethod = source.readUtf8LineStrict();
                  var varyHeadersBuilder: Headers.Builder = new Headers.Builder();
                  var varyRequestHeaderLineCount: Int = Cache.Companion.readInt$okhttp(source);

                  for (int i = 0; i < varyRequestHeaderLineCount; i++) {
                     varyHeadersBuilder.addLenient$okhttp(source.readUtf8LineStrict());
                  }

                  this.varyHeaders = varyHeadersBuilder.build();
                  var var28: StatusLine = StatusLine.Companion.parse(source.readUtf8LineStrict());
                  this.protocol = var28.protocol;
                  this.code = var28.code;
                  this.message = var28.message;
                  var responseHeadersBuilder: Headers.Builder = new Headers.Builder();
                  var responseHeaderLineCount: Int = Cache.Companion.readInt$okhttp(source);

                  for (int i = 0; i < responseHeaderLineCount; i++) {
                     responseHeadersBuilder.addLenient$okhttp(source.readUtf8LineStrict());
                  }

                  var var30: java.lang.String = responseHeadersBuilder.get(SENT_MILLIS);
                  var receivedResponseMillisString: java.lang.String = responseHeadersBuilder.get(RECEIVED_MILLIS);
                  responseHeadersBuilder.removeAll(SENT_MILLIS);
                  responseHeadersBuilder.removeAll(RECEIVED_MILLIS);
                  this.sentRequestMillis = if (var30 != null) java.lang.Long.parseLong(var30) else 0L;
                  this.receivedResponseMillis = if (receivedResponseMillisString != null) java.lang.Long.parseLong(receivedResponseMillisString) else 0L;
                  this.responseHeaders = responseHeadersBuilder.build();
                  if (this.isHttps()) {
                     var blank: java.lang.String = source.readUtf8LineStrict();
                     if (blank.length() > 0) {
                        throw new IOException("expected \"\" but was \"" + blank + '"');
                     }

                     this.handshake = Handshake.Companion
                        .get(
                           if (!source.exhausted()) TlsVersion.Companion.forJavaName(source.readUtf8LineStrict()) else TlsVersion.SSL_3_0,
                           CipherSuite.Companion.forJavaName(source.readUtf8LineStrict()),
                           this.readCertificateList(source),
                           this.readCertificateList(source)
                        );
                  } else {
                     this.handshake = null;
                  }
               } catch (java.lang.Throwable var22) {
                  var3 = var22;
                  throw var22;
               }
            } catch (java.lang.Throwable var23) {
               CloseableKt.closeFinally(var2, var3);
            }

            CloseableKt.closeFinally(var2, null);
         }
      }

      public constructor(response: Response) : super() {
         this.url = response.request().url();
         this.varyHeaders = Cache.Companion.varyHeaders(response);
         this.requestMethod = response.request().method();
         this.protocol = response.protocol();
         this.code = response.code();
         this.message = response.message();
         this.responseHeaders = response.headers();
         this.handshake = response.handshake();
         this.sentRequestMillis = response.sentRequestAtMillis();
         this.receivedResponseMillis = response.receivedResponseAtMillis();
      }

      @Throws(java/io/IOException::class)
      public fun writeTo(editor: Editor) {
         label43: {
            var var2: Closeable = Okio.buffer(editor.newSink(0)) as Closeable;
            var var3: java.lang.Throwable = null;

            try {
               try {
                  var sink: BufferedSink = var2 as BufferedSink;
                  (var2 as BufferedSink).writeUtf8(this.url.toString()).writeByte(10);
                  sink.writeUtf8(this.requestMethod).writeByte(10);
                  sink.writeDecimalLong((long)this.varyHeaders.size()).writeByte(10);
                  var i: Int = 0;

                  for (int var7 = this.varyHeaders.size(); i < var7; i++) {
                     sink.writeUtf8(this.varyHeaders.name(i)).writeUtf8(": ").writeUtf8(this.varyHeaders.value(i)).writeByte(10);
                  }

                  sink.writeUtf8(new StatusLine(this.protocol, this.code, this.message).toString()).writeByte(10);
                  sink.writeDecimalLong((long)(this.responseHeaders.size() + 2)).writeByte(10);
                  i = 0;

                  for (int var14 = this.responseHeaders.size(); i < var14; i++) {
                     sink.writeUtf8(this.responseHeaders.name(i)).writeUtf8(": ").writeUtf8(this.responseHeaders.value(i)).writeByte(10);
                  }

                  sink.writeUtf8(SENT_MILLIS).writeUtf8(": ").writeDecimalLong(this.sentRequestMillis).writeByte(10);
                  sink.writeUtf8(RECEIVED_MILLIS).writeUtf8(": ").writeDecimalLong(this.receivedResponseMillis).writeByte(10);
                  if (this.isHttps()) {
                     sink.writeByte(10);
                     var var10001: Handshake = this.handshake;
                     sink.writeUtf8(var10001.cipherSuite().javaName()).writeByte(10);
                     this.writeCertList(sink, this.handshake.peerCertificates());
                     this.writeCertList(sink, this.handshake.localCertificates());
                     sink.writeUtf8(this.handshake.tlsVersion().javaName()).writeByte(10);
                  }
               } catch (java.lang.Throwable var8) {
                  var3 = var8;
                  throw var8;
               }
            } catch (java.lang.Throwable var9) {
               CloseableKt.closeFinally(var2, var3);
            }

            CloseableKt.closeFinally(var2, null);
         }
      }

      @Throws(java/io/IOException::class)
      private fun readCertificateList(source: BufferedSource): List<Certificate> {
         var length: Int = Cache.Companion.readInt$okhttp(source);
         if (length == -1) {
            return CollectionsKt.emptyList();
         } else {
            try {
               var e: CertificateFactory = CertificateFactory.getInstance("X.509");
               var result: ArrayList = new ArrayList(length);

               for (int i = 0; i < length; i++) {
                  var line: java.lang.String = source.readUtf8LineStrict();
                  var bytes: Buffer = new Buffer();
                  var var10000: ByteString = ByteString.Companion.decodeBase64(line);
                  if (var10000 == null) {
                     throw new IOException("Corrupt certificate in cache entry");
                  }

                  bytes.write(var10000);
                  result.add(e.generateCertificate(bytes.inputStream()));
               }

               return result;
            } catch (CertificateException var9) {
               throw new IOException(var9.getMessage());
            }
         }
      }

      @Throws(java/io/IOException::class)
      private fun writeCertList(sink: BufferedSink, certificates: List<Certificate>) {
         try {
            sink.writeDecimalLong((long)certificates.size()).writeByte(10);

            for (Certificate element : certificates) {
               var bytes: ByteArray = element.getEncoded();
               var var10000: okio.ByteString.Companion = ByteString.Companion;
               sink.writeUtf8(okio.ByteString.Companion.of$default(var10000, bytes, 0, 0, 3, null).base64()).writeByte(10);
            }
         } catch (CertificateEncodingException var7) {
            throw new IOException(var7.getMessage());
         }
      }

      public fun matches(request: Request, response: Response): Boolean {
         return this.url == request.url() && this.requestMethod == request.method() && Cache.Companion.varyMatches(response, this.varyHeaders, request);
      }

      public fun response(snapshot: Snapshot): Response {
         return new Response.Builder()
            .request(new Request.Builder().url(this.url).method(this.requestMethod, null).headers(this.varyHeaders).build())
            .protocol(this.protocol)
            .code(this.code)
            .message(this.message)
            .headers(this.responseHeaders)
            .body(new Cache.CacheResponseBody(snapshot, this.responseHeaders.get("Content-Type"), this.responseHeaders.get("Content-Length")))
            .handshake(this.handshake)
            .sentRequestAtMillis(this.sentRequestMillis)
            .receivedResponseAtMillis(this.receivedResponseMillis)
            .build();
      }

      companion object Companion private constructor() {
         private final val RECEIVED_MILLIS: String
         private final val SENT_MILLIS: String

      }
   }

   private inner class RealCacheRequest(editor: Editor) : CacheRequest {
      private final val body: Sink
      private final val cacheOut: Sink
      public final var done: Boolean
         internal set
      private final val editor: Editor

      init {
         this.this$0 = `this$0`;
         this.editor = editor;
         this.cacheOut = this.editor.newSink(1);
         this.body = (new ForwardingSink(this.this$0, this, this.cacheOut) {
            {
               super(`$super_call_param$1`);
               this.this$0 = `$receiver`;
               this.this$1 = `$receiver`;
            }

            public void close() throws IOException {
               label23: {
                  var var1: Cache = this.this$0;
                  var var2: Cache.RealCacheRequest = this.this$1;
                  var var3: Cache = this.this$0;
                  synchronized (this.this$0){} // $VF: monitorenter 

                  label20: {
                     try {
                        if (var2.getDone()) {
                           break label20;
                        }

                        var2.setDone(true);
                        var3.setWriteSuccessCount$okhttp(var3.getWriteSuccessCount$okhttp() + 1);
                     } catch (java.lang.Throwable var7) {
                        // $VF: monitorexit
                     }

                     // $VF: monitorexit
                  }

                  // $VF: monitorexit
               }
            }
         }) as Sink;
      }

      public override fun abort() {
         label31: {
            var var1: Cache = this.this$0;
            var var2: Cache = this.this$0;
            synchronized (this.this$0){} // $VF: monitorenter 

            label32: {
               try {
                  if (this.done) {
                     break label32;
                  }

                  this.done = true;
                  var2.setWriteAbortCount$okhttp(var2.getWriteAbortCount$okhttp() + 1);
               } catch (java.lang.Throwable var7) {
                  // $VF: monitorexit
               }

               // $VF: monitorexit

               try {
                  this.editor.abort();
               } catch (IOException var6) {
               }

               return;
            }

            // $VF: monitorexit
         }
      }

      public override fun body(): Sink {
         return this.body;
      }
   }
}
