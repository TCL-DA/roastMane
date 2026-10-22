package org.eclipse.jetty.client;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.jetty.http.HttpField;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.io.RetainableByteBuffer;

public interface ContentDecoder {
   default void beforeDecoding(Response response) {
   }

   RetainableByteBuffer decode(ByteBuffer var1);

   default void afterDecoding(Response response) {
   }

   public static class Factories implements Iterable<ContentDecoder.Factory> {
      private final Map<String, ContentDecoder.Factory> factories = new LinkedHashMap<>();
      private HttpField acceptEncodingField;

      public HttpField getAcceptEncodingField() {
         return this.acceptEncodingField;
      }

      @Override
      public Iterator<ContentDecoder.Factory> iterator() {
         return this.factories.values().iterator();
      }

      public void clear() {
         this.factories.clear();
         this.acceptEncodingField = null;
      }

      public ContentDecoder.Factory put(ContentDecoder.Factory factory) {
         ContentDecoder.Factory result = this.factories.put(factory.getEncoding(), factory);
         String value = String.join(",", this.factories.keySet());
         this.acceptEncodingField = new HttpField(HttpHeader.ACCEPT_ENCODING, value);
         return result;
      }
   }

   public abstract static class Factory {
      private final String encoding;

      protected Factory(String encoding) {
         this.encoding = encoding;
      }

      public String getEncoding() {
         return this.encoding;
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj) {
            return true;
         } else {
            return obj instanceof ContentDecoder.Factory that ? this.encoding.equals(that.encoding) : false;
         }
      }

      @Override
      public int hashCode() {
         return this.encoding.hashCode();
      }

      public abstract ContentDecoder newContentDecoder();
   }
}
