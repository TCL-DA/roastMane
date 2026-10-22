package org.eclipse.jetty.http;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.function.Function;
import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.Index.Builder;

public enum HttpHeaderValue {
   CLOSE("close"),
   CHUNKED("chunked"),
   GZIP("gzip"),
   IDENTITY("identity"),
   KEEP_ALIVE("keep-alive"),
   CONTINUE("100-continue"),
   PROCESSING("102-processing"),
   TE("TE"),
   BYTES("bytes"),
   NO_CACHE("no-cache"),
   UPGRADE("Upgrade");

   public static final Index<HttpHeaderValue> CACHE = new Builder().caseSensitive(false).withAll(values(), HttpHeaderValue::toString).build();
   private final String _string;
   private final ByteBuffer _buffer;
   private static final EnumSet<HttpHeader> __known = EnumSet.of(HttpHeader.CONNECTION, HttpHeader.TRANSFER_ENCODING, HttpHeader.CONTENT_ENCODING);

   private HttpHeaderValue(String s) {
      this._string = s;
      this._buffer = BufferUtil.toBuffer(s);
   }

   public ByteBuffer toBuffer() {
      return this._buffer.asReadOnlyBuffer();
   }

   public boolean is(String s) {
      return this._string.equalsIgnoreCase(s);
   }

   public String asString() {
      return this._string;
   }

   @Override
   public String toString() {
      return this._string;
   }

   public static boolean hasKnownValues(HttpHeader header) {
      return header == null ? false : __known.contains(header);
   }

   public static boolean parseCsvIndex(String value, Function<HttpHeaderValue, Boolean> found) {
      return parseCsvIndex(value, found, null);
   }

   public static boolean parseCsvIndex(String value, Function<HttpHeaderValue, Boolean> found, Function<String, Boolean> unknown) {
      if (StringUtil.isBlank(value)) {
         return true;
      } else {
         int next = 0;

         label66:
         while (true) {
            if (next >= value.length()) {
               return true;
            }

            HttpHeaderValue token = (HttpHeaderValue)CACHE.getBest(value, next, value.length() - next);
            label43:
            if (token != null) {
               int i = next + token.toString().length();

               while (i < value.length()) {
                  switch (value.charAt(i)) {
                     case ' ':
                        i++;
                        break;
                     case ',':
                        if (!found.apply(token)) {
                           return false;
                        }

                        next = i + 1;
                        continue label66;
                     default:
                        break label43;
                  }
               }

               return found.apply(token);
            }

            if (' ' != value.charAt(next)) {
               int comma = value.indexOf(44, next);
               if (comma != next) {
                  if (comma <= next) {
                     break;
                  }

                  if (unknown == null) {
                     next = comma + 1;
                  } else {
                     String v = value.substring(next, comma).trim();
                     if (!StringUtil.isBlank(v) && !unknown.apply(v)) {
                        break;
                     }

                     next = comma + 1;
                  }
               } else {
                  next++;
               }
            } else {
               next++;
            }
         }

         return false;
      }
   }
}
