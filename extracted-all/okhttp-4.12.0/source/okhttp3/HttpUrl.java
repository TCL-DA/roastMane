package okhttp3

import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.net.URL
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.ArrayList
import java.util.Collections
import java.util.LinkedHashSet
import kotlin.internal.ProgressionUtilKt
import kotlin.jvm.internal.Intrinsics
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.HostnamesKt
import okhttp3.internal.Util
import okhttp3.internal.publicsuffix.PublicSuffixDatabase
import okio.Buffer

class HttpUrl internal constructor(scheme: String,
   username: String,
   password: String,
   host: String,
   port: Int,
   pathSegments: List<String>,
   queryNamesAndValues: List<String?>?,
   fragment: String?,
   url: String
) {
   @JvmStatic
   public HttpUrl.Companion Companion = new HttpUrl.Companion(null);
   @JvmStatic
   private char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   @JvmStatic
   public java.lang.String USERNAME_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
   @JvmStatic
   public java.lang.String PASSWORD_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#";
   @JvmStatic
   public java.lang.String PATH_SEGMENT_ENCODE_SET = " \"<>^`{}|/\\?#";
   @JvmStatic
   public java.lang.String PATH_SEGMENT_ENCODE_SET_URI = "[]";
   @JvmStatic
   public java.lang.String QUERY_ENCODE_SET = " \"'<>#";
   @JvmStatic
   public java.lang.String QUERY_COMPONENT_REENCODE_SET = " \"'<>#&=";
   @JvmStatic
   public java.lang.String QUERY_COMPONENT_ENCODE_SET = " !\"#$&'(),/:;<=>?@[]\\^`{|}~";
   @JvmStatic
   public java.lang.String QUERY_COMPONENT_ENCODE_SET_URI = "\\^`{|}";
   @JvmStatic
   public java.lang.String FORM_ENCODE_SET = " \"':;<=>@[]^`{}|/\\?#&!$(),~";
   @JvmStatic
   public java.lang.String FRAGMENT_ENCODE_SET = "";
   @JvmStatic
   public java.lang.String FRAGMENT_ENCODE_SET_URI = " \"#<>\\^`{|}";

   public final val encodedFragment: String?
      public final get() {
         if (this.fragment == null) {
            return null;
         } else {
            var var10000: java.lang.String = this.url.substring(StringsKt.indexOf$default(this.url, '#', 0, false, 6, null) + 1);
            return var10000;
         }
      }

   public final val encodedPassword: String
      public final get() {
         if (this.password.length() == 0) {
            return "";
         } else {
            var var10000: java.lang.String = this.url
               .substring(
                  StringsKt.indexOf$default(this.url, ':', this.scheme.length() + 3, false, 4, null) + 1,
                  StringsKt.indexOf$default(this.url, '@', 0, false, 6, null)
               );
            return var10000;
         }
      }

   public final val encodedPath: String
      public final get() {
         var pathStart: Int = StringsKt.indexOf$default(this.url, '/', this.scheme.length() + 3, false, 4, null);
         var var10000: java.lang.String = this.url.substring(pathStart, Util.delimiterOffset(this.url, "?#", pathStart, this.url.length()));
         return var10000;
      }

   public final val encodedPathSegments: List<String>
      public final get() {
         var pathStart: Int = StringsKt.indexOf$default(this.url, '/', this.scheme.length() + 3, false, 4, null);
         var pathEnd: Int = Util.delimiterOffset(this.url, "?#", pathStart, this.url.length());
         var result: java.util.List = new ArrayList();
         var i: Int = pathStart;

         while (i < pathEnd) {
            var segmentEnd: Int = Util.delimiterOffset(this.url, '/', ++i, pathEnd);
            var var10001: java.lang.String = this.url.substring(i, segmentEnd);
            result.add(var10001);
            i = segmentEnd;
         }

         return result;
      }

   public final val encodedQuery: String?
      public final get() {
         if (this.queryNamesAndValues == null) {
            return null;
         } else {
            var queryStart: Int = StringsKt.indexOf$default(this.url, '?', 0, false, 6, null) + 1;
            var var10000: java.lang.String = this.url.substring(queryStart, Util.delimiterOffset(this.url, '#', queryStart, this.url.length()));
            return var10000;
         }
      }

   public final val encodedUsername: String
      public final get() {
         if (this.username.length() == 0) {
            return "";
         } else {
            var usernameStart: Int = this.scheme.length() + 3;
            var var10000: java.lang.String = this.url.substring(usernameStart, Util.delimiterOffset(this.url, ":@", usernameStart, this.url.length()));
            return var10000;
         }
      }

   public final val fragment: String?
   public final val host: String
   public final val isHttps: Boolean
   public final val password: String
   public final val pathSegments: List<String>
   public final val pathSize: Int
      public final get() {
         return this.pathSegments.size();
      }

   public final val port: Int
   public final val query: String?
      public final get() {
         if (this.queryNamesAndValues == null) {
            return null;
         } else {
            var result: StringBuilder = new StringBuilder();
            Companion.toQueryString$okhttp(this.queryNamesAndValues, result);
            return result.toString();
         }
      }

   private final val queryNamesAndValues: List<String?>?
   public final val queryParameterNames: Set<String>
      public final get() {
         if (this.queryNamesAndValues == null) {
            return SetsKt.emptySet();
         } else {
            var result: LinkedHashSet = new LinkedHashSet();
            var var2: IntProgression = RangesKt.step(RangesKt.until(0, this.queryNamesAndValues.size()) as IntProgression, 2);
            var i: Int = var2.getFirst();
            var var4: Int = var2.getLast();
            var var5: Int = var2.getStep();
            if (var5 > 0 && i <= var4 || var5 < 0 && var4 <= i) {
               while (true) {
                  var var10001: Any = this.queryNamesAndValues.get(i);
                  result.add(var10001);
                  if (i == var4) {
                     break;
                  }

                  i += var5;
               }
            }

            var var10000: java.util.Set = Collections.unmodifiableSet(result);
            return var10000;
         }
      }

   public final val querySize: Int
      public final get() {
         return if (this.queryNamesAndValues != null) this.queryNamesAndValues.size() / 2 else 0;
      }

   public final val scheme: String
   private final val url: String
   public final val username: String

   init {
      this.scheme = scheme;
      this.username = username;
      this.password = password;
      this.host = host;
      this.port = port;
      this.pathSegments = pathSegments;
      this.queryNamesAndValues = queryNamesAndValues;
      this.fragment = fragment;
      this.url = url;
      this.isHttps = this.scheme == "https";
   }

   @JvmName(name = "url")
   public fun toUrl(): URL {
      try {
         return new URL(this.url);
      } catch (MalformedURLException var2) {
         throw new RuntimeException(var2);
      }
   }

   @JvmName(name = "uri")
   public fun toUri(): URI {
      var uri: java.lang.String = this.newBuilder().reencodeForUri$okhttp().toString();

      var var2: URI;
      try {
         var2 = new URI(uri);
      } catch (URISyntaxException var9) {
         var var10: URI;
         try {
            var10 = URI.create(new Regex("[\\u0000-\\u001F\\u007F-\\u009F\\p{javaWhitespace}]").replace(uri, ""));
         } catch (Exception var8) {
            throw new RuntimeException(var9);
         }

         var2 = var10;
      }

      return var2;
   }

   public fun queryParameter(name: String): String? {
      if (this.queryNamesAndValues == null) {
         return null;
      } else {
         var var2: IntProgression = RangesKt.step(RangesKt.until(0, this.queryNamesAndValues.size()) as IntProgression, 2);
         var i: Int = var2.getFirst();
         var var4: Int = var2.getLast();
         var var5: Int = var2.getStep();
         if (var5 > 0 && i <= var4 || var5 < 0 && var4 <= i) {
            while (true) {
               if (name == this.queryNamesAndValues.get(i)) {
                  return this.queryNamesAndValues.get(i + 1);
               }

               if (i == var4) {
                  break;
               }

               i += var5;
            }
         }

         return null;
      }
   }

   public fun queryParameterValues(name: String): List<String?> {
      if (this.queryNamesAndValues == null) {
         return CollectionsKt.emptyList();
      } else {
         var result: java.util.List = new ArrayList();
         var var3: IntProgression = RangesKt.step(RangesKt.until(0, this.queryNamesAndValues.size()) as IntProgression, 2);
         var i: Int = var3.getFirst();
         var var5: Int = var3.getLast();
         var var6: Int = var3.getStep();
         if (var6 > 0 && i <= var5 || var6 < 0 && var5 <= i) {
            while (true) {
               if (name == this.queryNamesAndValues.get(i)) {
                  result.add(this.queryNamesAndValues.get(i + 1));
               }

               if (i == var5) {
                  break;
               }

               i += var6;
            }
         }

         var var10000: java.util.List = Collections.unmodifiableList(result);
         return var10000;
      }
   }

   public fun queryParameterName(index: Int): String {
      if (this.queryNamesAndValues == null) {
         throw new IndexOutOfBoundsException();
      } else {
         var var10000: Any = this.queryNamesAndValues.get(index * 2);
         return var10000 as java.lang.String;
      }
   }

   public fun queryParameterValue(index: Int): String? {
      if (this.queryNamesAndValues == null) {
         throw new IndexOutOfBoundsException();
      } else {
         return this.queryNamesAndValues.get(index * 2 + 1);
      }
   }

   public fun redact(): String {
      var var10000: HttpUrl.Builder = this.newBuilder("/...");
      return var10000.username("").password("").build().toString();
   }

   public fun resolve(link: String): HttpUrl? {
      var var10000: HttpUrl.Builder = this.newBuilder(link);
      return if (var10000 != null) var10000.build() else null;
   }

   public fun newBuilder(): okhttp3.HttpUrl.Builder {
      var result: HttpUrl.Builder = new HttpUrl.Builder();
      result.setScheme$okhttp(this.scheme);
      result.setEncodedUsername$okhttp(this.encodedUsername());
      result.setEncodedPassword$okhttp(this.encodedPassword());
      result.setHost$okhttp(this.host);
      result.setPort$okhttp(if (this.port != Companion.defaultPort(this.scheme)) this.port else -1);
      result.getEncodedPathSegments$okhttp().clear();
      result.getEncodedPathSegments$okhttp().addAll(this.encodedPathSegments());
      result.encodedQuery(this.encodedQuery());
      result.setEncodedFragment$okhttp(this.encodedFragment());
      return result;
   }

   public fun newBuilder(link: String): okhttp3.HttpUrl.Builder? {
      var var2: HttpUrl.Builder;
      try {
         var2 = new HttpUrl.Builder().parse$okhttp(this, link);
      } catch (IllegalArgumentException var4) {
         var2 = null;
      }

      return var2;
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is HttpUrl && (other as HttpUrl).url == this.url;
   }

   public override fun hashCode(): Int {
      return this.url.hashCode();
   }

   public override fun toString(): String {
      return this.url;
   }

   public fun topPrivateDomain(): String? {
      return if (Util.canParseAsIpAddress(this.host)) null else PublicSuffixDatabase.Companion.get().getEffectiveTldPlusOne(this.host);
   }

   @Deprecated(message = "moved to toUrl()", replaceWith = @ReplaceWith(expression = "toUrl()", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_url")
   public fun url(): URL {
      return this.url();
   }

   @Deprecated(message = "moved to toUri()", replaceWith = @ReplaceWith(expression = "toUri()", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_uri")
   public fun uri(): URI {
      return this.uri();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "scheme", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_scheme")
   public fun scheme(): String {
      return this.scheme;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedUsername", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_encodedUsername")
   public fun encodedUsername(): String {
      return this.encodedUsername();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "username", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_username")
   public fun username(): String {
      return this.username;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedPassword", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_encodedPassword")
   public fun encodedPassword(): String {
      return this.encodedPassword();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "password", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_password")
   public fun password(): String {
      return this.password;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "host", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_host")
   public fun host(): String {
      return this.host;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "port", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_port")
   public fun port(): Int {
      return this.port;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "pathSize", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_pathSize")
   public fun pathSize(): Int {
      return this.pathSize();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedPath", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_encodedPath")
   public fun encodedPath(): String {
      return this.encodedPath();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedPathSegments", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_encodedPathSegments")
   public fun encodedPathSegments(): List<String> {
      return this.encodedPathSegments();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "pathSegments", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_pathSegments")
   public fun pathSegments(): List<String> {
      return this.pathSegments;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedQuery", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_encodedQuery")
   public fun encodedQuery(): String? {
      return this.encodedQuery();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "query", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_query")
   public fun query(): String? {
      return this.query();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "querySize", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_querySize")
   public fun querySize(): Int {
      return this.querySize();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "queryParameterNames", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_queryParameterNames")
   public fun queryParameterNames(): Set<String> {
      return this.queryParameterNames();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "encodedFragment", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_encodedFragment")
   public fun encodedFragment(): String? {
      return this.encodedFragment();
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "fragment", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_fragment")
   public fun fragment(): String? {
      return this.fragment;
   }

   @JvmStatic
   fun defaultPort(scheme: java.lang.String): Int {
      return Companion.defaultPort(scheme);
   }

   @JvmName(name = "get")
   @JvmStatic
   fun java.lang.String.get(): HttpUrl {
      return Companion.get(`$this$get`);
   }

   @JvmName(name = "parse")
   @JvmStatic
   fun java.lang.String.parse(): HttpUrl {
      return Companion.parse(`$this$parse`);
   }

   @JvmName(name = "get")
   @JvmStatic
   fun URL.get(): HttpUrl {
      return Companion.get(`$this$get`);
   }

   @JvmName(name = "get")
   @JvmStatic
   fun URI.get(): HttpUrl {
      return Companion.get(`$this$get`);
   }

   @SourceDebugExtension(["SMAP\nHttpUrl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 HttpUrl.kt\nokhttp3/HttpUrl$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,1869:1\n1#2:1870\n1549#3:1871\n1620#3,3:1872\n1549#3:1875\n1620#3,3:1876\n*S KotlinDebug\n*F\n+ 1 HttpUrl.kt\nokhttp3/HttpUrl$Builder\n*L\n1180#1:1871\n1180#1:1872,3\n1181#1:1875\n1181#1:1876,3\n*E\n"])
   class Builder {
      @JvmStatic
      public HttpUrl.Builder.Companion Companion = new HttpUrl.Builder.Companion(null);
      @JvmStatic
      public java.lang.String INVALID_HOST = "Invalid URL host";

      internal final var encodedFragment: String?
      internal final var encodedPassword: String = ""
      internal final val encodedPathSegments: MutableList<String> = (new ArrayList()) as java.util.List
      internal final var encodedQueryNamesAndValues: MutableList<String?>?
      internal final var encodedUsername: String = ""
      internal final var host: String?
      internal final var port: Int = -1
      internal final var scheme: String?


      public fun scheme(scheme: String): okhttp3.HttpUrl.Builder {
         var `$this$scheme_u24lambda_u240`: HttpUrl.Builder = this;
         if (StringsKt.equals(scheme, "http", true)) {
            `$this$scheme_u24lambda_u240`.scheme = "http";
         } else {
            if (!StringsKt.equals(scheme, "https", true)) {
               throw new IllegalArgumentException("unexpected scheme: " + scheme);
            }

            `$this$scheme_u24lambda_u240`.scheme = "https";
         }

         return this;
      }

      public fun username(username: String): okhttp3.HttpUrl.Builder {
         this.encodedUsername = HttpUrl.Companion.canonicalize$okhttp$default(
            HttpUrl.Companion, username, 0, 0, " \"':;<=>@[]^`{}|/\\?#", false, false, false, false, null, 251, null
         );
         return this;
      }

      public fun encodedUsername(encodedUsername: String): okhttp3.HttpUrl.Builder {
         this.encodedUsername = HttpUrl.Companion.canonicalize$okhttp$default(
            HttpUrl.Companion, encodedUsername, 0, 0, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, null, 243, null
         );
         return this;
      }

      public fun password(password: String): okhttp3.HttpUrl.Builder {
         this.encodedPassword = HttpUrl.Companion.canonicalize$okhttp$default(
            HttpUrl.Companion, password, 0, 0, " \"':;<=>@[]^`{}|/\\?#", false, false, false, false, null, 251, null
         );
         return this;
      }

      public fun encodedPassword(encodedPassword: String): okhttp3.HttpUrl.Builder {
         this.encodedPassword = HttpUrl.Companion.canonicalize$okhttp$default(
            HttpUrl.Companion, encodedPassword, 0, 0, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, null, 243, null
         );
         return this;
      }

      public fun host(host: String): okhttp3.HttpUrl.Builder {
         var `$this$host_u24lambda_u245`: HttpUrl.Builder = this;
         var var10000: java.lang.String = HostnamesKt.toCanonicalHost(
            HttpUrl.Companion.percentDecode$okhttp$default(HttpUrl.Companion, host, 0, 0, false, 7, null)
         );
         if (var10000 == null) {
            throw new IllegalArgumentException("unexpected host: " + host);
         } else {
            `$this$host_u24lambda_u245`.host = var10000;
            return this;
         }
      }

      public fun port(port: Int): okhttp3.HttpUrl.Builder {
         var `$this$port_u24lambda_u247`: HttpUrl.Builder = this;
         if (1 > port || port >= 65536) {
            throw new IllegalArgumentException(("unexpected port: " + port).toString());
         } else {
            `$this$port_u24lambda_u247`.port = port;
            return this;
         }
      }

      private fun effectivePort(): Int {
         var var10000: Int;
         if (this.port != -1) {
            var10000 = this.port;
         } else {
            var var1: HttpUrl.Companion = HttpUrl.Companion;
            var var10001: java.lang.String = this.scheme;
            var10000 = var1.defaultPort(var10001);
         }

         return var10000;
      }

      public fun addPathSegment(pathSegment: String): okhttp3.HttpUrl.Builder {
         this.push(pathSegment, 0, pathSegment.length(), false, false);
         return this;
      }

      public fun addPathSegments(pathSegments: String): okhttp3.HttpUrl.Builder {
         return this.addPathSegments(pathSegments, false);
      }

      public fun addEncodedPathSegment(encodedPathSegment: String): okhttp3.HttpUrl.Builder {
         this.push(encodedPathSegment, 0, encodedPathSegment.length(), false, true);
         return this;
      }

      public fun addEncodedPathSegments(encodedPathSegments: String): okhttp3.HttpUrl.Builder {
         return this.addPathSegments(encodedPathSegments, true);
      }

      private fun addPathSegments(pathSegments: String, alreadyEncoded: Boolean): okhttp3.HttpUrl.Builder {
         var `$this$addPathSegments_u24lambda_u2410`: HttpUrl.Builder = this;
         var offset: Int = 0;

         var segmentEnd: Int;
         do {
            segmentEnd = Util.delimiterOffset(pathSegments, "/\\", offset, pathSegments.length());
            `$this$addPathSegments_u24lambda_u2410`.push(pathSegments, offset, segmentEnd, segmentEnd < pathSegments.length(), alreadyEncoded);
            offset = segmentEnd + 1;
         } while (segmentEnd + 1 <= pathSegments.length());

         return this;
      }

      public fun setPathSegment(index: Int, pathSegment: String): okhttp3.HttpUrl.Builder {
         var `$this$setPathSegment_u24lambda_u2412`: HttpUrl.Builder = this;
         var canonicalPathSegment: java.lang.String = HttpUrl.Companion.canonicalize$okhttp$default(
            HttpUrl.Companion, pathSegment, 0, 0, " \"<>^`{}|/\\?#", false, false, false, false, null, 251, null
         );
         if (`$this$setPathSegment_u24lambda_u2412`.isDot(canonicalPathSegment) || `$this$setPathSegment_u24lambda_u2412`.isDotDot(canonicalPathSegment)) {
            throw new IllegalArgumentException(("unexpected path segment: " + pathSegment).toString());
         } else {
            `$this$setPathSegment_u24lambda_u2412`.encodedPathSegments.set(index, canonicalPathSegment);
            return this;
         }
      }

      public fun setEncodedPathSegment(index: Int, encodedPathSegment: String): okhttp3.HttpUrl.Builder {
         var `$this$setEncodedPathSegment_u24lambda_u2414`: HttpUrl.Builder = this;
         var canonicalPathSegment: java.lang.String = HttpUrl.Companion.canonicalize$okhttp$default(
            HttpUrl.Companion, encodedPathSegment, 0, 0, " \"<>^`{}|/\\?#", true, false, false, false, null, 243, null
         );
         `$this$setEncodedPathSegment_u24lambda_u2414`.encodedPathSegments.set(index, canonicalPathSegment);
         if (`$this$setEncodedPathSegment_u24lambda_u2414`.isDot(canonicalPathSegment)
            || `$this$setEncodedPathSegment_u24lambda_u2414`.isDotDot(canonicalPathSegment)) {
            throw new IllegalArgumentException(("unexpected path segment: " + encodedPathSegment).toString());
         } else {
            return this;
         }
      }

      public fun removePathSegment(index: Int): okhttp3.HttpUrl.Builder {
         var `$this$removePathSegment_u24lambda_u2415`: HttpUrl.Builder = this;
         this.encodedPathSegments.remove(index);
         if (`$this$removePathSegment_u24lambda_u2415`.encodedPathSegments.isEmpty()) {
            `$this$removePathSegment_u24lambda_u2415`.encodedPathSegments.add("");
         }

         return this;
      }

      public fun encodedPath(encodedPath: String): okhttp3.HttpUrl.Builder {
         var `$this$encodedPath_u24lambda_u2417`: HttpUrl.Builder = this;
         if (!StringsKt.startsWith$default(encodedPath, "/", false, 2, null)) {
            throw new IllegalArgumentException(("unexpected encodedPath: " + encodedPath).toString());
         } else {
            `$this$encodedPath_u24lambda_u2417`.resolvePath(encodedPath, 0, encodedPath.length());
            return this;
         }
      }

      public fun query(query: String?): okhttp3.HttpUrl.Builder {
         var `$this$query_u24lambda_u2418`: HttpUrl.Builder;
         var var10001: java.util.List;
         label12: {
            `$this$query_u24lambda_u2418` = this;
            if (query != null) {
               var var5: java.lang.String = HttpUrl.Companion.canonicalize$okhttp$default(
                  HttpUrl.Companion, query, 0, 0, " \"'<>#", false, false, true, false, null, 219, null
               );
               if (var5 != null) {
                  var10001 = HttpUrl.Companion.toQueryNamesAndValues$okhttp(var5);
                  break label12;
               }
            }

            var10001 = null;
         }

         `$this$query_u24lambda_u2418`.encodedQueryNamesAndValues = var10001;
         return this;
      }

      public fun encodedQuery(encodedQuery: String?): okhttp3.HttpUrl.Builder {
         var `$this$encodedQuery_u24lambda_u2419`: HttpUrl.Builder;
         var var10001: java.util.List;
         label12: {
            `$this$encodedQuery_u24lambda_u2419` = this;
            if (encodedQuery != null) {
               var var5: java.lang.String = HttpUrl.Companion.canonicalize$okhttp$default(
                  HttpUrl.Companion, encodedQuery, 0, 0, " \"'<>#", true, false, true, false, null, 211, null
               );
               if (var5 != null) {
                  var10001 = HttpUrl.Companion.toQueryNamesAndValues$okhttp(var5);
                  break label12;
               }
            }

            var10001 = null;
         }

         `$this$encodedQuery_u24lambda_u2419`.encodedQueryNamesAndValues = var10001;
         return this;
      }

      public fun addQueryParameter(name: String, value: String?): okhttp3.HttpUrl.Builder {
         var `$this$addQueryParameter_u24lambda_u2420`: HttpUrl.Builder = this;
         if (this.encodedQueryNamesAndValues == null) {
            `$this$addQueryParameter_u24lambda_u2420`.encodedQueryNamesAndValues = new ArrayList<>();
         }

         var var10000: java.util.List = `$this$addQueryParameter_u24lambda_u2420`.encodedQueryNamesAndValues;
         var10000.add(
            HttpUrl.Companion.canonicalize$okhttp$default(
               HttpUrl.Companion, name, 0, 0, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, false, null, 219, null
            )
         );
         var10000 = `$this$addQueryParameter_u24lambda_u2420`.encodedQueryNamesAndValues;
         var10000.add(
            if (value != null)
               HttpUrl.Companion.canonicalize$okhttp$default(
                  HttpUrl.Companion, value, 0, 0, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, false, null, 219, null
               )
               else
               null
         );
         return this;
      }

      public fun addEncodedQueryParameter(encodedName: String, encodedValue: String?): okhttp3.HttpUrl.Builder {
         var `$this$addEncodedQueryParameter_u24lambda_u2421`: HttpUrl.Builder = this;
         if (this.encodedQueryNamesAndValues == null) {
            `$this$addEncodedQueryParameter_u24lambda_u2421`.encodedQueryNamesAndValues = new ArrayList<>();
         }

         var var10000: java.util.List = `$this$addEncodedQueryParameter_u24lambda_u2421`.encodedQueryNamesAndValues;
         var10000.add(
            HttpUrl.Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedName, 0, 0, " \"'<>#&=", true, false, true, false, null, 211, null)
         );
         var10000 = `$this$addEncodedQueryParameter_u24lambda_u2421`.encodedQueryNamesAndValues;
         var10000.add(
            if (encodedValue != null)
               HttpUrl.Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedValue, 0, 0, " \"'<>#&=", true, false, true, false, null, 211, null)
               else
               null
         );
         return this;
      }

      public fun setQueryParameter(name: String, value: String?): okhttp3.HttpUrl.Builder {
         var `$this$setQueryParameter_u24lambda_u2422`: HttpUrl.Builder = this;
         this.removeAllQueryParameters(name);
         `$this$setQueryParameter_u24lambda_u2422`.addQueryParameter(name, value);
         return this;
      }

      public fun setEncodedQueryParameter(encodedName: String, encodedValue: String?): okhttp3.HttpUrl.Builder {
         var `$this$setEncodedQueryParameter_u24lambda_u2423`: HttpUrl.Builder = this;
         this.removeAllEncodedQueryParameters(encodedName);
         `$this$setEncodedQueryParameter_u24lambda_u2423`.addEncodedQueryParameter(encodedName, encodedValue);
         return this;
      }

      public fun removeAllQueryParameters(name: String): okhttp3.HttpUrl.Builder {
         var `$this$removeAllQueryParameters_u24lambda_u2424`: HttpUrl.Builder = this;
         if (this.encodedQueryNamesAndValues == null) {
            return `$this$removeAllQueryParameters_u24lambda_u2424`;
         } else {
            `$this$removeAllQueryParameters_u24lambda_u2424`.removeAllCanonicalQueryParameters(
               HttpUrl.Companion.canonicalize$okhttp$default(
                  HttpUrl.Companion, name, 0, 0, " !\"#$&'(),/:;<=>?@[]\\^`{|}~", false, false, true, false, null, 219, null
               )
            );
            return this;
         }
      }

      public fun removeAllEncodedQueryParameters(encodedName: String): okhttp3.HttpUrl.Builder {
         var `$this$removeAllEncodedQueryParameters_u24lambda_u2425`: HttpUrl.Builder = this;
         if (this.encodedQueryNamesAndValues == null) {
            return `$this$removeAllEncodedQueryParameters_u24lambda_u2425`;
         } else {
            `$this$removeAllEncodedQueryParameters_u24lambda_u2425`.removeAllCanonicalQueryParameters(
               HttpUrl.Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedName, 0, 0, " \"'<>#&=", true, false, true, false, null, 211, null)
            );
            return this;
         }
      }

      private fun removeAllCanonicalQueryParameters(canonicalName: String) {
         var var10000: java.util.List = this.encodedQueryNamesAndValues;
         var var2: Int = var10000.size() - 2;
         var i: Int = var2;
         var var4: Int = ProgressionUtilKt.getProgressionLastElement(var2, 0, -2);
         if (var4 <= var2) {
            while (true) {
               var var10001: java.util.List = this.encodedQueryNamesAndValues;
               if (canonicalName == var10001.get(i)) {
                  var10000 = this.encodedQueryNamesAndValues;
                  var10000.remove(i + 1);
                  var10000 = this.encodedQueryNamesAndValues;
                  var10000.remove(i);
                  var10000 = this.encodedQueryNamesAndValues;
                  if (var10000.isEmpty()) {
                     this.encodedQueryNamesAndValues = null;
                     return;
                  }
               }

               if (i == var4) {
                  break;
               }

               i -= 2;
            }
         }
      }

      public fun fragment(fragment: String?): okhttp3.HttpUrl.Builder {
         this.encodedFragment = if (fragment != null)
            HttpUrl.Companion.canonicalize$okhttp$default(HttpUrl.Companion, fragment, 0, 0, "", false, false, false, true, null, 187, null)
            else
            null;
         return this;
      }

      public fun encodedFragment(encodedFragment: String?): okhttp3.HttpUrl.Builder {
         this.encodedFragment = if (encodedFragment != null)
            HttpUrl.Companion.canonicalize$okhttp$default(HttpUrl.Companion, encodedFragment, 0, 0, "", true, false, false, true, null, 179, null)
            else
            null;
         return this;
      }

      internal fun reencodeForUri(): okhttp3.HttpUrl.Builder {
         var `$this$reencodeForUri_u24lambda_u2428`: HttpUrl.Builder = this;
         var var10001: java.lang.String;
         if (this.host != null) {
            var i: java.lang.CharSequence = `$this$reencodeForUri_u24lambda_u2428`.host;
            var10001 = new Regex("[\"<>^`{|}]").replace(i, "");
         } else {
            var10001 = null;
         }

         `$this$reencodeForUri_u24lambda_u2428`.host = var10001;
         var encodedQueryNamesAndValues: Int = 0;

         for (int var8 = $this$reencodeForUri_u24lambda_u2428.encodedPathSegments.size(); i < var8; i++) {
            `$this$reencodeForUri_u24lambda_u2428`.encodedPathSegments
               .set(
                  encodedQueryNamesAndValues,
                  HttpUrl.Companion.canonicalize$okhttp$default(
                     HttpUrl.Companion,
                     `$this$reencodeForUri_u24lambda_u2428`.encodedPathSegments.get(encodedQueryNamesAndValues),
                     0,
                     0,
                     "[]",
                     true,
                     true,
                     false,
                     false,
                     null,
                     227,
                     null
                  )
               );
         }

         var var11: java.util.List = `$this$reencodeForUri_u24lambda_u2428`.encodedQueryNamesAndValues;
         if (`$this$reencodeForUri_u24lambda_u2428`.encodedQueryNamesAndValues != null) {
            var ix: Int = 0;

            for (int var10 = $this$reencodeForUri_u24lambda_u2428.encodedQueryNamesAndValues.size(); ix < var10; ix++) {
               var var10002: java.lang.String = var11.get(ix) as java.lang.String;
               var11.set(
                  ix,
                  if (var10002 != null)
                     HttpUrl.Companion.canonicalize$okhttp$default(HttpUrl.Companion, var10002, 0, 0, "\\^`{|}", true, true, true, false, null, 195, null)
                     else
                     null
               );
            }
         }

         `$this$reencodeForUri_u24lambda_u2428`.encodedFragment = if (`$this$reencodeForUri_u24lambda_u2428`.encodedFragment != null)
            HttpUrl.Companion.canonicalize$okhttp$default(
               HttpUrl.Companion, `$this$reencodeForUri_u24lambda_u2428`.encodedFragment, 0, 0, " \"#<>\\^`{|}", true, true, false, true, null, 163, null
            )
            else
            null;
         return this;
      }

      public fun build(): HttpUrl {
         var var10000: java.lang.String = this.scheme;
         if (this.scheme == null) {
            throw new IllegalStateException("scheme == null");
         } else {
            var var10001: java.lang.String = HttpUrl.Companion.percentDecode$okhttp$default(HttpUrl.Companion, this.encodedUsername, 0, 0, false, 7, null);
            var var10002: java.lang.String = HttpUrl.Companion.percentDecode$okhttp$default(HttpUrl.Companion, this.encodedPassword, 0, 0, false, 7, null);
            var var10003: java.lang.String = this.host;
            if (this.host == null) {
               throw new IllegalStateException("host == null");
            } else {
               var `$this$map$iv`: java.lang.Iterable = this.encodedPathSegments;
               var var15: Int = this.effectivePort();
               var `$this$mapTo$iv$iv`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(`$this$map$iv`, 10));

               for (Object item$iv$iv : $this$map$iv) {
                  `$this$mapTo$iv$iv`.add(
                     HttpUrl.Companion.percentDecode$okhttp$default(HttpUrl.Companion, `item$iv$iv` as java.lang.String, 0, 0, false, 7, null)
                  );
               }

               var var16: java.util.List = `$this$mapTo$iv$iv` as java.util.List;
               var10000 = var10000;
               var10001 = var10001;
               var10002 = var10002;
               var10003 = var10003;
               var var10004: Int = var15;
               var var10005: java.util.List = var16;
               var var10006: java.util.List;
               if (this.encodedQueryNamesAndValues != null) {
                  var var27: java.lang.Iterable = this.encodedQueryNamesAndValues;
                  var `destination$iv$ivx`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(this.encodedQueryNamesAndValues, 10));

                  for (Object item$iv$iv : var27) {
                     `destination$iv$ivx`.add(
                        if (var31 as java.lang.String != null)
                           HttpUrl.Companion.percentDecode$okhttp$default(HttpUrl.Companion, var31 as java.lang.String, 0, 0, true, 3, null)
                           else
                           null
                     );
                  }

                  var var17: java.util.List = `destination$iv$ivx` as java.util.List;
                  var10000 = var10000;
                  var10001 = var10001;
                  var10002 = var10002;
                  var10003 = var10003;
                  var10004 = var15;
                  var10005 = var16;
                  var10006 = var17;
               } else {
                  var10006 = null;
               }

               return new HttpUrl(
                  var10000,
                  var10001,
                  var10002,
                  var10003,
                  var10004,
                  var10005,
                  var10006,
                  if (this.encodedFragment != null)
                     HttpUrl.Companion.percentDecode$okhttp$default(HttpUrl.Companion, this.encodedFragment, 0, 0, false, 7, null)
                     else
                     null,
                  this.toString()
               );
            }
         }
      }

      public override fun toString(): String {
         var var1: StringBuilder = new StringBuilder();
         if (this.scheme != null) {
            var1.append(this.scheme);
            var1.append("://");
         } else {
            var1.append("//");
         }

         if (this.encodedUsername.length() > 0 || this.encodedPassword.length() > 0) {
            var1.append(this.encodedUsername);
            if (this.encodedPassword.length() > 0) {
               var1.append(':');
               var1.append(this.encodedPassword);
            }

            var1.append('@');
         }

         if (this.host != null) {
            var var10000: java.lang.String = this.host;
            if (StringsKt.contains$default(var10000, ':', false, 2, null)) {
               var1.append('[');
               var1.append(this.host);
               var1.append(']');
            } else {
               var1.append(this.host);
            }
         }

         label55:
         if (this.port != -1 || this.scheme != null) {
            var effectivePort: Int = this.effectivePort();
            if (this.scheme != null) {
               var var10001: HttpUrl.Companion = HttpUrl.Companion;
               var var10002: java.lang.String = this.scheme;
               if (effectivePort == var10001.defaultPort(var10002)) {
                  break label55;
               }
            }

            var1.append(':');
            var1.append(effectivePort);
         }

         HttpUrl.Companion.toPathString$okhttp(this.encodedPathSegments, var1);
         if (this.encodedQueryNamesAndValues != null) {
            var1.append('?');
            var var5: HttpUrl.Companion = HttpUrl.Companion;
            var var7: java.util.List = this.encodedQueryNamesAndValues;
            var5.toQueryString$okhttp(var7, var1);
         }

         if (this.encodedFragment != null) {
            var1.append('#');
            var1.append(this.encodedFragment);
         }

         var var6: java.lang.String = var1.toString();
         return var6;
      }

      internal fun parse(base: HttpUrl?, input: String): okhttp3.HttpUrl.Builder {
         var var14: Int = Util.indexOfFirstNonAsciiWhitespace$default(input, 0, 0, 3, null);
         var limit: Int = Util.indexOfLastNonAsciiWhitespace$default(input, var14, 0, 2, null);
         var schemeDelimiterOffset: Int = HttpUrl.Builder.Companion.access$schemeDelimiterOffset(Companion, input, var14, limit);
         if (schemeDelimiterOffset != -1) {
            if (StringsKt.startsWith(input, "https:", var14, true)) {
               this.scheme = "https";
               var14 += 6;
            } else {
               if (!StringsKt.startsWith(input, "http:", var14, true)) {
                  var var10002: StringBuilder = new StringBuilder().append("Expected URL scheme 'http' or 'https' but was '");
                  var var10003: java.lang.String = input.substring(0, schemeDelimiterOffset);
                  throw new IllegalArgumentException(var10002.append(var10003).append('\'').toString());
               }

               this.scheme = "http";
               var14 += 5;
            }
         } else {
            if (base == null) {
               throw new IllegalArgumentException(
                  "Expected URL scheme 'http' or 'https' but no scheme was found for " + (if (input.length() > 6) StringsKt.take(input, 6) + "..." else input)
               );
            }

            this.scheme = base.scheme();
         }

         var hasUsername: Boolean = false;
         var hasPassword: Boolean = false;
         var slashCount: Int = HttpUrl.Builder.Companion.access$slashCount(Companion, input, var14, limit);
         if (slashCount < 2 && base != null && base.scheme() == this.scheme) {
            this.encodedUsername = base.encodedUsername();
            this.encodedPassword = base.encodedPassword();
            this.host = base.host();
            this.port = base.port();
            this.encodedPathSegments.clear();
            this.encodedPathSegments.addAll(base.encodedPathSegments());
            if (var14 == limit || input.charAt(var14) == '#') {
               this.encodedQuery(base.encodedQuery());
            }
         } else {
            var14 = var14 + slashCount;

            label92:
            while (true) {
               var pathDelimiterOffset: Int = Util.delimiterOffset(input, "@/\\?#", var14, limit);
               switch (componentDelimiterOffset != limit ? input.charAt(componentDelimiterOffset) : -1) {
                  case -1:
                  case 35:
                  case 47:
                  case 63:
                  case 92:
                     var var20: Int = HttpUrl.Builder.Companion.access$portColonOffset(Companion, input, var14, pathDelimiterOffset);
                     if (var20 + 1 < pathDelimiterOffset) {
                        this.host = HostnamesKt.toCanonicalHost(
                           HttpUrl.Companion.percentDecode$okhttp$default(HttpUrl.Companion, input, var14, var20, false, 4, null)
                        );
                        this.port = HttpUrl.Builder.Companion.access$parsePort(Companion, input, var20 + 1, pathDelimiterOffset);
                        if (this.port == -1) {
                           var var10000: StringBuilder = new StringBuilder().append("Invalid URL port: \"");
                           var var10001: java.lang.String = input.substring(var20 + 1, pathDelimiterOffset);
                           throw new IllegalArgumentException(var10000.append(var10001).append('"').toString().toString());
                        }
                     } else {
                        this.host = HostnamesKt.toCanonicalHost(
                           HttpUrl.Companion.percentDecode$okhttp$default(HttpUrl.Companion, input, var14, var20, false, 4, null)
                        );
                        var var25: HttpUrl.Companion = HttpUrl.Companion;
                        var var27: java.lang.String = this.scheme;
                        this.port = var25.defaultPort(var27);
                     }

                     if (this.host == null) {
                        var var24: StringBuilder = new StringBuilder().append("Invalid URL host: \"");
                        var var26: java.lang.String = input.substring(var14, var20);
                        throw new IllegalArgumentException(var24.append(var26).append('"').toString().toString());
                     }

                     var14 = pathDelimiterOffset;
                     break label92;
                  case 64:
                     if (!hasPassword) {
                        var portColonOffset: Int = Util.delimiterOffset(input, ':', var14, pathDelimiterOffset);
                        var canonicalUsername: java.lang.String = HttpUrl.Companion.canonicalize$okhttp$default(
                           HttpUrl.Companion, input, var14, portColonOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, null, 240, null
                        );
                        this.encodedUsername = if (hasUsername) this.encodedUsername + "%40" + canonicalUsername else canonicalUsername;
                        if (portColonOffset != pathDelimiterOffset) {
                           hasPassword = true;
                           this.encodedPassword = HttpUrl.Companion.canonicalize$okhttp$default(
                              HttpUrl.Companion,
                              input,
                              portColonOffset + 1,
                              pathDelimiterOffset,
                              " \"':;<=>@[]^`{}|/\\?#",
                              true,
                              false,
                              false,
                              false,
                              null,
                              240,
                              null
                           );
                        }

                        hasUsername = true;
                     } else {
                        this.encodedPassword = this.encodedPassword
                           + "%40"
                           + HttpUrl.Companion.canonicalize$okhttp$default(
                              HttpUrl.Companion, input, var14, pathDelimiterOffset, " \"':;<=>@[]^`{}|/\\?#", true, false, false, false, null, 240, null
                           );
                     }

                     var14 = pathDelimiterOffset + 1;
                  default:
               }
            }
         }

         var var18: Int = Util.delimiterOffset(input, "?#", var14, limit);
         this.resolvePath(input, var14, var18);
         var14 = var18;
         if (var18 < limit && input.charAt(var18) == '?') {
            var var19: Int = Util.delimiterOffset(input, '#', var18, limit);
            this.encodedQueryNamesAndValues = HttpUrl.Companion
               .toQueryNamesAndValues$okhttp(
                  HttpUrl.Companion.canonicalize$okhttp$default(
                     HttpUrl.Companion, input, var18 + 1, var19, " \"'<>#", true, false, true, false, null, 208, null
                  )
               );
            var14 = var19;
         }

         if (var14 < limit && input.charAt(var14) == '#') {
            this.encodedFragment = HttpUrl.Companion.canonicalize$okhttp$default(
               HttpUrl.Companion, input, var14 + 1, limit, "", true, false, false, true, null, 176, null
            );
         }

         return this;
      }

      private fun resolvePath(input: String, startPos: Int, limit: Int) {
         var pos: Int = startPos;
         if (startPos != limit) {
            var c: Char = input.charAt(startPos);
            if (c != '/' && c != '\\') {
               this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
            } else {
               this.encodedPathSegments.clear();
               this.encodedPathSegments.add("");
               pos = startPos + 1;
            }

            var i: Int = pos;

            while (i < limit) {
               var pathSegmentDelimiterOffset: Int = Util.delimiterOffset(input, "/\\", i, limit);
               var segmentHasTrailingSlash: Boolean = pathSegmentDelimiterOffset < limit;
               this.push(input, i, pathSegmentDelimiterOffset, pathSegmentDelimiterOffset < limit, true);
               i = pathSegmentDelimiterOffset;
               if (segmentHasTrailingSlash) {
                  i = pathSegmentDelimiterOffset + 1;
               }
            }
         }
      }

      private fun push(input: String, pos: Int, limit: Int, addTrailingSlash: Boolean, alreadyEncoded: Boolean) {
         var segment: java.lang.String = HttpUrl.Companion.canonicalize$okhttp$default(
            HttpUrl.Companion, input, pos, limit, " \"<>^`{}|/\\?#", alreadyEncoded, false, false, false, null, 240, null
         );
         if (!this.isDot(segment)) {
            if (this.isDotDot(segment)) {
               this.pop();
            } else {
               if (this.encodedPathSegments.get(this.encodedPathSegments.size() - 1).length() == 0) {
                  this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, segment);
               } else {
                  this.encodedPathSegments.add(segment);
               }

               if (addTrailingSlash) {
                  this.encodedPathSegments.add("");
               }
            }
         }
      }

      private fun isDot(input: String): Boolean {
         return input == "." || StringsKt.equals(input, "%2e", true);
      }

      private fun isDotDot(input: String): Boolean {
         return input == ".." || StringsKt.equals(input, "%2e.", true) || StringsKt.equals(input, ".%2e", true) || StringsKt.equals(input, "%2e%2e", true);
      }

      private fun pop() {
         if (this.encodedPathSegments.remove(this.encodedPathSegments.size() - 1).length() == 0 && !this.encodedPathSegments.isEmpty()) {
            this.encodedPathSegments.set(this.encodedPathSegments.size() - 1, "");
         } else {
            this.encodedPathSegments.add("");
         }
      }

      companion object Companion private constructor() {
         internal const val INVALID_HOST: String


         private fun schemeDelimiterOffset(input: String, pos: Int, limit: Int): Int {
            if (limit - pos < 2) {
               return -1;
            } else {
               var c0: Char = input.charAt(pos);
               if (Intrinsics.compare(c0, 97) >= 0 && Intrinsics.compare(c0, 122) <= 0 || Intrinsics.compare(c0, 65) >= 0 && Intrinsics.compare(c0, 90) <= 0) {
                  for (int i = pos + 1; i < limit; i++) {
                     var var6: Char = input.charAt(i);
                     if (('a' > var6 || var6 >= '{') && ('A' > var6 || var6 >= '[') && ('0' > var6 || var6 >= ':') && var6 != '+' && var6 != '-' && var6 != '.'
                        )
                      {
                        return if (var6 == 58) i else -1;
                     }
                  }

                  return -1;
               } else {
                  return -1;
               }
            }
         }

         private fun String.slashCount(pos: Int, limit: Int): Int {
            var slashCount: Int = 0;

            for (int i = pos; i < limit; i++) {
               var c: Char = `$this$slashCount`.charAt(i);
               if (c != '\\' && c != '/') {
                  break;
               }

               slashCount++;
            }

            return slashCount;
         }

         private fun portColonOffset(input: String, pos: Int, limit: Int): Int {
            for (int i = pos; i < limit; i++) {
               var var5: Char = input.charAt(i);
               if (var5 == '[') {
                  while (true) {
                     i++;
                     if (i >= limit || input.charAt(i) == ']') {
                        break;
                     }
                  }
               } else if (var5 == ':') {
                  return i;
               }
            }

            return limit;
         }

         private fun parsePort(input: String, pos: Int, limit: Int): Int {
            var portString: Int;
            try {
               var _: Int = Integer.parseInt(
                  HttpUrl.Companion.canonicalize$okhttp$default(HttpUrl.Companion, input, pos, limit, "", false, false, false, false, null, 248, null)
               );
               portString = if (1 <= _ && _ < 65536) _ else -1;
            } catch (NumberFormatException var6) {
               portString = -1;
            }

            return portString;
         }
      }
   }

   companion object Companion private constructor() {
      internal const val FORM_ENCODE_SET: String
      internal const val FRAGMENT_ENCODE_SET: String
      internal const val FRAGMENT_ENCODE_SET_URI: String
      private final val HEX_DIGITS: CharArray
      internal const val PASSWORD_ENCODE_SET: String
      internal const val PATH_SEGMENT_ENCODE_SET: String
      internal const val PATH_SEGMENT_ENCODE_SET_URI: String
      internal const val QUERY_COMPONENT_ENCODE_SET: String
      internal const val QUERY_COMPONENT_ENCODE_SET_URI: String
      internal const val QUERY_COMPONENT_REENCODE_SET: String
      internal const val QUERY_ENCODE_SET: String
      internal const val USERNAME_ENCODE_SET: String


      public fun defaultPort(scheme: String): Int {
         return if (scheme == "http") 80 else (if (scheme == "https") 443 else -1);
      }

      internal fun List<String>.toPathString(out: StringBuilder) {
         var i: Int = 0;

         for (int var4 = $this$toPathString.size(); i < var4; i++) {
            out.append('/');
            out.append(`$this$toPathString`.get(i) as java.lang.String);
         }
      }

      internal fun List<String?>.toQueryString(out: StringBuilder) {
         var var3: IntProgression = RangesKt.step(RangesKt.until(0, `$this$toQueryString`.size()) as IntProgression, 2);
         var i: Int = var3.getFirst();
         var var5: Int = var3.getLast();
         var var6: Int = var3.getStep();
         if (var6 > 0 && i <= var5 || var6 < 0 && var5 <= i) {
            while (true) {
               var name: java.lang.String = `$this$toQueryString`.get(i) as java.lang.String;
               var value: java.lang.String = `$this$toQueryString`.get(i + 1) as java.lang.String;
               if (i > 0) {
                  out.append('&');
               }

               out.append(name);
               if (value != null) {
                  out.append('=');
                  out.append(value);
               }

               if (i == var5) {
                  break;
               }

               i += var6;
            }
         }
      }

      internal fun String.toQueryNamesAndValues(): MutableList<String?> {
         var result: java.util.List = new ArrayList();
         var pos: Int = 0;

         while (pos <= $this$toQueryNamesAndValues.length()) {
            var ampersandOffset: Int = StringsKt.indexOf$default(`$this$toQueryNamesAndValues`, '&', pos, false, 4, null);
            if (ampersandOffset == -1) {
               ampersandOffset = `$this$toQueryNamesAndValues`.length();
            }

            var equalsOffset: Int = StringsKt.indexOf$default(`$this$toQueryNamesAndValues`, '=', pos, false, 4, null);
            if (equalsOffset != -1 && equalsOffset <= ampersandOffset) {
               var var6: java.lang.String = `$this$toQueryNamesAndValues`.substring(pos, equalsOffset);
               result.add(var6);
               var6 = `$this$toQueryNamesAndValues`.substring(equalsOffset + 1, ampersandOffset);
               result.add(var6);
            } else {
               var var10001: java.lang.String = `$this$toQueryNamesAndValues`.substring(pos, ampersandOffset);
               result.add(var10001);
               result.add(null);
            }

            pos = ampersandOffset + 1;
         }

         return result;
      }

      @JvmName(name = "get")
      public fun String.toHttpUrl(): HttpUrl {
         return new HttpUrl.Builder().parse$okhttp(null, `$this$toHttpUrl`).build();
      }

      @JvmName(name = "parse")
      public fun String.toHttpUrlOrNull(): HttpUrl? {
         var var2: HttpUrl;
         try {
            var2 = this.get(`$this$toHttpUrlOrNull`);
         } catch (IllegalArgumentException var4) {
            var2 = null;
         }

         return var2;
      }

      @JvmName(name = "get")
      public fun URL.toHttpUrlOrNull(): HttpUrl? {
         var var10001: java.lang.String = `$this$toHttpUrlOrNull`.toString();
         return this.parse(var10001);
      }

      @JvmName(name = "get")
      public fun URI.toHttpUrlOrNull(): HttpUrl? {
         var var10001: java.lang.String = `$this$toHttpUrlOrNull`.toString();
         return this.parse(var10001);
      }

      @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "url.toHttpUrl()", imports = ["okhttp3.HttpUrl.Companion.toHttpUrl"]), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_get")
      public fun get(url: String): HttpUrl {
         return this.get(url);
      }

      @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "url.toHttpUrlOrNull()", imports = ["okhttp3.HttpUrl.Companion.toHttpUrlOrNull"]), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_parse")
      public fun parse(url: String): HttpUrl? {
         return this.parse(url);
      }

      @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "url.toHttpUrlOrNull()", imports = ["okhttp3.HttpUrl.Companion.toHttpUrlOrNull"]), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_get")
      public fun get(url: URL): HttpUrl? {
         return this.get(url);
      }

      @Deprecated(message = "moved to extension function", replaceWith = @ReplaceWith(expression = "uri.toHttpUrlOrNull()", imports = ["okhttp3.HttpUrl.Companion.toHttpUrlOrNull"]), level = DeprecationLevel.ERROR)
      @JvmName(name = "-deprecated_get")
      public fun get(uri: URI): HttpUrl? {
         return this.get(uri);
      }

      internal fun String.percentDecode(pos: Int, limit: Int, plusIsSpace: Boolean): String {
         for (int i = pos; i < limit; i++) {
            var c: Char = `$this$percentDecode`.charAt(i);
            if (c == '%' || c == '+' && plusIsSpace) {
               var out: Buffer = new Buffer();
               out.writeUtf8(`$this$percentDecode`, pos, i);
               this.writePercentDecoded(out, `$this$percentDecode`, i, limit, plusIsSpace);
               return out.readUtf8();
            }
         }

         var var10000: java.lang.String = `$this$percentDecode`.substring(pos, limit);
         return var10000;
      }

      private fun Buffer.writePercentDecoded(encoded: String, pos: Int, limit: Int, plusIsSpace: Boolean) {
         var i: Int = pos;

         while (i < limit) {
            var var10: Int = encoded.codePointAt(i);
            if (var10 == 37 && i + 2 < limit) {
               var d1: Int = Util.parseHexDigit(encoded.charAt(i + 1));
               var d2: Int = Util.parseHexDigit(encoded.charAt(i + 2));
               if (d1 != -1 && d2 != -1) {
                  `$this$writePercentDecoded`.writeByte((d1 shl 4) + d2);
                  i = i + 2 + Character.charCount(var10);
                  continue;
               }
            } else if (var10 == 43 && plusIsSpace) {
               `$this$writePercentDecoded`.writeByte(32);
               i++;
               continue;
            }

            `$this$writePercentDecoded`.writeUtf8CodePoint(var10);
            i += Character.charCount(var10);
         }
      }

      private fun String.isPercentEncoded(pos: Int, limit: Int): Boolean {
         return pos + 2 < limit
            && `$this$isPercentEncoded`.charAt(pos) == '%'
            && Util.parseHexDigit(`$this$isPercentEncoded`.charAt(pos + 1)) != -1
            && Util.parseHexDigit(`$this$isPercentEncoded`.charAt(pos + 2)) != -1;
      }

      internal fun String.canonicalize(
         pos: Int,
         limit: Int,
         encodeSet: String,
         alreadyEncoded: Boolean,
         strict: Boolean,
         plusIsSpace: Boolean,
         unicodeAllowed: Boolean,
         charset: Charset?
      ): String {
         var i: Int = pos;

         while (i < limit) {
            var var13: Int = `$this$canonicalize`.codePointAt(i);
            if (var13 < 32
               || var13 == 127
               || var13 >= 128 && !unicodeAllowed
               || StringsKt.contains$default(encodeSet, (char)var13, false, 2, null)
               || var13 == 37 && (!alreadyEncoded || strict && !this.isPercentEncoded(`$this$canonicalize`, i, limit))
               || var13 == 43 && plusIsSpace) {
               var out: Buffer = new Buffer();
               out.writeUtf8(`$this$canonicalize`, pos, i);
               this.writeCanonicalized(out, `$this$canonicalize`, i, limit, encodeSet, alreadyEncoded, strict, plusIsSpace, unicodeAllowed, charset);
               return out.readUtf8();
            }

            i += Character.charCount(var13);
         }

         var var10000: java.lang.String = `$this$canonicalize`.substring(pos, limit);
         return var10000;
      }

      private fun Buffer.writeCanonicalized(
         input: String,
         pos: Int,
         limit: Int,
         encodeSet: String,
         alreadyEncoded: Boolean,
         strict: Boolean,
         plusIsSpace: Boolean,
         unicodeAllowed: Boolean,
         charset: Charset?
      ) {
         var encodedCharBuffer: Buffer = null;
         var i: Int = pos;

         while (i < limit) {
            var var15: Int = input.codePointAt(i);
            if (!alreadyEncoded || var15 != 9 && var15 != 10 && var15 != 12 && var15 != 13) {
               if (var15 == 43 && plusIsSpace) {
                  `$this$writeCanonicalized`.writeUtf8(if (alreadyEncoded) "+" else "%2B");
               } else if (var15 < 32
                  || var15 == 127
                  || var15 >= 128 && !unicodeAllowed
                  || StringsKt.contains$default(encodeSet, (char)var15, false, 2, null)
                  || var15 == 37 && (!alreadyEncoded || strict && !this.isPercentEncoded(input, i, limit))) {
                  if (encodedCharBuffer == null) {
                     encodedCharBuffer = new Buffer();
                  }

                  if (charset != null && !(charset == StandardCharsets.UTF_8)) {
                     encodedCharBuffer.writeString(input, i, i + Character.charCount(var15), charset);
                  } else {
                     encodedCharBuffer.writeUtf8CodePoint(var15);
                  }

                  while (!encodedCharBuffer.exhausted()) {
                     var b: Int = encodedCharBuffer.readByte() and 255;
                     `$this$writeCanonicalized`.writeByte(37);
                     `$this$writeCanonicalized`.writeByte(HttpUrl.access$getHEX_DIGITS$cp()[b shr 4 and 15]);
                     `$this$writeCanonicalized`.writeByte(HttpUrl.access$getHEX_DIGITS$cp()[b and 15]);
                  }
               } else {
                  `$this$writeCanonicalized`.writeUtf8CodePoint(var15);
               }
            }

            i += Character.charCount(var15);
         }
      }
   }
}
