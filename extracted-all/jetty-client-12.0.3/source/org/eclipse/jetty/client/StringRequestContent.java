package org.eclipse.jetty.client;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class StringRequestContent extends BytesRequestContent {
   public StringRequestContent(String content) {
      this("text/plain;charset=UTF-8", content);
   }

   public StringRequestContent(String content, Charset encoding) {
      this("text/plain;charset=" + encoding.name(), content, encoding);
   }

   public StringRequestContent(String contentType, String content) {
      this(contentType, content, StandardCharsets.UTF_8);
   }

   public StringRequestContent(String contentType, String content, Charset encoding) {
      super(contentType, content.getBytes(encoding));
   }
}
