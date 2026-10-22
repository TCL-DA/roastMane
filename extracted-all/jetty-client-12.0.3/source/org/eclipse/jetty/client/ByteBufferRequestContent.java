package org.eclipse.jetty.client;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import org.eclipse.jetty.io.content.ByteBufferContentSource;

public class ByteBufferRequestContent extends ByteBufferContentSource implements Request.Content {
   private final String contentType;

   public ByteBufferRequestContent(ByteBuffer... byteBuffers) {
      this("application/octet-stream", byteBuffers);
   }

   public ByteBufferRequestContent(String contentType, ByteBuffer... byteBuffers) {
      this(contentType, List.of(byteBuffers));
   }

   public ByteBufferRequestContent(String contentType, Collection<ByteBuffer> byteBuffers) {
      super(byteBuffers);
      this.contentType = contentType;
   }

   @Override
   public String getContentType() {
      return this.contentType;
   }
}
