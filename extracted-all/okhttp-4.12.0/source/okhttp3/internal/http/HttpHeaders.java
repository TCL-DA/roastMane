@file:JvmName(name = "HttpHeaders")

package okhttp3.internal.http

import java.io.EOFException
import java.util.ArrayList
import java.util.Collections
import java.util.LinkedHashMap
import okhttp3.Challenge
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.Headers
import okhttp3.HttpUrl
import okhttp3.Response
import okhttp3.internal.Util
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.ByteString

private final val QUOTED_STRING_DELIMITERS: ByteString = ByteString.Companion.encodeUtf8("\"\\")
private final val TOKEN_DELIMITERS: ByteString = ByteString.Companion.encodeUtf8("\t ,=")

public fun Headers.parseChallenges(headerName: String): List<Challenge> {
   var result: java.util.List = new ArrayList();
   var h: Int = 0;

   for (int var4 = $this$parseChallenges.size(); h < var4; h++) {
      if (StringsKt.equals(headerName, `$this$parseChallenges`.name(h), true)) {
         var header: Buffer = new Buffer().writeUtf8(`$this$parseChallenges`.value(h));

         try {
            readChallengeHeader(header, result);
         } catch (EOFException var7) {
            Platform.Companion.get().log("Unable to parse challenge", 5, var7);
         }
      }
   }

   return result;
}

@Throws(java/io/EOFException::class)
private fun Buffer.readChallengeHeader(result: MutableList<Challenge>) {
   var peek: java.lang.String = null;

   while (true) {
      if (peek == null) {
         skipCommasAndWhitespace(`$this$readChallengeHeader`);
         peek = readToken(`$this$readChallengeHeader`);
         if (peek == null) {
            return;
         }
      }

      var schemeName: java.lang.String = peek;
      var commaPrefixed: Boolean = skipCommasAndWhitespace(`$this$readChallengeHeader`);
      peek = readToken(`$this$readChallengeHeader`);
      if (peek == null) {
         if (!`$this$readChallengeHeader`.exhausted()) {
            return;
         }

         result.add(new Challenge(schemeName, MapsKt.emptyMap()));
         return;
      }

      var eqCount: Int = Util.skipAll(`$this$readChallengeHeader`, (byte)61);
      if (!commaPrefixed && (skipCommasAndWhitespace(`$this$readChallengeHeader`) || `$this$readChallengeHeader`.exhausted())) {
         var var10004: java.util.Map = Collections.singletonMap(null, peek + StringsKt.repeat("=", eqCount));
         result.add(new Challenge(schemeName, var10004));
         peek = null;
      } else {
         var parameters: java.util.Map = new LinkedHashMap();
         eqCount = eqCount + Util.skipAll(`$this$readChallengeHeader`, (byte)61);

         while (true) {
            if (peek == null) {
               peek = readToken(`$this$readChallengeHeader`);
               if (skipCommasAndWhitespace(`$this$readChallengeHeader`)) {
                  break;
               }

               eqCount = Util.skipAll(`$this$readChallengeHeader`, (byte)61);
            }

            if (eqCount == 0) {
               break;
            }

            if (eqCount > 1) {
               return;
            }

            if (skipCommasAndWhitespace(`$this$readChallengeHeader`)) {
               return;
            }

            var var10000: java.lang.String = if (startsWith(`$this$readChallengeHeader`, (byte)34))
               readQuotedString(`$this$readChallengeHeader`)
               else
               readToken(`$this$readChallengeHeader`);
            if (var10000 == null) {
               return;
            }

            var replaced: java.lang.String = parameters.put(peek, var10000);
            peek = null;
            if (replaced != null) {
               return;
            }

            if (!skipCommasAndWhitespace(`$this$readChallengeHeader`) && !`$this$readChallengeHeader`.exhausted()) {
               return;
            }
         }

         result.add(new Challenge(schemeName, parameters));
      }
   }
}

private fun Buffer.skipCommasAndWhitespace(): Boolean {
   var commaFound: Boolean = false;

   while (!$this$skipCommasAndWhitespace.exhausted()) {
      var var2: Byte = `$this$skipCommasAndWhitespace`.getByte(0L);
      if (var2 == 44) {
         `$this$skipCommasAndWhitespace`.readByte();
         commaFound = true;
      } else {
         if (var2 != 32 && var2 != 9) {
            break;
         }

         `$this$skipCommasAndWhitespace`.readByte();
      }
   }

   return commaFound;
}

private fun Buffer.startsWith(prefix: Byte): Boolean {
   return !`$this$startsWith`.exhausted() && `$this$startsWith`.getByte(0L) == prefix;
}

@Throws(java/io/EOFException::class)
private fun Buffer.readQuotedString(): String? {
   if (`$this$readQuotedString`.readByte() != 34) {
      throw new IllegalArgumentException("Failed requirement.".toString());
   } else {
      var result: Buffer = new Buffer();

      while (true) {
         var i: Long = `$this$readQuotedString`.indexOfElement(QUOTED_STRING_DELIMITERS);
         if (i == -1L) {
            return null;
         }

         if (`$this$readQuotedString`.getByte(i) == 34) {
            result.write(`$this$readQuotedString`, i);
            `$this$readQuotedString`.readByte();
            return result.readUtf8();
         }

         if (`$this$readQuotedString`.size() == i + 1L) {
            return null;
         }

         result.write(`$this$readQuotedString`, i);
         `$this$readQuotedString`.readByte();
         result.write(`$this$readQuotedString`, 1L);
      }
   }
}

private fun Buffer.readToken(): String? {
   var tokenSize: Long = `$this$readToken`.indexOfElement(TOKEN_DELIMITERS);
   if (tokenSize == -1L) {
      tokenSize = `$this$readToken`.size();
   }

   return if (tokenSize != 0L) `$this$readToken`.readUtf8(tokenSize) else null;
}

public fun CookieJar.receiveHeaders(url: HttpUrl, headers: Headers) {
   if (`$this$receiveHeaders` != CookieJar.NO_COOKIES) {
      var cookies: java.util.List = Cookie.Companion.parseAll(url, headers);
      if (!cookies.isEmpty()) {
         `$this$receiveHeaders`.saveFromResponse(url, cookies);
      }
   }
}

public fun Response.promisesBody(): Boolean {
   if (`$this$promisesBody`.request().method() == "HEAD") {
      return false;
   } else {
      var responseCode: Int = `$this$promisesBody`.code();
      if ((responseCode < 100 || responseCode >= 200) && responseCode != 204 && responseCode != 304) {
         return true;
      } else {
         return Util.headersContentLength(`$this$promisesBody`) != -1L
            || StringsKt.equals("chunked", Response.header$default(`$this$promisesBody`, "Transfer-Encoding", null, 2, null), true);
      }
   }
}

@Deprecated(message = "No longer supported", replaceWith = @ReplaceWith(expression = "response.promisesBody()", imports = []), level = DeprecationLevel.ERROR)
public fun hasBody(response: Response): Boolean {
   return promisesBody(response);
}

