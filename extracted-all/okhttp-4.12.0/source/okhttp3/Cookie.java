package okhttp3

import java.util.ArrayList
import java.util.Collections
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.jvm.internal.SourceDebugExtension
import okhttp3.internal.HostnamesKt
import okhttp3.internal.Util
import okhttp3.internal.http.DatesKt
import okhttp3.internal.publicsuffix.PublicSuffixDatabase
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

class Cookie private constructor(name: String,
   value: String,
   expiresAt: Long,
   domain: String,
   path: String,
   secure: Boolean,
   httpOnly: Boolean,
   persistent: Boolean,
   hostOnly: Boolean
) {
   @JvmStatic
   public Cookie.Companion Companion = new Cookie.Companion(null);
   @JvmStatic
   private Pattern YEAR_PATTERN = Pattern.compile("(\\d{2,4})[^\\d]*");
   @JvmStatic
   private Pattern MONTH_PATTERN = Pattern.compile("(?i)(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec).*");
   @JvmStatic
   private Pattern DAY_OF_MONTH_PATTERN = Pattern.compile("(\\d{1,2})[^\\d]*");
   @JvmStatic
   private Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):(\\d{1,2}):(\\d{1,2})[^\\d]*");

   public final val domain: String
   public final val expiresAt: Long
   public final val hostOnly: Boolean
   public final val httpOnly: Boolean
   public final val name: String
   public final val path: String
   public final val persistent: Boolean
   public final val secure: Boolean
   public final val value: String

   init {
      this.name = name;
      this.value = value;
      this.expiresAt = expiresAt;
      this.domain = domain;
      this.path = path;
      this.secure = secure;
      this.httpOnly = httpOnly;
      this.persistent = persistent;
      this.hostOnly = hostOnly;
   }

   public fun matches(url: HttpUrl): Boolean {
      if (if (this.hostOnly) !(url.host() == this.domain) else !Cookie.Companion.access$domainMatch(Companion, url.host(), this.domain)) {
         return false;
      } else if (!Cookie.Companion.access$pathMatch(Companion, url, this.path)) {
         return false;
      } else {
         return !this.secure || url.isHttps();
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      return other is Cookie
         && (other as Cookie).name == this.name
         && (other as Cookie).value == this.value
         && (other as Cookie).expiresAt == this.expiresAt
         && (other as Cookie).domain == this.domain
         && (other as Cookie).path == this.path
         && (other as Cookie).secure == this.secure
         && (other as Cookie).httpOnly == this.httpOnly
         && (other as Cookie).persistent == this.persistent
         && (other as Cookie).hostOnly == this.hostOnly;
   }

   @IgnoreJRERequirement
   public override fun hashCode(): Int {
      return 31
            * (
               31
                     * (
                        31
                              * (
                                 31
                                       * (
                                          31
                                                * (
                                                   31
                                                         * (
                                                            31 * (31 * (31 * 17 + this.name.hashCode()) + this.value.hashCode())
                                                               + java.lang.Long.hashCode(this.expiresAt)
                                                         )
                                                      + this.domain.hashCode()
                                                )
                                             + this.path.hashCode()
                                       )
                                    + java.lang.Boolean.hashCode(this.secure)
                              )
                           + java.lang.Boolean.hashCode(this.httpOnly)
                     )
                  + java.lang.Boolean.hashCode(this.persistent)
            )
         + java.lang.Boolean.hashCode(this.hostOnly);
   }

   public override fun toString(): String {
      return this.toString$okhttp(false);
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "name", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_name")
   public fun name(): String {
      return this.name;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "value", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_value")
   public fun value(): String {
      return this.value;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "persistent", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_persistent")
   public fun persistent(): Boolean {
      return this.persistent;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "expiresAt", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_expiresAt")
   public fun expiresAt(): Long {
      return this.expiresAt;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "hostOnly", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_hostOnly")
   public fun hostOnly(): Boolean {
      return this.hostOnly;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "domain", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_domain")
   public fun domain(): String {
      return this.domain;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "path", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_path")
   public fun path(): String {
      return this.path;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "httpOnly", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_httpOnly")
   public fun httpOnly(): Boolean {
      return this.httpOnly;
   }

   @Deprecated(message = "moved to val", replaceWith = @ReplaceWith(expression = "secure", imports = []), level = DeprecationLevel.ERROR)
   @JvmName(name = "-deprecated_secure")
   public fun secure(): Boolean {
      return this.secure;
   }

   internal fun toString(forObsoleteRfc2965: Boolean): String {
      var `$this$toString_u24lambda_u240`: StringBuilder = new StringBuilder();
      `$this$toString_u24lambda_u240`.append(this.name);
      `$this$toString_u24lambda_u240`.append('=');
      `$this$toString_u24lambda_u240`.append(this.value);
      if (this.persistent) {
         if (this.expiresAt == java.lang.Long.MIN_VALUE) {
            `$this$toString_u24lambda_u240`.append("; max-age=0");
         } else {
            `$this$toString_u24lambda_u240`.append("; expires=").append(DatesKt.toHttpDateString(new Date(this.expiresAt)));
         }
      }

      if (!this.hostOnly) {
         `$this$toString_u24lambda_u240`.append("; domain=");
         if (forObsoleteRfc2965) {
            `$this$toString_u24lambda_u240`.append(".");
         }

         `$this$toString_u24lambda_u240`.append(this.domain);
      }

      `$this$toString_u24lambda_u240`.append("; path=").append(this.path);
      if (this.secure) {
         `$this$toString_u24lambda_u240`.append("; secure");
      }

      if (this.httpOnly) {
         `$this$toString_u24lambda_u240`.append("; httponly");
      }

      var var10000: java.lang.String = `$this$toString_u24lambda_u240`.toString();
      return var10000;
   }

   @JvmStatic
   fun parse(url: HttpUrl, setCookie: java.lang.String): Cookie {
      return Companion.parse(url, setCookie);
   }

   @JvmStatic
   fun parseAll(url: HttpUrl, headers: Headers): MutableList<Cookie> {
      return Companion.parseAll(url, headers);
   }

   @SourceDebugExtension(["SMAP\nCookie.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Cookie.kt\nokhttp3/Cookie$Builder\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,614:1\n1#2:615\n*E\n"])
   class Builder {
      private final var domain: String?
      private final var expiresAt: Long = 253402300799999L
      private final var hostOnly: Boolean
      private final var httpOnly: Boolean
      private final var name: String?
      private final var path: String = "/"
      private final var persistent: Boolean
      private final var secure: Boolean
      private final var value: String?


      public fun name(name: String): okhttp3.Cookie.Builder {
         var `$this$name_u24lambda_u241`: Cookie.Builder = this;
         if (!(StringsKt.trim(name).toString() == name)) {
            throw new IllegalArgumentException("name is not trimmed".toString());
         } else {
            `$this$name_u24lambda_u241`.name = name;
            return this;
         }
      }

      public fun value(value: String): okhttp3.Cookie.Builder {
         var `$this$value_u24lambda_u243`: Cookie.Builder = this;
         if (!(StringsKt.trim(value).toString() == value)) {
            throw new IllegalArgumentException("value is not trimmed".toString());
         } else {
            `$this$value_u24lambda_u243`.value = value;
            return this;
         }
      }

      public fun expiresAt(expiresAt: Long): okhttp3.Cookie.Builder {
         var `$this$expiresAt_u24lambda_u244`: Cookie.Builder = this;
         var expiresAtx: Long = expiresAt;
         if (expiresAt <= 0L) {
            expiresAtx = java.lang.Long.MIN_VALUE;
         }

         if (expiresAtx > 253402300799999L) {
            expiresAtx = 253402300799999L;
         }

         `$this$expiresAt_u24lambda_u244`.expiresAt = expiresAtx;
         `$this$expiresAt_u24lambda_u244`.persistent = true;
         return this;
      }

      public fun domain(domain: String): okhttp3.Cookie.Builder {
         return this.domain(domain, false);
      }

      public fun hostOnlyDomain(domain: String): okhttp3.Cookie.Builder {
         return this.domain(domain, true);
      }

      private fun domain(domain: String, hostOnly: Boolean): okhttp3.Cookie.Builder {
         var `$this$domain_u24lambda_u245`: Cookie.Builder = this;
         var var10000: java.lang.String = HostnamesKt.toCanonicalHost(domain);
         if (var10000 == null) {
            throw new IllegalArgumentException("unexpected domain: " + domain);
         } else {
            `$this$domain_u24lambda_u245`.domain = var10000;
            `$this$domain_u24lambda_u245`.hostOnly = hostOnly;
            return this;
         }
      }

      public fun path(path: String): okhttp3.Cookie.Builder {
         var `$this$path_u24lambda_u247`: Cookie.Builder = this;
         if (!StringsKt.startsWith$default(path, "/", false, 2, null)) {
            throw new IllegalArgumentException("path must start with '/'".toString());
         } else {
            `$this$path_u24lambda_u247`.path = path;
            return this;
         }
      }

      public fun secure(): okhttp3.Cookie.Builder {
         this.secure = true;
         return this;
      }

      public fun httpOnly(): okhttp3.Cookie.Builder {
         this.httpOnly = true;
         return this;
      }

      public fun build(): Cookie {
         var var10000: Cookie = new Cookie;
         if (this.name == null) {
            throw new NullPointerException("builder.name == null");
         } else if (this.value == null) {
            throw new NullPointerException("builder.value == null");
         } else if (this.domain == null) {
            throw new NullPointerException("builder.domain == null");
         } else {
            var10000./* $VF: Unable to resugar constructor */<init>(
               this.name, this.value, this.expiresAt, this.domain, this.path, this.secure, this.httpOnly, this.persistent, this.hostOnly, null
            );
            return var10000;
         }
      }
   }

   companion object Companion private constructor() {
      private final val DAY_OF_MONTH_PATTERN: Pattern
      private final val MONTH_PATTERN: Pattern
      private final val TIME_PATTERN: Pattern
      private final val YEAR_PATTERN: Pattern


      private fun domainMatch(urlHost: String, domain: String): Boolean {
         if (urlHost == domain) {
            return true;
         } else {
            return StringsKt.endsWith$default(urlHost, domain, false, 2, null)
               && urlHost.charAt(urlHost.length() - domain.length() - 1) == '.'
               && !Util.canParseAsIpAddress(urlHost);
         }
      }

      private fun pathMatch(url: HttpUrl, path: String): Boolean {
         var urlPath: java.lang.String = url.encodedPath();
         if (urlPath == path) {
            return true;
         } else {
            if (StringsKt.startsWith$default(urlPath, path, false, 2, null)) {
               if (StringsKt.endsWith$default(path, "/", false, 2, null)) {
                  return true;
               }

               if (urlPath.charAt(path.length()) == '/') {
                  return true;
               }
            }

            return false;
         }
      }

      public fun parse(url: HttpUrl, setCookie: String): Cookie? {
         return this.parse$okhttp(System.currentTimeMillis(), url, setCookie);
      }

      internal fun parse(currentTimeMillis: Long, url: HttpUrl, setCookie: String): Cookie? {
         var cookiePairEnd: Int = Util.delimiterOffset$default(setCookie, ';', 0, 0, 6, null);
         var pairEqualsSign: Int = Util.delimiterOffset$default(setCookie, '=', 0, cookiePairEnd, 2, null);
         if (pairEqualsSign == cookiePairEnd) {
            return null;
         } else {
            var cookieName: java.lang.String = Util.trimSubstring$default(setCookie, 0, pairEqualsSign, 1, null);
            if (cookieName.length() != 0 && Util.indexOfControlOrNonAscii(cookieName) == -1) {
               var cookieValue: java.lang.String = Util.trimSubstring(setCookie, pairEqualsSign + 1, cookiePairEnd);
               if (Util.indexOfControlOrNonAscii(cookieValue) != -1) {
                  return null;
               } else {
                  var expiresAt: Long = 253402300799999L;
                  var deltaSeconds: Long = -1L;
                  var domain: java.lang.String = null;
                  var path: java.lang.String = null;
                  var secureOnly: Boolean = false;
                  var httpOnly: Boolean = false;
                  var hostOnly: Boolean = true;
                  var persistent: Boolean = false;
                  var pos: Int = cookiePairEnd + 1;
                  var limit: Int = setCookie.length();

                  while (pos < limit) {
                     var urlHost: Int = Util.delimiterOffset(setCookie, ';', pos, limit);
                     var encodedPath: Int = Util.delimiterOffset(setCookie, '=', pos, urlHost);
                     var lastSlash: java.lang.String = Util.trimSubstring(setCookie, pos, encodedPath);
                     var attributeValue: java.lang.String = if (encodedPath < urlHost) Util.trimSubstring(setCookie, encodedPath + 1, urlHost) else "";
                     if (StringsKt.equals(lastSlash, "expires", true)) {
                        try {
                           expiresAt = this.parseExpires(attributeValue, 0, attributeValue.length());
                           persistent = true;
                        } catch (IllegalArgumentException var28) {
                        }
                     } else if (StringsKt.equals(lastSlash, "max-age", true)) {
                        try {
                           deltaSeconds = this.parseMaxAge(attributeValue);
                           persistent = true;
                        } catch (NumberFormatException var27) {
                        }
                     } else if (StringsKt.equals(lastSlash, "domain", true)) {
                        try {
                           domain = this.parseDomain(attributeValue);
                           hostOnly = false;
                        } catch (IllegalArgumentException var26) {
                        }
                     } else if (StringsKt.equals(lastSlash, "path", true)) {
                        path = attributeValue;
                     } else if (StringsKt.equals(lastSlash, "secure", true)) {
                        secureOnly = true;
                     } else if (StringsKt.equals(lastSlash, "httponly", true)) {
                        httpOnly = true;
                     }

                     pos = urlHost + 1;
                  }

                  if (deltaSeconds == java.lang.Long.MIN_VALUE) {
                     expiresAt = java.lang.Long.MIN_VALUE;
                  } else if (deltaSeconds != -1L) {
                     expiresAt = currentTimeMillis + (if (deltaSeconds <= 9223372036854775L) deltaSeconds * (long)1000 else java.lang.Long.MAX_VALUE);
                     if (currentTimeMillis + (if (deltaSeconds <= 9223372036854775L) deltaSeconds * (long)1000 else java.lang.Long.MAX_VALUE)
                           < currentTimeMillis
                        || currentTimeMillis + (if (deltaSeconds <= 9223372036854775L) deltaSeconds * (long)1000 else java.lang.Long.MAX_VALUE)
                           > 253402300799999L) {
                        expiresAt = 253402300799999L;
                     }
                  }

                  var var30: java.lang.String = url.host();
                  if (domain == null) {
                     domain = var30;
                  } else if (!this.domainMatch(var30, domain)) {
                     return null;
                  }

                  if (var30.length() != domain.length() && PublicSuffixDatabase.Companion.get().getEffectiveTldPlusOne(domain) == null) {
                     return null;
                  } else {
                     if (path == null || !StringsKt.startsWith$default(path, "/", false, 2, null)) {
                        var var31: java.lang.String = url.encodedPath();
                        var var32: Int = StringsKt.lastIndexOf$default(var31, '/', 0, false, 6, null);
                        var var10000: java.lang.String;
                        if (var32 != 0) {
                           var10000 = var31.substring(0, var32);
                        } else {
                           var10000 = "/";
                        }

                        path = var10000;
                     }

                     return new Cookie(cookieName, cookieValue, expiresAt, domain, path, secureOnly, httpOnly, persistent, hostOnly, null);
                  }
               }
            } else {
               return null;
            }
         }
      }

      private fun parseExpires(s: String, pos: Int, limit: Int): Long {
         var posx: Int = this.dateCharacterOffset(s, pos, limit, false);
         var var15: Int = -1;
         var var16: Int = -1;
         var var17: Int = -1;
         var var18: Int = -1;
         var var19: Int = -1;
         var var20: Int = -1;
         var matcher: Matcher = Cookie.access$getTIME_PATTERN$cp().matcher(s);

         while (posx < limit) {
            var end: Int = this.dateCharacterOffset(s, posx + 1, limit, true);
            matcher.region(posx, end);
            if (var15 == -1 && matcher.usePattern(Cookie.access$getTIME_PATTERN$cp()).matches()) {
               var var34: java.lang.String = matcher.group(1);
               var15 = Integer.parseInt(var34);
               var34 = matcher.group(2);
               var16 = Integer.parseInt(var34);
               var34 = matcher.group(3);
               var17 = Integer.parseInt(var34);
            } else if (var18 == -1 && matcher.usePattern(Cookie.access$getDAY_OF_MONTH_PATTERN$cp()).matches()) {
               var var33: java.lang.String = matcher.group(1);
               var18 = Integer.parseInt(var33);
            } else if (var19 == -1 && matcher.usePattern(Cookie.access$getMONTH_PATTERN$cp()).matches()) {
               var var29: java.lang.String = matcher.group(1);
               var var30: Locale = Locale.US;
               var29 = var29.toLowerCase(var30);
               var var32: java.lang.String = Cookie.access$getMONTH_PATTERN$cp().pattern();
               var19 = StringsKt.indexOf$default(var32, var29, 0, false, 6, null) / 4;
            } else if (var20 == -1 && matcher.usePattern(Cookie.access$getYEAR_PATTERN$cp()).matches()) {
               var var10000: java.lang.String = matcher.group(1);
               var20 = Integer.parseInt(var10000);
            }

            posx = this.dateCharacterOffset(s, end + 1, limit, false);
         }

         if (70 <= var20 && var20 < 100) {
            var20 += 1900;
         }

         if (0 <= var20 && var20 < 70) {
            var20 += 2000;
         }

         if (var20 < 1601) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else if (var19 == -1) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else if (1 > var18 || var18 >= 32) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else if (0 > var15 || var15 >= 24) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else if (0 > var16 || var16 >= 60) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else if (0 > var17 || var17 >= 60) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else {
            var var21: GregorianCalendar = new GregorianCalendar(Util.UTC);
            var21.setLenient(false);
            var21.set(1, var20);
            var21.set(2, var19 - 1);
            var21.set(5, var18);
            var21.set(11, var15);
            var21.set(12, var16);
            var21.set(13, var17);
            var21.set(14, 0);
            return var21.getTimeInMillis();
         }
      }

      private fun dateCharacterOffset(input: String, pos: Int, limit: Int, invert: Boolean): Int {
         for (int i = pos; i < limit; i++) {
            var c: Int = input.charAt(i);
            if ((c < 32 && c != 9 || c >= 127 || 48 <= c && c < 58 || 97 <= c && c < 123 || 65 <= c && c < 91 || c == 58) == !invert) {
               return i;
            }
         }

         return limit;
      }

      private fun parseMaxAge(s: String): Long {
         try {
            var e: Long = java.lang.Long.parseLong(s);
            return if (e <= 0L) java.lang.Long.MIN_VALUE else e;
         } catch (NumberFormatException var4) {
            if (new Regex("-?\\d+").matches(s)) {
               return if (StringsKt.startsWith$default(s, "-", false, 2, null)) java.lang.Long.MIN_VALUE else java.lang.Long.MAX_VALUE;
            } else {
               throw var4;
            }
         }
      }

      private fun parseDomain(s: String): String {
         if (StringsKt.endsWith$default(s, ".", false, 2, null)) {
            throw new IllegalArgumentException("Failed requirement.".toString());
         } else {
            var var10000: java.lang.String = HostnamesKt.toCanonicalHost(StringsKt.removePrefix(s, "."));
            if (var10000 == null) {
               throw new IllegalArgumentException();
            } else {
               return var10000;
            }
         }
      }

      public fun parseAll(url: HttpUrl, headers: Headers): List<Cookie> {
         var cookieStrings: java.util.List = headers.values("Set-Cookie");
         var cookies: java.util.List = null;
         var i: Int = 0;

         for (int var6 = cookieStrings.size(); i < var6; i++) {
            var var10000: Cookie = this.parse(url, cookieStrings.get(i) as java.lang.String);
            if (var10000 != null) {
               if (cookies == null) {
                  cookies = new ArrayList();
               }

               cookies.add(var10000);
            }
         }

         var var8: java.util.List;
         if (cookies != null) {
            var8 = Collections.unmodifiableList(cookies);
         } else {
            var8 = CollectionsKt.emptyList();
         }

         return var8;
      }
   }
}
