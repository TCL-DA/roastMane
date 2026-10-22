package org.eclipse.jetty.http;

import java.nio.ByteBuffer;
import org.eclipse.jetty.util.Index;
import org.eclipse.jetty.util.StringUtil;
import org.eclipse.jetty.util.Index.Builder;

public enum HttpVersion {
   HTTP_0_9("HTTP/0.9", 9),
   HTTP_1_0("HTTP/1.0", 10),
   HTTP_1_1("HTTP/1.1", 11),
   HTTP_2("HTTP/2.0", 20),
   HTTP_3("HTTP/3.0", 30);

   public static final Index<HttpVersion> CACHE = new Builder().caseSensitive(false).withAll(values(), HttpVersion::toString).build();
   private final String _string;
   private final byte[] _bytes;
   private final ByteBuffer _buffer;
   private final int _version;

   private HttpVersion(String s, int version) {
      this._string = s;
      this._bytes = StringUtil.getBytes(s);
      this._buffer = ByteBuffer.wrap(this._bytes);
      this._version = version;
   }

   public byte[] toBytes() {
      return this._bytes;
   }

   public ByteBuffer toBuffer() {
      return this._buffer.asReadOnlyBuffer();
   }

   public int getVersion() {
      return this._version;
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

   public static HttpVersion fromString(String version) {
      return (HttpVersion)CACHE.get(version);
   }

   public static HttpVersion fromVersion(int version) {
      switch (version) {
         case 9:
            return HTTP_0_9;
         case 10:
            return HTTP_1_0;
         case 11:
            return HTTP_1_1;
         case 20:
            return HTTP_2;
         case 30:
            return HTTP_3;
         default:
            throw new IllegalArgumentException();
      }
   }
}
