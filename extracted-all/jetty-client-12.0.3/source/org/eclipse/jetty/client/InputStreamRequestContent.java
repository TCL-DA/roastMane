package org.eclipse.jetty.client;

import java.io.InputStream;
import org.eclipse.jetty.io.ByteBufferPool;
import org.eclipse.jetty.io.content.InputStreamContentSource;

public class InputStreamRequestContent extends InputStreamContentSource implements Request.Content {
   private final String contentType;

   public InputStreamRequestContent(InputStream stream) {
      this(stream, 4096);
   }

   public InputStreamRequestContent(InputStream stream, int bufferSize) {
      this("application/octet-stream", stream, bufferSize);
   }

   public InputStreamRequestContent(String contentType, InputStream stream, int bufferSize) {
      this(contentType, stream);
      this.setBufferSize(bufferSize);
   }

   public InputStreamRequestContent(String contentType, InputStream stream) {
      this(contentType, stream, null);
   }

   public InputStreamRequestContent(String contentType, InputStream stream, ByteBufferPool bufferPool) {
      super(stream, bufferPool);
      this.contentType = contentType;
   }

   @Override
   public String getContentType() {
      return this.contentType;
   }
}
