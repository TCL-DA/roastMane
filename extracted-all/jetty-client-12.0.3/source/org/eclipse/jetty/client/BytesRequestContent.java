package org.eclipse.jetty.client;

import java.nio.ByteBuffer;
import java.util.stream.Stream;

public class BytesRequestContent extends ByteBufferRequestContent {
   public BytesRequestContent(byte[]... bytes) {
      this("application/octet-stream", bytes);
   }

   public BytesRequestContent(String contentType, byte[]... bytes) {
      super(contentType, Stream.of(bytes).map(ByteBuffer::wrap).toList());
   }
}
