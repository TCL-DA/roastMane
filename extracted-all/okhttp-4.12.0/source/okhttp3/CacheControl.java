package okhttp3

import java.util.concurrent.TimeUnit
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.Util

class CacheControl private constructor(noCache: Boolean,
   noStore: Boolean,
   maxAgeSeconds: Int,
   sMaxAgeSeconds: Int,
   isPrivate: Boolean,
   isPublic: Boolean,
   mustRevalidate: Boolean,
   maxStaleSeconds: Int,
   minFreshSeconds: Int,
   onlyIfCached: Boolean,
   noTransform: Boolean,
   immutable: Boolean,
   headerValue: String?
) {
   @JvmStatic
   public CacheControl.Companion Companion = new CacheControl.Companion(null);
   @JvmField
   @JvmStatic
   public CacheControl FORCE_NETWORK = new CacheControl.Builder().noCache().build();
   @JvmField
   @JvmStatic
   public CacheControl FORCE_CACHE = new CacheControl.Builder().onlyIfCached().maxStale(Integer.MAX_VALUE, TimeUnit.SECONDS).build();

   private final var headerValue: String?
   public final val immutable: Boolean
   public final val isPrivate: Boolean
   public final val isPublic: Boolean
   public final val maxAgeSeconds: Int
   public final val maxStaleSeconds: Int
   public final val minFreshSeconds: Int
   public final val mustRevalidate: Boolean
   public final val noCache: Boolean
   public final val noStore: Boolean
   public final val noTransform: Boolean
   public final val onlyIfCached: Boolean
   public final val sMaxAgeSeconds: Int

   init {
      this.noCache = noCache;
      this.noStore = noStore;
      this.maxAgeSeconds = maxAgeSeconds;
      this.sMaxAgeSeconds = sMaxAgeSeconds;
      this.isPrivate = isPrivate;
      this.isPublic = isPublic;
      this.mustRevalidate = mustRevalidate;
      this.maxStaleSeconds = maxStaleSeconds;
      this.minFreshSeconds = minFreshSeconds;
      this.onlyIfCached = onlyIfCached;
      this.noTransform = noTransform;
      this.immutable = immutable;
      this.headerValue = headerValue;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "noCache", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_noCache")
   public fun noCache(): Boolean {
      return this.noCache;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "noStore", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_noStore")
   public fun noStore(): Boolean {
      return this.noStore;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "maxAgeSeconds", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_maxAgeSeconds")
   public fun maxAgeSeconds(): Int {
      return this.maxAgeSeconds;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "sMaxAgeSeconds", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_sMaxAgeSeconds")
   public fun sMaxAgeSeconds(): Int {
      return this.sMaxAgeSeconds;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "mustRevalidate", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_mustRevalidate")
   public fun mustRevalidate(): Boolean {
      return this.mustRevalidate;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "maxStaleSeconds", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_maxStaleSeconds")
   public fun maxStaleSeconds(): Int {
      return this.maxStaleSeconds;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "minFreshSeconds", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_minFreshSeconds")
   public fun minFreshSeconds(): Int {
      return this.minFreshSeconds;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "onlyIfCached", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_onlyIfCached")
   public fun onlyIfCached(): Boolean {
      return this.onlyIfCached;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "noTransform", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_noTransform")
   public fun noTransform(): Boolean {
      return this.noTransform;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "immutable", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_immutable")
   public fun immutable(): Boolean {
      return this.immutable;
   }

   public override fun toString(): String {
      var result: java.lang.String = this.headerValue;
      if (this.headerValue == null) {
         var var2: StringBuilder = new StringBuilder();
         if (this.noCache) {
            var2.append("no-cache, ");
         }

         if (this.noStore) {
            var2.append("no-store, ");
         }

         if (this.maxAgeSeconds != -1) {
            var2.append("max-age=").append(this.maxAgeSeconds).append(", ");
         }

         if (this.sMaxAgeSeconds != -1) {
            var2.append("s-maxage=").append(this.sMaxAgeSeconds).append(", ");
         }

         if (this.isPrivate) {
            var2.append("private, ");
         }

         if (this.isPublic) {
            var2.append("public, ");
         }

         if (this.mustRevalidate) {
            var2.append("must-revalidate, ");
         }

         if (this.maxStaleSeconds != -1) {
            var2.append("max-stale=").append(this.maxStaleSeconds).append(", ");
         }

         if (this.minFreshSeconds != -1) {
            var2.append("min-fresh=").append(this.minFreshSeconds).append(", ");
         }

         if (this.onlyIfCached) {
            var2.append("only-if-cached, ");
         }

         if (this.noTransform) {
            var2.append("no-transform, ");
         }

         if (this.immutable) {
            var2.append("immutable, ");
         }

         if (var2.length() == 0) {
            return "";
         }

         var2.delete(var2.length() - 2, var2.length());
         var var10000: java.lang.String = var2.toString();
         result = var10000;
         this.headerValue = var10000;
      }

      return result;
   }

   @JvmStatic
   fun parse(headers: Headers): CacheControl {
      return Companion.parse(headers);
   }

   @SourceDebugExtension(["SMAP\nCacheControl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CacheControl.kt\nokhttp3/CacheControl$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,416:1\n1#2:417\n*E\n"])
   class Builder {
      private final var immutable: Boolean
      private final var maxAgeSeconds: Int = -1
      private final var maxStaleSeconds: Int = -1
      private final var minFreshSeconds: Int = -1
      private final var noCache: Boolean
      private final var noStore: Boolean
      private final var noTransform: Boolean
      private final var onlyIfCached: Boolean


      public fun noCache(): okhttp3.CacheControl.Builder {
         this.noCache = true;
         return this;
      }

      public fun noStore(): okhttp3.CacheControl.Builder {
         this.noStore = true;
         return this;
      }

      public fun maxAge(maxAge: Int, timeUnit: TimeUnit): okhttp3.CacheControl.Builder {
         var `$this$maxAge_u24lambda_u243`: CacheControl.Builder = this;
         if (maxAge < 0) {
            throw new IllegalArgumentException(("maxAge < 0: " + maxAge).toString());
         } else {
            `$this$maxAge_u24lambda_u243`.maxAgeSeconds = `$this$maxAge_u24lambda_u243`.clampToInt(timeUnit.toSeconds((long)maxAge));
            return this;
         }
      }

      public fun maxStale(maxStale: Int, timeUnit: TimeUnit): okhttp3.CacheControl.Builder {
         var `$this$maxStale_u24lambda_u245`: CacheControl.Builder = this;
         if (maxStale < 0) {
            throw new IllegalArgumentException(("maxStale < 0: " + maxStale).toString());
         } else {
            `$this$maxStale_u24lambda_u245`.maxStaleSeconds = `$this$maxStale_u24lambda_u245`.clampToInt(timeUnit.toSeconds((long)maxStale));
            return this;
         }
      }

      public fun minFresh(minFresh: Int, timeUnit: TimeUnit): okhttp3.CacheControl.Builder {
         var `$this$minFresh_u24lambda_u247`: CacheControl.Builder = this;
         if (minFresh < 0) {
            throw new IllegalArgumentException(("minFresh < 0: " + minFresh).toString());
         } else {
            `$this$minFresh_u24lambda_u247`.minFreshSeconds = `$this$minFresh_u24lambda_u247`.clampToInt(timeUnit.toSeconds((long)minFresh));
            return this;
         }
      }

      public fun onlyIfCached(): okhttp3.CacheControl.Builder {
         this.onlyIfCached = true;
         return this;
      }

      public fun noTransform(): okhttp3.CacheControl.Builder {
         this.noTransform = true;
         return this;
      }

      public fun immutable(): okhttp3.CacheControl.Builder {
         this.immutable = true;
         return this;
      }

      private fun Long.clampToInt(): Int {
         return if (`$this$clampToInt` > 2147483647L) Integer.MAX_VALUE else (int)`$this$clampToInt`;
      }

      public fun build(): CacheControl {
         return new CacheControl(
            this.noCache,
            this.noStore,
            this.maxAgeSeconds,
            -1,
            false,
            false,
            false,
            this.maxStaleSeconds,
            this.minFreshSeconds,
            this.onlyIfCached,
            this.noTransform,
            this.immutable,
            null,
            null
         );
      }
   }

   companion object Companion private constructor() {
      public final val FORCE_CACHE: CacheControl
      public final val FORCE_NETWORK: CacheControl


      public fun parse(headers: Headers): CacheControl {
         var noCache: Boolean = false;
         var noStore: Boolean = false;
         var maxAgeSeconds: Int = -1;
         var sMaxAgeSeconds: Int = -1;
         var isPrivate: Boolean = false;
         var isPublic: Boolean = false;
         var mustRevalidate: Boolean = false;
         var maxStaleSeconds: Int = -1;
         var minFreshSeconds: Int = -1;
         var onlyIfCached: Boolean = false;
         var noTransform: Boolean = false;
         var immutable: Boolean = false;
         var canUseHeaderValue: Boolean = true;
         var headerValue: java.lang.String = null;
         var i: Int = 0;

         for (int var17 = headers.size(); i < var17; i++) {
            var name: java.lang.String = headers.name(i);
            var value: java.lang.String = headers.value(i);
            if (StringsKt.equals(name, "Cache-Control", true)) {
               if (headerValue != null) {
                  canUseHeaderValue = false;
               } else {
                  headerValue = value;
               }
            } else {
               if (!StringsKt.equals(name, "Pragma", true)) {
                  continue;
               }

               canUseHeaderValue = false;
            }

            var var29: Int = 0;

            while (pos < value.length()) {
               var tokenStart: Int = var29;
               var29 = this.indexOfElement(value, "=,;", var29);
               var var10000: java.lang.String = value.substring(tokenStart, var29);
               var directive: java.lang.String = StringsKt.trim(var10000).toString();
               var var30: java.lang.String;
               if (var29 != value.length() && value.charAt(var29) != ',' && value.charAt(var29) != ';') {
                  var29 = Util.indexOfNonWhitespace(value, ++var29);
                  if (var29 < value.length() && value.charAt(var29) == '"') {
                     var var31: Int = ++var29;
                     var29 = StringsKt.indexOf$default(value, '"', var29, false, 4, null);
                     var10000 = value.substring(var31, var29);
                     var30 = var10000;
                     var29++;
                  } else {
                     var29 = this.indexOfElement(value, ",;", var29);
                     var10000 = value.substring(var29, var29);
                     var30 = StringsKt.trim(var10000).toString();
                  }
               } else {
                  var29++;
                  var30 = null;
               }

               if (StringsKt.equals("no-cache", directive, true)) {
                  noCache = true;
               } else if (StringsKt.equals("no-store", directive, true)) {
                  noStore = true;
               } else if (StringsKt.equals("max-age", directive, true)) {
                  maxAgeSeconds = Util.toNonNegativeInt(var30, -1);
               } else if (StringsKt.equals("s-maxage", directive, true)) {
                  sMaxAgeSeconds = Util.toNonNegativeInt(var30, -1);
               } else if (StringsKt.equals("private", directive, true)) {
                  isPrivate = true;
               } else if (StringsKt.equals("public", directive, true)) {
                  isPublic = true;
               } else if (StringsKt.equals("must-revalidate", directive, true)) {
                  mustRevalidate = true;
               } else if (StringsKt.equals("max-stale", directive, true)) {
                  maxStaleSeconds = Util.toNonNegativeInt(var30, Integer.MAX_VALUE);
               } else if (StringsKt.equals("min-fresh", directive, true)) {
                  minFreshSeconds = Util.toNonNegativeInt(var30, -1);
               } else if (StringsKt.equals("only-if-cached", directive, true)) {
                  onlyIfCached = true;
               } else if (StringsKt.equals("no-transform", directive, true)) {
                  noTransform = true;
               } else if (StringsKt.equals("immutable", directive, true)) {
                  immutable = true;
               }
            }
         }

         if (!canUseHeaderValue) {
            headerValue = null;
         }

         return new CacheControl(
            noCache,
            noStore,
            maxAgeSeconds,
            sMaxAgeSeconds,
            isPrivate,
            isPublic,
            mustRevalidate,
            maxStaleSeconds,
            minFreshSeconds,
            onlyIfCached,
            noTransform,
            immutable,
            headerValue,
            null
         );
      }

      private fun String.indexOfElement(characters: String, startIndex: Int): Int {
         var i: Int = startIndex;

         for (int var5 = $this$indexOfElement.length(); i < var5; i++) {
            if (StringsKt.contains$default(characters, `$this$indexOfElement`.charAt(i), false, 2, null)) {
               return i;
            }
         }

         return `$this$indexOfElement`.length();
      }
   }
}
