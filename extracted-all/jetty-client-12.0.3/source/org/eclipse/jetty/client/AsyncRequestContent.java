package org.eclipse.jetty.client;

import java.nio.ByteBuffer;
import java.util.stream.Stream;
import org.eclipse.jetty.io.content.AsyncContent;
import org.eclipse.jetty.util.Callback;

public class AsyncRequestContent extends AsyncContent implements Request.Content {
   private final String contentType;

   public AsyncRequestContent(ByteBuffer... buffers) {
      this("application/octet-stream", buffers);
   }

   public AsyncRequestContent(String contentType, ByteBuffer... buffers) {
      this.contentType = contentType;
      Stream.of(buffers).forEach(buffer -> this.write(buffer, Callback.NOOP));
   }

   @Override
   public String getContentType() {
      return this.contentType;
   }

   public void write(ByteBuffer buffer, Callback callback) {
      this.write(false, buffer, callback);
   }
}
