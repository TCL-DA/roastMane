package org.eclipse.jetty.http;

public class Http10FieldPreEncoder extends Http1FieldPreEncoder {
   @Override
   public HttpVersion getHttpVersion() {
      return HttpVersion.HTTP_1_0;
   }
}
